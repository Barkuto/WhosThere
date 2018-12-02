package whosthere.whosthere;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

class DownloadProfilePicTask extends AsyncTask<Friend, Void, Bitmap> {

    private Exception exception;
    private HttpURLConnection mHttpUrl;
    private WeakReference<Friend> mParent;
    private WeakReference<ImageView> mListItem;

    public DownloadProfilePicTask(Friend parent, ImageView listItem) {
        this.mParent = new WeakReference<Friend>(parent);
        this.mListItem = new WeakReference<ImageView>(listItem);
    }


    protected Bitmap doInBackground(Friend... friend) {
        InputStream in = null;

        //Log.i("temp", flagUrl);

        try {
            //URL url = new URL(urls[0].toString());
            //URL url = new URL("https://www.freeiconspng.com/uploads/profile-icon-1.png");
            while(friend[0].getProfilePicURL() == null){

            }
            URL url = new URL(friend[0].getProfilePicURL());

            mHttpUrl = (HttpURLConnection) url.openConnection();

            in = mHttpUrl.getInputStream();

            //Friend.this.profilePic = in;
            return BitmapFactory.decodeStream(in);

        } catch (MalformedURLException e) {
            Log.e("DEBUG", e.toString());
        } catch (IOException e) {
            Log.e("DEBUG", e.toString());
        } finally {
            try {
                if (null != in) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            mHttpUrl.disconnect();
        }
        return null;
        //return BitmapFactory.decodeResource(mParent.get().getResources(), R.drawable.stub);
    }

    protected void onPostExecute(Bitmap pic) {
        mParent.get().setProfilePic(pic);
        mListItem.get().setImageBitmap(pic);
    }
}