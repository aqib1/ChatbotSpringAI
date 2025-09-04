package com.aifirst.io.chatbotspringai.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("image")
public class ImageController {

    private final ChatModel chatModel;

    public ImageController(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    @GetMapping("/to-text")
    public String describeImage() {
        return ChatClient.create(chatModel)
                .prompt()
                .user(userSpec ->
                        userSpec.text("Explain what you see in this image")
                                .media(
                                        MimeTypeUtils.IMAGE_JPEG,
                                        new ClassPathResource("image/image.jpg")
                                )
                ).call()
                .content();

    }
}
