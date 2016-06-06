package com.testbluetooth.socket;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.io.IOException;

/**
 * Created by USER on 06/02/2016.
 */
public class AcceptThread extends Thread {

    public interface AcceptedCallback {
        void accepted(BluetoothSocket socket);
    }

    private BluetoothServerSocket bluetoothServerSocket;

    private BluetoothAdapter bluetoothAdapter;
    private Handler mHandler;

    private AcceptedCallback acceptedCallback;

    public AcceptThread(Handler mHandler, AcceptedCallback acceptedCallback) {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothServerSocket tmp = null;

        String NAME_SECURE = UUID_String.NAME_SECURE;
        java.util.UUID MY_UUID_SECURE = UUID_String.MY_UUID_SECURE;

        try {
            tmp = bluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME_SECURE, MY_UUID_SECURE);
        } catch (IOException e) {

        }

        bluetoothServerSocket = tmp;
        this.mHandler = mHandler;
        this.acceptedCallback = acceptedCallback;
    }

    @Override
    public void run() {
        setName("AcceptThreadSecure");

        BluetoothSocket socket = null;

        // Listen to the server socket if we're not connected
        while (true) {
            try {
                // This is a blocking call and will only return on a
                // successful connection or an exception
                socket = bluetoothServerSocket.accept();
            } catch (IOException e) {
                sendMessage("accept fail");
                break;
            }

            // If a connection was accepted
            if (socket != null) {
                synchronized (this) {
                    sendMessage("accepted");
                    acceptedCallback.accepted(socket);
//                    break;
                }
            }
        }
    }

    public void cancel() {
        try {
            bluetoothServerSocket.close();
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
