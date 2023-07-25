import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.text.*;

public class RandomWeatherCityGenerator {

    public static void main(String[] args) {
        JFrame index = new JFrame("Random City Weather App");
        index.setSize(500, 500);
        index.setLocationRelativeTo(null);

        JPanel welcomePanel = new JPanel();
        welcomePanel.setLayout(new BorderLayout());

        JLabel welcomeLabel = new JLabel("Welcome to the app!");
        JButton generateBtn = new JButton ("Generate Weather Information!");
        welcomeLabel.setHorizontalAlignment(JLabel.CENTER);
        welcomePanel.add(welcomeLabel, BorderLayout.CENTER);
        welcomePanel.add(generateBtn, BorderLayout.SOUTH);

        generateBtn.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                try {
                    String apiKey = "pzRaENEiK4CDsS5IBItFg6BNzckGon2A";
                    String[] cities = { "Abu Dhabi", "Manila", "Bangkok", "Tokyo", "Dubai", "Beijing", "Singapore",
                            "Riyadh", "Manama", "Muscat", "New York", "Sacramento", "London", "Moscow" };
                    String randomCity = cities[new Random().nextInt(cities.length)];

                    URL searchLocation = new URL(
                            "http://dataservice.accuweather.com/locations/v1/cities/search?apikey=" + apiKey + "&q="
                                    + randomCity);

                    String response = doHTTPGetRequest(String.valueOf(searchLocation));

                    JSONObject location = new JSONArray(response).getJSONObject(0);
                    String locationKey = location.getString("Key");
                    String cityName = location.getString("EnglishName");

                    URL locationInfo = new URL(
                            "http://dataservice.accuweather.com/currentconditions/v1/" + locationKey + "?apikey="
                                    + apiKey);

                    String response2 = doHTTPGetRequest(String.valueOf(locationInfo));

                    JSONObject currentConditions = new JSONArray(response2).getJSONObject(0);
                    String weatherText = currentConditions.getString("WeatherText");
                    double temperature = currentConditions.getJSONObject("Temperature").getJSONObject("Metric")
                            .getDouble("Value");
                    long epochTime = currentConditions.getLong("EpochTime");
                    Date time = new Date(epochTime * 1000L);
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss z");
                    sdf.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
                    String formattedTime = sdf.format(time);

                    String localObservationDateTime = currentConditions.getString("LocalObservationDateTime");
                    String localTime = localObservationDateTime.substring(11, 19);

                    JFrame infoPage = new JFrame("Information Page");
                    infoPage.setLayout(new GridLayout(4, 2));

                    JLabel cityLabel = new JLabel("City: ");
                    JLabel weatherLabel = new JLabel("Weather: ");
                    JLabel temperatureLabel = new JLabel("Temperature: ");
                    JLabel localTimeLabel = new JLabel("Local Time: ");

                    JLabel cityData = new JLabel(cityName);
                    JLabel weatherData = new JLabel(weatherText);
                    JLabel temperatureData = new JLabel(String.valueOf(temperature));
                    JLabel localTimeDate = new JLabel(localTime);

                    infoPage.add(cityLabel);
                    infoPage.add(cityData);
                    infoPage.add(weatherLabel);
                    infoPage.add(weatherData);
                    infoPage.add(temperatureLabel);
                    infoPage.add(temperatureData);
                    infoPage.add(localTimeLabel);
                    infoPage.add(localTimeDate);


                    index.dispose();
                    infoPage.setSize(300, 200);
                    infoPage.setVisible(true);

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        index.getContentPane().add(welcomePanel);
        index.setVisible(true);
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

}

