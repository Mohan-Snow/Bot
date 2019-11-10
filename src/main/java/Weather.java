import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Scanner;

// to process the weather data retrieved from the file.json
public class Weather {

    // weather token: 4ddaaaf244cde3a27fb9d8daaeba1f5d
    public static String getWeather(String message, WeatherModel model) throws IOException {
        // url that process our get query
        URL url = new URL("https://api.openweathermap.org/data/2.5/weather?q=" + message + "&units=metric&appid=4ddaaaf244cde3a27fb9d8daaeba1f5d");

        // scanner reads file.json content
        Scanner in = new Scanner((InputStream) url.getContent());
        String result = "";

        while (in.hasNext()) {
            result += in.nextLine();
//            result = result.concat(in.nextLine());
        }
        System.out.println("result -> " + result);

        // casting the outer json object
        JSONObject wholeJSON = new JSONObject(result);

        model.setName(wholeJSON.getString("name"));
        JSONObject mainJSON = wholeJSON.getJSONObject("main");

        model.setTemperature(mainJSON.getDouble("temp"));
        model.setHumidity(mainJSON.getDouble("humidity"));

        // putting data into array to get to the sub-json
        JSONArray jsonArray = wholeJSON.getJSONArray("weather");
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);
            model.setIcon((String) obj.get("icon"));
            model.setMain((String) obj.get("main"));
        }

        return "City: " + model.getName() + "\n" +
                "temperature: " + model.getTemperature() + "C" + "\n" +
                "humidity: " + model.getHumidity() + "%" + "\n" +
                "http://openweathermap.org/img/wn/" + model.getIcon() + ".png";
    }
}
