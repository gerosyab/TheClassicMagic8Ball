package net.gerosyab.magicball.fragment;

import net.gerosyab.magicball.R;
import net.gerosyab.magicball.data.StaticData;
import net.gerosyab.magicball.util.MyLog;
import net.gerosyab.magicball.util.MyRandom;
import net.gerosyab.magicball.view.BackView;
import net.gerosyab.magicball.view.MsgView;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

@SuppressLint("NewApi")
public class MsgFragment extends Fragment {

	BackView backView;
	MsgView msgView;
	Context context;
	
	public MsgFragment(Context context) {
		super();
		this.context = context;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		MyLog.d("MsgFragment", "onCreateView");
		
		View view = inflater.inflate(R.layout.msg_fragment_layout, container, false);
		backView = (BackView)view.findViewById(R.id.backview);
		msgView = (MsgView)view.findViewById(R.id.msgview);
		
		msgView.setZOrderOnTop(true);
		msgView.getHolder().setFormat(PixelFormat.TRANSPARENT);
		
		msgView.setMsgIdx(MyRandom.getNum());
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		MyLog.d("MsgFragment", "onActivityCreated");
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onAttach(Activity activity) {
		MyLog.d("MsgFragment", "onAttach");
		super.onAttach(activity);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		MyLog.d("MsgFragment", "onCreate");
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onStart() {
		MyLog.d("MsgFragment", "onStart");
		super.onStart();
	}

	@Override
	public void onResume() {
		MyLog.d("MsgFragment", "onResume");
		super.onResume();
	}

	@Override
	public void onPause() {
		MyLog.d("MsgFragment", "onPause");
		super.onPause();
	}

	@Override
	public void onStop() {
		MyLog.d("MsgFragment", "onStop");
		super.onStop();
	}

	@Override
	public void onDestroy() {
		MyLog.d("MsgFragment", "onDestroy");
		super.onDestroy();
	}

	@Override
	public void onDetach() {
		MyLog.d("MsgFragment", "onDetach");
		super.onDetach();
	}

	public void setNewMessage() {
		MyLog.d("MsgFragment", "setNewMessage");
		if(msgView != null) {
			msgView.setMsgIdx(MyRandom.getNum());
			msgView.notifyMsgChanged();
		}
	}
	
}
