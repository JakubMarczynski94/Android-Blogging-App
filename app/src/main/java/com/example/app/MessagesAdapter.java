package com.example.app;

import android.content.Context;
import android.text.Layout;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import android.app.Activity;
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
import de.hdodenhof.circleimageview.CircleImageView;


public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder> {

    private ArrayList<String> contacts_id;
    private Context context;
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference();

    public MessagesAdapter(ArrayList<String> contacts_id) {
        this.contacts_id = contacts_id;
    }


    @Override
    public MessagesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // inflate this xml layout displaying one comment item
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.choose_chat_user_list_item, parent, false);
        context = parent.getContext();
        return new MessagesAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MessagesAdapter.ViewHolder holder, int position) {

        holder.setIsRecyclable(false);
        // in this list of comments, we find the comment at a specific position and then use the getMessage
        // method associated to this comment
        String idOfContact = contacts_id.get(position);

        // method specified below
        holder.setContactImage(idOfContact);

        // we go into the firestore database and get all the data under the user_id name from the users collection
        firebaseFirestore.collection("Users").document(idOfContact).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {
                    String name = task.getResult().getString("name");
                    // String userID = task.getResult().getString("userID");
                    // the user_id associated with the person who posted the image
                    holder.setContactName(name);

                } else {
                }

            }
        });

    }

    @Override
    public int getItemCount() {

        // is the commentList does not exist and is a null object then you can not apply the size method on it
        if(contacts_id != null) {

            return contacts_id.size();

        } else {

            return 0;

        }

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        // the necessary visual components of the viewholder, how about the image with the profile picture in it?
        private View mView;
        private TextView nameOfContact;
        private CircleImageView imageOfContact;
        private TextView nbUnreadMessages;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setContactName(String name) {

            nameOfContact = mView.findViewById(R.id.choose_chat_user_username);
            nameOfContact.setText(name);

        }

        public void setContactImage(String idOfContact) {

            imageOfContact = mView.findViewById(R.id.choose_chat_user_image);
            StorageReference profile_reference = storageReference.child("profile_images").child(idOfContact + ".jpg");
            GlideApp.with(context).load(profile_reference).placeholder(R.drawable.default_image).into(imageOfContact);

        }

    }

}
