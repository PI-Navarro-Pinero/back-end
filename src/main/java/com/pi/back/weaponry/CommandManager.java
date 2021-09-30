package com.pi.back.weaponry;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
public class CommandManager {

    Pattern COMMAND_MODEL_PATTERN = Pattern.compile("(\\[.*?\\])");

    public boolean validateUserInput(String commandModel, List<String> queryParamsSize) {
        Matcher commandMatcher = COMMAND_MODEL_PATTERN.matcher(commandModel);

        if (queryParamsSize != null)
            return commandMatcher.results().count() == queryParamsSize.size();

        return false;
    }

    public String buildCommand(String commandModel, List<String> queryParamsList) {
        StringBuilder commandBuilder = new StringBuilder();
        Matcher commandMatcher = COMMAND_MODEL_PATTERN.matcher(commandModel);

        AtomicInteger index = new AtomicInteger();
        while (commandMatcher.find()) {
            commandMatcher.appendReplacement(commandBuilder, queryParamsList.get(index.getAndIncrement()));
        }
        commandMatcher.appendTail(commandBuilder);

        return commandBuilder.toString();
    }
}
