package com.kuxhausen.hackrice;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.widget.ImageView;

public class SnapRouletteActivity extends Activity {
	
	ImageView iOne;
	
	public void onCreate(Bundle b){
		super.onCreate(b);
		this.setContentView(R.layout.activity_snap_roulette);
		
		iOne = (ImageView) this.findViewById(R.id.imageView1);
	}

	public void onResume(){
		super.onResume();
		
		/** show today's personal image selected for the pool **/
		//pick local filename
		//load file
		//Bitmap d = new BitmapDrawable(this.getResources(), filepath).getBitmap();
		//showImage(d);
		
		/** load images from the community */
		//pull from server then showImage on it
		
		
	}
	
	
	public void showImage(Bitmap d){
		
		int nh = (int) ( d.getHeight() * (512.0 / d.getWidth()) );
		Bitmap scaled = Bitmap.createScaledBitmap(d, 512, nh, true);
		iOne.setImageBitmap(scaled);
	}
}
