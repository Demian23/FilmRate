package by.wtj.filmrate.command;

import by.wtj.filmrate.command.exception.CommandException;
import jakarta.servlet.http.HttpServletRequest;

public interface Command {
    public String execute(HttpServletRequest request) throws CommandException;
}
