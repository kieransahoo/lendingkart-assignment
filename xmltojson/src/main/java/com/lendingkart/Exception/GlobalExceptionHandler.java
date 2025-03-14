package com.lendingkart.Exception;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.logging.Level;
import java.util.logging.Logger;

public class GlobalExceptionHandler {

    private static final Logger LOGGER = Logger.getLogger(GlobalExceptionHandler.class.getName());

    @ExceptionHandler(XmlToJsonConversionException.class)
    public ResponseEntity<String> handleXmlToJsonConversionException(XmlToJsonConversionException ex) {
        LOGGER.log(Level.SEVERE, "XML to JSON conversion error: " + ex.getMessage(), ex);

        JSONObject errorResponse = new JSONObject();
        errorResponse.put("error", "XML to JSON conversion failed");
        errorResponse.put("message", ex.getMessage());

        return new ResponseEntity<>(errorResponse.toString(2), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGlobalException(Exception ex) {
        LOGGER.log(Level.SEVERE, "Global exception: " + ex.getMessage(), ex);

        JSONObject errorResponse = new JSONObject();
        errorResponse.put("error", "Internal server error");
        errorResponse.put("message", "An unexpected error occurred.");

        return new ResponseEntity<>(errorResponse.toString(2), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
