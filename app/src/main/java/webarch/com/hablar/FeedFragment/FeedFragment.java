package webarch.com.hablar.FeedFragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import webarch.com.hablar.HelperClasses.Feed;
import webarch.com.hablar.HelperClasses.User;
import webarch.com.hablar.MainActivity;
import webarch.com.hablar.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class FeedFragment extends Fragment implements FeedAdapter.OnFeedItemSelected{

    private DatabaseReference mDatabase;
    EditText comment;
    ImageView send;
    MainActivity mainActivity;
    Context context;
    FirebaseUser firebaseUser;
    List<Feed> feedList;
    FeedAdapter feedAdapter;
    RecyclerView recyclerView;
    private HashMap<DatabaseReference, ChildEventListener> mChildListenerMap;
    private HashMap<DatabaseReference, ValueEventListener> mValueListenerMap;
    User currentUser;

    public FeedFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatabase=FirebaseDatabase.getInstance().getReference();
        context=getContext();
        mainActivity=(MainActivity)getActivity();
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        feedList=new ArrayList<>();
        mChildListenerMap=new HashMap<>();
        mValueListenerMap=new HashMap<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_feed, container, false);
        send=(ImageView)view.findViewById(R.id.ivSend);
        comment=(EditText)view.findViewById(R.id.etComment);
        recyclerView = (RecyclerView) view.findViewById(R.id.rv);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(
                        getContext(), LinearLayoutManager.VERTICAL, false
                )
        );
        feedAdapter=new FeedAdapter(getContext(),feedList);
        recyclerView.setAdapter(feedAdapter);
        feedAdapter.SetOnFeedListener(this);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(comment.getText().toString().length()>0)
                    saveComment();
            }
        });
        feedList.clear();
        addUserEventListener();
        addEventListener();
        return view;
    }

    private void saveComment() {
        String emailStr=firebaseUser.getEmail();
        String key=mDatabase.child("feed").child(emailStr.substring(emailStr.indexOf('@')+1).replace(".","_")).push().getKey();
        Feed feed=new Feed();
        feed.setUser(getUser());
        feed.setComment(comment.getText().toString());
        feed.setKey(key);
        mDatabase.child("feed").child(emailStr.substring(emailStr.indexOf('@')+1).replace(".","_")).child(key).setValue(feed);
        comment.setText("");
    }
    public User getUser(){
        User user=new User();
        user.setEmail(firebaseUser.getEmail());
        user.setUsername(firebaseUser.getDisplayName());
        user.setUid(firebaseUser.getUid());
        user.setAvatar(currentUser.getAvatar());
        return user;
    }
    public void addEventListener(){

        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Feed feed = dataSnapshot.getValue(Feed.class);
                feedList.add(0,feed);
                feedAdapter.notifyItemInserted(0);
                recyclerView.scrollToPosition(0);
                Log.d("TEST","childAdded"+dataSnapshot.getKey());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                Feed feed = dataSnapshot.getValue(Feed.class);
                String feedKey = dataSnapshot.getKey();
                for(int i=0;i<feedList.size();i++){
                    if(feedKey.equals(feedList.get(i).getKey())){
                        feedList.set(i,feed);
                        feedAdapter.notifyItemChanged(i);
                        recyclerView.scrollToPosition(i);
                    }
                }

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String feedKey = dataSnapshot.getKey();
                for(int i=0;i<feedList.size();i++){
                    if(feedKey.equals(feedList.get(i).getKey())){
                        feedList.remove(i);
                        feedAdapter.notifyItemRemoved(i);
                        recyclerView.scrollToPosition(i);
                    }
                }
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
        mChildListenerMap.put(mDatabase.child("feed").child(emailStr.substring(emailStr.indexOf('@')+1).replace(".","_")).getRef(), childEventListener);
        mDatabase.child("feed").child(emailStr.substring(emailStr.indexOf('@')+1).replace(".","_")).addChildEventListener(childEventListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        for (Map.Entry<DatabaseReference, ChildEventListener> entry : mChildListenerMap.entrySet()) {
            DatabaseReference ref = entry.getKey();
            ChildEventListener listener = entry.getValue();
            ref.removeEventListener(listener);
        }
        for (Map.Entry<DatabaseReference, ValueEventListener> entry : mValueListenerMap.entrySet()) {
            DatabaseReference ref = entry.getKey();
            ValueEventListener listener = entry.getValue();
            ref.removeEventListener(listener);
        }
    }
    public void addUserEventListener(){
        ValueEventListener valueEventListener=new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user=dataSnapshot.getValue(User.class);
                currentUser=user;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(context,databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        };
        String emailStr=firebaseUser.getEmail();
        mValueListenerMap.put(mDatabase.child("users").child(emailStr.substring(emailStr.indexOf('@')+1).replace(".","_")).child(firebaseUser.getUid()).getRef(), valueEventListener);
        mDatabase.child("users").child(emailStr.substring(emailStr.indexOf('@')+1).replace(".","_")).child(firebaseUser.getUid()).addValueEventListener(valueEventListener);
    }

    @Override
    public void feedSelected(View view, int position) {
        Intent intent=new Intent(getActivity(),FeedDetails.class);
        intent.putExtra("feed_id",feedList.get(position).getKey());
        startActivity(intent);
    }
}
