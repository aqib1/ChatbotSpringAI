package com.aifirst.io.chatbotspringai.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("chat")
public class HelloController {
    private final ChatClient chatClient;

    public HelloController(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    @GetMapping
    public String prompt(@RequestParam String message) {
//        return this.chatClient
//                .prompt(message)
//                .call()
//                .content();

        var response = Optional.ofNullable(this.chatClient
                .prompt(message)
                .call()
                .chatResponse()).orElseThrow(() -> new RuntimeException(""));

        return response.getResult()
                .getOutput()
                .getText();
    }
}
