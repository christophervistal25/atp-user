package com.atpuser;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterStep3Activity extends AppCompatActivity     {


    TextView code;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_step3);

        code = findViewById(R.id.code);


        findViewById(R.id.btn1).setOnClickListener(v -> code.setText(code.getText().toString().concat("1")));
        findViewById(R.id.btn2).setOnClickListener(v -> code.setText(code.getText().toString().concat("2")));
        findViewById(R.id.btn3).setOnClickListener(v -> code.setText(code.getText().toString().concat("3")));
        findViewById(R.id.btn4).setOnClickListener(v -> code.setText(code.getText().toString().concat("4")));
        findViewById(R.id.btn5).setOnClickListener(v -> code.setText(code.getText().toString().concat("5")));
        findViewById(R.id.btn6).setOnClickListener(v -> code.setText(code.getText().toString().concat("6")));
        findViewById(R.id.btn7).setOnClickListener(v -> code.setText(code.getText().toString().concat("7")));
        findViewById(R.id.btn8).setOnClickListener(v -> code.setText(code.getText().toString().concat("8")));
        findViewById(R.id.btn9).setOnClickListener(v -> code.setText(code.getText().toString().concat("9")));
        findViewById(R.id.btn0).setOnClickListener(v -> code.setText(code.getText().toString().concat("0")));

        findViewById(R.id.btnX).setOnClickListener(v -> {
            if(code.getText().length() != 0) {
                code.setText(
                        code.getText().toString().substring(0, code.getText().length() - 1)
                );
            }
        });

    }


}