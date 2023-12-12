package com.cryptic.utility;

import org.apache.commons.lang.ArrayUtils;
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

    static String[] directions = new String[]{"n", "e", "s", "w"};
    static int[] unwalkable_npcs = new int[]
        {
            766,
            1479,
            1480,
            1613,
            1618,
            1633,
            1634,
            2117,
            2118,
            2119,
            2292,
            2293,
            2368,
            2369,
            2633,
            2897,
            2898,
            3003,
            3089,
            3090,
            3091,
            3092,
            3093,
            3094,
            3227,
            3318,
            3843,
            3887,
            3888,
            4054,
            4055,
            4762,
            6084,
            6859,
            6860,
            6861,
            6863,
            6864,
            6939,
            6940,
            6941,
            6942,
            6969,
            6970,
            7057,
            7058,
            7059,
            7060,
            7077,
            7078,
            7079,
            7080,
            7081,
            7082,
            8321,
            8322,
            8589,
            8590,
            8666,
            9127,
            9129,
            9130,
            9131,
            9132,
            9484,
            9718,
            9719,
            10389,
            10734,
            10735,
            10736,
            10737,
            101,
            103,
            5936,
            7207,
            7663,
            308,
            7799,
            6601,
            766,
            1617,
            2035,
            1616,
            2038,
            6599,
            493,
            4287,
            4288
        };

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

                        int randomDirection = Utils.random(directions.length);

                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("id", idValue);
                        jsonObject.put("x", xValue);
                        jsonObject.put("y", yValue);
                        jsonObject.put("z", zValue);
                        if (!ArrayUtils.contains(unwalkable_npcs, idValue)) {
                            jsonObject.put("walkRange", Utils.random(2, 5));
                        } else {
                            jsonObject.put("walkRange", 0);
                        }
                        jsonObject.put("direction", directions[randomDirection]);

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
