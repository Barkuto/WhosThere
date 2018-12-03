package whosthere.whosthere;

import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class NavigationBarActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "NavigationBarActivity";

    private MapsFragment mapsFragment = new MapsFragment();
    private ArrayList<Friend> mFriendsList;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mDatabase;
    private FirebaseUser mUser;

    public ArrayList<Friend> getmFriendsList() {
        return mFriendsList;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_bar);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.mAuth = FirebaseAuth.getInstance();
        this.mDatabase = FirebaseFirestore.getInstance();
        this.mUser = mAuth.getCurrentUser();
        this.mFriendsList = new ArrayList<>();

        /*FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Hello, World", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Set initial fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.mainLayout, mapsFragment).commit();
            navigationView.getMenu().getItem(0).setChecked(true);
        }

        this.mUser = mAuth.getCurrentUser();
        DocumentReference docRef = mDatabase.collection("users").document(/*mUser.getUid()*/ "fahodayi");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        NavigationBarActivity.this.getmFriendsList().clear();

                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        Map<String, Object> result = document.getData();
                        Map<String, Object> friendsDB = (Map)result.get("friends");
                        for (Map.Entry<String,Object> entry : friendsDB.entrySet()){
                            String friendUID = entry.getKey();
                            Map<String, Object> friendInfo = (Map)entry.getValue();
                            final String profilePicURL = "";
                            Friend f = new Friend(
                                    new LatLng((double)friendInfo.get("lat"), (double)friendInfo.get("lng")),
                                    (String)friendInfo.get("full_name"),
                                    (String)friendInfo.get("user_name"),
                                    (boolean)friendInfo.get("isFriend"),
                                    (boolean)friendInfo.get("iRequested"),
                                    (boolean)friendInfo.get("theyRequested"),
                                    (boolean)friendInfo.get("isFamily"),
                                    (boolean)friendInfo.get("isBlocked"),
                                    (boolean)friendInfo.get("isIncognito"),
                                    (boolean)friendInfo.get("hasMeBlocked"),
                                    (Date)friendInfo.get("lastSeen"),
                                    friendUID,
                                    /*(String)friendInfo.get("profilePicURL")*/
                                    null);
                            NavigationBarActivity.this.getmFriendsList().add(f);
                        }

                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });


        SharedPreferences myPrefs = PreferenceManager.getDefaultSharedPreferences(NavigationBarActivity.this);
        //int interval = Integer.parseInt(myPrefs.getString("TEXT", "3600000"));
        int interval = Integer.parseInt(myPrefs.getString("FREQ", "3600000"));
        Intent serviceIntent = new Intent(this, LocationService.class);
        serviceIntent.putExtra("interval", interval);
        startService(serviceIntent);
        Toast.makeText(this, myPrefs.getString("FREQ", "1"), Toast.LENGTH_SHORT).show();

        LocalBroadcastManager.getInstance(this).registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        Log.e("Location: ", "ENTERED ONRECEIVE");

                        String latitude = intent.getStringExtra(LocationService.EXTRA_LATITUDE);
                        String longitude = intent.getStringExtra(LocationService.EXTRA_LONGITUDE);

                        if (latitude != null && longitude != null) {
                            //Log.e("Location: ", "(" + latitude + ", " + longitude + ")");
                            Log.e(TAG, "onLocationChanged: (" + latitude + ", " + longitude + ")");

                            Map<String, Object> data = new HashMap<>();
                            data.put("lat", latitude);
                            data.put("lng", longitude);
                            mDatabase.collection("users").document("").set(data, SetOptions.merge());
                        }
                    }
                }, new IntentFilter(LocationService.ACTION_LOCATION_BROADCAST)
        );
    }

    @Override
    protected void onResume() {
        SharedPreferences myPrefs = PreferenceManager.getDefaultSharedPreferences(NavigationBarActivity.this);
        //int interval = Integer.parseInt(myPrefs.getString("TEXT", "3600000"));
        int interval = Integer.parseInt(myPrefs.getString("FREQ", "3600000"));
        Intent serviceIntent = new Intent(this, LocationService.class);
        serviceIntent.putExtra("interval", interval);
        startService(serviceIntent);
        //Toast.makeText(this, myPrefs.getString("FREQ", "1"), Toast.LENGTH_SHORT).show();

        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.navigation_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.map) {
            MapsFragment mapsFragment = new MapsFragment();
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.mainLayout, mapsFragment).commit();

        } else if (id == R.id.friends) {
            Log.i("tag", "Clicked on friends option!");
            FriendsFragment mNewFragment = new FriendsFragment();
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.mainLayout, mNewFragment).addToBackStack(null).commit();

        } else if (id == R.id.profile) {
            Intent intent = new Intent(NavigationBarActivity.this, profile_page_arthur.class);
            NavigationBarActivity.this.startActivity(intent);

        } else if (id == R.id.settings) {
            Intent intent = new Intent(NavigationBarActivity.this, MyPreferencesActivity.class);
            NavigationBarActivity.this.startActivity(intent);

        } else if (id == R.id.about) {
            Intent intent = new Intent(NavigationBarActivity.this, AboutActivity.class);
            NavigationBarActivity.this.startActivity(intent);

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
