package whosthere.whosthere;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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
import android.widget.Toast;

import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

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
    private AlarmManager mAlarmManager;
    private PendingIntent mNotificationReceiverPendingIntent;
    private static final long JITTER = 1000L;
    private static final long REPEAT_INTERVAL = 5000;

    private final Friend me = new Friend();

    public ArrayList<Friend> getmFriendsList() {
        return mFriendsList;
    }

    public Friend getMe() {
        return me;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_bar);



        //SETUP ALARM

/*
        this.mAlarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        Intent mNotificationReceiverIntent = new Intent(NavigationBarActivity.this, AlarmNotificationReceiver.class);
        mNotificationReceiverPendingIntent = PendingIntent.getBroadcast(NavigationBarActivity.this,
                0, mNotificationReceiverIntent, 0);
        mAlarmManager.set(AlarmManager.)
*/


        //END SETUP ALARM

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();

                        // Log and toast
                        //String msg = getString(R.string.msg_token_fmt, token);
                        Log.d(TAG, "FAHO TOKEN" + token);

                        //PUT THIS ID IN THE SERVER!!

                        Map<String, Object> docData = new HashMap<>();
                        docData.put("tokenID", token);

                        mDatabase.collection("users").document(mUser.getUid())
                                .set(docData, SetOptions.merge())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "DocumentSnapshot successfully written!");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error writing document", e);
                                    }
                                });



                        //Toast.makeText(NavigationBarActivity.this, token, Toast.LENGTH_SHORT).show();
                    }
                });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.mAuth = FirebaseAuth.getInstance();
        this.mDatabase = FirebaseFirestore.getInstance();
        this.mUser = mAuth.getCurrentUser();
        this.mFriendsList = new ArrayList<>();
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Hello, World", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

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

        DocumentReference docRef2 = mDatabase.collection("users").document(mUser.getUid());
        docRef2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        me.setFullName((String)document.get("full_name"));
                        me.setIncognito((boolean)document.get("isIncognito"));
                        me.setLat(((Long)document.get("lat")).doubleValue());
                        me.setLng(((Long)document.get("lng")).doubleValue());
                        me.setProfilePicURL((String)document.get("profilePicURL"));
                        me.setUserName((String)document.get("user_name"));
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

/*        DocumentReference docRef = mDatabase.collection("users").document(*//*mUser.getUid()*//* "fahodayi");
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
                                    *//*(String)friendInfo.get("profilePicURL")*//*
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
        });*/
        //CollectionReference friendsRef2 = mDatabase.collection("users").document(mUser.getUid()).collection("friends");
        Task<QuerySnapshot> friendsRef = mDatabase.collection("users").document(mUser.getUid()).collection("friends")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());

                                Friend f = new Friend(
                                        new LatLng(
                                                ((Long)document.getData().get("lat")).doubleValue(),

                                                ((Long)document.getData().get("lng")).doubleValue()),
                                        (String)document.getData().get("full_name"),
                                        (String)document.getData().get("user_name"),
                                        (boolean)document.getData().get("isFriend"),
                                        (boolean)document.getData().get("iRequested"),
                                        (boolean)document.getData().get("theyRequested"),
                                        (boolean)document.getData().get("isFamily"),
                                        (boolean)document.getData().get("isBlocked"),
                                        (boolean)document.getData().get("isIncognito"),
                                        (boolean)document.getData().get("hasMeBlocked"),
                                        (Date)document.getData().get("lastSeen"),
                                        (String)document.getData().get("uid"),
                                        (String)document.getData().get("profilePicURL"));
                                NavigationBarActivity.this.getmFriendsList().add(f);
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });


        /*DocumentReference docRef = mDatabase.collection("users").document(mUser.getUid()).collection("friends");
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
                                    *//*(String)friendInfo.get("profilePicURL")*//*
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
        });*/



        //FRIEND REQUEST LISTENER:::

        /*final CollectionReference docRef = mDatabase.collection("users").document(me.getUid()).collection("friends");
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    Log.d(TAG, "Current data: " + snapshot.getData());
                } else {
                    Log.d(TAG, "Current data: null");
                }
            }
        });*/

        //END FRIEND REQUEST LISTENER

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

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
            MapsFragment mapsFragment = new MapsFragment();
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.mainLayout, mapsFragment).commit();

        } else if (id == R.id.settings) {
            MapsFragment mapsFragment = new MapsFragment();
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.mainLayout, mapsFragment).commit();

        } else if (id == R.id.about) {
            MapsFragment mapsFragment = new MapsFragment();
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.mainLayout, mapsFragment).commit();

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
