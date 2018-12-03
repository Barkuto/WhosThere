package whosthere.whosthere;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Switch;
import android.widget.CompoundButton;
import android.view.View;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.content.Context;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import de.hdodenhof.circleimageview.CircleImageView;

public class profile_page_arthur extends AppCompatActivity {
    Switch simpleSwitch;
    Button setting, logout;
    private FirebaseAuth mAuth;
    private Friend me;
    int share=1;
    protected Context mContext = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);
        this.mAuth = FirebaseAuth.getInstance();
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

        //this.me = (Friend)getIntent().getExtras().get("me");

        me = new Friend();
        me.setFullName((String)getIntent().getExtras().get("meName"));
        me.setUserName((String)getIntent().getExtras().get("meUserName"));
        me.setIncognito((boolean)getIntent().getExtras().get("meIncognito"));

        if(me.getProfilePic() == null) {
            DownloadProfilePicTask pdt = new DownloadProfilePicTask(me, (CircleImageView)findViewById(R.id.my_profile_pic));

            pdt.execute(me); //is this correct?
        } else {
            ((CircleImageView)findViewById(R.id.my_profile_pic)).setImageBitmap(me.getProfilePic());
        }

        ((TextView)findViewById(R.id.account_name)).setText(me.getUserName());
        ((TextView)findViewById(R.id.real_name)).setText(me.getFullName());
        ((Switch)findViewById(R.id.simpleSwitch)).setChecked(me.isIncognito());

        ((Switch)findViewById(R.id.simpleSwitch)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                profile_page_arthur.this.me.setIncognito(isChecked);
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
                mAuth.signOut();
                Intent gotoLogin = new Intent(profile_page_arthur.this, EntranceActivity.class);
                gotoLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(gotoLogin);
                //TODO--already clean the setting data, need to back to log in page
            }
        });



    }
}
