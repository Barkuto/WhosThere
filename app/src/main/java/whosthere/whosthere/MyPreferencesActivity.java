package whosthere.whosthere;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceActivity;
import android.widget.Toast;

public class MyPreferencesActivity extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction().replace(android.R.id.content, new PrefsFragment()).commit();
    }

    public static class PrefsFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener {

        int setting[] = {1,1,1};
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.prefer_arthur);

            // set texts correctly
            onSharedPreferenceChanged(null, "");
        }

        @Override
        public void onResume() {
            super.onResume();
            // Set up a listener whenever a key changes
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            super.onPause();
            // Set up a listener whenever a key changes
            getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            switch(key){

                //this option is used to determine a user wanna be interrupt by botification or not
                //if it is checked then we allow app send notification
                //else do not send notification or alarm
                case "distribution_set":
                    boolean check = sharedPreferences.getBoolean(key,true);
                    if(check == false){
                        setting[0]=0;
                        Toast.makeText(getActivity(), "notification_location: "+setting[0], Toast.LENGTH_LONG).show();
                    } else {
                        setting[0]=1;
                        Toast.makeText(getActivity(), "notification_location: "+setting[0], Toast.LENGTH_LONG).show();
                    }
                    break;

                    //when friend get close enough and then send notification
                //0-1 mile----1
                //1-5 miles----1
                // 5-10 miles----1
                case "distance_key":
                    String distance = sharedPreferences.getString(key,"");
                    setting[1]=Integer.parseInt(distance);
                    Toast.makeText(getActivity(), "distance_key: "+setting[1], Toast.LENGTH_LONG).show();
                    break;

                    //used to determine the frequency that our app refresh the user location data and use for the app
                //0-1 mins----1
                //2-5 mins----5
                //6-10 mins----10
                //11-30 mins----30
                //31-60 mins----60
                case "frequency_choice":

                    String freq = sharedPreferences.getString(key,"");
                    setting[2]=Integer.parseInt(freq);
                    Toast.makeText(getActivity(), "frequency_choice: "+setting[2], Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }
}
