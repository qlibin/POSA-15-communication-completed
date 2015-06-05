package vandy.mooc.utils;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import vandy.mooc.aidl.WeatherData;
import vandy.mooc.jsonweather.JsonWeather;
import vandy.mooc.jsonweather.WeatherJSONParser;

/**
 * @class WeatherDownloadUtils
 *
 * @brief Handles the actual downloading of Weather information from
 *        the Weather web service.
 */
public class WeatherWebService {
    /**
     * Logging tag used by the debugger.
     */
    private final static String TAG = WeatherWebService.class.getCanonicalName();

    /**
     * URL to the Weather web service.
     */
    private final static String sWeather_Web_Service_URL =
        "http://api.openweathermap.org/data/2.5/weather?units=imperial&q=";

    private static Cache<String, WeatherData> cache = new Cache<String, WeatherData>();

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

        Log.d(TAG, "Lookup weather for location in cache " + location);
        // caching
        WeatherData weatherData = cache.get(location);
        if (weatherData != null) {
            Log.d(TAG, "Weather for location found in cache " + location);
            return weatherData;
        }

        Log.d(TAG, "Get weather for location from web service " + location);

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
            Log.d(TAG, "Got weather for location. Store it in cache and return " + location);
            double speed = jsonWeather.getWind()!=null?jsonWeather.getWind().getSpeed():0;
            double deg = jsonWeather.getWind()!=null?jsonWeather.getWind().getDeg():0;
            double temp = jsonWeather.getMain()!=null?jsonWeather.getMain().getTemp():0;
            long hunidity = jsonWeather.getMain()!=null?jsonWeather.getMain().getHumidity():0;
            long sunrise = jsonWeather.getSys()!=null?jsonWeather.getSys().getSunrise():0;
            long sunset = jsonWeather.getSys()!=null?jsonWeather.getSys().getSunset():0;
            weatherData = new WeatherData(jsonWeather.getName(),
                                          speed,
                                          deg,
                                          temp,
                                          hunidity,
                                          sunrise,
                                          sunset);
            cache.put(location, weatherData);
            return weatherData;
        }  else
            return null;
    }

    public static class Cache<K, V> {

        private int DEFAULT_EXPIRATION_TIME = 10000;

        private int expirationTime;

        public Cache() {
            this.expirationTime = DEFAULT_EXPIRATION_TIME;
            map = new ConcurrentHashMap<K, CacheEntry<V>>();
        }
        public Cache(int expirationTime) {
            this.expirationTime = expirationTime;
            map = new ConcurrentHashMap<K, CacheEntry<V>>();
        }

        private ConcurrentHashMap<K, CacheEntry<V>> map;

        public V get(K key) {
            CacheEntry<V> entry = map.get(key);
            if (entry != null) {
                if  (System.currentTimeMillis() - entry.moment < expirationTime) {
                    return entry.value;
                } else {
                    map.remove(key, entry);
                }
            }
            return null;
        }

        public void put(K key, V value) {
            map.put(key, new CacheEntry<V>(value));
        }

        private static class CacheEntry<V> {
            private V value;
            private long moment;

            private CacheEntry(V value) {
                this.value = value;
                this.moment = System.currentTimeMillis();
            }
        }

    }

    /**
     * Ensure this class is only used as a utility.
     */
    private WeatherWebService() {
        throw new AssertionError();
    } 
}
