package com.pro.snap.controller;

import com.pro.snap.pojo.Users;
import com.pro.snap.service.IGoodsService;
import com.pro.snap.service.IUsersService;
import com.pro.snap.vo.DetailVo;
import com.pro.snap.vo.GoodsVo;
import com.pro.snap.vo.RespBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("/goods")
public class GoodsController {
    @Autowired
    private IUsersService usersService;
    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private ThymeleafViewResolver thymeleafViewResolver;
    // 跳转到商品列表页
    // Linux 优化前QPS: 384
    // Windows优化前QPS: 2052
    // Windows缓存QPS: 4023
    @RequestMapping(value = "/toList", produces = "text/html;charset=utf-8")
    @ResponseBody
    public String toList(Model model, Users users, HttpServletRequest request, HttpServletResponse response){
        // Redis中获取页面，如果不为空，直接返回界面
        ValueOperations valueOperations = redisTemplate.opsForValue();
        String html = (String)valueOperations.get("goodsList");
        if(!StringUtils.isEmpty(html)){
            return html;
        }
        model.addAttribute("users", users);
        model.addAttribute("goodsList", goodsService.findGoodsVo());
        //return "goodsList";
        //如果为空，手动渲染，存入Redis并返回
        WebContext webContext = new WebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goodsList", webContext);
        if(!StringUtils.isEmpty(html)){
            valueOperations.set("goodsList", html, 60, TimeUnit.SECONDS);
        }
        return html;
    }


    // 跳转到商品详情页
    @RequestMapping(value = "/toDetail2/{goodsId}", produces = "text/html;charset=utf-8")
    @ResponseBody
    public String toDetail2(Model model, Users users, @PathVariable Long goodsId, HttpServletRequest request, HttpServletResponse response){
        ValueOperations valueOperations = redisTemplate.opsForValue();
        String html = (String)valueOperations.get("goodsDetail:" + goodsId);
        // Redis中获取页面，如果不为空，直接返回界面
        if(!StringUtils.isEmpty(html)){
            return html;
        }
        model.addAttribute("users", users);
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
        Date startDate = goodsVo.getStartDate();
        Date endDate = goodsVo.getEndDate();
        Date nowDate = new Date();
        int snapStatus = 0;
        //秒杀倒计时
        int remainSeconds = 0;
        //秒杀持续时间
        //int lastSeconds = (int)((endDate.getTime() - startDate.getTime())/1000);
        //未开始
        if(nowDate.before(startDate)){
            remainSeconds = (int)((startDate.getTime() - nowDate.getTime())/1000);
        }
        //秒杀结束
        else if(nowDate.after(endDate)){
            snapStatus = 2;
            remainSeconds = -1;
        }
        //秒杀进行中
        else{
            snapStatus = 1;
            remainSeconds = 0;
        }
        model.addAttribute("remainSeconds", remainSeconds);
        model.addAttribute("snapStatus", snapStatus);
        model.addAttribute("goods", goodsVo);
        // return "goodsDetail";
        WebContext webContext = new WebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goodsDetail", webContext);
        if(!StringUtils.isEmpty(html)){
            valueOperations.set("goodsDetail:" + goodsId, html, 60, TimeUnit.SECONDS);
        }
        return html;
    }

    // 跳转到商品详情页
    @RequestMapping("/detail/{goodsId}")
    @ResponseBody
    public RespBean toDetail(Model model, Users users, @PathVariable Long goodsId){
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
        Date startDate = goodsVo.getStartDate();
        Date endDate = goodsVo.getEndDate();
        Date nowDate = new Date();
        int snapStatus = 0;
        //秒杀倒计时
        int remainSeconds = 0;
        //秒杀持续时间
        //int lastSeconds = (int)((endDate.getTime() - startDate.getTime())/1000);
        //未开始
        if(nowDate.before(startDate)){
            remainSeconds = (int)((startDate.getTime() - nowDate.getTime())/1000);
        }
        //秒杀结束
        else if(nowDate.after(endDate)){
            snapStatus = 2;
            remainSeconds = -1;
        }
        //秒杀进行中
        else{
            snapStatus = 1;
            remainSeconds = 0;
        }
        DetailVo detailVo = new DetailVo();
        detailVo.setUsers(users);
        detailVo.setGoodsVo(goodsVo);
        detailVo.setSnapStatus(snapStatus);
        detailVo.setRemainSeconds(remainSeconds);
        return RespBean.success(detailVo);
    }
}
