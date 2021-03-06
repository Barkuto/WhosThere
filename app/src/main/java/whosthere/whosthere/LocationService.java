package whosthere.whosthere;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class LocationService extends Service {
    private static final String TAG = "LOCATION SERVICE";
    private LocationManager mLocationManager = null;
    private int locationInterval = 360;
    private static final float LOCATION_DISTANCE = 0;

    public static final String ACTION_LOCATION_BROADCAST = LocationService.class.getName() + "LocationBroadcast";
    public static final String EXTRA_LATITUDE = "extra_latitude";
    public static final String EXTRA_LONGITUDE = "extra_longitude";

    private FirebaseAuth mAuth;
    private FirebaseFirestore mDatabase;
    private FirebaseUser mUser;

    private class LocationListener implements android.location.LocationListener {
        Location mLastLocation;

        LocationListener(String provider) {
            Log.e(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location) {
            // Log.e(TAG, "onLocationChanged: (" + location.getLatitude() + ", " + location.getLongitude() + ") --> " + location.getProvider());
            mLastLocation.set(location);

            Map<String, Object> data = new HashMap<>();
            data.put("lat", mLastLocation.getLatitude());
            data.put("lng", mLastLocation.getLongitude());

            if (mUser != null) {
                mDatabase.collection("users").document(mUser.getUid()).set(data, SetOptions.merge());
                Log.e(TAG, "onLocationChanged: (" + mLastLocation.getLatitude() + ", " + mLastLocation.getLongitude() + ")  ---->" + locationInterval);
            }

            /*if (mLastLocation != null) {
                Map<String, Object> data = new HashMap<>();
                data.put("lat", mLastLocation.getLatitude());
                data.put("lng", mLastLocation.getLongitude());
                mDatabase.collection("users").document(mUser.getUid()).set(data, SetOptions.merge());


            } else {
                Map<String, Object> data = new HashMap<>();
                data.put("lat", location.getLatitude());
                data.put("lng", location.getLongitude());
                mDatabase.collection("users").document(mUser.getUid()).set(data, SetOptions.merge());
            }*/
        }

        @Override
        public void onProviderDisabled(String provider) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.e(TAG, "onStatusChanged: " + provider + " ---> Interval = " + locationInterval);
        }
    }

    LocationListener[] mLocationListeners = new LocationListener[] {
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };

    private void sendMessageToUI(String lat, String lng) {
        Intent intent = new Intent(ACTION_LOCATION_BROADCAST);
        intent.putExtra(EXTRA_LATITUDE, lat);
        intent.putExtra(EXTRA_LONGITUDE, lng);

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Log.e(TAG, "onStartCommand");

        this.mAuth = FirebaseAuth.getInstance();
        this.mDatabase = FirebaseFirestore.getInstance();
        this.mUser = mAuth.getCurrentUser();

        SharedPreferences myPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        int interval = Integer.parseInt(myPrefs.getString("FREQ", "360"));
        locationInterval = interval;

        if (intent != null) {
            locationInterval = intent.getIntExtra("interval", 360);
            Log.e(TAG, "Changed interval to " + locationInterval);
        } else {
            locationInterval = 1;
        }

        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
        //return START_REDELIVER_INTENT;
    }

    @Override
    public void onCreate() {
        Log.e(TAG, "onCreate");
        initializeLocationManager();

        try {
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                    locationInterval, LOCATION_DISTANCE, mLocationListeners[1]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }
        try {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    locationInterval, LOCATION_DISTANCE, mLocationListeners[0]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");
        super.onDestroy();

        if (mLocationManager != null) {
            for (LocationListener mLocationListener : mLocationListeners) {
                try {
                    mLocationManager.removeUpdates(mLocationListener);
                } catch (Exception ex) {
                    Log.i(TAG, "fail to remove location listners, ignore", ex);
                }
            }
        }
    }

    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager");

        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }
}