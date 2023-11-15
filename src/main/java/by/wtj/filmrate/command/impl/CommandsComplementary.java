package by.wtj.filmrate.command.impl;

import by.wtj.filmrate.bean.Access;
import by.wtj.filmrate.bean.Admin;
import by.wtj.filmrate.bean.User;
import by.wtj.filmrate.command.SessionAttributes;

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
}
