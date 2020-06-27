package comsats.edu.atd.studymanager;

public class CollectionDataModel {

    private String collectionname;

    public String getCollectionname() {
        return collectionname;
    }

    public String getCollectiondate() {
        return collectiondate;
    }

    private String collectiondate;
    public CollectionDataModel(String collectionname, String collectiondate) {
        this.collectionname = collectionname;
        this.collectiondate = collectiondate;
    }

}
