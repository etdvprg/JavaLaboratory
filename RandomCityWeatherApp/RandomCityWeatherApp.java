import org.json.JSONArray;
import org.json.JSONObject;
import java.io.*;
import java.net.*;
import java.util.*;
import java.text.*;
public class RandomCityWeatherApp {
    public static void main(String[] args) throws IOException {
        String apiKey = "pzRaENEiK4CDsS5IBItFg6BNzckGon2A";
        String[] cities = {"Abu Dhabi", "Manila", "Bangkok", "Tokyo", "Dubai", "Beijing", "Singapore", "Riyadh", "Manama", "Muscat", "New York", "Sacramento", "London", "Moscow"};
        String randomCity = cities[new Random().nextInt(cities.length)];

        URL url = new URL("http://dataservice.accuweather.com/locations/v1/cities/search?apikey=" + apiKey + "&q=" + randomCity);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("GET");
        connection.connect();

        int responseCode = connection.getResponseCode();
        String response = "";

        if (responseCode == HttpURLConnection.HTTP_OK) {
            Scanner input = new Scanner(connection.getInputStream());
            while (input.hasNext()) {
                response += input.nextLine();
            }
            input.close();
        } else {
            System.out.println("Error: Unable to connect to AccuWeather API.");
        }
        connection.disconnect();

        JSONObject location = new JSONArray(response).getJSONObject(0);
        String locationKey = location.getString("Key");
        String cityName = location.getString("EnglishName");
        url = new URL("http://dataservice.accuweather.com/currentconditions/v1/" + locationKey + "?apikey=" + apiKey);

        connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();
        responseCode = connection.getResponseCode();
        response = "";

        if (responseCode == HttpURLConnection.HTTP_OK) {
            Scanner scanner = new Scanner(connection.getInputStream());
            while (scanner.hasNext()) {
                response += scanner.nextLine();
            }
            scanner.close();
        } else {
            System.out.println("Error: Unable to connect to AccuWeather API.");
            return;
        }
        connection.disconnect();

        JSONObject currentConditions = new JSONArray(response).getJSONObject(0);
        String weatherText = currentConditions.getString("WeatherText");
        double temperature = currentConditions.getJSONObject("Temperature").getJSONObject("Metric").getDouble("Value");
        long epochTime = currentConditions.getLong("EpochTime");
        Date time = new Date(epochTime * 1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
        sdf.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
        String formattedTime = sdf.format(time);
        System.out.println("City: " + cityName);
        System.out.println("Weather: " + weatherText);
        System.out.println("Temperature: " + temperature + "°C");
        System.out.println("Time: " + formattedTime);
    }
}
