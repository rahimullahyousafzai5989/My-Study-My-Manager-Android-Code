package comsats.edu.atd.studymanager;

import java.util.ArrayList;

public class NotificationDataModel {
    public static ArrayList<NotificationDetails> getDetails() {
        return details;
    }

    public static void setDetails(ArrayList<NotificationDetails> details) {
        NotificationDataModel.details = details;
    }

    public static ArrayList<NotificationDetails> details;
}
