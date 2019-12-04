package com.itmo.bot.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itmo.bot.entities.Location;
import com.itmo.bot.entities.jsonentities.WeatherModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

@Service
@PropertySource("classpath:telegram.properties")
public class WeatherAccess<T> {

    @Value("${weather.appid}")
    private String weatherAppid;

    public String getForecastForThisDay(T type) throws IOException {
        WeatherModel model = retrieveDataFromJson(type);
        assert model != null;

        return "Location: " + model.getName() + "\n" +
                "temperature varies from : " + (int) model.getMain().getTemp_min() +
                " to " + (int) model.getMain().getTemp_max() + "℃" + "\n" +
                "Description: " + model.getWeather()[0].getDescription();
    }

    public String getCurrentForecast(T type) throws IOException {
        WeatherModel model = retrieveDataFromJson(type);
        assert model != null;

        return "Location: " + model.getName() + "\n" +
                "temperature: " + (int) model.getMain().getTemp() + " C" + "\n" +
                "Description: " + model.getWeather()[0].getDescription();
    }


    private WeatherModel retrieveDataFromJson(T type) throws IOException {

        String input;

        try {
            input = getResultSetFromUrl(type);
        } catch (NullPointerException e) {
            System.out.println(e.getMessage());
            return null;
        }
        return new ObjectMapper().readValue(input, WeatherModel.class);
    }

    private String getResultSetFromUrl(T objectType) throws IOException {
        URL url;

        if (objectType instanceof Message) {
//            System.out.println("message");
            url = getUrlForText(((Message) objectType).getText().toLowerCase());

        } else if (objectType instanceof Location) {
            float latitude = ((Location) objectType).getLatitude();
            float longitude = ((Location) objectType).getLongitude();

//            System.out.println("location data: " + latitude + " : " + longitude + "\n");

            url = getUrlForLocation(latitude, longitude);
        } else {
            return null;
        }

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("User-Agent", "Mozilla/5.0");

        int responseCode = connection.getResponseCode();
        if (responseCode == 404)
            throw new IllegalArgumentException();

        StringBuffer response;
        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            response = new StringBuffer();

            String line;
            while ((line = in.readLine()) != null) {
                response.append(line);
            }
        }

        return response.toString();


        // scanner reads file.json content
//        Scanner in = new Scanner((InputStream) url.getContent());
//        String result = "";

//        while (in.hasNext()) {
//            result = result.concat(in.nextLine());
//        }
//        return result;
    }

    private URL getUrlForLocation(float latitude, float longitude) throws MalformedURLException {
//      TODO: change to String.format()

        // url that process our get query
        return new URL("https://api.openweathermap.org/data/2.5/weather?lat="
                + latitude + "&lon=" + longitude + "&units=metric&appid=" + weatherAppid);
    }

    private static URL getUrlForText(String city) throws MalformedURLException {
        return new URL("https://api.openweathermap.org/data/2.5/weather?q=" +
                city + "&units=metric&appid=4ddaaaf244cde3a27fb9d8daaeba1f5d");
    }
}
