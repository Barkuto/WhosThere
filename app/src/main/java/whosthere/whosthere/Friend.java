package whosthere.whosthere;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.renderscript.ScriptGroup;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class Friend implements Serializable{

    private static final long serialVersionUID = 1L;
    private static final String TAG = "Friend";

    private FirebaseAuth mAuth;
    private FirebaseFirestore mDatabase;
    private FirebaseUser mUser;
    //private LatLng location;
    private double lat;
    private double lng;
    private String fullName;
    private String userName;
    private double distanceAway;
    private boolean isMyFriend;
    private Bitmap profilePic;
    private String profilePicURL;
    private boolean iRequested;
    private boolean theyRequested;
    private boolean isFamily;
    private boolean isBlocked;
    private boolean hasMeBlocked;
    private boolean isIncognito;
    private Date lastSeen;
    private String uid;

    public Friend(LatLng location, String fullName, String userName) {
        //this.location = location;
        this.lat = location.latitude;
        this.lng = location.longitude;
        this.fullName = fullName;
        this.userName = userName;
        this.isMyFriend = true;
    }


    public Friend(){
        this.mAuth = FirebaseAuth.getInstance();
        this.mDatabase = FirebaseFirestore.getInstance();
        this.mUser = mAuth.getCurrentUser();
        this.setUid(Friend.this.mUser.getUid());

        DocumentReference docRef2 = mDatabase.collection("users").document(mUser.getUid());
        docRef2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Friend.this.setUid(Friend.this.mUser.getUid());
                        Friend.this.setFullName((String)document.get("full_name"));
                        Friend.this.setIncognito((boolean)document.get("isIncognito"));
                        Friend.this.setLat(((Double)document.get("lat")).doubleValue());
                        Friend.this.setLng(((Double)document.get("lng")).doubleValue());
                        Friend.this.setProfilePicURL((String)document.get("profilePicURL"));
                        Friend.this.setUserName((String)document.get("user_name"));
                        Friend.this.updateFriendsDatabase();
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });


        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        //Uri pathURI = storageRef.child("profilePics/default_avatar.png").getDownloadUrl();
        storageRef.child("profilePics/default_avatar.png").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                //DownloadProfilePicTask pdt = new DownloadProfilePicTask(Friend.this);
                //pdt.execute(uri.toString()); //is this correct?
                Friend.this.profilePicURL = uri.toString();


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });



        mDatabase.collection("users").document(uid).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    //Log.w(TAG, "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    Friend.this.isIncognito = (boolean)snapshot.getData().get("isIncognito");
                    Friend.this.lat = ((Double)snapshot.getData().get("lat")).doubleValue();
                    Friend.this.lng = ((Double)snapshot.getData().get("lng")).doubleValue();
                    Friend.this.profilePicURL = (String)snapshot.getData().get("profilePicURL");

                    Log.d(TAG, "Current data: " + snapshot.getData());
                } else {
                    //Log.d(TAG, "Current data: null");
                }
            }
        });


        mDatabase.collection("users").document(mUser.getUid()).collection("friends").document(uid).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    //Log.w(TAG, "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    Friend.this.hasMeBlocked = (boolean) snapshot.getData().get("hasMeBlocked");
                    //Friend.this.iRequested = (boolean) snapshot.getData().get("hasMeBlocked");
                    Friend.this.isBlocked = (boolean) snapshot.getData().get("isBlocked");
                    Friend.this.isFamily = (boolean) snapshot.getData().get("isFamily");
                    //Friend.this.isIncognito = (boolean) snapshot.getData().get("isIncognito");
                    Friend.this.lastSeen = (Date) snapshot.getData().get("lastSeen");
                    //Friend.this.lat = ((Long) snapshot.getData().get("lat")).doubleValue();
                    // Friend.this.lng = ((Long) snapshot.getData().get("lat")).doubleValue();




                    Log.d(TAG, "Current data: " + snapshot.getData());
                } else {
                    //Log.d(TAG, "Current data: null");
                }
            }
        });

    }
    public Friend(LatLng location, String fullName, String userName, boolean isMyFriend) {
        if(location != null){
            this.lat = location.latitude;
            this.lng = location.longitude;
        }
        this.fullName = fullName;
        this.userName = userName;
        this.isMyFriend = isMyFriend;



    }
    public Friend(LatLng location, String fullName, String userName,
                  boolean isMyFriend,
                  boolean iRequested,
                  boolean theyRequested,
                  boolean isFamily,
                  boolean isBlocked,
                  boolean isIncognito,
                  boolean hasMeBlocked,
                  Date lastSeen,
                  String uid,
                  String profilePicURL) {


        this.mAuth = FirebaseAuth.getInstance();
        this.mDatabase = FirebaseFirestore.getInstance();
        this.mUser = mAuth.getCurrentUser();

        if(location != null){
            this.lat = location.latitude;
            this.lng = location.longitude;
        } else {
            this.lat = 0.999;
            this.lng = 0.999;
        }
        this.fullName = fullName;
        this.userName = userName;
        this.isMyFriend = isMyFriend;
        this.iRequested = iRequested;
        this.theyRequested = theyRequested;
        this.isFamily = isFamily;
        this.isBlocked = isBlocked;
        this.lastSeen = lastSeen;
        this.uid = uid;
        this.profilePicURL = profilePicURL;
        this.hasMeBlocked = hasMeBlocked;
        this.isIncognito = isIncognito;


        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        //Uri pathURI = storageRef.child("profilePics/default_avatar.png").getDownloadUrl();
        storageRef.child("profilePics/default_avatar.png").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                //DownloadProfilePicTask pdt = new DownloadProfilePicTask(Friend.this);
                //pdt.execute(uri.toString()); //is this correct?
                Friend.this.profilePicURL = uri.toString();


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });



        mDatabase.collection("users").document(uid).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    //Log.w(TAG, "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    Friend.this.isIncognito = (boolean)snapshot.getData().get("isIncognito");
                    Friend.this.lat = ((Double)snapshot.getData().get("lat")).doubleValue();
                    Friend.this.lng = ((Double)snapshot.getData().get("lng")).doubleValue();
                    Friend.this.profilePicURL = (String)snapshot.getData().get("profilePicURL");

                    Log.d(TAG, "Current data: " + snapshot.getData());
                } else {
                    //Log.d(TAG, "Current data: null");
                }


            }
        });


        mDatabase.collection("users").document(mUser.getUid()).collection("friends").document(uid).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    //Log.w(TAG, "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    Friend.this.hasMeBlocked = (boolean) snapshot.getData().get("hasMeBlocked");
                    //Friend.this.iRequested = (boolean) snapshot.getData().get("hasMeBlocked");
                    Friend.this.isBlocked = (boolean) snapshot.getData().get("isBlocked");
                    Friend.this.isFamily = (boolean) snapshot.getData().get("isFamily");
                    //Friend.this.isIncognito = (boolean) snapshot.getData().get("isIncognito");
                    Friend.this.lastSeen = (Date) snapshot.getData().get("lastSeen");
                    //Friend.this.lat = ((Long) snapshot.getData().get("lat")).doubleValue();
                   // Friend.this.lng = ((Long) snapshot.getData().get("lat")).doubleValue();


                    Log.d(TAG, "Current data: " + snapshot.getData());
                } else {
                    //Log.d(TAG, "Current data: null");
                }
            }
        });

    }

    public Friend(Friend friend) {
        this(friend.getLocation(), friend.getFullName(), friend.getUserName());
    }

    public LatLng getLocation() {
        return new LatLng(lat, lng);
    }

    public String getFullName() {
        return fullName;
    }

    public String getUserName() {
        return userName;
    }

    public void setLocation(LatLng location) {
        this.lat = location.latitude;
        this.lng = location.longitude;
    }

    public double getDistanceAway() {
        return distanceAway;
    }

    public void setDistanceAway(double distanceAway) {
        this.distanceAway = distanceAway;
    }

    public boolean isMyFriend() {
        return isMyFriend;
    }

    public void setMyFriend(boolean myFriend) {
        isMyFriend = myFriend;

        Map<String, Object> data = new HashMap<>();
        data.put("isFriend", myFriend);
        if(!mUser.getUid().equals(uid)) {

            mDatabase.collection("users").document(mUser.getUid()).collection("friends").document(this.uid)
                    .set(data, SetOptions.merge());
        }
    }

    public Bitmap getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(Bitmap profilePic) {
        this.profilePic = profilePic;
    }

    public void setProfilePicURL(String profilePicURL) {
        this.profilePicURL = profilePicURL;
        Map<String, Object> data = new HashMap<>();
        data.put("profilePicURL", profilePicURL);

        if(!mUser.getUid().equals(uid)) {
            mDatabase.collection("users").document(mUser.getUid()).collection("friends").document(this.uid)
                    .set(data, SetOptions.merge());
        }
    }

    public String getProfilePicURL() {
        return profilePicURL;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public boolean isiRequested() {
        return iRequested;
    }

    public void setiRequested(boolean iRequested) {
        this.iRequested = iRequested;
    }

    public boolean isTheyRequested() {
        return theyRequested;
    }

    public void setTheyRequested(boolean theyRequested) {
        this.theyRequested = theyRequested;
    }

    public boolean isFamily() {
        return isFamily;
    }

    public void setFamily(boolean family) {
        isFamily = family;

        Map<String, Object> data = new HashMap<>();
        data.put("isFamily", family);
        if(!mUser.getUid().equals(uid)) {

            mDatabase.collection("users").document(mUser.getUid()).collection("friends").document(this.uid)
                    .set(data, SetOptions.merge());
        }
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
        Map<String, Object> data = new HashMap<>();
        data.put("isBlocked", blocked);

        if(!mUser.getUid().equals(uid)) {

            mDatabase.collection("users").document(mUser.getUid()).collection("friends").document(this.uid)
                    .set(data, SetOptions.merge());
        }
    }

    public boolean isHasMeBlocked() {
        return hasMeBlocked;
    }

    public void setHasMeBlocked(boolean hasMeBlocked) {
        this.hasMeBlocked = hasMeBlocked;

    }

    public boolean isIncognito() {
        return isIncognito;
    }

    public void setIncognito(boolean incognito) {
        isIncognito = incognito;
        Map<String, Object> data = new HashMap<>();
        data.put("isIncognito", incognito);
        if(!mUser.getUid().equals(uid)) {

            mDatabase.collection("users").document(mUser.getUid()).collection("friends").document(this.uid)
                    .set(data, SetOptions.merge());
        } else {

            mDatabase.collection("users").document(mUser.getUid())
                    .set(data, SetOptions.merge());
        }
    }

    public Date getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(Date lastSeen) {
        this.lastSeen = lastSeen;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    @Override
    public boolean equals(Object o){
        Friend other = (Friend)o;
        if(this.uid.equals(other.uid)){
            return true;
        } else {
            return false;
        }
    }


    public void updateFriendsDatabase(){
        if(mUser.getUid() != uid){
            Map<String, Object> data = new HashMap<>();
            data.put("lat", getLat());
            data.put("lng", getLng());

            mDatabase.collection("users").document(mUser.getUid()).collection("friends").document(uid)
                    .set(data, SetOptions.merge());

        }
    }
}
