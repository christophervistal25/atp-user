package com.atpuser;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.atpuser.Database.DB;
import com.atpuser.Helpers.SharedPref;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

public class DashboardActivity extends AppCompatActivity {

    public final static int WHITE = 0xFFFFFFFF;
    public final static int BLACK = 0xFF000000;
    public final static int WIDTH = 400;
    public final static int HEIGHT = 400;
    public final static String STR = "A string to be encoded as QR code";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        Toast.makeText(this, DB.getInstance(this).userDao().find(1).getOtp_code(), Toast.LENGTH_SHORT).show();
        if(SharedPref.getSharedPreferenceBoolean(this,"FIRST_VISIT", true)) {
            this.notificationDialog();
            SharedPref.setSharedPreferenceBoolean(this,"FIRST_VISIT", false);
        }



        ImageView barCodeImage = findViewById(R.id.barCodeImage);
        try {
            Bitmap bitmap = encodeAsBitmap(STR);
            barCodeImage.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }

    }

    Bitmap encodeAsBitmap(String str) throws WriterException {
        BitMatrix result;
        try {
            result = new MultiFormatWriter().encode(str,
                    BarcodeFormat.QR_CODE, WIDTH, HEIGHT, null);
        } catch (IllegalArgumentException iae) {
            // Unsupported format
            return null;
        }

        int w = result.getWidth();
        int h = result.getHeight();
        int[] pixels = new int[w * h];
        for (int y = 0; y < h; y++) {
            int offset = y * w;
            for (int x = 0; x < w; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, w, 0, 0, w, h);
        return bitmap;
    }


    private void notificationDialog() {
        AlertDialog.Builder notificationDialog = new AlertDialog.Builder(DashboardActivity.this);
        notificationDialog.setTitle("ATP Notification");
        notificationDialog.setCancelable(false);
        notificationDialog.setMessage("Welcome to ATP (Action Trace & Protect) Surigao del Sur COVID-19 Contact Tracing App.\n\nYou may now connect with the ATP through the APP or call us at our\nmobile hotline : 09193693499, \nIf you need any COVID-19 related Assistance.");
        notificationDialog.setNegativeButton("CLOSE", (dialog, which) -> dialog.dismiss());
        notificationDialog.show();
    }
}