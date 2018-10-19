package com.bootcamp.socialnetwork.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ApplicationController {

    @RequestMapping("/")
    String home(ModelMap modelMap) {
        modelMap.addAttribute("title", "Social Network");
        return "index";
    }

    @RequestMapping("/partials/{page}")
    String partialHandler(@PathVariable("page") final String page) {
        return page;
    }
}
