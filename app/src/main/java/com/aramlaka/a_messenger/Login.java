package com.aramlaka.a_messenger;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class Login extends AppCompatActivity implements View.OnClickListener {

    public final static String USER_KEY = "user_key";
    public final static String EMAIL_KEY = "email_key";
    public final static String PASSWORD_KEY = "password_key";
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    RelativeLayout view;
    EditText emailText;
    EditText passwordText;
    Button login;
    Button createAccount;
    String user;
    String email;
    String password;

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
                    startActivity(new Intent(Login.this, ChatRoom.class));
                    finish();
                } else {
                    Log.d("debug", "onAuthStateChanged:signed_out");
                }
            }
        };

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            if (extras.get(USER_KEY) != null ) {
                user = extras.getString(USER_KEY);
            }

            if (extras.get(EMAIL_KEY) != null ) {
                email = extras.getString(EMAIL_KEY);

                if (extras.get(PASSWORD_KEY) != null) {
                    password = extras.getString(PASSWORD_KEY);

                    login(email, password);
                }
            }
        }

        view = (RelativeLayout) findViewById(R.id.activity_login);
        emailText = (EditText) view.findViewById(R.id.emailEditText);
        passwordText = (EditText) view.findViewById(R.id.passwordEditText);
        login = (Button) view.findViewById(R.id.loginButton);
        createAccount = (Button) view.findViewById(R.id.newAccountButton);

        login.setOnClickListener(this);

        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, SignUp.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.loginButton:
                login(emailText.getText().toString(), passwordText.getText().toString());
                break;
        }
    }

    public void login(String emailText, String passwordText) {
        if (emailText == null || passwordText == null) {
            Toast.makeText(Login.this, "Please fill in your login details.",
                    Toast.LENGTH_SHORT).show();
        } else {
            mAuth.signInWithEmailAndPassword(emailText, passwordText)
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
    }
}
