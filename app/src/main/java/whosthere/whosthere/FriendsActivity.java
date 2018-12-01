package whosthere.whosthere;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import whosthere.whosthere.db.DB;
import whosthere.whosthere.db.Doer;

public class FriendsActivity extends AppCompatActivity {

    public ArrayList<Friend> friendList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

//        friendList.add(new Friend(new LatLng(38.989886, -76.936306), "Bruce", "Wayne", "Batman"));
//        friendList.add(new Friend(new LatLng(38.987260, -76.942088),"Dick", "Grayson", "Nightwing"));
//        friendList.add(new Friend(new LatLng(38.987923, -76.944648),"Barbara", "Gordan", "Batgirl"));
//        friendList.add(new Friend(new LatLng(38.897392, -77.037002),"Tim", "Drake", "Red Robin"));
//        friendList.add(new Friend(new LatLng(27.989720, -81.688442),"Jason", "Todd", "Red Hood"));
//        friendList.add(new Friend(new LatLng(27.987700, 86.925954),"Stephanie", "Brown", "Spoiler"));
//        friendList.add(new Friend(new LatLng(39.286132, -76.608427),"Alfred", "Pennyworth", "The Butler"));
//        friendList.add(new Friend(new LatLng(40.772290, -73.980208), "Damian", "Wayne", "Batman"));
//        friendList.add(new Friend(new LatLng(37.421716, -122.084344),"Selina", "Kyle", "Catwoman"));
//        friendList.add(new Friend(new LatLng(42.946947, -122.097894),"Katherine", "Kane", "Batwoman"));

        final FriendAdapter adapter = new FriendAdapter(this, friendList);
        ListView listView = findViewById(R.id.list);
        listView.setAdapter(adapter);

        DB.getUserFriends("admin", new Doer<Friend>() {
            @Override
            public void doFromResult(Friend result) {
                adapter.add(result);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Friend friend = friendList.get(i);
                // Link to map ... somehow
            }
        });
    }
}
