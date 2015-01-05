package com.knam.namchat.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {
	
	private Button mEnter;
	private EditText mName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mName = (EditText) findViewById(R.id.NameTextBox);
		
		mEnter = (Button) findViewById(R.id.JoinButton);
		
		mEnter.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				String name = mName.getText() + "";
				
				Toast.makeText(MainActivity.this, mName.getText() + " is the name.", Toast.LENGTH_LONG).show();
				
				Intent myIntent = new Intent(MainActivity.this,ChatActivity.class);
				myIntent.putExtra("USERNAME", name);
				MainActivity.this.startActivity(myIntent);
				finish();
				
			}
		});
		
		
	}

	
}
