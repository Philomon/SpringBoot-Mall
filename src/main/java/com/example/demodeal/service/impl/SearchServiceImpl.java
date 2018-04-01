package com.example.demodeal.service.impl;

import com.example.demodeal.domain.Goods;
import com.example.demodeal.repository.GoodsRepository;
import com.example.demodeal.service.SearchService;
import com.example.demodeal.service.search.GoodsIndexKey;
import com.example.demodeal.service.search.GoodsIndexMessage;
import com.example.demodeal.utils.JsonUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.elasticsearch.index.reindex.DeleteByQueryRequestBuilder;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Service
public class SearchServiceImpl implements SearchService {
    private static final Logger logger = LoggerFactory.getLogger(SearchService.class);

    private static final String INDEX_NAME = "demo";

    private static final String INDEX_TYPE = "goods";

    private static final String INDEX_TOPIC = "goods_build";


    @Autowired
    private  GoodsRepository goodsRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private TransportClient esClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @KafkaListener(topics = INDEX_TOPIC)
    private void handleMessage(String content) {
        try {
            //readValue是把字符串
            GoodsIndexMessage message = JsonUtil.string2Obj(content, GoodsIndexMessage.class);

            switch (message.getOperation()) {
                case GoodsIndexMessage.INDEX:
                    this.createOrUpdateIndex(message);
                    break;
                case GoodsIndexMessage.REMOVE:
                    this.removeIndex(message);
                    break;
                default:
                    logger.warn("Not support message content " + content);
                    break;
            }
        } catch (Exception e) {
            logger.error("Cannot parse json for " + content, e);
        }
    }

    private void createOrUpdateIndex(GoodsIndexMessage message) {
        Long goodsId = message.getGoodsID();

        Goods goods = goodsRepository.findOne(goodsId);

        //如果此时数据库被阻塞了，数据无法插入
        if (goods == null) {
            logger.error("Index goods {} dose not exist!", goodsId);
            //如果失败了 调用index

            this.index(goodsId, message.getRetry() + 1);
            return;
        }

        Goods indexTemplate = new Goods();
        modelMapper.map(goods, indexTemplate);



        SearchRequestBuilder requestBuilder = this.esClient.prepareSearch(INDEX_NAME).setTypes(INDEX_TYPE)
                .setQuery(QueryBuilders.termQuery(GoodsIndexKey.GOODS_ID, goodsId));
        //?

        logger.debug(requestBuilder.toString());
        SearchResponse searchResponse = requestBuilder.get();

        boolean success;
        long totalHit = searchResponse.getHits().getTotalHits();
        if (totalHit == 0) {
            success = create(indexTemplate);
        } else if (totalHit == 1) {
            String esId = searchResponse.getHits().getAt(0).getId();
            success = update(esId, indexTemplate);
        } else {
            success = deleteAndCreate(totalHit, indexTemplate);
        }


        if (!success) {
            this.index(message.getGoodsID(), message.getRetry() + 1);
        } else {
            logger.debug("Index success with goods " + goodsId);
        }
    }

    private void removeIndex(GoodsIndexMessage message) {
        Long goodsId = message.getGoodsID();
        DeleteByQueryRequestBuilder builder = DeleteByQueryAction.INSTANCE
                .newRequestBuilder(esClient)
                .filter(QueryBuilders.termQuery(GoodsIndexKey.GOODS_ID, goodsId))
                .source(INDEX_NAME);

        logger.debug("Delete by query for goods: " + builder);

        BulkByScrollResponse response = builder.get();
        long deleted = response.getDeleted();
        logger.debug("Delete total " + deleted);


        if (deleted <= 0) {
            logger.warn("Did not remove data from es for response: " + response);
            // 重新加入消息队列
            this.remove(goodsId, message.getRetry() + 1);
        }
    }


    //index 处理创建失败重新创建index（发送消息） 的逻辑
    @Override
    public void index(Long goodsId) {
        this.index(goodsId, 0);
    }

