package com.pro.snap.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.pro.snap.config.AccessLimit;
import com.pro.snap.exception.GlobalException;
import com.pro.snap.pojo.Orders;
import com.pro.snap.pojo.SnapMessages;
import com.pro.snap.pojo.SnapOrders;
import com.pro.snap.pojo.Users;
import com.pro.snap.rabbitmq.MQSender;
import com.pro.snap.service.IGoodsService;
import com.pro.snap.service.IOrdersService;
import com.pro.snap.service.ISnapOrdersService;
import com.pro.snap.utils.JsonUtil;
import com.pro.snap.vo.GoodsVo;
import com.pro.snap.vo.RespBean;
import com.pro.snap.vo.RespBeanEnum;
import com.wf.captcha.ArithmeticCaptcha;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Controller
@RequestMapping("/snap")
public class SnapController implements InitializingBean {

    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private IOrdersService ordersService;
    @Autowired
    private ISnapOrdersService snapOrdersService;
    @Autowired
    private MQSender mqSender;
    @Autowired
    private RedisScript<Long> redisScript;
    @Autowired
    private RedisTemplate redisTemplate;
    private Map<Long, Boolean> EmptyStockMap = new HashMap<>();

    //秒杀
    // Linux 优化前QPS: 201
    // Windows优化前QPS: 576
    // Windows优化后QPS: 2807
    @RequestMapping("/doSnap2")
    public String doSnap2(Model model, Users users, Long goodsId){
        if (users == null){
            return "login";
        }
        model.addAttribute("users", users);
        GoodsVo goods = goodsService.findGoodsVoByGoodsId(goodsId);
        //判断库存
        if(goods.getStockCount() < 1){
            model.addAttribute("errmsg", RespBeanEnum.EMPTY_STOCK.getMessage());
            return "snapFail";
        }
        //判断订单是否重复抢购
        SnapOrders snapOrders = snapOrdersService.getOne(new QueryWrapper<SnapOrders>().
                eq("users_id", users.getId()).
                eq("goods_id", goodsId));
        if (snapOrders != null){
            model.addAttribute("errmsg", RespBeanEnum.REPEAT_ERROR.getMessage());
            return "snapFail";
        }
        Orders orders = ordersService.snap(users, goods);
        model.addAttribute("orders", orders);
        model.addAttribute("goods", goods);
        return "ordersDetail";
    }

    //秒杀
    // Linux 优化前QPS: 201
    // Windows优化前QPS: 576
    // Windows缓存后QPS: 2534
    // Windows优化后QPS: 3652
    @RequestMapping(value ="/{path}/doSnap", method = RequestMethod.POST)
    @ResponseBody
    public RespBean doSnap(@PathVariable String path, Users users, Long goodsId){
        if (users == null){
            return RespBean.error(RespBeanEnum.USER_NOT_EXIST);
        }
        /*
        GoodsVo goods = goodsService.findGoodsVoByGoodsId(goodsId);
        //判断库存
        if(goods.getStockCount() < 1){
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }
        //判断是否重复抢购
        //SnapOrders snapOrders = snapOrdersService.getOne(new QueryWrapper<SnapOrders>().
                //eq("users_id", users.getId()).
                //eq("goods_id", goodsId));
        SnapOrders snapOrders = (SnapOrders) redisTemplate.opsForValue()
                .get("orders:"+users.getId()+":"+goodsId);
        if (snapOrders != null){
            return RespBean.error(RespBeanEnum.REPEAT_ERROR);
        }
        Orders orders = ordersService.snap(users, goods);
        return RespBean.success(orders);
         */
        ValueOperations valueOperations = redisTemplate.opsForValue();
        Boolean checkResult = ordersService.checkSnapPath(users, goodsId, path);
        if(!checkResult){
            return RespBean.error(RespBeanEnum.ILLEGAL_REQUEST);
        }
        // 判断是否重复抢购
        SnapOrders snapOrders = (SnapOrders) redisTemplate.opsForValue()
                .get("orders:" + users.getId() + ":"+goodsId);
        if (snapOrders != null){
            return RespBean.error(RespBeanEnum.REPEAT_ERROR);
        }
        // 内存标记，减少Redis访问
        if (EmptyStockMap.get(goodsId)){
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }
        // 预减库存
        // Long stock = valueOperations.decrement("snapGoods:" + goodsId);
        Long stock = (Long) redisTemplate.execute(redisScript, Collections.singletonList("snapGoods:" + goodsId),
                Collections.EMPTY_LIST);
        if (stock < 0){
            EmptyStockMap.put(goodsId, true);
            //valueOperations.increment("snapGoods:" + goodsId);
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }
        SnapMessages snapMessages = new SnapMessages(users, goodsId);
        mqSender.sendSnapMessages_topic(JsonUtil.object2JsonStr(snapMessages));
        return RespBean.success(0);
    }

