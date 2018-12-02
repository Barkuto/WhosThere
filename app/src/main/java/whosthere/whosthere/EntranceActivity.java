package whosthere.whosthere;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class EntranceActivity extends AppCompatActivity {

    private Button mRegisterButton;
    private LoginFragment mLoginFragment;
    private RegisterFragment mRegisterFragment;
    private FragmentManager mFragmentManager;
    private FirebaseAuth mAuth;
    private AnimationDrawable animationDrawable;

    public FirebaseAuth getmAuth() {
        return mAuth;
    }

    public void setmFragmentManager(FragmentManager mFragmentManager) {
        this.mFragmentManager = mFragmentManager;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrance);
        mAuth = FirebaseAuth.getInstance();


        this.mLoginFragment = (LoginFragment) getSupportFragmentManager().findFragmentById(R.id.login_frag_cont);
        this.mRegisterFragment = (RegisterFragment) getSupportFragmentManager().findFragmentById(R.id.register_frag_cont);
        this.mFragmentManager = getSupportFragmentManager();
        this.mRegisterButton = findViewById(R.id.register_button);

        if (null == mFragmentManager.findFragmentById(R.id.login_frag_cont)) {

            // Start a new FragmentTransaction
            android.support.v4.app.FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();

            mLoginFragment = new LoginFragment();

            // Add the TitleFragment to the layout
            fragmentTransaction.add(R.id.login_frag_cont,
                    mLoginFragment);

            // Commit the FragmentTransaction
            fragmentTransaction.commit();

        }

        animationDrawable =(AnimationDrawable)findViewById(R.id.entrance_bg).getBackground();
        animationDrawable.setEnterFadeDuration(3000);
        animationDrawable.setExitFadeDuration(1000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        animationDrawable.start();

    }




}
