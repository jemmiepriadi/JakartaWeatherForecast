package org.example;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;

import org.json.JSONArray;
import org.json.JSONObject;


public class Main {
    public static void main(String[] args) {
        try {
            String apiKey = "0c7941e70ea977cd56294113a359554e";
            //weather forecast for Jakarta
            String urlString = "https://api.openweathermap.org/data/2.5/forecast?q=Jakarta&units=metric&appid="+apiKey;
            URL url = new URL(urlString);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null)
                response.append(inputLine);
            in.close();

            JSONObject json = new JSONObject(response.toString());
            JSONArray list = json.getJSONArray("list");
            HashSet<String> printedDates = new HashSet<>();
            System.out.println("Weather Forecast:");

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE, dd MMMM yyyy")
                    .withZone(ZoneId.of("Asia/Jakarta"));

            for (int i = 0; i < list.length(); i++) {
                JSONObject item = list.getJSONObject(i);
                String dtTxt = item.getString("dt_txt");

                if (dtTxt.contains("12:00:00")) {  // Only mid-day entries
                    String date = dtTxt.split(" ")[0];
                    if (!printedDates.contains(date)) {
                        double temp = item.getJSONObject("main").getDouble("temp");

                        // Convert "dt" to formatted date
                        long dt = item.getLong("dt");
                        String formattedDate = formatter.format(Instant.ofEpochSecond(dt));

                        System.out.printf("%s: %.2f°C%n", formattedDate, temp);
                        printedDates.add(date);
                    }
                }

                if (printedDates.size() >= 5)
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}