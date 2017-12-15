package br.com.marcioikeda.bakingapp.util;

import android.content.Context;
import android.content.res.Configuration;

/**
 * Created by marcio.ikeda on 23/11/2017.
 */

public class Util {

    public static boolean isSW600(Context context) {
        Configuration config = context.getResources().getConfiguration();
        return config.smallestScreenWidthDp >= 600;
    }
}
