package com.tealium.sample.tv;

import android.app.Application;

import com.tealium.library.Tealium;

public final class SampleApp extends Application {

    public static final String TEALIUM_MAIN = "main";

    @Override
    public void onCreate() {
        super.onCreate();

        Tealium.createInstance(
                TEALIUM_MAIN,
                Tealium.Config.create(this, "tealiummobile", "demo", "dev"));
    }
}
