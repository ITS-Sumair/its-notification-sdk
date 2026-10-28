package com.its.notificationsdk;

import android.content.Context;
import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;

/**
 * Entry point for the ITS Notification SDK (v2).
 */
public final class NotificationSdk {

    private static final String TAG = "NotificationSdk";

    // From Firebase Console > Project Settings > General > Your apps (Android app).
    // These identify ITS's own Firebase project for this SDK — not the host app's.
    private static final String FIREBASE_APPLICATION_ID = "1:970314238698:android:c7e8a9ade70f48b8f5e593";
    private static final String FIREBASE_PROJECT_ID = "notification-sdk-d0a43";
    private static final String FIREBASE_GCM_SENDER_ID = "970314238698";

    private NotificationSdk() {
    }

    /**
     * Call once, early (e.g. your launcher Activity's onCreate). Initializes this
     * SDK's own Firebase project and fetches an FCM token for this device.
     *
     * <p>Unlike v1, this SDK does NOT reuse a host app's existing default
     * FirebaseApp. This SDK's backend is tied to its own dedicated Firebase
     * project, so a device token scoped to a different project would never
     * actually receive pushes from it. If the host app already has a default
     * FirebaseApp (its own Firebase usage), initialization fails loudly instead
     * of silently registering a token that will never work.
     *
     * @param context   any Context
     * @param firebaseApiKey Firebase Web API key for ITS's project (ITS will give you this)
     * @param clientId  your client/tenant identifier, issued by ITS
     * @param apiKey    your API key for ITS's backend, issued by ITS
     */
    public static void initialize(Context context, String firebaseApiKey, String clientId, String apiKey) {
        Context appContext = context.getApplicationContext();

        if (!FirebaseApp.getApps(appContext).isEmpty()) {
            throw new IllegalStateException(
                    "NotificationSdk.initialize() was called but this app already has a "
                            + "default FirebaseApp. This SDK requires sole ownership of the "
                            + "default FirebaseApp because its backend only accepts tokens "
                            + "scoped to its own Firebase project. If your app already uses "
                            + "Firebase for something else, talk to your ITS contact before "
                            + "integrating this SDK.");
        }

        FirebaseOptions firebaseOptions = new FirebaseOptions.Builder()
                .setApplicationId(FIREBASE_APPLICATION_ID)
                .setApiKey(firebaseApiKey)
                .setProjectId(FIREBASE_PROJECT_ID)
                .setGcmSenderId(FIREBASE_GCM_SENDER_ID)
                .build();
        FirebaseApp.initializeApp(appContext, firebaseOptions);

        fetchFcmToken(clientId, apiKey);
    }

    private static void fetchFcmToken(String clientId, String apiKey) {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.e(TAG, "Fetching FCM token failed", task.getException());
                        return;
                    }
                    String token = task.getResult();
                    // TODO: call the new backend's device-registration endpoint with
                    // (token, clientId, apiKey) once that API contract is finalized.
                });
    }
}
