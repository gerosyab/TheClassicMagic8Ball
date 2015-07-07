package net.gerosyab.magicball.data;

import net.gerosyab.magicball.R;

public class StaticData {
//	public static boolean DEBUG = true;
	public static boolean DEBUG = false;
	public static boolean VIEW_DEBUG = false;
	public static final String TAG = "magicball";
	public static final int vibTime = 300;
	public static final int[] msgID = {
		R.drawable.msg01, R.drawable.msg02, R.drawable.msg03, R.drawable.msg04, R.drawable.msg05,
		R.drawable.msg06, R.drawable.msg07, R.drawable.msg08, R.drawable.msg09, R.drawable.msg10,
		R.drawable.msg11, R.drawable.msg12, R.drawable.msg13, R.drawable.msg14, R.drawable.msg15,
		R.drawable.msg16, R.drawable.msg17, R.drawable.msg18, R.drawable.msg19, R.drawable.msg20};
	
	public static void setViewDebuggingMode(boolean status){
		VIEW_DEBUG = status;
	}
}

