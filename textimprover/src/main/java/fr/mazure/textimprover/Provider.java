package fr.mazure.textimprover;

public enum Provider {

    HUGGING_FACE("HuggingFace"),
    MISTRAL_AI("MistralAi");

    private final String name;

    Provider(final String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
    
    public static Provider fromString(String text) {
        for (final Provider provider : Provider.values()) {
            if (provider.name.equals(text)) {
                return provider;
            }
        }
        throw new IllegalArgumentException("Unknown provider: " + text);
    }
}
