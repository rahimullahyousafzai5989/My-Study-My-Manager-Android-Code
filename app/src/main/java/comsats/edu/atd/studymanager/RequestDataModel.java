package comsats.edu.atd.studymanager;

public class RequestDataModel {
String sendername;
    String recievername;
    String isAccepted;



    public String getProfilepic() {
        return profilepic;
    }

    String profilepic;

    public String getIsAccepted() {
        return isAccepted;
    }

    public String getSendername() {
        return sendername;
    }

    public String getRecievername() {
        return recievername;
    }

    public String getDate() {
        return date;
    }

    public RequestDataModel(String sendername, String recievername, String date,String isAccepted,String profilepic) {
        this.sendername = sendername;
        this.recievername = recievername;
        this.date = date;
        this.isAccepted = isAccepted;
        this.profilepic = profilepic;
    }

    String date;

}
