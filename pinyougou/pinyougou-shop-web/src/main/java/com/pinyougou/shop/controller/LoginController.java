package com.pinyougou.shop.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RequestMapping("/login")
@RestController
public class LoginController {
    @GetMapping("/getUsername")
    public Map<String, String> getUserName() {
        Map<String,String> resultMap = new HashMap<>();
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        resultMap.put("username",username);
        return resultMap;
    }
}
