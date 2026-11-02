package br.com.fiap.nutribot.config;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

@Configuration
public class ClinicDocumentsLoader {
    private static final Logger log = LoggerFactory.getLogger(ClinicDocumentsLoader.class);

    @Bean
    ApplicationRunner loadClinicDocuments(VectorStore nutriBotVectorStore) {
        return args -> {
            Resource[] resources = new PathMatchingResourcePatternResolver()
                    .getResources("classpath*:docs/*.md");
            if (resources.length == 0) {
                throw new IllegalStateException("Nenhum documento encontrado em resources/docs");
            }
            List<Document> documents = new ArrayList<>();
            for (Resource resource : resources) {
                String content = resource.getContentAsString(StandardCharsets.UTF_8);
                // Cada seção temática recebe embedding próprio: hidratação, consultas etc.
                for (String section : content.split("(?m)(?=^## )")) {
                    if (!section.isBlank()) {
                        documents.add(new Document(section, Map.of("source", resource.getFilename())));
                    }
                }
            }
            // Os embeddings são calculados no startup para alimentar o vector store.
            nutriBotVectorStore.add(documents);
            log.info("RAG pronto: {} documentos de {} arquivos.", documents.size(), resources.length);
        };
    }
}
