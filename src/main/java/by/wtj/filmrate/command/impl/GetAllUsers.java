package by.wtj.filmrate.command.impl;

import by.wtj.filmrate.command.Command;
import by.wtj.filmrate.command.exception.CommandException;

import javax.servlet.http.HttpServletRequest;

public class GetAllUsers implements Command {
    @Override
    public String execute(HttpServletRequest request) throws CommandException {
        return null;
    }

    @Override
    public boolean isRedirect() {
        return true;
    }
}