    // 获取秒杀结果，返回值 -> ordersId：成功， -1：失败， 0：排队中
    @RequestMapping(value = "/getResult", method = RequestMethod.GET)
    @ResponseBody
    public RespBean getResult(Users users, Long goodsId){
        if(users == null){
            return RespBean.error(RespBeanEnum.USER_NOT_EXIST);
        }
        Long ordersId = snapOrdersService.getResult(users, goodsId);
        return RespBean.success(ordersId);
    }

    // 获取秒杀地址
    @AccessLimit(second = 5, maxCount = 5, needLogin = true)
    @RequestMapping(value = "/path", method = RequestMethod.GET)
    @ResponseBody
    public RespBean getSnapPath(Users users, Long goodsId, String captcha, HttpServletRequest request){
        if(users == null) {
            return RespBean.error(RespBeanEnum.USER_NOT_EXIST);
        }
        /*
        ValueOperations valueOperations = redisTemplate.opsForValue();
        // 限制访问次数，5秒内访问5次
        String uri = request.getRequestURI();
        // 用于测试
        captcha = "0";
        Integer count = (Integer) valueOperations.get(uri + ":" + users.getId());
        if(count == null) {
            valueOperations.set(uri + ":" + users.getId(), 1, 5, TimeUnit.SECONDS);
        }
        else if(count < 5) {
            valueOperations.increment(uri + ":" + users.getId());
        }
        else {
            return RespBean.error(RespBeanEnum.FREQUENT_ACCESS);
        }
        */
        boolean checkResult = ordersService.checkCaptcha(users, goodsId, captcha);
        if(!checkResult){
            return RespBean.error(RespBeanEnum.WRONG_CAPTCHA);
        }
        String str = ordersService.createSnapPath(users, goodsId);
        return RespBean.success(str);
    }

    // 生成验证码，使用流的方式输出
    @RequestMapping(value = "/captcha", method = RequestMethod.GET)
    @ResponseBody
    public void getCaptcha(Users users, Long goodsId, HttpServletResponse response){
        if(users == null || goodsId < 0){
            throw new GlobalException(RespBeanEnum.ILLEGAL_REQUEST);
        }
        // 设置请求为图片类型
        response.setContentType("image/jpg");
        response.setHeader("Pargam", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        // 生成验证码，将结果存入Redis
        ArithmeticCaptcha captcha = new ArithmeticCaptcha(130, 32, 3);
        redisTemplate.opsForValue().set("captcha:" + users.getId() + ":" + goodsId, captcha.text(), 300, TimeUnit.SECONDS);
        try {
            captcha.out(response.getOutputStream());
        }
        catch(IOException e) {
            log.error("Generate captcha failed", e.getMessage());
        }

    }

    // implements重写，系统初始化，把商品加载到Redis
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> list = goodsService.findGoodsVo();
        if(CollectionUtils.isEmpty(list)) {
            return;
        }
        list.forEach(goodsVo -> {
                    redisTemplate.opsForValue().set("snapGoods:" + goodsVo.getId(), goodsVo.getStockCount());
                    EmptyStockMap.put(goodsVo.getId(), false);
                }
        );
    }
}
