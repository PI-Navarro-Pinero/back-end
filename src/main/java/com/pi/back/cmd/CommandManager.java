package com.pi.back.cmd;

import com.pi.back.services.SystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Configuration
public class CommandManager {

    Pattern COMMAND_MODEL_PATTERN = Pattern.compile("(\\[.*?\\])");

    private final SystemService systemService;

    @Autowired
    public CommandManager(SystemService systemService) {
        this.systemService = systemService;
    }

    @Bean
    public void executeCommand(/*Integer weaponId, Integer actionId, List<String> queryParamsList*/) {
        Integer weaponId = 0;
        Integer actionId = 0;
        List<String> queryParamsList = List.of("hello world!");

        String commandModel = systemService.retrieveCommandModel(weaponId, actionId);
        String command = build(commandModel, queryParamsList);
        systemService.run(command, weaponId.toString(), actionId.toString());
    }

    public boolean validateCommandWithQueryParametersSize(String commandModel, int queryParamsSize) {
        Matcher commandMatcher = COMMAND_MODEL_PATTERN.matcher(commandModel);

        return commandMatcher.results().count() == queryParamsSize;
    }

    public String build(String commandModel, List<String> queryParamsList) {
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
