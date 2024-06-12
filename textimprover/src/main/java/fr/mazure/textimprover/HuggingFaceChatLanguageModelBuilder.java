package fr.mazure.textimprover;

import java.util.Optional;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.huggingface.HuggingFaceChatModel;

public class HuggingFaceChatLanguageModelBuilder implements IChatLanguageModelBuilder {

    @Override
    public ChatLanguageModel build(final Optional<String> model,
                                   final Optional<String> project,
                                   final Optional<String> apiKey,
                                   final Optional<Double> temperature,
                                   final Optional<Integer> seed) {

        final HuggingFaceChatModel.Builder builder = HuggingFaceChatModel.builder();

        IChatLanguageModelBuilder.handleCompulsoryParameter("model", model, s -> builder.modelId(s));
        IChatLanguageModelBuilder.handleUnusedParameter("project", project);
        IChatLanguageModelBuilder.handleCompulsoryParameter("apiKey", apiKey, s -> builder.accessToken(s));
        IChatLanguageModelBuilder.handleOptionalParameter("temperature", temperature, s -> builder.temperature(s));
        IChatLanguageModelBuilder.handleUnusedParameter("seed", seed);

        return builder.maxNewTokens(2048)  // TODO remove this
                      .build();
    }
}
