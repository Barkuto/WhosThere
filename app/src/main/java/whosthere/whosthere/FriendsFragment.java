package whosthere.whosthere;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

public class FriendsFragment extends ListFragment {

    public ArrayList<Friend> friendList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*//
        // setContentView(R.layout.activity_friends);

        //friendList = (ArrayList)getIntent().getParcelableArrayListExtra("mFriendList");
        friendList = ((BottomNavigation)getActivity()).getmFriendList();


        FriendAdapter adapter = new FriendAdapter(getActivity(), friendList);

        setListAdapter(adapter);

        //ListView listView = findViewById(R.id.list);
        //listView.setAdapter(adapter);

*//*        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Friend friend = friendList.get(position);
                // Link to map ... somehow

            }
        });*//*

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    }
}
