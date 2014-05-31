package fr.olympicinsa.riocognized.facedetector;

import fr.olympicinsa.riocognized.facedetector.detection.FaceDetector;
import java.io.File;
import java.io.IOException;
import static java.lang.System.exit;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.bytedeco.javacpp.opencv_core.Mat;
import static org.bytedeco.javacpp.opencv_highgui.imread;
import static org.bytedeco.javacpp.opencv_highgui.imread;
import static org.bytedeco.javacpp.opencv_highgui.imread;
import static org.bytedeco.javacpp.opencv_highgui.imwrite;

public class FaceCropper {

    public static Logger log = Logger.getLogger(FaceCropper.class);
    public static String IMAGE_TO_RECOGNIZE = "/opt/openCV/lfwDB";
    public static String DIR_TO_RECOGNIZE = "/opt/openCV/testImage";
    public static int nbImage = 0;
    public static int nbFile = 0;
    public static int nbDetected = 0;
    
    public static void main(String[] args) {

        DOMConfigurator.configure(Thread.currentThread().getContextClassLoader().getResource("log4j.xml"));

        log.info("Demarrage de Riocognized");
        DateFormat dateFormat = new SimpleDateFormat("hhmmss-dd-MM-yy");
        Date date = new Date();

        String param = (args.length > 0) ? args[0] : IMAGE_TO_RECOGNIZE;
        File repertoire = new File(param);
        String fileOutput = null;
        if (repertoire.isDirectory()) {
            iterRepertoire(repertoire, 0, 2);
//            for (File file : list) {
//                if (file.getName().matches("(.*)jpg")) {
//                    String fileName = FilenameUtils.removeExtension(file.getAbsolutePath());
//                    crop(fileOutput, fileName);
//                }
//            }
        } else {
            String imageParam = IMAGE_TO_RECOGNIZE;
            File f = repertoire;
            try {
                if (!f.exists()) {
                    throw new IOException();
                }
                crop(imageParam, FilenameUtils.removeExtension(f.getAbsolutePath()));

            } catch (IOException e) {
                log.error("File not found");
                exit(0);
            }
        }
        
        log.info("Fichier présents : ");
        log.info("Images lues : " + nbImage);
        log.info("Visages detectés : " + nbDetected);
    }

    public static void iterRepertoire(File dir, int depth, int maxDepth) {
        if (depth > maxDepth) {
            return;
        }
        if (dir.isDirectory()) {
            File[] subdirs = dir.listFiles();
            for (int i = 0; i < subdirs.length; i++) {
                File subDir = subdirs[i];
                iterRepertoire(subDir, depth + 1, maxDepth);
            }
        } else if (dir.isFile()) {
            nbFile++;
            File f = new File(FilenameUtils.removeExtension(dir.getAbsolutePath()) + "_croped.jpg");
            System.out.println("fichier : " + FilenameUtils.removeExtension(dir.getAbsolutePath()));
            if (dir.getName().matches(".*\\.(png|jpg|gif|bmp|pgm)") && !f.exists() && !dir.getName().matches(".*croped.*")) {
                nbImage++;
                String fileName = FilenameUtils.removeExtension(dir.getAbsolutePath());
                crop(dir.getAbsolutePath(), fileName);
            }
        }
    }

    public static void crop(String fileOutput, String filename) {
        Mat image;
        log.info(
            "Utilisation du fichier : " + fileOutput);
        try {
            FaceDetector faceDetector = new FaceDetector();
            image = imread(fileOutput);
            try {
                //crop face
                Mat crop = faceDetector.cropFaceToMat(image);
                log.info("Detected " + faceDetector.getFacesDetected() + " athletes !");
                if (faceDetector.getFacesDetected() > 0) {
                    imwrite(filename + "_croped.jpg", crop);
                    nbDetected++;
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
