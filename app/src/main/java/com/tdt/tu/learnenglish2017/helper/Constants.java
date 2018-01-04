package com.tdt.tu.learnenglish2017.helper;

import android.os.Environment;

public class Constants {

    public static final int CODE_GET_REQUEST = 1024;
    public static final int CODE_POST_REQUEST = 1025;
    public static final String PREFERENCES_KEY = "MY_PREFS";
    public static final String API_KEY = "AIzaSyD3-fhnusRAieaxCw8sfkZyxJBoWpqr7PY";
    public static String DOWNLOAD_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/LearnEnglish2017/Download/";
    private static String ROOT_URL = "http://learnenglish2017.tk/app-service/";

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

    public static String URL_GET_USER_COURSE_IDS_BY_EMAIL = ROOT_URL + "api.php?apicall=getusercourseidsbyemail";

    public static String URL_GET_FAVORITE_COURSE_IDS_BY_EMAIL = ROOT_URL + "api.php?apicall=getfavoritecourseidsbyemail";

    public static String URL_REMOVE_COURSE_FROM_FAVORITE = ROOT_URL + "api.php?apicall=removecoursefromfavorite";

    public static String URL_GET_SEARCH_RESULTS = ROOT_URL + "api.php?apicall=getsearchresults";

    public static String URL_GET_SEARCH_SUGGESTIONS = ROOT_URL + "api.php?apicall=getsearchsuggestions";

    public static String URL_GET_FEATURED_COURSES = ROOT_URL + "api.php?apicall=getfeaturedcourses";

    public static String URL_GET_TOP_COURSES = ROOT_URL + "api.php?apicall=gettopcourses";

    public static String URL_GET_FIRST_QUIZ_QUESTIONS = ROOT_URL + "api.php?apicall=getfirstquizquestions";

    public static String URL_GET_LESSON_QUIZ_QUESTIONS = ROOT_URL + "api.php?apicall=getlessonquizquestions";

    public static String URL_ADD_LESSON_QUIZ_RESULT = ROOT_URL + "api.php?apicall=createlessonquizresult";

    public static String URL_GET_LESSON_QUIZ_RESULTS = ROOT_URL + "api.php?apicall=getlessonquizresults";

    public static String URL_GET_LESSON_NUMBERS_BY_COURSE_ID = ROOT_URL + "api.php?apicall=getlessonnumbersbycourseid";

    public static String URL_ADD_FIRST_QUIZ_RESULT = ROOT_URL + "api.php?apicall=createfirstquizresult";

    public static String URL_GET_FIRST_QUIZ_RESULTS = ROOT_URL + "api.php?apicall=getfirstquizresults";

    public static String URL_GET_COURSE_SUGGESTIONS = ROOT_URL + "api.php?apicall=getcoursesuggestions";

    public static String URL_SAVE_WATCHED_LESSON = ROOT_URL + "api.php?apicall=savewatchedlesson";

    public static String URL_LOAD_WATCHED_LESSONS = ROOT_URL + "api.php?apicall=loadwatchedlessons";


}
