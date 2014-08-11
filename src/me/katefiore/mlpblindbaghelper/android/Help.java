package me.katefiore.mlpblindbaghelper.android;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import me.katefiore.mlpblindbaghelper.R;

/**
 * @author cab404
 */
public class Help extends Activity {
	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.help);
	}

	public void rate(View view) {
		startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=me.katefiore.mlpblindbaghelper")));
	}
}
