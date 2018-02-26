package com.meilitech.zhongyi.resource.service;

import com.meilitech.zhongyi.resource.dao.UrlDao;
import com.meilitech.zhongyi.resource.dao.UrlRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UrlService {
    @Autowired
    private UrlRepository urlRepository;

    public List<UrlDao> findByUrl(String url){
        return  urlRepository.findByUrl(url);
    }
}
