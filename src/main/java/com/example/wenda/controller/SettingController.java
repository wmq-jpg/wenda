package com.example.wenda.controller;

import com.example.wenda.service.WendaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
@Controller
public class SettingController {
    @Autowired
    WendaService wendaservice;
    @RequestMapping(path={"/setting"},method= RequestMethod.GET)
    @ResponseBody
    public String index(HttpSession httpsession)
    {
        return "Setting OK."+wendaservice.getMessage(1);

    }
}
