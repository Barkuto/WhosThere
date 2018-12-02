package whosthere.whosthere.db;

import android.icu.util.Freezable;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import whosthere.whosthere.Friend;

public class UserInfo implements Serializable {
    public String email, pic;
    public List<String> friends;
    public List<Integer> settings;
    public ArrayList<Friend> friendList;

    public UserInfo() {
        // Default constructor required for calls to DataSnapshot.getValue(UserInfo.class)
    }

    public UserInfo(final String email, String pic, List<String> friends, List<Integer> settings) {
        this.email = email;
        this.pic = pic;
        this.friends = friends;
        this.settings = settings;

        friendList = new ArrayList<>();
        for (final String username : friends) {
            DB.getUserInfo(username, new Doer<UserInfo>() {
                @Override
                public void doFromResult(UserInfo info) {
                    DB.getUserLocation(username, new Doer<LatLng>() {
                        @Override
                        public void doFromResult(LatLng result) {
                            friendList.add(new Friend(result, email, email, email));
                        }
                    });
                }
            });
        }
    }

    public String getEmail() {
        return email == null ? "" : email;
    }

    public String getPic() {
        return pic == null ? "" : pic;
    }

    public List<String> getFriends() {
        return friends == null ? new ArrayList<String>() : friends;
    }

    public List<Integer> getSettings() {
        return settings == null ? new ArrayList<Integer>() : settings;
    }
}
