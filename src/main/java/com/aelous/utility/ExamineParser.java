package com.aelous.utility;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class ExamineParser {
    public static void main(String[] args) {
        String csvUrl = "https://raw.githubusercontent.com/Joshua-F/osrs-examines/master/locs.csv";
        String outputFile = "locationexamines.txt";

        try {
            String csvContent = fetchCsvContent(csvUrl);
            String parsedData = parseCsv(csvContent);
            writeToFile(outputFile, parsedData);
            System.out.println("Parsed data successfully dumped to " + outputFile);
        } catch (IOException e) {
            System.out.println("An error occurred while fetching, parsing, or writing the CSV data.");
            e.printStackTrace();
        }
    }

    private static String fetchCsvContent(String csvUrl) throws IOException {
        StringBuilder contentBuilder = new StringBuilder();

        URL url = new URL(csvUrl);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()));

        String line;
        while ((line = bufferedReader.readLine()) != null) {
            contentBuilder.append(line);
            contentBuilder.append(System.lineSeparator());
        }

        bufferedReader.close();
        return contentBuilder.toString();
    }

    private static String parseCsv(String csvContent) {
        StringBuilder parsedDataBuilder = new StringBuilder();
        String[] lines = csvContent.split(System.lineSeparator());

        for (int i = 1; i < lines.length; i++) {
            String line = lines[i];
            String[] parts = line.split(",", 2);

            if (parts.length == 2) {
                int locationId = Integer.parseInt(parts[0].trim());
                String examine = parts[1].trim().replaceAll("\"", "");
                String parsedOutput = locationId + ":" + examine;
                parsedDataBuilder.append(parsedOutput);
                parsedDataBuilder.append(System.lineSeparator());
            }
        }

        return parsedDataBuilder.toString();
    }

    private static void writeToFile(String filePath, String content) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
        writer.write(content);
        writer.close();
    }
}
