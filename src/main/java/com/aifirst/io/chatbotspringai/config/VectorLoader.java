package com.aifirst.io.chatbotspringai.config;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.File;

@Configuration
public class VectorLoader {
    @Value("classpath:/pdf/constitution.pdf")
    private Resource britishConstitution;

    @Bean
    public SimpleVectorStore simpleVectorStore(EmbeddingModel embeddingModel) {
        var vectorStore =
                SimpleVectorStore.builder(embeddingModel).build();

        var vectorStoreFile = new File(
                "/Users/aqibjaved/IdeaProjects/ChatbotSpringAI/src/main/resources/vector_store.json"
        );

        if(vectorStoreFile.exists()) {
            System.out.println("Loaded Vector Store File!");
            vectorStore.load(vectorStoreFile);
        } else {
            System.out.println("Creating Vector Store!");
            PdfDocumentReaderConfig pdfReaderConfig =
                    PdfDocumentReaderConfig.builder()
                            .withPagesPerDocument(1)
                            .build();
            var reader = new PagePdfDocumentReader(britishConstitution , pdfReaderConfig);
            var textSplitter = new TokenTextSplitter();
            var docs = textSplitter.apply(reader.get());
            vectorStore.add(docs);
            vectorStore.save(vectorStoreFile);
            System.out.println("Vector store created successfully");
        }
        return vectorStore;
    }
}
