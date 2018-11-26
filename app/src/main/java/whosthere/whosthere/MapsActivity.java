package whosthere.whosthere;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static final String TAG = MapsActivity.class.getName();
    private boolean mLocationPermissionGranted = false;
    public static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final float DEFAULT_ZOOM = 15f;

    private Location myLocation;
    private FusedLocationProviderClient mFusedLocationProviderClient;


    private ArrayList<Friend> friendList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
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
    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (mMap != null) {
            mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                @Override
                public View getInfoWindow(Marker marker) {
                    return null;
                }

                @Override
                public View getInfoContents(Marker marker) {
                    //generateFriendsList();
                    View row = getLayoutInflater().inflate(R.layout.custom_info_window, null);
                    TextView userName = row.findViewById(R.id.snippetUsername);
                    TextView fullName = row.findViewById(R.id.snippetFullName);
                    TextView distance = row.findViewById(R.id.distanceAway);

                    LatLng location = marker.getPosition();
                    String markerTitle = marker.getTitle();

                    Friend currentFriend = null;
                    for (Friend friend : friendList) {
                        if (friend.getUserName().equals(markerTitle)) {
                            currentFriend = friend;
                            break;
                        }
                    }

                    if (currentFriend != null) {
                        userName.setText("@" + marker.getTitle());
                        fullName.setText(currentFriend.getFullName());
                        distance.setText((Math.round((currentFriend.getDistanceAway() * 0.00062137) * 100.0) / 100.0) + "");
                    }

                    return row;
                }
            });
        }

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();

        /* if (mLocationPermissionGranted) {
            getCurrentLocation();
            mMap.setMyLocationEnabled(true);
            generateFriendsList();
        } else {
            // Add a marker in Sydney and move the camera
            LatLng csic = new LatLng(38.989914, -76.936259);
            mMap.addMarker(new MarkerOptions().position(csic).title("Marker in CSIC"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(csic));
        }*/

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
    private void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation called: getting the user's current location");
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            if (mLocationPermissionGranted) {
                Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Location was found!");
                            Location currentLocation = (Location) task.getResult();
                            myLocation = currentLocation;
                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), DEFAULT_ZOOM);
                            generateFriendsList();
                        } else {
                            Log.d(TAG, "Location was NOT found!");
                            Toast.makeText(MapsActivity.this, "Unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e ) {
            Log.e(TAG, "SecurityException: " + e.getMessage());
        }
    }



    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }


    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                //mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }





    private void generateFriendsList() {
        friendList = new ArrayList<>();

        friendList.add(new Friend(new LatLng(38.989886, -76.936306), "Bruce", "Wayne", "Batman"));
        friendList.add(new Friend(new LatLng(38.987260, -76.942088),"Dick", "Grayson", "Nightwing"));
        friendList.add(new Friend(new LatLng(38.987923, -76.944648),"Barbara", "Gordan", "Batgirl"));
        friendList.add(new Friend(new LatLng(38.897392, -77.037002),"Tim", "Drake", "Red Robin"));
        friendList.add(new Friend(new LatLng(27.989720, -81.688442),"Jason", "Todd", "Red Hood"));
        friendList.add(new Friend(new LatLng(27.987700, 86.925954),"Stephanie", "Brown", "Spoiler"));
        friendList.add(new Friend(new LatLng(39.286132, -76.608427),"Alfred", "Pennyworth", "The Butler"));
        friendList.add(new Friend(new LatLng(40.772290, -73.980208), "Damian", "Wayne", "Batman"));
        friendList.add(new Friend(new LatLng(37.421716, -122.084344),"Selina", "Kyle", "Catwoman"));
        friendList.add(new Friend(new LatLng(42.946947, -122.097894),"Katherine", "Kane", "Batwoman"));

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
        }
    }




}
