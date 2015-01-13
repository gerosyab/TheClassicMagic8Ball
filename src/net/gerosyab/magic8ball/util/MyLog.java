package net.gerosyab.magic8ball.util;

import net.gerosyab.magic8bal.data.StaticData;
import android.util.Log;

public class MyLog {
	
	public static void i(String tag, String message)
	{
		 if(StaticData.DEBUG) Log.i(tag, message);
	}
	public static void w(String tag, String message) 
	{
		if(StaticData.DEBUG) Log.w(tag, message);
	}
	public static void d(String tag, String message) 
	{
		if(StaticData.DEBUG) Log.d(tag, message);
	}
	public static void e(String tag, String message) 
	{
		if(StaticData.DEBUG) Log.e(tag, message);
	}
}
