package com.money.me.motivate.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TemplatesController {
    @GetMapping("/index")
    public String indexTemp() {
        return "index";
    }

    @GetMapping("/login")
    public String loginTemp() {
        return "login";
    }

    @GetMapping("/tasks")
    public String tasksTemp() {
        return "tasks";
    }

    @GetMapping("/admin-panel")
    public String adminPanelTemp() {
        return "adminPanel";
    }
}
