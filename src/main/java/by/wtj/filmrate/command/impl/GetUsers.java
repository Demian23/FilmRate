package by.wtj.filmrate.command.impl;

import by.wtj.filmrate.bean.UserWithBan;
import by.wtj.filmrate.command.Command;
import by.wtj.filmrate.command.SessionAttributes;
import by.wtj.filmrate.command.exception.CommandException;
import by.wtj.filmrate.controller.JspPageName;
import by.wtj.filmrate.dao.UserDAO;
import by.wtj.filmrate.dao.exception.DAOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

public class GetUsers implements Command {

    @Override
    public String execute(HttpServletRequest request) throws CommandException {
        HttpSession session = request.getSession();
        UserDAO userDAO = CommandsComplementary.getUserDAOForAccess(CommandsComplementary.getCurrentAccess(session));
        try{
            List<UserWithBan> allUsers = userDAO.getAllUsers();
            if(!allUsers.isEmpty())
                session.setAttribute(SessionAttributes.USERS, allUsers);
        } catch (DAOException ex){
            throw new CommandException(ex);
        }
        return JspPageName.adminUsers;
    }

    @Override
    public boolean isRedirect() {
        return true;
    }
}
