package com.tdt.tu.learnenglish2017.helper;

import android.os.Environment;

public class Constants {

    public static final int CODE_GET_REQUEST = 1024;
    public static final int CODE_POST_REQUEST = 1025;
    public static final String PREFERENCES_KEY = "MY_PREFS";

    public static final String API_KEY = "AIzaSyD3-fhnusRAieaxCw8sfkZyxJBoWpqr7PY";

    public static String DOWNLOAD_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/LearnEnglish2017/Download/";

    private static String ROOT_URL = "http://learn-english-api.000webhostapp.com/v1/api.php?apicall=";

    public static String URL_GET_LESSONS_BY_COURSE_ID = ROOT_URL + "getlessonsbycourseid";

    public static String URL_GET_QUESTIONS = ROOT_URL + "getquestions";

    public static String URL_ADD_QUESTION = ROOT_URL + "createquestion";

    public static String URL_GET_ANSWERS = ROOT_URL + "getanswersbyquestionid";

    public static String URL_ADD_ANSWER = ROOT_URL + "createanswer";

    public static String URL_GET_CATEGORIES = ROOT_URL + "getcategories";

    public static String URL_GET_COURSES_BY_CATEGORY_ID = ROOT_URL + "getcoursesbycategoryid";

    public static String URL_ADD_USER_COURSE = ROOT_URL + "createusercourse";

    public static String URL_GET_COURSES_BY_EMAIL = ROOT_URL + "getcoursesbyemail";

    public static String URL_ADD_USER_FAVORITE = ROOT_URL + "createuserfavorite";

    public static String URL_GET_FAVORITES_BY_EMAIL = ROOT_URL + "getfavoritesbyemail";

    public static String URL_GET_USER_COURSE_IDS_BY_EMAIL = ROOT_URL + "getusercourseidsbyemail";

    public static String URL_GET_FAVORITE_COURSE_IDS_BY_EMAIL = ROOT_URL + "getfavoritecourseidsbyemail";

    public static String URL_REMOVE_COURSE_FROM_FAVORITE = ROOT_URL + "removecoursefromfavorite";

    public static String URL_GET_SEARCH_RESULTS = ROOT_URL + "getsearchresults";

    public static String URL_GET_SEARCH_SUGGESTIONS = ROOT_URL + "getsearchsuggestions";

    public static String URL_GET_FEATURED_COURSES = ROOT_URL + "getfeaturedcourses";

    public static String URL_GET_TOP_COMMUNICATION_COURSES = ROOT_URL + "gettopcommunicationcourses";

    public static String URL_GET_TOP_PROFICIENCY_COURSES = ROOT_URL + "gettopproficiencycourses";

    public static String URL_GET_FIRST_QUIZ_QUESTIONS = ROOT_URL + "getfirstquizquestions";

    public static String URL_GET_LESSON_QUIZ_QUESTIONS = ROOT_URL + "getlessonquizquestions";

    public static String URL_ADD_LESSON_QUIZ_RESULT = ROOT_URL + "createlessonquizresult";

    public static String URL_GET_LESSON_QUIZ_RESULTS = ROOT_URL + "getlessonquizresults";

    public static String URL_GET_LESSON_NUMBERS_BY_COURSE_ID = ROOT_URL + "getlessonnumbersbycourseid";

    public static String URL_ADD_FIRST_QUIZ_RESULT = ROOT_URL + "createfirstquizresult";

    public static String URL_GET_FIRST_QUIZ_RESULTS = ROOT_URL + "getfirstquizresults";

    public static String URL_GET_COURSE_SUGGESTIONS = ROOT_URL + "getcoursesuggestions";

    public static String URL_SAVE_WATCHED_LESSON = ROOT_URL + "savewatchedlesson";

    public static String URL_LOAD_WATCHED_LESSONS = ROOT_URL + "loadwatchedlessons";

    public static String URL_LOAD_COURSE_RATING = ROOT_URL + "loadcourserating";

    public static String URL_SAVE_COURSE_RATING = ROOT_URL + "savecourserating";

    public static String URL_CHECK_FIRST_LOGIN = ROOT_URL + "checkfirstlogin";

    public static String URL_SAVE_FIRST_LOGIN = ROOT_URL + "savefirstlogin";

    public static String URL_GET_TOTAL_PROGRESS = ROOT_URL + "gettotalprogress";

    public static String URL_GET_SCORE_RANK = ROOT_URL + "getscorerank";

}
