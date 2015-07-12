package com.example.app.helpers;

import java.util.HashMap;
import java.util.Map;

/**
 * The Index actor helper class
 *
 * @author  Dan Todor
 * @version 1.0
 * @since   2015-07-12
 */
public class IndexActorHelper {

    public double calculateIndex (HashMap<String, Double> spotValues) {

        double seed = 1.0d;
        int pow = 0;
        for (Map.Entry<String,Double> spot : spotValues.entrySet()) {
            //add spot to calculation only if it's not0
            //due to the way Java stores doubles, we need to take an approximation
            if (spot.getValue()>0.00001d) {
                seed = seed * spot.getValue();
                pow++;
            }
        }
        return Math.pow(seed, 1.0 / (double) pow);

    }
}
