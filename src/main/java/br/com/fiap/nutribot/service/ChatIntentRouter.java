package br.com.fiap.nutribot.service;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

/** Separação determinística das intenções para não enviar ferramentas desnecessárias ao modelo. */
@Component
public class ChatIntentRouter {
    public enum Intent { GENERAL, CLINIC_RAG, FOOD_LOOKUP, DAY_SUMMARY, MEAL_REGISTRATION }

    private static final Pattern REGISTER = Pattern.compile(
            "\\b(comi|consumi|anote|anota|anotar|registre|registra|registrar|adicione|adicionar|inclua|lance|lancar)\\b");
    private static final Pattern SUMMARY = Pattern.compile(
            "\\b(resumo|saldo|diario|restante|restam|falta|consumidas|consumidos)\\b");
    private static final Pattern FOOD = Pattern.compile(
            "\\b(calorias|calorico|proteinas|proteina|carboidratos|carboidrato|gorduras|gordura|nutrientes|macros)\\b");
    private static final Pattern CLINIC = Pattern.compile(
            "\\b(clinica|guia|recomenda|recomendacao|agua|hidratacao|copos|horario|atendimento|endereco|agendamento|consulta|cancelamento|prato|jejum|frutas|fibras|lanche|suplementos|gestantes)\\b");

    public Intent route(String original) {
        String message = normalize(original);
        if (REGISTER.matcher(message).find() && !message.contains("nao registre")) {
            return Intent.MEAL_REGISTRATION;
        }
        if (SUMMARY.matcher(message).find()
                || (message.contains("hoje") && (message.contains("caloria") || message.contains("comer") || message.contains("consumi")))
                || message.contains("ainda posso comer")) {
            return Intent.DAY_SUMMARY;
        }
        if (FOOD.matcher(message).find() && !message.contains("clinica recomenda")) {
            return Intent.FOOD_LOOKUP;
        }
        if (CLINIC.matcher(message).find()) {
            return Intent.CLINIC_RAG;
        }
        return Intent.GENERAL;
    }

    private static String normalize(String text) {
        String normalized = Normalizer.normalize(text, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "");
        return normalized.toLowerCase(Locale.ROOT);
    }
}
