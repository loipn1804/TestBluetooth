package com.testbluetooth;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

@TargetApi(Build.VERSION_CODES.M)
public class MainActivity extends BaseActivity implements View.OnClickListener {

    private int REQUEST_ENABLE_BT = 123;
    private int REQUEST_DISCOVERABLE = 124;

    private BluetoothAdapter bluetoothAdapter;

    private Button btnEnable;
    private Button btnDiscoverable;
    private Button btnStartDiscover;
    private Button btnStopDiscover;

    private Handler scanHandler = new Handler();
    private List<ScanFilter> scanFilters = new ArrayList<ScanFilter>();
    private ScanSettings scanSettings;

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

        btnEnable.setOnClickListener(this);
        btnDiscoverable.setOnClickListener(this);
        btnStartDiscover.setOnClickListener(this);
        btnStopDiscover.setOnClickListener(this);
    }

    private void initData() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        setBtnEnable();
        setBtnDiscoverable();
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
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                        startScan();
//                    } else {
//                        bluetoothAdapter.startDiscovery();
//                    }
                    bluetoothAdapter.startDiscovery();
                }
                break;
            case R.id.btnStopDiscover:
                if (bluetoothAdapter.isEnabled()) {
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                        stopScan();
//                    } else {
//                        bluetoothAdapter.cancelDiscovery();
//                    }
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

    private void startScan() {
        ScanFilter mScanFilterTest = new ScanFilter.Builder().build();
        scanFilters.add(mScanFilterTest);

        ScanSettings.Builder scanSettingsBuilder = new ScanSettings.Builder();
        scanSettingsBuilder.setScanMode(ScanSettings.SCAN_MODE_BALANCED);
        scanSettingsBuilder.setReportDelay(0);
        scanSettingsBuilder.setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES);
        scanSettings = scanSettingsBuilder.build();

        BluetoothLeScanner scanner = BluetoothAdapter.getDefaultAdapter().getBluetoothLeScanner();
        scanner.startScan(null, scanSettings, scanCallback);

//        scanHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                BluetoothLeScanner scanner = BluetoothAdapter.getDefaultAdapter().getBluetoothLeScanner();
//                scanner.startScan(scanFilters, scanSettings, scanCallback);
//            }
//        }, 1000);
    }

    private void stopScan() {
        BluetoothLeScanner scanner = BluetoothAdapter.getDefaultAdapter().getBluetoothLeScanner();
        scanner.stopScan(scanCallback);
    }

    private ScanCallback scanCallback = new ScanCallback() {

        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            showToast(result.getDevice().getName());
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
            String name = "";
            for (ScanResult scanResult : results) {
                name += scanResult.getDevice().getName() + "--";
            }
            showToast(name);
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            showToast("Error: " + errorCode);
        }
    };

    BluetoothAdapter.LeScanCallback leScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {

        }
    };

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
