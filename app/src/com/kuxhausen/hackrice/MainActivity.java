package com.kuxhausen.hackrice;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.FileObserver;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;

import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxException;
import com.dropbox.sync.android.DbxException.Unauthorized;
import com.dropbox.sync.android.DbxFile;
import com.dropbox.sync.android.DbxFileSystem;
import com.dropbox.sync.android.DbxPath;
import com.google.android.glass.media.CameraManager;
import com.google.api.services.drive.Drive;

public class MainActivity extends Activity {

	final static int intentRequestCode = 1;
	
    private static final String appKey = "p78hpiyefxd4p9z";
    private static final String appSecret = "hwjzoa1qoxjoqft";
    private static final int REQUEST_LINK_TO_DBX = 0;
    
    private static final String USERNAME = "alabount";
    private static final String FILE = "post";
    private static final String SERVER_URL = "http://10.210.78.112/posts/add";

    private DbxAccountManager mDbxAcctMgr;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
	    mDbxAcctMgr = DbxAccountManager.getInstance(getApplicationContext(), appKey, appSecret);
		
		takePicture();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	private static final int TAKE_PICTURE_REQUEST = 1;

	private void takePicture() {
	    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	    startActivityForResult(intent, TAKE_PICTURE_REQUEST);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (requestCode == TAKE_PICTURE_REQUEST && resultCode == RESULT_OK) {
	        String picturePath = data.getStringExtra(
	                CameraManager.EXTRA_PICTURE_FILE_PATH);
	        processPictureWhenReady(picturePath);
	    }

	    super.onActivityResult(requestCode, resultCode, data);
	}

	private void processPictureWhenReady(final String picturePath) {
	    final File pictureFile = new File(picturePath);

	    if (pictureFile.exists()) {
	        // The picture is ready; process it.
	    	uploadToDropbox(pictureFile);
	    } else {
	        // The file does not exist yet. Before starting the file observer, you
	        // can update your UI to let the user know that the application is
	        // waiting for the picture (for example, by displaying the thumbnail
	        // image and a progress indicator).

	        final File parentDirectory = pictureFile.getParentFile();
	        FileObserver observer = new FileObserver(parentDirectory.getPath()) {
	            // Protect against additional pending events after CLOSE_WRITE is
	            // handled.
	            private boolean isFileWritten;

	            @Override
	            public void onEvent(int event, String path) {
	                if (!isFileWritten) {
	                    // For safety, make sure that the file that was created in
	                    // the directory is actually the one that we're expecting.
	                    File affectedFile = new File(parentDirectory, path);
	                    isFileWritten = (event == FileObserver.CLOSE_WRITE
	                            && affectedFile.equals(pictureFile));

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
	
	private void uploadToDropbox(File pictureFile) {

        mDbxAcctMgr.startLink((Activity)this, REQUEST_LINK_TO_DBX);
		
        try {
//            DbxPath testPath = new DbxPath(DbxPath.ROOT, pictureFile.getName());
            DbxPath testPath = new DbxPath(DbxPath.ROOT, "TESTING.TXT");
			DbxFileSystem dbxFs = DbxFileSystem.forAccount(mDbxAcctMgr.getLinkedAccount());
			
			if (!dbxFs.exists(testPath)) {
                DbxFile testFile = dbxFs.create(testPath);
                try {
                    testFile.writeString("TESTING");
//                    testFile.writeFromExistingFile(pictureFile,false);
                    
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
	
	private boolean sendToServer(String picturePath) {
		File pictureFile = new File(picturePath);
		
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(SERVER_URL);
		HttpContext context = new BasicHttpContext();
		
		ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
		postParameters.add(new BasicNameValuePair("username", USERNAME));
		postParameters.add(new BasicNameValuePair("file", FILE));
		
		 try {
		        FileEntity entity = new FileEntity(pictureFile, "WILD_CARD");
		        post.setEntity(entity);

		        HttpResponse response = client.execute(post, context);
		    } catch (IOException exc) {
		        Log.e("Conor debugs", exc.getMessage());
		        return false;
		    }
		return true;
	}
}
