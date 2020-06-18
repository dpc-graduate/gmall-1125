package com.atguigu.gmall.index.controller;

import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.pms.entity.CategoryEntity;
import com.atguigu.gmall.index.service.IndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.Servlet;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;

/**
 * @author: dpc
 * @data: 2020/6/2,11:03
 */
@Controller
public class IndexController{
    @Autowired
    private IndexService indexService;

    @GetMapping
    public String toIndex(Model model) {
        List<CategoryEntity> categoryEntities = indexService.queryLevelOneCategories();
        model.addAttribute("categories", categoryEntities);
        return "index";
    }

    @GetMapping("index/cates/{pid}")
    @ResponseBody
    public ResponseVo<List<CategoryEntity>> querySubLevelTwo(@PathVariable Long pid) {
        List<CategoryEntity> categoryEntities = this.indexService.querySubLevelTwo(pid);
        return ResponseVo.ok(categoryEntities);
    }

    @GetMapping("test/read")
    @ResponseBody
    public ResponseVo<Object> read() {

        return ResponseVo.ok(this.indexService.read());
    }

    @GetMapping("test/writer")
    @ResponseBody
    public ResponseVo<Object> writer() {

        return ResponseVo.ok(this.indexService.writer());
    }

    @GetMapping("test/lock")
    @ResponseBody
    public ResponseVo<Object> testLock() {
        this.indexService.testLock2();
        return ResponseVo.ok();
    }
    @GetMapping("test/latch")
    @ResponseBody
    public ResponseVo<Object> latch() throws InterruptedException {

        return ResponseVo.ok(this.indexService.latch());
    }

    @GetMapping("test/down")
    @ResponseBody
    public ResponseVo<Object> down() {
        this.indexService.down();
        return ResponseVo.ok();
    }
}
