package com.aifirst.io.chatbotspringai.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("chat")
public class HelloController {
    @Value("classpath:/prompts/celeb-details.st")
    private Resource celebPrompt;
    @Value("classpath:/prompts/sport-details.st")
    private Resource sportPrompt;
    @Value("classpath:/prompts/smart-sports-assistant.st")
    private Resource smartSportsAssistantPrompt;
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

    @GetMapping("/celebs")
    public String getCeleb(@RequestParam String celeb) {
        PromptTemplate template = new PromptTemplate(celebPrompt);
        Prompt prompt = template.create(Map.of("name", celeb));
        var response = Optional.ofNullable(
                chatClient.prompt(prompt)
                        .call()
                        .chatResponse()
        ).orElseThrow(() -> new RuntimeException(""));

        return response.getResult()
                .getOutput()
                .getText();
    }


    @GetMapping("/sports")
    public String getSportsDetails(@RequestParam String sport) throws IOException {
        String promptTemplate = Files.readString(sportPrompt.getFile().toPath());
        String filledPrompt = MessageFormat.format(promptTemplate, sport);

        UserMessage userMessage = new UserMessage(filledPrompt);
        SystemMessage systemMessage = new SystemMessage(smartSportsAssistantPrompt);
        Prompt prompt = new Prompt(List.of(userMessage, systemMessage));

        var response = Optional.ofNullable(
                chatClient.prompt(prompt)
                        .call()
                        .chatResponse()
        ).orElseThrow(() -> new RuntimeException(""));

        return response.getResult()
                .getOutput()
                .getText();
    }
}
