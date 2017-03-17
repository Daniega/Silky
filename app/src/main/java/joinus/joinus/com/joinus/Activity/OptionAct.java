package joinus.joinus.com.joinus.Activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseError;


import java.io.Console;
import java.util.Map;

import joinus.joinus.com.joinus.Class.GoogleSingleTon;
import joinus.joinus.com.joinus.Class.Management;
import joinus.joinus.com.joinus.Class.User;
import joinus.joinus.com.joinus.Fragment.RegisterFragment;
import joinus.joinus.com.joinus.R;
import com.google.firebase.database.IgnoreExtraProperties;
@IgnoreExtraProperties public class OptionAct extends FragmentActivity implements View.OnClickListener {
    private Firebase mfireBase;
    private Profile profile;
    private GoogleSingleTon single = new GoogleSingleTon();
    private GoogleSignInAccount googleAcc;
    private User user = null;
    private EditText etPassword,etEmail;
    private FirebaseAuth mAuth;
    private String email = null;
    private Management man = new Management();
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option);
        single = GoogleSingleTon.getInstance(null);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    // NOTE: this Activity should get onpen only when the user is not signed in, otherwise
                    // the user will receive another verification email.
                    if(!user.isEmailVerified()) {
                        //toast(mAuth.getCurrentUser().getDisplayName().toString()+" Please verify your email and come back");
                        sendVerificationEmail();
                    }
                } else {
                    // User is signed out

                }
                // ...
            }
        };
        startConne();
    }
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }
    public void onStop() {
        super.onStop();
        if (mAuthListener != null)
            mAuth.removeAuthStateListener(mAuthListener);

    }

    public void startConne(){
        Bundle b = getIntent().getBundleExtra("profile");
        single.locationOn(this);

        if(b != null && b.getParcelable("facebook_key") != null) {
            profile =  (Profile)b.getParcelable("facebook_key");
            mfireBase = new Firebase("https://joinus-2fc04.firebaseio.com/Users/Facebook");
            toast(profile.getId().toString());
            getData();
//            toast(user.getName().toString());
        } else if(b != null && b.getParcelable("Acc_key") != null ) {
            googleAcc =  (GoogleSignInAccount)b.getParcelable("Acc_key");
            mfireBase = new Firebase("https://joinus-2fc04.firebaseio.com/Users/Google");
            getData();
        }else if(b != null && b.getString("Man_con") != null){
            email = b.getString("Man_con");
            mfireBase = new Firebase("https://joinus-2fc04.firebaseio.com/Users/Business/" );
            getData();
        }

        etPassword = (EditText)findViewById(R.id.tbPassword);
        etEmail = (EditText)findViewById(R.id.tbEmail);

        Button btnLogIn = (Button) findViewById(R.id.btnLogIn);
        btnLogIn.setOnClickListener(this);

        Button btnSignIn = (Button) findViewById(R.id.btnSignIn);
        btnSignIn.setOnClickListener(this);

        Button logOut = (Button) findViewById((R.id.btnSignOut));
        logOut.setOnClickListener(this);
    }
    //fetch users data
    private void getData() {
        mfireBase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Map<String, String> users = dataSnapshot.getValue(Map.class);
                    if (users != null) {
                        for (String key : users.keySet()) {
                            if (profile != null && key.equals(profile.getId())) {
                                user = new User(profile.getFirstName() + " " + profile.getLastName(), profile.getId(), profile.getProfilePictureUri(60, 60).toString());
                            }
                            else if (googleAcc != null && key.equals(googleAcc.getId())) {
                                user = new User(googleAcc.getGivenName() + " " + googleAcc.getFamilyName(), googleAcc.getId(), googleAcc.getPhotoUrl().toString());
                            }
                            else if (key.equals(mAuth.getCurrentUser().getUid())) {// fetch business user details, still to do
                                    //man(dataSnapshot.getValue())
                                    dataSnapshot=dataSnapshot.child(key.toString());
                                    man = dataSnapshot.getValue(Management.class);
                                    toast("Welcome "+man.getBusinessname());

                            }
        /*                        //getAllData
                                Firebase ref = new Firebase("https://joinus-2fc04.firebaseio.com/Users/Business");
                                ref.addValueEventListener(new ValueEventListener() {

                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot){
                                        for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                                            man = postSnapshot.getValue(Management.class);
                                           System.out.println(man.getName());}
                                        *//*man.setBusinessLine(dataSnapshot.child("Businessline").getValue().toString());
                                        man.setId(dataSnapshot.child("Email").getValue().toString());
                                        man.setMangerPhone(dataSnapshot.child("ManagerPhone").getValue().toString());
                                        man.setAddress(dataSnapshot.child("Address").getValue().toString());
                                        man.setManagerName(dataSnapshot.child("Managername").getValue().toString());
                                        man.setBusinessPhone(dataSnapshot.child("BusinessPhone").getValue().toString());
                                        man.setName(dataSnapshot.child("Businessname").getValue().toString());
                                        man.setNumberOfemployees(dataSnapshot.child("numberOfemployees").getValue().toString());*//*

                                    }


                                    @Override
                                    public void onCancelled(FirebaseError firebaseError) {

                                    }


                                });*/
                            }
                        }
                    }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }

            //if (user == null)
                       // addUser();

            });

    }

    private void addUser() {
        if (googleAcc != null) {
            user = new User(googleAcc.getGivenName() + " " + googleAcc.getFamilyName(), googleAcc.getId(),googleAcc.getPhotoUrl().toString());
        } else if (profile != null) {
            user = new User(profile.getFirstName() + " " + profile.getLastName(), profile.getId(), profile.getProfilePictureUri(60,60).toString());
        }

        mfireBase = mfireBase.child(user.getId());
        mfireBase.child("Name").setValue(user.getName());
        mfireBase.child("ProfilePhoto").setValue(user.getPersonPhotoUrl());
    }

    public void onClick(View v) {  //click on google sing in.
        if (v.getId() == R.id.btnSignOut) {
            single.get_GoogleApiClient().connect();
            single.get_GoogleApiClient().registerConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                @Override
                public void onConnected(@Nullable Bundle bundle) {
                    if(single.get_GoogleApiClient().isConnected())
                        single.logOut();
                    LoginManager.getInstance().logOut();
                    FirebaseAuth.getInstance().signOut();
                    acti();

                }

                @Override
                public void onConnectionSuspended(int i) {}
            });
        }

        if (v.getId() == R.id.btnLogIn) {
            final String password = etPassword.getText().toString();
            email = etEmail.getText().toString();

            if (TextUtils.isEmpty(password) || TextUtils.isEmpty(email)) {
                toast("שם משתמש או סיסמא ריקים");
            }
            else {
                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            toast("שם משתמש או סימסא לא נכונים");

                        }else{
                            signIn(email,password);

                           // mfireBase=mfireBase.child("Users").child("Business");
                            mfireBase = new Firebase("https://joinus-2fc04.firebaseio.com/Users/Business");
                            getData();
                           // mfireBase=mfireBase.child("1");
                            //mfireBase = new Firebase("https://joinus-2fc04.firebaseio.com/Users/Business");
                            //mfireBase.child("1").removeValue();
                            //toast(man.getName());
                        }
                    }
                });
            }
        }

        if(v.getId() == R.id.btnSignIn){
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            RegisterFragment fragment = new RegisterFragment();
            fragmentTransaction.add(R.id.activity_option, fragment);
            changeUI(false);
            fragmentTransaction.commit();
        }
    }

    private void toast(String text){
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }

    private void changeUI(boolean signedIn){
        if(signedIn){
            findViewById(R.id.btnLogIn).setVisibility(View.VISIBLE);
            findViewById(R.id.btnSignIn).setVisibility(View.VISIBLE);
            findViewById(R.id.tbEmail).setVisibility(View.VISIBLE);
            findViewById(R.id.tbPassword).setVisibility(View.VISIBLE);
        }else{
            findViewById(R.id.btnLogIn).setVisibility(View.GONE);
            findViewById(R.id.btnSignIn).setVisibility(View.GONE);
            findViewById(R.id.tbEmail).setVisibility(View.GONE);
            findViewById(R.id.tbPassword).setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        acti();
    }

    public void acti(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

   public void createAccount(Management man, String password) {
        final Management manager = man;
        final String pass= password;
        mfireBase = new Firebase("https://joinus-2fc04.firebaseio.com/Users/Business");
        mAuth.createUserWithEmailAndPassword(man.getEmail(), password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
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
                    mfireBase.child("email").setValue(manager.getEmail().toString());
                    mfireBase.child("businessLine").setValue(manager.getBusinessLine());
                    mfireBase.child("businessname").setValue(manager.getName());
                    mfireBase.child("managerName").setValue(manager.getManagerName());
                    mfireBase.child("mangerPhone").setValue(manager.getMangerPhone());
                    mfireBase.child("address").setValue(manager.getAddress());
                    mfireBase.child("businessphone").setValue(manager.getMangerPhone());
                    mfireBase.child("numberOfemployees").setValue(manager.getNumberOfemployees());

                    //signIn(manager.getId(), pass);




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
                            //finish();
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
    }
    private void signIn(String email, String password) {
        //Log.d(TAG, "signIn:" + email);

        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                           // Log.w(TAG, "signInWithEmail:failed", task.getException());
                            Toast.makeText(OptionAct.this, "failed",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // [START_EXCLUDE]
                        if (!task.isSuccessful()) {
                            toast("failed");
                        }

                        // [END_EXCLUDE]
                    }
                });
        // [END sign_in_with_email]
    }
}
