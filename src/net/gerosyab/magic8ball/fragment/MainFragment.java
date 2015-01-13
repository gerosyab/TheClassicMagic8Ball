package net.gerosyab.magic8ball.fragment;

import java.util.Timer;
import java.util.TimerTask;

import net.gerosyab.magic8ball.R;
import net.gerosyab.magic8ball.activity.MainActivity;
import net.gerosyab.magic8ball.util.MyLog;
import net.gerosyab.magic8ball.view.FrontView;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

@SuppressLint("NewApi")
public class MainFragment extends Fragment {

	FrontView frontView;
	Context context;
	MainActivity activity;
	TextSwitcher textSwitcher;
	TextView textView;
	
	static boolean isTimerRunning = false;
	static Timer timer;
	static Handler mHandler;
	Animation in;
	Animation out;
	
	int msgIdx = -1;
	int second = -1;
	boolean isEmptyString = true;
	boolean isBallTouched = false;
	
	String[] msg = {
			"Shake me",
			"or",
			"Touch me",
			"Miss me?",
			"Boring...",
			"Ask me",
			"and",
			"Find the Answer",
			"but",
			"Don't trust me too much",
			"I might be wrong",
			"Sometimes..."
	};
	
	public MainFragment(Context context, MainActivity activity) {
		super();
		this.context = context;
		this.activity = activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		MyLog.d("MainFragment", "onAttach");
		
		View view = inflater.inflate(R.layout.main_fragment_layout, container, false);
		frontView = (FrontView) view.findViewById(R.id.frontview);
		textSwitcher = (TextSwitcher) view.findViewById(R.id.textswitcher);
		textSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
			
			@Override
			public View makeView() {
				textView = new TextView(context);
				textView.setGravity(Gravity.CENTER);
				textView.setTextSize(20);
				textView.setTextColor(Color.WHITE);
				return textView;
			}
		});
		
		// for determine whether ball is clicked or not
		frontView.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int action = event.getAction();
				float cx = frontView.getCx();
				float cy = frontView.getCy();
				float radius = frontView.getRadius();
				
				switch(action){
				case MotionEvent.ACTION_DOWN:
					if(Math.pow(event.getX() - cx, 2) + Math.pow(event.getY() - cy, 2) <= radius * radius){
						isBallTouched = true;
					}
					break;
				case MotionEvent.ACTION_UP:
					if(isBallTouched){
						if(Math.pow(event.getX() - cx, 2) + Math.pow(event.getY() - cy, 2) <= radius * radius){
							isBallTouched = false;
							activity.shakingDetected();
						}
					}
					break;
				}
				return true;
			}
		});
		
		in = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
        out = AnimationUtils.loadAnimation(context, android.R.anim.fade_out);
		
        textSwitcher.setInAnimation(in);
        textSwitcher.setOutAnimation(out);
        
        timer = new Timer();
        
        // to show text message 3 seconds and not show 1 seconds in every 4 seconds 
        mHandler = new Handler() {
    	    public void handleMessage(Message msg) {
    	    	second++;
    	    	if(second % 4 == 3 && !isEmptyString){
    	    		isEmptyString = true;
    	    		textSwitcher.setText("");
    	    	}
    	    	else if(second % 4 == 0 && isEmptyString) {
    	    		isEmptyString = false;
    	    		msgIdx++;
        	        if(msgIdx >= MainFragment.this.msg.length){
        	        	msgIdx = 0;
        	        }
    	    		textSwitcher.setText(MainFragment.this.msg[msgIdx]);
    	    	}
    	        
    	        
    	    }
    	};
    	
    	startTimer();
        
		return view;
	}
	
	protected static void startTimer() {
	    isTimerRunning = true; 
	    
	    timer.scheduleAtFixedRate(new TimerTask() {
	        public void run() {
	            mHandler.obtainMessage(1).sendToTarget();
	        }
	    }, 0, 1000);
	};

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		MyLog.d("MainFragment", "onActivityCreated");
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onAttach(Activity activity) {
		MyLog.d("MainFragment", "onAttach");
		super.onAttach(activity);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		MyLog.d("MainFragment", "onCreate");
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onStart() {
		MyLog.d("MainFragment", "onStart");
		super.onStart();
	}

	@Override
	public void onResume() {
		MyLog.d("MainFragment", "onResume");
		super.onResume();
		msgIdx = -1;
		second = -1;
		isEmptyString = true;
	}

	@Override
	public void onPause() {
		timer.cancel();
		super.onPause();
	}

	@Override
	public void onStop() {
		MyLog.d("MainFragment", "onStop");
		super.onStop();
	}

	@Override
	public void onDestroy() {
		MyLog.d("MainFragment", "onDestroy");
		super.onDestroy();
	}

	@Override
	public void onDetach() {
		MyLog.d("MainFragment", "onDetach");
		super.onDetach();
	}
}
