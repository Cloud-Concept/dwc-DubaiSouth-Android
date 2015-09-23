package custom;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.ProgressBar;

/**
 * Created by Abanoub Wagdy on 9/12/2015.
 */
public class CustomProgressBar extends ProgressBar {
    public CustomProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        CircularProgressBarDrawable drawable = new CircularProgressBarDrawable();
        drawable.setColors(new int[]{0xffff0000, 0xffff00a8, 0xffb400ff, 0xff2400ff, 0xff008aff,
                0xff00ffe4, 0xff00ff60, 0xff0cff00, 0xffa8ff00, 0xffffc600, 0xffff3600, 0xffff0000});
        setProgressDrawable(drawable);
    }
}
