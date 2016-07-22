package ru.loredan13.btchat;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.util.Log;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: loredan
 * Date: 05.08.13
 * Time: 16:24
 * To change this template use File | Settings | File Templates.
 */
public class ExpectConnectThread extends Thread {
    MyActivity activity;
    BluetoothAdapter adapter;

    private boolean isRunning = true;

    public ExpectConnectThread(BluetoothAdapter btAdapter, MyActivity _activity) {
        Log.i("ExpectConnectThread", "started");
        adapter = btAdapter;
        activity = _activity;
        try {
            Ref.btServerSocket = btAdapter.listenUsingRfcommWithServiceRecord("BTChat", Ref.uuid);
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    @Override
    public void run() {
        while (isRunning) {
            BluetoothSocket socket = null;
            Log.i("Bluetooth", "Try");
            try {
                socket = Ref.btServerSocket.accept();
            } catch (IOException e) {
                Log.i("Bluetooth", "Fail");
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            if (socket != null) {
                Log.i("Bluetooth", "Connection detected");
                isRunning = false;
                Ref.btSocket = socket;
//                adapter.cancelDiscovery();
                Intent connectIntent = new Intent(activity, ChatActivity.class);
                connectIntent.putExtra("server", true);
                activity.startActivity(connectIntent);
                break;
            }
        }
    }

    public void close() {
        isRunning = false;
        try {
            Ref.btServerSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
