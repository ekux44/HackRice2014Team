package com.kuxhausen.hackrice;

import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

public class ReviewDayActivity extends Activity {
	
	private GestureDetector mGestureDetector;
	
	public int imageNumber = 0;
	
	public Cursor pictureCursor;
	ImageView iOne;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setContentView(R.layout.activity_review);
		
		iOne = (ImageView) this.findViewById(R.id.imageView1);
	}
	public void onResume(){
		super.onResume();
		String[] pictureColumns = { DatabaseHelper.PictureDB.FILE_PATH, DatabaseHelper.PictureDB.PICTURE_ID };
		pictureCursor = this.getContentResolver().query(DatabaseHelper.PictureDB.PICTURES_URI, pictureColumns, null, null, null );
		
		loadImage(0);
		
		mGestureDetector = createGestureDetector(this);
	}
	
	
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
          if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
        	  openOptionsMenu();
              return true;
          } else if (keyCode == KeyEvent.KEYCODE_BACK){
        	  this.finish();
          }
          return false;
    }
	public void loadImage(int number){
		Log.e("loadImage", "totalCount:  "+pictureCursor.getCount());
		
		if(pictureCursor.moveToPosition(number)){
		
			String filepath = pictureCursor.getString(0); 
			Log.e("loadImage", ""+number+" "+filepath);
			
			Bitmap d = new BitmapDrawable(this.getResources(), filepath).getBitmap();
			int nh = (int) ( d.getHeight() * (512.0 / d.getWidth()) );
			Bitmap scaled = Bitmap.createScaledBitmap(d, 512, nh, true);
			iOne.setImageBitmap(scaled);
			
			imageNumber = number;
		}
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_review, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection. Menu items typically start another
        // activity, start a service, or broadcast another intent.
        switch (item.getItemId()) {
            case R.id.glass_keep_menu_item:
            	
            	//TODO mark saved, advance to comments voice input
            	return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private GestureDetector createGestureDetector(Context context) {
        GestureDetector gestureDetector = new GestureDetector(context);
            //Create a base listener for generic gestures
            gestureDetector.setBaseListener( new GestureDetector.BaseListener() {
                @Override
                public boolean onGesture(Gesture gesture) {
                    /*if (gesture == Gesture.TAP) {
                        // do something on tap
                        return true;
                    } else if (gesture == Gesture.TWO_TAP) {
                        // do something on two finger tap
                        return true;
                    } else*/ 
                    if (gesture == Gesture.SWIPE_RIGHT) {
                        // do something on right (forward) swipe
                    	loadImage(imageNumber+1);
                    	return true;
                    } else if (gesture == Gesture.SWIPE_LEFT) {
                        // do something on left (backwards) swipe
                    	loadImage(imageNumber-1);
                        return true;
                    }
                    return false;
                }
            });
            /*gestureDetector.setFingerListener(new GestureDetector.FingerListener() {
                @Override
                public void onFingerCountChanged(int previousCount, int currentCount) {
                  // do something on finger count changes
                }
            });
            gestureDetector.setScrollListener(new GestureDetector.ScrollListener() {
                @Override
                public boolean onScroll(float displacement, float delta, float velocity) {
                    // do something on scrolling
                	return false;
                }
            });*/
            return gestureDetector;
        }

        /*
         * Send generic motion events to the gesture detector
         */
        @Override
        public boolean onGenericMotionEvent(MotionEvent event) {
            if (mGestureDetector != null) {
                return mGestureDetector.onMotionEvent(event);
            }
            return false;
        }
}
