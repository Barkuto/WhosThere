package whosthere.whosthere;

import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;

public class DB {
    public static final FirebaseDatabase fdb = FirebaseDatabase.getInstance();
    public static final DatabaseReference dbr = fdb.getReference();

    // Tables:

    // users
    // username -> email, profile pic, friends, settings
    public static final String USERSTABLE = "users";
    public static final String USERS_EMAIL = "email";
    public static final String USERS_PIC = "pic";
    public static final String USERS_FRIENDS = "friends";
    public static final String USERS_SETTINGS = "settings";

    // logins
    // username -> password, salt
    public static final String LOGINSTABLE = "logins";
    public static final String LOGINS_PASSWORD = "password";
    public static final String LOGINS_SALT = "salt";

    // locations
    // username : loc
    public static final String LOCATIONSTABLE = "locations";

    // Set Value Methods
    public static void setValue(String path, Object value) {
        dbr.child(sanitizePath(path)).setValue(value);
    }

    public static void addUser(final String username, final String email, final String password, final Doer<Boolean> doer) {
        // Need to add users and logins
        String userPath = sanitizePath(usersPath(username));
        doIfHasPath(userPath, new Doer<Boolean>() {
            @Override
            public void doFromResult(Boolean result) {
                // User Does not Exist, add user to DB
                if (!result) {
                    double salt = Math.random();
                    // Login Info
                    dbr.child(loginsSaltPath(username)).setValue(salt);
                    dbr.child(loginsPasswordPath(username)).setValue((password + salt).hashCode());

                    // User Info
                    dbr.child(usersEmailPath(username)).setValue(email);
                    dbr.child(usersPicPath(username)).setValue("PIC");
                    dbr.child(usersFriendsPath(username)).setValue(new ArrayList<>(Arrays.asList("A")));
                    dbr.child(usersSettingsPath(username)).setValue(new ArrayList<>(Arrays.asList(0, 0, 0, 0, 0)));

                    // Location Info
                    dbr.child(locationsPath(username)).setValue("LOCATION");
                }
                if (doer != null) doer.doFromResult(result);
            }
        });
    }

    public static void setUserLocation(String username, final Object location) {
        final String locationPath = sanitizePath(locationsPath(username));
        doIfHasPath(locationPath, new Doer<Boolean>() {
            @Override
            public void doFromResult(Boolean result) {
                if (result) dbr.child(locationPath).setValue(location);
            }
        });
    }


    // "Get" Value Methods
    public static void doIfHasPath(String path, final Doer<Boolean> doer) {
        HasValueEventListener hvel = new HasValueEventListener(sanitizePath(path), new Doer<Boolean>() {
            @Override
            public void doFromResult(Boolean result) {
                if (doer != null) doer.doFromResult(result);
            }
        });
        dbr.addListenerForSingleValueEvent(hvel);
    }

    public static void doWithValue(String path, final Doer<Object> doer) {
        GetValueEventListener<Object> gvel = new GetValueEventListener<>(sanitizePath(path), new Doer<Object>() {
            @Override
            public void doFromResult(Object result) {
                doer.doFromResult(result);
            }
        });
        dbr.addListenerForSingleValueEvent(gvel);
    }

    // Verification Methods
    private static String sanitizePath(String path) {
        return path.replace(".", "")
                .replace("#", "")
                .replace("$", "")
                .replace("[", "")
                .replace("]", "");
    }

    public static void verifyUserLogin(final String username, final String password, final Doer<Boolean> doer) {
        String usersPath = sanitizePath(usersPath(username));
        doIfHasPath(usersPath, new Doer<Boolean>() {
            @Override
            public void doFromResult(Boolean result) {
                if (result) {
                    // User exists
                    doWithValue(loginsSaltPath(username), new Doer<Object>() {
                        @Override
                        public void doFromResult(Object result) {
                            double salt = (double) result;
                            final int hpass = (password + salt).hashCode();
                            doWithValue(loginsPasswordPath(username), new Doer<Object>() {
                                @Override
                                public void doFromResult(Object result) {
                                    long password = (long) result;
                                    doer.doFromResult(hpass == password);
                                }
                            });
                        }
                    });
                } else {
                    // User does not exist
                    doer.doFromResult(false);
                }
            }
        });
    }

    // Path Generation Methods

    // Users
    public static String usersPath(String username) {
        return USERSTABLE + "/" + username;
    }

    public static String usersEmailPath(String username) {
        return usersPath(username) + "/" + USERS_EMAIL;
    }

    public static String usersPicPath(String username) {
        return usersPath(username) + "/" + USERS_PIC;
    }

    public static String usersFriendsPath(String username) {
        return usersPath(username) + "/" + USERS_FRIENDS;
    }

    public static String usersSettingsPath(String username) {
        return usersPath(username) + "/" + USERS_SETTINGS;
    }

    // Logins
    public static String loginsPath(String username) {
        return LOGINSTABLE + "/" + username;
    }

    public static String loginsPasswordPath(String username) {
        return loginsPath(username) + "/" + LOGINS_PASSWORD;
    }

    public static String loginsSaltPath(String username) {
        return loginsPath(username) + "/" + LOGINS_SALT;
    }

    // Location
    public static String locationsPath(String username) {
        return LOCATIONSTABLE + "/" + username;
    }


    // Private Inner Classes
    private static class HasValueEventListener implements ValueEventListener {
        private String path;
        private Doer<Boolean> doer;

        public HasValueEventListener(String path, Doer<Boolean> doer) {
            super();
            this.path = path;
            this.doer = doer;
        }

        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            doer.doFromResult(dataSnapshot.child(path).exists());
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
        }
    }

    private static class GetValueEventListener<T> implements ValueEventListener {
        private String path;
        private Doer<T> doer;

        public GetValueEventListener(String path, Doer<T> doer) {
            super();
            this.path = path;
            this.doer = doer;
        }

        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            doer.doFromResult((T) dataSnapshot.child(path).getValue());
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    }
}
