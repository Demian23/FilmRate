package by.wtj.filmrate.command.impl;

import by.wtj.filmrate.bean.UserCredentials;
import by.wtj.filmrate.command.Command;
import by.wtj.filmrate.command.CommandName;
import by.wtj.filmrate.command.SessionAttributes;
import by.wtj.filmrate.command.exception.CommandException;
import by.wtj.filmrate.controller.JspPageName;
import by.wtj.filmrate.controller.RequestParameterName;
import by.wtj.filmrate.dao.DAOFactory;
import by.wtj.filmrate.dao.UserDAO;
import by.wtj.filmrate.dao.exception.DAOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class Authorization implements Command {
    @Override
    public boolean isRedirect(){return true;}
    @Override
    public String execute(HttpServletRequest request) throws CommandException {
        UserCredentials userCredentials = new UserCredentials();
        userCredentials.setName(request.getParameter(RequestParameterName.USER_NAME));
        userCredentials.setPasswordHash(request.getParameter(RequestParameterName.USER_PASSWORD));
        if(isValidUserCredentials(userCredentials)){
            UserDAO userDAO = DAOFactory.getInstance().getUserDAO();
            int uid;
            try{
                uid = userDAO.getUserId(userCredentials);
            } catch (DAOException ex){
                throw new CommandException(ex);
            }
            HttpSession session = request.getSession();
            session.setAttribute(SessionAttributes.USER_ID, uid);
            session.setAttribute(SessionAttributes.CURRENT_LANG, "en");
            // TODO get id from db here
            session.setAttribute(SessionAttributes.CURRENT_LANG_ID, 1);
            session.setAttribute(RequestParameterName.commandName, CommandName.FillFilmsInUserPage.name());
            return JspPageName.controller;
        }else
            throw new CommandException("Invalid credentials.");
    }

    private boolean isValidUserCredentials(UserCredentials credentials){
        return !credentials.getName().isEmpty() && !credentials.getPasswordHash().isEmpty();
    }
}
