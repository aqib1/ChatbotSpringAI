package com.aifirst.io.chatbotspringai.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class BCSController {
    @Value("classpath:/prompts/british-constitution-prompt.st")
    private Resource bcsPrompt;
    private final ChatClient chatClient;
    private final VectorStore vectorStore;

    public BCSController(ChatClient.Builder builder, VectorStore vectorStore) {
//        this.chatClient = builder
//                .defaultAdvisors(new QuestionAnswerAdvisor(
//                        vectorStore,
//                        SearchRequest.defaults()
//                ))
//                .build();
        this.chatClient = builder.build();
        this.vectorStore = vectorStore;
    }

    @GetMapping("/bcs")
    public String bcsQuestion(@RequestParam String question) {
        return chatClient.prompt()
                .user(question)
                .call()
                .content();
    }

    @GetMapping("/simp-bcs")
    public String simplifyBcsQuestion(@RequestParam String question) {
        var template = new PromptTemplate(bcsPrompt);
        Map<String, Object> promptParams = Map.of(
                "input", question,
                "documents", findSimilarData(question)
        );

        return chatClient.prompt(template.create(promptParams))
                .call()
                .content();
    }

    private String findSimilarData(String question) {
        var documents = vectorStore.similaritySearch(
                SearchRequest.query(question)
                        .withTopK(5)
        );
        return documents.stream()
                .map(Document::getContent)
                .collect(Collectors.joining());
    }
}
