package imooc.android.com.animation;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.BounceInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    int MIN_POS = 0;
    int MAX_POS = 5;

    ItemView mView;
    FrameLayout mLayout;
    boolean mAnimLock;

    int mCurrentPos;
    boolean[] mIsFilled = new boolean[MAX_POS + 1];
    List<View> mViews = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLayout = (FrameLayout) findViewById(R.id.animation_area);

        ControlButtonListener listener = new ControlButtonListener();
        findViewById(R.id.left).setOnClickListener(listener);
        findViewById(R.id.right).setOnClickListener(listener);
        findViewById(R.id.down).setOnClickListener(listener);
        nextView();
    }

    private void nextView() {
        mCurrentPos = 0;
        mView = new ItemView(this);
        mLayout.addView(mView);
        mViews.add(mView);
    }

    private class ControlButtonListener implements OnClickListener {
        private static final int LEFT = 10;
        private static final int RIGHT = 11;
        private static final int DOWN = 12;
        private static final int DISAPPEAR = 13;

        @Override
        public void onClick(View v) {
            Log.d("MC", "current Position = " + mCurrentPos);
            if (mAnimLock) {
                return;
            }
            Animation anim = null;
            switch (v.getId()) {
                case R.id.right:
                    if (mCurrentPos < MAX_POS) {
                        anim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.right_step);
                        anim.setAnimationListener(new AnimationAfterListener(mView, RIGHT));
                    }
                    break;
                case R.id.left:
                    if (mCurrentPos > MIN_POS) {
                        anim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.left_step);
                        anim.setAnimationListener(new AnimationAfterListener(mView, LEFT));
                    }
                    break;
                case R.id.down:
                    anim = new TranslateAnimation(0, 0, 0, mLayout.getBottom() - mView.getBottom());
                    anim.setDuration(1000);
                    anim.setFillAfter(true);
                    anim.setInterpolator(new BounceInterpolator());
                    anim.setAnimationListener(new AnimationAfterListener(mView, DOWN));
                    break;
            }
            if (anim != null) {
                anim.setFillAfter(true);
                mView.startAnimation(anim);
                mAnimLock = true;
                Log.d("MC", "anim lock : " + mCurrentPos);
            }
        }

        private class AnimationAfterListener implements Animation.AnimationListener {
            private View view;
            private int direction;

            AnimationAfterListener(View view, int direction) {
                this.view = view;
                this.direction = direction;
            }

            @Override
            public void onAnimationStart(Animation animation) {
                Log.d("MC", "animation start");
            }

            @Override
            public void onAnimationEnd(final Animation animation) {
                Log.d("MC", "animation end");

                final int left = view.getLeft();
                final int top = view.getTop();
                final int right = view.getRight();
                final int bottom = view.getBottom();
                switch (direction) {
                    case LEFT:
                        view.post(new Runnable() {
                            @Override
                            public void run() {
                                view.clearAnimation();
                                view.layout(left - view.getWidth(), top, right - view.getWidth(), bottom);
                                mCurrentPos--;
                                mAnimLock = false;
                                Log.d("MC", "anim unlock : " + mCurrentPos);
                            }
                        });
                        break;
                    case RIGHT:
                        view.post(new Runnable() {
                            @Override
                            public void run() {
                                view.clearAnimation();
                                view.layout(left + view.getWidth(), top, right + view.getWidth(), bottom);
                                mCurrentPos++;
                                mAnimLock = false;
                                Log.d("MC", "anim unlock : " + mCurrentPos);
                            }
                        });
                        break;
                    case DOWN:
                        view.post(new Runnable() {
                            @Override
                            public void run() {
                                view.clearAnimation();
                                view.layout(left, top + mLayout.getBottom() - view.getBottom(),
                                        right, bottom + mLayout.getBottom() - view.getBottom());
                                mView.end();
                                mView.requestLayout();
                                mIsFilled[mCurrentPos] = true;
                                boolean isFull = true;
                                for (boolean isFilled : mIsFilled) {
                                    if (!isFilled) {
                                        isFull = false;
                                        break;
                                    }
                                }

                                if (isFull) {
                                    for (final View v : mViews) {
                                        v.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                Animation disappearAnim = AnimationUtils
                                                        .loadAnimation(MainActivity.this, R.anim.disappear);
                                                disappearAnim.setAnimationListener(new AnimationAfterListener(v, DISAPPEAR));
                                                disappearAnim.setFillAfter(true);
                                                v.startAnimation(disappearAnim);
                                                mAnimLock = true;
                                            }
                                        });

                                    }
                                }
                                nextView();
                                mAnimLock = false;
                            }
                        });
                        break;
                    case DISAPPEAR:
                        view.post(new Runnable() {
                            @Override
                            public void run() {
                                disappear();
                                mAnimLock = false;
                                if (mLayout.getChildCount() <= 0) {
                                    nextView();
                                    mAnimLock = false;
                                }
                            }
                        });
                        break;

                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                Log.d("MC", "animation repeat");
            }
        }
    }

    private void disappear() {
        for (int i = 0; i < mIsFilled.length; i++) {
            mIsFilled[i] = false;
        }
        mViews.clear();
        mLayout.removeAllViews();
    }
}
