package com.fasttech.rewind;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.spark.submitbutton.SubmitButton;

import java.util.concurrent.TimeUnit;

import dmax.dialog.SpotsDialog;
import info.hoang8f.widget.FButton;

public class SignUpActivity extends AppCompatActivity {
    Animation uptodown,downtoup;
    CardView cardView;
    ImageView imageView;
    MaterialEditText edtPhone,edtName,edtPassword,edtOTP;
    SubmitButton btnSignUp1;
    FButton btnVerify,btnSendOTP;
    DatabaseReference table_user;
    FirebaseAuth mAuth;
    String codeSent;
    SpotsDialog mProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mAuth = FirebaseAuth.getInstance();
        cardView = (CardView)findViewById(R.id.card1);
        imageView = (ImageView)findViewById(R.id.signup);
        downtoup = AnimationUtils.loadAnimation(this,R.anim.downtoup);
        uptodown = AnimationUtils.loadAnimation(this,R.anim.uptodown);
        cardView.setAnimation(downtoup);
        imageView.setAnimation(uptodown);

        edtName = (MaterialEditText)findViewById(R.id.edtName1);
        edtPhone = (MaterialEditText)findViewById(R.id.edtPhone1);
        edtPassword = (MaterialEditText)findViewById(R.id.edtPassword1);
        btnSignUp1 = (SubmitButton)findViewById(R.id.btnSignUp1);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        table_user = database.getReference("User");


        btnSignUp1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                table_user.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String name = edtName.getText().toString().trim();
                        String phone = edtPhone.getText().toString().trim();
                        String pass = edtPassword.getText().toString().trim();
                        if (name.isEmpty() || phone.isEmpty() || pass.isEmpty()) {
                            Toast.makeText(SignUpActivity.this, "Please enter your details!!", Toast.LENGTH_SHORT).show();
                        } else if(phone.length()>10 || phone.length()<10){
                            Toast.makeText(SignUpActivity.this, "Please enter valid number", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            if (dataSnapshot.child(edtPhone.getText().toString()).exists()) {
                                //Toast.makeText(SignUpActivity.this, "Phone Number is already registered!!", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                /*User user = new User(edtName.getText().toString(), edtPassword.getText().toString());
                                table_user.child(edtPhone.getText().toString()).setValue(user);
                                Toast.makeText(SignUpActivity.this, "Registered Successfully!!", Toast.LENGTH_SHORT).show();
                                finish();*/
                                showOTPdialog();

                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        });






    }

    private void showOTPdialog(){
        AlertDialog.Builder alertDialog=new AlertDialog.Builder(SignUpActivity.this);
        alertDialog.setTitle("One Time Password (OTP)");
        alertDialog.setMessage("Enter the OTP");

        LayoutInflater inflater = this.getLayoutInflater();
        View add_menu_layout = inflater.inflate(R.layout.otp_layout,null);

        edtOTP = add_menu_layout.findViewById(R.id.edtOTP);

        btnVerify = add_menu_layout.findViewById(R.id.btnVerify);
        btnSendOTP = add_menu_layout.findViewById(R.id.btnSendOTP);

        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verify();
            }
        });

        btnSendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendVerificationCode();
            }
        });

        alertDialog.setView(add_menu_layout);
        alertDialog.setIcon(R.drawable.ic_smartphone_black_24dp);

       /* alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();


            }
        });

        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });*/
        alertDialog.show();

    }

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                signInWithPhone(phoneAuthCredential);
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Log.i("Error",e.getMessage());
            Toast.makeText(getApplicationContext(),"Error"+e.getMessage(),Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            //super.onCodeSent(s, forceResendingToken);
            mProgressDialog.dismiss();
            codeSent = s;}
    };

    private void sendVerificationCode() {
        String number = "+91"+edtPhone.getText().toString();
        if(number.isEmpty()){
            edtPhone.setError("Phone number is required");
            edtPhone.requestFocus();
            return;
        }
        PhoneAuthProvider.getInstance().verifyPhoneNumber(number,60,TimeUnit.SECONDS,this,mCallbacks);
        mProgressDialog = new SpotsDialog(SignUpActivity.this, R.style.Custom);
        mProgressDialog.setMessage("Getting OTP...");
        mProgressDialog.show();
    }

    private void verify() {
        String code = edtOTP.getText().toString();
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeSent,code);
        signInWithPhone(credential);
    }

    private void signInWithPhone(PhoneAuthCredential credential){
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            User user = new User(edtName.getText().toString(), edtPassword.getText().toString());
                            table_user.child(edtPhone.getText().toString()).setValue(user);
                            Toast.makeText(SignUpActivity.this, "Registered Successfully!!", Toast.LENGTH_SHORT).show();
                            finish();

                        }else{
                            if(task.getException() instanceof FirebaseAuthInvalidCredentialsException){
                                Toast.makeText(getApplicationContext(),"Incorrect Verification code",Toast.LENGTH_SHORT).show();
                            }
                        }

                    }
                });
    }





}
