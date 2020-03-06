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
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import id.zelory.compressor.Compressor;
// comments added

public class NewPostActivity extends AppCompatActivity {

    private ImageView newPostImage;
    private EditText newPostDesc;
    private Button newPostBtn;
    private Uri postImageUri = null;
    private ProgressBar newPostProgress;
    private StorageReference storageReference;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private String current_user_id;
    private Bitmap compressedImageFile;

    // variables added with displaying the post part
    String downloadUri;
    String downloadthumbUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        // need to initialize the references to firebase
        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        current_user_id = firebaseAuth.getCurrentUser().getUid();

        newPostImage = findViewById(R.id.new_post_image);
        newPostDesc = findViewById(R.id.new_post_desc);
        newPostBtn = findViewById(R.id.post_btn);
        newPostProgress = findViewById(R.id.new_post_progress);

        newPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // here you use this external library for cropping images, which is activated when you crop an image
                // you make this crop result into a square
                // it is associated with the current class

                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setMinCropResultSize(512, 512)
                        .setAspectRatio(1, 1)
                        .start(NewPostActivity.this);

            }
        });

        newPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // you take the description written by the user and you convert it into a string

                final String desc = newPostDesc.getText().toString();

                if(!TextUtils.isEmpty(desc) && postImageUri != null){

                    // then you see the progress bar which will be turning, show the progressbar
                    newPostProgress.setVisibility(View.VISIBLE);

                    // class that represents an immutable universally unique identifier UUID
                    // there are four types, time based, DCE security, name based and randomly generated ones
                    // used to create sessions ids in web applications and used to create transactions ids
                    // first parameter specifies the most significant bits of the UUID, and the second the least significant bits
                    // retrieve the timestamp
                    final String randomName = UUID.randomUUID().toString();

                    // here you have the absolute path of the file in the parameter
                    File newImageFile = new File(postImageUri.getPath());

                    try {
                        compressedImageFile = new Compressor(NewPostActivity.this)
                                .setMaxHeight(720)
                                .setMaxWidth(720)
                                .setQuality(50)
                                .compressToBitmap(newImageFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    // creates a buffer in the memory and all the data sent to the stream is stored in the buffer
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    // this method creates a newly allocated byte array, contents of the buffer are copied into it
                    // returns the current contents of the output stream as a byte array
                    byte[] imageData = baos.toByteArray();

                    // PHOTO UPLOAD
                    // we access the storage of firebase storage reference, then we create a subnode called post images, append the uri and the jpg extension
                    // image data is put as bytes in this storage location
                    // we want a random string to be generated for the images
                    UploadTask filePath = storageReference.child("post_images").child(randomName + ".jpg").putBytes(imageData);

                    // when the task has been completed, we take a snapshot of the data sent through the task
                    filePath.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task) {

                            // download the image to firestore ---------------------

                            StorageReference storageRef = FirebaseStorage.getInstance().getReference();

                            storageRef.child("post_images").child(randomName + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {

                                @Override
                                public void onSuccess(Uri uri) {
                                    downloadUri = uri.toString();
                                }

                            }).addOnFailureListener(new OnFailureListener() {

                                @Override
                                public void onFailure(@NonNull Exception exception) {

                                    Toast.makeText(NewPostActivity.this, "Image not added to storage and database", Toast.LENGTH_LONG).show();
                                }
                            });

                            //---------------------------------------------
                            // final String downloadUri = task.getResult().getMetadata().getReference().getDownloadUrl().toString();

                            if(task.isSuccessful()){

                                File newThumbFile = new File(postImageUri.getPath());
                                try {

                                    compressedImageFile = new Compressor(NewPostActivity.this)
                                            .setMaxHeight(100)
                                            .setMaxWidth(100)
                                            .setQuality(1)
                                            .compressToBitmap(newThumbFile);

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                byte[] thumbData = baos.toByteArray();

                                UploadTask uploadTask = storageReference.child("post_images/thumbs")
                                        .child(randomName + ".jpg").putBytes(thumbData);

                                // the above is a success, but the text is not saved in the firestore database

                                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                        // this getreference() etc is correct because there was an upload into the storage space

                                        StorageReference storageRef = FirebaseStorage.getInstance().getReference();

                                        storageRef.child("post_images/thumbs").child(randomName + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {

                                            @Override
                                            public void onSuccess(Uri uri) {
                                                downloadthumbUri = uri.toString();
                                            }

                                        }).addOnFailureListener(new OnFailureListener() {

                                            @Override
                                            public void onFailure(@NonNull Exception exception) {

                                                Toast.makeText(NewPostActivity.this, "Thumbnail image not added to storage and database", Toast.LENGTH_LONG).show();
                                            }
                                        });

                                        // String downloadthumbUri = taskSnapshot.getMetadata().getReference().getDownloadUrl().toString();

                                        // TODO: for the moment the thumburl does not appear in the database storage despite code above
                                        // key type string, value type is general object, we can put multiple values
                                        Map<String, Object> postMap = new HashMap<>();
                                        postMap.put("image_url", downloadUri);
                                        postMap.put("image_thumb", downloadthumbUri);
                                        postMap.put("text", desc);
                                        // the user who logged in posted the image
                                        postMap.put("user_id", current_user_id);
                                        // when the post has been posted
                                        postMap.put("timestamp", FieldValue.serverTimestamp());

                                        // we upload our image into the collection of firebase firestore
                                        // adds random id for document

                                        // TEST -----------------------------------
                                        /*
                                        System.out.println(postMap.toString());

                                        DocumentReference docRef = firebaseFirestore.collection("Posts").document("4NOxSY3vzhBmd3ixzhaO");

                                        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                if(documentSnapshot.exists()){
                                                    Toast.makeText(NewPostActivity.this, "have accessed doc", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(NewPostActivity.this, "empty documente", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(NewPostActivity.this, "wasn't able to access", Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                         */
                                        // END OF TEST -------------------------

                                        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                                        final String timestampString = timestamp.toString();

                                        firebaseFirestore.collection("Posts").document(current_user_id + timestampString)
                                                .set(postMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                                // go to blog post page and show a text
                                                Toast.makeText(NewPostActivity.this, "Part 1 was added", Toast.LENGTH_LONG).show();
                                                // then we want to go to our main page, and we want to make sure the user can't press the back button so add finish()
                                                Intent mainIntent = new Intent(NewPostActivity.this, NewPostActivity2.class);
                                                mainIntent.putExtra("firestoreDocumentName", current_user_id + timestampString);
                                                startActivity(mainIntent);
                                                finish();

                                                // progressbar should disappear in both cases
                                                newPostProgress.setVisibility(View.INVISIBLE);

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(NewPostActivity.this, "Part 1 was added to storage but not to firestore", Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                        Toast.makeText(NewPostActivity.this, "Part 1 was not added to storage", Toast.LENGTH_LONG).show();

                                    }
                                });


                            } else {

                                newPostProgress.setVisibility(View.INVISIBLE);

                            }

                        }
                    });

                }

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                postImageUri = result.getUri();
                newPostImage.setImageURI(postImageUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();

            }
        }

    }

}


