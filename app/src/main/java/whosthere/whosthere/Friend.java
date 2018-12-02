package whosthere.whosthere;

import com.google.android.gms.maps.model.LatLng;

public class Friend {

    private LatLng location;
    private String firstName;
    private String lastName;
    private String userName;
    private double distanceAway;

    public Friend(LatLng location, String firstName, String lastName, String userName) {
        this.location = location;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
    }

    public Friend(Friend friend) {
        this(friend.getLocation(), friend.getFirstName(), friend.getLastName(), friend.getUserName());
    }

    public LatLng getLocation() {
        return location;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public String getUserName() {
        return userName;
    }

    public double getDistanceAway() {
        return distanceAway;
    }

    public void setDistanceAway(double distanceAway) {
        this.distanceAway = distanceAway;
    }
}
