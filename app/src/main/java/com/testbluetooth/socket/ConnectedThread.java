package com.testbluetooth.socket;

import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by USER on 06/03/2016.
 */
public class ConnectedThread extends Thread {

    private BluetoothSocket bluetoothSocket;
    private InputStream inputStream;
    private OutputStream outputStream;

    private Handler mHandler;

    public ConnectedThread(BluetoothSocket bluetoothSocket, Handler mHandler) {
        this.bluetoothSocket = bluetoothSocket;
        this.mHandler = mHandler;

        InputStream tmpIn = null;
        OutputStream tmpOut = null;

        // Get the BluetoothSocket input and output streams
        try {
            tmpIn = bluetoothSocket.getInputStream();
            tmpOut = bluetoothSocket.getOutputStream();
        } catch (IOException e) {

        }

        inputStream = tmpIn;
        outputStream = tmpOut;
    }

    @Override
    public void run() {
        byte[] buffer = new byte[1024];

        // Keep listening to the InputStream while connected
        while (true) {
            try {
                // Read from the InputStream
                inputStream.read(buffer);

                // Send the obtained bytes to the UI Activity
                String message = new String(buffer);
                sendMessage(message);
            } catch (IOException e) {
                sendMessage("Disconnected");
                break;
            }
        }
    }

    public void write(String message) {
        byte[] buffer = message.getBytes();
        try {
            outputStream.write(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
