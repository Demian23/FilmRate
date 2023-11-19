package by.wtj.filmrate.command.impl;

import by.wtj.filmrate.bean.*;
import by.wtj.filmrate.command.Command;
import by.wtj.filmrate.command.SessionAttributes;
import by.wtj.filmrate.command.exception.CommandException;
import by.wtj.filmrate.controller.JspPageName;
import by.wtj.filmrate.controller.RequestParameterName;
import by.wtj.filmrate.dao.UserDAO;
import by.wtj.filmrate.dao.exception.DAOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Optional;

public class BanUser implements Command {
    @Override
    public String execute(HttpServletRequest request) throws CommandException {
        HttpSession session = request.getSession();
        Access access = CommandsComplementary.getCurrentAccess(session);
        int chosenUserId = Integer.parseInt(request.getParameter(RequestParameterName.USER_ID));
        UserDAO userDAO = CommandsComplementary.getUserDAOForAccess(access);
        Optional<UserWithBan> chosenUser = CommandsComplementary.getChosenUser(chosenUserId, session);
        if(chosenUser.isPresent()){
            Admin currentAdmin = (Admin) session.getAttribute(SessionAttributes.CURRENT_USER);
            banUser(currentAdmin, chosenUser.get(), userDAO);
        }else{
            CommandException commandException = new CommandException();
            commandException.setMsgForUser("No user with id: " + chosenUserId);
            throw commandException;
        }
        return JspPageName.adminUsers;
    }


    private void banUser(Admin admin, UserWithBan user, UserDAO userDAO) throws CommandException {
        try {
            BannedUser bannedUser = userDAO.banUser(user.getUser().getUserId(), admin.getAdminId());
            bannedUser.setAdminBannedName(admin.getUser().getName());
            user.setBannedInfo(bannedUser);
            user.setBan(true);
        }catch(DAOException e){
            throw new CommandException(e);
        }
    }


    @Override
    public boolean isRedirect() {
        return false;
    }
}
