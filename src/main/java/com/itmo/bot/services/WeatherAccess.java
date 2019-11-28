package com.itmo.bot.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itmo.bot.entities.Location;
import com.itmo.bot.entities.jsonentities.WeatherModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

@Service
@PropertySource("classpath:telegram.properties")
public class WeatherAccess {

    @Value("${weather.appid}")
    private String weatherAppid;
    private Message message;
    private Location location;
    
    public String getWeather(Message message, Location location) throws IOException {
        this.message = message;
        this.location = location;

        WeatherModel model;

        try {
            model = retrieveDataFromJson();
        } catch (NullPointerException e) {
            System.out.println(e.getMessage());
            return "Something went wrong with accessing weather service";
        }

        return "Location: " + model.getName() + "\n" +
                "temperature: " + (int) model.getMain().getTemp() + " C" + "\n" +
                "Description: " + model.getWeather()[0].getDescription();
    }


    private WeatherModel retrieveDataFromJson() throws IOException {
        String input;

        try {
            input = getResultSetFromUrl();
        } catch (NullPointerException e) {
            System.out.println(e.getMessage());
            return null;
        }
        return new ObjectMapper().readValue(input, WeatherModel.class);
    }

    private String getResultSetFromUrl() throws IOException {
        URL url;

        if (message != null) {
            System.out.println("message");
            url = getUrlForText(message.getText().toLowerCase());

        } else if (location != null) {
            float latitude = location.getLatitude();
            float longitude = location.getLongitude();

            System.out.println("location data: " + latitude + " : " + longitude + "\n");

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
                + latitude + "&lon=" + longitude + "&units=metric&appid=" + weatherAppid);
        // https://api.openweathermap.org/data/2.5/weather?lat=59.95239&lon=59.95239&units=metric&appid=4ddaaaf244cde3a27fb9d8daaeba1f5d
    }

    private static URL getUrlForText(String city) throws MalformedURLException {
        return new URL("https://api.openweathermap.org/data/2.5/weather?q=" +
                city + "&units=metric&appid=4ddaaaf244cde3a27fb9d8daaeba1f5d");

        // https://api.openweathermap.org/data/2.5/weather?q=london&units=metric&appid=4ddaaaf244cde3a27fb9d8daaeba1f5d
    }
}
