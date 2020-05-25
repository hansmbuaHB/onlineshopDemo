package com.example.onlineshoping;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class AdminAddNewProductActivity extends AppCompatActivity {
    private String CategoryName, description, price, pnam,saveCurrentDate,saveCurrentTime;
    private Button AddNewProductButton;
    private EditText InputProductName, InputProductDiscription, InputProductPrice;
    private ImageView InputProductImage;
    private final static int Gallarypick = 1;
    private Uri ImageUri;
    private String productRandomKey,downloadimageUrl;
    private StorageReference productImageRef;
    private DatabaseReference ProductRef;
    private ProgressDialog loadingBar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_new_product);

        CategoryName = getIntent().getExtras().get("category").toString();
        //creating the falder
        productImageRef = FirebaseStorage.getInstance().getReference().child("Product Images");
        ProductRef = FirebaseDatabase.getInstance().getReference().child("Products");

        //loadingBar
        loadingBar = new ProgressDialog(this);

        AddNewProductButton = (Button) findViewById(R.id.add_new_product);
        InputProductName = (EditText) findViewById(R.id.product_name);
        InputProductDiscription = (EditText) findViewById(R.id.description);
        InputProductPrice = (EditText) findViewById(R.id.product_price);
        InputProductImage = (ImageView) findViewById(R.id.select_product_image);


        InputProductImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenGallary();
            }

        });

        AddNewProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidateProductData();


            }
        });


    }



    private void OpenGallary()
    {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, Gallarypick);
    }

    //getting image from the phone using intent
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode== Gallarypick  &&  resultCode==RESULT_OK  &&  data!=null)
        {
            ImageUri = data.getData();
            InputProductImage.setImageURI(ImageUri);
        }
    }
    //Valiate product and check the the conditions
    private void ValidateProductData() {
        description = InputProductDiscription.getText().toString();
        price = InputProductPrice.getText().toString();
        pnam = InputProductName.getText().toString();
        if (ImageUri == null) {
            Toast.makeText(AdminAddNewProductActivity.this, "Product Image is mandatory....", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(description)) {
            Toast.makeText(AdminAddNewProductActivity.this, "please write product description....", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(price)) {
            Toast.makeText(AdminAddNewProductActivity.this, "please write product price....", Toast.LENGTH_SHORT).show();

        } else if (TextUtils.isEmpty(pnam)) {
            Toast.makeText(AdminAddNewProductActivity.this, "please write product productName....", Toast.LENGTH_SHORT).show();

        } else {
            //store the product to the data base
            StoreProductInformation();

        }


    }
     //contain product name description to be stored in the database

    private void StoreProductInformation() {

        loadingBar.setTitle("Add new Product");
        loadingBar.setMessage("Dear Admin please wait while we are adding the new product..");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd,yyyy");//hmmm
            saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");//hmmm
        saveCurrentDate = currentDate.format(calendar.getTime());
        //creating unique random key
     productRandomKey = saveCurrentDate + saveCurrentTime;
     //Storing the product image inside the firebase
        final StorageReference filePath = productImageRef.child(ImageUri.getLastPathSegment() + productRandomKey+".jpg"); //getLastPathSegment gets the image name
      //uploading image to the database
        final UploadTask uploadTask = filePath.putFile(ImageUri);
        //if the is any failor
          uploadTask.addOnFailureListener(new OnFailureListener() {
              @Override
              public void onFailure(@NonNull Exception e) {
                  String message = e.toString();
                  Toast.makeText(AdminAddNewProductActivity.this, "Error: "+message, Toast.LENGTH_SHORT).show();
                  loadingBar.dismiss();
              }
          }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
              @Override
              public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                  Toast.makeText(AdminAddNewProductActivity.this, "Product Uploaded succesfully ", Toast.LENGTH_SHORT).show();

                  //get the link of image an store into firebase
                  Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                      @Override
                      public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                          if(!task.isSuccessful()){
                              throw task.getException();




                          }

                        downloadimageUrl  = filePath.getDownloadUrl().toString();
                          return filePath.getDownloadUrl();
                      }
                  }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                      @Override
                      public void onComplete(@NonNull Task<Uri> task) {
                          if (task.isSuccessful()){

                              downloadimageUrl = task.getResult().toString();
                              Toast.makeText(AdminAddNewProductActivity.this, "got Product Image Url sucessfully ....", Toast.LENGTH_SHORT).show();
                              SaveProductInforToDatabase();
                          }
                      }
                  });
              }
          });
    }

    private void SaveProductInforToDatabase() {

        HashMap<String, Object> productMap = new HashMap<>();
        productMap.put("pid",productRandomKey);
        productMap.put("date",saveCurrentDate);
        productMap.put("time",saveCurrentDate);
        productMap.put("description",description);
        productMap.put("image",downloadimageUrl);
        productMap.put("category",CategoryName);
        productMap.put("price",price);
        productMap.put("pname",pnam);


        ProductRef.child(productRandomKey).updateChildren(productMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Intent intent = new Intent(AdminAddNewProductActivity.this,AdminCategoryActivity.class);//this has been changed
                            startActivity(intent);
                            loadingBar.dismiss();
                            Toast.makeText(AdminAddNewProductActivity.this, "Product is added Successfully ", Toast.LENGTH_SHORT).show();

                        }else{
                            loadingBar.dismiss();
                            String message = task.getException().toString();
                            Toast.makeText(AdminAddNewProductActivity.this, "Error: "+message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

}
