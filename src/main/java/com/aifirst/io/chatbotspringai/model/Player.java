package com.aifirst.io.chatbotspringai.model;

import java.util.List;

public record Player(
        String playerName,
        List<String> achievements
) {
}
