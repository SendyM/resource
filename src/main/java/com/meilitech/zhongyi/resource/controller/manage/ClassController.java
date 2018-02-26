package com.meilitech.zhongyi.resource.controller.manage;

import com.meilitech.zhongyi.resource.dao.UrlRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RequestMapping("/class")
@Controller
@EnableAutoConfiguration
public class ClassController {
    @Autowired
    UrlRepository repository;

    @RequestMapping("/")
    public String index(Model model) {
        model.addAttribute("key", "value");
        return "class";
    }

    @GetMapping
    @RequestMapping("/get")
    public String create(@RequestParam Map<String, Object> reqMap, HttpEntity<String> httpEntity) {
        return "ok";
    }
}
