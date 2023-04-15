plugins {
    java
    id("org.springframework.boot") version "3.5.5"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "com.aifirst.io"
version = "0.0.1-SNAPSHOT"
description = "ChatbotSpringAI"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://repo.spring.io/milestone")
    }
}

val springAiVersion by extra("1.0.0-M3")

dependencies {
    implementation(platform("org.springframework.ai:spring-ai-bom:${springAiVersion}"))

    // Spring Boot
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    // Spring AI
    implementation("org.springframework.ai:spring-ai-pdf-document-reader")
    implementation("org.springframework.ai:spring-ai-pgvector-store-spring-boot-starter")
    implementation("org.springframework.ai:spring-ai-openai-spring-boot-starter")

    // Optional Docker Compose
    runtimeOnly("org.springframework.boot:spring-boot-docker-compose")
    runtimeOnly("org.springframework.ai:spring-ai-spring-boot-docker-compose")

    // Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
