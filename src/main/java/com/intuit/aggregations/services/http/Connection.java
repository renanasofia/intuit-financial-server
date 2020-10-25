package com.intuit.aggregations.services.http;

import java.io.IOException;
import java.net.URL;
import java.net.HttpURLConnection;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.lang.StringBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Http Connection Helper
 */
public class Connection {

    private static final Logger logger = LoggerFactory.getLogger(Connection.class);

    public Connection() {
    }

    public static HttpResponse performHttpGet(String urlToRead) throws Exception {
        URL url = new URL(urlToRead);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        return fetchResponse(conn);
    }

    public HttpResponse call(String urlString){
        HttpResponse output = new HttpResponse();
        try{
            output = performHttpGet(urlString);
            logger.debug("RESPONSE BODY:\n{}", output.getResponse());
        } catch (Exception error) {
            output.setSuccess(false);
            output.setResponse(error.getMessage());
            logger.error("Failed to complete HTTP request", error.getCause());
        }
        return output;
    }


    public static HttpResponse fetchResponse(HttpURLConnection connection) throws Exception{
        StringBuilder response = new StringBuilder();

        boolean requestSuccess = true;
        InputStream inputStream = connection.getInputStream();

        if (connection.getResponseCode() >= HttpURLConnection.HTTP_BAD_REQUEST) {
            /* error from server */
            requestSuccess = false;
            inputStream = connection.getErrorStream();
        }

        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");

        String responseLine = null;
        try(BufferedReader br = new BufferedReader(inputStreamReader)) {
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
        } catch (IOException ex) {
            logger.error("An IOException was caught: ", ex);
        } catch (Exception e) {
            logger.error("Failed to parse input", e);
        }

        return new HttpResponse(requestSuccess, response.toString());
    }

}
