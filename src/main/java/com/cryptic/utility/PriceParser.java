package com.cryptic.utility;

import com.google.gson.*;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class PriceParser {
    public static void main(String[] args) {
        String apiUrl = "https://prices.runescape.wiki/api/v1/osrs/latest";

        try {
            JsonObject jsonObject = fetchData(apiUrl);
            JsonArray formattedObject = formatData(jsonObject);
            writeToFile(formattedObject.toString());
            System.out.println("Formatted data has been written to the file.");
        } catch (Exception e) {
            System.out.println("An error occurred while fetching, parsing, or writing the data.");
            e.printStackTrace();
        }
    }

    private static JsonObject fetchData(String apiUrl) throws IOException {
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        try (InputStreamReader inputStreamReader = new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8)) {
            Gson gson = new Gson();
            JsonElement jsonElement = gson.fromJson(inputStreamReader, JsonElement.class);
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            return jsonObject.getAsJsonObject("data");
        }
    }

    private static JsonArray formatData(JsonObject jsonObject) {
        JsonArray formattedArray = new JsonArray();

        for (String key : jsonObject.keySet()) {
            JsonObject item = jsonObject.getAsJsonObject(key);
            JsonElement highElement = item.get("high");
            if (highElement != null && !(highElement instanceof JsonNull)) {
                int itemId = Integer.parseInt(key);
                long value = highElement.getAsLong();

                JsonObject formattedItem = new JsonObject();
                formattedItem.addProperty("id", itemId);
                formattedItem.addProperty("value", value);

                formattedArray.add(formattedItem);
            }
        }

        return formattedArray;
    }


    private static void writeToFile(String data) throws IOException {
        try (FileWriter writer = new FileWriter("item_prices.json")) {
            writer.write(data);
        }
    }
}