    private void index(Long goodsId, int retry) {
        if (retry > GoodsIndexMessage.MAX_RETRY) {
            logger.error("Retry index times over 3 for goods: " + goodsId + " Please check it!");
            return;
        }

        GoodsIndexMessage message = new GoodsIndexMessage(goodsId, GoodsIndexMessage.INDEX, retry);

        try {
            kafkaTemplate.send(INDEX_TOPIC, objectMapper.writeValueAsString(message));
        } catch (JsonProcessingException e) {
            logger.error("Json encode error for " + message);
        }

    }

    private boolean create(Goods indexTemplate) {

        try {
            IndexResponse response = this.esClient.prepareIndex(INDEX_NAME, INDEX_TYPE)
                    .setSource(JsonUtil.obj2String(indexTemplate), XContentType.JSON).get();

            logger.debug("Create index with goods: " + indexTemplate.getId());
            if (response.status() == RestStatus.CREATED) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            logger.error("Error to index goods " + indexTemplate.getId(), e);
            return false;
        }
    }

    private boolean update(String esId, Goods indexTemplate) {

        try {
            UpdateResponse response = this.esClient.prepareUpdate(INDEX_NAME, INDEX_TYPE, esId).setDoc(JsonUtil.obj2String(indexTemplate), XContentType.JSON).get();

            logger.debug("Update index with goods: " + indexTemplate.getId());
            if (response.status() == RestStatus.OK) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            logger.error("Error to index goods " + indexTemplate.getId(), e);
            return false;
        }
    }

    private boolean deleteAndCreate(long totalHit, Goods indexTemplate) {
        DeleteByQueryRequestBuilder builder = DeleteByQueryAction.INSTANCE
                .newRequestBuilder(esClient)
                .filter(QueryBuilders.termQuery(GoodsIndexKey.GOODS_ID, indexTemplate.getId()))
                .source(INDEX_NAME);

        logger.debug("Delete by query for goods: " + builder);

        BulkByScrollResponse response = builder.get();
        long deleted = response.getDeleted();
        if (deleted != totalHit) {
            logger.warn("Need delete {}, but {} was deleted!", totalHit, deleted);
            return false;
        } else {
            return create(indexTemplate);
        }
    }

    @Override
    public void remove(Long goodsId) {
        this.remove(goodsId, 0);
    }

    @Override
    public List<Goods> findByName(String name) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(QueryBuilders.matchQuery("name",name));
        SearchRequestBuilder builder = this.esClient.prepareSearch(INDEX_NAME)
                .setTypes(INDEX_TYPE)
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setQuery(boolQueryBuilder)
                .setFrom(0);

        SearchResponse searchResponse = builder.get();

        List<Goods> result = new ArrayList<Goods>();
        for(SearchHit hit:searchResponse.getHits()){
            result.add((Goods) hit.getSource());
        }

        return result;
    }

    @Override
    public List<Goods> findById(Long goodsId) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(QueryBuilders.matchQuery("id",goodsId));
        SearchRequestBuilder builder = this.esClient.prepareSearch(INDEX_NAME)
                .setTypes(INDEX_TYPE)
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setQuery(boolQueryBuilder)
                .setFrom(0);

        SearchResponse searchResponse = builder.get();

        List<Goods> result = new ArrayList<Goods>();
        for(SearchHit hit:searchResponse.getHits()){
            result.add((Goods) hit.getSource());
        }

        return result;
    }


    private void remove(Long goodsId, int retry) {
        if (retry > GoodsIndexMessage.MAX_RETRY) {
            logger.error("Retry remove times over 3 for goods: " + goodsId + " Please check it!");
            return;
        }

        GoodsIndexMessage message = new GoodsIndexMessage(goodsId, GoodsIndexMessage.REMOVE, retry);
        try {
            this.kafkaTemplate.send(INDEX_TOPIC, objectMapper.writeValueAsString(message));
        } catch (JsonProcessingException e) {
            logger.error("Cannot encode json for " + message, e);
        }
    }

}
