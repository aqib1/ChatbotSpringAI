package com.aifirst.io.chatbotspringai.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.image.ImageModel;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.openai.OpenAiImageOptions;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("image")
public class ImageController {

    private final ChatModel chatModel;
    private final ImageModel imageModel;

    public ImageController(ChatModel chatModel, ImageModel imageModel) {
        this.chatModel = chatModel;
        this.imageModel = imageModel;
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

    // Using DALL-E model
    @GetMapping("/to-image/{prompt}")
    public String generateImage(@PathVariable String prompt) {
       var imageResponse = imageModel.call(
                new ImagePrompt(
                        prompt,
                        OpenAiImageOptions.builder()
                                .N(1)
                                .width(1024)
                                .height(1024)
                                .quality("hd")
                                .build()
                )
        );

       return imageResponse.getResult().getOutput().getUrl();
    }
}
