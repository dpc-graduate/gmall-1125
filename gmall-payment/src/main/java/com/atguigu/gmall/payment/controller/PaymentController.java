package com.atguigu.gmall.payment.controller;

import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.atguigu.gmall.oms.entity.OrderEntity;
import com.atguigu.gmall.oms.exception.OrderException;
import com.atguigu.gmall.payment.config.AlipayTemplate;
import com.atguigu.gmall.payment.service.PaymentService;
import com.atguigu.gmall.payment.vo.PayAsyncVo;
import com.atguigu.gmall.payment.vo.PayVo;
import com.atguigu.gmall.payment.vo.PaymentInfoEntity;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

/**
 * @author: dpc
 * @data: 2020/6/15,10:39
 */

@Controller
public class PaymentController {
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private AlipayTemplate alipayTemplate;

    @GetMapping("pay.html")
    public String paySelect(@RequestParam("orderToken") String orderToken, Model model) {
        OrderEntity orderEntity = paymentService.queryOrder(orderToken);
        model.addAttribute("orderEntity", orderEntity);
        return "pay";
    }

    @GetMapping("ailipay.html")
    @ResponseBody
    public String alipay(@RequestParam("orderToken") String orderToken) {
        /**
         *两套密钥：
         * 我们发起支付请求的时候用自己的私钥加密，把自己的公钥给阿里，阿里收到请求用我们的公钥解密
         * 回调的时候阿里用自己的私钥进行加密，我们用阿里的公钥进行解密
         */
        //调用支付接口，跳转到支付页面
        OrderEntity orderEntity = this.paymentService.queryOrder(orderToken);
        if (orderEntity.getStatus() != 0) {
            throw new OrderException("该订单无法支付，请查看订单状态");
        }
        PayVo payVo = new PayVo();
        payVo.setOut_trade_no(orderToken);
       // payVo.setTotal_amount(orderEntity.getPayAmount().toString());
        payVo.setTotal_amount("0.01");
        payVo.setSubject("谷粒商城支付平台");
        Long aLong = this.paymentService.savePayment(orderEntity);
        payVo.setPassback_params(aLong.toString());
        String form = null;
        try {
            form = alipayTemplate.pay(payVo);
            System.err.println(form);
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        return form;
    }

    @GetMapping("pay/return")
    public String payReturn(PayAsyncVo payAsyncVo, Model model) {
        model.addAttribute("total_amount", payAsyncVo.getTotal_amount());

        return "paysuccess";
    }

    @GetMapping("pay/success")
    @ResponseBody
    public String paySuccess(PayAsyncVo payAsyncVo){
        // 1.验签
        Boolean flag = this.alipayTemplate.checkSignature(payAsyncVo);
        if (!flag){
            return "failure";
        }

        // 2.校验业务数据:app_id、out_trade_no、total_amount。根据
        String payId = payAsyncVo.getPassback_params();
        PaymentInfoEntity paymentInfoEntity = this.paymentService.queryPaymentByPayId(Long.valueOf(payId));
        if (paymentInfoEntity == null
                || !StringUtils.equals(payAsyncVo.getOut_trade_no(), paymentInfoEntity.getOutTradeNo())
                || !StringUtils.equals(payAsyncVo.getBuyer_pay_amount(), paymentInfoEntity.getTotalAmount().toString())){
            return "failure";
        }

        // 3.校验交易状态码：TRADE_SUCCESS
        if (!StringUtils.equals("TRADE_SUCCESS", payAsyncVo.getTrade_status())){
            return "failure";
        }

        // 4.记录支付状态
        paymentInfoEntity.setPaymentStatus(1);
        paymentInfoEntity.setTradeNo(payAsyncVo.getTrade_no());
        paymentInfoEntity.setCreateTime(new Date());
        paymentInfoEntity.setCallbackContent(JSON.toJSONString(payAsyncVo));
        this.paymentService.updatePayStatus(paymentInfoEntity);

        // 5.更新订单状态 减库存.TODO

        // 6.响应支付宝需要的状态给支付宝
        return "success";
    }

}
