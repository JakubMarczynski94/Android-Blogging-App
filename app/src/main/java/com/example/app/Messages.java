package com.example.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Messages extends AppCompatActivity {

    // -------------------- beginning of variables for chat --------------

    TextView noUsersText;

    ArrayList<String> al = new ArrayList<>();
    int totalUsers = 0;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    String username_of_key;

    private RecyclerView users_list;
    private MessagesAdapter messagesAdapter;
    private ArrayList<String> usersList;
    // -------------------------------------- end of variables for chat ----------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        auth = FirebaseAuth.getInstance();

        //get current user
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        users_list = (RecyclerView) findViewById(R.id.usersList);
        noUsersText = (TextView) findViewById(R.id.noUsersText);
        // --------------------------------------------------

        users_list.addOnItemTouchListener(
                new RecyclerItemClickListener(getBaseContext(), users_list ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {

                        // chat with the person which was clicked in this array that we are considering, at the right position
                        UserDetails.chatWith = al.get(position);
                        // when clicked we move from this class to the other class, which itself will have a new layout
                        startActivity(new Intent(Messages.this, Chat.class));
                        finish();
                    }

                    @Override public void onLongItemClick(View view, int position) {

                        // chat with the person which was clicked in this array that we are considering, at the right position
                        UserDetails.chatWith = al.get(position);
                        // when clicked we move from this class to the other class, which itself will have a new layout
                        startActivity(new Intent(Messages.this, Chat.class));
                        finish();
                    }
                })
        );
        /*
        users_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            // adapterview where the click happened, the view within the adapterview that was clicked, the position of the view within the adapter, the row id of the item that was clicked
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // chat with the person which was clicked in this array that we are considering, at the right position
                UserDetails.chatWith = al.get(position);
                // when clicked we move from this class to the other class, which itself will have a new layout
                startActivity(new Intent(Messages.this, Chat.class));
            }
        });
        */
        // -------------------------------------------------
        if (user != null) {
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

            RequestQueue rQueue = Volley.newRequestQueue(Messages.this);

            // meaning you add the above request into the queu, which is managed by the volley class
            rQueue.add(request);

            // adapterview extends viewgroup, generally takes in an array as parameter
            // OnItemClickListener is an interface definition for a call back to be invoked when an item in this adapterview has been clicked

        }
    }


// ---------- CHAT PART

        // when you are indeed able to retrieve the right data from the database of the users, we perform the following action

        public void doOnSuccess(String s){

        firestore = FirebaseFirestore.getInstance();

            try {

                // used for client-server communication, stores unordered key-value pairs
                JSONObject obj = new JSONObject(s);

                // in our case the keys must be the uid's
                Iterator i = obj.keys();
                String key = "";

                while (i.hasNext()) {

                    // you transform the Uid to a string
                    key = i.next().toString();
                    String currentUID = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    // then here you should be comparing the key with the current key under consideration, of the current authenticated user
                    if (!key.equals(
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
            if (totalUsers <= 1) {
                noUsersText.setVisibility(View.VISIBLE);
                users_list.setVisibility(View.GONE);
            } else {
                noUsersText.setVisibility(View.GONE);
                users_list.setVisibility(View.VISIBLE);

                // then you have the usersList listview from the beginning and then you set the corresponding adapter
                // used to treat a database, file and transform it into UI material
                // context is the reference of the current class
                // used to set ou the layout for the list items in which you have a text view
                // array of objects used to set the textView
                // usersList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, al));


                //RecyclerView Firebase List
                usersList = al;
                messagesAdapter = new MessagesAdapter(usersList);
                users_list.setHasFixedSize(true);
                users_list.setLayoutManager(new LinearLayoutManager(this));
                users_list.setAdapter(messagesAdapter);

            }

        }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        startActivity(new Intent(Messages.this, Dashboard.class));
        finish();
    }
}

