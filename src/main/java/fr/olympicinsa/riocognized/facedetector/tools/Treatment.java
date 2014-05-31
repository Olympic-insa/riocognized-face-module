/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.olympicinsa.riocognized.facedetector.tools;

import static fr.olympicinsa.riocognized.facedetector.recognition.RioRecognizer.log;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import org.apache.log4j.Logger;
import static org.bytedeco.javacpp.opencv_core.IPL_DEPTH_32F;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.Size;
import static org.bytedeco.javacpp.opencv_highgui.*;
import org.bytedeco.javacpp.opencv_imgproc;
import static org.bytedeco.javacpp.opencv_imgproc.COLOR_BGR2GRAY;
import static org.bytedeco.javacpp.opencv_imgproc.CV_INTER_NN;
import static org.bytedeco.javacpp.opencv_imgproc.INTER_CUBIC;
import static org.bytedeco.javacpp.opencv_imgproc.cvtColor;
import static org.bytedeco.javacpp.opencv_imgproc.cvtColor;
import static org.bytedeco.javacpp.opencv_imgproc.cvtColor;
import static org.bytedeco.javacpp.opencv_imgproc.cvtColor;
import static org.bytedeco.javacpp.opencv_imgproc.cvtColor;
import static org.bytedeco.javacpp.opencv_imgproc.equalizeHist;
import static org.bytedeco.javacpp.opencv_imgproc.resize;
/**
 *
 * @author alex
 */
public class Treatment {

    public static Logger log = Logger.getLogger(Treatment.class);

    /**
     * Images should be resized and grayscaled before eigenfaces. Then, egalized
     * its histogram
     *
     * @param image Mat of image to treat
     * @return Mat of grayscale equalized image
     */
    public static Mat beforeDetection(Mat image) {
        Mat gray = new Mat();
        cvtColor(image, gray, COLOR_BGR2GRAY);
        equalizeHist(gray, gray);
        return gray;
    }

    public static Mat resize(Mat image, int maxW) {
        log.info("Image size: " + image.size().width() + "," + image.size().height());
        double f = (double) image.size().width() / (double) maxW;
        log.info("Resize factor : " + f);
        int w = (int) (image.size().width() / f);
        int h = (int) (image.size().height() / f);
        Size sz = new Size(w, h);
        Mat resizeImage = new Mat(sz);
        log.info("Resizing image to : " + w + "," + h);
        opencv_imgproc.resize(image, resizeImage, sz);
        return resizeImage;
    }

    /**
     * Images should be resized and grayscaled before eigenfaces. Then, egalized
     * its histogram
     *
     * @param image IplImage of byte image to fit for ACP
     * @param x int of new image width
     * @param y int of new image height
     * @return IplImage of grayscale fitted image
     */
    public static Mat beforeRecognition(Mat image, int x, int y) {

        // Create empty image
        final Mat resized = new Mat(new Size(x, y), IPL_DEPTH_32F);
        final Mat gray = new Mat(new Size(x, y), IPL_DEPTH_32F);

        log.debug("Convert to Grayscale");
        log.debug("Resizing image");

        opencv_imgproc.resize(gray, resized, new Size(x, y));
        //log.debug("Egalisation");
        resized.

        // Write gray scale resized image
         BufferedImage write = resized.getBufferedImage();
         try {
         ImageIO.write(write, "jpg", new File("/debugIpl.jpg"));
         } catch (IOException e) {
         log.error("Can't write ");
         e.printStackTrace();
         }
         //
        return resized;
    }

}
