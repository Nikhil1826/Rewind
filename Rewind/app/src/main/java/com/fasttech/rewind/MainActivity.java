package com.fasttech.rewind;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.spark.submitbutton.SubmitButton;

import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {

    Button btnSignUp,btnLogin;
    SpotsDialog alertDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        btnSignUp = (Button) findViewById(R.id.btnSignUp);
        btnLogin = (Button) findViewById(R.id.btnLogin);



        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent);

            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,SignUpActivity.class);
                startActivity(intent);
            }
        });

    }


}
