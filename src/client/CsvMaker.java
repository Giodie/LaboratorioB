package client;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CsvMaker {


    private FileFinder fileFinder;
    /**
     * Costruttore della classe CsvMaker, inizializza l'oggetto della classe FileFinder. Crea un nuovo file csv formattato in un certo modo partendo da un file csv in input.
     */
    public CsvMaker() {
        fileFinder = new FileFinder();
        String inputFile = String.valueOf(fileFinder.MasterCSVPath());
        String outputFile = String.valueOf(fileFinder.LibrifilePath());


        try (
                CSVReader reader = new CSVReader(new FileReader(inputFile));
                CSVWriter writer = new CSVWriter(new FileWriter(outputFile));
        ) {
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                nextLine = fixMalformedLine(reader, nextLine);

                List<String> selectedColumns = new ArrayList<>();
                selectedColumns.add(nextLine[0]);
                selectedColumns.add(nextLine[1]);
                selectedColumns.add(nextLine[7]);



                writer.writeNext(selectedColumns.toArray(new String[0]));
            }
        } catch (IOException | CsvValidationException ex) {
            ex.printStackTrace();
        }
    }
    /**
     * Metodo utilizzato per aggiustare le linee del file.
     * 
     * @param reader il reader del file in input
     * @param line l'Array su cui viene effettuata la modifica
     * @return un array di stringhe.
     * @throws IOException
     * @throws CsvValidationException
     */
    private String[] fixMalformedLine(CSVReader reader, String[] line) throws IOException, CsvValidationException {
        for (int i = 0; i < line.length; i++) {
            if (line[i].startsWith("\"") && !line[i].endsWith("\"")) {
                StringBuilder fixedField = new StringBuilder(line[i]);
                String[] nextLine;

                while ((nextLine = reader.readNext()) != null) {
                    fixedField.append("\n").append(nextLine[i]);
                    if (nextLine[i].endsWith("\"")) {
                        line[i] = fixedField.toString();
                        break;
                    }
                }
            }
        }
        return line;
    }
}