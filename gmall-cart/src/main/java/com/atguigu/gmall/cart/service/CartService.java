package com.atguigu.gmall.cart.service;

import com.atguigu.gmall.cart.entity.Cart;
import com.atguigu.gmall.cart.entity.UserInfo;
import com.atguigu.gmall.cart.feign.GmallPmsClient;
import com.atguigu.gmall.cart.feign.GmallSmsClient;
import com.atguigu.gmall.cart.feign.GmallWmsClient;
import com.atguigu.gmall.cart.interceptor.LoginInterceptor;
import com.atguigu.gmall.pms.entity.SkuAttrValueEntity;
import com.atguigu.gmall.pms.entity.SkuEntity;
import com.atguigu.gmall.pms.vo.ItemSaleVo;
import com.atguigu.gmall.wms.entity.WareSkuEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.concurrent.ListenableFuture;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author: dpc
 * @data: 2020/6/10,15:01
 * 在一个类中调用类中的方法，这个方法就是这个类本身的方法，而不是代理对象的方法。比如事务注解，和异步任务。spring-task
 */
@Service
public class CartService {
    @Autowired
    private GmallPmsClient pmsClient;
    @Autowired
    private GmallWmsClient wmsClient;
    @Autowired
    private GmallSmsClient smsClient;
    @Autowired
    private StringRedisTemplate redisTemplate;
    //    @Autowired
//    private CartMapper cartMapper;
    @Autowired
    private CartAsyncService cartAsyncService;
    private static final String KEY_PREFIX = "cart:info:";
    private static final String PRICE_PREFIX = "price:info:";
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public void addCart(Cart cart) {
        //获取用户的登录状态信息，登录了以userId保存，没登录以userKey保存
        String userId = getUserId();
        String key = KEY_PREFIX + userId;
        //获取 用户的购物车
        BoundHashOperations<String, Object, Object> hashOps = redisTemplate.boundHashOps(key);
        String skuId = cart.getSkuId().toString();
        BigDecimal count = cart.getCount();
        //判断用户的购物车是否包含当前的购物记录
        try {
            if (hashOps.hasKey(skuId)) {
                //包含 新增数量
                String cartJson = hashOps.get(skuId).toString();
                cart = MAPPER.readValue(cartJson, Cart.class);
                cart.setCount(cart.getCount().add(count));
//                cartMapper.update(cart, new UpdateWrapper<Cart>().eq("user_id", userId).eq("sku_id", cart.getSkuId()));
                cartAsyncService.updateCardByUserIdAndSkuId(cart, userId);
            } else {
                //不包含，更新状态
                cart.setUserId(userId);
                SkuEntity skuEntity = pmsClient.querySkuById(cart.getSkuId()).getData();
                if (skuEntity == null) {
                    return;
                }
                cart.setPrice(skuEntity.getPrice());
                cart.setCheck(true);
                cart.setTitle(skuEntity.getTitle());
                cart.setDefaultImage(skuEntity.getDefaultImage());
                List<WareSkuEntity> wareSkuEntities = wmsClient.queryWareSkusBySkuId(cart.getSkuId()).getData();
                if (!CollectionUtils.isEmpty(wareSkuEntities)) {
                    cart.setStore(wareSkuEntities.stream().anyMatch(wareSkuEntity -> wareSkuEntity.getStock() - wareSkuEntity.getStockLocked() > 0));
                }
                List<SkuAttrValueEntity> skuAttrValueEntities = pmsClient.querySaleAttrValueBySpuId(cart.getSkuId()).getData();


                /**
                 * 销售属性
                 */
                cart.setSaleAttrs(MAPPER.writeValueAsString(skuAttrValueEntities));
                List<ItemSaleVo> itemSaleVos = smsClient.querySaleVosBySkuId(cart.getSkuId()).getData();
                /**
                 * 营销信息
                 */
                cart.setSales(MAPPER.writeValueAsString(itemSaleVos));
                //保存到redis和mysql中
//                this.cartMapper.insert(cart);
                //新增购物车时缓存价格到redis中
                this.redisTemplate.opsForValue().set(PRICE_PREFIX + skuId, skuEntity.getPrice().toString());
                this.cartAsyncService.saveCart(cart);
            }
            //保存购物车到redis中
            hashOps.put(skuId, MAPPER.writeValueAsString(cart));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

    }

    /**
     * 获取用户登录信息
     *
     * @return
     */
    private String getUserId() {
        UserInfo userInfo = LoginInterceptor.getUserInfo();
        String userId = "";
        Long uid = userInfo.getUserId();
        if (uid == null) {
            userId = userInfo.getUserKey();
        } else {
            userId = uid.toString();
        }
        return userId;
    }


    public Cart queryCartBySkuId(Long skuId) {
        String userId = this.getUserId();
        String key = KEY_PREFIX + userId;
        //获取该用户的所有购物车
        BoundHashOperations<String, Object, Object> hashOps = this.redisTemplate.boundHashOps(key);
        //判断有没有该商品对应的购物车信息
        if (hashOps.hasKey(skuId.toString())) {
            String cartJson = hashOps.get(skuId.toString()).toString();
            try {
                return MAPPER.readValue(cartJson, Cart.class);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        return new Cart();
    }

    @Async
    public ListenableFuture<String> execute1() {
        try {
            System.out.println("这是方法一开始执行");
            TimeUnit.SECONDS.sleep(5);
            System.out.println("方法一结束执行" + Thread.currentThread().getName());
            int i = 1 / 0;
            return AsyncResult.forValue("方法一");
        } catch (InterruptedException e) {
            e.printStackTrace();
            return AsyncResult.forExecutionException(e);
        }
//        return "方法一";
    }

    @Async
    public /*ListenableFuture<String>*/String execute2() {
        try {
            System.out.println("这是方法二开始执行");
            TimeUnit.SECONDS.sleep(4);
            System.out.println("方法二结束执行" + Thread.currentThread().getName());
//            return AsyncResult.forValue("方法二");
        } catch (InterruptedException e) {
            e.printStackTrace();
//            return AsyncResult.forExecutionException(e);
        }
        return "方法二";
    }

    /**
     * 查询购物车
     *
     * @return
     */
    public List<Cart> queryCarts() {
        //先查询未登录的购物车，以userKey查
        UserInfo userInfo = LoginInterceptor.getUserInfo();
        String userKey = userInfo.getUserKey();
        String unloginKey = KEY_PREFIX + userKey;
        //获取未登录状态下的购物车
        BoundHashOperations<String, Object, Object> hashOps = this.redisTemplate.boundHashOps(unloginKey);
        List<Object> cartJsons = hashOps.values();
        List<Cart> cartList = null;
        if (!CollectionUtils.isEmpty(cartJsons)) {
            cartList = cartJsons.stream().map(cartJson -> {
                try {
                    Cart cart = MAPPER.readValue(cartJson.toString(), Cart.class);
                    //查询购物车的实时价格
                    cart.setPrice(new BigDecimal(this.redisTemplate.opsForValue().get(PRICE_PREFIX + cart.getSkuId().toString())));
                    return cart;
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                    return null;
                }
            }).collect(Collectors.toList());
        }
        //是否登录？       //未登录直接返回
        Long userId = userInfo.getUserId();
        //未登录直接返回未登录状态下的购物车
        if (userId == null) {
            return cartList;
        }
        //合并购物车
        String loginKey = KEY_PREFIX + userId;
        //获取登录状态购物车
        BoundHashOperations<String, Object, Object> loginHash = this.redisTemplate.boundHashOps(loginKey);
        if (!CollectionUtils.isEmpty(cartList)) {
            //登录状态下的购物车是否已经有此商品，有则更新数量
            //遍历未登录状态下的购物车
            cartList.forEach(cart -> {
                try {
                    if (loginHash.hasKey(cart.getSkuId().toString())) {

                        BigDecimal count = cart.getCount();
                        String cartJson = loginHash.get(cart.getSkuId().toString()).toString();
                        cart = MAPPER.readValue(cartJson, Cart.class);
                        cart.setCount(count.add(cart.getCount()));
                        this.cartAsyncService.updateCardByUserIdAndSkuId(cart, userId.toString());
                    } else {
                        cart.setUserId(userId.toString());
                        this.cartAsyncService.saveCart(cart);
                    }
                    loginHash.put(cart.getSkuId().toString(), MAPPER.writeValueAsString(cart));
                } catch (JsonProcessingException e) {
                    //如果未登录状态下不包含登录状态下的购物车则新增，现在操作的是登录状态下的购物车
                    e.printStackTrace();
                }
            });
            //合并完成之后 删除未登录状态下的购物车
            this.cartAsyncService.deleteCart(userKey);
            this.redisTemplate.delete(unloginKey);
        }
        //合并完成之后，返回购物车对象
        List<Object> values = loginHash.values();
        if (!CollectionUtils.isEmpty(values)) {
            return values.stream().map(value -> {
                try {
                    Cart cart = MAPPER.readValue(value.toString(), Cart.class);
                    //查询实时价格
                    cart.setPrice(new BigDecimal(this.redisTemplate.opsForValue().get(PRICE_PREFIX + cart.getSkuId().toString())));
                    return cart;
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                    return null;
                }
            }).collect(Collectors.toList());
        }
        return null;
    }

    /**
     * 更新购物车数量
     *
     * @param cart
     */
    public void updateNum(Cart cart) {
        String userId = this.getUserId();
        String key = KEY_PREFIX + userId;
        BoundHashOperations<String, Object, Object> hashOps = this.redisTemplate.boundHashOps(key);
        //如果包含该购物车就更新数量
        try {
            if (hashOps.hasKey(cart.getSkuId().toString())) {
                //页面传递过来要更新的数量
                BigDecimal count = cart.getCount();
                String cartJson = hashOps.get(cart.getSkuId().toString()).toString();
                cart = MAPPER.readValue(cartJson, Cart.class);
                cart.setCount(count);
                //更新到mysql和redis中
                this.cartAsyncService.updateCardByUserIdAndSkuId(cart, userId);
                //这里的hasOps是里层的map，是以skuid为键，cart.toString为值的数据。
                //外层的map是以userKey为键，hasOps为值的
                hashOps.put(cart.getSkuId().toString(), MAPPER.writeValueAsString(cart));
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除购物车
     *
     * @param skuId
     */
    public void deleteCart(Long skuId) {
        String userId = this.getUserId();
        String key = KEY_PREFIX + userId;
        BoundHashOperations<String, Object, Object> hashOps = this.redisTemplate.boundHashOps(key);
        if (hashOps.hasKey(skuId.toString())) {
            this.cartAsyncService.deleteCartByUserIdAndSkuId(userId, skuId);
            hashOps.delete(skuId.toString());
        }
    }

    public List<Cart> queryCheckedCartsByUserId(Long userId) {
        String key = KEY_PREFIX + userId;
        //查询redis中的购物车
        BoundHashOperations<String, Object, Object> hashOps = this.redisTemplate.boundHashOps(key);
        List<Object> cartJsons = hashOps.values();
        //如果redis中有记录则把查到的字符串反序列化为Cart对象，没有记录则直接返回null;
        if (!CollectionUtils.isEmpty(cartJsons)) {
            return cartJsons.stream().map(cart -> {
                try {
                    return MAPPER.readValue(cart.toString(), Cart.class);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
                return null;
            }).filter(Cart::getCheck).collect(Collectors.toList());

        }
        return null;
    }

/*    @Scheduled(fixedRate = 10000)
    public void testScheduled(){
        System.out.println("这是一个定时任务"+System.currentTimeMillis());
    }*/

}
