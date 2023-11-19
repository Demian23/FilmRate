package by.wtj.filmrate.controller;

import by.wtj.filmrate.command.CommandExecutor;
import by.wtj.filmrate.command.Command;
import by.wtj.filmrate.exception.FilmRateException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;

import org.apache.log4j.Logger;

public class Controller extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final static Logger logger = Logger.getLogger(Controller.class);
    public Controller(){
        super();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String commandName = request.getParameter(RequestParameterName.commandName);
        if(commandName == null){
            commandName = tryGetCommandFromSession(request);
            //TODO clean session somewhere from command
        }
        Command command = CommandExecutor.getInstance().getCommand(commandName);
        boolean isRedirect = command.isRedirect();
        String page;
        try{
            page = command.execute(request);
        }catch(FilmRateException e){
            request.setAttribute(RequestParameterName.ERROR_MSG, e.getMsgForUser());
            if(e.getLogMsg() != null && !e.getLogMsg().isEmpty()){
                logger.error(e.getLogMsg() + "cause: " + e.getCauseModule());
            }
            if(e.getMsgForUser() != null && !e.getMsgForUser().isEmpty())
                logger.info(e.getMsgForUser());
            else
                logger.error(e.getMessage(), e);
            page = JspPageName.errorPage;
            isRedirect = false;
        } catch (Exception e){
            logger.error(e.getMessage(), e);
            page = JspPageName.errorPage;
            isRedirect = false;
        }
        if(isRedirect){
            response.sendRedirect(request.getContextPath() + "/" + page);
        }else{
            RequestDispatcher dispatcher = request.getRequestDispatcher(page);
            dispatcher.forward(request, response);
        }
    }

    private String tryGetCommandFromSession(HttpServletRequest request){
        HttpSession session = request.getSession();
        String result = null;
        if(session != null){
            result = session.getAttribute(RequestParameterName.commandName).toString();
            if(result != null){
                session.removeAttribute(RequestParameterName.commandName);
            }
        }
        return result;
    }
}
