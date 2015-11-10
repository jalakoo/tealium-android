package com.tealium.example.helper;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;
import android.webkit.WebView;

import com.tealium.example.BuildConfig;
import com.tealium.library.Tealium;

import java.util.Map;

public final class TealiumHelper {

    private final static String KEY_TEAL_INIT_COUNT = "tealium_init_count";
    private final static String TAG = "TealiumHelper";
    public static final String TEALIUM_MAIN = "main";


    // Not instantiatable.
    private TealiumHelper() {
    }

    @SuppressLint("NewApi")
    public static void initialize(Application application) {
        Log.i(TAG, "initialize(" + application.getClass().getSimpleName() + ")");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && BuildConfig.DEBUG) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

        Tealium.Config config = Tealium.Config.create(application, "tealiummobile", "demo", "dev");
        Tealium instance = Tealium.createInstance(TEALIUM_MAIN, config);

        SharedPreferences sp = instance.getDataSources().getPersistentDataSources();
        sp.edit().putInt(KEY_TEAL_INIT_COUNT, sp.getInt(KEY_TEAL_INIT_COUNT, 0) + 1).commit();
    }

    public static void trackView(String viewName, Map<String, String> data) {
        Tealium.getInstance(TEALIUM_MAIN).trackView(viewName, data);
    }

    public static void trackEvent(String eventName, Map<String, String> data) {
        Tealium.getInstance(TEALIUM_MAIN).trackEvent(eventName, data);
    }
}
