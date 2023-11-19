package by.wtj.filmrate.command.impl;

import by.wtj.filmrate.bean.*;
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
import java.util.List;
import java.util.Optional;

public class Authorization implements Command {
    boolean redirect;
    @Override
    public boolean isRedirect(){return redirect;}
    @Override
    public String execute(HttpServletRequest request) throws CommandException {
        UserCredentials credentials = setUserCredentials(request);
        checkUserCredentials(credentials);
        HttpSession session = request.getSession();
        UserWithBan user = retrieveUser(credentials);
        if(!user.isBan()){
            redirect = true;
            trySetAllLanguagesInSession(session);
            setUserAccessInSession(user.getUser(), session);
            session.setAttribute(RequestParameterName.commandName, CommandName.FillFilmsInUserPage.name());
            return JspPageName.controller;
        }else{
            redirect = false;
            request.setAttribute(RequestParameterName.BANNED, user.getBannedInfo());
            return JspPageName.bannedUserPage;
        }
    }

    private UserCredentials setUserCredentials(HttpServletRequest request){
        UserCredentials userCredentials = new UserCredentials();
        userCredentials.setName(request.getParameter(RequestParameterName.USER_NAME));
        userCredentials.setPasswordHash(request.getParameter(RequestParameterName.USER_PASSWORD));
        return userCredentials;
    }

    private void setUserAccessInSession(User user, HttpSession session) throws CommandException {
        Optional<Admin> admin;
        try{
            admin = CommandsComplementary.getFactory().getAdminDAO().getAdmin(user);
        } catch (DAOException ex){
            throw new CommandException(ex);
        }
        if(admin.isPresent()){
            session.setAttribute(SessionAttributes.CURRENT_ACCESS, Access.Admin);
            session.setAttribute(SessionAttributes.CURRENT_USER, admin.get());
            session.setAttribute(SessionAttributes.CURRENT_USER_NAME, admin.get().getUser().getName());
        }else{
            session.setAttribute(SessionAttributes.CURRENT_ACCESS, Access.User);
            session.setAttribute(SessionAttributes.CURRENT_USER, user);
            session.setAttribute(SessionAttributes.CURRENT_USER_NAME, user.getName());
        }
    }

    private UserWithBan retrieveUser(UserCredentials credentials) throws CommandException {
        UserDAO userDAO = CommandsComplementary.getUserDAOForAccess(Access.App);
        Optional<UserWithBan> user;
        try{
            user = userDAO.getUser(credentials);
        } catch (DAOException ex){
            throw new CommandException(ex);
        }
        if(user.isPresent()) {
            return user.get();
        } else{
            CommandException commandException = new CommandException();
            commandException.setMsgForUser("No such user");
            throw commandException;
        }
    }

    private void trySetAllLanguagesInSession(HttpSession session) throws CommandException {
        Object languages = session.getAttribute(SessionAttributes.LANGUAGES);
        if(languages == null){
            List<Language> availableLanguages;
            try {
                availableLanguages = DAOFactory.getInstance().getTranslationDAO(Access.App).getAllLanguages();
            }catch(DAOException e){
                throw new CommandException(e);
            }
            session.setAttribute(SessionAttributes.LANGUAGES, availableLanguages);
            session.setAttribute(SessionAttributes.CURRENT_LANG_ID, availableLanguages.get(1).getId());
        }
    }

    private void checkUserCredentials(UserCredentials credentials) throws CommandException {
        CommandsComplementary.checkEmptyValue(credentials.getName(), "User name");
        CommandsComplementary.checkEmptyValue(credentials.getPasswordHash(), "User password");
    }
}
