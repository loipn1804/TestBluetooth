package com.testbluetooth;

import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.testbluetooth.socket.AcceptThread;
import com.testbluetooth.socket.ConnectedThread;

/**
 * Created by USER on 06/02/2016.
 */
public class ServerActivity extends BaseActivity implements OnClickListener, AcceptThread.AcceptedCallback {

    private AcceptThread acceptThread;
    private ConnectedThread connectedThread;

    private Button btnStartServer;
    private Button btnSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);

        initView();
        initData();
    }

    private void initView() {
        btnStartServer = (Button) findViewById(R.id.btnStartServer);
        btnSend = (Button) findViewById(R.id.btnSend);

        btnStartServer.setOnClickListener(this);
        btnSend.setOnClickListener(this);
    }

    private void initData() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (acceptThread != null) {
            acceptThread.cancel();
            acceptThread = null;
        }
        if (connectedThread != null) {
            connectedThread.cancel();
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
                    connectedThread.write("server message");
                }
                break;
            case R.id.btnStartServer:
                acceptThread = new AcceptThread(mHandler, this);
                acceptThread.start();
                break;
        }
    }

    @Override
    public void accepted(BluetoothSocket socket) {
        connectedThread = new ConnectedThread(socket, mHandler);
        connectedThread.start();
    }
}
