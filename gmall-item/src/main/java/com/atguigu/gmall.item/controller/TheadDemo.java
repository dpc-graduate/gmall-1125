package com.atguigu.gmall.item.controller;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * @author: dpc
 * @data: 2020/6/9,10:41
 */
public class TheadDemo {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        System.out.println(CompletableFuture.supplyAsync(() -> {
            System.out.println("supplyAsync初始化子任务");
            int i = 1 / 0;
            return "11";
        }).whenCompleteAsync((t, u) -> {
            // t是返回结果集
            // u是异常信息
            System.out.println("开启一个任务");
            System.out.println("这是正常信息:"+t);
            System.out.println("这是异常信息:"+u);

        }).exceptionally(t -> {
            //处理异常结果集的
            System.out.println("info：" + t);
            return "发生异常了";
        }).get());
        System.out.println("主线程打印");
    }
}
