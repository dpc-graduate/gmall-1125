package com.atguigu.gmall.ums.config;

import net.bytebuddy.implementation.bind.annotation.Super;

/**
 * @author: dpc
 * @data: 2020/6/9,15:52
 */
public class UserException extends RuntimeException {
    public UserException(String message) {
        super(message);
    }
    public UserException(){

    }
}
