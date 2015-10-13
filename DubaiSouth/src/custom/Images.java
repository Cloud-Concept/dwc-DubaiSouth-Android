package custom;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.text.Html;

import cloudconcept.dwc.R;

/**
 * Created by M_Ghareeb on 10/12/2015.
 */
public class Images implements Html.ImageGetter{
    Activity activity;
public Images(Activity activity){
    this.activity=activity;
}
        public Drawable getDrawable(String source) {
            int id=0;

            if (source.equals("search")) {
                id = R.drawable.search;
            }



            Drawable d = activity.getResources().getDrawable(id);
            d.setBounds(-10,20,d.getIntrinsicWidth()-10,d.getIntrinsicHeight()+20);

            return d;
        }

}
