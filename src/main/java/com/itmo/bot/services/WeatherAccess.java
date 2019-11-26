package com.itmo.bot.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itmo.bot.config.BotConfig;
import com.itmo.bot.entities.jsonentities.WeatherModel;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class WeatherAccess {

    private static volatile WeatherAccess instance; // Instance for this class
    private Message message;

    private WeatherAccess() {
    }

    public static WeatherAccess getInstance() {
        WeatherAccess currentInstance;

        if (instance == null) {

            synchronized (WeatherAccess.class) {
                if (instance == null) {
                    instance = new WeatherAccess();
                }
                currentInstance = instance;
            }
        } else {
            currentInstance = instance;
        }
        return currentInstance;
    }

    public String getWeather(Message message) throws IOException {

        this.message = message;

        WeatherModel model = retrieveDataFromJson();

        System.out.println(model.getName() + "\n" +
                "id: " + model.getId() + "\n" +
                "timezone: " + model.getTimezone());


        return "Location: " + model.getName() + "\n" +
                "temperature: " + model.getMain().getTemp() + "C" + "\n";
    }

    private WeatherModel retrieveDataFromJson() throws IOException {
        String input = getResultSetFromUrl();
        return new ObjectMapper().readValue(input, WeatherModel.class);
    }

    private String getResultSetFromUrl() throws IOException {
        URL url;

        if (message.hasText()) {
            url = getUrlForText(message.getText().toLowerCase());

        } else if (message.hasLocation()) {
            float latitude = message.getLocation().getLatitude();
            float longitude = message.getLocation().getLongitude();

            url = getUrlForLocation(latitude, longitude);
        } else {
            return null;
        }

        // scanner reads file.json content
        Scanner in = new Scanner((InputStream) url.getContent());
        String result = "";

        while (in.hasNext()) {
            result = result.concat(in.nextLine());
        }
        return result;
    }

    private URL getUrlForLocation(float latitude, float longitude) throws MalformedURLException {


        // suits for retrieving data from properties
//        String.format()

        // url that process our get query
        return new URL("https://api.openweathermap.org/data/2.5/weather?lat="
                + latitude + "&lon=" + longitude + "&units=metric&appid=" + BotConfig.WEATHER_APPID);
        // https://api.openweathermap.org/data/2.5/weather?lat=59.926983&lon=30.361164&units=metric&appid=4ddaaaf244cde3a27fb9d8daaeba1f5d
    }

    private static URL getUrlForText(String city) throws MalformedURLException {
        return new URL("https://api.openweathermap.org/data/2.5/weather?q=" +
                city + "&units=metric&appid=4ddaaaf244cde3a27fb9d8daaeba1f5d");
    }
}
