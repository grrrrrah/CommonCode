package com.lijangop.sdk.widget;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lijangop.sdk.R;
import com.lijangop.sdk.definterface.SimpleCallBack;

/**
 * @Author lijiangop
 * @CreateTime 2019/9/6 17:04
 */
public class TitleBar extends RelativeLayout {

    private Activity mActivity;

    private ImageView mIvLeft;
    private TextView  mTvTitle;
    private EditText  mEtSearch;
    private TextView  mTvRight;
    private ImageView mIvRight;

    private String  mTitleStr;
    private boolean mRImgVState;
    private boolean mRTvVState;

    private SimpleCallBack rightCallBack;

    public TitleBar(Context context) {
        super(context);
    }

    public TitleBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        mActivity = (Activity) context;
        @SuppressLint("Recycle") TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TitleBar);
        mTitleStr = typedArray.getString(R.styleable.TitleBar_titleText);
        mRImgVState = typedArray.getBoolean(R.styleable.TitleBar_isShowRightImg, false);
        mRTvVState = typedArray.getBoolean(R.styleable.TitleBar_isShowRightTv, false);
        initView(context);
    }

    private void initView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.title_bar, null, false);

        mIvLeft = view.findViewById(R.id.iv_back);
        mTvTitle = view.findViewById(R.id.tv_title);
        mEtSearch = view.findViewById(R.id.et_search);
        mTvRight = view.findViewById(R.id.tv_right);
        mIvRight = view.findViewById(R.id.iv_right);

        mTvTitle.setText(TextUtils.isEmpty(mTitleStr) ? "" : mTitleStr);
        mIvLeft.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mActivity != null)
                    mActivity.finish();
            }
        });
        mIvRight.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rightCallBack != null)
                    rightCallBack.doSomething();
            }
        });
        mIvRight.setVisibility(mRImgVState ? VISIBLE : INVISIBLE);
        mTvRight.setVisibility(mRTvVState ? VISIBLE : INVISIBLE);
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        addView(view, lp);
    }

    //-----------------------------------------------------------------------------------对外提供方法-----------------------------------------------------------------------------

    public void setTitle(String title) {
        mTvTitle.setText(TextUtils.isEmpty(title) ? "" : title);
    }

    public void setTitleColor(int color) {
        mTvTitle.setTextColor(color);
    }

    public void setTitleGravity(int left) {
        mTvTitle.setGravity(left);
    }

    public void setLeftOnclick(OnClickListener listener) {
        mIvLeft.setOnClickListener(listener);
    }

    public void initRightImg(SimpleCallBack callBack) {
        mIvRight.setVisibility(VISIBLE);
        this.rightCallBack = callBack;
    }

    public void initRightTv(String right, int color, OnClickListener listener) {
        mTvRight.setVisibility(View.VISIBLE);
        mTvRight.setText(right);
        mTvRight.setTextColor(color);
        mTvRight.setOnClickListener(listener);
    }
}
