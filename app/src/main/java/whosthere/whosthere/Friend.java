package whosthere.whosthere;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.renderscript.ScriptGroup;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;


public class Friend implements Serializable{

    private static final long serialVersionUID = 1L;

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

        if(location != null){
            this.lat = location.latitude;
            this.lng = location.longitude;
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
    }

    public Bitmap getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(Bitmap profilePic) {
        this.profilePic = profilePic;
    }

    public void setProfilePicURL(String profilePicURL) {
        this.profilePicURL = profilePicURL;
    }

    public String getProfilePicURL() {
        return profilePicURL;
    }
}
