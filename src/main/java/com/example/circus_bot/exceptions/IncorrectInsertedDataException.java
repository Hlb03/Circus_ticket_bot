package com.example.circus_bot.exceptions;

/**
 * Informs that user types ticket information in a wrong format
 */
public class IncorrectInsertedDataException extends Exception{
    public IncorrectInsertedDataException(String message) {
        super(message);
    }
}
