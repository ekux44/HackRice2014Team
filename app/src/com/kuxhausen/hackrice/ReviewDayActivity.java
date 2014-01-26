package com.kuxhausen.hackrice;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

public class ReviewDayActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_review);
		
		ImageView iOne = (ImageView) this.findViewById(R.id.imageView1);
		
		String[] pictureColumns = { DatabaseHelper.PictureDB.FILE_PATH, DatabaseHelper.PictureDB.PICTURE_ID };
		Cursor pictureCursor = this.getContentResolver().query(DatabaseHelper.PictureDB.PICTURES_URI, pictureColumns, null, null, null );
		
		pictureCursor.moveToFirst();
		try {
			String filepath = pictureCursor.getString(0); 
			Log.e("wtf", filepath);
			//Bitmap bmImg = BitmapFactory.decodeFile(filepath);
			//iOne.setImageBitmap(bmImg);
			
			
			
			
			Bitmap d = new BitmapDrawable(this.getResources(), filepath).getBitmap();
			int nh = (int) ( d.getHeight() * (512.0 / d.getWidth()) );
			Bitmap scaled = Bitmap.createScaledBitmap(d, 512, nh, true);
			iOne.setImageBitmap(scaled);
		} catch(Exception e){
			Log.e("wtf","excpeiton after setImage");
			e.printStackTrace();
		}
	}
	
}
