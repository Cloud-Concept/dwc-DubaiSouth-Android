package custom;

import android.content.Context;
import android.graphics.Canvas;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Abanoub Wagdy on 9/6/2015.
 */
public class CustomViewPager extends ViewPager {

//    private int childId;
//    private String objectAsString;
//    private Gson gson;

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

//
//    @Override
//    public boolean onInterceptTouchEvent(MotionEvent event) {
//
//        if (childId > 0) {
//            View scroll = findViewById(childId);
//            if (scroll != null) {
//                Rect rect = new Rect();
//                scroll.getHitRect(rect);
//                if (rect.contains((int) event.getX(), (int) event.getY())) {
//                    return false;
//                }
//            }
//        }
//        return super.onInterceptTouchEvent(event);
//    }
//
//    public void setChildId(int id) {
//        this.childId = id;
//    }


    @Override
    protected void onDraw(Canvas canvas) {
        setOffscreenPageLimit(1);
        super.onDraw(canvas);
    }

    @Override
    protected boolean canScroll(View v, boolean checkV, int dx, int x, int y) {
        if (v != this && (v instanceof HorizontalListView)) {
//            v.requestFocus();
//            v.getParent().requestDisallowInterceptTouchEvent(true);
//            ((HorizontalListView) v).requestDisallowInterceptTouchEvent(false);
//            HorizontalListView listView = (HorizontalListView) v;
//            HorizontalListViewAdapter adapter = (HorizontalListViewAdapter) listView.getAdapter();
//            ArrayList<ServiceItem> _items = adapter.getServiceItems();
//            if (!adapter.getInflated()) {
//                listView.setAdapter(adapter);
//                adapter.setInflated(true);
//                adapter.notifyDataSetChanged();
//            } else {
////                return super.canScroll(v, checkV, dx, x, y);
//                if (listView.getLastVisiblePosition() == _items.size()) {
//                    listView.setSelection(0);
//                }
//            }

            return true;
        } else {
            return super.canScroll(v, checkV, dx, x, y);
        }
    }
}
