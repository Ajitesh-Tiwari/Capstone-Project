package webarch.com.hablar.LoginController.Fragments;


import android.content.Context;
import android.content.Intent;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import webarch.com.hablar.LoginController.LoginActivity;
import webarch.com.hablar.MainActivity;
import webarch.com.hablar.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignInFragment extends Fragment implements View.OnClickListener {

    EditText email, password;
    Button btnLogin;
    TextView forgotPass;
    LoginActivity loginActivity;
    Context context;
    private FirebaseAuth mAuth;
    int pos=0;


    public SignInFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginActivity=(LoginActivity)getActivity();
        context=getContext();
        //Get Firebase auth instance
        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(context, MainActivity.class));
            loginActivity.finish();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_sign_in, container, false);
        email= (EditText) view.findViewById(R.id.etEmail);
        password=(EditText)view.findViewById(R.id.etPassword);
        btnLogin=(Button)view.findViewById(R.id.btnLogin);
        forgotPass=(TextView)view.findViewById(R.id.tvForgotPass);
        forgotPass.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        setRememberPasswordUI();
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnLogin:
                if(validateForm()) {
                    if(pos==1)
                        loginUser();
                    else if(pos==-1)
                        sendPasswordResetMail();
                }
                break;
            case R.id.tvForgotPass:
                toggle();
                break;
        }

    }
    private void setForgetPasswordUI(){
        pos=-1;
        forgotPass.setText("Remember Password ?");
        password.setVisibility(View.GONE);
        btnLogin.setText("RESET PASSWORD");
    }
    private void setRememberPasswordUI(){
        pos=1;
        forgotPass.setText("Forgot Password ?");
        password.setVisibility(View.VISIBLE);
        btnLogin.setText("LOGIN");
    }
    private void toggle(){
        if(pos==-1)
            setRememberPasswordUI();
        else if(pos==1)
            setForgetPasswordUI();

    }


    private void sendPasswordResetMail() {
        loginActivity.showProgressDialog("Sending Mail");
        String emailStr=email.getText().toString().trim();
        mAuth.sendPasswordResetEmail(emailStr).addOnCompleteListener(loginActivity,new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
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
    private void loginUser(){
        String emailStr=email.getText().toString().trim();
        String passwordStr=password.getText().toString().trim();
        loginActivity.showProgressDialog("Welcome to Hablar");
        mAuth.signInWithEmailAndPassword(emailStr, passwordStr)
                .addOnCompleteListener(loginActivity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        loginActivity.hideProgressDialog();
                        if (!task.isSuccessful()) {
                            // there was an error
                            Toast.makeText(context,task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();

                        } else {
                            FirebaseUser currentUser=FirebaseAuth.getInstance().getCurrentUser();
                            if(currentUser.isEmailVerified()) {
                                Intent intent = new Intent(context, MainActivity.class);
                                startActivity(intent);
                                loginActivity.finish();
                            }
                            else{
                                Toast.makeText(context,"Please verify your email address.",Toast.LENGTH_SHORT).show();
                                FirebaseAuth.getInstance().signOut();
                            }
                        }
                    }
                });
    }

    private boolean validateForm() {
        String emailStr = email.getText().toString().trim();
        String passwordStr=password.getText().toString().trim();
        boolean b=true;

        if (TextUtils.isEmpty(emailStr)) {
            email.setError("Enter Email address");
            b=false;
        }
        if(pos==1){
            if (TextUtils.isEmpty(passwordStr)) {
                password.setError("Enter Password");
                b=false;
            }
        }
        return b;
    }
}
