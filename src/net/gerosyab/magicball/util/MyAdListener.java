package net.gerosyab.magicball.util;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;

public class MyAdListener extends AdListener {

	@Override
	public void onAdLoaded() {
		MyLog.d("AdListener", "onAdLoaded()");
	}

	@Override
	public void onAdFailedToLoad(int errorCode) {
		String errorReason = "";
		switch (errorCode) {
		case AdRequest.ERROR_CODE_INTERNAL_ERROR:
			errorReason = "Internal error";
			break;
		case AdRequest.ERROR_CODE_INVALID_REQUEST:
			errorReason = "Invalid request";
			break;
		case AdRequest.ERROR_CODE_NETWORK_ERROR:
			errorReason = "Network Error";
			break;
		case AdRequest.ERROR_CODE_NO_FILL:
			errorReason = "No fill";
			break;
		}
		MyLog.d("AdListener", String.format("onAdFailedToLoad(%s)", errorReason));
	}

	@Override
	public void onAdOpened() {
		MyLog.d("AdListener", "onAdOpened()");
	}

	@Override
	public void onAdClosed() {
		MyLog.d("AdListener", "onAdClosed()");
	}

	@Override
	public void onAdLeftApplication() {
		MyLog.d("AdListener", "onAdLeftApplication()");
	}
}
