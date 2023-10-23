package by.wtj.filmrate.controller;

import by.wtj.filmrate.command.CommandExecutor;
import by.wtj.filmrate.command.Command;
import by.wtj.filmrate.command.exception.CommandException;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class Controller extends HttpServlet {
    private static final long serialVersionUID = 1L;
    public Controller(){super();}

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doGet(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String commandName = request.getParameter(RequestParameterName.commandName);
        Command command = CommandExecutor.getInstance().getCommand(commandName);
        String page = null;
        try{
            page = command.execute(request);
        } catch (CommandException exception){
            //TODO log
            page = JspPageName.errorPage;
        } catch (Exception exception){
           //TODO log with another priority
            page = JspPageName.errorPage;
        }
        RequestDispatcher dispatcher = request.getRequestDispatcher(page);
        if(dispatcher != null){
            dispatcher.forward(request, response);
        }else{
            errorMessageDirectlyFromResponse(response);
        }
    }

    private void errorMessageDirectlyFromResponse(HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        response.getWriter().println("Fatal error");
    }
}
