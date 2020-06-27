package comsats.edu.atd.studymanager;

public class VideoContentModel {
    public String getFiletitle() {
        return filetitle;
    }

    String filename,filetitle;

    public String getFilename() {
        return filename;
    }
    public VideoContentModel(String filename,String filetitle) {
        this.filename = filename;
        this.filetitle = filetitle;
    }
}
