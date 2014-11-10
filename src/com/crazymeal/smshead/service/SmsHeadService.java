package com.crazymeal.smshead.service;

import java.util.HashMap;
import java.util.Map.Entry;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.SmsMessage;
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
	private int lastYPosition;
	private HashMap<View, LayoutParams> viewList;
	
	@Override
	public IBinder onBind(Intent arg0) {
		Toast.makeText(getApplicationContext(), "Service launched", Toast.LENGTH_SHORT).show();
		return null;
	}

	@Override public void onCreate() {
	    super.onCreate();
	    this.viewList = new HashMap<View, WindowManager.LayoutParams>();
	    
	    /*
	    this.smsHead = LayoutInflater.from(this).inflate(R.layout.sms_head_layout, null);
	    TextView messageView = (TextView) this.smsHead.findViewById(R.id.textView_message);
	    TextView senderView = (TextView) this.smsHead.findViewById(R.id.textView_sender);
	    senderView.setText("Paul");
	    messageView.setText("Salut henry");
	    */
	    this.lastYPosition = 100;
	    
	    this.windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
	    
	    this.deleteZone = new ImageView(this);
	    this.deleteZone.setImageResource(R.drawable.ic_delete);
	    this.deleteZone.setVisibility(View.INVISIBLE);
	    
	    this.initParameters();
	    this.windowManager.addView(this.deleteZone, this.deleteParams);
	  }
	
	

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		for(Entry<View, LayoutParams> v : this.viewList.entrySet()){
			this.windowManager.addView(v.getKey(), v.getValue());
		}
		
		IntentFilter filter = new IntentFilter();
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        filter.addCategory("com.crazymeal.smshead");
        filter.setPriority(1000);
        this.registerReceiver(new SmsReceiver(), filter);
        
		return super.onStartCommand(intent, flags, startId);
	}

	@Override public void onDestroy(){
		super.onDestroy();
		if(smsHead != null) this.windowManager.removeView(this.smsHead);
		if(deleteZone != null) this.windowManager.removeView(this.deleteZone);
	}
	
	@SuppressLint("NewApi")
	protected boolean checkIfHovered(View view) {
		boolean hovered = false;
		if((this.viewList.get(view).x > (this.deleteParams.x - view.getWidth()/2)) && (this.viewList.get(view).x < (this.deleteParams.x + this.deleteZone.getWidth() - view.getWidth()/2))){
			if((this.viewList.get(view).y > (this.deleteParams.y - view.getHeight()/2)) && (this.viewList.get(view).y < (this.deleteParams.y + this.deleteZone.getHeight() - view.getHeight()/2)))
				hovered = true;;
		}
		return hovered;
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
	public void addView(String sender, String message){
		
		LayoutParams tmpParams = new WindowManager.LayoutParams(
		        WindowManager.LayoutParams.WRAP_CONTENT,
		        WindowManager.LayoutParams.WRAP_CONTENT,
		        WindowManager.LayoutParams.TYPE_PHONE,
		        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
		        PixelFormat.TRANSLUCENT);
		tmpParams.gravity = Gravity.TOP | Gravity.LEFT;
		tmpParams.x = 0;
		tmpParams.y = lastYPosition + 100;
	    
		View tmpView = LayoutInflater.from(this).inflate(R.layout.sms_head_layout, null);
	    
		TextView messageView = (TextView) tmpView.findViewById(R.id.textView_message);
	    TextView senderView = (TextView) tmpView.findViewById(R.id.textView_sender);
	    senderView.setText(sender);
	    messageView.setText(message);
	    
	    this.viewList.put(tmpView, tmpParams);
		this.lastYPosition = tmpParams.y;
		
	    tmpView.setOnTouchListener(new View.OnTouchListener() {
	    	  private int initialX;
	    	  private int initialY;
	    	  private float initialTouchX;
	    	  private float initialTouchY;

			@Override 
			public boolean onTouch(View v, MotionEvent event) {
	    		  deleteZone.setVisibility(View.VISIBLE);
	    		  switch (event.getAction()) {
		    	      case MotionEvent.ACTION_DOWN:
		    	        initialX = viewList.get(v).x;
		    	        initialY = viewList.get(v).y;
		    	        initialTouchX = event.getRawX();
		    	        initialTouchY = event.getRawY();
		    	        return true;
		    	      case MotionEvent.ACTION_UP:
		    	    	  if(checkIfHovered(v)){
		    	    		  v.setVisibility(View.INVISIBLE);
		    	    		  removeView(v);
		    	    		  lastYPosition -= 100;
		    	    	  }
		    	    	  deleteZone.setVisibility(View.INVISIBLE);
		    	        return true;
		    	      case MotionEvent.ACTION_MOVE:
		    	    	//checkIfHovered(v);
		    	    	viewList.get(v).x = initialX + (int) (event.getRawX() - initialTouchX);
		    	    	viewList.get(v).y = initialY + (int) (event.getRawY() - initialTouchY);
		    	        windowManager.updateViewLayout(v, viewList.get(v));
		    	        return true;
	    	    }
	    	    return false;
	    	  }
	    	});
	    
		
		this.windowManager.addView(tmpView, tmpParams);
	}
	private void removeView(View view){
		this.windowManager.removeView(view);
		this.viewList.remove(view);
	}
	
	class SmsReceiver extends BroadcastReceiver{
		@SuppressLint("NewApi")
		@Override
		public void onReceive(Context context, Intent intent) {

	        Bundle bundle = intent.getExtras();
	        SmsMessage[] msgs = null;
	        if (bundle != null) {
	        	String sender = "", message = "";
	            Object[] pdus = (Object[]) bundle.get("pdus");
	            msgs = new SmsMessage[pdus.length];
	            for (int i = 0; i < msgs.length; i++) {
	                msgs[i] = SmsMessage.createFromPdu((byte[])    pdus[i]);

	                sender += msgs[i].getOriginatingAddress();
	                message += msgs[i].getMessageBody().toString();
	            }
	            addView(sender, message);
	        }
		}
	}
}
