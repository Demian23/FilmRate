package by.wtj.filmrate.command.impl;

import by.wtj.filmrate.command.Command;
import by.wtj.filmrate.command.CommandName;
import by.wtj.filmrate.command.SessionAttributes;
import by.wtj.filmrate.command.exception.CommandException;
import by.wtj.filmrate.controller.JspPageName;
import by.wtj.filmrate.controller.RequestParameterName;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class ChangeLanguage implements Command {

    @Override
    public String execute(HttpServletRequest request) throws CommandException {
        int languageID = Integer.parseInt(request.getParameter(RequestParameterName.LANGUAGE));
        HttpSession session = request.getSession();
        session.setAttribute(SessionAttributes.CURRENT_LANG_ID, languageID);
        // TODO redirect to page that user was before
        session.setAttribute(RequestParameterName.commandName, CommandName.FillFilmsInUserPage.name());
        return JspPageName.controller;
    }

    @Override
    public boolean isRedirect() {
        return true;
    }
}
