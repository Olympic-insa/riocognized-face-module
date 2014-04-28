/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.olympicinsa.riocognized.facedetector.exception;

/**
 *
 * @author alex
 */
public class RiocognizedException extends Exception {

    /**
     * Creates a new instance of RiocognizedException
     */
    public RiocognizedException() {
    }

    /**
     * Creates a new instance of RiocognizedException with exception cause.
     *
     * @param e Throwable exception that cause this exception.
     */
    public RiocognizedException(Throwable e) {
        super(e);
    }

    /**
     * Creates a new instance of RiocognizedException with detail message.
     *
     * @param msg String of the detail message.
     */
    public RiocognizedException(String msg) {
        super(msg);
    }

    /**
     * Constructs an instance of RiocognizedException with the specified detail
     * message and Exception cause.
     *
     * @param msg String the detail message.
     * @param e Throwable original exception.
     */
    public RiocognizedException(String msg, Throwable e) {
        super(msg, e);
    }
}
