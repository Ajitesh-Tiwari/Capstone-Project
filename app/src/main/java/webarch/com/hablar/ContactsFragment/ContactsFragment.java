package webarch.com.hablar.ContactsFragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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

import webarch.com.hablar.FeedFragment.FeedAdapter;
import webarch.com.hablar.HelperClasses.Feed;
import webarch.com.hablar.HelperClasses.RecyclerItemClickListener;
import webarch.com.hablar.HelperClasses.User;
import webarch.com.hablar.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactsFragment extends Fragment {

    RecyclerView recyclerView;
    ContactsAdapter contactsAdapter;
    List<User> users;
    Context context;
    private DatabaseReference mDatabase;
    FirebaseUser firebaseUser;
    private HashMap<DatabaseReference, ChildEventListener> mListenerMap;



    public ContactsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        users= new ArrayList<>();
        context=getContext();
        mDatabase= FirebaseDatabase.getInstance().getReference();
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        mListenerMap=new HashMap<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);
        recyclerView= (RecyclerView) view.findViewById(R.id.rvContacts);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(
                        getContext(), LinearLayoutManager.VERTICAL, false
                )
        );
        contactsAdapter=new ContactsAdapter(context,users);
        recyclerView.setAdapter(contactsAdapter);
        users.clear();
        addEventListener();
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(context, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        Intent intent=new Intent(context,ContactDetails.class);
                        intent.putExtra("userID",users.get(position).getUid());
                        startActivity(intent);
                    }
                })
        );
        return view;
    }
    public void addEventListener(){

        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                User user = dataSnapshot.getValue(User.class);
                if(!user.getUid().equals(firebaseUser.getUid()))
                    users.add(user);
                contactsAdapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(users.size()-1);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                User user = dataSnapshot.getValue(User.class);
                String feedKey = dataSnapshot.getKey();
                for(int i=0;i<users.size();i++){
                    if(feedKey.equals(users.get(i).getUid())){
                        users.set(i,user);
                        contactsAdapter.notifyItemChanged(i);
                        recyclerView.scrollToPosition(i);
                    }
                }

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String feedKey = dataSnapshot.getKey();
                for(int i=0;i<users.size();i++){
                    if(feedKey.equals(users.get(i).getUid())){
                        users.remove(i);
                        contactsAdapter.notifyItemRemoved(i);
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
        mListenerMap.put(mDatabase.child("users").child(emailStr.substring(emailStr.indexOf('@')+1).replace(".","_")).getRef(), childEventListener);
        mDatabase.child("users").child(emailStr.substring(emailStr.indexOf('@')+1).replace(".","_")).addChildEventListener(childEventListener);
    }
    @Override
    public void onStop() {
        super.onStop();
        for (Map.Entry<DatabaseReference, ChildEventListener> entry : mListenerMap.entrySet()) {
            DatabaseReference ref = entry.getKey();
            ChildEventListener listener = entry.getValue();
            ref.removeEventListener(listener);
        }
    }

}
