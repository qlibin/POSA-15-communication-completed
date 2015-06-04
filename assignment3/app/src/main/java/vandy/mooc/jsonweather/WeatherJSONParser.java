package vandy.mooc.jsonweather;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.util.JsonReader;
import android.util.JsonToken;

/**
 * Parses the Json weather data returned from the Weather Services API
 * and returns a List of JsonWeather objects that contain this data.
 */
public class WeatherJSONParser {
    /**
     * Used for logging purposes.
     */
    private final String TAG =
        this.getClass().getCanonicalName();

    /**
     * Parse the @a inputStream and convert it into a List of JsonWeather
     * objects.
     */
    public List<JsonWeather> parseJsonStream(InputStream inputStream)
        throws IOException {
        // Create a JsonReader for the inputStream.
        JsonReader reader =
                new JsonReader(new InputStreamReader(inputStream,
                        "UTF-8"));
        try {
            // Log.d(TAG, "Parsing the results returned as an array");

            return parseJsonWeatherArray(reader);
        } finally {
            reader.close();
        }
    }

    public JsonWeather parseJson(InputStream inputStream)
        throws IOException {
        // Create a JsonReader for the inputStream.
        JsonReader reader =
                new JsonReader(new InputStreamReader(inputStream,
                        "UTF-8"));
        try {
            // Log.d(TAG, "Parsing the results returned as an array");

            return parseJsonWeather(reader);
        } finally {
            reader.close();
        }
    }

    /**
     * Parse a Json stream and convert it into a List of JsonWeather
     * objects.
     */
    public List<JsonWeather> parseJsonWeatherArray(JsonReader reader)
        throws IOException {

        reader.beginArray();
        try {
            if (reader.peek() == JsonToken.END_ARRAY)
                return null;

            List<JsonWeather> jsonWeathers = new ArrayList<JsonWeather>();

            while (reader.hasNext())
                jsonWeathers.add(parseJsonWeather(reader));

            return jsonWeathers;
        } finally {
            reader.endArray();
        }
    }

    /**
     * Parse a Json stream and return a JsonWeather object.
     */
    public JsonWeather parseJsonWeather(JsonReader reader)
            throws IOException {

        JsonWeather jsonWeather = new JsonWeather();
        reader.beginObject();

        try {
            while (reader.hasNext()) {
                String name = reader.nextName();
                if (name.equals(JsonWeather.base_JSON)) {
                    jsonWeather.setBase(reader.nextString());

                } else if (name.equals(JsonWeather.cod_JSON)) {
                    jsonWeather.setCod(reader.nextLong());

                } else if (name.equals(JsonWeather.dt_JSON)) {
                    jsonWeather.setDt(reader.nextLong());

                } else if (name.equals(JsonWeather.id_JSON)) {
                    jsonWeather.setId(reader.nextLong());

                } else if (name.equals(JsonWeather.main_JSON)) {
                    Main main = parseMain(reader);
                    jsonWeather.setMain(main);

                } else if (name.equals(JsonWeather.name_JSON)) {
                    jsonWeather.setName(reader.nextString());

                } else if (name.equals(JsonWeather.sys_JSON)) {
                    Sys sys = parseSys(reader);
                    jsonWeather.setSys(sys);

                } else if (name.equals(JsonWeather.weather_JSON)) {
                    if (reader.peek() == JsonToken.BEGIN_ARRAY) {
                        List<Weather> weathers = parseWeathers(reader);
                        jsonWeather.setWeather(weathers);
                    }

                } else if (name.equals(JsonWeather.wind_JSON)) {
                    Wind wind = parseWind(reader);
                    jsonWeather.setWind(wind);

                } else {
                    reader.skipValue();
                    // Log.d(TAG, "weird problem with " + name + " field");

                }
            }
        } finally {
            reader.endObject();
        }
        return jsonWeather;
    }

    /**
     * Parse a Json stream and return a List of Weather objects.
     */
    public List<Weather> parseWeathers(JsonReader reader) throws IOException {
        reader.beginArray();

        try {
            List<Weather> weathers = new ArrayList<Weather>();

            while (reader.hasNext())
                weathers.add(parseWeather(reader));

            return weathers;
        } finally {
            reader.endArray();
        }
    }

    /**
     * Parse a Json stream and return a Weather object.
     */
    public Weather parseWeather(JsonReader reader) throws IOException {

        reader.beginObject();

        Weather weather = new Weather();

        try {
            while (reader.hasNext()) {
                String name = reader.nextName();
                if (name.equals(Weather.description_JSON)) {
                    weather.setDescription(reader.nextString());

                } else if (name.equals(Weather.icon_JSON)) {
                    weather.setIcon(reader.nextString());

                } else if (name.equals(Weather.id_JSON)) {
                    weather.setId(reader.nextLong());

                } else if (name.equals(Weather.main_JSON)) {
                    weather.setMain(reader.nextString());

                } else {
                    reader.skipValue();

                }
            }
        } finally {
            reader.endObject();
        }
        return weather;
    }
    
    /**
     * Parse a Json stream and return a Main Object.
     */
    public Main parseMain(JsonReader reader) 
        throws IOException {
        reader.beginObject();

        Main main = new Main();

        try {
            while (reader.hasNext()) {
                String name = reader.nextName();
                if (name.equals(Main.grndLevel_JSON)) {
                    main.setGrndLevel(reader.nextDouble());

                } else if (name.equals(Main.humidity_JSON)) {
                    main.setHumidity(reader.nextLong());

                } else if (name.equals(Main.pressure_JSON)) {
                    main.setPressure(reader.nextDouble());

                } else if (name.equals(Main.seaLevel_JSON)) {
                    main.setSeaLevel(reader.nextDouble());

                } else if (name.equals(Main.temp_JSON)) {
                    main.setTemp(reader.nextDouble());

                } else if (name.equals(Main.tempMax_JSON)) {
                    main.setTempMax(reader.nextDouble());

                } else if (name.equals(Main.tempMin_JSON)) {
                    main.setTempMin(reader.nextDouble());

                } else {
                    reader.skipValue();

                }
            }
        } finally {
            reader.endObject();
        }
        return main;
    }

    /**
     * Parse a Json stream and return a Wind Object.
     */
    public Wind parseWind(JsonReader reader) throws IOException {
        reader.beginObject();

        Wind wind = new Wind();

        try {
            while (reader.hasNext()) {
                String name = reader.nextName();
                if (name.equals(Wind.deg_JSON)) {
                    wind.setDeg(reader.nextDouble());

                } else if (name.equals(Wind.speed_JSON)) {
                    wind.setSpeed(reader.nextDouble());

                } else {
                    reader.skipValue();

                }
            }
        } finally {
            reader.endObject();
        }
        return wind;
    }

    /**
     * Parse a Json stream and return a Sys Object.
     */
    public Sys parseSys(JsonReader reader) throws IOException {
        reader.beginObject();

        Sys sys = new Sys();

        try {
            while (reader.hasNext()) {
                String name = reader.nextName();
                if (name.equals(Sys.country_JSON)) {
                    sys.setCountry(reader.nextString());

                } else if (name.equals(Sys.message_JSON)) {
                    sys.setMessage(reader.nextDouble());

                } else if (name.equals(Sys.sunrise_JSON)) {
                    sys.setSunrise(reader.nextLong());

                } else if (name.equals(Sys.sunset_JSON)) {
                    sys.setSunset(reader.nextLong());

                } else {
                    reader.skipValue();

                }
            }
        } finally {
            reader.endObject();
        }
        return sys;
    }
}
