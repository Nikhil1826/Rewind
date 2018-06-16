package com.fasttech.rewind;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.spark.submitbutton.SubmitButton;

import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {
    Animation uptodown,downtoup;
    CardView cardView;
    ImageView imageView;
    EditText edtPhone,edtPassword;
    SubmitButton btnLogin;
    SpotsDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        cardView = (CardView)findViewById(R.id.card);
        imageView = (ImageView)findViewById(R.id.login);
        downtoup = AnimationUtils.loadAnimation(this,R.anim.downtoup);
        uptodown = AnimationUtils.loadAnimation(this,R.anim.uptodown);
        cardView.setAnimation(downtoup);
        imageView.setAnimation(uptodown);

        edtPassword = (MaterialEditText) findViewById(R.id.edtPassword);
        edtPhone = (MaterialEditText) findViewById(R.id.edtPhone);
        btnLogin = (SubmitButton) findViewById(R.id.btnLogin2);


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_user = database.getReference("User");

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    alertDialog = new SpotsDialog(LoginActivity.this, R.style.Custom);
                    alertDialog.setMessage("Please Wait...");
                    alertDialog.show();
                    table_user.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            //checking if user exists
                            if (dataSnapshot.child(edtPhone.getText().toString()).exists()) {

                                alertDialog.dismiss();
                                String phone = edtPhone.getText().toString().trim();
                                String pass = edtPassword.getText().toString().trim();
                                if (phone.isEmpty() || pass.isEmpty()) {
                                    Toast.makeText(LoginActivity.this, "Please enter your details!!", Toast.LENGTH_SHORT).show();
                                } else if(phone.length()>10 || phone.length()<10){
                                    Toast.makeText(LoginActivity.this, "Please enter valid number", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    //get user information
                                    User user = dataSnapshot.child(edtPhone.getText().toString()).getValue(User.class);
                                    user.setPhone(edtPhone.getText().toString());
                                    if (user.getPassword().equals(edtPassword.getText().toString())) {
                                        Intent intent = new Intent(LoginActivity.this, UploadActivity.class);
                                        CurrentUser.cUser = user;
                                        intent.putExtra("MobileNo",phone);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Toast.makeText(LoginActivity.this, "Incorrect Password!!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            } else {
                                alertDialog.dismiss();
                                Toast.makeText(LoginActivity.this, "User does not exists in Database", Toast.LENGTH_SHORT).show();
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            });



    }
}
