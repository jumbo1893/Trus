package com.jumbo.trus.update;

import android.content.Context;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import java.util.HashMap;
import java.util.Map;

public class ForceUpdateChecker {

    private static final String TAG = ForceUpdateChecker.class.getSimpleName();

    public static final String KEY_UPDATE_REQUIRED = "force_update_required";
    public static final String KEY_CURRENT_VERSION = "force_update_current_version";
    public static final String KEY_UPDATE_URL = "force_update_store_url";

    private OnUpdateNeededListener onUpdateNeededListener;
    private Context context;

    public interface OnUpdateNeededListener {
        void onUpdateNeeded(String updateUrl, String version, boolean bool);
    }

    public static Builder with(@NonNull Context context) {
        return new Builder(context);
    }

    public ForceUpdateChecker(@NonNull Context context,
                              OnUpdateNeededListener onUpdateNeededListener) {
        this.context = context;
        this.onUpdateNeededListener = onUpdateNeededListener;
    }

    public void check() {
        final FirebaseRemoteConfig remoteConfig = FirebaseRemoteConfig.getInstance();
        // long cacheExpiration = 12 * 60 * 60; // fetch every 12 hours
        // set in-app defaults
        Map<String, Object> remoteConfigDefaults = new HashMap();
        final String updateUrl = remoteConfig.getString(KEY_UPDATE_URL);
        remoteConfigDefaults.put(KEY_UPDATE_REQUIRED, false);
        remoteConfigDefaults.put(KEY_CURRENT_VERSION, "1.0.0");
        remoteConfig.setDefaultsAsync(remoteConfigDefaults);
        remoteConfig.fetch(0)

                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "remote config is fetched.");
                            remoteConfig.activate();
                        }
                        Log.d(TAG, "onComplete: config: " + remoteConfig.getBoolean(KEY_UPDATE_REQUIRED) + "current version config: " + remoteConfig.getString(KEY_CURRENT_VERSION)
                                + "app version " + getAppVersion(context));
                        if (remoteConfig.getBoolean(KEY_UPDATE_REQUIRED)) {
                            String currentVersion = remoteConfig.getString(KEY_CURRENT_VERSION);
                            String appVersion = getAppVersion(context);
                            if (!TextUtils.equals(currentVersion, appVersion)
                                    && onUpdateNeededListener != null) {
                                onUpdateNeededListener.onUpdateNeeded(updateUrl, currentVersion, true);
                                return;
                            }
                        }
                        if (onUpdateNeededListener != null) {
                            onUpdateNeededListener.onUpdateNeeded("", "", false);
                        }
                    }
                });
    }

    private String getAppVersion(Context context) {
        String result = "";

        try {
            result = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0)
                    .versionName;
            result = result.replaceAll("[a-zA-Z]|-", "");
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, e.getMessage());
        }

        return result;
    }

    public static class Builder {

        private Context context;
        private OnUpdateNeededListener onUpdateNeededListener;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder onUpdateNeeded(OnUpdateNeededListener onUpdateNeededListener) {
            this.onUpdateNeededListener = onUpdateNeededListener;
            return this;
        }

        public ForceUpdateChecker build() {
            return new ForceUpdateChecker(context, onUpdateNeededListener);
        }

        public ForceUpdateChecker check() {
            ForceUpdateChecker forceUpdateChecker = build();
            forceUpdateChecker.check();

            return forceUpdateChecker;
        }
    }
}