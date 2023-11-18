package by.wtj.filmrate.command.impl;

import by.wtj.filmrate.bean.Access;
import by.wtj.filmrate.bean.Admin;
import by.wtj.filmrate.bean.UserWithBan;
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

public class TakeOffBan implements Command {
    @Override
    public String execute(HttpServletRequest request) throws CommandException {
        HttpSession session = request.getSession();
        UserDAO userDAO = CommandsComplementary.getUserDAOForAccess(CommandsComplementary.getCurrentAccess(session));
        int chosenUserId = Integer.parseInt(request.getParameter(RequestParameterName.USER_ID));
        Optional<UserWithBan> chosenUser = CommandsComplementary.getChosenUser(chosenUserId, session);
        if(chosenUser.isPresent()){
            takeOffUserBan(chosenUser.get(), userDAO);
        }else{
            CommandException commandException = new CommandException();
            commandException.setMsgForUser("No user with id: " + chosenUserId);
            throw commandException;
        }
        return JspPageName.adminUsers;
    }

    private void takeOffUserBan(UserWithBan userWithBan, UserDAO userDAO) throws CommandException {
        if(userWithBan.isBan()){
            boolean isTakeOff = false;
            try{
                isTakeOff = userDAO.takeOffBan(userWithBan.getUser().getUserId());
            }catch(DAOException e){
                throw new CommandException(e);
            }
            if(isTakeOff){
                userWithBan.setBan(false);
                userWithBan.setBannedInfo(null);
            }else{
                CommandException commandException = new CommandException();
                commandException.setMsgForUser("Ban can't be taken off for: " + userWithBan.getUser().getName());
                throw commandException;
            }

        }
    }

    @Override
    public boolean isRedirect() {
        return false;
    }
}
