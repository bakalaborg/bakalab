package org.bakalab.app.interfaces;

import android.content.Context;

import org.bakalab.app.utils.BakaTools;

import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

public class Api {

    private static Api instance;
    private static BakalariAPI bakalariAPI;
    private static SkolyAPI skolyAPI;

    public static Api getInstance(Context context) {
        if (instance == null) {
            instance = new Api(context);
        }
        return instance;
    }

    private Api(Context context) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BakaTools.getUrl(context))
                .addConverterFactory(SimpleXmlConverterFactory.createNonStrict())
                .build();

        bakalariAPI = retrofit.create(BakalariAPI.class);
        skolyAPI = retrofit.create(SkolyAPI.class);
    }

    public BakalariAPI getBakalariAPI() {
        return bakalariAPI;
    }

    public SkolyAPI getSkolyAPI() {
        return skolyAPI;
    }
}