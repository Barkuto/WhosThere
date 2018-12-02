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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

public class RegisterFragment extends Fragment implements View.OnClickListener {


    private Button mBackButton;
    private LoginFragment mLoginFragment;
    private FragmentManager mFragmentManager;
    private EditText mEmailField;
    private EditText mPasswordField;
    private EditText mCPasswordField;
    private EditText mFirstName;
/*
    private EditText mLastName;
*/
    private EditText mUserName;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mDatabase;
    private TextView mRegisterError;

    private static final String TAG = "Register";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mFragmentManager = getActivity().getSupportFragmentManager();
        this.mAuth = ((EntranceActivity)getActivity()).getmAuth();
        this.mDatabase = FirebaseFirestore.getInstance();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_register, container, false);


        View v = inflater.inflate(R.layout.fragment_register, container, false);

        v.findViewById(R.id.register_button).setOnClickListener(this);

        this.mEmailField = v.findViewById(R.id.user_email);
        this.mPasswordField = v.findViewById(R.id.user_password);
        this.mFirstName = v.findViewById(R.id.user_fname);
        /*this.mLastName = v.findViewById(R.id.user_lname);*/
        this.mUserName = v.findViewById(R.id.user_uname);
        this.mCPasswordField = v.findViewById(R.id.user_cpassword);
        this.mRegisterError = v.findViewById(R.id.register_error_msg);

                                                this.mBackButton = v.findViewById(R.id.back_button);
        this.mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("test", "Back Button Clicked!");
                if (mFragmentManager.getBackStackEntryCount() > 0){
                    boolean done = mFragmentManager.popBackStackImmediate();
                }
            }
        });
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

        String cpassword = mCPasswordField.getText().toString();
        if (TextUtils.isEmpty(cpassword)) {
            mCPasswordField.setError("Required.");
            valid = false;
        } else {
            mCPasswordField.setError(null);
        }


        if (password.length() < 6) {
            mPasswordField.setError("Password should be >= 6 chars.");
            valid = false;
        } else {
            mPasswordField.setError(null);
        }


        if(!TextUtils.equals(password, cpassword) && valid){
            mPasswordField.setError("Passwords do not match.");
            mCPasswordField.setError("Passwords do not match.");
            valid = false;
        }

        String firstName = mFirstName.getText().toString();
        if (TextUtils.isEmpty(firstName)) {
            mFirstName.setError("Required.");
            valid = false;
        } else {
            mFirstName.setError(null);
        }

/*
        String lastName = mLastName.getText().toString();
        if (TextUtils.isEmpty(lastName)) {
            mLastName.setError("Required.");
            valid = false;
        } else {
            mLastName.setError(null);
        }
*/

        String userName = mUserName.getText().toString();
        if (TextUtils.isEmpty(userName)) {
            mUserName.setError("Required.");
            valid = false;
        } else {
            mUserName.setError(null);
        }

        return valid;
    }

    public ProgressDialog mProgressDialog;

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

    private void createAccount(String email, String password,  String userName,  String firstName, String lastName) {
        Log.d(TAG, "createAccount:" + email);
        if (!validateForm()) {
            return;
        }

        showProgressDialog();

        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener( getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();


                            //Update Firstname, lastname, username fields in the database
                            //DocumentReference alovelaceDocumentRef = mDatabase.collection("users").document("alovelace");
                            Map<String, Object> newUser = new HashMap<>();
                            newUser.put("full_name", mFirstName.getText().toString());
                            newUser.put("user_name", mUserName.getText().toString());

                            mDatabase.collection("users").document(mAuth.getUid())
                                    .set(newUser)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(TAG, "DocumentSnapshot successfully written!");
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w(TAG, "Error writing document", e);
                                        }
                                    });


                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            updateUI(null);
                        }

                        // [START_EXCLUDE]
                        hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
        // [END create_user_with_email]
    }

    private void updateUI(FirebaseUser user) {
        hideProgressDialog();
        if (user != null) {
            Log.i(TAG, "FAHO REGISTER SUCCESSFUL!!!");
            Intent gotoMain = new Intent(getActivity(), NavigationBarActivity.class);
            gotoMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            getActivity().startActivity(gotoMain);
        } else {
            mRegisterError.setVisibility(View.VISIBLE);
        }
    }
    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.register_button) {
            createAccount(mEmailField.getText().toString(),
                    mPasswordField.getText().toString(),
                    mUserName.getText().toString(),
                    mFirstName.getText().toString(),
                    mFirstName.getText().toString());
        }
    }
}
