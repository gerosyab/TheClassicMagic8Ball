package net.gerosyab.magic8ball.activity;

import java.io.InputStream;

import net.gerosyab.magic8ball.R;
import net.gerosyab.magic8ball.util.MyLog;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class InfoActivity extends Activity {
	
	TextView gitText;
	TextView emailText;
	TextView licenseText;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.info_activity_layout);
		
		gitText = (TextView)findViewById(R.id.gitText);
		emailText = (TextView)findViewById(R.id.emailText);
		licenseText = (TextView)findViewById(R.id.gplText);
		
		gitText.setText(getResources().getString(R.string.github));
		Linkify.addLinks(gitText, Linkify.WEB_URLS);
		Linkify.addLinks(emailText, Linkify.EMAIL_ADDRESSES);
		
		readLicense();
	}
	
	private void readLicense(){
		try {
	        Resources res = getResources();
	        InputStream in_s1 = res.openRawResource(R.raw.gpl_3);

	        byte[] b1 = new byte[in_s1.available()];
	        in_s1.read(b1);
	        
	        licenseText.setText(new String(b1));
	    } catch (Exception e) {
	    	licenseText.setText("Error: can't show help.");
	    }
	}
}
