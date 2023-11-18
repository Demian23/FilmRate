package by.wtj.filmrate.command.impl;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DateTimeValidatorTest {

    @Test
    void isValidTestForEmptyPattern(){
        DateTimeValidator validator = new DateTimeValidator("");

        boolean result = validator.isValid("121");
        assertFalse(result);

        result = validator.isValid("");
        assertTrue(result);
    }

    @Test
    void isValidTestForDatePattern(){
        DateTimeValidator validator = new DateTimeValidator("uuuu-MM-dd");

        boolean result = validator.isValid("2005-11-12");
        assertTrue(result);

        result = validator.isValid("Gloria");
        assertFalse(result);
    }
    @Test
    void isValidTestForTimePattern(){
        DateTimeValidator validator = new DateTimeValidator("HH:mm:ss");

        boolean result = validator.isValid("12:11:30");
        assertTrue(result);

        result = validator.isValid("Zombie");
        assertFalse(result);
    }

}