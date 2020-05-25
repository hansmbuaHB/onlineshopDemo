package com.example.onlineshoping;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.HashMap;

public class ResgisterActivity extends AppCompatActivity {
    private Button CreateAccountButton;
    private EditText InputName,InputPhoneNumber,InputPassword;
    private ProgressDialog loadingBar;
    private FirebaseApp mFirebaseApp;
    private DatePicker mDatePickerDialog;
    private TextView datePickerTextview;
    private RelativeLayout relativelayoutid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resgister);
            CreateAccountButton = (Button)findViewById(R.id.register_btn);
            InputName = (EditText) findViewById(R.id.register_userName_input);
           InputPhoneNumber  = (EditText) findViewById(R.id.register_phone_number_input);
            InputPassword  = (EditText) findViewById(R.id.register_passWord_input);
            datePickerTextview = (TextView)findViewById(R.id.DatepickerTextView) ;
            mDatePickerDialog = (DatePicker)findViewById(R.id.DatepickerID);
        relativelayoutid = (RelativeLayout)findViewById(R.id.relativelayoutid);

        Context context;
        loadingBar = new ProgressDialog(this);

            datePickerTextview.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onClick(View v) {
//                   LocalDate date = LocalDate.of(mDatePickerDialog.getYear(),mDatePickerDialog.getMonth(),mDatePickerDialog.getDayOfMonth());
//                   String text = date.format(DateTimeFormatter.ISO_LOCAL_DATE);
//                   datePickerTextview.setText(text);
                   Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.YEAR,mDatePickerDialog.getYear());
                    calendar.set(Calendar.DAY_OF_MONTH,mDatePickerDialog.getDayOfMonth());
                    calendar.set(Calendar.MONTH,mDatePickerDialog.getMonth());
                    int  year = calendar.get(Calendar.YEAR);
                    int  day = calendar.get(Calendar.DAY_OF_MONTH);
                    int month = calendar.get(Calendar.MONTH)+1;

                    datePickerTextview.setText(" "+day+" /"+month+" /"+year);


                   mDatePickerDialog.setVisibility(View.VISIBLE);

                }
            });
        relativelayoutid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatePickerDialog.setVisibility(View.INVISIBLE);
            }
        });



      InputPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatePickerDialog.setVisibility(View.INVISIBLE);

            }
        });






        CreateAccountButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CreateAccount();
                }
            });


    }

    public void CreateAccount(){
        String name = InputName.getText().toString();
        String phoneNumber = InputPhoneNumber.getText().toString();
        String password = InputPassword.getText().toString();
       //checking if object is empty
        if (TextUtils.isEmpty(name)){
            Toast.makeText(this, "Please Write your name bitch ",Toast.LENGTH_SHORT).show();
        }
       else if (TextUtils.isEmpty(phoneNumber)){
            Toast.makeText(this, "Please Write your Phone Number bitch ",Toast.LENGTH_SHORT).show();
        }

       else if (TextUtils.isEmpty(password)){
            Toast.makeText(this, "Please Write your Password bitch ",Toast.LENGTH_SHORT).show();
        }
       else{
             loadingBar.setTitle("Create Account");
             loadingBar.setMessage("Please wait, while we are checking the credentials");
             loadingBar.setCanceledOnTouchOutside(false);
             loadingBar.show();
             ValidatePhoneNumber(name,phoneNumber,password);
        }

    }

    private void ValidatePhoneNumber(final String name, final String phoneNumber, final String password) {
        final DatabaseReference rootRef;
        rootRef     = FirebaseDatabase.getInstance().getReference();
         rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 if(!(dataSnapshot.child("Users").child(phoneNumber)).exists()){
                         HashMap<String, Object> userDataMap = new HashMap<>();
                         userDataMap.put("phone",phoneNumber);
                         userDataMap.put("name",name);
                         userDataMap.put("password",password);
                         //adding field inside the database taken from Hasmap, and updating children
                         rootRef.child("Users").child(phoneNumber).updateChildren(userDataMap)
                                 .addOnCompleteListener(new OnCompleteListener<Void>() {
                                     @Override
                                     public void onComplete(@NonNull Task<Void> task) {
                                         if (task.isSuccessful()){
                                             Toast.makeText(ResgisterActivity.this, "congratulation your account has been created ",Toast.LENGTH_SHORT).show();
                                            // clearFields();
                                             loadingBar.dismiss();
                                             Intent intent = new Intent(ResgisterActivity.this,LoginActivity.class);//from Resgister to Login
                                             startActivity(intent);

                                         }else
                                         { loadingBar.dismiss();
                                             Toast.makeText(ResgisterActivity.this, "Network Error: Please try again after some time... ",Toast.LENGTH_SHORT).show();
                                         }
                                     }
                                 });
                 }
                 else{
                     Toast.makeText(ResgisterActivity.this, "this "+phoneNumber+" already exist ",Toast.LENGTH_SHORT).show();
                     loadingBar.dismiss();
                     Toast.makeText(ResgisterActivity.this, "please try using another phone number ",Toast.LENGTH_SHORT).show();
                     Intent intent = new Intent(ResgisterActivity.this,MainActivity.class);
                     startActivity(intent);
                 }
             }

             @Override
             public void onCancelled(@NonNull DatabaseError databaseError) {

             }
         });


    }
    public void clearFields(){
        //InputName,InputPhoneNumber,InputPassword
        InputName.setText(" ");
        InputPhoneNumber.setText(" ");
        InputPassword.setText(" ");
    }
}
