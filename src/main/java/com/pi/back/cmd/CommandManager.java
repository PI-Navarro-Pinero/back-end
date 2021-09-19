package com.pi.back.cmd;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Configuration
public class CommandManager {

    Pattern COMMAND_MODEL_PATTERN = Pattern.compile("(\\[.*?\\])");

    public void executeCommand(Integer weaponId, Integer actionId, List<String> queryParamsList) {

    }

//    @Bean
    public boolean validateCommandWithQueryParametersSize(/*String commandModel, int queryParamsSize*/) {
        List<String> queryParamsList = List.of("foo", "bar");
        int queryParamsSize = queryParamsList.size();
        String commandModel = "python -m pandora -m intrusion [var1] -et -it2 [var2]";

        Matcher commandMatcher = COMMAND_MODEL_PATTERN.matcher(commandModel);

        return commandMatcher.results().count() == queryParamsSize;
    }

    @Bean
    public String build(/*String commandModel, List<String> queryParamsList*/) {
        List<String> queryParamsList = List.of("foo", "bar");

        String commandModel ="python -m pandora -m intrusion [var1] -et -it2 [var2]";

        StringBuilder commandBuilder = new StringBuilder();
        Matcher commandMatcher = COMMAND_MODEL_PATTERN.matcher(commandModel);

        AtomicInteger index = new AtomicInteger();
        while (commandMatcher.find())
        {
            commandMatcher.appendReplacement(commandBuilder, queryParamsList.get(index.getAndIncrement()));
        }
        commandMatcher.appendTail(commandBuilder);

        return commandBuilder.toString();
    }
}
