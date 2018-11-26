package whosthere.whosthere;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class FriendAdapter extends ArrayAdapter<Friend> {

    public FriendAdapter(Activity context, ArrayList<Friend> words) {
        super(context, 0 , words);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listViewItem = convertView;
        if (listViewItem == null) {
            listViewItem = LayoutInflater.from(getContext()).inflate(R.layout.friend_list_item, parent, false);
        }

        Friend currentFriend = getItem(position);

        TextView userName = listViewItem.findViewById(R.id.username);
        userName.setText("@" + currentFriend.getUserName());

        TextView fullName = listViewItem.findViewById(R.id.fullname);
        fullName.setText(currentFriend.getFullName());

        TextView distanceAway = listViewItem.findViewById(R.id.distance);
        distanceAway.setText(currentFriend.getDistanceAway() + "");

        return listViewItem;
    }
}
