package com.example.underwater;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {
    TextView tv1;
    EditText mid, name, address, phone, password;
    Button register;
    TestAdapter adapter;
    String id, semial, saddress, sphone, spassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mid = (EditText) findViewById(R.id.txt_id);
        name = (EditText) findViewById(R.id.txt_name);
        address = (EditText) findViewById(R.id.txt_address);
        phone = (EditText) findViewById(R.id.txt_mobile);
        password = (EditText) findViewById(R.id.txt_password);
        register = (Button) findViewById(R.id.btn_signup);
        tv1 = (TextView) findViewById(R.id.txt_alredyregister);




        tv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(i);
            }
        });
        try {
            adapter = new TestAdapter(this);
            adapter.createDatabase();
            adapter.open();

            autoincrementid();

            register.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    id = mid.getText().toString();
                    semial = name.getText().toString();
                    saddress = address.getText().toString();
                    sphone = phone.getText().toString();
                    spassword = password.getText().toString();

                    String mpass = null;
                    Cursor cursor = adapter.selectUser();
                    while (cursor.moveToNext()) {
                        mpass = cursor.getString(3).toString();
                        if (sphone.equalsIgnoreCase(mpass)) {

                            Toast.makeText(getApplicationContext(), "This User is Already Registered..!", Toast.LENGTH_SHORT).show();
                            Log.w("5", "ok");
                            return;
                        }
                    }


                    if (TextUtils.isEmpty(id)) {
                        Toast.makeText(getApplicationContext(), "Please Enter User Name..", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (TextUtils.isEmpty(semial)) {
                        Toast.makeText(getApplicationContext(), "Please Enter Email Address..", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (TextUtils.isEmpty(saddress)) {
                        Toast.makeText(getApplicationContext(), "Please Enter  Address..", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (TextUtils.isEmpty(sphone)) {
                        Toast.makeText(getApplicationContext(), "Please Enter  Mobile Number..", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (sphone.length() != 10) {
                        Toast.makeText(getApplicationContext(), "Please Enter 10 Digit Mobile Number.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (sphone.startsWith("0") || sphone.startsWith("1") || sphone.startsWith("2") || sphone.startsWith("3") || sphone.startsWith("4") || sphone.startsWith("5")) {
                        Toast.makeText(getApplicationContext(), "Please Enter Valid Mobile Number.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (spassword.length() <= 7) {
                        Toast.makeText(getApplicationContext(), "Please Choose" +
                                " Strong Password.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (TextUtils.isEmpty(spassword)) {
                        Toast.makeText(getApplicationContext(), "Please Enter Password..", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        RegisterUser();

                    }
                }
            });


        } catch (Exception e) {
        }


    }

    private void autoincrementid() {
        String number = " ";
        int no1;
        int flag = 0;
        Cursor cursor1 = adapter.selectUser();
        //cursor1.moveToFirst();
        while (cursor1.moveToNext()) {
            flag = 1;
        }
        cursor1.close();
        //setting id into edit text
        if (flag == 1) {
            try {
                Cursor cursor2 = adapter.userincrementid();
                while (cursor2.moveToNext()) {
                    if (cursor2.getString(0) != null)
                        number = cursor2.getString(0);

                }
                int n = Integer.parseInt(number);
                no1 = n + 1;
                mid.setText("" + no1);
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
    }

    private void RegisterUser() {

        final ProgressDialog dialog =
                new ProgressDialog(RegisterActivity.this);
        dialog.setIcon(R.drawable.add);
        dialog.setTitle("Processing Your Request...");
        dialog.setMessage("Please wait...");
        dialog.show();

        long i = adapter.Insertuser(id, semial, saddress, sphone, spassword);
        // Toast.makeText(getApplicationContext(), " Register Sucessfully..." + i, Toast.LENGTH_SHORT).show();

        final Runnable progressRunnable = new Runnable() {

            @Override
            public void run() {
                dialog.cancel();
                String mpass = null;

                final android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.
                        Builder(RegisterActivity.this);

                alertDialogBuilder.setTitle("Registration Successfully Done..");

                final android.app.AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialogBuilder.setPositiveButton("OK.",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                Intent i = new Intent(RegisterActivity.this, MainActivity.class);
                                startActivity(i);


                            }
                        });
                android.app.AlertDialog alDialog = alertDialogBuilder.create();
                alDialog.show();
            }

        };

        Handler pdCanceller = new Handler();
        pdCanceller.postDelayed(progressRunnable, 6000);

    }
}