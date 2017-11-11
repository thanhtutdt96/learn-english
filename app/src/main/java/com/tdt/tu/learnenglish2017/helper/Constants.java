package com.tdt.tu.learnenglish2017.helper;

public class Constants {

    public static final int CODE_GET_REQUEST = 1024;

    public static final int CODE_POST_REQUEST = 1025;

    private static String ROOT_URL = "http://192.168.1.55/learn_english_2017/";

    public static final String API_KEY = "AIzaSyD3-fhnusRAieaxCw8sfkZyxJBoWpqr7PY";

    public static String URL_LOGIN = ROOT_URL + "login.php";

    public static String URL_REGISTER = ROOT_URL + "register.php";

    public static String URL_ALL_LESSONS = ROOT_URL + "get_lessons_info.php";

    public static String URL_GET_QUESTIONS = ROOT_URL + "api.php?apicall=getquestions";

    public static String URL_ADD_QUESTION = ROOT_URL + "api.php?apicall=createquestion";

    public static String URL_GET_ANSWERS = ROOT_URL + "api.php?apicall=getanswersbyquestionid";

    public static String URL_ADD_ANSWER = ROOT_URL + "api.php?apicall=createanswer";

    public static String URL_GET_CATEGORIES = ROOT_URL + "api.php?apicall=getcategories";

    public static String URL_GET_COURSES_BY_CATEGORY_ID = ROOT_URL + "api.php?apicall=getcoursesbycategoryid";

    public static String URL_ADD_USER_COURSE = ROOT_URL + "api.php?apicall=createusercourse";

    public static String URL_GET_COURSES_BY_EMAIL = ROOT_URL + "api.php?apicall=getcoursesbyemail";

    public static String URL_ADD_USER_FAVORITE = ROOT_URL + "api.php?apicall=createuserfavorite";

    public static String URL_GET_FAVORITES_BY_EMAIL = ROOT_URL + "api.php?apicall=getfavoritesbyemail";

}
