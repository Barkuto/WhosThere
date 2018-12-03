package whosthere.whosthere;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Log;
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

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchFriendAdapter extends ArrayAdapter<Friend>  implements Filterable{
    private static final String TAG = "SearchAdapter";

    //FAHO: from stack overflow
    private ArrayList<Friend> mOriginalValues; // Original Values
    private ArrayList<Friend> mDisplayedValues;    // Values to be displayed
    private FirebaseAuth mAuth;
    private FirebaseFirestore mDatabase;
    private FirebaseUser mUser;
    private View conv;
    private boolean isSearchDone;

    LayoutInflater inflater;

    public SearchFriendAdapter(Activity context, ArrayList<Friend> words) {
        super(context, 0 , words);
        this.mOriginalValues = new ArrayList<Friend>();
        this.mDisplayedValues = new ArrayList<Friend>();
        this.mAuth = FirebaseAuth.getInstance();
        this.mDatabase = FirebaseFirestore.getInstance();
        this.mUser = mAuth.getCurrentUser();
        this.isSearchDone = false;
        this.inflater = LayoutInflater.from(context);

    }

    public boolean isSearchDone() {
        return isSearchDone;
    }

    public void setSearchDone(boolean searchDone) {
        isSearchDone = searchDone;
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
        CircleImageView profilepic;
        LinearLayout friendRequested;
        LinearLayout friendRecevied;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;

        final Friend current = mDisplayedValues.get(position);
        if (convertView == null) {

            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.friend_list_item, null);
            holder.listLayout = (LinearLayout)convertView.findViewById(R.id.list_container);
            holder.friendName = (TextView) convertView.findViewById(R.id.fullname);
            holder.username = (TextView) convertView.findViewById(R.id.username);
            holder.profilepic = (CircleImageView) convertView.findViewById(R.id.image);
            holder.distance = (TextView) convertView.findViewById(R.id.distance);
            holder.alreadyFriend = (LinearLayout) convertView.findViewById(R.id.already_friend);
            holder.addFriend = (LinearLayout) convertView.findViewById(R.id.add_friend);
            holder.friendRequested = (LinearLayout) convertView.findViewById(R.id.friend_requested);
            holder.friendRecevied = (LinearLayout) convertView.findViewById(R.id.friend_received);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        this.conv = convertView;
        holder.friendName.setText(current.getFullName());
        holder.username.setText("@"+current.getUserName());

        //holder.profilepic.setImageBitmap(mDisplayedValues.get(position).getProfilePic());

        if(current.getProfilePic() == null) {
            DownloadProfilePicTask pdt = new DownloadProfilePicTask(current, holder.profilepic);
            pdt.execute(current); //is this correct?
        } else {
            holder.profilepic.setImageBitmap(current.getProfilePic());
        }
        holder.distance.setText(new Double(current.getDistanceAway()).toString());

/*        if(!mDisplayedValues.get(position).isMyFriend()){
            holder.addFriend.setVisibility(View.VISIBLE);
            holder.alreadyFriend.findViewById(R.id.already_friend).setVisibility(View.GONE);
        } else if() {
            holder.addFriend.setVisibility(View.GONE);
            holder.alreadyFriend.setVisibility(View.VISIBLE);
        }*/

        if(current.isMyFriend()){
            holder.alreadyFriend.setVisibility(View.VISIBLE);
            holder.addFriend.setVisibility(View.GONE);
            holder.friendRecevied.setVisibility(View.GONE);
            holder.friendRequested.setVisibility(View.GONE);
        } else if(current.isiRequested()){
            holder.alreadyFriend.setVisibility(View.GONE);
            holder.addFriend.setVisibility(View.GONE);
            holder.friendRecevied.setVisibility(View.GONE);
            holder.friendRequested.setVisibility(View.VISIBLE);
        }else if(current.isTheyRequested()){
            holder.alreadyFriend.setVisibility(View.GONE);
            holder.addFriend.setVisibility(View.GONE);
            holder.friendRecevied.setVisibility(View.VISIBLE);
            holder.friendRequested.setVisibility(View.GONE);
        } else {
            holder.alreadyFriend.setVisibility(View.GONE);
            holder.addFriend.setVisibility(View.VISIBLE);
            holder.friendRecevied.setVisibility(View.GONE);
            holder.friendRequested.setVisibility(View.GONE);
        }

        holder.addFriend.findViewById(R.id.add_friend_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Faho request friend request");

                Map<String, Object> notification = new HashMap<>();
                notification.put("notType", "friendRequest");
                notification.put("isSent", false);
                mDatabase.collection("users").document(current.getUid()).collection("notifications").document(mUser.getUid())
                        .set(notification, SetOptions.merge())
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


                current.setiRequested(true);
                //adding a friend to me
                Map<String, Object> modUser = new HashMap<>();
                modUser.put("iRequested", true);
                modUser.put("full_name", current.getFullName());
                modUser.put("hasMeBlocked", false);
                modUser.put("isBlocked", false);
                modUser.put("isFamily",false);
                modUser.put("isFriend", false);
                modUser.put("isIncognito", current.isIncognito());
                modUser.put("lastSeen", Timestamp.now());
                modUser.put("lat", 0);
                modUser.put("lng", 0);
                modUser.put("profilePicURL", current.getProfilePicURL());
                modUser.put("theyRequested", false);
                modUser.put("uid", current.getUid());
                modUser.put("user_name", current.getUserName());

                mDatabase.collection("users").document(mUser.getUid()).collection("friends").document(current.getUid())
                    .set(modUser, SetOptions.merge())
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


                //sending them a request
                Friend meReference = ((NavigationBarActivity)getContext()).getMe();
                Map<String, Object> modUser2 = new HashMap<>();
                modUser2.put("iRequested", false);
                modUser2.put("full_name", meReference.getFullName());
                modUser2.put("hasMeBlocked", false);
                modUser2.put("isBlocked", false);
                modUser2.put("isFamily",false);
                modUser2.put("isFriend", false);
                modUser2.put("isIncognito", meReference.isIncognito());
                modUser2.put("lastSeen", Timestamp.now());
                modUser2.put("lat", 0);
                modUser2.put("lng", 0);
                modUser2.put("profilePicURL", meReference.getProfilePicURL());
                modUser2.put("uid", mUser.getUid());
                modUser2.put("user_name", meReference.getUserName());
                modUser2.put("theyRequested", true);
                mDatabase.collection("users").document(current.getUid()).collection("friends").document(mUser.getUid())
                    .set(modUser2, SetOptions.merge())
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

                 SearchFriendAdapter.this.conv.findViewById(R.id.already_friend).setVisibility(View.GONE);
                 SearchFriendAdapter.this.conv.findViewById(R.id.add_friend).setVisibility(View.GONE);
                 SearchFriendAdapter.this.conv.findViewById(R.id.friend_received). setVisibility(View.GONE);
                 SearchFriendAdapter.this.conv.findViewById(R.id.friend_requested) .setVisibility(View.VISIBLE);
            }
        });
        holder.friendRecevied.findViewById(R.id.friend_received_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Faho accepted friend request");
                //adding a friend to me
                Map<String, Object> modUser = new HashMap<>();
                modUser.put("iRequested", false);
                modUser.put("isFriend", true);
                modUser.put("theyRequested", false);

                mDatabase.collection("users").document(mUser.getUid()).collection("friends").document(current.getUid())
                        .set(modUser, SetOptions.merge())
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


                //sending them a request
                Friend meReference = ((NavigationBarActivity)getContext()).getMe();
                Map<String, Object> modUser2 = new HashMap<>();
                modUser2.put("iRequested", false);
                modUser2.put("isFriend", true);
                modUser2.put("theyRequested", false);
                mDatabase.collection("users").document(current.getUid()).collection("friends").document(mUser.getUid())
                    .set(modUser2, SetOptions.merge())
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

                 SearchFriendAdapter.this.conv.findViewById(R.id.already_friend).setVisibility(View.VISIBLE);
                 SearchFriendAdapter.this.conv.findViewById(R.id.add_friend).setVisibility(View.GONE);
                 SearchFriendAdapter.this.conv.findViewById(R.id.friend_received). setVisibility(View.GONE);
                 SearchFriendAdapter.this.conv.findViewById(R.id.friend_requested) .setVisibility(View.GONE);

                Map<String, Object> notification = new HashMap<>();
                notification.put("notType", "friendAccept");
                notification.put("isSent", false);
                mDatabase.collection("users").document(current.getUid()).collection("notifications").document(mUser.getUid())
                        .set(notification, SetOptions.merge())
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

            }
        });


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
                    //mOriginalValues = new ArrayList<Friend>(mDisplayedValues); // saves the original data in mOriginalValues

                }

                /********
                 *
                 *  If constraint(CharSequence that is received) is null returns the mOriginalValues(Original) values
                 *  else does the Filtering and returns FilteredArrList(Filtered)
                 *
                 ********/
                if (constraint == null || constraint.length() == 0) {
                    Log.i(TAG, "FAHO GOT 1");

                    // set the Original result to return
                    results.count = mOriginalValues.size();
                    results.values = mOriginalValues; //maybe make this empty?
                    results.count = 0;
                    results.values = new ArrayList<>();
                } else {
                    constraint = constraint.toString().toLowerCase();
                    mOriginalValues = new ArrayList<>();
                    mDisplayedValues = new ArrayList<>();
                    //need to fill mOriginalValues
                    Log.i(TAG, "FAHO GOT 2");

                    mDatabase.collection("users")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Log.d(TAG, document.getId() + " => " + document.getData());
                                        Log.i(TAG, "Document!");
/*                                        if(!(boolean)document.getData().get("isFriend") &&
                                                !(boolean)document.getData().get("hasMeBlocked") &&
                                                !(boolean)document.getData().get("isBlocked")
                                                ){
                                            mOriginalValues.add(new Friend(
                                             new LatLng((double)document.getData().get("lat"),
                                             (double)document.getData().get("lng")),
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
                                            (String)document.getData().get("profilePicURL"))
                                            );
                                        }*/

                                    if(document.getData().get("isIncognito") == null){
                                       continue;
                                    }
                                    if(!((String)document.getId()).equals(mUser.getUid()) && !(boolean)document.getData().get("isIncognito")){
                                        Log.i(TAG, "FAHO about to add user to search list!!");
                                        mOriginalValues.add(new Friend(
                                                null,
                                                (String)document.getData().get("full_name"),
                                                (String)document.getData().get("user_name"),
                                                false,
                                                false,
                                                false,
                                                false,
                                                false,
                                                (boolean)document.getData().get("isIncognito"),
                                                false,
                                                null,
                                                (String)document.getId(),
                                                (String)document.getData().get("profilePicURL"))
                                        );
                                    }
                                    }
                                    SearchFriendAdapter.this.setSearchDone(true);

                                } else {
                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                }
                            }
                        });

                    while(!SearchFriendAdapter.this.isSearchDone()){
                        // do nothing
                        Log.i(TAG, "FAHO GOT 3");

                    }

                    for (int i = 0; i < mOriginalValues.size(); i++) {
                        String fn = mOriginalValues.get(i).getFullName();
                        String un = mOriginalValues.get(i).getUserName();
                        Log.i(TAG, "FAHO ADDED USER TO LIST");

                        if (fn.toLowerCase().startsWith(constraint.toString()) || un.toLowerCase().startsWith(constraint.toString())) {




                            filteredArrListFriends.add(new Friend(mOriginalValues.get(i).getLocation(),
                                mOriginalValues.get(i).getFullName(),
                                mOriginalValues.get(i).getUserName(),
                                mOriginalValues.get(i).isMyFriend(),
                                mOriginalValues.get(i).isiRequested(),
                                mOriginalValues.get(i).isTheyRequested(),
                                mOriginalValues.get(i).isFamily(),
                                mOriginalValues.get(i).isBlocked(),
                                mOriginalValues.get(i).isIncognito(),
                                mOriginalValues.get(i).isHasMeBlocked(),
                                mOriginalValues.get(i).getLastSeen(),
                                mOriginalValues.get(i).getUid(),
                                mOriginalValues.get(i).getProfilePicURL())
                            );
                            Log.i(TAG, "FAHO ADDED USER TO LIST");
                        }
                    }

                    SearchFriendAdapter.this.setSearchDone(false);
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
