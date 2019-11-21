package com.itmo.bot.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itmo.bot.BotConfig;
import com.itmo.bot.entities.jsonentities.WeatherModel;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class WeatherAccessService {

    private static volatile WeatherAccessService instance; // Instance for this class

    private WeatherAccessService() {
    }

    public static WeatherAccessService getInstance() {
        WeatherAccessService currentInstance;

        if (instance == null) {

            synchronized (WeatherAccessService.class) {
                if (instance == null) {
                    instance = new WeatherAccessService();
                }
                currentInstance = instance;
            }
        } else {
            currentInstance = instance;
        }
        return currentInstance;
    }

    public String getWeather(Message message) throws IOException {

        WeatherModel model = retrieveDataFromJson(message);

        System.out.println(model.getName() + "\n" +
                "id: " + model.getId() + "\n" +
                "timezone: " + model.getTimezone());


        return "Location: " + model.getName() + "\n" +
                "temperature: " + model.getMain().getTemp() + "C" + "\n";
    }

    private WeatherModel retrieveDataFromJson(Message message) throws IOException {
        String input = getResultSetFromUrl(message);
        return new ObjectMapper().readValue(input, WeatherModel.class);
    }

    private String getResultSetFromUrl(Message message) throws IOException {
        URL url = getUrlForLocation(message);

        // scanner reads file.json content
        Scanner in = new Scanner((InputStream) url.getContent());
        String result = "";

        while (in.hasNext()) {
            result = result.concat(in.nextLine());
        }
        return result;
    }

    private URL getUrlForLocation(Message message) throws MalformedURLException {
        float latitude = message.getLocation().getLatitude();
        float longitude = message.getLocation().getLongitude();

        // suits for retrieving data from properties
//        String.format()

        System.out.println(latitude);
        System.out.println(longitude);

        // url that process our get query
        return new URL("https://api.openweathermap.org/data/2.5/weather?lat="
                + latitude + "&lon=" + longitude + "&units=metric&appid=" + BotConfig.WEATHER_APPID);
        // https://api.openweathermap.org/data/2.5/weather?lat=59.926983&lon=30.361164&units=metric&appid=4ddaaaf244cde3a27fb9d8daaeba1f5d
    }
}
