package webarch.com.hablar.LoginController.Fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import webarch.com.hablar.HelperClasses.User;
import webarch.com.hablar.LoginController.LoginActivity;
import webarch.com.hablar.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignUpFragment extends Fragment implements View.OnClickListener {

    EditText username, regID, email, password;
    Button btnRegister;
    LoginActivity loginActivity;
    Context context;
    String TAG="Hablar";
    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]


    private DatabaseReference mDatabase;
    public SignUpFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginActivity=(LoginActivity)getActivity();
        context=getContext();
        mAuth = FirebaseAuth.getInstance();
        mDatabase=FirebaseDatabase.getInstance().getReference();
        if (mAuth.getCurrentUser() != null) {
            mAuth.signOut();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_sign_up,container,false);
        username=(EditText)view.findViewById(R.id.etUsername);
        regID=(EditText)view.findViewById(R.id.etRegId);
        email=(EditText)view.findViewById(R.id.etEmail);
        password=(EditText)view.findViewById(R.id.etPassword);
        btnRegister=(Button)view.findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(this);

        return view;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnRegister:
                if(validateForm())
                    checkDomain();
                break;
        }
    }

    private void checkDomain() {
        final String emailStr = email.getText().toString().trim();
        mDatabase.child("organizations").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(emailStr.substring(emailStr.indexOf('@')+1).replace(".","_"))){
                    createUser();
                }
                else{
                    Toast.makeText(context,"Not possible",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(context,databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createUser() {
        String emailStr = email.getText().toString().trim();
        String passwd=password.getText().toString().trim();
        final String usernameStr=username.getText().toString().trim();
        loginActivity.showProgressDialog("Registering User");
        mAuth.createUserWithEmailAndPassword(emailStr, passwd)
                .addOnCompleteListener(loginActivity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //mAuth.signOut();
                        if (!task.isSuccessful()) {
                            Toast.makeText(context,task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                            loginActivity.hideProgressDialog();
                        }
                        else if(task.isSuccessful()){

                            final FirebaseUser user = task.getResult().getUser();

                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(usernameStr)
                                    .build();

                            user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                sendVerificationMail(user);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }
    private void saveToDatabase(FirebaseUser firebaseUser){
        String registerStr=regID.getText().toString().trim();
        String emailStr = email.getText().toString().trim();
        User user=new User(firebaseUser.getUid(),username.getText().toString().trim(),registerStr,firebaseUser.getEmail());
        user.setAvatar(0);
        if(FirebaseInstanceId.getInstance().getToken()!=null)
            user.setFcmid(FirebaseInstanceId.getInstance().getToken());
        mDatabase.child("users").child(emailStr.substring(emailStr.indexOf('@')+1).replace(".","_")).child(firebaseUser.getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                mAuth.signOut();
                loginActivity.hideProgressDialog();
                if (!task.isSuccessful()) {
                    Toast.makeText(context,task.getException().getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
                else if(task.isSuccessful()){
                    Toast.makeText(context,"Please check your Mailbox.",Toast.LENGTH_SHORT).show();
                    loginActivity.setSignInFragment();
                }
            }
        });
    }

    private void sendVerificationMail(final FirebaseUser user) {
        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (!task.isSuccessful()) {
                    loginActivity.hideProgressDialog();
                    Toast.makeText(context,task.getException().getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
                else if(task.isSuccessful()){
                    saveToDatabase(user);
                }
            }
        });
    }

    private boolean validateForm() {
        final String emailStr = email.getText().toString().trim();
        String usernameStr=username.getText().toString().trim();
        String registerStr=regID.getText().toString().trim();
        String passwd=password.getText().toString().trim();
        boolean b=true;

        if (TextUtils.isEmpty(emailStr)) {
            email.setError("Enter Email address");
            b=false;
        }
        else if(!emailStr.contains("@")){
            email.setError("Enter valid Email address");
            b=false;
        }
        if(passwd.length()<8){
            password.setError("Weak Password");
        }
        if (TextUtils.isEmpty(usernameStr)) {
            username.setError("Enter Username");
            b=false;
        }
        if (TextUtils.isEmpty(registerStr)) {
            regID.setError("Enter Registration ID");
            b=false;
        }
        return b;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}
