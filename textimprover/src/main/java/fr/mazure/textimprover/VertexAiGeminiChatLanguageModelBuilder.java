package fr.mazure.textimprover;

import java.util.Optional;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.vertexai.VertexAiGeminiChatModel;
import dev.langchain4j.model.vertexai.VertexAiGeminiChatModel.VertexAiGeminiChatModelBuilder;

public class VertexAiGeminiChatLanguageModelBuilder implements IChatLanguageModelBuilder {
    
    @Override
    public ChatLanguageModel build(final Optional<String> model,
                                   final Optional<String> project,
                                   final Optional<String> apiKey,
                                   final Optional<Double> temperature,
                                   final Optional<Integer> seed) {

        final VertexAiGeminiChatModelBuilder builder = VertexAiGeminiChatModel.builder();

        IChatLanguageModelBuilder.handleCompulsoryParameter("project", project, s -> builder.project(s));
        IChatLanguageModelBuilder.handleCompulsoryParameter("model", model, s -> builder.modelName(s));
        IChatLanguageModelBuilder.handleUnusedParameter("apiKey", apiKey);
        IChatLanguageModelBuilder.handleOptionalParameter("temperature", temperature, s -> builder.temperature(s.floatValue()));
        IChatLanguageModelBuilder.handleUnusedParameter("seed", seed);
        
        return builder.build();
    }
}
