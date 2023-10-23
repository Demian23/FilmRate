package by.wtj.filmrate.command.impl;

import by.wtj.filmrate.command.Command;
import by.wtj.filmrate.command.exception.CommandException;
import by.wtj.filmrate.controller.JspPageName;
import jakarta.servlet.http.HttpServletRequest;

public class NoSuchCommand implements Command {
    @Override
    public String execute(HttpServletRequest request) throws CommandException {
        return JspPageName.errorPage;
    }
}
