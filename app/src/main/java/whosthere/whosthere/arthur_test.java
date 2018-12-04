package whosthere.whosthere;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.app.DialogFragment;
import android.widget.Button;
import android.view.View;
import android.view.View.OnClickListener;


public class arthur_test extends AppCompatActivity {
    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.arthur_test);

        addListenerOnButton();
    }

    public void addListenerOnButton() {
        button = (Button) findViewById(R.id.button47);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                showDialog();
            }

        });

    }
    void showDialog() {
        DialogFragment newFragment = selection_dialog.newInstance();
        newFragment.show(getFragmentManager(), "dialog");
    }


}
