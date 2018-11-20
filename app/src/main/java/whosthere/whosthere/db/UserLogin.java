package whosthere.whosthere.db;

public class UserLogin {
    public int password;
    public double salt;

    public UserLogin() {
        // Default constructor required for calls to DataSnapshot.getValue(UserInfo.class)
    }

    public UserLogin(int password, double salt) {
        this.password = password;
        this.salt = salt;
    }
}
