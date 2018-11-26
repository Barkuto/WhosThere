package whosthere.whosthere.db;

import java.util.ArrayList;
import java.util.List;

public class UserInfo {
    public String email, pic;
    public List<String> friends;
    public List<Integer> settings;

    public UserInfo() {
        // Default constructor required for calls to DataSnapshot.getValue(UserInfo.class)
    }

    public UserInfo(String email, String pic, List<String> friends, List<Integer> settings) {
        this.email = email;
        this.pic = pic;
        this.friends = friends;
        this.settings = settings;
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
