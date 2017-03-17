package joinus.joinus.com.joinus.Class;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.telephony.SmsManager;

import java.util.Random;

public class SmsManage {

    public SmsManage(){}

    public String sendSms(String phone){
        String verCode = Integer.toString(new Random().nextInt(99999) + 9999);

        try {
            SmsManager.getDefault().sendTextMessage(phone, null, "Verification code: " + verCode, null, null);


        } catch (Exception e) {}

        return verCode;
    }
}
