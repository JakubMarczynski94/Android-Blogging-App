package com.example.app;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class Dashboard extends AppCompatActivity {


    /* ------------------------- LOGIN ------------------ */


    private Button btnChangeEmail, btnChangePassword, btnSendResetEmail, btnRemoveUser,
            changeEmail, changePassword, sendEmail, remove, signOut;

    private EditText oldEmail, newEmail, password, newPassword;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;


    /*---------------------- END of LOGIN VAR--------------------*/

    // -------------------- beginning of variables for chat --------------

    ListView usersList;
    TextView noUsersText;

    // this arraylist grows in size automatically when new elements are added to it, this particular one contains elements that are of the
    // string data type

    ArrayList<String> al = new ArrayList<>();
    int totalUsers = 0;

    // -------------------------------------- end of variables for chat ----------------
    // ------------------------------------- beginning of variables for setup ------

    private CircleImageView setupImage;
    private Uri mainImageURI = null;

    private String user_id;

    private boolean isChanged = false;

    private EditText setupName;
    private Button setupBtn;

    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    private Bitmap compressedImageFile;
    String download_uri;

    // ---------------------------- end of variables for setup

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_view);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.nav_view);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.featured:
                        Intent a = new Intent(Dashboard.this, MainActivity.class);
                        startActivity(a);
                        break;
                    case R.id.dashboard:
                        break;
                    case R.id.apply:
                        Intent b = new Intent(Dashboard.this, Apply.class);
                        startActivity(b);

                        break;
                }
                return false;
            }
        });


        /* ........................... LOGIN ........................................... */

        //get firebase auth instance
        auth = FirebaseAuth.getInstance();

        //get current user
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        authListener = new FirebaseAuth.AuthStateListener() {

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity, we get to this part but then the LoginActivity is having trouble launching
                    startActivity(new Intent(Dashboard.this, LoginActivity.class));
                    finish();
                }
            }
        };

        btnChangeEmail = (Button) findViewById(R.id.change_email_button);
        btnChangePassword = (Button) findViewById(R.id.change_password_button);
        btnSendResetEmail = (Button) findViewById(R.id.sending_pass_reset_button);
        btnRemoveUser = (Button) findViewById(R.id.remove_user_button);
        changeEmail = (Button) findViewById(R.id.changeEmail);
        changePassword = (Button) findViewById(R.id.changePass);
        sendEmail = (Button) findViewById(R.id.send);
        remove = (Button) findViewById(R.id.remove);
        signOut = (Button) findViewById(R.id.sign_out);

        oldEmail = (EditText) findViewById(R.id.old_email);
        newEmail = (EditText) findViewById(R.id.new_email);
        password = (EditText) findViewById(R.id.password);
        newPassword = (EditText) findViewById(R.id.newPassword);

        oldEmail.setVisibility(View.GONE);
        newEmail.setVisibility(View.GONE);
        password.setVisibility(View.GONE);
        newPassword.setVisibility(View.GONE);
        changeEmail.setVisibility(View.GONE);
        changePassword.setVisibility(View.GONE);
        sendEmail.setVisibility(View.GONE);
        remove.setVisibility(View.GONE);


         btnChangeEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oldEmail.setVisibility(View.GONE);
                newEmail.setVisibility(View.VISIBLE);
                password.setVisibility(View.GONE);
                newPassword.setVisibility(View.GONE);
                changeEmail.setVisibility(View.VISIBLE);
                changePassword.setVisibility(View.GONE);
                sendEmail.setVisibility(View.GONE);
                remove.setVisibility(View.GONE);
            }
        });

        changeEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (user != null && !newEmail.getText().toString().trim().equals("")) {
                    user.updateEmail(newEmail.getText().toString().trim())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(Dashboard.this, "Email address is updated. Please sign in with new email id!", Toast.LENGTH_LONG).show();
                                        signOut();

                                    } else {
                                        Toast.makeText(Dashboard.this, "Failed to update email!", Toast.LENGTH_LONG).show();

                                    }
                                }
                            });
                } else if (newEmail.getText().toString().trim().equals("")) {
                    newEmail.setError("Enter email");

                }
            }
        });


        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oldEmail.setVisibility(View.GONE);
                newEmail.setVisibility(View.GONE);
                password.setVisibility(View.GONE);
                newPassword.setVisibility(View.VISIBLE);
                changeEmail.setVisibility(View.GONE);
                changePassword.setVisibility(View.VISIBLE);
                sendEmail.setVisibility(View.GONE);
                remove.setVisibility(View.GONE);
            }
        });

        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (user != null && !newPassword.getText().toString().trim().equals("")) {
                    if (newPassword.getText().toString().trim().length() < 6) {
                        newPassword.setError("Password too short, enter minimum 6 characters");

                    } else {
                        user.updatePassword(newPassword.getText().toString().trim())
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(Dashboard.this, "Password is updated, sign in with new password!", Toast.LENGTH_SHORT).show();
                                            signOut();

                                        } else {
                                            Toast.makeText(Dashboard.this, "Failed to update password!", Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                });
                    }
                } else if (newPassword.getText().toString().trim().equals("")) {
                    newPassword.setError("Enter password");

                }
            }
        });

        btnSendResetEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oldEmail.setVisibility(View.VISIBLE);
                newEmail.setVisibility(View.GONE);
                password.setVisibility(View.GONE);
                newPassword.setVisibility(View.GONE);
                changeEmail.setVisibility(View.GONE);
                changePassword.setVisibility(View.GONE);
                sendEmail.setVisibility(View.VISIBLE);
                remove.setVisibility(View.GONE);
            }
        });

        sendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!oldEmail.getText().toString().trim().equals("")) {
                    auth.sendPasswordResetEmail(oldEmail.getText().toString().trim())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(Dashboard.this, "Reset password email is sent!", Toast.LENGTH_SHORT).show();

                                    } else {
                                        Toast.makeText(Dashboard.this, "Failed to send reset email!", Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });
                } else {
                    oldEmail.setError("Enter email");

                }
            }
        });

        btnRemoveUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (user != null) {
                    user.delete()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(Dashboard.this, "Your profile is deleted:( Create a account now!", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(Dashboard.this, SignupActivity.class));
                                        finish();
                                    } else {
                                        Toast.makeText(Dashboard.this, "Failed to delete your account!", Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });
                }
            }
        });

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });

        // ------------------------------------------------ CHAT PART -----------------

        usersList = (ListView)findViewById(R.id.usersList);
        noUsersText = (TextView)findViewById(R.id.noUsersText);

        if(user != null) {
            // this is the url of our table that contains in the users sub-table the information about the users
            String url = "https://appdata-67dc1.firebaseio.com/users.json";

            // Finds the text on the internet, and you can retrieve it. With this you make an HTTP request that has to be made
            // and you parse it as a string. This request specifies the method, the url, and the listeners invoked when you have a success or a failure

            StringRequest request = new StringRequest(Request.Method.GET, url,

                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String s) {
                            doOnSuccess(s);
                        }
                    },

                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            System.out.println("" + volleyError);
                        }
                    });

            RequestQueue rQueue = Volley.newRequestQueue(Dashboard.this);

            // meaning you add the above request into the queu, which is managed by the volley class
            rQueue.add(request);

            // adapterview extends viewgroup, generally takes in an array as parameter
            // OnItemClickListener is an interface definition for a call back to be invoked when an item in this adapterview has been clicked
            usersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                // adapterview where the click happened, the view within the adapterview that was clicked, the position of the view within the adapter, the row id of the item that was clicked
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    // chat with the person which was clicked in this array that we are considering, at the right position
                    UserDetails.chatWith = al.get(position);
                    // when clicked we move from this class to the other class, which itself will have a new layout
                    startActivity(new Intent(Dashboard.this, Chat.class));
                }
            });
            // -------------------- END OF CHAT PART ------------------------------------------------------
            // -- setup part when user is non null meaning the user is authenticated

            user_id = auth.getCurrentUser().getUid();

            firebaseFirestore = FirebaseFirestore.getInstance();
            storageReference = FirebaseStorage.getInstance().getReference();

            setupImage = findViewById(R.id.setup_image);
            setupName = findViewById(R.id.setup_name);
            setupBtn = findViewById(R.id.setup_btn);

            firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    if(task.isSuccessful()) {

                        if (task.getResult().exists()) {

                            // here we have set the name if it exists in firestore
                            String name = task.getResult().getString("name");
                            setupName.setText(name);

                        } else {

                            if (task.getException() != null){
                                String error = task.getException().getMessage();
                                Toast.makeText(Dashboard.this, "(FIRESTORE Retrieve Error) : " + error, Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }
            });

            // this profile picture is not mandatory, and there is a default image if the user does not want to set a picture
            storageReference.child("profile_images").child(user_id + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {

                    // we also need to set the correct username if it exists in firestore
                    StorageReference profile_reference = storageReference.child("profile_images").child(user_id + ".jpg");
                    GlideApp.with(Dashboard.this).load(profile_reference).placeholder(R.drawable.default_image).into(setupImage);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // in the case when we do not find a profile image then we just set this image resource to the default
                    // the name is also not set in this case, no error warning is emitted
                    setupImage.setImageResource(R.drawable.default_image);
                }
            });

            setupBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final String user_name = setupName.getText().toString();

                    // here for some reason we input the URI, and check whether it is not null
                    if (!TextUtils.isEmpty(user_name) && mainImageURI != null) {

                        if (isChanged) {

                            user_id = auth.getCurrentUser().getUid();
                            // the uri is like a pointer to some other content and changes with time
                            // the url also specify how to get to the resource, and the uri just name the resource
                            // URNs are like URIs but also require to be unique

                            // the uri.getPath() and uri.toString() return two different things
                            File newImageFile = new File(mainImageURI.getPath());

                            try {

                                compressedImageFile = new Compressor(Dashboard.this)
                                        .setMaxHeight(125)
                                        .setMaxWidth(125)
                                        .setQuality(50)
                                        .compressToBitmap(newImageFile);

                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            // used to write data into files written into an array of bytes and then sent to multiple files
                            // it holds a copy of data and sends it to multiple streams
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                            // put into the byte array output stream, and then the method toByteArray is used to create a newly allocated byte array
                            byte[] thumbData = baos.toByteArray();

                            // putBytes() takes a byte[] and returns an upload task, this requires the app to contain the entire contents of a file at once
                            UploadTask image_path = storageReference.child("profile_images").child(user_id + ".jpg").putBytes(thumbData);

                            image_path.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                                    // suppose that now we have stored in the storage the byte[] array containing our image
                                    if (task.isSuccessful()) {
                                        storeFirestore(task, user_name);

                                    } else {

                                        String error = task.getException().getMessage();
                                        Toast.makeText(Dashboard.this, "(IMAGE Error) : " + error, Toast.LENGTH_LONG).show();

                                    }
                                }
                            });

                        } else {

                            // supposing that the image is not changed, hence it is the same, we still refer to the inner function firestore but the task here is null
                            storeFirestore(null, user_name);

                        }

                    }

                }

            });

            setupImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // on the right hand side we have the version code for the marshmallow platform
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

                        // to declare that an app needs a permission, this is written in the android manifest, the user needs to approve the persmissions are runtime
                        // the expression below checks if we have the required permission, here first we treat the case when the permission is not granted
                        if(ContextCompat.checkSelfPermission(Dashboard.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

                            Toast.makeText(Dashboard.this, "Permission Denied", Toast.LENGTH_LONG).show();
                            // here a standard android dialog pops up which we can not customize
                            ActivityCompat.requestPermissions(Dashboard.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

                        } else {

                            // supposing that we have the permission indeed to access the gallery then that is what we do
                            BringImagePicker();

                        }

                    } else {

                        BringImagePicker();

                    }

                }

            });

            // -------------------------- end of setup -----------------
        }

    }

    //sign out method
    public void signOut() {
        auth.signOut();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }

