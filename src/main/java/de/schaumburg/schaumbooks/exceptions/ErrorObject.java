package de.schaumburg.schaumbooks.exceptions;

import java.util.Date;


public record ErrorObject(Integer statusCode, String message, Date timestamp) {
}

