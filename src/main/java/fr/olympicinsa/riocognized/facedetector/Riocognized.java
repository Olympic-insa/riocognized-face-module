package fr.olympicinsa.riocognized.facedetector;

import static fr.olympicinsa.riocognized.facedetector.ImageConvertor.MatTobyteArray;
import static fr.olympicinsa.riocognized.facedetector.ImageConvertor.matToBufferedImage;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.imageio.ImageIO;
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
            BufferedImage bimage = matToBufferedImage(image);
            File outputfile = new File("/opt/openCV/image_matToBuff.jpg");
            ImageIO.write(bimage, "jpg", outputfile);
            System.out.println("Mat to Buffered Image works !");
            
            byte[] Bimage = MatTobyteArray(image);
            ByteArrayInputStream bis = new ByteArrayInputStream(Bimage);
            BufferedImage imageFull = ImageIO.read(bis);
            outputfile = new File("/opt/openCV/image_matToByte.jpg");
            ImageIO.write(imageFull, "jpg", outputfile);
            System.out.println("Mat to Byte works !");

            String output = "/opt/open CV/athletes_detected_" + dateString + ".jpg";
            System.out.println("Result will be written in : " + output + " ....");

            try {
                int detected = faceDetector.detectFaces(athletePath, output);
                System.out.println("Detected " + detected + " athletes !");
                // Highgui.imwrite("/opt/openCV/face_" + dateString + ".png", Matimage);

            } catch (Exception e) {
                System.out.println("Error processiong detection");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
