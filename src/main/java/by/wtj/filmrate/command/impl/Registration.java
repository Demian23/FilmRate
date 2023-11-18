package by.wtj.filmrate.command.impl;

import by.wtj.filmrate.bean.Access;
import by.wtj.filmrate.bean.NewUser;
import by.wtj.filmrate.command.Command;
import by.wtj.filmrate.command.exception.CommandException;
import by.wtj.filmrate.controller.JspPageName;
import by.wtj.filmrate.controller.RequestParameterName;
import by.wtj.filmrate.dao.DAOFactory;
import by.wtj.filmrate.dao.UserDAO;
import by.wtj.filmrate.dao.exception.DAOException;
import javax.servlet.http.HttpServletRequest;

public class Registration implements Command {
    @Override
    public boolean isRedirect(){return true;}
    @Override
    public String execute(HttpServletRequest request) throws CommandException {
        NewUser newUser = fillNewUser(request);
        UserDAO userDAO = CommandsComplementary.getUserDAOForAccess(Access.App);
            try{
                userDAO.registration(newUser);
            } catch (DAOException ex){
                throw new CommandException(ex);
            }
        return JspPageName.authorizationPage;
    }

    private NewUser fillNewUser(HttpServletRequest request) throws CommandException {
        NewUser newUser = new NewUser(request.getParameter(RequestParameterName.USER_NAME),
                request.getParameter(RequestParameterName.USER_PASSWORD), request.getParameter(RequestParameterName.USER_MAIL));
        if(isValidUserData(newUser))
            return newUser;
        else{
            CommandException commandException = new CommandException();
            commandException.setMsgForUser("Wrong data");
            throw commandException;
        }
    }

    private boolean isValidUserData(NewUser newUser){
        return newUser.getNewPassword() != null && newUser.getUserName() != null && newUser.getMail() != null;
    }
}
