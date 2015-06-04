package vandy.mooc.utils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import vandy.mooc.aidl.WeatherData;
import vandy.mooc.jsonweather.WeatherJSONParser;
import vandy.mooc.jsonweather.JsonWeather;
import android.app.Activity;
import android.content.Context;
import android.os.IBinder;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

/**
 * @class WeatherDownloadUtils
 *
 * @brief Handles the actual downloading of Weather information from
 *        the Weather web service.
 */
public class Utils {
    /**
     * Logging tag used by the debugger. 
     */
    private final static String TAG = Utils.class.getCanonicalName();

    /** 
     * URL to the Weather web service.
     */
    private final static String sWeather_Web_Service_URL =
        "http://api.openweathermap.org/data/2.5/weather?units=imperial&q=";

    /**
     * Obtain the Weather information.
     * 
     * @return The information that responds to your current weather search.
     */
    public static WeatherData getResult(final String location) {
        // Create a List that will return the WeatherData obtained
        // from the Weather Service web service.
        final List<WeatherData> returnList =
            new ArrayList<WeatherData>();

        // TODO: implement caching

        // A List of JsonWeather objects.
        JsonWeather jsonWeather = null;

        try {
            // Append the location to create the full URL.
            final URL url =
                new URL(sWeather_Web_Service_URL
                        + location);

            // Opens a connection to the Weather Service.
            HttpURLConnection urlConnection =
                (HttpURLConnection) url.openConnection();
            
            // Sends the GET request and reads the Json results.
            try {
                 // Create the parser.
                final WeatherJSONParser parser = new WeatherJSONParser();

                InputStream in = urlConnection.getInputStream();
                in = new BufferedInputStream(in);
                // Parse the Json results and create JsonWeather data
                // objects.
                jsonWeather = parser.parseJson(in);
            } finally {
                urlConnection.disconnect();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // See if we parsed any valid data.
        if (jsonWeather != null && jsonWeather.getCod() == 200l) {
            // Convert the JsonWeather data objects to our WeatherData
            // object, which can be passed between processes.
            double speed = jsonWeather.getWind()!=null?jsonWeather.getWind().getSpeed():0;
            double deg = jsonWeather.getWind()!=null?jsonWeather.getWind().getDeg():0;
            double temp = jsonWeather.getMain()!=null?jsonWeather.getMain().getTemp():0;
            long hunidity = jsonWeather.getMain()!=null?jsonWeather.getMain().getHumidity():0;
            long sunrise = jsonWeather.getSys()!=null?jsonWeather.getSys().getSunrise():0;
            long sunset = jsonWeather.getSys()!=null?jsonWeather.getSys().getSunset():0;
            return new WeatherData(jsonWeather.getName(),
                                   speed,
                                   deg,
                                   temp,
                                   hunidity,
                                   sunrise,
                                   sunset);
        }  else
            return null;
    }

    /**
     * This method is used to hide a keyboard after a user has
     * finished typing the url.
     */
    public static void hideKeyboard(Activity activity,
                                    IBinder windowToken) {
        InputMethodManager mgr =
           (InputMethodManager) activity.getSystemService
            (Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(windowToken,
                                    0);
    }

    /**
     * Show a toast message.
     */
    public static void showToast(Context context,
                                 String message) {
        Toast.makeText(context,
                       message,
                       Toast.LENGTH_SHORT).show();
    }

    /**
     * Ensure this class is only used as a utility.
     */
    private Utils() {
        throw new AssertionError();
    } 
}
