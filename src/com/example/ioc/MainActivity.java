package com.example.ioc;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.iocsrc.CheckNet;
import com.example.iocsrc.OnClick;
import com.example.iocsrc.ViewById;
import com.example.iocsrc.ViewUtils;

public class MainActivity extends Activity {
	@ViewById(R.id.tv_hello_world)
	private TextView mTvHelloWorld;

	@ViewById(R.id.tv_hello_world1)
	private TextView mTvHelloWorld1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ViewUtils.inject(this);
		mTvHelloWorld.setText("hahaha");
	}

	@OnClick(values = { R.id.tv_hello_world, R.id.tv_hello_world1 })
	@CheckNet
	private void onClick(View v) {
		Toast.makeText(this, "µã»÷ÊÂ¼þ", Toast.LENGTH_SHORT).show();
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
}
