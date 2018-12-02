package whosthere.whosthere;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Switch;
import android.widget.CompoundButton;
import android.view.View;
import android.content.SharedPreferences;
import android.widget.Toast;
import android.preference.PreferenceManager;
import android.content.Context;

public class profile_page_arthur extends AppCompatActivity {
    Switch simpleSwitch;
    Button setting, logout;
    int share=1;
    protected Context mContext = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);

        simpleSwitch = (Switch) findViewById(R.id.simpleSwitch);
        setting = (Button) findViewById(R.id.setting_button_arthur);
        logout = (Button) findViewById(R.id.log_out);
        simpleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){

            //TODO--this is the listener to check user wnat hide o the map or not
            //isChecked means dont show him up on the map
            //else show up on the map

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    share = 1;
                } else {
                    share = 0;
                }
            }

        });


        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(profile_page_arthur.this, MyPreferencesActivity.class);
                startActivity(i);
            }
        });


        logout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                SharedPreferences myPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
                SharedPreferences.Editor editor = myPrefs.edit();
                editor.clear().commit();
                //TODO--already clean the setting data, need to back to log in page
            }
        });

    }
}
