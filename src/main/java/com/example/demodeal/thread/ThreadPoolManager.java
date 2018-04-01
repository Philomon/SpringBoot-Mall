package com.example.demodeal.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.*;

@Component
public class ThreadPoolManager implements BeanFactoryAware {

    private static Logger log = LoggerFactory.getLogger(ThreadPoolManager.class);
    private BeanFactory factory;//用于从IOC里取对象
    // 线程池维护线程的最少数量
    private final static int CORE_POOL_SIZE = 2;
    // 线程池维护线程的最大数量
    private final static int MAX_POOL_SIZE = 10;
    // 线程池维护线程所允许的空闲时间
    private final static int KEEP_ALIVE_TIME = 0;
    // 线程池所使用的缓冲队列大小
    private final static int WORK_QUEUE_SIZE = 50;
    // 消息缓冲队列
    Queue<Object> msgQueue = new LinkedList<Object>();

    //用于储存在队列中的订单,防止重复提交
    Map<String, Object> cacheMap = new ConcurrentHashMap<>();

    //由于超出线程范围和队列容量而使执行被阻塞时所使用的处理程序
    final RejectedExecutionHandler handler = new RejectedExecutionHandler() {
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            //System.out.println("太忙了,把该订单交给调度线程池逐一处理" + ((DBThread) r).getMsg());
            msgQueue.offer(((DBThread) r).getMsg());
        }
    };

    // 订单线程池
    final ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
            CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_TIME,
            TimeUnit.SECONDS, new ArrayBlockingQueue(WORK_QUEUE_SIZE), this.handler);

    // 调度线程池。此线程池支持定时以及周期性执行任务的需求。
    final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(5);

    // 访问消息缓存的调度线程,每秒执行一次
    // 查看是否有待定请求，如果有，则创建一个新的AccessDBThread，并添加到线程池中
    final ScheduledFuture taskHandler = scheduler.scheduleAtFixedRate(new Runnable() {
        @Override
        public void run() {
            if (!msgQueue.isEmpty()) {
                if (threadPool.getQueue().size() < WORK_QUEUE_SIZE) {
                    System.out.print("调度：");
                    String orderId = (String) msgQueue.poll();
                    DBThread accessDBThread = (DBThread) factory.getBean("dBThread");
                    accessDBThread.setMsg(orderId);
                    threadPool.execute(accessDBThread);
                }
                // while (msgQueue.peek() != null) {
                // }
            }
        }
    }, 0, 1, TimeUnit.SECONDS);

    //终止订单线程池+调度线程池
    public void shutdown() {
        //true表示如果定时任务在执行，立即中止，false则等待任务结束后再停止
        System.out.println(taskHandler.cancel(false));
        scheduler.shutdown();
        threadPool.shutdown();
    }

    public Queue<Object> getMsgQueue() {
        return msgQueue;
    }


    //将任务加入订单线程池

    public void processOrders(String objString) {
        if (cacheMap.get(objString) == null) {
            cacheMap.put(objString,new Object());
            DBThread accessDBThread = (DBThread) factory.getBean("DBThread");
            accessDBThread.setMsg(objString);
            threadPool.execute(accessDBThread);
        }
    }


    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        factory = beanFactory;
    }
}