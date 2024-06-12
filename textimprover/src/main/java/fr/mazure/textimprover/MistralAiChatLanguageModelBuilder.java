package fr.mazure.textimprover;

import java.util.Optional;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.mistralai.MistralAiChatModel;
import dev.langchain4j.model.mistralai.MistralAiChatModel.MistralAiChatModelBuilder;

public class MistralAiChatLanguageModelBuilder implements IChatLanguageModelBuilder {
    
    @Override
    public ChatLanguageModel build(final Optional<String> model,
                                   final Optional<String> project,
                                   final Optional<String> apiKey,
                                   final Optional<Double> temperature,
                                   final Optional<Integer> seed) {
        final MistralAiChatModelBuilder builder = MistralAiChatModel.builder();

        IChatLanguageModelBuilder.handleCompulsoryParameter("model", model, s -> builder.modelName(s));
        IChatLanguageModelBuilder.handleUnusedParameter("project", project);
        IChatLanguageModelBuilder.handleCompulsoryParameter("apiKey", apiKey, s -> builder.apiKey(s));
        IChatLanguageModelBuilder.handleOptionalParameter("temperature", temperature, s -> builder.temperature(s));
        IChatLanguageModelBuilder.handleOptionalParameter("seed", seed, s -> builder.randomSeed(s));

        return builder.build();
    }
}
