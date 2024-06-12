package fr.mazure.textimprover;

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
    
    public static ProviderEnum fromString(String text) {
        for (final ProviderEnum provider : ProviderEnum.values()) {
            if (provider.name.equals(text)) {
                return provider;
            }
        }
        throw new IllegalArgumentException("Unknown provider: " + text);
    }
}
