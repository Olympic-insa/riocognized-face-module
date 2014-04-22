package fr.olympicinsa.riocognized.facedetector;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;

public class Riocognized {

    public static void main(String[] args) {
        DateFormat dateFormat = new SimpleDateFormat("hhmmss-dd-MM-yy");
        Date date = new Date();
        String dateString = dateFormat.format(date);
        Mat image;
        String athletePath;

        //System.load("/opt/openCV/libopencv_java248.so");
        //System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        System.out.println("\nRunning Riocognized FaceDetector");
        String imageParam = (args.length > 0) ? args[0] : "/opt/openCV/image.jpg";

        try {
            athletePath = Riocognized.class.getResource(imageParam).getPath();
        } catch (Exception e) {
            System.err.println("Couldn't load your athlete picture, use London Olympic's Triathlon podium instead ;-)");
            athletePath = "/opt/openCV/image.jpg";
        }

        try {
            FaceDetector faceDetector = new FaceDetector();
            image = Highgui.imread(athletePath);

            String output = "/opt/openCV/athletes_detected_" + dateString + ".png";
            System.out.println("Result will be written in : " + output + " ....");

            try {
                int detected = faceDetector.detectFaces(athletePath, output);
                System.out.println("Detected " + detected + " athletes !");
                Mat crop = faceDetector.cropFaceToMat(image);
                Highgui.imwrite("/opt/openCV/face_" + dateString + ".png", crop);
            } catch (Exception e) {
                System.out.println("Error processiong detection");
            }
        } catch (Exception e) {
            System.err.println("Couldn't read your picture)");
        }
    }
}
