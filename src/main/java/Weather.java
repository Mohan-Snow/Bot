import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;

// to process the weather data retrieved from the json
public class Weather {

    // weather token: 4ddaaaf244cde3a27fb9d8daaeba1f5d
    public static String getWeather(String message, WeatherModel model) throws IOException {
        // url that process our get query
        URL url = new URL("https://api.openweathermap.org/data/2.5/weather?q=" + message + "&units=metric&appid=4ddaaaf244cde3a27fb9d8daaeba1f5d");

        // scanner reads json content
        Scanner in = new Scanner((InputStream) url.getContent());
        String result = "";

        while (in.hasNext()) {
//            result += in.nextLine();
            result = result.concat(in.nextLine());
        }
        System.out.println("result -> " + result);
        return result;
    }
}
