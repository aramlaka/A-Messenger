package com.aramlaka.a_messenger;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.ArrayList;
import java.util.Date;

public class ChatRoom extends AppCompatActivity implements View.OnClickListener, MessageAdapter.SetMessages {

    public static final int SELECT_IMAGE = 1010;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private PrettyTime p;
    private ArrayList<Message> messages;
    private MessageAdapter messageAdapter;
    private Picasso picasso;

    RecyclerView rvChatRoom;
    EditText chatEdit;
    TextView usernameText;
    ImageView logoutButton;
    ImageView sendMessage;
    ImageView pickImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        rvChatRoom = (RecyclerView) findViewById(R.id.rvChatRoom);
        usernameText = (TextView) findViewById(R.id.usernameText);
        logoutButton = (ImageView) findViewById(R.id.logoutButton);
        sendMessage = (ImageView) findViewById(R.id.sendMessageButton);
        chatEdit = (EditText) findViewById(R.id.chatEdit);
        pickImage = (ImageView) findViewById(R.id.sendPictureButton);

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl("gs://inclass11-65ba9.appspot.com/Images/");
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("messages");
        p = new PrettyTime();
        mAuth = FirebaseAuth.getInstance();
        usernameText.setText(mAuth.getCurrentUser().getDisplayName());
        messages = new ArrayList<>();

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                startActivity(new Intent(ChatRoom.this, Login.class));
                finish();
            }
        });

        sendMessage.setOnClickListener(this);
        pickImage.setOnClickListener(this);

        messageAdapter = new MessageAdapter(messages, this, this);
        rvChatRoom.setAdapter(messageAdapter);
        rvChatRoom.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        messageAdapter.notifyDataSetChanged();

        Query getMessages = myRef.orderByChild("date");
        getMessages.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Message message = dataSnapshot.getValue(Message.class);
                message.setKey(dataSnapshot.getKey());
                messages.add(message);
                messageAdapter.notifyDataSetChanged();
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
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sendMessageButton:
                sendMessage(mAuth.getCurrentUser().getUid(), chatEdit.getText().toString(), mAuth.getCurrentUser().getDisplayName());
                break;
            case R.id.sendPictureButton:
                uploadImage();
                break;
        }
    }

    public void sendMessage(String userId, String body, String author) {
        if (body != null && !body.equals(""))
            myRef.push().setValue(new Message(userId, author, body, new Date(System.currentTimeMillis()), null));
    }

    public void uploadImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_IMAGE);
    }


    @Override
    public void setMessages(ArrayList<Message> messages) {

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_IMAGE) {
                Uri selectedImageUri = data.getData();
                UploadTask uploadTask = storageRef.putFile(selectedImageUri);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ChatRoom.this, "Upload Failed!",
                                Toast.LENGTH_SHORT).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(ChatRoom.this, "Upload Success!",
                                Toast.LENGTH_SHORT).show();
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();

                        myRef.push().setValue(new Message(mAuth.getCurrentUser().getUid(), mAuth.getCurrentUser().getDisplayName(),
                                "", new Date(System.currentTimeMillis()), downloadUrl.toString()));
                    }
                });
            }
        }
    }

    public void deleteMessage(Message message) {
        messages.remove(message);
        messageAdapter.notifyDataSetChanged();
        myRef.child(message.getKey()).removeValue();
    }
}
