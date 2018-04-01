package com.example.demodeal.controller;

import com.example.demodeal.domain.OrderItem;
import com.example.demodeal.domain.Result;
import com.example.demodeal.enums.ResultEnum;
import com.example.demodeal.repository.OrderItemRepository;
import com.example.demodeal.service.OrderItemService;
import com.example.demodeal.utils.ResultUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
public class OrderItemController {

    private final static Logger logger = LoggerFactory.getLogger(OrderItemController.class);

    @Autowired
    private OrderItemService orderItemService;
    @Autowired
    private OrderItemRepository orderItemRepository;


    //查询订单子项
    @GetMapping(value = "/orderItem/id/{id}")
    public  Result<OrderItem> orderItemFindOne(@PathVariable("id") long id) {
        return  ResultUtil.success(ResultEnum.SUCCESS,orderItemRepository.findOne(id));
    }

    /**
     * 查询所有订单子项列表
     * @return
     */
    @GetMapping(value = "/orderItem")
    public Result<OrderItem> orderItemList(){
        return ResultUtil.success(ResultEnum.SUCCESS,orderItemRepository.findAll());
    }


    /**
     * 添加订单子项
     * @return
     */
    @PostMapping(value = "/orderItem")
    public Result<OrderItem> orderItemAdd(OrderItem orderItem, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResultUtil.error(201, bindingResult.getFieldError().getDefaultMessage());
        }

        orderItem.setName(orderItem.getName());
        orderItem.setNumber(orderItem.getNumber());
        orderItem.setPrice(orderItem.getPrice());

        return ResultUtil.success(ResultEnum.SUCCESS,orderItemRepository.save(orderItem));
    }

    //更新
    @PutMapping(value = "/orderItem/{id}")
    public Result<OrderItem> orderItemUpdate(@PathVariable("id") long id,
                                     @RequestParam("name") String name,
                                     @RequestParam("number") int number,
                                     @RequestParam("price") double price ) {
        OrderItem orderItem = new OrderItem();
        orderItem.setId(id);
        orderItem.setName(name);
        orderItem.setNumber(number);
        orderItem.setPrice(price);

        return  ResultUtil.success(ResultEnum.SUCCESS,orderItemRepository.save(orderItem));
    }

    //删除
    @DeleteMapping(value = "/orderItem/{id}")
    public Result<OrderItem> orderItemDelete(@PathVariable("id") long id) {
        orderItemRepository.delete(id);
        return ResultUtil.success(ResultEnum.SUCCESS,null);
    }


}
