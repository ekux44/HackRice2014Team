package com.kuxhausen.hackrice;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class SnapRouletteActivity extends Activity {

	ImageView iOne;
	ProgressBar progress;

	private static final String SERVER_GET_URL = "http://192.168.43.159/posts/getRandom";

	public void onCreate(Bundle b) {
		super.onCreate(b);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		this.setContentView(R.layout.activity_snap_roulette);

		iOne = (ImageView) this.findViewById(R.id.snapRouletteAct);
		
		progress = (ProgressBar)this.findViewById(R.id.progressBar);
		
	}
	

	public void onResume() {
		super.onResume();

		/** show today's personal image selected for the pool **/
		// pick local filename
		// load file
		// Bitmap d = new BitmapDrawable(this.getResources(),
		// filepath).getBitmap();
		// showImage(d);

		/** load images from the community */
		// pull from server then showImage on it

		getRandomPic();
	}

	public void showImage(Bitmap d) {
		
		iOne.setVisibility(View.VISIBLE);
		int nh = (int) (d.getHeight() * (512.0 / d.getWidth()));
		Bitmap scaled = Bitmap.createScaledBitmap(d, 512, nh, true);
		iOne.setImageBitmap(scaled);
		progress.setVisibility(View.GONE);
		
	}

	private void getRandomPic() {
		ServerGetRandomTask getRandom = new ServerGetRandomTask();
		getRandom.execute(SERVER_GET_URL);
	}

	private class ServerGetRandomTask extends AsyncTask<String, Void, Bitmap> {

		protected Bitmap doInBackground(String... urls) {
			String urldisplay = urls[0];
			try {
				URL url = new URL(urldisplay);
			    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			    InputStream is = new BufferedInputStream(connection.getInputStream());
				return BitmapFactory.decodeStream(is);
			} catch (Exception e) {
				Log.w("ImageDownloader", e.getMessage());
				e.printStackTrace();
			}
			return null;
		}

		protected void onPostExecute(Bitmap result) {
			/* bmImage. */
			Log.w("ImageDownloader","IONE" + iOne);
			Log.w("ImageDownloader","result" + result);
			Display disp = getWindowManager().getDefaultDisplay();
			Point dim = new Point();
			disp.getSize(dim);
			int picWid = dim.x;
			int picHeight = (picWid*result.getHeight())/result.getWidth();
			Bitmap rBm = Bitmap.createScaledBitmap(result, picWid, picHeight, true);
			iOne.setImageBitmap(rBm);

			iOne.setVisibility(View.VISIBLE);
			progress.setVisibility(View.GONE);
		}
	}

}
