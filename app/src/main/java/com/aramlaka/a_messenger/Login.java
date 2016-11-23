package com.aramlaka.a_messenger;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {

    public static FirebaseAuth mAuth;
    public static FirebaseAuth.AuthStateListener mAuthListener;

    public final static String USER_KEY = "user_key";
    public final static String EMAIL_KEY = "email_key";
    public final static String PASSWORD_KEY = "password_key";

    RelativeLayout view;
    EditText emailEdit;
    EditText passwordEdit;
    Button loginButton;
    Button createAccountButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    Log.d("debug", "onAuthStateChanged:signed_in:" + user.getUid());
                    mAuth.signOut();
                    //startActivity(new Intent(Login.this, ChatRoom.class));
                    //finish();
                } else {
                    Log.d("debug", "onAuthStateChanged:signed_out");
                }
            }
        };

        view = (RelativeLayout) findViewById(R.id.activity_login);
        emailEdit = (EditText) view.findViewById(R.id.emailEditText);
        passwordEdit = (EditText) view.findViewById(R.id.passwordEditText);
        loginButton = (Button) view.findViewById(R.id.loginButton);
        createAccountButton = (Button) view.findViewById(R.id.newAccountButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailEdit.getText().toString();
                String password = passwordEdit.getText().toString();

                if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
                    login(email, password);
                } else {
                    Toast.makeText(Login.this, "Invalid text. Please fill in all forms.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, SignUp.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    public void login(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("debug", "signInWithEmail:onComplete:" + task.isSuccessful());

                        if (!task.isSuccessful()) {
                            Log.w("debug", "signInWithEmail:failed", task.getException());
                            Toast.makeText(Login.this, "Sign in Failed!",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(Login.this, "Sign in Success!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if(intent.getStringExtra("SignUp").equals("login")){
            String user = intent.getStringExtra(USER_KEY);
            String password = intent.getStringExtra(PASSWORD_KEY);

            login(user, password);
        }
    }
}
