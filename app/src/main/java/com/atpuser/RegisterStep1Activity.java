package com.atpuser;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.atpuser.Database.DB;
import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class RegisterStep1Activity extends AppCompatActivity {

    private static final int SEND_SMS_CODE = 23;
    private static final int CAMERA_REQUEST = 1888;
    private static final int PICK_IMAGE = 1;

    AlertDialog.Builder municipalDialog, barangayDialog, cameraDialog;

    ImageView user_image;
    Uri userImageLink = null;



    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_step1);

        askPermissionForSMS();



        EditText firstname = findViewById(R.id.firstName);
        EditText middlename = findViewById(R.id.middleName);
        EditText lastname = findViewById(R.id.lastName);
        EditText suffix = findViewById(R.id.suffix);
        EditText age = findViewById(R.id.age);
        EditText phoneNumber = findViewById(R.id.phone_number);
        EditText email = findViewById(R.id.email_address);
        EditText purok = findViewById(R.id.purok);
        EditText street = findViewById(R.id.street);

        user_image = findViewById(R.id.user_image);


        Spinner spinnerCivilStatus = findViewById(R.id.civil_status);
        EditText spinnerProvince = findViewById(R.id.province);
        EditText spinnerMunicipality = findViewById(R.id.municipality);
        EditText spinnerBarangay = findViewById(R.id.barangay);


        AlertDialog.Builder provinceDialog = new AlertDialog.Builder(RegisterStep1Activity.this);
        provinceDialog.setTitle("Select Province");
        provinceDialog.setCancelable(false);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(RegisterStep1Activity.this, android.R.layout.simple_spinner_dropdown_item, DB.getInstance(getApplicationContext()).provinceDao().all());


        provinceDialog.setNegativeButton("CANCEL", (dialog, which) -> dialog.dismiss());




        provinceDialog.setAdapter(arrayAdapter, (dialog, which) -> {
            String selectedProvince = arrayAdapter.getItem(which);
            spinnerProvince.setText(selectedProvince);
            spinnerMunicipality.setText("");

            initMunicipalityDialog(spinnerMunicipality, spinnerBarangay, selectedProvince);

        });


        spinnerProvince.setOnClickListener((v) -> {
                provinceDialog.show();
        });

        spinnerMunicipality.setOnClickListener(v -> {
            if(municipalDialog != null) {
                municipalDialog.show();
            } else {
                Toast.makeText(this, "Please select province first.", Toast.LENGTH_SHORT).show();
            }
        });

        spinnerBarangay.setOnClickListener(v -> {
            if(barangayDialog != null) {
                barangayDialog.show();
            } else {
                Toast.makeText(this, "Please select municipality first.", Toast.LENGTH_SHORT).show();
            }
        });


        AlertDialog.Builder cameraDialog = new AlertDialog.Builder(RegisterStep1Activity.this);

        List<String> options = new ArrayList<>();
        options.add("Take Photo");
        options.add("Choose from Gallery");

        final ArrayAdapter<String> cameraAdapter = new ArrayAdapter<>(RegisterStep1Activity.this, android.R.layout.simple_spinner_dropdown_item, options);



        cameraDialog.setAdapter(cameraAdapter, (dialog, which) -> {
            String selectedType = cameraAdapter.getItem(which);
            int position = cameraAdapter.getPosition(selectedType);
            if(position == 0) {
                // Opening the camera.
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            } else {
               // Picking image
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, PICK_IMAGE);
            }
        });


        Button btnCamera = findViewById(R.id.btnCamera);
        btnCamera.setOnClickListener(v -> cameraDialog.show());




        findViewById(R.id.submit).setOnClickListener(v -> {



            AwesomeValidation mAwesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
            mAwesomeValidation.addValidation(this, R.id.lastName, "[a-zA-Z\\s]+", R.string.validation_error);
            mAwesomeValidation.addValidation(this, R.id.firstName, "[a-zA-Z\\s]+", R.string.validation_error);
            mAwesomeValidation.addValidation(this, R.id.age, "[0-9]+", R.string.validation_error);
            mAwesomeValidation.addValidation(this, R.id.phone_number, "^(09|\\+639)\\d{9}$", R.string.validation_error);
            mAwesomeValidation.addValidation(this, R.id.civil_status, "[a-zA-Z\\s]+", R.string.validation_error);
            mAwesomeValidation.addValidation(this, R.id.province, "[a-zA-Z\\s]+", R.string.validation_error);
            mAwesomeValidation.addValidation(this, R.id.municipality, "[a-zA-Z\\s]+", R.string.validation_error);
            mAwesomeValidation.addValidation(this, R.id.barangay, "[a-zA-Z\\s]+", R.string.validation_error);
            mAwesomeValidation.addValidation(this, R.id.purok, "[a-zA-Z\\s]+", R.string.validation_error);

//            if(mAwesomeValidation.validate()) {
//                AlertDialog.Builder confirmationDialog = new AlertDialog.Builder(RegisterStep1Activity.this);
//                confirmationDialog.setTitle("TITLE");
//                confirmationDialog.setMessage("MESSAGE");
//                confirmationDialog.setPositiveButton("REGISTER", (dialog, which) -> {
                    if(userImageLink == null) {
                        Toast.makeText(this, "Please attach an image or take photo.", Toast.LENGTH_SHORT).show();
                        return;
                    }


//                    User user = new User();
//                    user.setLastname(lastname.getText().toString());
//                    user.setFirstname(firstname.getText().toString());
//                    user.setMiddlename(middlename.getText().toString());
//                    user.setSuffix(suffix.getText().toString());
//                    user.setAge(Integer.parseInt(age.getText().toString()));
//                    user.setCivil_status(spinnerCivilStatus.getSelectedItem().toString());
//                    user.setPhone_number(phoneNumber.getText().toString());
//                    user.setEmail(email.getText().toString());
//                    user.setProvince(spinnerProvince.getText().toString());
//                    user.setMunicipality(spinnerMunicipality.getText().toString());
//                    user.setBarangay(spinnerBarangay.getText().toString());
//                    user.setPurok(purok.getText().toString());
//                    user.setStreet(street.getText().toString());
//                    user.setOtp_code("");
//                    user.setImage(userImageLink);

//                    DB.getInstance(this).userDao().create(user);
//                    this.sendSMS();
                    this.redirectToStep2(phoneNumber);
//                });

//                confirmationDialog.show();



//            }

        });
    }

    private void redirectToStep2(EditText phoneNumber) {
        Intent step2Activity = new Intent(RegisterStep1Activity.this, RegisterStep2Activity.class);
        step2Activity.putExtra("PHONE_NUMBER", phoneNumber.getText().toString());
        startActivity(step2Activity);
    }

    private void sendSMS() {
        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent("SMS_SENT"), 0);
        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0, new Intent("SMS_DELIVERED"), 0);
        SmsManager.getDefault().sendTextMessage("+639630711082", null, "REQUEST_CODE", sentPI, deliveredPI);
    }

    // For camera take photo process.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK)
        {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            final Uri imageUri = data.getData();
            userImageLink = imageUri;
            Toast.makeText(this, imageUri.toString(), Toast.LENGTH_SHORT).show();
            user_image.setImageBitmap(photo);
        } else if(resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                userImageLink = imageUri;
                user_image.setImageBitmap(selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }

    }


    private void askPermissionForSMS() {
        ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.SEND_SMS }, SEND_SMS_CODE);

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Please Allow the SMS feature to use this application.", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.SEND_SMS }, SEND_SMS_CODE);
        }
    }

    private void initMunicipalityDialog(EditText spinnerMunicipality, EditText spinnerBarangay, String selectedProvince) {
        int province_id = DB.getInstance(this).provinceDao().getIdByName(selectedProvince);
        ArrayAdapter<String> municipalAdapter = new ArrayAdapter<>(RegisterStep1Activity.this, android.R.layout.simple_spinner_dropdown_item, DB.getInstance(getApplicationContext()).municipalDao().findByProvince(province_id));
        municipalAdapter.notifyDataSetChanged();

        municipalDialog = new AlertDialog.Builder(RegisterStep1Activity.this);
        municipalDialog.setTitle("Select Municipal");
        municipalDialog.setCancelable(false);


        municipalDialog.setNegativeButton("CANCEL", (municipalDialog, municipalWhich) -> municipalDialog.dismiss());



        municipalDialog.setAdapter(municipalAdapter, (d, w) -> {
            String selectedMunicipality = municipalAdapter.getItem(w);
            spinnerMunicipality.setText(selectedMunicipality);
            spinnerBarangay.setText("");

            this.initBarangayDialog(spinnerBarangay, selectedMunicipality);

        });
    }

    private void initBarangayDialog(EditText spinnerBarangay, String selectedMunicipality) {
        int municipality_id = DB.getInstance(this).municipalDao().getIdByName(selectedMunicipality);
        ArrayAdapter<String> barangayAdapter = new ArrayAdapter<>(RegisterStep1Activity.this, android.R.layout.simple_spinner_dropdown_item, DB.getInstance(this).barangayDao().getByMunicipal(municipality_id));
        barangayAdapter.notifyDataSetChanged();

        barangayDialog = new AlertDialog.Builder(RegisterStep1Activity.this);
        barangayDialog.setTitle("Select Barangay");
        barangayDialog.setCancelable(false);

        barangayDialog.setNegativeButton("CANCEL", (barangayDialog, barangayWhich) -> barangayDialog.dismiss());

        barangayDialog.setAdapter(barangayAdapter, (barangayD, barangayW) -> {
            String selectedBarangay = barangayAdapter.getItem(barangayW);
            spinnerBarangay.setText(selectedBarangay);
        });
    }

}