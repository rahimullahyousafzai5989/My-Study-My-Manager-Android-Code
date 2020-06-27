package comsats.edu.atd.studymanager;

public class PdfFileDataModel {
    String filename;
    String filediscription;
    String file_date;


    public String getFiletitle() {
        return filetitle;
    }

    String filetitle;

    public String getFilename() {
        return filename;
    }

    public String getFilediscription() {
        return filediscription;
    }

    public String getFile_date() {
        return file_date;
    }

    public PdfFileDataModel(String filename, String filediscription, String file_date,String filetitle) {
        this.filename = filename;
        this.filediscription = filediscription;
        this.file_date = file_date;
        this.filetitle = filetitle;
    }
}