// ---------- CHAT PART

    // when you are indeed able to retrieve the right data from the database of the users, we perform the following action

    public void doOnSuccess(String s){
        try {

            // used for client-server communication, stores unordered key-value pairs
            JSONObject obj = new JSONObject(s);

            // in our case the keys must be the uid's
            Iterator i = obj.keys();
            String key = "";

            while(i.hasNext()){

                // you transform the Uid to a string
                key = i.next().toString();
                String currentUID = FirebaseAuth.getInstance().getCurrentUser().getUid();

                // then here you should be comparing the key with the current key under consideration, of the current authenticated user
                if(!key.equals(
                        // UserDetails.email
                        currentUID
                )) {
                    al.add(key);
                }

                // when you discovered new users, you add a user
                totalUsers++;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // probably means when you are the only user, then you set the view that there are no users to true
        if(totalUsers <=1){
            noUsersText.setVisibility(View.VISIBLE);
            usersList.setVisibility(View.GONE);
        }
        else{
            noUsersText.setVisibility(View.GONE);
            usersList.setVisibility(View.VISIBLE);

            // then you have the usersList listview from the beginning and then you set the corresponding adapter
            // used to treat a database, file and transform it into UI material
            // context is the reference of the current class
            // used to set ou the layout for the list items in which you have a text view
            // array of objects used to set the textView
            usersList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, al));
        }

    }

    // ---------------------- setup methods ----------------------
    // we keep the below code in order to add the usernam of the user

    private void storeFirestore(@NonNull Task<UploadTask.TaskSnapshot> task, String user_name) {

        String user_id = auth.getCurrentUser().getUid();

        Map<String, String> userMap = new HashMap<>();
        userMap.put("name", user_name);

        firebaseFirestore.collection("Users").document(user_id).set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){

                    Toast.makeText(Dashboard.this, "The user Settings are updated.", Toast.LENGTH_LONG).show();

                } else {

                    String error = task.getException().getMessage();
                    Toast.makeText(Dashboard.this, "(FIRESTORE Error) : " + error, Toast.LENGTH_LONG).show();
                }
            }
        });

    }


    // these two methods below though are still necessary
    private void BringImagePicker() {

        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .start(Dashboard.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                // suppose that the image was indeed cropped then the result is called result
                // then we get the Uri or the name of the result, it's like a pointer on the result and we set the uri of the circle to this one
                // in this case the image really has been changed
                mainImageURI = result.getUri();
                setupImage.setImageURI(mainImageURI);

                isChanged = true;

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                // supposing that there is an error while cropping the image then we actually display the error in a toast
                Exception error = result.getError();
                Toast.makeText(this, "The error with the crop :" + error.toString(), Toast.LENGTH_SHORT).show();

            }
        }

    }
}


