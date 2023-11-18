package by.wtj.filmrate.command.impl;

import by.wtj.filmrate.command.exception.CommandException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CommandsComplementaryTest {

    @Test
    void isThrowOnEmptyValue(){
        assertThrows(CommandException.class, ()-> CommandsComplementary.checkEmptyValue("", "test")) ;
    }


}