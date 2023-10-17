package by.wtj.filmrate.command.impl;

import by.wtj.filmrate.command.ICommand;
import by.wtj.filmrate.command.exception.CommandException;
import jakarta.servlet.http.HttpServletRequest;

public class Registration implements ICommand {
    @Override
    public String execute(HttpServletRequest request) throws CommandException {
        return null;
    }
}
