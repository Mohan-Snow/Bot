import org.json.JSONArray;
import org.json.JSONObject;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

// to process the weather data retrieved from the file.json
public class Weather {

    private static URL getUrlForText(Message message) throws MalformedURLException {
        String city = message.getText().toLowerCase();

        return new URL("https://api.openweathermap.org/data/2.5/weather?q=" + city + "&units=metric&appid=4ddaaaf244cde3a27fb9d8daaeba1f5d");
    }

    private static URL getUrlForLocation(Message message) throws MalformedURLException {
        float latitude = message.getLocation().getLatitude();
        float longitude = message.getLocation().getLongitude();

        // url that process our get query
        return new URL("https://api.openweathermap.org/data/2.5/weather?lat="
                + latitude + "&lon=" + longitude + "&units=metric&appid=4ddaaaf244cde3a27fb9d8daaeba1f5d");
    }

    // weather token: 4ddaaaf244cde3a27fb9d8daaeba1f5d
    public static String getWeather(Message message) throws IOException {

        URL url = getUrlForLocation(message);

        // scanner reads file.json content
        Scanner in = new Scanner((InputStream) url.getContent());
        String result = "";

        while (in.hasNext()) {
            result = result.concat(in.nextLine());
        }
//        System.out.println("result -> " + result);

        WeatherModel model = new WeatherModel();

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

        return "Location: " + model.getName() + "\n" +
                "temperature: " + model.getTemperature() + "C" + "\n" +
                "humidity: " + model.getHumidity() + "%" + "\n" +
                "http://openweathermap.org/img/wn/" + model.getIcon() + ".png";
    }
}
