package by.wtj.filmrate.command.impl;

import by.wtj.filmrate.command.Command;
import by.wtj.filmrate.command.exception.CommandException;
import by.wtj.filmrate.controller.JspPageName;

import javax.servlet.http.HttpServletRequest;

public class LogOut implements Command {
    @Override
    public String execute(HttpServletRequest request) throws CommandException {
        request.getSession().invalidate();
        return JspPageName.indexPage;
    }

    @Override
    public boolean isRedirect() {
        return true;
    }
}
