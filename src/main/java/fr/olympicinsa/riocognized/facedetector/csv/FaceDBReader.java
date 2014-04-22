/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.olympicinsa.riocognized.facedetector.csv;

import au.com.bytecode.opencsv.CSVReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author alex
 */
public class FaceDBReader {

    public static List<String> readFile(String File) throws IOException {

        try {
            FileReader fileReader = new FileReader(File);
            CSVReader csvReader = new CSVReader(fileReader,';');
            List content = csvReader.readAll();
            csvReader.close();
            return content;
        } catch (IOException e) {
            return null;
        }
    }

}
