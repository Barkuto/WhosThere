package whosthere.whosthere;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;


public class MapsFragment extends Fragment implements OnMapReadyCallback {

    GoogleMap mMap;
    SupportMapFragment mapFragment;
    private static final String TAG = MapsActivity.class.getName();
    private boolean mLocationPermissionGranted = false;
    public static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final float DEFAULT_ZOOM = 15f;

    private Location myLocation;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    private LocationRequest locationRequest;
    private LocationCallback locationCallback;

    private static ArrayList<Friend> friendList = new ArrayList<>();


    public MapsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_maps, container, false);


        /*mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(20 * 1000);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        //wayLatitude = location.getLatitude();
                        //wayLongitude = location.getLongitude();
                        myLocation = location;
                        //txtLocation.setText(String.format(Locale.US, "%s -- %s", wayLatitude, wayLongitude));
                    }
                }
            }
        };*/

        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapFragment = (SupportMapFragment)getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        getLocationPermission();


        /*
        Button button = getActivity().findViewById(R.id.butt);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), profile_page_arthur.class);
                getActivity().startActivity(intent);
            }
        });*/
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(false);

        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getActivity(), R.raw.style_json_bluegreen));
            //boolean success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getActivity(), R.raw.style_json_retro));
            //boolean success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getActivity(), R.raw.style_json_aubergine));

            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }

        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(-37.813, 144.962))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));

        // Set custom window for map location marker
        if (mMap != null) {
            mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                @Override
                public View getInfoWindow(Marker marker) {
                    return null;
                }

                @Override
                public View getInfoContents(Marker marker) {
                    //updateFriendsList();
                    View row = getLayoutInflater().inflate(R.layout.custom_info_window, null);
                    TextView userName = row.findViewById(R.id.snippetUsername);
                    TextView fullName = row.findViewById(R.id.snippetFullName);
                    TextView distance = row.findViewById(R.id.distanceAway);

                    //LatLng location = marker.getPosition();
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


        /*LatLng test = new LatLng(38.989886, -76.936306);
        MarkerOptions option = new MarkerOptions();
        option.position(test).title("Test Location");
        mMap.addMarker(option);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(test));*/

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();
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
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        try {
            if (mLocationPermissionGranted) {
                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful() && location != null) {
                            Log.d(TAG, "Location was found!");
                            Location currentLocation = (Location) task.getResult();
                            if(currentLocation != null){
                                myLocation = currentLocation;
                                moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), DEFAULT_ZOOM);
                                updateFriendsList();
                            } else {

                            }
                        } else {
                            Log.d(TAG, "Location was NOT found!");
                            Toast.makeText(getActivity(), "Unable to get current location", Toast.LENGTH_SHORT).show();
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
        if (ContextCompat.checkSelfPermission(this.getActivity(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this.getActivity(),
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
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void updateFriendsList() {
/*        friendList = new ArrayList<>();

        friendList.add(new Friend(new LatLng(38.989886, -76.936306), "Bruce", "Wayne", "Batman"));
        friendList.add(new Friend(new LatLng(38.987260, -76.942088),"Dick", "Grayson", "Nightwing"));
        friendList.add(new Friend(new LatLng(38.987923, -76.944648),"Barbara", "Gordan", "Batgirl"));
        friendList.add(new Friend(new LatLng(38.897392, -77.037002),"Tim", "Drake", "Red Robin"));
        friendList.add(new Friend(new LatLng(27.989720, -81.688442),"Jason", "Todd", "Red Hood"));
        friendList.add(new Friend(new LatLng(27.987700, 86.925954),"Stephanie", "Brown", "Spoiler"));
        friendList.add(new Friend(new LatLng(39.286132, -76.608427),"Alfred", "Pennyworth", "The Butler"));
        friendList.add(new Friend(new LatLng(40.772290, -73.980208), "Damian", "Wayne", "Batman"));
        friendList.add(new Friend(new LatLng(37.421716, -122.084344),"Selina", "Kyle", "Catwoman"));
        friendList.add(new Friend(new LatLng(42.946947, -122.097894),"Katherine", "Kane", "Batwoman"));*/

        mapFriends(friendList);
    }

    private void mapFriends(ArrayList<Friend> friendList) {
        for (Friend friend : friendList) {
            Location friendLocation = new Location("");
            friendLocation.setLatitude(friend.getLocation().latitude);
            friendLocation.setLongitude(friend.getLocation().longitude);
            friend.setDistanceAway(myLocation.distanceTo(friendLocation));
            LatLng location = friend.getLocation();
            mMap.addMarker(new MarkerOptions().position(location).title(friend.getUserName())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
        }
    }

    private void updateMyLocation(Location myLocation) {
        double lat = myLocation.getLatitude();
        double lng = myLocation.getLongitude();
    }

}
