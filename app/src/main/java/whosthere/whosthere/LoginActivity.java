package whosthere.whosthere;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Pattern;

import whosthere.whosthere.db.DB;
import whosthere.whosthere.db.Doer;
import whosthere.whosthere.db.UserInfo;

public class LoginActivity extends AppCompatActivity {

    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEmailView = findViewById(R.id.email);
        mPasswordView = findViewById(R.id.password);

        Button mEmailSignInButton = findViewById(R.id.sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        Button mRegisterButton = findViewById(R.id.register_button);
        mRegisterButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });

        Button mTestButton = findViewById(R.id.test_button);
        mTestButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent testIntent = new Intent(LoginActivity.this, BottomNavigation.class);
                LoginActivity.this.startActivity(testIntent);

            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    public void attemptLogin() {
        DB.verifyUserLogin(mEmailView.getText().toString(), mPasswordView.getText().toString(), new Doer<Boolean>() {
            @Override
            public void doFromResult(Boolean result) {
                Toast.makeText(getApplicationContext(), result ? "Logged in" : "Invalid Credentials", Toast.LENGTH_LONG).show();
                DB.getUserInfo(mEmailView.getText().toString(), new Doer<UserInfo>() {
                    @Override
                    public void doFromResult(UserInfo result) {
                        Log.i("Who", result.getFriends().toString());
                        Log.i("Who", result.getSettings().toString());
                    }
                });
            }
        });
    }

    public void register() {
        DB.addUser(parseUsername(), parseEmail(), parsePassword(), new Doer<Boolean>() {
            @Override
            public void doFromResult(Boolean result) {
                if (result) {
                    Toast.makeText(getApplicationContext(), "User Added", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "User Already Exists", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private String parseEmail() throws InvalidEmailFormatException {
        String text = mEmailView.getText().toString();
        Pattern p = Pattern.compile("[a-zA-Z0-9.]+@[a-zA-Z0-9.]+.[a-zA-Z0-9.]+");
        if (!text.contains("@")) throw new InvalidEmailFormatException("Email must contain @");
        return mEmailView.getText().toString();
    }

    private String parseUsername() throws InvalidEmailFormatException {
        return parseEmail().substring(0, parseEmail().indexOf("@"));
    }

    private String parsePassword() {
        return mPasswordView.getText().toString();
    }

    private class InvalidEmailFormatException extends Exception {
        InvalidEmailFormatException(String message) {
            super(message);
        }
    }
}

