import org.json.JSONArray;
import org.json.JSONObject;
import java.io.*;
import java.net.*;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.text.*;
public class RandomCityWeatherApp {
    public static void main(String[] args) throws IOException {
        String apiKey = "pzRaENEiK4CDsS5IBItFg6BNzckGon2A";
        String[] cities = {"Abu Dhabi", "Manila", "Bangkok", "Tokyo", "Dubai", "Beijing", "Singapore", "Riyadh", "Manama", "Muscat", "New York", "Sacramento", "London", "Moscow"};
        String randomCity = cities[new Random().nextInt(cities.length)];

        URL searchLocation = new URL("http://dataservice.accuweather.com/locations/v1/cities/search?apikey=" + apiKey + "&q=" + randomCity);

        String response = doHTTPGetRequest(String.valueOf(searchLocation));

        JSONObject location = new JSONArray(response).getJSONObject(0);
        String locationKey = location.getString("Key");
        String cityName = location.getString("EnglishName");

        URL locationInfo = new URL("http://dataservice.accuweather.com/currentconditions/v1/" + locationKey + "?apikey=" + apiKey);

        String response2 = doHTTPGetRequest(String.valueOf(locationInfo));
        System.out.println(response2);

        JSONObject currentConditions = new JSONArray(response2).getJSONObject(0);
        String weatherText = currentConditions.getString("WeatherText");
        double temperature = currentConditions.getJSONObject("Temperature").getJSONObject("Metric").getDouble("Value");
        long epochTime = currentConditions.getLong("EpochTime");
        Date time = new Date(epochTime * 1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss z");
        sdf.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
        String formattedTime = sdf.format(time);

        String localObservationDateTimeS = currentConditions.getString("LocalObservationDateTime");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        OffsetDateTime localObservationDateTime = OffsetDateTime.parse(localObservationDateTimeS, formatter);
        System.out.println(localObservationDateTime);
        generatedInfo(cityName, weatherText, temperature, formattedTime);
    }

    public static String doHTTPGetRequest(String url) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
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
                System.out.println("Error: Unable to connect to API.");
            }
            connection.disconnect();
            return response;
        } catch (ProtocolException e) {
            throw new RuntimeException(e);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void generatedInfo(String cn, String w, double t, String ft) {
        System.out.println("City: " + cn);
        System.out.println("Weather: " + w);
        System.out.println("Temperature: " + t + "°C");
        System.out.println("Standard Time: " + ft);
        //System.out.println("Local Time: " + lt);
    }
}
