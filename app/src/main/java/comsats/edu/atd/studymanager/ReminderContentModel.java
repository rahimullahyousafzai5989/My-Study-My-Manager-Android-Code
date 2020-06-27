package comsats.edu.atd.studymanager;

public class ReminderContentModel {
    String id;

    public String getId() {
        return id;
    }

    String remindername;
    String subjectname;
    String cat;
    String date;
    String time;
    String file_date;
    String code;

    public String getCode() {
        return code;
    }

    public String getRemindername() {
        return remindername;
    }

    public String getSubjectname() {
        return subjectname;
    }

    public String getCat() {
        return cat;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getMedium() {
        return medium;
    }

    public String getFile_date() {
        return file_date;
    }

    public ReminderContentModel(String remindername, String subjectname, String cat, String date, String time, String medium, String file_date,String id,String code) {
        this.remindername = remindername;
        this.subjectname = subjectname;
        this.cat = cat;
        this.date = date;
        this.time = time;
        this.medium = medium;
        this.file_date=file_date;
        this.id = id;
        this.code = code;
    }

    String medium;
}
