package com.example.onlineshoping;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.onlineshoping.Model.Users;
import com.example.onlineshoping.Prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {
    private Button login_button, joinButton;
    private ProgressDialog loadingBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        login_button = (Button) findViewById(R.id.main_lognin_btn);
        joinButton = (Button) findViewById(R.id.main_join_now_btn);

        loadingBar = new ProgressDialog(this);

        Paper.init(this);

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);

            }
        });

        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ResgisterActivity.class);
                startActivity(intent);

            }
        });
          ////read codes from paper and send to the login always
          String userphonekey = Paper.book().read(Prevalent.UserPhoneKey);
          String userpasswordkey = Paper.book().read(Prevalent.UserPassWordKey);

          if (userphonekey !="" && userpasswordkey !=""){
              if (!TextUtils.isEmpty(userphonekey) && !TextUtils.isEmpty(userpasswordkey)){
//check if user is already logged in
                  AllowAccess(userphonekey, userpasswordkey);
                  loadingBar.setTitle("already logged in");
                  loadingBar.setMessage("Please wait...");
                  loadingBar.setCanceledOnTouchOutside(false);
                  loadingBar.show();
                  loadingBar.dismiss();
              }
          }


    }

    private void AllowAccess(final String phone ,final String password) {

        final DatabaseReference rootRef;
        rootRef     = FirebaseDatabase.getInstance().getReference();

        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //if parentName that is "users" exit then we will do the if but if its dose not we will do the else
                if (dataSnapshot.child("Users").child(phone).exists()){
                    //getting the account and phone number from the data base

                    Users users = dataSnapshot.child("Users").child(phone).getValue(Users.class);

                    System.out.println(" the password is "+users.getPassword());
                    if (users.getPhone().equals(phone)){
                        if (users.getPassword().equals(password)){
                            Toast.makeText(MainActivity.this, "logged in Successfully....",Toast.LENGTH_SHORT).show();

                            loadingBar.dismiss();
                            Intent intent = new Intent(MainActivity.this,HomeActivity.class);//from Resgister to Login
                            startActivity(intent);

                        }

                    }

                }
                else{
                    Toast.makeText(MainActivity.this, "Account with this  "+phone+" do not exit",Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                    Toast.makeText(MainActivity.this, "you need to creat a new account. ",Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
