package fr.olympicinsa.riocognized.facedetector;

import com.googlecode.javacv.cpp.opencv_core.IplImage;
import fr.olympicinsa.riocognized.facedetector.csv.FaceDBReader;
import fr.olympicinsa.riocognized.facedetector.recognition.RioRecognizer;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;

public class Riocognized {

    public static Logger log = Logger.getLogger(Riocognized.class);
    public static String IMAGE_TO_RECOGNIZE = "/opt/openCV/usain.jpg";

    public static void main(String[] args) {

        DOMConfigurator.configure(Thread.currentThread().getContextClassLoader().getResource("log4j.xml"));

        log.info("Demarrage de Riocognized");
        DateFormat dateFormat = new SimpleDateFormat("hhmmss-dd-MM-yy");
        Date date = new Date();
        String dateString = dateFormat.format(date);
        Mat image;
        String athletePath;
        FaceDBReader faceDB;

        String path = "/opt/openCV/test.yml";
        //Load faceDB
        faceDB = new FaceDBReader("/opt/openCV/athleteDB/faces.csv");
        //Create  Recognizor
        log.info("Create Recognizer");
        RioRecognizer recognizor = new RioRecognizer(faceDB, path);
        //Store faces
        log.info("Read CSV and load images");
        recognizor.init();
        log.info("Train Recognizer");
        recognizor.train();
        log.info("Save Recognizer");
        recognizor.save();

        log.info("Initialize Riocognized FaceDetector");
        String imageParam = (args.length > 0) ? args[0] : IMAGE_TO_RECOGNIZE;

        try {
            File f = new File(imageParam);
            if (!f.exists()) {
                throw new IOException();
            }
            athletePath = imageParam;
        } catch (IOException e) {
            log.error("File not found");
            athletePath = Riocognized.class.getResource("/image.jpg").getPath();
        }
        log.info("Utilisation du fichier : " + athletePath);
        log.info("Start FaceDetector");
        try {
            FaceDetector faceDetector = new FaceDetector();
            image = Highgui.imread(athletePath);
            try {
                //crop face
                Mat crop = faceDetector.cropFaceToMat(image);
                log.info("Detected " + faceDetector.getFacesDetected() + " athletes !");
                if (faceDetector.getFacesDetected() > 0) {
                    Highgui.imwrite("face_croped.jpg", crop);
                    IplImage face = ImageConvertor.matToIplImage(crop);
                    for (int i = 0; i < 5; i++) {
                        int athlete = recognizor.predictedLabel(face);
                        System.out.println("\nAthlete recognized : " + athlete + "\n");
                        recognizor.changeRecognizer(i);
                    }
                }
            } catch (Exception e) {
                log.error("Error processiong detection");
            }

        } catch (Exception e) {
            log.error("Error reading image");
            e.printStackTrace();
        }

    }

}
