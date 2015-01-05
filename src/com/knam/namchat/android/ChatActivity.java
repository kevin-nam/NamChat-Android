package com.knam.namchat.android;

import java.io.IOException;
import java.net.InetAddress;

import android.content.Context;
import android.content.Intent;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ChatActivity extends ActionBarActivity{

	Button mSend;
	TextView mConsole;
	EditText mMessageBox;

	private Client client;
	Thread listen, run, connect;

	final String ADDRESS = "localhost";
	final int PORT = 8192;
	
	private Context mContext;
	private Context mContext2;
	
	private InetAddress ip;
	
	String name;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);

		Intent intent = getIntent();
		name = intent.getStringExtra("USERNAME");

		mSend = (Button) findViewById(R.id.SendMessage);
		mConsole = (TextView) findViewById(R.id.Console);
		mMessageBox = (EditText) findViewById(R.id.MessageBox);
		
		//client = new Client(name, ADDRESS, PORT);
		//connect();
		
		console("Attempting to connect to NamChat...");
		System.out.println("Connecting...");
		
		mContext = getApplicationContext();
		mContext2 = ChatActivity.this;
		
		try{
		ip = getBroadcastAddress();
		} catch(Exception e){
			e.printStackTrace();
		}
		
		Thread t = new Thread(client = new Client(name, ip, PORT, mContext2));
		t.start();

		mSend.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String message = mMessageBox.getText() + "";
				send(message);
			}
		});
		
		

	}
	
	@Override
	public void onBackPressed(){
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
		String dc = "/dc/" + client.getID() + "/e/";
		send(dc);
		finish();
	}
	
	public InetAddress getBroadcastAddress() throws IOException{
		WifiManager wifi = (WifiManager)mContext.getSystemService(Context.WIFI_SERVICE);
		DhcpInfo dhcp = wifi.getDhcpInfo();
		
		int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
		byte[] quads = new byte[4];
		for(int k =0; k<4;k++)
			quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
		return InetAddress.getByAddress(quads);
	}

	public void console(String message) {
		mConsole.append(message + "\n\r");
	}

	public void send(String message) {
		if (message.equals("")) {
			return;
		}
		if (message.startsWith("/dc/")) {
			client.send(message.getBytes());
		}

		else {
			client.send(("/m/" + client.getName() + ": " + message).getBytes());
			mMessageBox.setText("");
		}
	}


	/*public void connect() {
		connect = new Thread("Connect") {
			@Override
			public void run() {
				
				boolean connected = client.openConnection();
				if (!connected) {
					System.err.println("Connection failed!");
					console("Connection failed!");
				}
				console("Connecting to " + ADDRESS + ":" + 8192 + "...");
			}
		};
		connect.start();
	}*/

	

}
