package by.wtj.filmrate.command.impl;

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
        NewUser newUser = new NewUser();
        newUser.setMail(request.getParameter(RequestParameterName.USER_MAIL));
        newUser.setUserName(request.getParameter(RequestParameterName.USER_NAME));
        newUser.setNewPassword(request.getParameter(RequestParameterName.USER_PASSWORD));
        if(isValidUserData(newUser)){
            UserDAO userDAO = DAOFactory.getInstance().getUserDAO();
            try{
                userDAO.registration(newUser);
            } catch (DAOException ex){
                throw new CommandException(ex);
            }
        }else{
            throw new CommandException("Wrong user data");
        }
        return JspPageName.authorizationPage;
    }

    private boolean isValidUserData(NewUser newUser){
        return newUser.getNewPassword() != null && newUser.getUserName() != null && newUser.getMail() != null;
    }
}
