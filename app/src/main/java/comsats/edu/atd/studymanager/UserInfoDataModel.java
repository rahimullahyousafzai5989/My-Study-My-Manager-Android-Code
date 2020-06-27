package comsats.edu.atd.studymanager;

public class UserInfoDataModel {
    String firstname;
    String lastname;
    String username;
    String date;
    String email;

    public String getProfilesummary() {
        return profilesummary;
    }

    String profilesummary;

    public String getProfilepic() {
        return profilepic;
    }

    String profilepic;

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getUsername() {
        return username;
    }

    public String getDate() {
        return date;
    }

    public String getEmail() {
        return email;
    }

    public UserInfoDataModel(String firstname, String lastname, String username, String date, String email,String profilepic,String profilesummary) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.username = username;
        this.date = date;
        this.email = email;
        this.profilepic = profilepic;
        this.profilesummary = profilesummary;
    }
}
