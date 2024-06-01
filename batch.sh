#!/bin/sh

find $1 -iname '*.md' -print | while read file
do
  echo $file
  java -jar textimprover/target/textimprover-1.0-SNAPSHOT-jar-with-dependencies.jar --system-prompt-file sysprompt-fr.txt --user-prompt-file $file --output-file $file.new --error-file $file.log --model "mistralai/Mistral-7B-Instruct-v0.3" --api-key $HUGGINGFACEHUB_API_TOKEN
  if [ $? -eq 0 ]
  then
    mv $file.new $file
  else
    rm $file.new
  fi
done
