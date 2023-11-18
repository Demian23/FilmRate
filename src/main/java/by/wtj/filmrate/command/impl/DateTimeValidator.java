package by.wtj.filmrate.command.impl;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateTimeValidator {
    private final DateTimeFormatter formatter;

    public DateTimeValidator(String format){
        formatter = DateTimeFormatter.ofPattern(format);
    }

    public boolean isValid(String value){
        try {
            formatter.parse(value);
        }catch(DateTimeParseException e){
            return false;
        }
        return true;
    }
}
