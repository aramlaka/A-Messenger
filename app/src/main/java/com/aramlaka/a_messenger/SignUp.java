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
import com.google.firebase.auth.UserProfileChangeRequest;

public class SignUp extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    RelativeLayout view;
    EditText name;
    EditText email;
    EditText password;
    Button signUp;
    Button cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    Log.d("debug", "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    Log.d("debug", "onAuthStateChanged:signed_out");
                }
            }
        };

        view = (RelativeLayout) findViewById(R.id.activity_sign_up);
        name = (EditText) view.findViewById(R.id.fullNameEditText);
        email = (EditText) view.findViewById(R.id.emailEditText);
        password = (EditText) view.findViewById(R.id.passwordEditText);
        signUp = (Button) view.findViewById(R.id.signUpButton);
        cancel = (Button) view.findViewById(R.id.cancelButton);

        signUp.setOnClickListener(this);
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
            case R.id.signUpButton:
                signUp();
                break;
        }
    }

    public void signUp() {
        final String emailText = email.getText().toString();
        final String passwordText = password.getText().toString();
        final String displayName = name.getText().toString();

        mAuth.createUserWithEmailAndPassword(emailText, passwordText)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("debug", "createUserWithEmail:onComplete:" + task.isSuccessful());

                        if (!task.isSuccessful()) {
                            Toast.makeText(SignUp.this, "Invalid login details",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            FirebaseUser user = mAuth.getCurrentUser();

                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(displayName)
                                    .build();

                            user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(new OnCompleteListener<Void>()
                                    {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if(!task.isSuccessful())
                                            {
                                                Toast.makeText(SignUp.this, "Authorization Failed",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                            Intent intent = new Intent(SignUp.this, Login.class);
                            intent.putExtra(Login.EMAIL_KEY, emailText);
                            intent.putExtra(Login.PASSWORD_KEY, passwordText);
                            startActivity(intent);
                        }
                    }
                });
    }
}
