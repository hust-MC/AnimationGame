package imooc.android.com.animation;


import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.WindowManager;

public class ItemView extends View {

    private final int DENSITY;
    private final int HEIGHT;
    private final int WIDTH;

    private int mLeft, mRight, mTop, mBottom;
    private boolean mIsEnd;

    public ItemView(Context context) {
        super(context);
        WindowManager wm = ((Activity) context).getWindowManager();

        DENSITY = (int) context.getResources().getDisplayMetrics().density;
        WIDTH = wm.getDefaultDisplay().getWidth() - 2 * 5 * DENSITY;
        HEIGHT = wm.getDefaultDisplay().getHeight() - 2 * 5 * DENSITY;
    }

    public void end() {
        mIsEnd = true;
        mLeft = getLeft();
        mRight = getRight();
        mTop = getTop();
        mBottom = getBottom();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (mIsEnd && changed) {
            layout(mLeft, mTop, mRight, mBottom);
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(WIDTH / 6, HEIGHT / 10);
    }
}
