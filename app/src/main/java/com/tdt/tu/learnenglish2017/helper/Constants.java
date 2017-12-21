package com.tdt.tu.learnenglish2017.helper;

public class Constants {

    public static final int CODE_GET_REQUEST = 1024;

    public static final int CODE_POST_REQUEST = 1025;

    public static final String PREFERENCES_KEY = "MY_PREFS";

    public static final String API_KEY = "AIzaSyD3-fhnusRAieaxCw8sfkZyxJBoWpqr7PY";

    private static String ROOT_URL = "http://192.168.1.55/learn_english_2017/";

    public static String URL_LOGIN = ROOT_URL + "login.php";

    public static String URL_REGISTER = ROOT_URL + "register.php";

    public static String URL_GET_LESSONS_BY_COURSE_ID = ROOT_URL + "api.php?apicall=getlessonsbycourseid";

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

    public static String URL_GET_COURSE_ID_BY_EMAIL = ROOT_URL + "api.php?apicall=getcourseidbyemail";

    public static String URL_GET_SEARCH_RESULTS = ROOT_URL + "api.php?apicall=getsearchresults";

    public static String URL_GET_FEATURED_COURSES = ROOT_URL + "api.php?apicall=getfeaturedcourses";

    public static String URL_GET_TOP_COURSES = ROOT_URL + "api.php?apicall=gettopcourses";

    public static String URL_GET_FIRST_QUIZ_QUESTIONS = ROOT_URL + "api.php?apicall=getfirstquizquestions";

    public static String URL_GET_LESSON_QUIZ_QUESTIONS = ROOT_URL + "api.php?apicall=getlessonquizquestions";

    public static String URL_GET_LESSON_NUMBERS_BY_COURSE_ID = ROOT_URL + "api.php?apicall=getlessonnumbersbycourseid";

}
