package by.wtj.filmrate.command;

import by.wtj.filmrate.command.exception.CommandException;
import javax.servlet.http.HttpServletRequest;

public interface Command {
    /**
     * @param request http request received from main servlet - controller
     * @return destination of response
     * @throws CommandException exception for command layer
     */
    String execute(HttpServletRequest request) throws CommandException;

    /**
     * @return true if redirect to result of execute should be performed
     * another way is to forward
     */
    boolean isRedirect();
}
