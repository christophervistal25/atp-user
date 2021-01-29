package com.atpuser;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterStep2Activity extends AppCompatActivity {

    public static final String CODE = "123456";
    LinearLayout codeLayout;
    EditText code1, code2, code3, code4, code5, code6;
    TextView resentOtp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_step2);
        Bundle extra = getIntent().getExtras();

        String phone = "09193693499";

        Button btnClearCode = findViewById(R.id.btnClearCode);
        resentOtp = findViewById(R.id.resendOTP);

        TextView userPhoneNumber = findViewById(R.id.userPhoneNumber);
//        userPhoneNumber.setText(extra.getString("PHONE_NUMBER"));
        userPhoneNumber.setText(replaceOtherParts(phone));

        code1 = findViewById(R.id.code1);
        code2 = findViewById(R.id.code2);
        code3 = findViewById(R.id.code3);
        code4 = findViewById(R.id.code4);
        code5 = findViewById(R.id.code5);
        code6 = findViewById(R.id.code6);

        // By default focus must be in the first index of the sequence.
        findViewById(R.id.code1).requestFocus();
        findViewById(R.id.code1).callOnClick();


        codeLayout = findViewById(R.id.codeLayout);
        int childCount = codeLayout.getChildCount();

        for(int i = 0; i<childCount; i++) {
            if(codeLayout.getChildAt(i) instanceof EditText) {
                EditText e = (EditText) codeLayout.getChildAt(i);
                e.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if(e.getText().length() != 0) {
                            // Last EditText.
                            if(!e.getTag().equals("5")) {
                                int nextEditEditTextIndex = Integer.parseInt(String.valueOf(e.getTag()));
                                codeLayout.getChildAt(++nextEditEditTextIndex).requestFocus();
                            }
                        }

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if(codeChecker(CODE)) {
                            Intent mainActivity = new Intent(RegisterStep2Activity.this, RegisterStep3Activity.class);
                            startActivity(mainActivity);
                        }
                    }
                });
            }
        }

        btnClearCode.setOnClickListener(v -> {
            for(int i = 0; i<childCount; i++) {
                if (codeLayout.getChildAt(i) instanceof EditText) {
                    ((EditText) codeLayout.getChildAt(i)).setText("");
                }
            }
            findViewById(R.id.code1).requestFocus();
        });


        resentOtp.setOnClickListener(v -> Toast.makeText(RegisterStep2Activity.this, "Resend OTP", Toast.LENGTH_SHORT).show());
    }

    private String replaceOtherParts(String phone) {
        char[] chars = phone.toCharArray();
        int no_of_replace = 6;
        for(int i = chars.length - 1; i>(chars.length - no_of_replace); i--) {
            chars[i] = '*';
        }
        return String.valueOf(chars);
    }

    private boolean codeChecker(String SEND_CODE)
    {
        int valueCount = 0;

        for(int i = 0; i<codeLayout.getChildCount(); i++) {
            if (codeLayout.getChildAt(i) instanceof EditText) {
                EditText e = (EditText) codeLayout.getChildAt(i);
                if(!e.getText().toString().isEmpty()) {
                    valueCount++;
                }
            }
        }

        if(codeLayout.getChildCount() == valueCount) {
            String inputCode = code1.getText().toString() + code2.getText().toString() + code3.getText().toString() + code4.getText().toString() + code5.getText().toString() + code6.getText().toString();
            return SEND_CODE.equals(inputCode);
        }

        return false;
    }
}