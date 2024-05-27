package com.example.underwater;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    TextView tv1;
    Button b1,b2;
    EditText mobile,password;
    TestAdapter adapter;
    String smobile,spassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        b2=(Button) findViewById(R.id.register);
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(MainActivity.this,RegisterActivity.class);
                startActivity(i);
            }
        });

        b1=(Button)findViewById(R.id.btn_login);
        mobile=(EditText)findViewById(R.id.txt_mobile);
        password=(EditText)findViewById(R.id.txt_password);

        tv1=(TextView) findViewById(R.id.register);
        try {

            adapter=new TestAdapter(this);
            adapter.createDatabase();
            adapter.open();



            b1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    smobile = mobile.getText().toString();
                    spassword= password.getText().toString();

                    if(TextUtils.isEmpty(smobile))
                    {
                        mobile.setError("Mobile is Required.");
                        return;
                    }

                    if(TextUtils.isEmpty(spassword))
                    {
                        password.setError("Password is Required.");
                        return;
                    }

                    int i = adapter.checkUserLogin(smobile,spassword);
                    if (i == 1) {


                        userlogin();
                        return;

                    }


                    else{
                        Toast.makeText(MainActivity.this, "Invalid Mobile Or Password", Toast.LENGTH_SHORT).show();
                        return;

                    }


                }
            });

        }catch (Exception e){}


    }

    private void userlogin() {
        final ProgressDialog dialog =
                new ProgressDialog(MainActivity.this);
        dialog.setIcon(R.drawable.login);
        dialog.setTitle("Login");
        dialog.setMessage("Please wait User Login is Processing...");
        dialog.show();

        final Runnable progressRunnable = new Runnable() {

            @Override
            public void run() {
                dialog.cancel();
                String mpass = null;

                Intent i = new Intent(MainActivity.this, HomePageActivity.class);
                i.putExtra("Key",smobile);
                startActivity(i);

                Toast.makeText(getApplicationContext(), "Your Login is Successfull..." , Toast.LENGTH_SHORT).show();

                mobile.setText("");
                password.setText("");


            }
        };
        Handler pdCanceller = new Handler();
        pdCanceller.postDelayed(progressRunnable, 6000);


    }
}