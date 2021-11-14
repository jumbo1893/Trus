package com.jumbo.trus;


import android.Manifest;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import java.util.Map;

public class PermissionHelper {

    private Fragment fragment;
    private IPermission iPermission;

    public PermissionHelper(Fragment fragment, IPermission iPermission) {
        this.fragment = fragment;
        this.iPermission = iPermission;
    }

    private ActivityResultContracts.RequestMultiplePermissions requestMultiplePermissionsContract;
    private ActivityResultLauncher<String[]> multiplePermissionActivityResultLauncher;

    public void startMultiplePermissionRequest(final String[] manifestPermission) {
        requestMultiplePermissionsContract = new ActivityResultContracts.RequestMultiplePermissions();
        multiplePermissionActivityResultLauncher = fragment.registerForActivityResult(requestMultiplePermissionsContract, new ActivityResultCallback<Map<String, Boolean>>() {
            @Override
            public void onActivityResult(Map<String, Boolean> isGranted) {
                if (isGranted.containsValue(false)) {
                    //iPermission.onGranted(false);
                    Log.d("PERMISSIONS", "At least one of the permissions was not granted, launching again...");
                    //multiplePermissionActivityResultLauncher.launch(manifestPermission);
                }
            }
        });

        askPermissions(manifestPermission);
    }

    private void askPermissions(String[] manifestPermission) {
        if (!hasPermissions(manifestPermission)) {
            Log.d("PERMISSIONS", "Launching multiple contract permission launcher for ALL required permissions");
            multiplePermissionActivityResultLauncher.launch(manifestPermission);

        } else {
            Log.d("PERMISSIONS", "All permissions are already granted");
        }
    }

    private boolean hasPermissions(String[] permissions) {
        if (permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(fragment.getContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                    Log.d("PERMISSIONS", "Permission is not granted: " + permission);
                    return false;
                }
                Log.d("PERMISSIONS", "Permission already granted: " + permission);
            }
            return true;
        }
        return false;
    }
}
