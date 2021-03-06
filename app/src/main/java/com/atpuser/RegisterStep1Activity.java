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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.atpuser.Database.DB;
import com.atpuser.Database.Models.User;
import com.atpuser.Helpers.SharedPref;
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
    public static final String GATEWAY_NUMBER = "+639630711082";

    AlertDialog.Builder municipalDialog, barangayDialog, cameraDialog;

    ImageView user_image;
    Uri userImageLink = null;

    CheckBox termsAndPrivacy;



    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_step1);

        askPermissionForSMS();

        termsAndPrivacy = findViewById(R.id.termsAndPrivacyCheckbox);

        TextView termsText = findViewById(R.id.termsAndPrivacy);
        termsAndPrivacyDialog();
        termsText.setOnClickListener(v -> termsAndPrivacyDialog());



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


        spinnerProvince.setOnClickListener((v) -> provinceDialog.show());

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
            mAwesomeValidation.addValidation(this, R.id.province, input -> !spinnerProvince.getText().toString().isEmpty(), R.string.validation_error);
            mAwesomeValidation.addValidation(this, R.id.municipality, input -> !spinnerMunicipality.getText().toString().isEmpty(), R.string.validation_error);
            mAwesomeValidation.addValidation(this, R.id.barangay, input -> !spinnerBarangay.getText().toString().isEmpty(), R.string.validation_error);
            mAwesomeValidation.addValidation(this, R.id.phone_number, input -> DB.getInstance(getApplicationContext()).userDao().findByPhone(phoneNumber.getText().toString()) == null, R.string.duplicate_phone_number);

            /*mAwesomeValidation.addValidation(this, R.id.municipality, "[a-zA-Z\\s]+", R.string.validation_error);
            mAwesomeValidation.addValidation(this, R.id.barangay, "[a-zA-Z\\s]+", R.string.validation_error);
            mAwesomeValidation.addValidation(this, R.id.purok, "[a-zA-Z\\s]+", R.string.validation_error);*/

            if(userImageLink == null) {
                Toast.makeText(this, "Please attach an image or take photo.", Toast.LENGTH_SHORT).show();
                return;
            }

            if(!termsAndPrivacy.isChecked()) {
                termsText.setError("Please read this.");
            }

            if(mAwesomeValidation.validate()) {
                AlertDialog.Builder confirmationDialog = new AlertDialog.Builder(RegisterStep1Activity.this);
                confirmationDialog.setTitle("Important Message");
                confirmationDialog.setMessage("Did you read properly the Terms of Use and Privacy Policy?");
                confirmationDialog.setNegativeButton("NO", (dialog, which) -> {
                    dialog.dismiss();
                   termsAndPrivacyDialog();
                });

                confirmationDialog.setPositiveButton("YES", (dialog, which) -> {

                    User user = new User();
                    user.setLastname(lastname.getText().toString());
                    user.setFirstname(firstname.getText().toString());
                    user.setMiddlename(middlename.getText().toString());
                    user.setSuffix(suffix.getText().toString());
                    user.setAge(Integer.parseInt(age.getText().toString()));
                    user.setCivil_status(spinnerCivilStatus.getSelectedItem().toString());
                    user.setPhone_number(phoneNumber.getText().toString());
                    user.setEmail(email.getText().toString());
                    user.setProvince(spinnerProvince.getText().toString());
                    user.setMunicipality(spinnerMunicipality.getText().toString());
                    user.setBarangay(spinnerBarangay.getText().toString());
                    user.setPurok(purok.getText().toString());
                    user.setStreet(street.getText().toString());
                    user.setOtp_code("");
                    user.setImage(userImageLink.toString());

                    DB.getInstance(this).userDao().create(user);

                    this.redirectToStep2(phoneNumber);
                });

                confirmationDialog.show();
            }

        });
    }

    private void termsAndPrivacyDialog() {
        AlertDialog.Builder termsAndPolicyDialog = new AlertDialog.Builder(RegisterStep1Activity.this);
        termsAndPolicyDialog.setTitle("Terms of Use and Privacy Policy");
        termsAndPolicyDialog.setMessage("Terms of Use and Privacy Policy\n" +
                "The PROVINCIAL GOVERNMENT OF TANDAG CITY (\"we, us\") is committed to protect and respect your personal data privacy.\n" +
                "\n" +
                "In order to protect the health of our constituents in the threat of the COVID-19 pandemic, we are gathering information to process their entry.\n" +
                "\n"+
                "We are at the forefront of not only implementing but also\n" +
                "complying with RA No. 11332 or the Mandatory and Health Events of Public Health the Data \n" +
                "Privacy Act of 2012 and its Implementing Rules and Regulations (IRR) with respect to\n" +
                "gathering information to help the Contact Tracing of Close Contacts of\n" +
                "COVID-19 Cases.\n" +
                "\n" +
                "We are committed to protecting the heal and well-being\n" +
                "of our constituents. In order to protect the health of local citizens of the province,\n" +
                "we are collecting your information for contact tracing\n" +
                "\n" +
                "Your information might be disclosed to DOH, other agencies, and\n" +
                "authorized persons to provide and effective response during this\n" +
                "COVID-19 pandemic.\n" +
                "\n" +
                "You will be asked to provide basic information including your full name, gender, birth date, address, employment information, place of origin and haelth/medical history, travel history, and other information deemed necessary for the purpose of contact tracing.\n" +
                "\n" +
                "By entering information asked on the form, you understand and agree to the use of the\n" +
                "Provincial Government of Tandag City to process and disclose your data to other parties within\n" +
                "the bounds of law mentioned above.");

        termsAndPolicyDialog.setPositiveButton("I AGREE", (dialog, which) -> termsAndPrivacy.setChecked(true));
        termsAndPolicyDialog.setNegativeButton("CANCEL", (dialog, which) -> dialog.dismiss());
        termsAndPolicyDialog.show();
    }

    private void redirectToStep2(EditText phoneNumber) {
        this.requestCode();
        Intent step2Activity = new Intent(RegisterStep1Activity.this, RegisterStep2Activity.class);
        step2Activity.putExtra("PHONE_NUMBER", phoneNumber.getText().toString());
        SharedPref.setSharedPreferenceString(this,"USER_PHONE_NUMBER", phoneNumber.getText().toString());
        startActivity(step2Activity);
    }

    private void requestCode() {
        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent("SMS_SENT"), 0);
        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0, new Intent("SMS_DELIVERED"), 0);
        SmsManager.getDefault().sendTextMessage(GATEWAY_NUMBER, null, "REQUEST_CODE", sentPI, deliveredPI);
    }

    // For camera take photo process.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            final Uri imageUri = data.getData();
            userImageLink = imageUri;
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