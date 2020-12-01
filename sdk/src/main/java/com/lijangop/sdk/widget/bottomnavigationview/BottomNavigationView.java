package com.lijangop.sdk.widget.bottomnavigationview;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.lijangop.sdk.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author lijiangop
 * @CreateTime 2020/8/24 11:20
 */
public class BottomNavigationView extends LinearLayout {

    //必须(外部传入)
    private String[] tabTexts;
    private int[]    normalIcons, selectedIcons;
    private List<Fragment>  fragments;
    private FragmentManager fragmentManager;

    //非必须(外部传入)
    private int                      centerImgRes;
    private OnTabClickListener       onTabClickListener;
    private OnCenterTabClickListener onCenterTabClickListener;
    //Viewpager是否可滑动
    private boolean                  canScroll           = false;
    //Viewpager是否显示滑动动画
    private boolean                  isShowViewPagerAnim = false;
    //tabItem的选中后动画
    private Techniques               anim                = null;

    //内部变量
    private int tabCount, currentPosition;

    //view
    private View            mTopLine;
    private CustomViewPager mViewPager;
    private ImageView       mCenterImageView;
    private LinearLayout    mLLTab, mLLAddView;
    private RelativeLayout mContainer, mBottomContainer;

    //底部Text集合
    private List<TextView>  textViewList  = new ArrayList<>();
    //底部Image集合
    private List<ImageView> imageViewList = new ArrayList<>();
    //底部TabLayout（除中间加号）
    private List<View>      tabList       = new ArrayList<>();


    public BottomNavigationView(Context context) {
        super(context);
        initViews(context, null);
    }

