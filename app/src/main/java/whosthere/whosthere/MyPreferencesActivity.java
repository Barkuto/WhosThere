package whosthere.whosthere;


import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceActivity;
import android.widget.Toast;
import android.preference.SwitchPreference;


public class MyPreferencesActivity extends PreferenceActivity {
    private SwitchPreference swP;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction().replace(android.R.id.content, new PrefsFragment()).commit();

    }


    public static class PrefsFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener {
        private static final String switchkey = "notifications_location";
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

                case "distance_key":
                    String distance = sharedPreferences.getString(key,"");
                    setting[1]=Integer.parseInt(distance);
                    Toast.makeText(getActivity(), "distance_key: "+setting[1], Toast.LENGTH_LONG).show();
                    break;

                case "frequency_choice":
                    String freq = sharedPreferences.getString(key,"");
                    setting[2]=Integer.parseInt(freq);
                    Toast.makeText(getActivity(), "frequency_choice: "+setting[2], Toast.LENGTH_LONG).show();
                    break;
            }
        }


    }


}
