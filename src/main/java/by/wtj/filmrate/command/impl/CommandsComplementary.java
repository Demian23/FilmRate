package by.wtj.filmrate.command.impl;

import by.wtj.filmrate.bean.Access;
import by.wtj.filmrate.bean.Admin;
import by.wtj.filmrate.bean.User;
import by.wtj.filmrate.command.SessionAttributes;
import by.wtj.filmrate.command.exception.CommandException;
import by.wtj.filmrate.dao.CommentDAO;
import by.wtj.filmrate.dao.DAOFactory;
import by.wtj.filmrate.dao.FilmDAO;
import by.wtj.filmrate.dao.UserDAO;
import by.wtj.filmrate.dao.exception.DAOException;

import javax.servlet.http.HttpSession;

public class CommandsComplementary {
    static Access getCurrentAccess(HttpSession session){
        Object accessInSession = session.getAttribute(SessionAttributes.CURRENT_ACCESS);
        return accessInSession != null ? (Access) accessInSession : Access.App;
    }

    static int getCurrentUserId(HttpSession session){
        Access access = getCurrentAccess(session);
        if(access == Access.User){
            User user = (User)session.getAttribute(SessionAttributes.CURRENT_USER);
            return user.getUserId();
        }else{
            Admin admin = (Admin)session.getAttribute(SessionAttributes.CURRENT_USER);
            return admin.getUser().getUserId();
        }
    }

    static public DAOFactory getFactory() throws CommandException {
        try{
            return  DAOFactory.getInstance();
        }catch (DAOException e){
            throw new CommandException(e);
        }
    }
    static public UserDAO getUserDAOForAccess(Access access) throws CommandException {
        return getFactory().getUserDAO(access);
    }
    static public FilmDAO getFilmDAOForAccess(Access access) throws CommandException {
        return getFactory().getFilmDAO(access);
    }
    static public CommentDAO getCommentDAO() throws CommandException {
        return getFactory().getCommentDAO();
    }
}
