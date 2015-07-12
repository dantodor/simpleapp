package com.example.app.utils;

import com.google.gson.Gson;
import spark.ResponseTransformer;

/**
 * A simple json<->java object mapper
 * needed by the Spark REST library
 *
 * @author  Dan Todor
 * @version 1.0
 * @since   2015-07-12
 */
public class JsonTransformer implements ResponseTransformer {

    private Gson gson = new Gson();

    @Override
    public String render(Object model) {
        String result="";
        try {
            result = gson.toJson(model);
        } catch (Exception e){
            result = "{}"; // protect against parsing failures
        }
        return result;
    }

}