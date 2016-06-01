package com.testbluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private int REQUEST_ENABLE_BT = 123;
    private int REQUEST_DISCOVERABLE = 124;

    private BluetoothAdapter bluetoothAdapter;

    private Button btnEnable;
    private Button btnDiscoverable;
    private Button btnStartDiscover;
    private Button btnStopDiscover;

    private ListView lvBonded;
    private List<String> bondedList;
    private SimpleStringAdapter bondedAdapter;

    private ListView lvAvailable;
    private List<String> availableList;
    private SimpleStringAdapter availableAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initData();

        registerBroadcastReceiver();
    }

    private void initView() {
        btnEnable = (Button) findViewById(R.id.btnEnable);
        btnDiscoverable = (Button) findViewById(R.id.btnDiscoverable);
        btnStartDiscover = (Button) findViewById(R.id.btnStartDiscover);
        btnStopDiscover = (Button) findViewById(R.id.btnStopDiscover);

        lvBonded = (ListView) findViewById(R.id.lvBonded);
        lvAvailable = (ListView) findViewById(R.id.lvAvailable);

        btnEnable.setOnClickListener(this);
        btnDiscoverable.setOnClickListener(this);
        btnStartDiscover.setOnClickListener(this);
        btnStopDiscover.setOnClickListener(this);
    }

    private void initData() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        setBtnEnable();
        setBtnDiscoverable();

        setListBonded();
        if (bluetoothAdapter.isEnabled()) {
            availableList.clear();
            availableAdapter.setListData(availableList);
            bluetoothAdapter.startDiscovery();
        }
    }

    private void setBtnEnable() {
        if (bluetoothAdapter == null) {
            finish();
        } else {
            if (bluetoothAdapter.isEnabled()) {
                btnEnable.setText("Disable");
            } else {
                btnEnable.setText("Enable");
            }
        }
    }

    private void setBtnDiscoverable() {
        if (bluetoothAdapter == null) {
            finish();
        } else {
            if (bluetoothAdapter.getScanMode() == BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
                btnDiscoverable.setText("Discoverable (On)");
            } else {
                btnDiscoverable.setText("Discoverable (Off)");
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnEnable:
                if (bluetoothAdapter.isEnabled()) {
                    bluetoothAdapter.disable();
                } else {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                }
                break;
            case R.id.btnDiscoverable:
                Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0);
                startActivityForResult(discoverableIntent, REQUEST_DISCOVERABLE);
                break;
            case R.id.btnStartDiscover:
                if (bluetoothAdapter.isEnabled()) {
                    availableList.clear();
                    availableAdapter.setListData(availableList);
                    if (bluetoothAdapter.isDiscovering()) {
                        bluetoothAdapter.cancelDiscovery();
                    }
                    bluetoothAdapter.startDiscovery();
                }
                break;
            case R.id.btnStopDiscover:
                if (bluetoothAdapter.isEnabled()) {
                    bluetoothAdapter.cancelDiscovery();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                showToast("Bluetooth enabled");
            } else {
                showToast("Bluetooth disabled");
            }
            setBtnEnable();
        }
    }

    private void setListBonded() {
        availableList = new ArrayList<>();
        availableAdapter = new SimpleStringAdapter(this, availableList);
        lvAvailable.setAdapter(availableAdapter);

        bondedList = new ArrayList<>();
        bondedAdapter = new SimpleStringAdapter(this, bondedList);
        lvBonded.setAdapter(bondedAdapter);

        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        // If there are paired devices
        if (pairedDevices.size() > 0) {
            // Loop through paired devices
            for (BluetoothDevice device : pairedDevices) {
                // Add the name and address to an array adapter to show in a ListView
                bondedList.add(device.getName() + " - " + device.getAddress() + " - " + device.getUuids());
            }
            bondedAdapter.setListData(bondedList);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegisterBroadcastReceiver();
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                setBtnEnable();
            } else if (action.equals(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED)) {
                setBtnDiscoverable();
            } else if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // Add the name and address to an array adapter to show in a ListView
                showToast(device.getName() + "--" + device.getAddress());
                availableList.add(device.getName() + " - " + device.getAddress() + " - " + device.getUuids());
                availableAdapter.setListData(availableList);
            }
        }
    };

    private void registerBroadcastReceiver() {
        if (broadcastReceiver != null) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
            intentFilter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
            intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
            registerReceiver(broadcastReceiver, intentFilter);
        }
    }

    private void unRegisterBroadcastReceiver() {
        if (broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
        }
    }
}
