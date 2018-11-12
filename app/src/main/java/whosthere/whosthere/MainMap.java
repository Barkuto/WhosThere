package whosthere.whosthere;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.wrappers.PackageManagerWrapper;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.security.Security;
import java.util.ArrayList;

public class MainMap extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static final String TAG = MainMap.class.getName();
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_REQUEST_CODE = 123;
    private static final float DEFAULT_ZOOM = 15f;

    private Location myLocation;
    private boolean permissionsGranted = false;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    private ArrayList<Friend> friendList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        getLocationPermission();
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        getLocationPermission();

        if (permissionsGranted) {
            getCurrentLocation();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            //mMap.getUiSettings().setMyLocationButtonEnabled(false);
            //mMap.getUiSettings().

            generateFriendsList();

        } else {
            Toast.makeText(MainMap.this, "Unable to load data: Permissions not granted 🤦‍♀️", Toast.LENGTH_LONG).show();
        }

        // Add a marker in Sydney and move the camera
        /*LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/
    }


    private void generateFriendsList() {
        friendList = new ArrayList<Friend>();

        friendList.add(new Friend(new LatLng(38.989886, -76.936306),
                "Bruce", "Wayne", "Batman"));
        friendList.add(new Friend(new LatLng(38.987260, -76.942088),
                "Dick", "Grayson", "Nightwing"));
        friendList.add(new Friend(new LatLng(38.987923, -76.944648),
                "Barbara", "Gordan", "Batgirl"));
        friendList.add(new Friend(new LatLng(38.897392, -77.037002),
                "Tim", "Drake", "Red Robin"));
        friendList.add(new Friend(new LatLng(27.989720, -81.688442),
                "Jason", "Todd", "Red Hood"));
        friendList.add(new Friend(new LatLng(27.987700, 86.925954),
                "Stephanie", "Brown", "Spoiler"));
        friendList.add(new Friend(new LatLng(39.286132, -76.608427),
                "Alfred", "Pennyworth", "The Butler"));
        friendList.add(new Friend(new LatLng(40.772290, -73.980208),
                "Damian", "Wayne", "Batman"));
        friendList.add(new Friend(new LatLng(37.421716, -122.084344),
                "Selina", "Kyle", "Catwoman"));
        friendList.add(new Friend(new LatLng(42.946947, -122.097894),
                "Katherine", "Kane", "Batwoman"));

        mapFriends(friendList);
    }

    private void mapFriends(ArrayList<Friend> friendList) {
        for (Friend friend : friendList) {
            Location friendLocation = new Location("");
            friendLocation.setLatitude(friend.getLocation().latitude);
            friendLocation.setLongitude(friend.getLocation().longitude);
            friend.setDistanceAway(myLocation.distanceTo(friendLocation));
            LatLng location = friend.getLocation();
            mMap.addMarker(new MarkerOptions().position(location).title(friend.getUserName()));
            //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        }
    }


    /**
     * Move focus of the map top a given LatLng location.
     */
    private void moveCamera(LatLng latLng, float zoom) {
        Log.d(TAG, "Moving camera to (" + latLng.latitude + "," + latLng.longitude + ")");
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }


    /**
     * Get the user's current location if location permissions were granted successfully.
     * A mtoast message will be printed if not.
     */
    private void getCurrentLocation() {
        Log.d(TAG, "getDeviceLocation called: getting the user's current location");
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            if (permissionsGranted) {
                Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Location was found!");
                            Location currentLocation = (Location) task.getResult();
                            myLocation = currentLocation;
                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), DEFAULT_ZOOM);
                        } else {
                            Log.d(TAG, "Location was NOT found!");
                            Toast.makeText(MainMap.this, "Unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e ) {
            Log.e(TAG, "SecurityException: " + e.getMessage());
        }
    }


    /**
     * Helper methods to get and set required location permissions.
     * Map will display if not, however information will not be shown.
     */
    private void getLocationPermission() {
        String[] permissions = {FINE_LOCATION, COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                permissionsGranted = true;
            } else {
                ActivityCompat.requestPermissions(this, permissions, LOCATION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this, permissions, LOCATION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsGranted = false;

        switch(requestCode) {
            case LOCATION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            permissionsGranted = false;
                            return;
                        }
                    }
                    permissionsGranted = true;
                }
            }
        }
    }
}