    public BottomNavigationView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initViews(context, attrs);
    }

    private void initViews(Context context, AttributeSet attributeSet) {
        mContainer = (RelativeLayout) View.inflate(context, R.layout.bnv_navigation_layout, null);
        mBottomContainer = mContainer.findViewById(R.id.re_container);
        mLLTab = mContainer.findViewById(R.id.ll_tab);
        mLLAddView = mContainer.findViewById(R.id.ll_add_view);
        mTopLine = mContainer.findViewById(R.id.top_line);
        addView(mContainer);
    }

    //----------------------------------------------------------------------------------------------------------------------------------------------------------------

    public BottomNavigationView fragment(List<Fragment> fragments) {
        this.fragments = fragments;
        tabCount = fragments.size();
        return this;
    }

    public BottomNavigationView tabText(String[] tabTexts) {
        this.tabTexts = tabTexts;
        return this;
    }

    public BottomNavigationView tabIcon(int[] normalIcons) {
        this.normalIcons = normalIcons;
        return this;
    }

    public BottomNavigationView tabSelectedIcon(int[] selectedIcons) {
        this.selectedIcons = selectedIcons;
        return this;
    }

    public BottomNavigationView fragmentManager(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
        return this;
    }

    public BottomNavigationView canScroll(boolean canScroll) {
        this.canScroll = canScroll;
        return this;
    }

    public BottomNavigationView showViewPagerAnim(boolean isShowViewPagerAnim) {
        this.isShowViewPagerAnim = isShowViewPagerAnim;
        return this;
    }

    public BottomNavigationView setOnTabClickListener(OnTabClickListener onTabClickListener) {
        this.onTabClickListener = onTabClickListener;
        return this;
    }

    public BottomNavigationView tabAnimation(Anim anim) {
        if (anim != null)
            this.anim = anim.getYoyo();
        return this;
    }

    public BottomNavigationView centerImageRes(int centerImgRes, OnCenterTabClickListener onCenterTabClickListener) {
        this.onCenterTabClickListener = onCenterTabClickListener;
        this.centerImgRes = centerImgRes;
        return this;
    }

    public void setAddViewLayout(View addViewLayout) {
        FrameLayout.LayoutParams addParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        this.mLLAddView.addView(addViewLayout, addParams);
    }

    public ViewGroup getAddViewLayout() {
        return mLLAddView;
    }

    public boolean build() {
        //简单判断build之前的参数设置
        if (tabCount == 0) {
            ToastUtils.showShort("请添加页面数据");
            return false;
        }
        if (centerImgRes != 0 && tabCount % 2 != 0) {
            ToastUtils.showShort("添加中间图片,tab总数不能为奇数");
            return false;
        }
        if (tabTexts.length != tabCount) {
            ToastUtils.showShort("tab文字请与页面数保持一致");
            return false;
        }
        if (normalIcons.length != tabCount) {
            ToastUtils.showShort("tab图标请与页面数保持一致");
            ToastUtils.showShort("");
        }
        if (selectedIcons.length != tabCount) {
            ToastUtils.showShort("tab图标请与页面数保持一致");
            return false;
        }

        //修改导航栏的一些UI属性
        //1.
        mLLTab.setBackgroundColor(Color.parseColor("#fafafa"));
        ViewGroup.LayoutParams layoutParams = mLLTab.getLayoutParams();
        layoutParams.height = SizeUtils.dp2px(45);
        mLLTab.setLayoutParams(layoutParams);
        //2.
        mTopLine.setBackgroundColor(Color.parseColor("#265AA5"));

        //添加TabItem
        post(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < tabCount; i++) {
                    if (i == tabCount / 2)
                        addCenterTabItemView();
                    addNormalTabItemView(i);
                }
                updateNavigation(0, false);
            }
        });

        //绑定ViewPager
        setViewPagerAdapter();
        return true;
    }

    //-----------------------------------------------------------------------------内部方法------------------------------------------------------------------------------------
    //添加中心TabItem
    private void addCenterTabItemView() {
        if (centerImgRes == 0)
            return;
        //占位View
        RelativeLayout addItemView = new RelativeLayout(getContext());
        RelativeLayout.LayoutParams addItemParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        addItemParams.width = getWidth() / (tabCount + 1);
        addItemView.setLayoutParams(addItemParams);
        mLLTab.addView(addItemView);
        //中心ImageView
        mCenterImageView = new ImageView(getContext());
        LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        imageParams.width = SizeUtils.dp2px(55);
        imageParams.height = SizeUtils.dp2px(55);
        mCenterImageView.setTag(0);
        mCenterImageView.setLayoutParams(imageParams);
        mCenterImageView.setImageResource(centerImgRes);
        int dp2px = SizeUtils.dp2px(10);
        mCenterImageView.setPadding(dp2px, dp2px, dp2px, dp2px);
        mCenterImageView.setBackgroundResource(R.drawable.shape_circle);
        mCenterImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onCenterTabClickListener != null)
                    onCenterTabClickListener.onClick();
                //图片动画
                Integer tag = (Integer) mCenterImageView.getTag();
                if (tag == 0) {
                    mCenterImageView.setTag(1);
                    mCenterImageView.animate().rotation(45).setDuration(500);
                } else {
                    mCenterImageView.setTag(0);
                    mCenterImageView.animate().rotation(0).setDuration(500);
                }
                //更新导航栏
                for (int i = 0; i < imageViewList.size(); i++) {
                    imageViewList.get(i).setImageResource(normalIcons[i]);
                    textViewList.get(i).setTextColor(Color.parseColor("#aaaaaa"));
                    textViewList.get(i).setText(tabTexts[i]);
                }
            }
        });
        //mCenterImageView的父容器(第一层)
        final LinearLayout centerLinearLayout = new LinearLayout(getContext());
        centerLinearLayout.setOrientation(VERTICAL);
        centerLinearLayout.setGravity(Gravity.CENTER);
        centerLinearLayout.addView(mCenterImageView);
        //添加进容器中(第二层)
        RelativeLayout.LayoutParams linearParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        linearParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        linearParams.addRule(RelativeLayout.ABOVE, R.id.bottom_line);
        mBottomContainer.addView(centerLinearLayout, linearParams);
    }

    //添加TabItem
    private void addNormalTabItemView(final int position) {
        View itemView = View.inflate(getContext(), R.layout.bnv_navigation_tab_layout, null);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        if (centerImgRes != 0)
            params.width = getWidth() / (tabCount + 1);
        else
            params.width = getWidth() / tabCount;
        itemView.setLayoutParams(params);
        itemView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onTabClickListener != null) {//拦截点击事件
                    if (!onTabClickListener.interceptFromSelectEvent(view, position))
                        selectTab(position, isShowViewPagerAnim, true);
                } else
                    selectTab(position, isShowViewPagerAnim, true);
            }
        });
        //设置tab中内容对齐方式
        LinearLayout tabContent = itemView.findViewById(R.id.ll_tab_content);
        RelativeLayout.LayoutParams llParams = (RelativeLayout.LayoutParams) tabContent.getLayoutParams();
        llParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        tabContent.setLayoutParams(llParams);
        //textView
        TextView text = itemView.findViewById(R.id.tab_text_tv);
        LayoutParams textParams = (LayoutParams) text.getLayoutParams();
        textParams.topMargin = SizeUtils.dp2px(2);
        text.setLayoutParams(textParams);
        text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10f);
        textViewList.add(text);
        //imageView
        ImageView icon = itemView.findViewById(R.id.tab_icon_iv);
        icon.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        LayoutParams iconParams = (LayoutParams) icon.getLayoutParams();
        iconParams.width = SizeUtils.dp2px(23);
        iconParams.height = SizeUtils.dp2px(23);
        icon.setLayoutParams(iconParams);
        imageViewList.add(icon);

        tabList.add(itemView);
        mLLTab.addView(itemView);
    }

    /**
     * 添加ViewPager
     */
    private void setViewPagerAdapter() {
        if (mViewPager == null) {
            mViewPager = new CustomViewPager(getContext());
            mViewPager.setId(R.id.vp_layout);
            mContainer.addView(mViewPager, 0);
        }
        ViewPagerAdapter adapter = new ViewPagerAdapter(fragmentManager, fragments);
        mViewPager.setAdapter(adapter);
        mViewPager.setOffscreenPageLimit(10);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (onTabClickListener != null) {//拦截点击事件
                    if (!onTabClickListener.interceptFromSelectEvent(mViewPager, position))
                        selectTab(position, isShowViewPagerAnim, false);
                } else
                    selectTab(position, isShowViewPagerAnim, false);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        if (canScroll) {
            mViewPager.setCanScroll(true);
        } else {
            mViewPager.setCanScroll(false);
        }
    }

    /**
     * 切换ViewPager页面
     */
    private void selectTab(int position, boolean isShowViewPagerAnim, boolean isSelectViewPager) {
        if (currentPosition == position)
            return;
        currentPosition = position;
        if (isSelectViewPager && mViewPager != null)
            mViewPager.setCurrentItem(position, isShowViewPagerAnim);
        updateNavigation(position, true);
    }

    /**
     * 更新导航栏UI
     *
     * @param position
     */
    private void updateNavigation(int position, boolean showAnim) {
        for (int i = 0; i < tabCount; i++) {
            if (i == position) {
                if (anim != null && showAnim)
                    YoYo.with(anim).duration(300).playOn(tabList.get(i));
                imageViewList.get(i).setImageResource(selectedIcons[i]);
                textViewList.get(i).setTextColor(Color.parseColor("#265AA5"));
                textViewList.get(i).setText(tabTexts[i]);
            } else {
                imageViewList.get(i).setImageResource(normalIcons[i]);
                textViewList.get(i).setTextColor(Color.parseColor("#8B8989"));
                textViewList.get(i).setText(tabTexts[i]);
            }
        }
    }

}
