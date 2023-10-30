package by.wtj.filmrate.controller;

import by.wtj.filmrate.command.CommandExecutor;
import by.wtj.filmrate.command.Command;
import by.wtj.filmrate.command.exception.CommandException;
import by.wtj.filmrate.command.impl.NoSuchCommand;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;

public class Controller extends HttpServlet {
    private static final long serialVersionUID = 1L;
    public Controller(){super();}

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
        String page = null;
        try{
            page = command.execute(request);
        } catch (CommandException exception){
            System.out.println(exception.getMessage());
            request.setAttribute(RequestParameterName.ERROR_MSG, exception.getMessage());
            //TODO log
            page = JspPageName.errorPage;
            isRedirect = false;
        } catch (Exception exception){
           //TODO log with another priority
            System.out.println(exception.getMessage());
            page = JspPageName.errorPage;
            isRedirect = false;
            request.setAttribute(RequestParameterName.ERROR_MSG, exception.getMessage());
        }
        if(isRedirect){
            response.sendRedirect(request.getContextPath() + "/" + page);
        }else{
            RequestDispatcher dispatcher = request.getRequestDispatcher(page);
            dispatcher.forward(request, response);
        }
    }

    private void errorMessageDirectlyFromResponse(HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        response.getWriter().println("Fatal error");
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
