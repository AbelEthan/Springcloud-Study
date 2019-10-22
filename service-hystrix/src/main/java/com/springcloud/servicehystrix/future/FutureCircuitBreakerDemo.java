package com.springcloud.servicehystrix.future;

import java.util.Random;
import java.util.concurrent.*;

/**
 * Title: 通过 {@link Future} 实现 服务熔断
 * Description:
 *
 * @author 谭 tmn
 * @email AbelEthan@126.com
 * @date 2018/10/30 23:12
 */
public class FutureCircuitBreakerDemo {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        //初始化线程池
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        RandomCommand command = new RandomCommand();
        Future<String> future = executorService.submit(command::run);
        String result = null;
        // 100 毫秒超时时间
        try {
            result = future.get(100, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            // fallback 方法调用
            result = command.fallback();
        }
        System.out.println(result);
        executorService.shutdown();
    }

    /**
     * 随机对象
     */
    public static final Random random = new Random();

    /**
     * 随机事件执行命令
     */
    public static class RandomCommand implements Command<String>{

        @Override
        public String run() throws InterruptedException {
            long executeTime = random.nextInt(200);
            System.out.println("Execute Time : " + executeTime + " ms");
            Thread.sleep(executeTime);
            return "Hello, World";
        }

        @Override
        public String fallback() {
            return "Fallback";
        }
    }

    public interface Command<T>{

        /**
         * 正常执行，并且返回结果
         *
         * @return
         */
        T run() throws InterruptedException;

        /**
         * 错误时，返回容错结果
         *
         * @return
         */
        T fallback();
    }
}
