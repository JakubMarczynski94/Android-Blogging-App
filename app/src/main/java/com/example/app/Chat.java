package com.example.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class Chat extends AppCompatActivity {

    LinearLayout layout;
    ImageView sendButton;
    EditText messageArea;
    ScrollView scrollView;
    Firebase reference1, reference2;
    String correspondent;
    TextView correspondent_name;
    CircleImageView correspondent_image;
    String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        layout = (LinearLayout)findViewById(R.id.layout1);
        sendButton = (ImageView)findViewById(R.id.sendButton);
        messageArea = (EditText)findViewById(R.id.messageArea);
        scrollView = (ScrollView)findViewById(R.id.scrollView);
        correspondent_name = (TextView) findViewById(R.id.correspondent_name);
        correspondent_image = (CircleImageView) findViewById(R.id.correspondent_image);

        StorageReference profile_reference = storageReference.child("profile_images").child(UserDetails.chatWith + ".jpg");
        GlideApp.with(getBaseContext()).load(profile_reference).placeholder(R.drawable.default_image).into(correspondent_image);

        Firebase.setAndroidContext(this);

        // maybe the userDetaisl.email doesn't appear below because firebase doesn't allow symbols like @ to be in the name? The underscore appears though
        reference1 = new Firebase("https://appdata-67dc1.firebaseio.com/messages/" + user_id + "_" + UserDetails.chatWith);
        reference2 = new Firebase("https://appdata-67dc1.firebaseio.com/messages/" + UserDetails.chatWith + "_" + user_id);

        // we go into the firestore database and get all the data under the user_id name from the users collection
        firebaseFirestore.collection("Users").document(UserDetails.chatWith).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {

                    correspondent = task.getResult().getString("name");
                    correspondent_name.setText(correspondent);

                } else {
                    Toast.makeText(Chat.this, "Wasn't able to access name of correspondent", Toast.LENGTH_SHORT).show();
                }
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = messageArea.getText().toString();

                if(!messageText.equals("")){

                    // if the message we want to set is not null, then we create a new map which will assign a string to a string
                    Map<String, String> map = new HashMap<String, String>();
                    // you link the message and the user who sent it, and then you push it into the two references for the two users
                    // for some reason here and above the userdetails.email is not added into the map. Explains inconsistencies.
                    map.put("messages", messageText);
                    map.put("users", user_id);

                    // the problem is that user_details is actually not accessed for some reason but chat with is
                    //map.put("users", "s");

                    reference1.push().setValue(map);
                    reference2.push().setValue(map);
                }
            }
        });

        // specific to one of the databases for one way communication
        reference1.addChildEventListener(new ChildEventListener() {

            @Override
            // method triggered when child added to the location where the listener was previously added
            // the string specifies the previous child name
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                // when you read firebase data you receive the data as a data snapshot
                // getvalue returns the data from datasnapshot, indicate the type of generic collections you want back
                Map map = dataSnapshot.getValue(Map.class);
                String message = map.get("messages").toString();
                String userName = map.get("users").toString();

                // since child contains one user and one message therefore here we can compare directly
                if(userName.equals(user_id)){
                    addMessageBox(message, 1);
                }
                else{
                    addMessageBox(message, 2);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    // making the message appear in the message box
    public void addMessageBox(String message, int type){
        TextView textView = new TextView(Chat.this);
        textView.setText(message);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, 0, 10);
        textView.setLayoutParams(lp);

        if(type == 1) {
            textView.setBackgroundResource(R.drawable.rounded_corner1);
        }
        else{
            textView.setBackgroundResource(R.drawable.rounded_corner2);
        }

        layout.addView(textView);
        scrollView.fullScroll(View.FOCUS_DOWN);
    }
}