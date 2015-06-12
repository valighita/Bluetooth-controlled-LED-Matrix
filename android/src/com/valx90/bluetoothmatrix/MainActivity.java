package com.valx90.bluetoothmatrix;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnGenericMotionListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

public class MainActivity extends Activity {

	public BluetoothAdapter btAdapter;

	Set<BluetoothDevice> devices;
	public ListView devicesList;
	public ArrayAdapter<String> devicesListArrayAdapter;
	private final String BTDebviceADDR = "98:D3:31:B2:66:66";

	private BluetoothSocket btConnectionSocket = null;

	private static final UUID MY_UUID = UUID
			.fromString("00001101-0000-1000-8000-00805F9B34FB");
	
	ArrayList<ImageView> images = new ArrayList<ImageView>();

	byte[] bytes = { 0, 0, 0, 0, 0, 0, 0, 0 };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		btAdapter = BluetoothAdapter.getDefaultAdapter();

		if (btAdapter == null) {
			Toast.makeText(getApplicationContext(),
					"Your device does not support Bluetooth",
					Toast.LENGTH_SHORT).show();

			this.finish();
		}

		if (!btAdapter.isEnabled()) {
			Intent turnOnBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(turnOnBT, 1);
		}

		devices = btAdapter.getBondedDevices();

		for (final BluetoothDevice device : devices) {
			if (device.getName().equals("HC-06")
					&& device.getAddress().equals(BTDebviceADDR)) {

				try {
					btConnectionSocket = device
							.createInsecureRfcommSocketToServiceRecord(MY_UUID);
					btConnectionSocket.connect();
				} catch (IOException e1) {
					e1.printStackTrace();
				}

			}

		}
		TableLayout table = (TableLayout) findViewById(R.id.table_layout);

		for(int i1=0; i1<8; i1++) {
			TableRow row = new TableRow(getApplicationContext());
			table.addView(row);
			
			for(int j1=0; j1 < 8; j1++) {
				final ImageView img = new ImageView(getApplicationContext());
				TableRow.LayoutParams params = new TableRow.LayoutParams(80, 80);
				img.setLayoutParams(params);
				img.setImageResource(R.drawable.led_off);
				img.setScaleType(ImageView.ScaleType.FIT_XY);
				row.addView(img);
				
				images.add(img);
				
				final int i = i1;
				final int j = 7 - j1;
				
				img.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						byte b = bytes[i];
						if( ((1 << j) & b) == 0 ) { // este stins
							img.setImageResource(R.drawable.led_on);
							bytes[i] |= 1 << j;
						} else {
							img.setImageResource(R.drawable.led_off);
							bytes[i] &= ~(1 << j);
						}
						
						sendBytes();
					}

				});
			
			}
			
		}
		
		Button clearBtn = (Button) findViewById(R.id.button1);
		clearBtn.setText("Clear");
		
		clearBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				for(int i = 0; i< 8; i++) {
					bytes[i] = 0;
					for(ImageView img : images) {
						img.setImageResource(R.drawable.led_off);
					}
					sendBytes();
				}
			}
		});
	
		if (btConnectionSocket == null) {
			Toast.makeText(getApplicationContext(),
					"Could not connect to device", Toast.LENGTH_SHORT).show();
			onDestroy();
		}

	}

	private void sendBytes() {
			try {
				
				for(int i=1; i < 8; i++) {
					btConnectionSocket.getOutputStream().write(bytes[i]);
				}
				btConnectionSocket.getOutputStream().write(bytes[0]);
			} catch (IOException e) {
				e.printStackTrace();
			}
		
	}
	
	@Override
	protected void onDestroy() {
		if (btConnectionSocket != null) {
			try {
				btConnectionSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
