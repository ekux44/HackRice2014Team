package com.kuxhausen.hackrice;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.FileObserver;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;

import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxException;
import com.dropbox.sync.android.DbxException.Unauthorized;
import com.dropbox.sync.android.DbxFile;
import com.dropbox.sync.android.DbxFileSystem;
import com.dropbox.sync.android.DbxPath;
import android.content.ContentValues;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.glass.media.CameraManager;

public class MainActivity extends Activity {

	final static int intentRequestCode = 1;

	private static final String appKey = "p78hpiyefxd4p9z";
	private static final String appSecret = "hwjzoa1qoxjoqft";
	private static final int REQUEST_LINK_TO_DBX = 0;

	private static final String USERNAME = "alabount";
	private static final String SERVER_POST_URL = "http://192.168.43.159/posts/add";
	private static final String SERVER_GET_URL = "http://192.168.43.159/posts/getRandom";

	private static final int TAKE_PICTURE_REQUEST = 1;

	private DbxAccountManager mDbxAcctMgr;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mDbxAcctMgr = DbxAccountManager.getInstance(getApplicationContext(),
				appKey, appSecret);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		
		setContentView(R.layout.activity_main);
		
		takePicture();
		//getRandomPic();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.action_review, menu);
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (requestCode == TAKE_PICTURE_REQUEST && resultCode == RESULT_OK) {
	        String picturePath = data.getStringExtra(
	                CameraManager.EXTRA_PICTURE_FILE_PATH);
	        processPictureWhenReady(picturePath);
	    }
	}

	private void takePicture() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(intent, TAKE_PICTURE_REQUEST);
	}

	private void processPictureWhenReady(final String picturePath) {
		final File pictureFile = new File(picturePath);

		if (pictureFile.exists()) {
			ContentValues mNewValues = new ContentValues();
			/*
			 * Sets the values of each column and inserts the
			 * word. The arguments to the "put" method are
			 * "column name" and "value"
			 */
			mNewValues.put(DatabaseHelper.PictureDB.FILE_PATH, picturePath);
			mNewValues.put(DatabaseHelper.PictureDB.PICTURE_ID, DatabaseHelper.PictureDB.PICTURE_ID);
			this.getContentResolver().insert(DatabaseHelper.PictureDB.PICTURES_URI, mNewValues);
			
			
			sendToServer(pictureFile);
			
			this.finish();
		} else {
			// The file does not exist yet. Before starting the file observer,
			// you
			// can update your UI to let the user know that the application is
			// waiting for the picture (for example, by displaying the thumbnail
			// image and a progress indicator).

			final File parentDirectory = pictureFile.getParentFile();
			FileObserver observer = new FileObserver(parentDirectory.getPath()) {
				// Protect against additional pending events after CLOSE_WRITE
				// is
				// handled.
				private boolean isFileWritten;

				@Override
				public void onEvent(int event, String path) {
					if (!isFileWritten) {
						// For safety, make sure that the file that was created
						// in
						// the directory is actually the one that we're
						// expecting.
						File affectedFile = new File(parentDirectory, path);
						isFileWritten = (event == FileObserver.CLOSE_WRITE && affectedFile
								.equals(pictureFile));

						if (isFileWritten) {
							stopWatching();

							// Now that the file is ready, recursively call
							// processPictureWhenReady again (on the UI thread).
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									processPictureWhenReady(picturePath);
								}
							});
						}
					}
				}
			};
			observer.startWatching();
		}
	}

	private void sendToServer(File pictureFile) {
		ServerUploadTask upload = new ServerUploadTask();
		upload.execute(pictureFile);
	}

	private void getRandomPic() {
		File randFile = new File("random.jpg");
		ServerGetRandomTask getRandom = new ServerGetRandomTask();
		getRandom.execute(randFile);
	}
		
	/*private void uploadToDropbox(File pictureFile) {

		mDbxAcctMgr.startLink((Activity) this, REQUEST_LINK_TO_DBX);

		try {
			// DbxPath testPath = new DbxPath(DbxPath.ROOT,
			// pictureFile.getName());
			DbxPath testPath = new DbxPath(DbxPath.ROOT, "TESTING.TXT");
			DbxFileSystem dbxFs = DbxFileSystem.forAccount(mDbxAcctMgr
					.getLinkedAccount());

			if (!dbxFs.exists(testPath)) {
				DbxFile testFile = dbxFs.create(testPath);
				try {
					testFile.writeString("TESTING");
					// testFile.writeFromExistingFile(pictureFile,false);

				} finally {
					testFile.close();
				}
			}
		} catch (Unauthorized e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DbxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
*/
	private class ServerGetRandomTask extends AsyncTask<File, Integer, Integer> {

		@Override
		protected Integer doInBackground(File... file) {
			try {
				URL url = new URL(SERVER_GET_URL);
				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();

				InputStream is = conn.getInputStream();
				Bitmap img = BitmapFactory.decodeStream(is);

				FileOutputStream out = new FileOutputStream(file[0]);
				img.compress(Bitmap.CompressFormat.JPEG, 90, out);
				out.close();

			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return null;
		}
	}

	private class ServerUploadTask extends AsyncTask<File, Integer, Integer> {
		@Override
		protected Integer doInBackground(File... pictureFile) {
			HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(SERVER_POST_URL);
			HttpContext context = new BasicHttpContext();

			String img_str = convertBase64(pictureFile[0]);

			ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
			postParameters.add(new BasicNameValuePair("username", USERNAME));
			postParameters.add(new BasicNameValuePair("post", img_str));

			try {
				// FileEntity entity = new
				// FileEntity((java.io.File)pictureFile[0], "image/jpg");
				// post.setEntity(entity);

				post.setEntity(new UrlEncodedFormEntity(postParameters));

				HttpResponse response = client.execute(post, context);
			} catch (IOException exc) {
				Log.e("Conor debugs", exc.getMessage());
			}

			Log.e("Conor debugs", "You've sent the message!!");

			return new Integer(0);
		}

		@Override
		protected void onProgressUpdate(Integer... progress) {

		}

		@Override
		protected void onPostExecute(Integer result) {
		}
	}

	private static String convertBase64(File file) {
		Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 30, stream);
		byte[] byte_arr = stream.toByteArray();
		return Base64.encodeToString(byte_arr, Base64.DEFAULT);
	}

}
