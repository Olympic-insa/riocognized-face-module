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
public class FaceDBException extends RiocognizedException {

    /**
     * Creates a new instance of FaceDBException
     */
    public FaceDBException() {
    }

    /**
     * Creates a new instance of FaceDBException with exception cause.
     *
     * @param e Throwable exception that cause this exception.
     */
    public FaceDBException(Throwable e) {
        super(e);
    }

    /**
     * Creates a new instance of FaceDBException with detail message.
     *
     * @param msg String of the detail message.
     */
    public FaceDBException(String msg) {
        super(msg);
    }

    /**
     * Constructs an instance of FaceDBException with the specified detail
     * message and Exception cause.
     *
     * @param msg String the detail message.
     * @param e Throwable original exception.
     */
    public FaceDBException(String msg, Throwable e) {
        super(msg, e);
    }
}
