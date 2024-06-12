package fr.mazure.textimprover;

import java.util.Optional;
import java.util.function.Consumer;

import dev.langchain4j.model.chat.ChatLanguageModel;

public interface IChatLanguageModelBuilder {  
    
    ChatLanguageModel build(final Optional<String> model,
                            final Optional<String> project,
                            final Optional<String> apiKey,
                            final Optional<Double> temperature,
                            final Optional<Integer> seed);
    
    static <T> void handleCompulsoryParameter(final String parameterName,
                                              final Optional<T> parameterValue,
                                              final Consumer<T> consumer) {
        if (parameterValue.isEmpty()) {
            throw new IllegalArgumentException("Error: " + parameterName + " is missing");
        }
        consumer.accept(parameterValue.get());
    }

    static <T> void handleOptionalParameter(final String parameterName,
                                            final Optional<T> parameterValue,
                                            final Consumer<T> consumer) {
        if (parameterValue.isPresent()) {
            consumer.accept(parameterValue.get());
        }
    }

    static <T> void handleUnusedParameter(final String parameterName,
                                          final Optional<T> parameterValue) {
        if (parameterValue.isPresent()) {
            System.out.println("Warning: " + parameterName + " will be unused");
        }
    }  

}
