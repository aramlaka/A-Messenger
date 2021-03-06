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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class SignUpActivity extends AppCompatActivity {

    RelativeLayout view;
    EditText nameEdit;
    EditText emailEdit;
    EditText passwordEdit;
    Button signUpButton;
    Button cancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        view = (RelativeLayout) findViewById(R.id.activity_sign_up);
        nameEdit = (EditText) view.findViewById(R.id.fullNameEditText);
        emailEdit = (EditText) view.findViewById(R.id.emailEditText);
        passwordEdit = (EditText) view.findViewById(R.id.passwordEditText);
        signUpButton = (Button) view.findViewById(R.id.signUpButton);
        cancelButton = (Button) view.findViewById(R.id.cancelButton);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailEdit.getText().toString();
                String password = passwordEdit.getText().toString();
                String name = nameEdit.getText().toString();

                if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)
                        && !TextUtils.isEmpty(name)) {
                    signUp(email, password, name);
                } else {
                    Toast.makeText(SignUpActivity.this, "Invalid text. Please fill in all forms.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void signUp(final String email, final String password, final String displayName) {
        LoginActivity.mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("debug", "createUserWithEmail:onComplete:" + task.isSuccessful());

                        if (!task.isSuccessful()) {
                            Toast.makeText(SignUpActivity.this, "Invalid login details",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            FirebaseUser user = LoginActivity.mAuth.getCurrentUser();

                            //Adds display name to user
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(displayName)
                                    .build();

                            user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(new OnCompleteListener<Void>()
                                    {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if(!task.isSuccessful()) {
                                                Toast.makeText(SignUpActivity.this, "Authorization Failed",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                            intent.putExtra("SignUpActivity", "login");
                            intent.putExtra(LoginActivity.EMAIL_KEY, email);
                            intent.putExtra(LoginActivity.PASSWORD_KEY, password);
                            startActivity(intent);
                        }
                    }
                });
    }
}
