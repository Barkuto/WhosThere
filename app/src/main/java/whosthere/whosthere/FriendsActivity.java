package whosthere.whosthere;

import android.app.Activity;
import android.app.ListActivity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Icon;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class FriendsActivity extends AppCompatActivity {

    public ArrayList<Friend> friendList = new ArrayList<>();

    private NotificationManager mNotificationManager;
    private static final int MY_NOTIFICATION_ID = 1;
    private String mChannelID;
    private final long[] mVibratePattern = {100, 200, 300, 400, 500, 400, 300, 200, 400};
    FriendAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater mLayoutInflater = LayoutInflater.from(FriendsActivity.this);


        setContentView(R.layout.activity_friends);
        createNotificationChannel();
        friendList = (ArrayList)getIntent().getParcelableArrayListExtra("mFriendList");
        adapter = new FriendAdapter(this, friendList);
        ListView listView = findViewById(R.id.list);
        listView.setAdapter(adapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Friend friend = friendList.get(position);
                // Link to map ... somehow
            }
        });


        EditText searchView = (EditText)mLayoutInflater.inflate(R.layout.search_view, null);

        searchView.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Call back the Adapter with current character to Filter
                adapter.getFilter().filter(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        listView.addHeaderView(searchView);

/*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar mSnack = Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Action", null);
                mSnack.setAction("Button", new MyButtonListener());
                mSnack.show();
            }

        });
*/

    }

    private void createNotificationChannel() {

        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mChannelID = getPackageName() + ".channel_01";
        // The user-visible name of the channel.
        CharSequence name = getString(R.string.channel_name);

        // The user-visible description of the channel.
        String description = getString(R.string.channel_description);
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel mChannel = new NotificationChannel(mChannelID, name, importance);

        // Configure the notification channel.
        mChannel.setDescription(description);
        mChannel.enableLights(true);

        // Sets the notification light color for notifications posted to this
        // channel, if the device supports this feature.
        mChannel.setLightColor(Color.RED);
        mChannel.enableVibration(true);
        mChannel.setVibrationPattern(mVibratePattern);

        //Uri soundURI = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.alarm_rooster);
        //mChannel.setSound(soundURI, (new AudioAttributes.Builder()).setUsage(AudioAttributes.USAGE_NOTIFICATION).build());

        mNotificationManager.createNotificationChannel(mChannel);
    }

    public class MyButtonListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            //show notification
            // Define action Intent
            Intent mNotificationIntent = new Intent(getApplicationContext(),
                    NotificationSubActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent mContentIntent = PendingIntent.getActivity(getApplicationContext(), 0,
                    mNotificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            // Define the Notification's expanded message and Intent:
            Notification.Builder notificationBuilder = new Notification.Builder(
                    getApplicationContext(), mChannelID)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.baseline_person_pin_black_48))
                    .setSmallIcon(R.drawable.baseline_person_pin_circle_24)
                    .setTicker("Test1") //what is this?
                    .setAutoCancel(true)
                    .setContentTitle("John Doe is near you!")
                    .setContentText(
                            "John Doe 15.3 miles away from you!")
                    .setContentIntent(mContentIntent);


            // Pass the Notification to the NotificationManager:
            mNotificationManager.notify(MY_NOTIFICATION_ID,
                    notificationBuilder.build());

        }
    }

    public class NotificationSubActivity extends Activity {

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.sub_activity);
        }
    }

}
