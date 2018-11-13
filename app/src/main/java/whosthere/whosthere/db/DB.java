package whosthere.whosthere.db;

import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DB {
    public static final FirebaseDatabase fdb = FirebaseDatabase.getInstance();
    public static final DatabaseReference dbr = fdb.getReference();

    // Tables:
    // users -> username -> email, profile pic, friends, settings
    public static final String USERSTABLE = "users";
    // logins -> username -> password, salt
    public static final String LOGINSTABLE = "logins";
    // locations -> username : loc
    public static final String LOCATIONSTABLE = "locations";

    // Set Value Methods

    /**
     * Sets the given path value to the given value
     * path parameter best to use a DB#xxxPath() method
     *
     * @param path  Database path to set a value to
     * @param value Object to set the path value to
     */
    public static void setValue(String path, Object value) {
        dbr.child(sanitizePath(path)).setValue(value);
    }

    /**
     * Attempts to add a new user based on the given values
     *
     * @param username Username
     * @param email    Email
     * @param password Password
     * @param doer     Doer to execute whether or not username exists or not
     */
    public static void addUser(final String username, final String email, final String password, final Doer<Boolean> doer) {
        doIfHasPath(usersPath(username), new Doer<Boolean>() {
            @Override
            public void doFromResult(Boolean result) {
                // UserInfo Does not Exist, add user to DB
                if (!result) {
                    double salt = Math.random();
                    UserLogin userLogin = new UserLogin((password + salt).hashCode(), salt);
                    UserInfo userInfo = new UserInfo(email, "PICURL", new ArrayList<String>(), new ArrayList<Integer>());
                    setValue(usersPath(username), userInfo);
                    setValue(loginsPath(username), userLogin);
                    setUserLocation(username, "LOCATION");
                }
                if (doer != null) doer.doFromResult(!result);
            }
        });
    }

    /**
     * Sets a users location
     *
     * @param username Username of user to set location of
     * @param location Location to set for given user
     */
    public static void setUserLocation(final String username, final Object location) {
        doIfHasPath(usersPath(username), new Doer<Boolean>() {
            @Override
            public void doFromResult(Boolean result) {
                if (result) dbr.child(locationsPath(username)).setValue(location);
            }
        });
    }

    // "Get" Value Methods

    /**
     * Executes the given Doer with the value of whether or not the path exists
     *
     * @param path Path to check if exists
     * @param doer Doer to execute once checked
     */
    public static void doIfHasPath(String path, final Doer<Boolean> doer) {
        HasValueEventListener hvel = new HasValueEventListener(sanitizePath(path), new Doer<Boolean>() {
            @Override
            public void doFromResult(Boolean result) {
                if (doer != null) doer.doFromResult(result);
            }
        });
        dbr.addListenerForSingleValueEvent(hvel);
    }

    /**
     * Executes given Doer with the value associated with the given path
     *
     * @param path      Path to get value from
     * @param classType Class type of the data to retrieve
     * @param doer      Doer to execute once data is retrieved
     */
    public static void doWithValue(String path, Class classType, final Doer<Object> doer) {
        GetValueEventListener<Object> gvel = new GetValueEventListener<>(sanitizePath(path), classType, new Doer<Object>() {
            @Override
            public void doFromResult(Object result) {
                doer.doFromResult(result);
            }
        });
        dbr.addListenerForSingleValueEvent(gvel);
    }

    /**
     * Executes Doer with the given username's UserInfo
     *
     * @param username Username of user to get UserInfo of
     * @param doer     Doer to execute with retrieved UserInfo
     */
    public static void getUserInfo(final String username, final Doer<UserInfo> doer) {
        doIfHasPath(usersPath(username), new Doer<Boolean>() {
            @Override
            public void doFromResult(Boolean result) {
                if (result)
                    doWithValue(usersPath(username), UserInfo.class, new Doer<Object>() {
                        @Override
                        public void doFromResult(Object result) {
                            doer.doFromResult((UserInfo) result);
                        }
                    });
            }
        });
    }

    /**
     * Executes Doer with the given username's UserLogin
     *
     * @param username Username of user to get UserLogin of
     * @param doer     Doer to execute with retrieved UserInfo
     */
    public static void getUserLogin(final String username, final Doer<UserLogin> doer) {
        doIfHasPath(usersPath(username), new Doer<Boolean>() {
            @Override
            public void doFromResult(Boolean result) {
                if (result)
                    doWithValue(usersPath(username), UserLogin.class, new Doer<Object>() {
                        @Override
                        public void doFromResult(Object result) {
                            doer.doFromResult((UserLogin) result);
                        }
                    });
            }
        });
    }

    /**
     * Executes Doer with the given username's location data
     *
     * @param username Username of user to get location data of
     * @param doer     Doer to execute with retrieved location data
     */
    public static void getUserLocation(final String username, final Doer<Object> doer) {
        doIfHasPath(usersPath(username), new Doer<Boolean>() {
            @Override
            public void doFromResult(Boolean result) {
                if (result)
                    doWithValue(usersPath(username), Object.class, new Doer<Object>() {
                        @Override
                        public void doFromResult(Object result) {
                            doer.doFromResult(result);
                        }
                    });
            }
        });
    }

    // Verification Methods

    /**
     * Removes invalid characters from a given path
     *
     * @param path A path to sanitize
     * @return A path without invalid characters
     */
    private static String sanitizePath(String path) {
        return path.replace(".", "")
                .replace("#", "")
                .replace("$", "")
                .replace("[", "")
                .replace("]", "");
    }

    /**
     * Executes Doer with the result of whether or not given credentials are valid for username
     *
     * @param username Username to attempt to login to
     * @param password Password to use to login
     * @param doer     Doer to execute with result of login attempt
     */
    public static void verifyUserLogin(final String username, final String password, final Doer<Boolean> doer) {
        doIfHasPath(usersPath(username), new Doer<Boolean>() {
            @Override
            public void doFromResult(Boolean result) {
                if (result) {
                    // User exists
                    doWithValue(loginsPath(username), UserLogin.class, new Doer<Object>() {
                        @Override
                        public void doFromResult(Object result) {
                            UserLogin userLogin = (UserLogin) result;
                            doer.doFromResult((password + userLogin.salt).hashCode() == userLogin.password);
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

    public static String usersPath(String username) {
        return sanitizePath(USERSTABLE + "/" + username);
    }

    public static String loginsPath(String username) {
        return sanitizePath(LOGINSTABLE + "/" + username);
    }

    public static String locationsPath(String username) {
        return sanitizePath(LOCATIONSTABLE + "/" + username);
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
        private Class<T> classType;
        private Doer<T> doer;

        public GetValueEventListener(String path, Class<T> classType, Doer<T> doer) {
            super();
            if (path == null || classType == null)
                throw new RuntimeException("Path nor classType can be null.");
            this.path = path;
            this.classType = classType;
            this.doer = doer;
        }

        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            doer.doFromResult(dataSnapshot.child(path).getValue(classType));
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    }
}
