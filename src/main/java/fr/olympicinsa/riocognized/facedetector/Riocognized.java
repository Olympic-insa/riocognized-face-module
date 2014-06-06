package fr.olympicinsa.riocognized.facedetector;

import com.googlecode.javacv.cpp.opencv_core.IplImage;
import fr.olympicinsa.riocognized.facedetector.db.FaceDBReader;
import fr.olympicinsa.riocognized.facedetector.detection.FaceDetector;
import fr.olympicinsa.riocognized.facedetector.exception.FaceDBException;
import fr.olympicinsa.riocognized.facedetector.recognition.RioRecognizer;
import fr.olympicinsa.riocognized.facedetector.tools.ImageConvertor;
import java.io.File;
import java.io.IOException;
import static java.lang.System.exit;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.math3.stat.descriptive.moment.Variance;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;

public class Riocognized {

    public static Logger log = Logger.getLogger(Riocognized.class);
    public static String IMAGE_TO_RECOGNIZE = "/opt/openCV/testbench";
    public static String DIR_TO_RECOGNIZE = "/opt/openCV/TestImage";
    public static String DIR_TO_DB = "/opt/openCV/athleteDB/faces.csv";
    public static double SEUIL = 85;
    public static double right = 0;
    public static double wrong = 0;
    public static double notrec = 0;
    public static HashMap<String,double[]> athletes = new HashMap<>();
    public static List<Double> precisionTrue = new ArrayList<>();
    public static List<Double> precisionFalse = new ArrayList<>();
    
    public static void main(String[] args) {

        DOMConfigurator.configure(Thread.currentThread().getContextClassLoader().getResource("log4j.xml"));
        athletes.put("207", new double[]{0,0});
        athletes.put("1", new double[]{0,0});
        athletes.put("218", new double[]{0,0});
        athletes.put("376", new double[]{0,0});
        log.info("Demarrage de Riocognized");
        DateFormat dateFormat = new SimpleDateFormat("hhmmss-dd-MM-yy");
        Date date = new Date();
        String dateString = dateFormat.format(date);
        String db = (args.length > 1) ? args[1] : DIR_TO_DB;
        String athletePath;
        FaceDBReader faceDB;
        
        String path = "/opt/openCV/test.yml";
        //Load faceDB
        try {
            faceDB = new FaceDBReader(db);

            //Create  Recognizor
            log.info("Create Recognizer");
            RioRecognizer recognizor = new RioRecognizer(faceDB, path);
            //Store faces
            log.info("Read CSV and load images");
            recognizor.init();
            log.info("Train Recognizer");
            recognizor.train();
            log.info("Load Recognizer");
            //recognizor.load(path);
            recognizor.save();
            

            log.info("Initialize Riocognized FaceDetector");
            String param = (args.length > 0) ? args[0] : IMAGE_TO_RECOGNIZE;
            File repertoire = new File(param);
            File repertoire2;
            String fileOutput = null;
            if (repertoire.isDirectory()) {
                File[] list = repertoire.listFiles();
                for (File file : list) {
                    if (file.getName().matches("(.*)jpg")) {
                        fileOutput = file.getAbsolutePath();
                        String fileName = FilenameUtils.removeExtension(file.getName());
                        recognizeTest(fileOutput, fileName, recognizor);
                    } else if (file.isDirectory()) {
                        File[] list2 = file.listFiles();
                        for (File file3 : list2) {
                            if (file3.getName().matches("(.*)jpg")) {
                                fileOutput = file3.getAbsolutePath();
                                String fileName = FilenameUtils.removeExtension(file3.getName());
                                recognizeTest(fileOutput, fileName, recognizor);
                            }
                        }
                    }
                }
            } else {
                String imageParam = param;
                File f = repertoire;
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
                recognizeTest(imageParam, FilenameUtils.removeExtension(f.getName()), recognizor);
            }
        } catch (FaceDBException e) {
            log.error("Can't read/create faceDB csv");
            exit(0);
        }
        double precision = right / (right+ wrong);
        double accuracy = right / (right+ wrong + notrec);
        log.info("#############################");
        log.info("#---------Results-----------#");
        log.info("TRUE :" + right);
        log.info("FALSE :" + wrong);
        log.info("NOTREC :" + notrec);
        log.info("PRECISION :" + precision);
        log.info("ACCURACY :" + accuracy);
        Set listAthletes = athletes.keySet();
        Iterator it = listAthletes.iterator();
        while (it.hasNext()) {
            String key = (String) it.next();
            double[] stat = athletes.get(key);
            double occ = stat[1]/stat[0];
            log.info("Athlete "+key +"- Nb: "+stat[0]+" - Reco: " + stat[1] + " - Occuracy : "+occ);
        }
        log.info("Precision True - MOY: " + moyenne(precisionTrue) + " - VAR:" + var(precisionTrue) + 
            " - EC:" + ec(precisionTrue));
        log.info("Precision False - MOY: " + moyenne(precisionFalse) + " - VAR:" + var(precisionFalse) + 
            " - EC:" + ec(precisionFalse));
    }

    public Riocognized() {
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
                    //Highgui.imwrite(filename + "_croped.jpg", crop);
                    IplImage face = ImageConvertor.matToIplImage(crop);
                    for (int i = 0; i < 1; i++) {
                        int athlete = recognizor.predictedLabel(face);
                        log.info("Athlete recognized : " + athlete);
                        log.info("Image: " + filename.split("_")[0]);
                        double[] nbAth = athletes.get(filename.split("_")[0]);
                        nbAth[0]++;
                        //recognizor.changeRecognizer(i);
                        if ((double)recognizor.getPrecision()[0] < SEUIL) {
                            if (fileOutput.contains(Integer.toString(athlete) + "_")) {
                                log.info("TRUE");
                                nbAth[1]++;
                                precisionTrue.add((double)recognizor.getPrecision()[0]);
                                right++;
                            } else {
                                log.info("FALSE");
                                wrong++;
                                precisionFalse.add((double)recognizor.getPrecision()[0]);
                            }
                            athletes.put(filename.split("_")[0], nbAth);
                        } else {
                            log.info("NOT REC");
                            precisionFalse.add((double)recognizor.getPrecision()[0]);
                            notrec++;
                        }
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
    
    public static double moyenne(List list) {
        Iterator it = list.iterator();
        double tot = 0d;
        while (it.hasNext()) {
            double val = (double) it.next();
            tot = tot + val;
        }
        return tot / list.size();
    }
    
    public static double var(List list) {
        Variance var = new Variance();
        int i = 0;
        Iterator it = list.iterator();
        double moy = moyenne(list);
        double [] values = new double[list.size()];

        while (it.hasNext()) {
            double val = (double) it.next();
            values[i] = val;
            i++;
        }
                
        return var.evaluate(values, moy);
        
    }
    
    public static double ec(List list) {
        return Math.sqrt(var(list));
    }
}
