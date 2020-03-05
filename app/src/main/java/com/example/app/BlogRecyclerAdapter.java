package com.example.app;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import de.hdodenhof.circleimageview.CircleImageView;
// comments added

public class BlogRecyclerAdapter extends RecyclerView.Adapter<BlogRecyclerAdapter.ViewHolder> {

    // we will have a list of blog posts
    public List<BlogPost> blog_list;

    // is the below necessary? after removing the fragment code?
    public Context context;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    // receives a list of classes of blogposts which is a class
    public BlogRecyclerAdapter(List<BlogPost> blog_list){

        // the value of our private variable, now blog_post retrieves all data
        this.blog_list = blog_list;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.blog_list_item, parent, false);
        context = parent.getContext();

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        // you return a copy of the view above, maybe because you don't want to actually change the view defined above
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        holder.setIsRecyclable(false);

        // because blog_list is a list, so you can get an item from a specific position.
        // since BlogPostId is a public parameter in a public class I can access it here
        final String blogPostId = blog_list.get(position).BlogPostId;
        final String currentUserId = firebaseAuth.getCurrentUser().getUid();

        // here you use a getter method to get the descrption of that specific blog post
        String desc_data = blog_list.get(position).getDesc();

        // here ViewHolder is a class that we define ourselves below
        holder.setDescText(desc_data);

        // to get all this data we use the get and set methods from the BlogPost class which we defined previously
        String image_url = blog_list.get(position).getImage_url();
        String thumbUri = blog_list.get(position).getImage_thumb();
        holder.setBlogImage(image_url, thumbUri);

        final String user_id = blog_list.get(position).getUser_id();

        // we go into the firestore database and get all the data under the user_id name from the users collection
        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.isSuccessful()){

                    String userName = task.getResult().getString("name");
                    // String userID = task.getResult().getString("userID");

                    // the user_id associated with the person who posted the image
                    holder.setUserData(userName, user_id);


                } else {

                    //Firebase Exception

                }

            }
        });

        try {

            // the getTime() method is invoked on the Data type class
            long millisecond = blog_list.get(position).getTimestamp().getTime();
            String dateString = DateFormat.format("MM/dd/yyyy", new Date(millisecond)).toString();
            holder.setTime(dateString);

        } catch (Exception e) {

            Toast.makeText(context, "Exception : " + e.getMessage(), Toast.LENGTH_SHORT).show();

        }

        // documentSnapshots is the result if we do retrieve it, and e is the error if we fail to retrieve the right data
        firebaseFirestore.collection("Posts/" + blogPostId + "/Likes").addSnapshotListener( new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                if(!documentSnapshots.isEmpty()){

                    // is the number of likes that we got for this specific blog post
                    int count = documentSnapshots.size();
                    // method to update the number of likes related to our specific blog post
                    holder.updateLikesCount(count);

                } else {

                    holder.updateLikesCount(0);

                }

            }
        });

        // here we are going into the likes collection for the blogpost and we want to check if the current user has put a like on this picture and has
        // left his like there
        firebaseFirestore.collection("Posts/" + blogPostId + "/Likes").document(currentUserId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {

                // in the case when the like indeed has been left on this blog post
                if(documentSnapshot.exists()){

                    // then you change the image representing the like button
                    holder.blogLikeBtn.setImageDrawable(context.getDrawable(R.mipmap.action_like_accent));

                } else {

                    holder.blogLikeBtn.setImageDrawable(context.getDrawable(R.mipmap.action_like_gray));

                }
            }
        });


        holder.blogLikeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                firebaseFirestore.collection("Posts/" + blogPostId + "/Likes").document(currentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        // suppose that we have not yet liked the post, meaning that the result from the task does not exist yet
                        if(!task.getResult().exists()){

                            Map<String, Object> likesMap = new HashMap<>();

                            // here we put the string name associated with the time when the click was placed
                            likesMap.put("timestamp", FieldValue.serverTimestamp());

                            firebaseFirestore.collection("Posts/" + blogPostId + "/Likes").document(currentUserId).set(likesMap);

                        } else {

                            // in this case we previously had like the photo, and now we unlike the photo and hence we now delete the like
                            firebaseFirestore.collection("Posts/" + blogPostId + "/Likes").document(currentUserId).delete();

                        }

                    }
                });
            }
        });

        holder.blogCommentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // when we click on the comment button we are taken to a new activity associated with the commenting action
                Intent commentIntent = new Intent(context, CommentsActivity.class);
                // probably is the extra information we are sending with our intent that is going to be received by the comments class
                commentIntent.putExtra("blog_post_id", blogPostId);
                context.startActivity(commentIntent);

            }
        });

    }

    // counts the number of items in recycler adapter, we want to count the number of items to populate in recycler view
    @Override
    public int getItemCount() {
        return blog_list.size();
    }

    //  ViewHolders belong to the adapter. Adapters should feel free to use their own custom ViewHolder
    //  implementations to store data that makes binding view contents easier. Implementations should
    //  assume that individual item views will hold strong references to ViewHolder objects and that
    //  RecyclerView instances may hold strong references to extra off-screen item views for caching purposes
    public class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;
        private TextView descView;
        private ImageView blogImageView;
        private TextView blogDate;
        private TextView blogUserName;
        private CircleImageView blogUserImage;
        private ImageView blogLikeBtn;
        private TextView blogLikeCount;
        private ImageView blogCommentBtn;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            // assign to these imageviews a part of an XML file
            blogLikeBtn = mView.findViewById(R.id.blog_like_btn);
            blogCommentBtn = mView.findViewById(R.id.blog_comment_icon);
        }

        public void setDescText(String descText){

            descView = mView.findViewById(R.id.blog_desc);
            descView.setText(descText);
        }

        public void setBlogImage(String downloadUri, String thumbUri){

            blogImageView = mView.findViewById(R.id.blog_image);

            // Provides type independent options to customize loads with Glide.
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.image_placeholder);

            // you load your picture into the image_placeholder, and the thumbnail too

            Glide.with(context).applyDefaultRequestOptions(requestOptions).load(downloadUri).thumbnail(
                    Glide.with(context).load(thumbUri)
            ).into(blogImageView);

        }

        public void setTime(String date) {

            blogDate = mView.findViewById(R.id.blog_date);
            blogDate.setText(date);

        }

        public void setUserData(String name, String user_id){

            blogUserImage = mView.findViewById(R.id.blog_user_image);
            blogUserName = mView.findViewById(R.id.blog_user_name);

            blogUserName.setText(name);

            // RequestOptions placeholderOption = new RequestOptions();
            // placeholderOption.placeholder(R.drawable.profile_placeholder);

            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
            StorageReference profile_reference = storageReference.child("profile_images").child(user_id + ".jpg");
            GlideApp.with(context).load(profile_reference).placeholder(R.drawable.default_image).into(blogUserImage);

            //Glide.with(context).applyDefaultRequestOptions(placeholderOption).load(image).into(blogUserImage);

        }

        public void updateLikesCount(int count){

            blogLikeCount = mView.findViewById(R.id.blog_like_count);
            blogLikeCount.setText(count + " Likes");

        }

    }

}
