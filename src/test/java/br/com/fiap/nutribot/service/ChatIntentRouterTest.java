package br.com.fiap.nutribot.service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static br.com.fiap.nutribot.service.ChatIntentRouter.Intent.*;

class ChatIntentRouterTest {
    private final ChatIntentRouter router = new ChatIntentRouter();

    @Test void perguntaDeNomeNaoRecebeFerramentas() {
        assertEquals(GENERAL, router.route("Meu nome é Rafael."));
        assertEquals(GENERAL, router.route("Qual é o meu nome?"));
    }
    @Test void dadosDaClinicaUsamRag() {
        assertEquals(CLINIC_RAG, router.route("Quantos copos de água a clínica recomenda?"));
        assertEquals(CLINIC_RAG, router.route("Qual o horário de atendimento?"));
    }
    @Test void consultaAlimento() {
        assertEquals(FOOD_LOOKUP, router.route("Quantas calorias tem 100 g de tapioca?"));
    }
    @Test void resumoDiario() {
        assertEquals(DAY_SUMMARY, router.route("Quantas calorias ainda posso comer hoje?"));
    }
    @Test void registroExplicito() {
        assertEquals(MEAL_REGISTRATION, router.route("Comi 2 ovos no café da manhã"));
        assertEquals(MEAL_REGISTRATION, router.route("Registre 100 gramas de tapioca no almoço"));
    }
    @Test void perguntaNaoRegistra() {
        assertEquals(GENERAL, router.route("Qual é meu nome?"));
    }
}
