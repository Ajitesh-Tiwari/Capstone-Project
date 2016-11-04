package webarch.com.hablar.ContactsFragment;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;
import java.util.List;

import webarch.com.hablar.FeedFragment.FeedDetails;
import webarch.com.hablar.HelperClasses.Chat;
import webarch.com.hablar.HelperClasses.ChatAdapter;
import webarch.com.hablar.HelperClasses.Feed;
import webarch.com.hablar.HelperClasses.User;
import webarch.com.hablar.R;

public class ContactDetails extends AppCompatActivity {
    String userID;
    private DatabaseReference mDatabase;
    private FirebaseUser firebaseUser;
    TextView name, email, rollNo;
    ImageView imageView;
    EditText editText;
    Button button;
    User currentUser;
    RecyclerView recyclerView;
    ChatAdapter chatAdapter;
    List<Chat> chats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_details);
        mDatabase= FirebaseDatabase.getInstance().getReference();
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        chats=new ArrayList<>();
        userID=getIntent().getStringExtra("userID");
        name= (TextView) findViewById(R.id.name);
        email=(TextView)findViewById(R.id.email);
        imageView=(ImageView)findViewById(R.id.ivProfile);
        rollNo=(TextView)findViewById(R.id.rollNo);
        editText=(EditText)findViewById(R.id.etMessage);
        button=(Button)findViewById(R.id.btnSend);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editText.getText().toString().length()>0)
                    sendMessage();
            }
        });
        recyclerView= (RecyclerView) findViewById(R.id.rvChat);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(
                        this, LinearLayoutManager.VERTICAL, false
                )
        );
        chatAdapter=new ChatAdapter(this,chats);
        recyclerView.setAdapter(chatAdapter);

        addUserEventListener();
        addEventListener();

    }

    private void sendMessage() {
        String emailStr=firebaseUser.getEmail();
        Feed feed=new Feed();
        feed.setUser(getUser());
        feed.setComment(editText.getText().toString());
        Chat chat=new Chat();
        chat.setMessage(editText.getText().toString());
        chat.setName(firebaseUser.getDisplayName());
        mDatabase.child("messages").child(emailStr.substring(emailStr.indexOf('@')+1).replace(".","_")).child(userID).child(firebaseUser.getUid()).setValue(feed);
        DatabaseReference databaseReference=mDatabase.child("chat").child(emailStr.substring(emailStr.indexOf('@')+1).replace(".","_")).child(getUniqueKey(firebaseUser.getUid(),userID));
        databaseReference.child(databaseReference.push().getKey()).setValue(chat);
        editText.setText("");
    }

    public void addUserEventListener(){
        ValueEventListener valueEventListener=new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user=dataSnapshot.getValue(User.class);
                currentUser=user;
                if(user.getAvatar()!=0)
                    imageView.setImageDrawable(getDrawable("avatar_"+user.getAvatar()));
                name.setText(user.getUsername());
                email.setText(user.getEmail());
                rollNo.setText(user.getRegisterID());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ContactDetails.this,databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        };
        String emailStr=firebaseUser.getEmail();
        mDatabase.child("users").child(emailStr.substring(emailStr.indexOf('@')+1).replace(".","_")).child(userID).addListenerForSingleValueEvent(valueEventListener);
    }
    public Drawable getDrawable(String name) {
        int resourceId = getResources().getIdentifier(name, "drawable", getPackageName());
        return ContextCompat.getDrawable(this,resourceId);
    }
    public User getUser(){
        User user=new User();
        user.setUsername(firebaseUser.getDisplayName());
        user.setUid(firebaseUser.getUid());
        user.setAvatar(currentUser.getAvatar());
        return user;
    }
    public String getUniqueKey(String str1,String str2){
        if(str1.compareTo(str2)>0)
            return str1+str2;
        else
            return str2+str1;
    }
    public void addEventListener(){

        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Chat chat = dataSnapshot.getValue(Chat.class);
                chats.add(0,chat);
                chatAdapter.notifyItemInserted(0);
                recyclerView.scrollToPosition(0);
                //Toast.makeText(ContactDetails.this,dataSnapshot.toString(),Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //Toast.makeText(context, databaseError.getMessage().toString(),
                //Toast.LENGTH_SHORT).show();
            }
        };
        String emailStr=firebaseUser.getEmail();
        mDatabase.child("chat").child(emailStr.substring(emailStr.indexOf('@')+1).replace(".","_")).child(getUniqueKey(firebaseUser.getUid(),userID)).addChildEventListener(childEventListener);
    }
}
