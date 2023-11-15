package by.wtj.filmrate.command.impl;

import by.wtj.filmrate.bean.Access;
import by.wtj.filmrate.bean.Language;
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
import java.util.List;

public class Authorization implements Command {
    @Override
    public boolean isRedirect(){return true;}
    @Override
    public String execute(HttpServletRequest request) throws CommandException {
        UserDAO userDAO = getUserDAO();
        UserCredentials credentials = setUserCredentials(request);
        if(isValidUserCredentials(credentials)){
            HttpSession session = request.getSession();
            retrieveUserIdFromDAOAndSaveInSession(userDAO, credentials, session);
            trySetAllLanguagesInSession(session);
            session.setAttribute(RequestParameterName.commandName, CommandName.FillFilmsInUserPage.name());
            return JspPageName.controller;
        }else
            throw new CommandException("Invalid credentials.");
    }

    private UserDAO getUserDAO() throws CommandException {
        DAOFactory factory;
        try{
            factory = DAOFactory.getInstance();
        }catch (DAOException e){
            throw new CommandException(e);
        }
        return factory.getUserDAO(Access.App);
    }

    private UserCredentials setUserCredentials(HttpServletRequest request){
        UserCredentials userCredentials = new UserCredentials();
        userCredentials.setName(request.getParameter(RequestParameterName.USER_NAME));
        userCredentials.setPasswordHash(request.getParameter(RequestParameterName.USER_PASSWORD));
        return userCredentials;
    }
    private void retrieveUserIdFromDAOAndSaveInSession(UserDAO userDAO, UserCredentials credentials,
       HttpSession session) throws CommandException {
        int uid;
        try{
            uid = userDAO.getUserId(credentials);
        } catch (DAOException ex){
            throw new CommandException(ex);
        }
        session.setAttribute(SessionAttributes.USER_ID, uid);
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

    private boolean isValidUserCredentials(UserCredentials credentials){
        //TODO for testing
        if(credentials.getName().isEmpty() && credentials.getPasswordHash().isEmpty()){
            credentials.setName("egor");
            credentials.setPasswordHash("1212");
        }
        return true;
    }
}
