package com.crazymeal.smshead;

import com.crazymeal.smshead.service.SmsHeadService;

import android.support.v7.app.ActionBarActivity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Messenger;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends ActionBarActivity {
	private Messenger mService = null;
	private boolean mBound;
	
	private ServiceConnection mConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			setmService(new Messenger(service));
			setmBound(true);
		}

		public void onServiceDisconnected(ComponentName className) {
			setmService(null);
			setmBound(false);
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_main);
		startService(new Intent(this.getApplicationContext(), SmsHeadService.class));
	}

	@Override
	public void onStart() {
		super.onStart();
		bindService(new Intent(this, SmsHeadService.class), mConnection, Context.BIND_AUTO_CREATE);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (mBound) {
			unbindService(mConnection);
			mBound = false;
		}
		//stopService(new Intent(this.getApplicationContext(), SmsHeadService.class));
	}

	public Messenger getmService() {
		return mService;
	}

	public void setmService(Messenger mService) {
		this.mService = mService;
	}

	public boolean ismBound() {
		return mBound;
	}

	public void setmBound(boolean mBound) {
		this.mBound = mBound;
	}
	
}
