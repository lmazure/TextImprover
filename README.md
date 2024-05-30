```bash
cd TextImprover
mvn clean package
java -jar target/textimprover-1.0-SNAPSHOT-jar-with-dependencies.jar --user-prompt-string "Hello!" --system-prompt-string "You are a humorist. You always answer with jokes." --model "mistralai/Mistral-7B-Instruct-v0.3" --api-key $HUGGINGFACEHUB_API_TOKEN
```
