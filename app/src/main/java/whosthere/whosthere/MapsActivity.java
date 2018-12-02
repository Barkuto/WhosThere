package whosthere.whosthere;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
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
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static final String TAG = MapsActivity.class.getName();
    private boolean mLocationPermissionGranted = false;
    public static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final float DEFAULT_ZOOM = 15f;

    private Location myLocation;
    private DrawerLayout drawer;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    private static ArrayList<Friend> friendList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.open, R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        getLocationPermission();

        /*
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Profile profile = new Profile();
                    profile.setEmail(ds.child("johnnyn1261").getValue(Profile.class).getEmail());
                    profile.setFirstName(ds.child("johnnyn1261").getValue(Profile.class).getFirstName());
                    profile.setFriends(ds.child("johnnyn1261").getValue(Profile.class).getFriends());
                    profile.setLastName(ds.child("johnnyn1261").getValue(Profile.class).getLastName());
                    profile.setLatitude(ds.child("johnnyn1261").getValue(Profile.class).getLatitude());
                    profile.setLongitude(ds.child("johnnyn1261").getValue(Profile.class).getLongitude());
                    profile.setPic(ds.child("johnnyn1261").getValue(Profile.class).getPic());
                }


                mMap.addMarker(new MarkerOptions().position(new Location()).title(friend.getUserName()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Intent intentUserInfo = getIntent();
        myProfile = (UserInfo)intentUserInfo.getSerializableExtra("profile");


        myProfile = (UserInfo)getIntent().getSerializableExtra("profile");


        Button button = findViewById(R.id.testButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MapsActivity.this, myProfile.friendList.size() + "!!", Toast.LENGTH_SHORT).show();
                //mapFriends(friendList);
            }
        });*/
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
        //mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(false);

        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            //boolean success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style_json_retro));
            boolean success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style_json_aubergine));

            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }

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
        /*friendList = new ArrayList<>();*/

       /* friendList.add(new Friend(new LatLng(38.989886, -76.936306), "Bruce", "Wayne", "Batman"));
        friendList.add(new Friend(new LatLng(38.987260, -76.942088),"Dick", "Grayson", "Nightwing"));
        friendList.add(new Friend(new LatLng(38.987923, -76.944648),"Barbara", "Gordan", "Batgirl"));
        friendList.add(new Friend(new LatLng(38.897392, -77.037002),"Tim", "Drake", "Red Robin"));
        friendList.add(new Friend(new LatLng(27.989720, -81.688442),"Jason", "Todd", "Red Hood"));
        friendList.add(new Friend(new LatLng(27.987700, 86.925954),"Stephanie", "Brown", "Spoiler"));
        friendList.add(new Friend(new LatLng(39.286132, -76.608427),"Alfred", "Pennyworth", "The Butler"));
        friendList.add(new Friend(new LatLng(40.772290, -73.980208), "Damian", "Wayne", "Batman"));
        friendList.add(new Friend(new LatLng(37.421716, -122.084344),"Selina", "Kyle", "Catwoman"));
        friendList.add(new Friend(new LatLng(42.946947, -122.097894),"Katherine", "Kane", "Batwoman"));*/
        /*

        DB.getUserFriends("admin", new Doer<Friend>() {
            @Override
            public void doFromResult(Friend result) {
                if (!friendList.contains(result)) {
                    friendList.add(result);

                    Toast.makeText(MapsActivity.this, "FriendList size = " + friendList.size() + "\n" +
                            result.getUserName(), Toast.LENGTH_SHORT).show();
                }
            }
        });
*/



        /*DB.getUserFriends("admin", new Doer<Friend>() {
            ArrayList<Friend> friends = new ArrayList<>();

            @Override
            public void doFromResult(Friend result) {
                friends.add(new Friend(result));
                Toast.makeText(MapsActivity.this, result.getLocation() + "", Toast.LENGTH_SHORT).show();
                copy(friends);
            }

            public void copy(ArrayList<Friend> friends) {
                friendList.addAll(friends);
            }
        });

        Toast.makeText(this, "FriendsList: size = " + friendList.size(), Toast.LENGTH_LONG).show();
*/
        //mMap.addMarker(new MarkerOptions().position(friendList.get(0).getLocation()).title(friendList.get(0).getUserName()));

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
