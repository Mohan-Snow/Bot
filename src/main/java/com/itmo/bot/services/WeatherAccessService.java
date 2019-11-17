package com.itmo.bot.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itmo.bot.entities.jsonentities.WeatherModel;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class WeatherAccessService {

    private Message message;
    private String weatherAppid;

    public WeatherAccessService(Message message, String weatherAppid) {
        this.message = message;
        this.weatherAppid = weatherAppid;
    }

    public String getWeather() throws IOException {
//        WeatherModel model = new WeatherModel();

        // casting the outer json object
//        JSONObject wholeJSON = new JSONObject(getResultSetFromUrl());

//        model.setName(wholeJSON.getString("name"));
//        JSONObject mainJSON = wholeJSON.getJSONObject("main");

//        model.setTemperature(mainJSON.getDouble("temp"));
//        model.setHumidity(mainJSON.getDouble("humidity"));

        // putting data into array to get to the sub-json
//        JSONArray jsonArray = wholeJSON.getJSONArray("weather");
//        for (int i = 0; i < jsonArray.length(); i++) {
//            JSONObject obj = jsonArray.getJSONObject(i);
//            model.setIcon((String) obj.get("icon"));
//            model.setMain((String) obj.get("main"));
//        }


        WeatherModel model = retrieveDataFromJson();


        return "Location: " + model.getName() + "\n" +
                "temperature: " + model.getMain().getTemp() + "C" + "\n";

        // "http://openweathermap.org/img/wn/" + model.getWeather()[3] + ".png"
    }

    private WeatherModel retrieveDataFromJson() throws IOException {
        String input = getResultSetFromUrl();
        return new ObjectMapper().readValue(input, WeatherModel.class);
    }

    private String getResultSetFromUrl() throws IOException {
        URL url = getUrlForLocation();

        // scanner reads file.json content
        Scanner in = new Scanner((InputStream) url.getContent());
        String result = "";

        while (in.hasNext()) {
            result = result.concat(in.nextLine());
        }
        return result;
    }

    private URL getUrlForLocation() throws MalformedURLException {
        float latitude = message.getLocation().getLatitude();
        float longitude = message.getLocation().getLongitude();

        System.out.println(latitude);
        System.out.println(longitude);

        // url that process our get query
        return new URL("https://api.openweathermap.org/data/2.5/weather?lat="
                + latitude + "&lon=" + longitude + "&units=metric&appid=" + weatherAppid);
    }
}
