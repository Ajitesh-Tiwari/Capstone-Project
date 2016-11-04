package webarch.com.hablar.ProfileFragment;


import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.Map;

import webarch.com.hablar.HelperClasses.BaseActivity;
import webarch.com.hablar.HelperClasses.User;
import webarch.com.hablar.LoginController.LoginActivity;
import webarch.com.hablar.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment implements View.OnClickListener, AvatarsAdapter.OnAvatarSelected{

    TextView name, email;
    Context context;
    BaseActivity baseActivity;
    Button logout, delete;
    RecyclerView recyclerViewAvatars;
    AvatarsAdapter avatarsAdapter;
    ImageView ivProfile;
    CardView cardView;
    private DatabaseReference mDatabase;
    FirebaseUser currentUser;
    private HashMap<DatabaseReference, ValueEventListener> mListenerMap;


    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=getContext();
        mDatabase= FirebaseDatabase.getInstance().getReference();
        baseActivity=(BaseActivity) getActivity();
        mListenerMap=new HashMap<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_profile, container, false);
        name=(TextView)view.findViewById(R.id.name);
        email=(TextView)view.findViewById(R.id.email);
        logout=(Button) view.findViewById(R.id.logout);
        delete=(Button)view.findViewById(R.id.delete);
        ivProfile=(ImageView)view.findViewById(R.id.ivProfile);
        ivProfile.setOnClickListener(this);
        cardView=(CardView)view.findViewById(R.id.cardViewAvatars);
        recyclerViewAvatars=(RecyclerView)view.findViewById(R.id.avatars);
        recyclerViewAvatars.setHasFixedSize(true);
        recyclerViewAvatars.setLayoutManager(new LinearLayoutManager(
                getContext(), LinearLayoutManager.HORIZONTAL, false
        ));
        avatarsAdapter=new AvatarsAdapter(getContext());
        avatarsAdapter.SetOnAvatarListener(this);
        recyclerViewAvatars.setAdapter(avatarsAdapter);
        logout.setOnClickListener(this);
        delete.setOnClickListener(this);
        setData();
        addEventListener();
        return view;
    }
    
    

    private void setData() {
        baseActivity.showProgressDialog("Loading your Profile...");
        currentUser= FirebaseAuth.getInstance().getCurrentUser();
        name.setText(currentUser.getDisplayName());
        email.setText(currentUser.getEmail());
        baseActivity.hideProgressDialog();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.logout:
                logout();
                break;
            case R.id.delete:
                deleteAccount();
                break;
            case R.id.ivProfile:
                cardView.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void deleteAccount() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(context, LoginActivity.class));
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(context, LoginActivity.class));
    }

    @Override
    public void avatarSelected(View view, int position) {
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/avatar/", position+1);
        String emailStr=currentUser.getEmail();
        mDatabase.child("users").child(emailStr.substring(emailStr.indexOf('@')+1).replace(".","_")).child(currentUser.getUid()).updateChildren(childUpdates);
        cardView.setVisibility(View.GONE);
    }
    public void addEventListener(){
        ValueEventListener valueEventListener=new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user=dataSnapshot.getValue(User.class);
                if(user.getAvatar()!=0)
                    ivProfile.setImageDrawable(getDrawable("avatar_"+user.getAvatar()));
                else if(user.getAvatar()==0)
                    cardView.setVisibility(View.VISIBLE);
                if(!dataSnapshot.hasChild("fcmid"))
                    sendRegistrationToServer(FirebaseInstanceId.getInstance().getToken());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(context,databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        };
        String emailStr=currentUser.getEmail();
        mListenerMap.put(mDatabase.child("users").child(emailStr.substring(emailStr.indexOf('@')+1).replace(".","_")).child(currentUser.getUid()).getRef(), valueEventListener);
        mDatabase.child("users").child(emailStr.substring(emailStr.indexOf('@')+1).replace(".","_")).child(currentUser.getUid()).addValueEventListener(valueEventListener);
    }
    public Drawable getDrawable(String name) {
        int resourceId = context.getResources().getIdentifier(name, "drawable", context.getPackageName());
        return ContextCompat.getDrawable(context,resourceId);
    }
    @Override
    public void onStop() {
        super.onStop();
        for (Map.Entry<DatabaseReference, ValueEventListener> entry : mListenerMap.entrySet()) {
            DatabaseReference ref = entry.getKey();
            ValueEventListener listener = entry.getValue();
            ref.removeEventListener(listener);
        }
    }
    public void sendRegistrationToServer(String token){
        mDatabase = FirebaseDatabase.getInstance().getReference();
        String emailStr=currentUser.getEmail();
        mDatabase.child("users").child(emailStr.substring(emailStr.indexOf('@')+1).replace(".","_")).child(currentUser.getUid()).child("fcmid").setValue(token);
    }
}
