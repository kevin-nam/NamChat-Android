package com.knam.namchat.android;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class Client implements Runnable{

	// Networking variables
	// using UDP
	private DatagramSocket socket;
	private InetAddress ip;
	private Thread send, receive, listen;
	private String name, address;
	private int port, id = -1;
	
	protected ChatActivity context;
	
	private TextView mConsole;

	// constructor
	public Client(String name, InetAddress ip, int port, Context context) {
		this.name = name;
		this.ip = ip;
		this.port = port;
		this.context = (ChatActivity) context;
		
		
		
	}
	
	public int getID(){
		return id;
	}
	
	public void setID(int id){
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public String getAddress() {
		return address;
	}

	public int getPort() {
		return port;
	}
	
	@Override
	public void run(){
		
		try{
			socket = new DatagramSocket();
			//ip = InetAddress.getByName(address);
			socket.setBroadcast(true);
			socket.setReuseAddress(true);
			
			String connection = ("/c/" + name + "/e/");
			send(connection.getBytes());
			
			
			console("Hello " + name + ".");
			Log.d("UDP","Attemping connection to " + address);
			Log.d("UDP","Sending connection packet: " + connection);

		} catch (SocketException e) {
			e.printStackTrace();
		} catch (Exception e){
			e.printStackTrace();
		}
		
		listen();
		
	}
	
	
	// opening connection
	public boolean openConnection() {
		
		run();

		return true;
	}

	// receiving messages
	// waits for a packet
	public String receive() {
		byte[] data = new byte[1024];
		DatagramPacket packet = new DatagramPacket(data, data.length);
		while (true) {
			try {
				socket.receive(packet);
			} catch (IOException e) {
				e.printStackTrace();
			}
			String message = new String(packet.getData());
			return message;
		}
		
	}

	// sending message over UDP
	public void send(final byte[] data) {
		send = new Thread("Send") {
			@Override
			public void run() {
				DatagramPacket packet = new DatagramPacket(data, data.length,
						ip, port);
				try {
					socket.send(packet);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		send.start();
	}
	
	// listen method
		public void listen() {
			Log.d("UDP", "Listening...");
			listen = new Thread("Listen") {
				@Override
				public void run() {
					while (true) {
						String message = receive();
						if (message.startsWith("/c/")) {

							int newID = Integer
									.parseInt(message.split("/c/|/e/")[1]);
							console(newID + " is the ID.");
							console("Successfully connected to NamChat!");
							Log.d("UDP", "Received connection packet");
							id = newID;

						} else {
							console(message);
						}
					}
				}
			};
			listen.start();
		}
		
		public void console(final String message) {
			context.runOnUiThread(new Runnable(){
				@Override
				public void run(){
					context.mConsole.append(message + "\n\r");
				}
			});
			
		}

}
