# Usage
```bash
cd TextImprover
mvn clean package
java -jar target/textimprover-1.0-SNAPSHOT-jar-with-dependencies.jar --user-prompt-string "Hello!" --system-prompt-string "You are a humorist. You always answer with jokes." --provider HuggingFace --model "mistralai/Mistral-7B-Instruct-v0.3" --api-key $HUGGINGFACEHUB_API_TOKEN
java -jar target/textimprover-1.0-SNAPSHOT-jar-with-dependencies.jar --user-prompt-string "Hello!" --system-prompt-string "You are a humorist. You always answer with jokes." --provider MistralAi --model "mistral-small" --api-key $MISTRALAI_API_TOKEN
```

# CLI
| parameter                                       | description                            |
| ----------------------------------------------- | -------------------------------------- |
| `--system-prompt-string <system-prompt-string>` | system prompt as a string              |
| `--system-prompt-file <system-prompt-file>`     | system prompt as the content of a file |
| `--user-prompt-string <user-prompt-string>`     | user prompt as a string                |
| `--user-prompt-file <user-prompt-file>`         | user prompt as the content of a file   |
| `--output-file <output-file>`                   | output file (stdout by default)        |
| `--error-file error-file>`                      | error file (stderr by default)         |
| `--provider <provider>`                         | provider                               |
| `--model <model>`                               | model name                             |
| `--project <project>`                           | project name                           |
| `--api-key <api-key>`                           | api key                                |
| `--temperature <temperature>`                   | temperature                            |
| `--seed <seed>`                                 | random seed                            |
| `--help`                                        | display help and exit                  |

# Parameter per provider
C compulsory  
O optional  
N unused

| provider       | model | project | api-key | temperature | seed |
| -------------- | ----- | ------- | ------- | ----------- | ---- |
| HuggingFace    | C     | N       | C       | O           | N    |
| MistralAi      | C     | N       | C       | O           | O    |
| VertexAiGemini | C     | C       | N       | O           | N    |
