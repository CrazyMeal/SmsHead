package com.crazymeal.smshead.service;

import com.crazymeal.smshead.R;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class SmsHeadService extends Service{
	
	private WindowManager windowManager;
	private ImageView deleteZone;
	private LayoutParams params, deleteParams;
	private View smsHead;
	
	@Override
	public IBinder onBind(Intent arg0) {
		Toast.makeText(getApplicationContext(), "Service launched", Toast.LENGTH_SHORT).show();
		return null;
	}

	@Override public void onCreate() {
	    super.onCreate();

	    this.smsHead = LayoutInflater.from(this).inflate(R.layout.sms_head_layout, null);
	    TextView txt_title = (TextView) this.smsHead.findViewById(R.id.textView_message);
	    txt_title.setText("TEST");
	    
	    this.windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

	    //this.smsHead = new ImageView(this);
	    //this.smsHead.setImageResource(R.drawable.ic_message);

	    this.deleteZone = new ImageView(this);
	    this.deleteZone.setImageResource(R.drawable.ic_delete);
	    this.deleteZone.setVisibility(View.INVISIBLE);
	    
	    this.initParameters();
	    
	    this.smsHead.setOnTouchListener(new View.OnTouchListener() {
	    	  private int initialX;
	    	  private int initialY;
	    	  private float initialTouchX;
	    	  private float initialTouchY;

			@Override public boolean onTouch(View v, MotionEvent event) {
	    		  deleteZone.setVisibility(View.VISIBLE);
	    		  switch (event.getAction()) {
		    	      case MotionEvent.ACTION_DOWN:
		    	        initialX = params.x;
		    	        initialY = params.y;
		    	        initialTouchX = event.getRawX();
		    	        initialTouchY = event.getRawY();
		    	        return true;
		    	      case MotionEvent.ACTION_UP:
		    	    	  if(checkIfHovered()){
		    	    		  smsHead.setVisibility(View.INVISIBLE);
		    	    		  windowManager.removeView(smsHead);
		    	    	  }
		    	    	  deleteZone.setVisibility(View.INVISIBLE);
		    	        return true;
		    	      case MotionEvent.ACTION_MOVE:
		    	    	checkIfHovered();
		    	        params.x = initialX + (int) (event.getRawX() - initialTouchX);
		    	        params.y = initialY + (int) (event.getRawY() - initialTouchY);
		    	        windowManager.updateViewLayout(smsHead, params);
		    	        return true;
	    	    }
	    	    return false;
	    	  }
	    	});
	    this.windowManager.addView(this.deleteZone, this.deleteParams);
	    this.windowManager.addView(this.smsHead, this.params);
	  }
	
	@SuppressLint("NewApi")
	protected boolean checkIfHovered() {
		boolean hovered = false;
		if((this.params.x > (this.deleteParams.x - this.smsHead.getWidth()/2)) && (this.params.x < (this.deleteParams.x + this.deleteZone.getWidth() - this.smsHead.getWidth()/2))){
			if((this.params.y > (this.deleteParams.y - this.smsHead.getHeight()/2)) && (this.params.y < (this.deleteParams.y + this.deleteZone.getHeight() - this.smsHead.getHeight()/2)))
				hovered = true;;
		}
		return hovered;
	}

	@Override public void onDestroy(){
		super.onDestroy();
		if(smsHead != null) this.windowManager.removeView(this.smsHead);
		if(deleteZone != null) this.windowManager.removeView(this.deleteZone);
	}
	
	@SuppressLint("NewApi")
	public void initParameters(){
		this.params = new WindowManager.LayoutParams(
		        WindowManager.LayoutParams.WRAP_CONTENT,
		        WindowManager.LayoutParams.WRAP_CONTENT,
		        WindowManager.LayoutParams.TYPE_PHONE,
		        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
		        PixelFormat.TRANSLUCENT);
		    this.deleteParams = new WindowManager.LayoutParams(
			        WindowManager.LayoutParams.WRAP_CONTENT,
			        WindowManager.LayoutParams.WRAP_CONTENT,
			        WindowManager.LayoutParams.TYPE_PHONE,
			        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
			        PixelFormat.TRANSLUCENT);
		    
		    params.gravity = Gravity.TOP | Gravity.LEFT;
		    params.x = 0;
		    params.y = 100;
		    
		    
		    Display display = this.windowManager.getDefaultDisplay();
		    final Point size = new Point();
		    display.getSize(size);
		    float centerY = size.y/2;//i expect this is the y coordinate of center
		    float centerX = size.x/2;
		    
		    deleteParams.gravity = Gravity.TOP | Gravity.LEFT;
		    deleteParams.x = (int) centerX - 64;
		    deleteParams.y = (int) centerY - 64;
	}
}
