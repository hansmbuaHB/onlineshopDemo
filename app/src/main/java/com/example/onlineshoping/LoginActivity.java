package com.example.onlineshoping;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.onlineshoping.Model.Users;
import com.example.onlineshoping.Prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rey.material.widget.CheckBox;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {

    private EditText InputNumber , InputPassword;
    private Button LoginButton;
    private ProgressDialog loadingBar;
    private com.rey.material.widget.CheckBox ckBoxRemberMe;
    private TextView Adminlink, notAdminlink;



    private String parentsbdName = "Users";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        InputNumber = (EditText) findViewById(R.id.login_phone_number_input);
        InputPassword = (EditText) findViewById(R.id.login_passWord_input);
        LoginButton = (Button) findViewById(R.id.lognin_btn);

        Adminlink = (TextView) findViewById(R.id.Admin_panel_link);
       notAdminlink = (TextView)findViewById(R.id.Not_Admin_panel_link);

        ckBoxRemberMe = (CheckBox) findViewById(R.id.remember_me_chk);
        Paper.init(this);


        loadingBar = new ProgressDialog(this);

         LoginButton.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 loginUser();
             }
         });

         //adminlink button
        Adminlink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginButton.setText("Login Admin");
                Adminlink.setVisibility(View.INVISIBLE);
                notAdminlink.setVisibility(View.VISIBLE);
                parentsbdName = "Admins";//check
            }
        });

        notAdminlink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginButton.setText("Login");
                Adminlink.setVisibility(View.VISIBLE);
                notAdminlink.setVisibility(View.INVISIBLE);
                parentsbdName = "Users";
            }
        });

    }

    public void loginUser()
    {
        String phoneNumber = InputNumber.getText().toString();
        String password = InputPassword.getText().toString();
        //checking if phone number  user password is emt if not login
         if (TextUtils.isEmpty(phoneNumber))
           {
        Toast.makeText(this, "Please Write your Phone Number  ",Toast.LENGTH_SHORT).show();
          }

    else if (TextUtils.isEmpty(password))
           {
        Toast.makeText(this, "Please Write your Password  ",Toast.LENGTH_SHORT).show();
           }
      else
        {
            //login
            loadingBar.setTitle("Login Account");
            loadingBar.setMessage("Please wait, while we are checking the credentials");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();


            AllowAccessToAccount(phoneNumber,password);


         }
    }

    private void AllowAccessToAccount(final String phoneNumber, final String password)
    {      //checkbox initialiese users login details
        if (ckBoxRemberMe.isChecked()) {
           Paper.book().write(Prevalent.UserPhoneKey,phoneNumber);
           Paper.book().write(Prevalent.UserPassWordKey,password);


        }



             final DatabaseReference rootRef;
                 rootRef     =  FirebaseDatabase.getInstance().getReference();

        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //if parentName that is "users" exit then we will do the if but if its dose not we will do the else
             if (dataSnapshot.child(parentsbdName).child(phoneNumber).exists()){
                 //getting the account and phone number from the data base

                 Users users = dataSnapshot.child(parentsbdName).child(phoneNumber).getValue(Users.class);


                 if (users.getPhone().equals(phoneNumber)){
                     if (users.getPassword().equals(password)){
                      if (parentsbdName.equals("Admins")){
                          Toast.makeText(LoginActivity.this, "welcome admin you are logged in Successfully....",Toast.LENGTH_SHORT).show();

                          loadingBar.dismiss();
                          Intent intent = new Intent(LoginActivity.this,AdminCategoryActivity.class);//this has been changed
                          startActivity(intent);
                      }
                      else if (parentsbdName.equals("Users")){
                          Toast.makeText(LoginActivity.this, "logged in Successfully....",Toast.LENGTH_SHORT).show();

                          loadingBar.dismiss();
                          Intent intent = new Intent(LoginActivity.this,HomeActivity.class);//from Resgister to Login
                          startActivity(intent);
                      }


                     }

                 }

             }
             else{
                 Toast.makeText(LoginActivity.this, "Account with this  "+phoneNumber+" do not exit",Toast.LENGTH_SHORT).show();
                 loadingBar.dismiss();
                 Toast.makeText(LoginActivity.this, "you need to create a new account. ",Toast.LENGTH_SHORT).show();
             }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
