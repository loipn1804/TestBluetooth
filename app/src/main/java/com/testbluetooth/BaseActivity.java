package com.testbluetooth;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

/**
 * Created by USER on 05/25/2016.
 */
public class BaseActivity extends AppCompatActivity {

    protected Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        toast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
    }

    protected void showToast(String message) {
        toast.setText(message);
        toast.show();
    }
}
