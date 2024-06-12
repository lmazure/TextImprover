# Usage
```bash
cd TextImprover
mvn clean package
java -jar target/textimprover-1.0-SNAPSHOT-jar-with-dependencies.jar --user-prompt-string "Hello!" --system-prompt-string "You are a humorist. You always answer with jokes." --model "mistralai/Mistral-7B-Instruct-v0.3" --api-key $HUGGINGFACEHUB_API_TOKEN --provider HuggingFace
java -jar target/textimprover-1.0-SNAPSHOT-jar-with-dependencies.jar --user-prompt-string "Hello!" --system-prompt-string "You are a humorist. You always answer with jokes." --model "mistral-small" --api-key $MISTRALAI_API_TOKEN --provider MistralAi
```

# Parameter per provider
C compulsory
O optional
N unused

| provider       | model | project | api-key | temperature | seed |
| -------------- | ----- | ------- | ------- | ----------- | ---- |
| HuggingFace    | C     | N       | C       | O           | N    |
| MistralAi      | C     | N       | C       | O           | O    |
| VertexAiGemini | C     | C       | N       | O           | N    |
