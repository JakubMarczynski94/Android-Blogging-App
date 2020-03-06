package com.example.app;
import android.app.SearchManager;

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

// maybe should instead extend cardview
// https://stackoverflow.com/questions/29540724/start-new-activity-from-searchview

public class SearchActivity extends AppCompatActivity {

    // for post display
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private String current_user_id;
    private RecyclerView blog_list_view;
    // list of class elements
    private List<BlogPost> blog_list;
    private FirebaseAuth firebaseAuth;
    private BlogRecyclerAdapter blogRecyclerAdapter;
    private DocumentSnapshot lastVisible;
    private Boolean isFirstPageFirstLoad = true;
    public String query;
    Query nextQuery;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            query = intent.getStringExtra(SearchManager.QUERY);
            doMySearch(query);
        }
    }

    /* necessary when you are displaying the search in the same place that you did the search so maybe here unecessary
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            doMySearch(query);
        }
    }
    */

    public void doMySearch(String query){

        // need to change the code below in order to append the correct blogs into this activity

        blog_list = new ArrayList<>();
        blog_list_view = findViewById(R.id.searchblog_list_view);

        firebaseAuth = FirebaseAuth.getInstance();

        // contains list of blog posts
        blogRecyclerAdapter = new BlogRecyclerAdapter(blog_list);
        ViewGroup container = findViewById(R.id.search_container);
        blog_list_view.setLayoutManager(new LinearLayoutManager(container.getContext()));
        blog_list_view.setAdapter(blogRecyclerAdapter);
        blog_list_view.setHasFixedSize(true);

        // which we made sure is correct by forcing login when you open the main activity
        if(firebaseAuth.getCurrentUser() != null) {

            firebaseFirestore = FirebaseFirestore.getInstance();

            blog_list_view.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    Boolean reachedBottom = !recyclerView.canScrollVertically(1);

                    if(reachedBottom){

                        loadMorePost();

                    }

                }
            });

            Query firstQuery = firebaseFirestore.collection("Posts").whereArrayContains("tags", query).limit(3);
            firstQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                    if (!documentSnapshots.isEmpty()) {
                        // we found the document snapshot is not empty
                        // Toast.makeText(SearchActivity.this, "Document snapshot is not empty", Toast.LENGTH_SHORT).show();

                        if (isFirstPageFirstLoad) {

                            lastVisible = documentSnapshots.getDocuments().get(documentSnapshots.size() - 1);
                            blog_list.clear();
                        }

                        for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                            if (doc.getType() == DocumentChange.Type.ADDED) {

                                String blogPostId = doc.getDocument().getId();
                                BlogPost blogPost = doc.getDocument().toObject(BlogPost.class).withId(blogPostId);

                                if (isFirstPageFirstLoad) {
                                    // we get to this stage
                                    // Toast.makeText(SearchActivity.this, "we add the blogpost to the list", Toast.LENGTH_SHORT).show();
                                    blog_list.add(blogPost);

                                } else {

                                    blog_list.add(0, blogPost);

                                }

                                blogRecyclerAdapter.notifyDataSetChanged();

                            }
                        }

                        isFirstPageFirstLoad = false;

                    }

                }

            });

        }

    }

    // need to change the below code
    public void loadMorePost(){

        if(firebaseAuth.getCurrentUser() != null) {

            firebaseFirestore.collection("Posts").whereArrayContains("tags", query)
                    .startAfter(lastVisible).limit(3).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                    if (!queryDocumentSnapshots.isEmpty()) {

                        lastVisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);
                        for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {

                            if (doc.getType() == DocumentChange.Type.ADDED) {

                                String blogPostId = doc.getDocument().getId();
                                BlogPost blogPost = doc.getDocument().toObject(BlogPost.class).withId(blogPostId);
                                blog_list.add(blogPost);

                                blogRecyclerAdapter.notifyDataSetChanged();
                            }

                        }
                    }
                }
            });

        /*
        nextQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                            if (!documentSnapshots.isEmpty()) {

                                lastVisible = documentSnapshots.getDocuments().get(documentSnapshots.size() - 1);
                                for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                                    if (doc.getType() == DocumentChange.Type.ADDED) {

                                        String blogPostId = doc.getDocument().getId();
                                        BlogPost blogPost = doc.getDocument().toObject(BlogPost.class).withId(blogPostId);
                                        blog_list.add(blogPost);

                                        blogRecyclerAdapter.notifyDataSetChanged();
                                    }

                                }
                            }

                        }
                    }); */


        }

    }
}
