package joinus.joinus.com.joinus.Fragment;

import android.Manifest;
import android.app.Fragment;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import joinus.joinus.com.joinus.Activity.MainActivity;
import joinus.joinus.com.joinus.Activity.OptionAct;
import joinus.joinus.com.joinus.Class.Management;
import joinus.joinus.com.joinus.Class.SmsManage;
import joinus.joinus.com.joinus.R;

import static android.app.Activity.RESULT_OK;

public class RegisterFragment extends Fragment implements View.OnClickListener {
    private String busType;
    int flag = 0;
    EditText address;
    private Firebase mfireBase;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    // Inflate the Spinner_item for this fragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    public void onStart(){
        super.onStart();

        ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.SEND_SMS},1);
        address = (EditText) getView().findViewById(R.id.etAddress);
        Button done = (Button)getView().findViewById(R.id.btnDone);

        //Set click listeners.
        done.setOnClickListener(this);
        address.setOnClickListener(this);
        makeSpinner();
    }

    public void onClick(View v) {
        //If btDone button was pressed.
        if(v.getId() == R.id.btnDone) {
            SmsManage sms = new SmsManage();
            Management man = new Management();
            EditText busName = (EditText) getView().findViewById(R.id.etName);
            EditText busPhone = (EditText) getView().findViewById(R.id.etPhone);
            EditText busEmail = (EditText) getView().findViewById(R.id.etEmail);
            EditText busPassword1 = (EditText) getView().findViewById(R.id.etPassword1);
            EditText busPassword2 = (EditText) getView().findViewById(R.id.etPassword2);
            EditText managerName = (EditText) getView().findViewById(R.id.etManagerName);
            EditText managerPhone = (EditText) getView().findViewById(R.id.etManagerPhone);
            EditText numberOfemployees =(EditText)getView().findViewById(R.id.etNumemplo);




            if (TextUtils.isEmpty(busName.getText().toString()))
                busName.setError("יש להכניס שם עסק");
            else
                ++flag;

            if (!TextUtils.isEmpty(busPhone.getText().toString()) && man.checkPhoneOnlyNum(busPhone.getText().toString())) {
                if (man.checkPhoneSize(busPhone.getText().toString()))
                    ++flag;
                else
                    busPhone.setError("יש להכניס מספר בעל 9/10 ספרות");
            } else
                busPhone.setError("יש להכניס מספר תקין");


            if (busType != null && busType != "סוג העסק") {
                ++flag;
            } else
                Toast.makeText(getActivity(), "יש להכניס תחום עיסוק", Toast.LENGTH_SHORT).show();

            if (man.isEmailValid(busEmail.getText().toString())) {
                if (!TextUtils.isEmpty(busPassword1.getText().toString()) && busPassword1.getText().toString().length() >= 6) {
                    if (!TextUtils.isEmpty(busPassword2.getText().toString()) && busPassword1.getText().toString().equals(busPassword2.getText().toString())) {
                        ++flag;
                    } else
                        busPassword2.setError("סיסמאות לא זהות");
                } else
                    busPassword1.setError("סיסמא צריכה להכיל לפחות 6 תווים");
            } else
                busEmail.setError("יש להכניס כתובת אימייל חוקית");

            if (TextUtils.isEmpty(managerName.getText().toString()))
                managerName.setError("יש להכניס שם איש קשר");
            else
                ++flag;
            if (TextUtils.isEmpty(numberOfemployees.getText().toString()))
                numberOfemployees.setError("יש להכניס מספר עובדים");
            else
                ++flag;

            if (!TextUtils.isEmpty(managerPhone.getText().toString()) && man.checkPhoneOnlyNum(managerPhone.getText().toString())) {
                if (man.checkPhoneSize(managerPhone.getText().toString()))
                    ++flag;
                else
                    busPhone.setError("יש להכניס מספר בעל 9/10 ספרות");
            } else
                busPhone.setError("יש להכניס מספר תקין");

            if (flag == 8) {
                man.setBusinessname(busName.getText().toString());
                man.setBusinessLine(busType.toString());
                // man.setBusinessPhone(busPhone.toString());
                man.setManagerName(managerName.getText().toString());
                man.setMangerPhone(managerPhone.getText().toString());
                man.setEmail(busEmail.getText().toString());
                man.setName(busName.getText().toString());
                man.setAddress(address.getText().toString());
                man.setNumberOfemployees(numberOfemployees.getText().toString());
                mfireBase = new Firebase("https://joinus-2fc04.firebaseio.com/Business");
                ((OptionAct) getActivity()).createAccount(man, busPassword1.getText().toString());
            }
        }

        //If etAddress EditText is pressed.
        if(v.getId() == R.id.etAddress){
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();  //Build a intent builder.
            try {
                Intent intent = builder.build(getActivity());  //Set the intent with activity.
                startActivityForResult(intent,1); //Load the intent and wait for result.
                ++flag;
            } catch (GooglePlayServicesRepairableException e) {
                e.printStackTrace();
            } catch (GooglePlayServicesNotAvailableException e) {
                e.printStackTrace();
            }
        }
    }

    //Handle result
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == 1){   //Hand picker result.
            if(resultCode == RESULT_OK){
                Place place = PlacePicker.getPlace(getActivity(),data); //Get place object from placePicker.
                address.setText(place.getAddress().toString()); //Set the address in the etAddress EditText.
            }
        }
    }

    //Spinner maker (drop down for business Lines)
    public void makeSpinner(){
        final Spinner spinner = (Spinner) getView().findViewById(R.id.sType);
        final List<String> busTypeList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.busType)));  //Get array of data from resources.

        // Initializing an ArrayAdapter, Disable the first item for hint.
        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getActivity(),R.layout.spinner_item,busTypeList){
            @Override
            public boolean isEnabled(int position){
                if(position == 0)
                    return false;
                else
                    return true;
            }

            // Set the hint text color gray, else set black.
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position == 0)
                    tv.setTextColor(Color.GRAY);
                else
                    tv.setTextColor(Color.BLACK);
                return view;
            }
        };

        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);  //Drop down list of business Lines
        spinner.setAdapter(spinnerArrayAdapter);

        // If user change the default selection
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) { //Item selected.
                String selectedItemText = (String) parent.getItemAtPosition(position);

                if(position > 0)// Notify the selected item text
                    busType = selectedItemText;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }
}