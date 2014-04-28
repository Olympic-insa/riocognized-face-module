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
public class ImageConvertionException extends RiocognizedException {

    /**
     * Creates a new instance of ImageConvertionException
     */
    public ImageConvertionException() {
    }

    /**
     * Creates a new instance of ImageConvertionException with exception cause.
     *
     * @param e Throwable exception that cause this exception.
     */
    public ImageConvertionException(Throwable e) {
        super(e);
    }

    /**
     * Creates a new instance of ImageConvertionException with detail message.
     *
     * @param msg String of the detail message.
     */
    public ImageConvertionException(String msg) {
        super(msg);
    }

    /**
     * Constructs an instance of ImageConvertionException with the specified detail
     * message and Exception cause.
     *
     * @param msg String the detail message.
     * @param e Throwable original exception.
     */
    public ImageConvertionException(String msg, Throwable e) {
        super(msg, e);
    }
}
