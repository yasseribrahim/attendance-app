package com.student.attendance.app.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Looper;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.List;

public class LocationService extends LocationCallback {
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private Location location;
    private LocationServiceCallback callback;

    @Override
    public void onLocationAvailability(@NonNull LocationAvailability locationAvailability) {
        super.onLocationAvailability(locationAvailability);
    }

    @Override
    public void onLocationResult(@NonNull LocationResult locationResult) {
        location = locationResult.getLastLocation();
        if (callback != null) {
            callback.onSuccess(location);
        }
    }

    public void init(Activity activity) {
        fusedLocationProviderClient = new FusedLocationProviderClient(activity);
        locationRequest = new LocationRequest().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY).setInterval(1000).setFastestInterval(1000).setNumUpdates(1);
    }

    private void checkLocationStatusAndGetLocation(Activity activity) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.getSettingsClient(activity).checkLocationSettings(new LocationSettingsRequest.Builder().addLocationRequest(locationRequest).setAlwaysShow(true).build()).addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
                @SuppressLint("MissingPermission")
                @Override
                public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                    try {
                        task.getResult(ApiException.class);
                        fusedLocationProviderClient.requestLocationUpdates(locationRequest, LocationService.this, Looper.getMainLooper());
                    } catch (ApiException exception) {
                        switch (exception.getStatusCode()) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                try {
                                    ((ResolvableApiException) exception).startResolutionForResult(activity, 7025);
                                } catch (Exception ex) {
                                    promptShowLocation(activity);
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                promptShowLocation(activity);
                                break;
                        }
                    }
                }
            });
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION)) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new AlertDialog.Builder(activity)
                            .setMessage("To continue, allow the device to use location, witch uses Google's Location Service")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    activity.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (callback != null) {
                                        callback.onError();
                                    }
                                }
                            }).show();
                }
            });
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 7024);
        }
    }

    private void promptShowLocation(Activity activity) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new AlertDialog.Builder(activity)
                        .setMessage("To continue, allow the device to use location, witch uses Google's Location Service")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                activity.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (callback != null) {
                                    callback.onError();
                                }
                            }
                        }).show();
            }
        });
    }

    public void onRequestPermissionsResult(Activity activity, int requestCode, List<Integer> grantResults) {
        if (requestCode == 7024) {
            if (!grantResults.isEmpty() && grantResults.get(0) == PackageManager.PERMISSION_GRANTED) {
                checkLocationStatusAndGetLocation(activity);
            } else {
                if (callback != null) {
                    callback.onError();
                }
            }
        }
    }

    void onActivityResult(Activity activity, int requestCode, int resultCode) {
        if (requestCode == 7025) {
            if (resultCode == Activity.RESULT_OK) {
                checkLocationStatusAndGetLocation(activity);
            } else {
                if (callback != null) {
                    callback.onError();
                }
            }
        }
    }

    public void getLocation(Activity activity, LocationServiceCallback callback) {
        this.callback = callback;
        checkLocationStatusAndGetLocation(activity);
    }

    public interface LocationServiceCallback {
        void onSuccess(Location location);

        void onError();
    }
}
