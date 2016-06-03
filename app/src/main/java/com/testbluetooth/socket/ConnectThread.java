package com.testbluetooth.socket;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by USER on 06/02/2016.
 */
public class ConnectThread extends Thread {

    public interface ConnectedCallback {
        void connect(BluetoothSocket socket);
    }

    // Name for the SDP record when creating server socket
    private static final String NAME_SECURE = "BluetoothChatSecure";

    // Unique UUID for this application
    private static final UUID MY_UUID_SECURE = UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a77");

    private BluetoothSocket bluetoothSocket;
    private BluetoothDevice bluetoothDevice;

    private BluetoothAdapter bluetoothAdapter;
    private Handler mHandler;

    private ConnectedCallback connectedCallback;

    public ConnectThread(BluetoothDevice bluetoothDevice, Handler mHandler, ConnectedCallback connectedCallback) {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        this.bluetoothDevice = bluetoothDevice;

        BluetoothSocket tmp = null;

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
