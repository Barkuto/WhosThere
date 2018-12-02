package whosthere.whosthere;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;


public class LoginFragment extends Fragment implements View.OnClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "Login";

    private Button mRegisterButton;
    private Button mLoginButton;
    private RegisterFragment mRegisterFragment;
    private FragmentManager mFragmentManager;
    private FirebaseAuth mAuth;
    private View mMainView;
    public ProgressDialog mProgressDialog;
    private EditText mEmailField;
    private EditText mPasswordField;
    private TextView mErrorLogin;
    private boolean mFirstLogin;


    //private TextView mStatusTextView;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mFragmentManager = getActivity().getSupportFragmentManager();
        this.mAuth = ((EntranceActivity)getActivity()).getmAuth();
        this.mFirstLogin = true;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);
        updateUI(currentUser);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_login, container, false);
        this.mMainView = v;
        v.findViewById(R.id.login_button).setOnClickListener(this);
        this.mRegisterButton = v.findViewById(R.id.register_button);
        this.mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("test", "Register Button Clicked!");
                // Start a new FragmentTransaction
                android.support.v4.app.FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();

                mRegisterFragment = new RegisterFragment();

                // Add the TitleFragment to the layout
                fragmentTransaction.replace(R.id.login_frag_cont,
                        mRegisterFragment);
                fragmentTransaction.addToBackStack(null);


                // Commit the FragmentTransaction
                fragmentTransaction.commit();
            }
        });


        this.mEmailField = v.findViewById(R.id.user_email);
        this.mPasswordField = v.findViewById(R.id.user_password);
        this.mLoginButton = v.findViewById(R.id.login_button);
        this.mErrorLogin = (TextView)v.findViewById(R.id.login_error_msg);

        return v;

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.login_button) {
            signIn(mEmailField.getText().toString(), mPasswordField.getText().toString());
        }
    }



    private void updateUI(FirebaseUser user) {
        hideProgressDialog();
        if (user != null) {
            Log.i(TAG, "FAHO LOGIN SUCCESSFUL!!!");
            Intent gotoMain = new Intent(getActivity(), NavigationBarActivity.class);
            gotoMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            getActivity().startActivity(gotoMain);
        } else if(!mFirstLogin) {
            mErrorLogin.setVisibility(View.VISIBLE);
        }
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = mEmailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailField.setError("Required.");
            valid = false;
        } else {
            mEmailField.setError(null);
        }

        String password = mPasswordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPasswordField.setError("Required.");
            valid = false;
        } else {
            mPasswordField.setError(null);
        }

        return valid;
    }

    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);
        if (!validateForm()) {
            return;
        }

        showProgressDialog();

        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener( getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            LoginFragment.this.mFirstLogin = false;
                            updateUI(null);
                        }

                        // [START_EXCLUDE]
                        if (!task.isSuccessful()) {
                            //TODO: set error login message
                            //mStatusTextView.setText(R.string.auth_failed);
                        }
                        hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
        // [END sign_in_with_email]
    }
    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getContext());
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    public void hideKeyboard(View view) {
        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        hideProgressDialog();
    }

}
