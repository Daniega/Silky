package joinus.joinus.com.joinus.Class;

import android.app.Activity;
import android.content.IntentSender;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;

//Google singelTon class Manage one instance of google.
public class GoogleSingleTon {
    private static GoogleSingleTon instance = null;
    private static GoogleApiClient mGoogleApiClient = null;
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;

    public GoogleSingleTon() {}

    public static GoogleSingleTon getInstance(GoogleApiClient aGoogleApiClient) {
        if (instance == null) {
            instance = new GoogleSingleTon();
            mGoogleApiClient = aGoogleApiClient;
        }
        return instance;
    }

    public static void clear() {
        instance = null;
    }

    public GoogleApiClient get_GoogleApiClient() {
        return mGoogleApiClient;
    }

    //Sing out facebook and google.
    public void logOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {}
                });
    }

    //Activate location
    public void locationOn(final Activity act) {
        GoogleApiClient apiClient = new GoogleApiClient.Builder(act).addApi(LocationServices.API).build();
        apiClient.connect();
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true); //this is the key ingredient

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(apiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. show dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result in onActivityResult().
                            status.startResolutionForResult(act, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. we won't show the dialog.
                        break;
                }
            }
        });
    }
}
