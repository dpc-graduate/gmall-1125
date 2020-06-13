package com.atguigu.gmall.item.test;

import io.netty.util.concurrent.CompleteFuture;
import io.netty.util.concurrent.DefaultThreadFactory;

import java.util.concurrent.*;

/**
 * @author: dpc
 * @data: 2020/6/13,11:12
 */
public class CompleteableFutureTest {
    public static void main(String[] args) {
        CompletableFuture.supplyAsync(() -> {
            System.out.println("supplyAsync执行了");
            int i=1/0;
            return "hello";
        }).whenComplete((t, u) -> {
            System.out.println("开启另一个任务");
            System.out.println();
            if (t != null) {
                System.out.println("正常返回结果:" + t);
            }
//            if (u != null) {
//                System.out.println("异常返回结果:" + u);
//            }
        }).exceptionally(e->{
            System.out.println(e.getMessage());
            return "异常";
        });
    }
}
