package com.atguigu.gmall.order.exception;

/**
 * @author: dpc
 * @data: 2020/6/12,11:37
 */
public class OrderException extends RuntimeException {
    public OrderException() {
        super();
    }

    public OrderException(String message) {
        super(message);
    }
}
