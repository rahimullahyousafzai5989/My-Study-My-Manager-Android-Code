package comsats.edu.atd.studymanager;

public class Urls {
    public static String IP = "192.168.10.8";
    public static String PORT = "3000";
    public static String DOMAIN1 = IP.trim() + ":" + PORT.trim();
    public static String DOMAIN = "http://"+IP.trim() + ":" + PORT.trim();

    public static String LOGIN_ACCOUNT = "http://" + DOMAIN1 + "/userlogin";
    public static String SIGNUP_ACCOUNT = "http://" + DOMAIN1 + "/registeruser";
    public static String ADD_COLLECTION = "http://" + DOMAIN1 + "/addcollection";
    public static String VIEW_COLLECTION = "http://" + DOMAIN1 + "/viewcollection";
    public static String VIEW_PDFFILES = "http://" + DOMAIN1 + "/viewpdffiles";
    public static String VIEW_DOCFILES = "http://" + DOMAIN1 + "/viewdocfiles";
    public static String VIEW_PHOTOS = "http://" + DOMAIN1 + "/viewphotos";
    public static String VIEW_AUDIOS = "http://" + DOMAIN1 + "/viewaudios";
    public static String VIEW_VIDEOS = "http://" + DOMAIN1 + "/viewvideos";
    public static String VIEW_ZIPFILES = "http://" + DOMAIN1 + "/viewzip";
    public static String VIEW_REMINDER = "http://" + DOMAIN1 + "/viewreminder";
    public static String CHECK_TIME = "http://" + DOMAIN1 + "/checktime";
    public static String UPLOAD_PDF = "http://" + DOMAIN1 + "/uploadpdfroute/uploadpdffile";
    public static String UPLOAD_DOC = "http://" + DOMAIN1 + "/uploaddocroute/uploaddocfile";
    public static String UPLOAD_PHOTO = "http://" + DOMAIN1 + "/uploadphotoroute/uploadphotofile";
    public static String UPLOAD_VIDEO = "http://" + DOMAIN1 + "/uploadvideoroute/uploadvideo";
    public static String UPLOAD_AUDIO = "http://" + DOMAIN1 + "/uploadaudioroute/uploadaudio";
    public static String UPLOAD_ZIP = "http://" + DOMAIN1 + "/uploadziproute/uploadzip";
    public static String UPLOAD_REMINDER = "http://" + DOMAIN1 + "/uploadreminderroute/setreminder";
    public static String DELETE_REMINDER = "http://" + DOMAIN1 + "/deleteroute/deletereminder";
    public static String DOWNLOAD_ZIP = "http://" + DOMAIN1 + "/assets/ZipFiles/";
    public static String DELETE_PDFFILES = "http://" + DOMAIN1 + "/deleteroute/deletepdffiles";
    public static String DELETE_DOCFILES = "http://" + DOMAIN1 + "/deleteroute/deletedocfiles";
    public static String DELETE_PHOTOS = "http://" + DOMAIN1 + "/deleteroute/deletephotos";
    public static String DELETE_VIDEOS = "http://" + DOMAIN1 + "/deleteroute/deletevideos";
    public static String DELETE_AUDIOS = "http://" + DOMAIN1 + "/deleteroute/deleteaudios";
    public static String DELETE_ZIPFILES = "http://" + DOMAIN1 + "/deleteroute/deletezipfiles";
    public static String DELETE_COLLECTION = "http://" + DOMAIN1 + "/deleteroute/deleteCollection";
   public static String SEARCH_USERS = "http://" + DOMAIN1 + "/searchroute/searchusers";

    public static String CHECK_REQUEST = "http://" + DOMAIN1 + "/requestroute/checkrequest";
    public static String SEND_REQUEST = "http://" + DOMAIN1 + "/requestroute/sendrequest";
    public static String ACCEPT_REQUEST = "http://" + DOMAIN1 + "/requestroute/acceptrequest";
    public static String DELETE_REQUEST = "http://" + DOMAIN1 + "/requestroute/deleterequest";
    public static String FIND_FRIENDS = "http://" + DOMAIN1 + "/requestroute/findfriend";

    public static String RECIEVER_REQUEST = "http://" + DOMAIN1 + "/requestroute/checkrecieverrequest";
    public static String FRIEND_REQUEST = "http://" + DOMAIN1 + "/requestroute/checkfriendrequest";

    public static String UPDATE_FIRSTNAME = "http://" + DOMAIN1 + "/updateprofile/updatefirstname";
    public static String UPDATE_LASTNAME = "http://" + DOMAIN1 + "/updateprofile/updatelastname";
    public static String UPDATE_USERNAME = "http://" + DOMAIN1 + "/updateprofile/updateusername";
    public static String UPDATE_EMAIL = "http://" + DOMAIN1 + "/updateprofile/updateemail";
    public static String UPDATE_PASSWORD = "http://" + DOMAIN1 + "/updateprofile/updatepassword";
    public static String UPDATE_SUMMMARY = "http://" + DOMAIN1 + "/updateprofile/updatesummary";
    public static String UPDATE_PROFILE_PIC = "http://" + DOMAIN1 + "/updateprofile/updateprofilepic";

    public static String GET_COUNT = "http://" + DOMAIN1 + "/getcount";

    public static String UPDATE_COLLECTION = "http://" + DOMAIN1 + "/updatecollection";
    public static String UPDATE_VIDEO = "http://" + DOMAIN1 + "/updatevideo";
    public static String UPDATE_AUDIO = "http://" + DOMAIN1 + "/updateaudio";
    public static String UPDATE_PDF = "http://" + DOMAIN1 + "/updatepdf";
    public static String UPDATE_DOCUMENT= "http://" + DOMAIN1 + "/updatedocument";
    public static String UPDATE_ZIP= "http://" + DOMAIN1 + "/updatezip";


}
