package com.example.advancedprogramming4;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    protected void Connect(View view){
        EditText ip = (EditText)findViewById(R.id.ip_box);
        EditText port = (EditText)findViewById(R.id.port_box);

        Intent intent = new Intent(this,JoystickActivity.class);
        intent.putExtra("ip",ip.getText().toString());
        intent.putExtra("port",port.getText().toString());

        startActivity(intent);
    }
}
