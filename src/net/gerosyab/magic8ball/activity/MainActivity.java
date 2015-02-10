package net.gerosyab.magic8ball.activity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import net.gerosyab.magic8bal.data.StaticData;
import net.gerosyab.magic8ball.R;
import net.gerosyab.magic8ball.fragment.MainFragment;
import net.gerosyab.magic8ball.fragment.MsgFragment;
import net.gerosyab.magic8ball.util.MyLog;
import net.gerosyab.magic8ball.util.Shaker;
import net.gerosyab.magic8ball.util.ToastAdListener;
import android.annotation.SuppressLint;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v4.app.FragmentActivity;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

@SuppressLint({ "NewApi" })
public class MainActivity extends FragmentActivity implements Shaker.Callback {
	
	Context context;
	Toast toast;
	LinearLayout motherLinear;
	LinearLayout contentLinear;
	Boolean isMain = false;
	Boolean isFirstPaused = false;
	static Shaker shaker;
	
	FragmentManager fragmentManager;
	FragmentTransaction transaction;
	
	MainFragment mainFragment;
	MsgFragment msgFragment;
	
	public static Vibrator vibrator;
	
	private AdView mAdView;
	
	private boolean mBackKeyFlag = false;
	
	private Handler mHandler = new Handler(){
		public void handleMessage(Message msg){
			if(msg.what == 0){
				mBackKeyFlag = false;
			}
		}
	};
	
	 protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity_layout);
		
		context = getApplicationContext();
		toast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
		
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		
		motherLinear = (LinearLayout) findViewById(R.id.mother_linear);
		contentLinear = (LinearLayout) findViewById(R.id.content_linear);
		
		fragmentManager = getFragmentManager();
		transaction = fragmentManager.beginTransaction();
		
		mainFragment = new MainFragment(context, this);
		msgFragment = new MsgFragment(context);
		
		transaction.add(R.id.content_linear, mainFragment);
		
		transaction.commit();
		
		mAdView = (AdView) findViewById(R.id.adView);
		
		
		if (StaticData.DEBUG) {
			mAdView.setAdListener(new ToastAdListener(this));
			
			TelephonyManager telephony = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
			String my_phone_num = telephony.getLine1Number(); // device phone number
			String my_phone_deviceid = telephony.getDeviceId(); // device id

			MyLog.d("ads", "phone number : " + my_phone_num + ", device id : " + my_phone_deviceid);

			mAdView.loadAd(new AdRequest.Builder()
					.addTestDevice(my_phone_deviceid)
					.addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build());
		} else {
			mAdView.loadAd(new AdRequest.Builder().build());
		}
	};
	
	@Override
	public void onResume() {
		MyLog.d("MainActivity", "onResume");
		super.onResume();
		
		if (mAdView != null) {
			mAdView.resume();
		}
		
		if(shaker == null){
			shaker = new Shaker(context, this);
		}
		shaker.open();
	}

	@Override
	public void onPause() {
		MyLog.d("MainActivity", "onPause");
		super.onPause();
		
		if (mAdView != null) {
			mAdView.pause();
		}
		
		if(shaker != null){
			shaker.close();
		}
	}

	/** Called before the activity is destroyed. */
	@Override
	public void onDestroy() {
		MyLog.d("MainActivity", "onDestroy");
		// Destroy the AdView.
		if (mAdView != null) {
			mAdView.destroy();
		}
		super.onDestroy();
	}
	
	@Override
	public void onBackPressed() {
		// Catch back action and pops from backstack
	    // (if you called previously to addToBackStack() in your transaction)
	    if (fragmentManager.getBackStackEntryCount() > 0){
	    	fragmentManager.popBackStack();
	    }
	    // Default action on back pressed
	    else if (!mBackKeyFlag) {
			sendToastMessage("Back Again to Exit");
			mBackKeyFlag = true;
			mHandler.sendEmptyMessageDelayed(0, 2000);
		}
		else {
			super.onBackPressed();
		}

	}
	
	@Override
	public void onShakingDetected() {
		MyLog.d("MainActivity", "onShakingDetected");
		vibrator.vibrate(StaticData.vibTime);
		if (fragmentManager.getBackStackEntryCount() > 0){
	    	if(msgFragment != null) {
	    		MyLog.d("MainActivity", "setNewMessage()");
	    		msgFragment.setNewMessage();
	    	}
	    }
		else {
			transaction = fragmentManager.beginTransaction();
			transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE|FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
			transaction.replace(R.id.content_linear, msgFragment);
			transaction.addToBackStack(null);
//			transaction.commit();
			transaction.commitAllowingStateLoss();
		}
	}

	protected void sendToastMessage(String message){
		toast.setText(message);
		toast.show();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add("Information");
		return true;
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		
		if(item.getTitle().equals("Information")){
			Intent i = new Intent(context, InfoActivity.class);
			startActivity(i);
		}
		
		return true;
	}
	
	public static Shaker getShagerInstance(){
		return shaker;
	}
	
}
