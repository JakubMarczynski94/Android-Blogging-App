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
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
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
import java.util.Date;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
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

    // --------------------------- beginning of variables for setup ------

    private CircleImageView setupImage;
    private Uri mainImageURI = null;
    private String user_id;
    private boolean isChanged = false;
    private EditText setupName;
    private TextView setup;
    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private Bitmap compressedImageFile;
    String download_uri;

    // ---------------------------- end of variables for setup-----------
    // ----------- beginning of variables for dashboard refinement --------
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;
    private TextView Settings;
    private TextView Messages;

    // ---- beginning of variables for my posts display -----------------
    private FirebaseAuth mAuth;
    private RecyclerView myblog_list_view;
    private List<BlogPost> myblog_list;
    private BlogRecyclerAdapter myblogRecyclerAdapter;
    private DocumentSnapshot lastVisible;
    private Boolean isFirstPageFirstLoad = true;

    // ---------------- end of initial parameter definitions ---------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_view);
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        auth = FirebaseAuth.getInstance();

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


            // -- setup part when user is non null meaning the user is authenticated

            user_id = auth.getCurrentUser().getUid();

            firebaseFirestore = FirebaseFirestore.getInstance();
            storageReference = FirebaseStorage.getInstance().getReference();

            setupImage = findViewById(R.id.setup_image);
            setupName = findViewById(R.id.setup_name);
            setup = findViewById(R.id.setup);

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

            setup.setOnClickListener(new View.OnClickListener() {
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
            // --------------- settings button ----------------

            Settings = findViewById(R.id.settings);

            Settings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Dashboard.this, Settings.class));
                    finish();
                }
            });

            // ---------------- message button --------------
            Messages = findViewById(R.id.takeToMessages);

            Messages.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Dashboard.this, Messages.class));
                    finish();
                }
            });

            //-------------------- beginning of my posts display in on create --------------

            myblog_list = new ArrayList<>();
            myblog_list_view = findViewById(R.id.myblog_list_view);

            firebaseAuth = FirebaseAuth.getInstance();

            // contains list of blog posts
            myblogRecyclerAdapter = new BlogRecyclerAdapter(myblog_list);
            ViewGroup container = findViewById(R.id.myposts_container);
            myblog_list_view.setLayoutManager(new LinearLayoutManager(container.getContext()));
            myblog_list_view.setAdapter(myblogRecyclerAdapter);
            myblog_list_view.setHasFixedSize(true);

            // which we made sure is correct by forcing login when you open the main activity
            if(firebaseAuth.getCurrentUser() != null) {

                firebaseFirestore = FirebaseFirestore.getInstance();
                myblog_list_view.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);

                        Boolean reachedBottom = !recyclerView.canScrollVertically(1);

                        if(reachedBottom){

                            loadMorePost();

                        }

                    }
                });

                Query firstQuery = firebaseFirestore.collection("Posts").whereEqualTo("user_id", user_id).limit(3);
                firstQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                        if (!documentSnapshots.isEmpty()) {

                            if (isFirstPageFirstLoad) {

                                lastVisible = documentSnapshots.getDocuments().get(documentSnapshots.size() - 1);
                                myblog_list.clear();

                            }

                            for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                                if (doc.getType() == DocumentChange.Type.ADDED) {

                                    String blogPostId = doc.getDocument().getId();
                                    BlogPost blogPost = doc.getDocument().toObject(BlogPost.class).withId(blogPostId);

                                    // manually set the timestamp as a date
                                    /*
                                    Timestamp timestamp = (Timestamp)doc.getDocument().get("timestamp");
                                    Date date = timestamp.toDate();
                                    blogPost.setTimestamp(date); */

                                    if (isFirstPageFirstLoad) {

                                        myblog_list.add(blogPost);

                                    } else {

                                        myblog_list.add(0, blogPost);

                                    }


                                    myblogRecyclerAdapter.notifyDataSetChanged();

                                }
                            }

                            isFirstPageFirstLoad = false;

                        }

                    }

                });

            }

            // --------------- end for my posts display
    }


    // ---------------------- setup methods outside of oncreate ----------------------
    // we keep the below code in order to add the username of the user

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

    // display more of my posts

    // -------------------- function for display posts

    public void loadMorePost(){

        if(firebaseAuth.getCurrentUser() != null) {

            Query nextQuery = firebaseFirestore.collection("Posts")
                    .whereEqualTo("user_id", user_id)
                    .startAfter(lastVisible)
                    .limit(3);

            nextQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                    if (!documentSnapshots.isEmpty()) {

                        lastVisible = documentSnapshots.getDocuments().get(documentSnapshots.size() - 1);
                        for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                            if (doc.getType() == DocumentChange.Type.ADDED) {

                                String blogPostId = doc.getDocument().getId();
                                BlogPost blogPost = doc.getDocument().toObject(BlogPost.class).withId(blogPostId);
                                myblog_list.add(blogPost);

                                myblogRecyclerAdapter.notifyDataSetChanged();
                            }

                        }
                    }

                }
            });

        }

    }
}


