package com.terrymoreii.phishradio.DAO;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;

/**
 * Created by tmoore on 7/21/14.
 */
public class PhishInDAO {

        private final String LOG_TAG = PhishInDAO.class.getSimpleName();


        public String getData(String urlStr){

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;


        // Will contain the raw JSON response as a string.
        String JsonStr = null;

        try {
            // Construct the URL for the OpenWeatherMap query
            // Possible parameters are avaiable at OWM's forecast API page, at
            // http://openweathermap.org/API#forecast
            URL url = new URL(urlStr);

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoOutput(false);
            urlConnection.setRequestProperty("Accept", "application/json");

            urlConnection.connect();

            Log.d(LOG_TAG, "Response Code: " + urlConnection.getResponseCode());

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            JsonStr = buffer.toString();

            return JsonStr;

        }

        catch(
        SocketTimeoutException se
        )

        {
            Log.e(LOG_TAG, "SocketTimeoutException", se);
            return null;
        }

        catch(
        IOException e
        )

        {
            Log.e(LOG_TAG, "Error ", e);
            Log.e(LOG_TAG, "IO Exception");

            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.
            return null;
        }

        finally

        {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
    }

}
