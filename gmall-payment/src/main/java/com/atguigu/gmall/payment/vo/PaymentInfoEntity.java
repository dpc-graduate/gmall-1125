package com.atguigu.gmall.payment.vo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author: dpc
 * @data: 2020/6/15,15:16
 */
@Data
@TableName("payment_info")
public class PaymentInfoEntity {

    @TableId
    private Long id;

    private String outTradeNo;

    private Integer paymentType;

    private String tradeNo;

    private BigDecimal totalAmount;

    private String subject;

    private Integer paymentStatus;

    private Date createTime;

    private Date callbackTime;

    private String callbackContent;
}