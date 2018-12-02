package whosthere.whosthere;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class BottomNavigation extends AppCompatActivity{

    private TextView mTextMessage;
    private ArrayList<Friend> mFriendList;
    private FirebaseAuth mAuth;
    private FriendsFragment mFriendsFragment;
    private FragmentManager mFragmentManager;



    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_dashboard);
                    mAuth.signOut();
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_notifications);
                    //Intent gotoFriendsList = new Intent(BottomNavigation.this, FriendsActivity.class);
                    //gotoFriendsList.putExtra("mFriendList", mFriendList);
                    //getApplicationContext().startActivity(gotoFriendsList);
                    Log.i("test", "Friend List Button Clicked!");
                    // Start a new FragmentTransaction
                    android.support.v4.app.FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();

                    mFriendsFragment = new FriendsFragment();

                    // Add the TitleFragment to the layout
                    fragmentTransaction.replace(R.id.friends_frag_cont,
                            mFriendsFragment);
                    fragmentTransaction.addToBackStack(null);
                    // Commit the FragmentTransaction
                    fragmentTransaction.commit();

                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_navigation);

        this.mAuth = FirebaseAuth.getInstance();
        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        this.mFragmentManager = getSupportFragmentManager();

/*        mFriendList = new ArrayList<>();
        mFriendList.add(new Friend(new LatLng(38.989886, -76.936306), "Bruce", "Wayne", "Batman"));
        mFriendList.add(new Friend(new LatLng(38.987260, -76.942088),"Dick", "Grayson", "Nightwing"));
        mFriendList.add(new Friend(new LatLng(38.987923, -76.944648),"Barbara", "Gordan", "Batgirl"));
        mFriendList.add(new Friend(new LatLng(38.897392, -77.037002),"Tim", "Drake", "Red Robin"));
        mFriendList.add(new Friend(new LatLng(27.989720, -81.688442),"Jason", "Todd", "Red Hood"));
        mFriendList.add(new Friend(new LatLng(27.987700, 86.925954),"Stephanie", "Brown", "Spoiler"));
        mFriendList.add(new Friend(new LatLng(39.286132, -76.608427),"Alfred", "Pennyworth", "The Butler"));
        mFriendList.add(new Friend(new LatLng(40.772290, -73.980208), "Damian", "Wayne", "Batman"));
        mFriendList.add(new Friend(new LatLng(37.421716, -122.084344),"Selina", "Kyle", "Catwoman"));
        mFriendList.add(new Friend(null,"Katherine", "Kane", "Batwoman", false));*/

    }

    public void setFriendList(ArrayList<Friend> mFriendList) {
        this.mFriendList = mFriendList;
    }

    public ArrayList<Friend> getFriendList() {
        return mFriendList;
    }



}
