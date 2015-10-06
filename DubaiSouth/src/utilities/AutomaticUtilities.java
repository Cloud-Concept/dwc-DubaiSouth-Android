package utilities;

import android.app.Activity;
import android.view.inputmethod.InputMethodManager;

import java.util.Date;

/**
 * Created by M_Ghareeb on 8/30/2015.
 */
public class AutomaticUtilities {
    public static int daysBetween(Date d1, long d2){
        return (int)( ( d1.getTime()-d2 ) / (1000 * 60 * 60 * 24));
    }
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        try {
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }catch (NullPointerException e){
            
        }
    }
}
