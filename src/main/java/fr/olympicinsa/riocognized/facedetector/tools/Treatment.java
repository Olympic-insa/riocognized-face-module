/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.olympicinsa.riocognized.facedetector.tools;

import com.googlecode.javacv.cpp.opencv_core;
import static com.googlecode.javacv.cpp.opencv_core.IPL_DEPTH_32F;
import static com.googlecode.javacv.cpp.opencv_core.IPL_DEPTH_8U;
import static com.googlecode.javacv.cpp.opencv_core.IPL_DEPTH_32S;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import static com.googlecode.javacv.cpp.opencv_core.cvConvertScale;
import static com.googlecode.javacv.cpp.opencv_core.cvCreateImage;
import static com.googlecode.javacv.cpp.opencv_core.cvSize;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_INTER_AREA;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_INTER_NN;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvEqualizeHist;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvResize;
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
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
import static org.opencv.imgproc.Imgproc.COLOR_BGR2GRAY;
import static org.opencv.imgproc.Imgproc.cvtColor;
import static org.opencv.imgproc.Imgproc.equalizeHist;

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
        Mat resizeImage = new Mat();
        log.info("Image size: " + image.width() + "," + image.height());
        double f = (double) image.width() / (double) maxW;
        log.info("Resize factor : " + f);
        int w = (int) (image.width() / f);
        int h = (int) (image.height() / f);
        Size sz = new Size(w, h);
        log.info("Resizing image to : " + w + "," + h);
        Imgproc.resize(image, resizeImage, sz);
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
    public static IplImage beforeRecognition(opencv_core.IplImage image, int x, int y) {

        // Create empty image
        final IplImage resized = cvCreateImage(cvSize(x, y), IPL_DEPTH_32F, 1);
        final IplImage gray = cvCreateImage(cvSize(image.width(), image.height()), IPL_DEPTH_32F, 1);

        log.debug("Convert to Grayscale");
        cvConvertScale(image, gray, 1. / 255, 0);
        log.debug("Resizing image");

        cvResize(gray, resized, CV_INTER_NN);
        //log.debug("Egalisation");
        //cvEqualizeHist(resized, resized);

        /* Write gray scale resized image
         BufferedImage write = resized.getBufferedImage();
         try {
         ImageIO.write(write, "jpg", new File("testIpl.jpg"));
         } catch (IOException e) {
         log.error("Can't write ");
         e.printStackTrace();
         }
         */
        return resized;
    }

    public static void showResult(Mat img) {
        Imgproc.resize(img, img, new Size(640, 480));
        MatOfByte matOfByte = new MatOfByte();
        Highgui.imencode(".jpg", img, matOfByte);
        byte[] byteArray = matOfByte.toArray();
        BufferedImage bufImage = null;
        try {
            InputStream in = new ByteArrayInputStream(byteArray);
            bufImage = ImageIO.read(in);
            JFrame frame = new JFrame();
            frame.getContentPane().add(new JLabel(new ImageIcon(bufImage)));
            frame.pack();
            frame.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
