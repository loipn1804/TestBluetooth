package com.testbluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;

import com.testbluetooth.socket.ConnectThread;
import com.testbluetooth.socket.ConnectedThread;

/**
 * Created by USER on 06/02/2016.
 */
public class ClientActivity extends BaseActivity implements View.OnClickListener, ConnectThread.ConnectedCallback {

    private ConnectThread connectThread;
    private BluetoothAdapter bluetoothAdapter;
    private ConnectedThread connectedThread;

    private Button btnStartClient;
    private Button btnSend;

    private String addressServer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);

        initView();
        initData();

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        addressServer = getIntent().getStringExtra("address");
        showToast(addressServer);
    }

    private void initView() {
        btnStartClient = (Button) findViewById(R.id.btnStartClient);
        btnSend = (Button) findViewById(R.id.btnSend);

        btnStartClient.setOnClickListener(this);
        btnSend.setOnClickListener(this);
    }

    private void initData() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (connectThread != null) {
            connectThread.cancel();
            connectThread.interrupt();
            connectThread = null;
        }
        if (connectedThread != null) {
            connectedThread.cancel();
            connectedThread.interrupt();
            connectedThread = null;
        }
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            showToast(msg.getData().getString("message", "no message"));
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSend:
                if (connectedThread != null) {
                    connectedThread.write("client message");
                }
                break;
            case R.id.btnStartClient:
                BluetoothDevice device = bluetoothAdapter.getRemoteDevice(addressServer);
                connectThread = new ConnectThread(device, mHandler, this);
                connectThread.start();
                break;
        }
    }

    @Override
    public void connect(BluetoothSocket socket) {
        connectedThread = new ConnectedThread(socket, mHandler);
        connectedThread.start();
    }
}
