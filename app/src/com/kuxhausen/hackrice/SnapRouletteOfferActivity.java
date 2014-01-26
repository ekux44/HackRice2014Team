package com.kuxhausen.hackrice;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class SnapRouletteOfferActivity extends Activity implements OnClickListener{

	public void onCreate(Bundle b){
		super.onCreate(b);
		this.setContentView(R.layout.activity_snap_roulette_offer);
		
		((Button)this.findViewById(R.id.playButton)).setOnClickListener(this);
		((Button)this.findViewById(R.id.declineButton)).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.playButton:
			Intent i1 = new Intent(this, SnapRouletteActivity.class);
			this.startActivity(i1);
			break;
		case R.id.declineButton:
			Intent i2 = new Intent(this, ReviewDayActivity.class);
			this.startActivity(i2);
			break;
		}
	}
}
