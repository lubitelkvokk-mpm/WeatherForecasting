package se.ifmo.ru;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;

public class Main {

    private static JSONObject getJsonFromObj(Object obj, String key) {
        return (JSONObject) ((JSONObject) obj).get(key);
    }

    private static JSONObject getJsonFromObj(JSONObject obj, String key) {
        return (JSONObject) obj.get(key);
    }

    private static JSONArray getJsonArrFromObj(Object obj, String key) {
        return (JSONArray) ((JSONObject) obj).get(key);
    }

    private static JSONArray getJsonArrFromObj(JSONObject obj, String key) {
        return (JSONArray) obj.get(key);
    }

    static void main() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();


        System.out.println("Введите количество дней, на которое вы хотите получить прогноз (от 1 до 11)");
        Scanner scanner = new Scanner(System.in);
        int limit = scanner.nextInt();
        if (limit < 1 || limit > 11) throw new RuntimeException("Указанный лимит прогноза находится вне допустимого диапазона");


        String apiKey = System.getenv("YANDEX_WEATHER_KEY");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.weather.yandex.ru/v2/forecast?lat=52.37125&lon=4.89388&limit=" + limit))
                .header("X-Yandex-Weather-Key", apiKey)
                .GET()
                .build();


        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        String body = response.body();
        System.out.println("FULL BODY:\n\t" + body);

        Object jsonObject = JSONValue.parse(body);
        JSONObject fact = getJsonFromObj(jsonObject, "fact");
        Long temp = (Long) fact.get("temp");
        System.out.println("Current temp is " + temp + " degrees");


        JSONArray forecasts = getJsonArrFromObj(jsonObject, "forecasts");
        Long tempAvgSum = 0L;
        for (Object forecast : forecasts) {
            JSONObject parts = getJsonFromObj(forecast, "parts");
            JSONObject day = getJsonFromObj(parts, "day");
            tempAvgSum += (Long) day.get("temp_avg");
        }

        System.out.println("AvgTemp for the next " + limit + " days: " + (float) tempAvgSum / limit + " degrees");
    }
}
