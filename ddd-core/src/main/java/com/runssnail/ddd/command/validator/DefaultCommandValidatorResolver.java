package com.runssnail.ddd.command.validator;

import com.runssnail.ddd.common.command.Command;
import com.runssnail.ddd.common.result.Result;
import com.runssnail.ddd.common.validator.CommandValidator;
import com.runssnail.ddd.common.validator.GlobalCommandValidator;

import org.apache.commons.lang3.Validate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import lombok.extern.slf4j.Slf4j;

/**
 * 默认的CommandValidator解析器
 *
 * @author zhengwei
 * @date 2019-11-07 15:37
 **/
@Slf4j
public class DefaultCommandValidatorResolver implements CommandValidatorResolver {

    private Map<Class, CommandValidator> commandValidatorMapping = new ConcurrentHashMap<>();

    private List<GlobalCommandValidator> globalValidators = new ArrayList<>();

    @Override
    public <C extends Command<T>, T extends Result> CommandValidator<C> resolve(Command<T> command) {
        Validate.notNull(command);
        return commandValidatorMapping.get(command.getClass());
    }

    @Override
    public <C extends Command> void registerValidator(CommandValidator<C> validator) {
        if (validator instanceof GlobalCommandValidator) {
            this.globalValidators.add((GlobalCommandValidator) validator);
            return;
        }

        Class requestType = validator.supportType();
        if (requestType == null) {
            throw new RuntimeException("The Validator's support type is required, '" + validator + "'");
        }

        if (commandValidatorMapping.containsKey(requestType)) {
            throw new RuntimeException("The Validator duplicated, '" + requestType + "'");
        }

        this.commandValidatorMapping.put(validator.supportType(), validator);
    }

    @Override
    public List<GlobalCommandValidator> getGlobalValidators() {
        return this.globalValidators;
    }

    @PostConstruct
    public void init() {

    }

}
