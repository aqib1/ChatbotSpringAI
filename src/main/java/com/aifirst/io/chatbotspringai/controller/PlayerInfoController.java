package com.aifirst.io.chatbotspringai.controller;

import com.aifirst.io.chatbotspringai.model.Achievement;
import com.aifirst.io.chatbotspringai.model.Player;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/player")
public class PlayerInfoController {
    @Value("classpath:/prompts/career-achievements-of-player.st")
    private Resource careerAchievementPrompt;
    private final ChatClient chatClient;

    public PlayerInfoController(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }


    @GetMapping("/achievements")
    public List<Achievement> getAchievements(@RequestParam String player) {
        String message = """
                Provide a List of Achievements for {player}
                """;
        PromptTemplate template = new PromptTemplate(message);
        var prompt = template.create(Map.of("player", player));
        return Optional.ofNullable(
                chatClient.prompt(prompt)
                        .call()
                        .entity(new ParameterizedTypeReference<List<Achievement>>() {
                        })
        ).orElseThrow(() -> new RuntimeException(""));
    }


    @GetMapping
    public List<Player> playerInfo(@RequestParam String sport) {
        BeanOutputConverter<List<Player>> converter =
                new BeanOutputConverter<>(new ParameterizedTypeReference<>() {

                });
        PromptTemplate promptTemplate = new PromptTemplate(careerAchievementPrompt);
        Prompt prompt = promptTemplate.create(Map.of("sport", sport, "format", converter.getFormat()));
        var chatResponse = Optional.ofNullable(
                chatClient.prompt(prompt)
                        .call()
                        .chatResponse()
        ).orElseThrow(() -> new RuntimeException(""));

        var textResponse = Optional.ofNullable(
                chatResponse.getResult()
                        .getOutput()
                        .getContent()
        ).orElseThrow(() -> new RuntimeException(""));

        return converter.convert(textResponse);
    }
}
