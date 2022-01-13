package com.pi.back.utils;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class CommandValidator {

    static Pattern COMMAND_MODEL_PATTERN = Pattern.compile("[\\[\\{\\(].*?[\\]\\}\\)]");

    public static Optional<String> buildCommand(String commandModel, List<String> queryParamsList) {
        Validations.notNullNorEmpty(commandModel, "commandModel");
        Validations.notNull(queryParamsList, "queryParametersList");

        boolean isValid = validateUserInput(commandModel, queryParamsList);

        if (!isValid)
            return Optional.empty();

        StringBuilder commandBuilder = new StringBuilder();
        Matcher commandMatcher = COMMAND_MODEL_PATTERN.matcher(commandModel);

        AtomicInteger index = new AtomicInteger();
        while (commandMatcher.find()) {
            commandMatcher.appendReplacement(commandBuilder, queryParamsList.get(index.getAndIncrement()));
        }
        commandMatcher.appendTail(commandBuilder);

        return Optional.of(commandBuilder.toString());
    }

    private static boolean validateUserInput(String commandModel, List<String> queryParamsSize) {
        Matcher commandMatcher = COMMAND_MODEL_PATTERN.matcher(commandModel);
        long matcherResults = commandMatcher.results().count();

        if (matcherResults == 0)
            return true;

        if (queryParamsSize != null)
            return matcherResults == queryParamsSize.size();

        return false;
    }
}
