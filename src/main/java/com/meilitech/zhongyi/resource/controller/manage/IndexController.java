package com.meilitech.zhongyi.resource.controller.manage;

import com.meilitech.zhongyi.resource.dao.UrlRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author zhongyi
 */
@Controller
@EnableAutoConfiguration
public class IndexController {
    @Autowired
    UrlRepository repository;

    @RequestMapping("/")
    public String index(Model model) {
        model.addAttribute("key", "value");
        return "manage/index";
    }

    @RequestMapping("/resource")
    public String resource(Model model) {
        model.addAttribute("key", "value");
        return "manage/resource";
    }

    @RequestMapping("/resource/detail")
    public String resourceDetail(Model model) {
        model.addAttribute("key", "value");
        return "manage/resource_detail";
    }

}
