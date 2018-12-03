package whosthere.whosthere;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.widget.Toast;



public class MyPreferencesActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction().replace(android.R.id.content, new PrefsFragment()).commit();
    }


    public static class PrefsFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener {


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
            SharedPreferences myPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            SharedPreferences.Editor editor = myPrefs.edit();
            switch(key){


                //this option is used to determine a user wanna be interrupt by botification or not
                //if it is checked then we allow app send notification
                //else do not send notification or alarm
                case "distribution_set":
                    boolean check = sharedPreferences.getBoolean(key,true);
                    if(check == false){
                        editor.putString(key, "1").apply();
                    } else {
                        editor.putString(key,"0").apply();
                    }
                    break;


                    //when friend get close enough and then send notification
                //0-1 mile----1
                //1-5 miles----5
                // 5-10 miles----10
                case "distance_key":
                    String distance = sharedPreferences.getString(key,"");
                    if(distance==null){
                        editor.putString(key, "1").apply();
                        String test = myPrefs.getString(key,"");
                        Toast.makeText(getActivity(),test,Toast.LENGTH_LONG).show();
                    } else {
                        editor.putString(key, distance).apply();
                        String test = myPrefs.getString(key,"");
                        Toast.makeText(getActivity(),test,Toast.LENGTH_LONG).show();
                    }

                    break;



                    //used to determine the frequency that our app refresh the user location data and use for the app
                //0-1 mins----1
                //2-5 mins----5
                //6-10 mins----10
                //11-30 mins----30
                //31-60 mins----60
                case "frequency_choice":
                    String freq = sharedPreferences.getString(key,"");
                    if(freq == null){
                        editor.putString(key,"1").apply();
                        String test = myPrefs.getString(key,"");
                        Toast.makeText(getActivity(),test,Toast.LENGTH_LONG).show();
                    } else {
                        editor.putString(key,freq).apply();
                        String test = myPrefs.getString(key,"");
                        Toast.makeText(getActivity(),test,Toast.LENGTH_LONG).show();
                    }
                    break;


            }
        }


    }


}
