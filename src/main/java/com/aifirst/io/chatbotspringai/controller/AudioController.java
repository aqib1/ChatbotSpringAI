package com.aifirst.io.chatbotspringai.controller;

import org.springframework.ai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.ai.openai.OpenAiAudioSpeechModel;
import org.springframework.ai.openai.OpenAiAudioSpeechOptions;
import org.springframework.ai.openai.OpenAiAudioTranscriptionModel;
import org.springframework.ai.openai.OpenAiAudioTranscriptionOptions;
import org.springframework.ai.openai.api.OpenAiAudioApi;
import org.springframework.ai.openai.audio.speech.SpeechPrompt;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AudioController {
    private final OpenAiAudioTranscriptionModel audioTranscriptionModel;
    private final OpenAiAudioSpeechModel audioSpeechModel;

    public AudioController(
            OpenAiAudioTranscriptionModel audioTranscriptionModel,
            OpenAiAudioSpeechModel audioSpeechModel
    ) {
        this.audioTranscriptionModel = audioTranscriptionModel;
        this.audioSpeechModel = audioSpeechModel;
    }

    @GetMapping("/audio-to-text")
    public String audioTranscription() {
        var options = OpenAiAudioTranscriptionOptions
                .builder()
                .withLanguage("es")
                .withResponseFormat(OpenAiAudioApi.TranscriptResponseFormat.TEXT)
                .withTemperature(0.5f)
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

    @GetMapping("/text-to-audio/{prompt}")
    public ResponseEntity<Resource> generateAudio(@PathVariable String prompt) {
        var options = OpenAiAudioSpeechOptions.builder()
                .withModel(OpenAiAudioApi.TtsModel.TTS_1.getValue())
                .withResponseFormat(OpenAiAudioApi.SpeechRequest.AudioResponseFormat.MP3)
                .withVoice(OpenAiAudioApi.SpeechRequest.Voice.ALLOY)
                .withSpeed(1.0f)
                .build();
        var speechPrompt = new SpeechPrompt(prompt, options);
        var response = audioSpeechModel.call(speechPrompt);
        var output = response.getResult().getOutput();
        var byteArrayResource = new ByteArrayResource(output);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(byteArrayResource.contentLength())
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment()
                                .filename("test.mp3")
                                .build().toString())
                .body(byteArrayResource);


    }
}
