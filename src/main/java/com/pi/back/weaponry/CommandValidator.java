package com.pi.back.weaponry;

import org.springframework.stereotype.Component;

import javax.naming.directory.InvalidAttributesException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class CommandValidator {

    Pattern COMMAND_MODEL_PATTERN = Pattern.compile("[\\[\\{\\(].*?[\\]\\}\\)]");

    public String buildCommand(String commandModel, List<String> queryParamsList) throws InvalidAttributesException {
        boolean isValid = validateUserInput(commandModel, queryParamsList);

        if (!isValid)
            throw new InvalidAttributesException("Unsuitable parameters list");

        StringBuilder commandBuilder = new StringBuilder();
        Matcher commandMatcher = COMMAND_MODEL_PATTERN.matcher(commandModel);

        AtomicInteger index = new AtomicInteger();
        while (commandMatcher.find()) {
            commandMatcher.appendReplacement(commandBuilder, queryParamsList.get(index.getAndIncrement()));
        }
        commandMatcher.appendTail(commandBuilder);

        return commandBuilder.toString();
    }

    private boolean validateUserInput(String commandModel, List<String> queryParamsSize) {
        Matcher commandMatcher = COMMAND_MODEL_PATTERN.matcher(commandModel);
        long matcherResults = commandMatcher.results().count();

        if (matcherResults == 0)
            return true;

        if (queryParamsSize != null)
            return matcherResults == queryParamsSize.size();

        return false;
    }
}
