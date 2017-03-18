package joinus.joinus.com.joinus.Activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.firebase.client.Firebase;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import android.support.design.widget.CollapsingToolbarLayout;
import joinus.joinus.com.joinus.Class.GoogleSingleTon;
import joinus.joinus.com.joinus.Class.Management;
import joinus.joinus.com.joinus.Fragment.RegisterFragment;
import joinus.joinus.com.joinus.R;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,View.OnClickListener {
    private static final String TAG = "MainActivity";
    private static final int RC_SIGN_IN = 9001;
    private GoogleSingleTon single = new GoogleSingleTon();
    private ProgressDialog mProgressDialog;
    private CallbackManager callbackManager = null;
    private AccessTokenTracker mtracker = null;
    private ProfileTracker mprofileTracker = null;
    private LoginButton loginButton;
    private Button btnJoin;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private Firebase mfireBase;

    //Make a call back Facebook.
    FacebookCallback<LoginResult> callback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            facebookBundle(Profile.getCurrentProfile());
        }

        @Override
        public void onCancel() {}

        @Override
        public void onError(FacebookException error) {}
    };

    //  Create callback to facebook and start tracking token\profile changes , create GoogleApiClient.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        mtracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                Log.v("AccessTokenTracker", "oldAccessToken=" + oldAccessToken + "||" + "CurrentAccessToken" + currentAccessToken);
            }
        };

        mprofileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                Log.v("Session Tracker", "oldProfile=" + oldProfile + "||" + "currentProfile" + currentProfile);
                facebookBundle(currentProfile);  //If profile changed reconnect to app.
            }
        };

        mtracker.startTracking();
        mprofileTracker.startTracking();
        setContentView(R.layout.activity_main);

        //initialize google api.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */).addApi(Auth.GOOGLE_SIGN_IN_API, gso).addApi(LocationServices.API).build();
        single = GoogleSingleTon.getInstance(mGoogleApiClient);

        //initialize buttons.
        SignInButton signIn_btn = (SignInButton) findViewById(R.id.plus_one_button);
        signIn_btn.setSize(SignInButton.SIZE_STANDARD);
        signIn_btn.setOnClickListener(this);
        signIn_btn.setScopes(gso.getScopeArray());

        btnJoin = (Button) findViewById(R.id.btnJoin);
        btnJoin.setOnClickListener(this);

        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("user_friends","email","public_profile");

        //Check if connected using email and password(Manager).
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    sucFire();
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());

                }
            }
        };
    }

    //If log in using email and password(Manager).
    public void sucFire(){
        Bundle GBundle = new Bundle();
        GBundle.putString("Man_con",user.getEmail().toString());
        Intent intent = new Intent(this, OptionAct.class);
        intent.putExtra("profile", GBundle);
        opAct(intent);
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(single.get_GoogleApiClient());

        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else{
            // If the user has not previously signed in on this device or the sign-in has expired,
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }

    //Handle the result from google/facebook sing in.
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // if = Result returned from GoogleSignInApi.getSignInIntent   else = Result from facebook sing in.
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }else{
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    //Set google profile to bundle => activate opAct
    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();
            Bundle GBundle = new Bundle();
            GBundle.putParcelable("Acc_key", acct);
            Intent intent = new Intent(this, OptionAct.class);
            intent.putExtra("profile", GBundle);
            opAct(intent);
        }
    }

    //Activate optionAct
    private void opAct(Intent intent){
            startActivity(intent);
            changeUI(true);
            finish();
    }

    //Set facebook profile to bundle => activate opAct
    private void facebookBundle(Profile profile) {
        if (profile != null) {
                Bundle mBundle = new Bundle();
                mBundle.putParcelable("facebook_key", profile);
                Intent intent = new Intent(this, OptionAct.class);
                intent.putExtra("profile", mBundle);
                opAct(intent);
        }
    }

    //Activate sing in intent.
    protected void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(single.get_GoogleApiClient());
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    //An revoke error has occurred, show button again.
    private void revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(single.get_GoogleApiClient()).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        changeUI(false);
                    }
                });
    }

    // An unresolvable error has occurred and Google APIs (including Sign-In) will not be available.
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    //click on google sing in or manager button
    @Override
    public void onClick(View v) {
        if (v.getId() ==  R.id.plus_one_button) {
            signIn();
        } else if(v.getId() == R.id.btnJoin){
            Bundle GBundle = new Bundle();
            GBundle.putString("Man_key", "Manager");
            Intent intent = new Intent(this, OptionAct.class);
            intent.putExtra("profile", GBundle);
            opAct(intent);
        }
    }

    //Change button visibility.
    public void changeUI(boolean signedIn) {
        if (signedIn) {
            this.findViewById(R.id.plus_one_button).setVisibility(View.GONE);
            loginButton.setVisibility(View.GONE);
            btnJoin.setVisibility(View.GONE);
        } else {
            this.findViewById(R.id.plus_one_button).setVisibility(View.VISIBLE);
            loginButton.setVisibility(View.VISIBLE);
            btnJoin.setVisibility(View.VISIBLE);
        }
    }

    //When App stop-> stop tracking facebook.
    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null)
            mAuth.removeAuthStateListener(mAuthListener);

        mtracker.stopTracking();
        mprofileTracker.stopTracking();
    }

    //Check access token if exist return true, else false.
    public boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }

    //Check when resume if the app remember facebook user or google.
    @Override
    public void onResume() {
        super.onResume();
        if (isLoggedIn())
            facebookBundle(Profile.getCurrentProfile());
    }

/*    public void createAccount(Management man, String password) {
        final Management manager = man;
        mfireBase = new Firebase("https://joinus-2fc04.firebaseio.com/Users/Business");
        mAuth.createUserWithEmailAndPassword(man.getId(), password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                toast("createUserWithEmail:onComplete:" + task.isSuccessful());

                if (!task.isSuccessful()) {
                    toast("שם משתמש או סימסא לא נכונים");
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    RegisterFragment fragment = new RegisterFragment();
                    fragmentTransaction.add(R.id.activity_option, fragment);
                    changeUI(false);
                    fragmentTransaction.commit();
                }
                else {
                    mfireBase = new Firebase("https://joinus-2fc04.firebaseio.com/Users/Business");
                    mfireBase = mfireBase.child(mAuth.getCurrentUser().getUid().toString());
                    mfireBase.child("Email").setValue(manager.getId());
                    mfireBase.child("Busineesline").setValue(manager.getBusinessLine());
                    mfireBase.child("Busineesname").setValue(manager.getName());
                    mfireBase.child("Managername").setValue(manager.getManagerName());
                    mfireBase.child("ManagerPhone").setValue(manager.getMangerPhone());
                    mfireBase.child("Address").setValue(manager.getAddress());
                    mfireBase.child("BusinessPhone").setValue(manager.getMangerPhone());
                    mfireBase.child("numberOfemployees").setValue(manager.getNumberOfemployees());




                }

            }


        });

    }
    private void sendVerificationEmail()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // email sent
                            toast("Email was sent");

                            // after email is sent just logout the user and finish this activity
                            FirebaseAuth.getInstance().signOut();
                            //startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                            finish();
                        }
                        else
                        {
                            // email not sent, so display message and restart the activity or do whatever you wish to do
                            toast("Failed to send Email");
                            //restart this activity
                            overridePendingTransition(0, 0);
                            finish();
                            overridePendingTransition(0, 0);
                            startActivity(getIntent());

                        }
                    }
                });
    }*/
    private void toast(String text){
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }
}
