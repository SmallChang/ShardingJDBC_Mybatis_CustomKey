package org.cly.controller;

import jdk.nashorn.internal.runtime.linker.InvokeByName;
import org.cly.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Controller
@RequestMapping("/notice")
public class NoticeController {

        @Autowired
        private UserService userService;

        @RequestMapping("/add")
        public Object add() {
            List<String> strings = userService.insertData();
            return "success";
        }

        private void queryAll() {
            userService.printData();
        }
}

