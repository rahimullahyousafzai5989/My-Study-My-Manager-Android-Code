package comsats.edu.atd.studymanager;

public class NotificationDetails {

    public String getSubjecttitle() {
        return subjecttitle;
    }

    public String getCategory() {
        return category;
    }

    public NotificationDetails(String subjecttitle, String category) {
        this.subjecttitle = subjecttitle;
        this.category = category;
    }

    String  subjecttitle,category;

}
