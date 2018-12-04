package whosthere.whosthere;


import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.app.AlertDialog;
import android.content.Context;
import android.app.Dialog;
import android.widget.Toast;


public class selection_dialog extends DialogFragment  {
//    Context mcontext =getContext();
    int item[] = new int[] {1, 2, 3};
    static selection_dialog newInstance() {
        selection_dialog f = new selection_dialog();
        return f;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.option)
                .setItems(R.array.friend_option, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getContext(), "The choice value is"+item[which], Toast.LENGTH_SHORT).show();
                    }
                });
        return builder.create();
    }

}
