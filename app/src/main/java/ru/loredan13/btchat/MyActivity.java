package ru.loredan13.btchat;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;

public class MyActivity extends Activity {
    public BluetoothAdapter btAdapter;
    public ArrayList<BluetoothDevice> devices;
    public ArrayAdapter<String> btArrAdapter;
    public static final int REQUEST_ENABLE_BT = 1;
    public ListView lvDevices;
    public BroadcastReceiver btDeviceFound;
    public ExpectConnectThread expectConnectThread;
    public Handler handler;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.main);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.refresh:
                        btAdapter.startDiscovery();
                        return true;
                    default:
                        return false;
                }
            }
        });

        lvDevices = (ListView) findViewById(R.id.lv_devices);
        btArrAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1);
        lvDevices.setAdapter(btArrAdapter);

        lvDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.i("Item", "Click");
                btAdapter.cancelDiscovery();
                expectConnectThread.close();

                String name = (String) ((TextView) view).getText();
                BluetoothDevice selectedDevice = null;
                BluetoothSocket temp = null;
                for (BluetoothDevice device : devices) {
                    String devName = device.getName();
                    if (device.getName().equals(name)) {
                        selectedDevice = device;
                        break;
                    }
                }
                try {
                    temp = selectedDevice.createRfcommSocketToServiceRecord(Ref.uuid);
                } catch (IOException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
                Ref.btSocket = temp;
                try {
                    Ref.btSocket.connect();
                } catch (IOException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
                Intent connectIntent = new Intent(getApplicationContext(), ChatActivity.class);
                connectIntent.putExtra("server", false);
                connectIntent.putExtra("device", selectedDevice);
                startActivity(connectIntent);
            }
        });

        Log.i("Bluetooth", "Fetch adapter");
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!btAdapter.isEnabled()) {
            Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBT, REQUEST_ENABLE_BT);
        } else {
            start();
        }
    }

    private void start() {
        Log.i("Bluetooth", "Adapter locked, start server connection thread");
        expectConnectThread = new ExpectConnectThread(btAdapter, this);
        expectConnectThread.start();
        Log.i("Bluetooth", "Thread started, device search");
        devices = new ArrayList<>(btAdapter.getBondedDevices());
        for (BluetoothDevice device : devices) {
            btArrAdapter.add(device.getName());
        }

        btDeviceFound = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    BluetoothDevice foundDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    Log.d("foundDevice", foundDevice == null ? "null" : "OK");
                    if (foundDevice != null) {
                        devices.add(foundDevice);
                        btArrAdapter.add(foundDevice.getName());
                    }
                }
            }
        };

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(btDeviceFound, filter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                start();
            } else {
                finish();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(btDeviceFound);
    }
}
