package whosthere.whosthere;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.function.Consumer;

public class DB {
    public static final FirebaseDatabase fdb = FirebaseDatabase.getInstance();
    public static final DatabaseReference dbr = fdb.getReference();

    // Tables:

    // users
    // id, username, email, profile pic, friends, settings
    public static final String USERSTABLE = "users";
    public static final String USERS_EMAIL = "email";
    public static final String USERS_USERNAME = "username";
    public static final String USERS_PIC = "pic";
    public static final String USERS_FRIENDS = "friends";
    public static final String USERS_SETTINGS = "settings";

    // logins
    // id, username, password, salt
    public static final String LOGINSTABLE = "logins";
    public static final String LOGINS_USERNAME = "username";
    public static final String LOGINS_PASSWORD = "password";
    public static final String LOGINS_SALT = "salt";

    // locations
    // id, loc
    public static final String LOCATIONSTABLE = "locations";
    public static final String LOCATIONS_LOC = "loc";

    // Errors
    public static final String USER_EXISTS_ERROR = "USER_EXISTS_ERROR";
    public static final String EMAIL_EXISTS_ERROR = "EMAIL_EXISTS_ERROR";

    public static boolean addUser(String username, String password) {
        return false;
    }

    public static boolean hasUser(String username) {
        return hasPath(USERSTABLE + "/" + username);
    }

    private static boolean hasPath(String path) {
        HasValueEventListener hvel = new HasValueEventListener(path);
        dbr.addListenerForSingleValueEvent(hvel);
        return hvel.hasValue;
    }

    private static class HasValueEventListener implements ValueEventListener {
        private String path;
        private boolean hasValue = false;

        public HasValueEventListener(String path) {
            super();
            this.path = path;
        }

        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            Log.i("Who", path);
            dataSnapshot.child(path).getChildren().forEach(new Consumer<DataSnapshot>() {
                @Override
                public void accept(DataSnapshot dataSnapshot) {
                    Log.i("Who", dataSnapshot.getKey());
                }
            });
            Log.i("Who", dataSnapshot.child(path).exists() + "");
            hasValue = dataSnapshot.child(path).exists();
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
        }
    }
}
