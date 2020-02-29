package com.example.app;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
        }
        // -------------------- END OF CHAT PART ------------------------------------------------------
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

}


