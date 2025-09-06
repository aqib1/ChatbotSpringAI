package com.aifirst.io.chatbotspringai.controller;

import org.springframework.ai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.ai.openai.OpenAiAudioTranscriptionModel;
import org.springframework.ai.openai.OpenAiAudioTranscriptionOptions;
import org.springframework.ai.openai.api.OpenAiAudioApi;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("audio")
public class AudioController {
    private final OpenAiAudioTranscriptionModel audioTranscriptionModel;
    public AudioController(OpenAiAudioTranscriptionModel audioTranscriptionModel) {
        this.audioTranscriptionModel = audioTranscriptionModel;
    }

    @GetMapping("/to-text")
    public String audioTranscription() {
        var options = OpenAiAudioTranscriptionOptions
                .builder()
                .language("es")
                .responseFormat(OpenAiAudioApi.TranscriptResponseFormat.TEXT)
                .temperature(0.5f)
                .build();
        var prompt = new AudioTranscriptionPrompt(
                new ClassPathResource("voice/text-voice.wav"),
                options
        );

        return audioTranscriptionModel
                .call(prompt)
                .getResult()
                .getOutput();
    }
}
