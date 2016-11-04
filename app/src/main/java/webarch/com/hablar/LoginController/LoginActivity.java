package webarch.com.hablar.LoginController;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;

import webarch.com.hablar.HelperClasses.BaseActivity;
import webarch.com.hablar.LoginController.Fragments.SignInFragment;
import webarch.com.hablar.LoginController.Fragments.SignUpFragment;
import webarch.com.hablar.R;

public class LoginActivity extends BaseActivity implements View.OnClickListener {
    FragmentManager fm ;
    TextView tvStatus;
    int pos=0;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        setContentView(R.layout.activity_login);
        tvStatus=(TextView)findViewById(R.id.statusText);
        setSignInFragment();
        tvStatus.setOnClickListener(this);
    }
    public void setSignInFragment(){
        pos=1;
        tvStatus.setText("Don't have an Account ? SIGN UP");
        fm=getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.containerFragment, new SignInFragment());
        ft.commit();
    }
    public void setSignUpFragment(){
        pos=-1;
        tvStatus.setText("Already have an Account ? SIGN IN");
        fm=getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.containerFragment, new SignUpFragment());
        ft.commit();
    }
    public void toggle(){
        if(pos==1)
            setSignUpFragment();
        else if(pos==-1)
            setSignInFragment();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.statusText:
                toggle();
        }
    }
}
