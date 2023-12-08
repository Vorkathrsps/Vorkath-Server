package com.cryptic.utility;

import org.json.simple.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class SpawnParser {

    public static void main(String[] args) {
        String inputFilePath = "C:\\Users\\2007z\\OneDrive\\Desktop\\Aelous210\\npcs.txt";
        String outputFilePath = "npc_spawns.json";
        parseFile(inputFilePath, outputFilePath);
    }

    public static void parseFile(String inputFilePath, String outputFilePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFilePath))) {
            StringBuilder outputContent = new StringBuilder();

            String line;
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length >= 5) {
                    try {
                        int idValue = Integer.parseInt(values[1].trim());
                        int zValue = Integer.parseInt(values[2].trim());
                        int xValue = Integer.parseInt(values[3].trim());
                        int yValue = Integer.parseInt(values[4].trim());

                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("id", idValue);
                        jsonObject.put("x", xValue);
                        jsonObject.put("y", yValue);
                        jsonObject.put("z", zValue);
                        jsonObject.put("walkRange", 5);

                        outputContent.append(jsonObject).append(System.lineSeparator());
                    } catch (NumberFormatException e) {
                        System.err.println("Error parsing line: " + line + ". Skipping.");
                    }
                } else {
                    System.err.println("Invalid line format: " + line + ". Skipping.");
                }
            }

            writeToFile(outputFilePath, outputContent.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeToFile(String filePath, String content) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
        writer.write(content);
        writer.close();
    }
}
