package joinus.joinus.com.joinus.Class;

import java.sql.Date;

/**
 * Created by Romi on 30/10/2016.
 */

public class User {
    public String name;
    private String id;
    private String personPhotoUrl;


    public User(){}

    public User(String name, String id, String photoUrl){
        setName(name);
        setId(id);
        setPersonPhotoUrl(photoUrl);
    }

    public void setName(String name){
            this.name = name;
    }

    public String getName(){
        return name;
    }

    public void setId(String id){
        this.id = id;
    }

    public String getId(){
        return id;
    }

    public void setPersonPhotoUrl(String personPhotoUrl){
        this.personPhotoUrl = personPhotoUrl;
    }

    public String getPersonPhotoUrl(){
        return personPhotoUrl;
    }
}
