package webarch.com.hablar.MessagesFragment;


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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import webarch.com.hablar.ContactsFragment.ContactDetails;
import webarch.com.hablar.FeedFragment.FeedAdapter;
import webarch.com.hablar.HelperClasses.Feed;
import webarch.com.hablar.HelperClasses.RecyclerItemClickListener;
import webarch.com.hablar.MainActivity;
import webarch.com.hablar.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class MessagesFragment extends Fragment {

    private DatabaseReference mDatabase;
    FirebaseUser firebaseUser;
    List<Feed> feedList;
    FeedAdapter feedAdapter;
    RecyclerView recyclerView;
    private HashMap<DatabaseReference, ChildEventListener> mChildListenerMap;
    Context context;
    MainActivity mainActivity;


    public MessagesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatabase= FirebaseDatabase.getInstance().getReference();
        context=getContext();
        mainActivity=(MainActivity)getActivity();
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        feedList=new ArrayList<>();
        mChildListenerMap=new HashMap<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_messages, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.rvMessages);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(
                        getContext(), LinearLayoutManager.VERTICAL, false
                )
        );
        feedAdapter=new FeedAdapter(getContext(),feedList);
        recyclerView.setAdapter(feedAdapter);
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(context, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent=new Intent(context,ContactDetails.class);
                intent.putExtra("userID",feedList.get(position).getUser().getUid());
                startActivity(intent);
            }
        }));
        feedList.clear();
        addEventListener();
        return view;
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
                    if(feedKey.equals(feedList.get(i).getUser().getUid())){
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
                    if(feedKey.equals(feedList.get(i).getUser().getUid())){
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
        mDatabase.child("messages").child(emailStr.substring(emailStr.indexOf('@')+1).replace(".","_")).child(firebaseUser.getUid()).addChildEventListener(childEventListener);
    }
    @Override
    public void onStop() {
        super.onStop();
        for (Map.Entry<DatabaseReference, ChildEventListener> entry : mChildListenerMap.entrySet()) {
            DatabaseReference ref = entry.getKey();
            ChildEventListener listener = entry.getValue();
            ref.removeEventListener(listener);
        }
    }

}
