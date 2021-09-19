package com.pi.back.cmd;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Configuration
public class CommandManager {

    Pattern COMMAND_MODEL_PATTERN = Pattern.compile("\\[(.*?)\\]");

    public void executeCommand(Integer weaponId, Integer actionId, List<String> queryParamsList) {

    }

    @Bean
    public boolean validateCommandWithQueryParametersSize(/*String commandModel, int queryParamsSize*/) {
        List<String> queryParamsList = List.of("foo");
        int queryParamsSize = queryParamsList.size();
        String commandModel = "python -m pandora -m intrusion [var1] -et -it2 [var2]";

        Matcher commandMatcher = COMMAND_MODEL_PATTERN.matcher(commandModel);

        return commandMatcher.results().count() == queryParamsSize;
    }
}
