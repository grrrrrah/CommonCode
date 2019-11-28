package com.lijangop.sdk.widget;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;


/**
 * @Author lijiangop
 * @CreateTime 2019/11/27 10:50
 */
public class SuspensionItemDecoration extends RecyclerView.ItemDecoration {

    private OnTagListener listener;
    private Paint         mPaint, mTextPaint;
    private int                  mDecorationHeight    = dp2px(30);
    //点击事件
    private GestureDetector      gestureDetector;
    @SuppressLint("UseSparseArrays")
    private SparseArray<Integer> stickyHeaderPosArray = new SparseArray<>();//记录每个头部和悬浮头部的坐标信息【用于点击事件】

    public SuspensionItemDecoration(OnTagListener listener) {
        this.listener = listener;

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.parseColor("#F9F8F9"));

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(Color.parseColor("#898889"));
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setTextSize(sp2px(15));
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
        //点击事件
        if (gestureDetector == null) {
            gestureDetector = new GestureDetector(parent.getContext(), gestureListener);
            parent.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    return gestureDetector.onTouchEvent(motionEvent);
                }
            });
        }
        stickyHeaderPosArray.clear();
        //显示
        int itemCount = state.getItemCount();//获取所有item的数量
        int childCount = parent.getChildCount();//获取当前屏幕显示的item数量
        int left = parent.getLeft();
        int right = parent.getRight();
        String preTag;
        String curTag = null;
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            int position = parent.getChildAdapterPosition(child);//获取在列表中的位置
            preTag = curTag;
            curTag = listener.getTag(position);//获取当前位置tag
            if (curTag == null || TextUtils.equals(preTag, curTag))  //如果两个item属于同一个tag，就不绘制
                continue;
            int bottom = child.getBottom(); //获取item 的bottom
            int tagBottom = Math.max(mDecorationHeight, child.getTop());//计算出tag的bottom
            if (position + 1 < itemCount) { //判断是否是最后一个
                String nextTag = listener.getTag(position + 1); //获取下个tag
                if (!TextUtils.equals(curTag, nextTag) && bottom < tagBottom) //被顶起来的条件 当前tag与下个tag不等且item的bottom已小于分割线高度
                    tagBottom = bottom; //将item的bottom值赋给tagBottom 就会实现被顶上去的效果
            }
            c.drawRect(left, tagBottom - mDecorationHeight, right, tagBottom, mPaint); //绘制tag文字
            c.drawText(curTag, right / 2, tagBottom - (mDecorationHeight / 3), mTextPaint); //将tag绘制出来
            stickyHeaderPosArray.put(position, tagBottom);//将头部信息放进array
        }
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int childAdapterPosition = parent.getChildAdapterPosition(view);
        if (childAdapterPosition > 0 && TextUtils.equals(listener.getTag(childAdapterPosition), listener.getTag(childAdapterPosition - 1)))
            outRect.top = dp2px(0);
        else
            outRect.top = mDecorationHeight; //为每个decoration预留空间
    }

    public interface OnTagListener {
        String getTag(int position); //返回当前的itemDecoration的显示值

        void onTagClick(int position);//当前itemDecoration点击事件
    }

    private GestureDetector.OnGestureListener gestureListener = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            for (int i = 0; i < stickyHeaderPosArray.size(); i++) {
                int value = stickyHeaderPosArray.valueAt(i);
                float y = e.getY();
                if (value - mDecorationHeight <= y && y <= value) {//如果点击到分组头
                    if (listener != null) {
                        listener.onTagClick(stickyHeaderPosArray.keyAt(i));
                    }
                    return true;
                }
            }
            return false;
        }
    };

    private int dp2px(final float dpValue) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    private int sp2px(final float spValue) {
        final float fontScale = Resources.getSystem().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

}
