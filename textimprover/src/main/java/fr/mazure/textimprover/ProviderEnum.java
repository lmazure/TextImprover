package fr.mazure.textimprover;

import java.util.Arrays;

public enum ProviderEnum {

    HUGGING_FACE("HuggingFace"),
    MISTRAL_AI("MistralAi"),
    VERTEX_AI_GEMINI("VertexAiGemini");

    private final String name;

    ProviderEnum(final String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
    
    public static ProviderEnum fromString(final String text) {
        return Arrays.stream(ProviderEnum.values())
                     .filter(p -> p.name.equals(text))
                     .findFirst()
                     .orElseThrow(() -> new IllegalArgumentException("Unknown provider: " + text));
    }
}
