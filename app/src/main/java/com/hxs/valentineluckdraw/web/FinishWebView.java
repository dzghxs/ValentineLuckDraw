package com.hxs.valentineluckdraw.web;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.widget.AbsListView;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;

import androidx.viewpager.widget.ViewPager;

import com.github.lzyzsd.jsbridge.BridgeWebView;

public class FinishWebView extends BridgeWebView {

    private OnLoadFinishListener mOnLoadFinishListener;

    public interface OnLoadFinishListener {
        public void onLoadFinish(String url);
    }

    private boolean isRendered = false;

    public FinishWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FinishWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public FinishWebView(Context context) {
        super(context);
    }

    private void init() {
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!isRendered) {
            isRendered = getContentHeight() > 0;
            if (mOnLoadFinishListener != null) {
                mOnLoadFinishListener.onLoadFinish(this.getUrl());
            }
        }
    }

    public void setOnLoadFinishListener(OnLoadFinishListener onLoadFinishListener) {
        this.mOnLoadFinishListener = onLoadFinishListener;
    }

    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        if (clampedX) {
            ViewParent viewParent = findViewParentIfNeeds(this);
            viewParent.requestDisallowInterceptTouchEvent(false);
        }
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            ViewParent viewParent = findViewParentIfNeeds(this);
            if (viewParent != null)
                viewParent.requestDisallowInterceptTouchEvent(true);
        }
        return super.onTouchEvent(event);
    }

    private ViewParent findViewParentIfNeeds(View tag) {
        ViewParent parent = tag.getParent();
        if (parent == null) {
            return null;
        }
        if (parent instanceof ViewPager || parent instanceof AbsListView || parent instanceof ScrollView || parent instanceof HorizontalScrollView) {
            return parent;
        } else {
            if (parent instanceof View) {
                findViewParentIfNeeds((View) parent);
            } else {
                return parent;
            }
        }
        return parent;
    }

}
