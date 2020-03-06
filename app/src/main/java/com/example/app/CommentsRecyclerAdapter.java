package com.example.app;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Date;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import de.hdodenhof.circleimageview.CircleImageView;
import java.util.List;
// comments added

public class CommentsRecyclerAdapter extends RecyclerView.Adapter<CommentsRecyclerAdapter.ViewHolder> {

    public List<Comments> commentsList;
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    public Context context;

    public CommentsRecyclerAdapter(List<Comments> commentsList){

        this.commentsList = commentsList;

    }

    // it returns the viewholder associated to this subclass of recyclerview.adapter
    @Override
    public CommentsRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // inflate this xml layout displaying one comment item
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_list_item, parent, false);
        context = parent.getContext();
        return new CommentsRecyclerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CommentsRecyclerAdapter.ViewHolder holder, int position) {

        holder.setIsRecyclable(false);
        // in this list of comments, we find the comment at a specific position and then use the getMessage
        // method associated to this comment
        String commentMessage = commentsList.get(position).getMessage();
        String userIDOfCommenter = commentsList.get(position).getUser_id();

        // method specified below
        holder.setComment_message(commentMessage);
        holder.setCommentImage(userIDOfCommenter);

        // we go into the firestore database and get all the data under the user_id name from the users collection
        firebaseFirestore.collection("Users").document(userIDOfCommenter).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.isSuccessful()){
                    String userName = task.getResult().getString("name");
                    // String userID = task.getResult().getString("userID");
                    // the user_id associated with the person who posted the image
                    holder.setUserNameOfCommenter(userName);

                } else {}

            }
        });

        try {
            // the getTime() method is invoked on the Data type class
            // the code below works after a while for some reason,
            long millisecond = commentsList.get(position).getTimestamp().getTime();
            String dateString = DateFormat.format("MM/dd/yyyy", new Date(millisecond)).toString();
            holder.setCommentTimeStamp(dateString);

        } catch (Exception e) {

            Toast.makeText(context, "Exception : " + e.getMessage(), Toast.LENGTH_SHORT).show();

        }

    }


    @Override
    public int getItemCount() {

        // is the commentList does not exist and is a null object then you can not apply the size method on it
        if(commentsList != null) {

            return commentsList.size();

        } else {

            return 0;

        }

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        // the necessary visual components of the viewholder, how about the image with the profile picture in it?
        private View mView;
        private TextView comment_message;
        private TextView userNameOfCommenter;
        private CircleImageView userImageOfCommenter;
        private TextView commentTimeStamp;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setComment_message(String message){

            comment_message = mView.findViewById(R.id.comment_message);
            comment_message.setText(message);

        }

        public void setCommentImage(String userIDOfCommenter){

            userImageOfCommenter = mView.findViewById(R.id.comment_image);
            StorageReference profile_reference = storageReference.child("profile_images").child(userIDOfCommenter + ".jpg");
            GlideApp.with(context).load(profile_reference).placeholder(R.drawable.default_image).into(userImageOfCommenter);

        }

        public void setUserNameOfCommenter(String userName){
            userNameOfCommenter = mView.findViewById(R.id.comment_username);
            userNameOfCommenter.setText(userName);
        }

        public void setCommentTimeStamp(String commentDate){
            commentTimeStamp = mView.findViewById(R.id.comment_timestamp);
            commentTimeStamp.setText(commentDate);
        }
    }

}