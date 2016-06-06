package com.testbluetooth.socket;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.io.IOException;

/**
 * Created by USER on 06/02/2016.
 */
public class ConnectThread extends Thread {

    public interface ConnectedCallback {
        void connect(BluetoothSocket socket);
    }

    private BluetoothSocket bluetoothSocket;
    private BluetoothDevice bluetoothDevice;

    private BluetoothAdapter bluetoothAdapter;
    private Handler mHandler;

    private ConnectedCallback connectedCallback;

    public ConnectThread(BluetoothDevice bluetoothDevice, Handler mHandler, ConnectedCallback connectedCallback) {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        this.bluetoothDevice = bluetoothDevice;

        BluetoothSocket tmp = null;

        java.util.UUID MY_UUID_SECURE = UUID_String.MY_UUID_SECURE;

        try {
            tmp = bluetoothDevice.createRfcommSocketToServiceRecord(MY_UUID_SECURE);
        } catch (IOException e) {
            e.printStackTrace();
        }

        bluetoothSocket = tmp;
        this.mHandler = mHandler;
        this.connectedCallback = connectedCallback;
    }

    @Override
    public void run() {
        setName("ConnectThreadSecure");

        bluetoothAdapter.cancelDiscovery();

        try {
            bluetoothSocket.connect();
        } catch (IOException e) {
            e.printStackTrace();
            try {
                bluetoothSocket.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            sendMessage("connect fail");
        }

        connectedCallback.connect(bluetoothSocket);
    }

    public void cancel() {
        try {
            bluetoothSocket.close();
        } catch (IOException e) {

        }
    }

    private void sendMessage(String message) {
        Message msg = mHandler.obtainMessage(0);
        Bundle bundle = new Bundle();
        bundle.putString("message", message);
        msg.setData(bundle);
        mHandler.sendMessage(msg);
    }
}
