package com.lendingkart.Exception;

public class XmlToJsonConversionException extends Exception {
    private static final long serialVersionUID = 1L;

    public XmlToJsonConversionException(String message) {
        super(message);
    }

    public XmlToJsonConversionException(String message, Throwable cause) {
        super(message, cause);
    }
}
