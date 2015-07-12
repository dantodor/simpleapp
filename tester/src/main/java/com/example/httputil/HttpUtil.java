package com.example.httputil;

import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Request;
import com.ning.http.client.RequestBuilder;
import com.ning.http.client.Response;

import java.util.concurrent.Future;

/**
 * A helper class for the Worker actor.
 * It wraps around the ASyncHttpClient from NING
 * We only need the POST and GET logic
 *
 * @author  Dan Todor
 * @version 1.0
 * @since   2015-07-12
 */public class HttpUtil {

    public void post(String url, String json) {
        final AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        Request req = new RequestBuilder("POST")
                .setUrl(url)
                .setBody(json)
                .addHeader("Content-Type", "application/json")
                .build();
        Future<Response> f = asyncHttpClient.prepareRequest(req).execute();
        try {
            while (!f.isDone())
                Thread.sleep(100);
        } catch (Exception e) {
            e.printStackTrace();
        }
        asyncHttpClient.close();
        while (!asyncHttpClient.isClosed()) {

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void get(String url) {
        final AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        Request req = new RequestBuilder("GET")
                .setUrl(url)
                .addHeader("Content-Type", "application/json")
                .build();
        Future<Response> f = asyncHttpClient.prepareRequest(req).execute();
        try {
            while (!f.isDone())
                Thread.sleep(100);
        } catch (Exception e) {
            e.printStackTrace();
        }
        asyncHttpClient.close();
        while (!asyncHttpClient.isClosed()) {

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
