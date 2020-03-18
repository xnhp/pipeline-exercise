package org.bm;

/**
 * @author Benjamin Moser.
 */
public class InvalidArgumentException extends Exception {

    String message;

    public InvalidArgumentException(String[] strings) {
        this.message = strings[0];
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
