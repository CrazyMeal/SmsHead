package com.crazymeal.smshead.service;

import java.util.HashMap;
import java.util.Map.Entry;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.IBinder;
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

import com.crazymeal.smshead.R;

public class SmsHeadService extends Service{
	
	private WindowManager windowManager;
	private ImageView deleteZone;
	private LayoutParams params, deleteParams;
	private View smsHead;
	private HashMap<View, LayoutParams> viewList;
	
	@Override
	public IBinder onBind(Intent arg0) {
		Toast.makeText(getApplicationContext(), "Service launched", Toast.LENGTH_SHORT).show();
		return null;
	}

	@Override public void onCreate() {
	    super.onCreate();
	    this.viewList = new HashMap<View, WindowManager.LayoutParams>();
	    
	    this.smsHead = LayoutInflater.from(this).inflate(R.layout.sms_head_layout, null);
	    TextView messageView = (TextView) this.smsHead.findViewById(R.id.textView_message);
	    TextView senderView = (TextView) this.smsHead.findViewById(R.id.textView_sender);
	    senderView.setText("Paul");
	    messageView.setText("Salut henry");
	    
	    this.windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
	    
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
		    	    		  removeView(v);
		    	    		  if(viewList.size() == 0)
		    	    			  windowManager.removeView(deleteZone);
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
	    this.viewList.put(this.smsHead, this.params);
	    
	    this.windowManager.addView(this.deleteZone, this.deleteParams);
	    //this.windowManager.addView(this.smsHead, this.params);
	  }
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		for(Entry<View, LayoutParams> v : this.viewList.entrySet()){
			this.windowManager.addView(v.getKey(), v.getValue());
		}
		return super.onStartCommand(intent, flags, startId);
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
	private void removeView(View view){
		this.windowManager.removeView(view);
		this.viewList.remove(view);
	}
}
