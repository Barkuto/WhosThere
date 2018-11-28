package whosthere.whosthere;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

public class Friend implements Serializable{

    private static final long serialVersionUID = 1L;

    //private LatLng location;
    private double lat;
    private double lng;
    private String firstName;
    private String lastName;
    private String userName;
    private double distanceAway;

    public Friend(LatLng location, String firstName, String lastName, String userName) {
        //this.location = location;
        this.lat = location.latitude;
        this.lng = location.longitude;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
    }

    public LatLng getLocation() {
        return new LatLng(lat, lng);
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
}
