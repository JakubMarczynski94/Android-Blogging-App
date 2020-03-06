package com.example.app;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import androidx.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ServerTimestamp;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.UUID;
import id.zelory.compressor.Compressor;

public class NewPostActivity2 extends AppCompatActivity{

    private EditText newPostTitle;
    private EditText newPostDesc;
    private EditText newPostTags;
    private ArrayList<String> newPostTagsArrayList;
    private String newPostTagsString;
    private EditText newPostLocation;
    private String firestoreDocumentName;

    private Button newPostBtn;
    private ProgressBar newPostProgress;
    private StorageReference storageReference;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private String current_user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post2);
        firestoreDocumentName = getIntent().getStringExtra("firestoreDocumentName");

        // need to initialize the references to firebase
        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        current_user_id = firebaseAuth.getCurrentUser().getUid();

        newPostTitle = findViewById(R.id.post_title);
        newPostDesc = findViewById(R.id.post_desc);
        newPostTags = findViewById(R.id.tags);
        newPostLocation = findViewById(R.id.post_location);
        newPostBtn = findViewById(R.id.post_btn2);
        newPostProgress = findViewById(R.id.new_post_progress2);

        newPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // you take the description written by the user and you convert it into a string
                final String title = newPostTitle.getText().toString();
                final String desc = newPostDesc.getText().toString();
                final String location = newPostLocation.getText().toString();
                newPostTagsString = newPostTags.getText().toString();
                // should implement the functionality for the words to change color when a space is added
                newPostTagsArrayList = new ArrayList<String>(Arrays.asList(newPostTagsString.trim().split("\\s+")));

                // means that the title was changed from its hint
                if(title != "Title"){

                    // then you see the progress bar which will be turning, show the progressbar
                    newPostProgress.setVisibility(View.VISIBLE);

                    Map<String, Object> postMap = new HashMap<>();
                    postMap.put("title", title);
                    postMap.put("desc", desc);
                    postMap.put("tags", newPostTagsArrayList);
                    postMap.put("location", location);

                    firebaseFirestore.collection("Posts").document(firestoreDocumentName).update(postMap).addOnSuccessListener(new OnSuccessListener<Void>() {

                        @Override
                        public void onSuccess(Void aVoid) {

                            Toast.makeText(NewPostActivity2.this, "Post was added", Toast.LENGTH_LONG).show();
                            Intent mainIntent = new Intent(NewPostActivity2.this, MainActivity.class);
                            startActivity(mainIntent);
                            finish();
                            newPostProgress.setVisibility(View.INVISIBLE);
                        }
                    }).addOnFailureListener(new OnFailureListener() {

                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(NewPostActivity2.this, "Part 2 was not added to firestore", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
}
