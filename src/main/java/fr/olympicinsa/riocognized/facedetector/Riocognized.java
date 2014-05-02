package fr.olympicinsa.riocognized.facedetector;

import fr.olympicinsa.riocognized.facedetector.detection.FaceDetector;
import fr.olympicinsa.riocognized.facedetector.tools.ImageConvertor;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import fr.olympicinsa.riocognized.facedetector.db.FaceDBReader;
import fr.olympicinsa.riocognized.facedetector.recognition.RioRecognizer;
import java.io.File;
import java.io.IOException;
import static java.lang.System.exit;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;

public class Riocognized {

    public static Logger log = Logger.getLogger(Riocognized.class);
    public static String IMAGE_TO_RECOGNIZE = "/opt/openCV/teddy.jpg";
    public static String DIR_TO_RECOGNIZE = "/opt/openCV";

    public static void main(String[] args) {

        DOMConfigurator.configure(Thread.currentThread().getContextClassLoader().getResource("log4j.xml"));

        log.info("Demarrage de Riocognized");
        DateFormat dateFormat = new SimpleDateFormat("hhmmss-dd-MM-yy");
        Date date = new Date();
        String dateString = dateFormat.format(date);

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

        File repertoire = new File(DIR_TO_RECOGNIZE);
        String fileOutput = null;
        if (repertoire.isDirectory()) {
            File[] list = repertoire.listFiles();
            for (File file : list) {
                if (file.getName().matches("(.*)jpg")) {
                    fileOutput = file.getAbsolutePath();
                    String fileName = FilenameUtils.removeExtension(file.getName());
                    recognizeTest(fileOutput, fileName, recognizor);
                }
            }
        }
        String imageParam = (args.length > 0) ? args[0] : IMAGE_TO_RECOGNIZE;
        File f = new File(imageParam);
        try {
            if (!f.exists()) {
                throw new IOException();
            }
            athletePath = imageParam;
            
        } catch (IOException e) {
            log.error("File not found");
            exit(0);
            athletePath = Riocognized.class.getResource("/image.jpg").getPath();
        }
        recognizeTest(imageParam, FilenameUtils.removeExtension(f.getName()),recognizor);
    }

    public static void recognizeTest(String fileOutput, String filename, RioRecognizer recognizor) {
        Mat image;
        log.info(
            "Utilisation du fichier : " + fileOutput);
        log.info(
            "Start FaceDetector");
        try {
            FaceDetector faceDetector = new FaceDetector();
            image = Highgui.imread(fileOutput);
            try {
                //crop face
                Mat crop = faceDetector.cropFaceToMat(image);
                log.info("Detected " + faceDetector.getFacesDetected() + " athletes !");
                if (faceDetector.getFacesDetected() > 0) {
                    Highgui.imwrite(filename + "_croped.jpg", crop);
                    IplImage face = ImageConvertor.matToIplImage(crop);
                    for (int i = 0; i < 5; i++) {
                        int athlete = recognizor.predictedLabel(face);
                        System.out.println("\nAthlete recognized : " + athlete + "\n");
                        recognizor.changeRecognizer(i);
                    }
                }
            } catch (Exception e) {
                log.error("Error processiong detection");
                e.printStackTrace();
            }

        } catch (Exception e) {
            log.error("Error reading image");
            e.printStackTrace();
        }

    }
}
