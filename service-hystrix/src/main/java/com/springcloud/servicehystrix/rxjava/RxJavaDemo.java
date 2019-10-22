package com.springcloud.servicehystrix.rxjava;

import rx.Observable;
import rx.Single;
import rx.schedulers.Schedulers;

import java.util.Arrays;
import java.util.List;

/**
 * Title: RxJava 1.x 示例
 * Description:
 *
 * @author 谭 tmn
 * @email AbelEthan@126.com
 * @date 2018/10/30 23:40
 */
public class RxJavaDemo {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("主线程：" + Thread.currentThread().getName());
        demoStandardReactive();
    }

    private static void demoStandardReactive() throws InterruptedException {
        List<Integer> values = Arrays.asList(1, 2, 3);
        //发布多个数据
        Observable.from(values)
                // 在 I/O 线程执行
                .subscribeOn(Schedulers.newThread())
                // 订阅并且消费数据
                .subscribe(value ->{
                    if (value > 2){
                        throw new IllegalStateException("数据不应许大于 2");
                    }
                    //消费数据
                    println("消费数据：" + value);
                },e ->{
                    // 当异常情况，中断执行
                    println("发生异常" + e.getMessage());
                }, () -> {
                    // 当整体流程完成时
                    println("流程执行完成");
                })
        ;
        // 等待线程执行完毕
        Thread.sleep(100);
    }

    private static void demoObservable() throws InterruptedException {
        List<Integer> values = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8);
        //发布多个数据
        Observable.from(values)
                // 在 I/O 线程执行
                .subscribeOn(Schedulers.io())
                // 订阅并且消费数据
                .subscribe(RxJavaDemo::println)
        ;
        // 等待线程执行完毕
        Thread.sleep(100);
    }

    private static void demoSingle(){
        // 仅能发布单个数据
        Single.just("Hello, World")
                // 在 I/O 线程执行
                .subscribeOn(Schedulers.io())
                // 订阅并且消费数据
                .subscribe(RxJavaDemo::println)
        ;
    }

    public static void println(Object value) {
        System.out.printf("[线程：%s] 数据：%s\n", Thread.currentThread().getName(), value);
    }
}
