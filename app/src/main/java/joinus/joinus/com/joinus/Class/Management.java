package joinus.joinus.com.joinus.Class;
import com.google.firebase.database.IgnoreExtraProperties;
@IgnoreExtraProperties  public class Management extends User{
    private String businessphone;
    private String managerName;
    private String mangerPhone;
    private String numberOfemployees;
    private String businessLine;
    private String address;
    private String email;
    private String businessname;

    public Management(){}

    public Management(String businessname, String id, String personPhotoUrl, String businessphone, String address, String managerName, String mangerPhone, String businessLine, String numberOfemployees, String email){
        super(businessname,"id",personPhotoUrl);
        setBusinessLine(businessLine);
        setAddress(address);
        setManagerName(managerName);
        setMangerPhone(mangerPhone);
        setBusinessLine(businessLine);
        setNumberOfemployees(numberOfemployees);
        setEmail(email);
    }

    public void setBusinessPhone(String phone){
        businessphone = phone;
    }

    public String getBusinessphone(){
        return businessphone;
    }

    public void setManagerName(String name){
        managerName = name;
    }

    public String getManagerName(){
        return managerName;
    }

    public void setMangerPhone(String phone){
        mangerPhone = phone;
    }

    public String getMangerPhone(){
        return mangerPhone;
    }

    public void setNumberOfemployees(String num){
        numberOfemployees = num;
    }

    public String getNumberOfemployees(){
        return numberOfemployees;
    }

    public void setBusinessLine(String line){
        businessLine = line;
    }

    public String getEmail(){
        return email;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public String getBusinessLine(){
        return businessLine;
    }

    public void setAddress(String add){
        address = add;
    }

    public String getAddress(){
        return address;
    }

    public boolean checkPhoneOnlyNum(String phone){
        if(phone.matches("[0-9]+"))
            return true;
        return false;
    }

    public boolean checkPhoneSize(String phone){
        if(phone.length() == 9 || phone.length() == 10)
            return true;
        return false;
    }

    public boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    public String getBusinessname(){
        return businessname;
    }

    public void setBusinessname(String businessname){
        this.businessname=businessname;
    }
}
