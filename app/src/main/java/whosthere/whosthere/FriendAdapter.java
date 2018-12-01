package whosthere.whosthere;

import android.app.Activity;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;

import whosthere.whosthere.db.DB;

public class FriendAdapter extends ArrayAdapter<Friend>  implements Filterable{

    //FAHO: from stack overflow
    private ArrayList<Friend> mOriginalValues; // Original Values
    private ArrayList<Friend> mDisplayedValues;    // Values to be displayed
    LayoutInflater inflater;

    public FriendAdapter(Activity context, ArrayList<Friend> words) {
        super(context, 0 , words);
        this.mOriginalValues = words;
        this.mDisplayedValues = words;
        inflater = LayoutInflater.from(context);

    }
    @Override
    public int getCount() {
        return mDisplayedValues.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder {
        LinearLayout listLayout;
        TextView friendName;
        TextView username;
        TextView distance;
        LinearLayout alreadyFriend;
        LinearLayout addFriend;
        ImageView profilepic;
    }

/*    @Override
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

        if(!currentFriend.isMyFriend()){
            listViewItem.findViewById(R.id.add_friend).setVisibility(View.VISIBLE);
            listViewItem.findViewById(R.id.already_friend).setVisibility(View.GONE);
        } else {
            listViewItem.findViewById(R.id.add_friend).setVisibility(View.GONE);
            listViewItem.findViewById(R.id.already_friend).setVisibility(View.VISIBLE);
        }

        return listViewItem;
    }*/

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;

        if (convertView == null) {

            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.friend_list_item, null);
            holder.listLayout = (LinearLayout)convertView.findViewById(R.id.list_container);
            holder.friendName = (TextView) convertView.findViewById(R.id.fullname);
            holder.username = (TextView) convertView.findViewById(R.id.username);
            holder.profilepic = (ImageView) convertView.findViewById(R.id.image);
            holder.distance = (TextView) convertView.findViewById(R.id.distance);
            holder.alreadyFriend = (LinearLayout) convertView.findViewById(R.id.already_friend);
            holder.addFriend = (LinearLayout) convertView.findViewById(R.id.add_friend);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.friendName.setText(mDisplayedValues.get(position).getFullName());
        holder.username.setText("@"+mDisplayedValues.get(position).getUserName());
        holder.profilepic.setImageBitmap(mDisplayedValues.get(position).getProfilePic());
        holder.distance.setText(new Double(mDisplayedValues.get(position).getDistanceAway()).toString());

        if(!mDisplayedValues.get(position).isMyFriend()){
            holder.addFriend.setVisibility(View.VISIBLE);
            holder.alreadyFriend.findViewById(R.id.already_friend).setVisibility(View.GONE);
        } else {
            holder.addFriend.setVisibility(View.GONE);
            holder.alreadyFriend.findViewById(R.id.already_friend).setVisibility(View.VISIBLE);
        }

        holder.listLayout.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                Toast.makeText(v.getContext(), "Hello world!", Toast.LENGTH_SHORT).show();
            }
        });

        return convertView;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint,FilterResults results) {

                mDisplayedValues = (ArrayList<Friend>) results.values; // has the filtered values
                notifyDataSetChanged();  // notifies the data with new filtered values
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();        // Holds the results of a filtering operation in values
                ArrayList<Friend> filteredArrListFriends = new ArrayList<>();
                //ArrayList<Friend> filteredArrListNonFriends = new ArrayList<Friend>();

                /*
                *  get a list of all people from the database
                *   distinguish between friends and non friends;
                * */

                //ArrayList<Friend> allUsers = new ArrayList<>();

                if (mOriginalValues == null) {
                    mOriginalValues = new ArrayList<Friend>(mDisplayedValues); // saves the original data in mOriginalValues
                }

                /********
                 *
                 *  If constraint(CharSequence that is received) is null returns the mOriginalValues(Original) values
                 *  else does the Filtering and returns FilteredArrList(Filtered)
                 *
                 ********/
                if (constraint == null || constraint.length() == 0) {

                    // set the Original result to return
                    results.count = mOriginalValues.size();
                    results.values = mOriginalValues;
                } else {
                    constraint = constraint.toString().toLowerCase();
                    for (int i = 0; i < mOriginalValues.size(); i++) {
                        String fn = mOriginalValues.get(i).getFullName();
                        String un = mOriginalValues.get(i).getUserName();

                        if (fn.toLowerCase().startsWith(constraint.toString())
                                || un.toLowerCase().startsWith(constraint.toString())) {

                            filteredArrListFriends.add(new Friend(mOriginalValues.get(i).getLocation(),
                                    mOriginalValues.get(i).getFirstName(),
                                    mOriginalValues.get(i).getLastName(),
                                    mOriginalValues.get(i).getUserName()));
                        }
                    }

                    /*
                    *  In here we are going to search for the results from the database
                    *  maybe have some sort of a separator?
                    *
                    * */

                    // set the Filtered result to return
                    results.count = filteredArrListFriends.size();
                    results.values = filteredArrListFriends;
                }
                return results;
            }
        };
        return filter;
    }
}
