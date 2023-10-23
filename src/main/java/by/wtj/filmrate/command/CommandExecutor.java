package by.wtj.filmrate.command;

import by.wtj.filmrate.command.impl.NoSuchCommand;
import by.wtj.filmrate.command.impl.Registration;

import java.util.HashMap;
import java.util.Map;

public final class CommandExecutor {
    private static final CommandExecutor instance = new CommandExecutor();
    private final Map<CommandName, Command> commandNameToCommand = new HashMap<>();

    private CommandExecutor(){
        commandNameToCommand.put(CommandName.Registration, new Registration());
        commandNameToCommand.put(CommandName.NoSuchCommand, new NoSuchCommand());
    }

    public static CommandExecutor getInstance(){return instance;}
    public Command getCommand(String commandName){
        CommandName name;
        try{
            name = CommandName.valueOf(commandName);
        }catch(IllegalArgumentException ex){
            //TODO log here?
            name = CommandName.NoSuchCommand;
        }
        return commandNameToCommand.get(name);
    }
}
