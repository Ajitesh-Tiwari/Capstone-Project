package webarch.com.hablar.FeedFragment;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import java.util.ArrayList;
import java.util.List;

import webarch.com.hablar.ContactsFragment.ContactDetails;
import webarch.com.hablar.HelperClasses.BaseActivity;
import webarch.com.hablar.HelperClasses.Comment;
import webarch.com.hablar.HelperClasses.Feed;
import webarch.com.hablar.HelperClasses.RecyclerItemClickListener;
import webarch.com.hablar.HelperClasses.User;
import webarch.com.hablar.R;


public class FeedDetails extends BaseActivity {
    String feedID;
    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    TextView name, email, tvFeed;
    ImageView imageView;
    CardView buttonPanel;
    Button comment;
    EditText etComment;
    User user;
    List<Comment> commentList;
    CommentAdapter commentAdapter;
    RecyclerView recyclerView;
    CardView cardProfile;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatabase= FirebaseDatabase.getInstance().getReference();
        currentUser= FirebaseAuth.getInstance().getCurrentUser();
        commentList=new ArrayList<>();
        setContentView(R.layout.activity_feed_details);
        feedID=getIntent().getStringExtra("feed_id");
        name= (TextView) findViewById(R.id.name);
        email=(TextView)findViewById(R.id.email);
        buttonPanel= (CardView) findViewById(R.id.buttonPanel);
        tvFeed=(TextView)findViewById(R.id.tvFeed);
        imageView=(ImageView)findViewById(R.id.ivProfile);
        comment=(Button)findViewById(R.id.btnSend);
        etComment=(EditText)findViewById(R.id.etMessage);
        cardProfile= (CardView) findViewById(R.id.cardProfile);

        comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(etComment.getText().toString().length()>0)
                    sendMessage();
            }
        });
        addEventListener();
        addUserEventListener(currentUser.getUid(),true);
        recyclerView = (RecyclerView) findViewById(R.id.rvComments);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(
                        this, LinearLayoutManager.VERTICAL, false
                )
        );
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (!commentList.get(position).getUserID().equals(currentUser.getUid())) {
                    Intent intent = new Intent(FeedDetails.this, ContactDetails.class);
                    intent.putExtra("userID", commentList.get(position).getUserID());
                    startActivity(intent);
                }
                else
                    Toast.makeText(FeedDetails.this,"This is your comment",Toast.LENGTH_SHORT).show();
            }
        }));
        commentAdapter=new CommentAdapter(this,commentList);
        recyclerView.setAdapter(commentAdapter);
        addCommentEventListener();
    }

    private void sendMessage() {
        String emailStr=currentUser.getEmail();
        Comment comment=new Comment();
        comment.setAvatar(user.getAvatar());
        comment.setName(user.getUsername());
        comment.setComment(etComment.getText().toString());
        comment.setUserID(currentUser.getUid());
        DatabaseReference databaseReference=mDatabase.child("comments").child(emailStr.substring(emailStr.indexOf('@')+1).replace(".","_")).child(feedID);
        databaseReference.child(databaseReference.push().getKey()).setValue(comment);
        etComment.setText("");
    }

    public void addEventListener(){
        ValueEventListener valueEventListener=new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Feed feed=dataSnapshot.getValue(Feed.class);
                User user=feed.getUser();
                addUserEventListener(user.getUid(),false);
                tvFeed.setText(feed.getComment());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(FeedDetails.this,databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        };
        String emailStr=currentUser.getEmail();
        mDatabase.child("feed").child(emailStr.substring(emailStr.indexOf('@')+1).replace(".","_")).child(feedID).addListenerForSingleValueEvent(valueEventListener);
    }
    public Drawable getDrawable(String name) {
        int resourceId = getResources().getIdentifier(name, "drawable", getPackageName());
        return ContextCompat.getDrawable(this,resourceId);
    }
    public void addUserEventListener(final String ID, final boolean b){
        final ValueEventListener valueEventListener=new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(b){
                    user=dataSnapshot.getValue(User.class);
                }
                else {
                    User user = dataSnapshot.getValue(User.class);
                    if (currentUser.getUid().equals(user.getUid())){}
                        //buttonPanel.setVisibility(View.VISIBLE);
                    else{
                        cardProfile.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(FeedDetails.this, ContactDetails.class);
                                intent.putExtra("userID", userID);
                                startActivity(intent);
                            }
                        });
                    }
                    if (user.getAvatar() != 0)
                        imageView.setImageDrawable(getDrawable("avatar_" + user.getAvatar()));
                    name.setText(user.getUsername());
                    email.setText(user.getEmail());
                    userID=user.getUid();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(FeedDetails.this,databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        };
        String emailStr=currentUser.getEmail();
        mDatabase.child("users").child(emailStr.substring(emailStr.indexOf('@')+1).replace(".","_")).child(ID).addListenerForSingleValueEvent(valueEventListener);
    }
    public void addCommentEventListener(){

        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Comment comment = dataSnapshot.getValue(Comment.class);
                commentList.add(0,comment);
                commentAdapter.notifyItemInserted(0);
                recyclerView.scrollToPosition(0);
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
        String emailStr=currentUser.getEmail();
        mDatabase.child("comments").child(emailStr.substring(emailStr.indexOf('@')+1).replace(".","_")).child(feedID).addChildEventListener(childEventListener);
    }
}
