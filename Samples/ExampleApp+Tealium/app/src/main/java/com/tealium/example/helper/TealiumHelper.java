package com.tealium.example.helper;

import android.app.Application;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.WebView;

import com.tealium.example.BuildConfig;
import com.tealium.internal.data.Dispatch;
import com.tealium.internal.listeners.WebViewCreatedListener;
import com.tealium.internal.listeners.WebViewLoadListener;
import com.tealium.internal.tagbridge.RemoteCommand;
import com.tealium.library.DispatchValidator;
import com.tealium.library.Tealium;

import java.util.HashMap;
import java.util.Map;

/**
 * This class abstracts interaction with the Tealium library and simplifies comprehension
 */
public final class TealiumHelper {

    private final static String KEY_TEALIUM_INIT_COUNT = "tealium_init_count";
    private final static String KEY_TEALIUM_INITIALIZED = "tealium_initialized";
    private final static String TAG = "TealiumHelper";

    // Identifier for the main Tealium instance
    public static final String TEALIUM_MAIN = "main";

    // Not instantiatable.
    private TealiumHelper() {
    }

    public static void initialize(Application application) {
        Log.i(TAG, "initialize(" + application.getClass().getSimpleName() + ")");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && BuildConfig.DEBUG) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

        final Tealium.Config config = Tealium.Config.create(application, "tealiummobile", "demo", "dev");

        config.getEventListeners().add(new WebViewCreatedListener() {
            @Override
            public void onWebViewCreated(WebView webView) {
                final CookieManager mgr = CookieManager.getInstance();

                // Accept all cookies
                mgr.setAcceptCookie(true);

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mgr.setAcceptThirdPartyCookies(webView, true);
                }

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
                    CookieManager.setAcceptFileSchemeCookies(true);
                }

                Log.d(TAG, "WebView " + webView + " created and cookies enabled.");
            }
        });

        // Get the WebView with UTag loaded
        config.getEventListeners().add(new WebViewLoadListener() {
            @Override
            public void onWebViewLoad(WebView webView, boolean success) {
                Log.d(TAG, "WebView " + webView +
                        (success ? " loaded successfully" : "failed to load"));
            }
        });

        // Control how the library treats views/links
        config.getDispatchValidators().add(new DispatchValidator() {

            @Override
            protected boolean shouldDrop(Dispatch dispatch) {
                return super.shouldDrop(dispatch);
            }

            @Override
            protected boolean shouldQueue(Dispatch dispatch, boolean shouldQueue) {
                return super.shouldQueue(dispatch, shouldQueue);
            }
        });


        // Enhanced integrations
        config.getRemoteCommands().add(new RemoteCommand("logger", "Writes out messages to LogCat.") {
            @Override
            protected void onInvoke(Response response) throws Exception {
                final String message = response.getRequestPayload()
                        .optString("message", "no_message");
                Log.i(TAG, "RemoteCommand Message: " + message);
            }
        });

        final Tealium instance = Tealium.createInstance(TEALIUM_MAIN, config);

        // Use tealium.getDataSources().getPersistentDataSources() to set/modify lifetime values
        SharedPreferences sp = instance.getDataSources().getPersistentDataSources();
        sp.edit().putInt(KEY_TEALIUM_INIT_COUNT, sp.getInt(KEY_TEALIUM_INIT_COUNT, 0) + 1).commit();

        // Use tealium.getDataSources().getVolatileDataSources() to set/modify runtime only values
        instance.getDataSources().getVolatileDataSources()
                .put(KEY_TEALIUM_INITIALIZED, System.currentTimeMillis());

        // Adding event-specific data
        final Map<String, Object> data = new HashMap<>(2);
        data.put("logged_in", false);
        data.put("visitor_status", new String[]{"new_user", "unregistered"});

        instance.trackEvent("initialization", data);
    }

    public static void trackView(String viewName, Map<String, String> data) {
        Tealium.getInstance(TEALIUM_MAIN).trackView(viewName, data);
    }

    public static void trackEvent(String eventName, Map<String, String> data) {
        Tealium.getInstance(TEALIUM_MAIN).trackEvent(eventName, data);
    }
}
