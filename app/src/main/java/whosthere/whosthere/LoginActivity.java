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

    // https://emailregex.com/
    private Pattern emailRegex = Pattern.compile("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])");

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

        Button openDrawer = findViewById(R.id.temp_button);
        openDrawer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent testIntent = new Intent(LoginActivity.this, BottomNavigation.class);
                Intent intent = new Intent(LoginActivity.this, NavigationBarActivity.class);
                LoginActivity.this.startActivity(intent);
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

                if (result) {
                    DB.getUserInfo(mEmailView.getText().toString(), new Doer<UserInfo>() {
                        @Override
                        public void doFromResult(UserInfo result) {
                            Intent intent = new Intent(LoginActivity.this, MapsActivity.class);
                            intent.putExtra("profile", result);
                            LoginActivity.this.startActivity(intent);
                        }
                    });
                }



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
        try {
            String email = parseEmail();
            String username = parseUsername();
            String password = parsePassword();

            DB.addUser(username, email, password, new Doer<Boolean>() {
                @Override
                public void doFromResult(Boolean result) {
                    if (result) {
                        Toast.makeText(getApplicationContext(), "User Added", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "User Already Exists", Toast.LENGTH_LONG).show();
                    }
                }
            });
        } catch (InvalidEmailFormatException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private String parseEmail() throws InvalidEmailFormatException {
        String text = mEmailView.getText().toString();
        if (!emailRegex.matcher(text).matches())
            throw new InvalidEmailFormatException("Invalid email format");
        return text;
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

