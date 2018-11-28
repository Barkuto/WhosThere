package whosthere.whosthere;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class BottomNavigation extends AppCompatActivity {

    private TextView mTextMessage;
    private ArrayList<Friend> mFriendList;


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
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_notifications);
                    Intent gotoFriendsList = new Intent(BottomNavigation.this, FriendsActivity.class);
                    gotoFriendsList.putExtra("mFriendList", mFriendList);
                    getApplicationContext().startActivity(gotoFriendsList);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_navigation);

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        mFriendList = new ArrayList<>();
        mFriendList.add(new Friend(new LatLng(38.989886, -76.936306), "Bruce", "Wayne", "Batman"));
        mFriendList.add(new Friend(new LatLng(38.987260, -76.942088),"Dick", "Grayson", "Nightwing"));
        mFriendList.add(new Friend(new LatLng(38.987923, -76.944648),"Barbara", "Gordan", "Batgirl"));
        mFriendList.add(new Friend(new LatLng(38.897392, -77.037002),"Tim", "Drake", "Red Robin"));
        mFriendList.add(new Friend(new LatLng(27.989720, -81.688442),"Jason", "Todd", "Red Hood"));
        mFriendList.add(new Friend(new LatLng(27.987700, 86.925954),"Stephanie", "Brown", "Spoiler"));
        mFriendList.add(new Friend(new LatLng(39.286132, -76.608427),"Alfred", "Pennyworth", "The Butler"));
        mFriendList.add(new Friend(new LatLng(40.772290, -73.980208), "Damian", "Wayne", "Batman"));
        mFriendList.add(new Friend(new LatLng(37.421716, -122.084344),"Selina", "Kyle", "Catwoman"));
        mFriendList.add(new Friend(new LatLng(42.946947, -122.097894),"Katherine", "Kane", "Batwoman"));
    }

    public void setmFriendList(ArrayList<Friend> mFriendList) {
        this.mFriendList = mFriendList;
    }

    public ArrayList<Friend> getmFriendList() {
        return mFriendList;
    }
}
