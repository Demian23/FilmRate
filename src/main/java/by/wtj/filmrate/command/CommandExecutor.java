package by.wtj.filmrate.command;

import by.wtj.filmrate.command.impl.*;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public final class CommandExecutor {
    @Getter
    private static final CommandExecutor instance = new CommandExecutor();
    private final Map<CommandName, Command> commandNameToCommand = new HashMap<>();

    private CommandExecutor(){
        commandNameToCommand.put(CommandName.Registration, new Registration());
        commandNameToCommand.put(CommandName.LogOut, new LogOut());
        commandNameToCommand.put(CommandName.NoSuchCommand, new NoSuchCommand());
        commandNameToCommand.put(CommandName.Authorization, new Authorization());
        commandNameToCommand.put(CommandName.FillFilmsInUserPage, new FillFilmsInUserPage());
        commandNameToCommand.put(CommandName.FilmDetails, new FilmDetails());
        commandNameToCommand.put(CommandName.ChangeLanguage, new ChangeLanguage());
        commandNameToCommand.put(CommandName.SetUserMarkAndComment, new SetUserMarkAndComment());
        commandNameToCommand.put(CommandName.AddCommentsToCurrentFilmInSession, new AddCommentsToCurrenFilm());
        commandNameToCommand.put(CommandName.AddMarksToCurrentFilmInSession, new AddMarksToCurrentFilmInSession());
        commandNameToCommand.put(CommandName.GetUsers, new GetUsers());
        commandNameToCommand.put(CommandName.TakeOffBan, new TakeOffBan());
        commandNameToCommand.put(CommandName.BanUser, new BanUser());
        commandNameToCommand.put(CommandName.AddNewFilm, new AddNewFilm());
    }

    public Command getCommand(String commandName){
        CommandName name;
        try{
            name = CommandName.valueOf(commandName);
        }catch(IllegalArgumentException ex){
            name = CommandName.NoSuchCommand;
        }
        return commandNameToCommand.get(name);
    }
}
