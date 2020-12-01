package com.lijangop.commoncode.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.lijangop.commoncode.animate.KickBackAnimator;
import com.lijangop.commoncode.R;
import com.lijangop.sdk.widget.bottomnavigationview.Anim;
import com.lijangop.sdk.widget.bottomnavigationview.BottomNavigationView;
import com.lijangop.sdk.widget.bottomnavigationview.OnCenterTabClickListener;
import com.lijangop.sdk.widget.bottomnavigationview.OnTabClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * 测试Project
 */
public class MainActivity extends AppCompatActivity {
    private String[]       tabTitles   = {"首页", "发现", "消息", "我的"};
    private int[]          normalIcons = {R.drawable.home, R.drawable.find, R.drawable.message, R.drawable.mine};
    private int[]          selectIcons = {R.drawable.home_, R.drawable.find_, R.drawable.message_, R.drawable.mine_};
    private List<Fragment> fragments   = new ArrayList();

    //仿微博 模块
    private Handler              mHandler      = new Handler();
    private BottomNavigationView mBnv;
    private View                 cancelImageView;
    private int[]                menuIconItems = {R.mipmap.pic1, R.mipmap.pic2, R.mipmap.pic3, R.mipmap.pic4};
    private String[]             menuTextItems = {"文字", "拍摄", "相册", "直播"};
    private LinearLayout         mMenuLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //data
        for (int i = 0; i < 4; i++) {
            ExampleFragment exampleFragment = new ExampleFragment();
            Bundle bundle = new Bundle();
            bundle.putString("content", String.valueOf(i));
            exampleFragment.setArguments(bundle);
            fragments.add(exampleFragment);
        }
        //init
        mBnv = findViewById(R.id.bnv);
        mBnv.fragment(fragments)
                .tabText(tabTitles)
                .tabIcon(normalIcons)
                .tabAnimation(Anim.ZoomIn)
                .tabSelectedIcon(selectIcons)
                .fragmentManager(getSupportFragmentManager())
                .setOnTabClickListener(new OnTabClickListener() {
                    @Override
                    public boolean interceptFromSelectEvent(View view, int position) {
                        if (position == 3) {
                            Toast.makeText(MainActivity.this, "请登录", Toast.LENGTH_SHORT).show();
                            return true;
                        }
                        return false;
                    }
                })
                .centerImageRes(R.drawable.hatch_add, new OnCenterTabClickListener() {
                    @Override
                    public void onClick() {
                        showMenu();
                    }
                })
                .build();
        mBnv.setAddViewLayout(createWeiboView());
    }

    //仿微博弹出菜单
    private View createWeiboView() {
        ViewGroup view = (ViewGroup) View.inflate(MainActivity.this, R.layout.layout_add_view, null);
        mMenuLayout = view.findViewById(R.id.icon_group);
        cancelImageView = view.findViewById(R.id.cancel_iv);
        cancelImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeAnimation();
            }
        });
        for (int i = 0; i < 4; i++) {
            View itemView = View.inflate(MainActivity.this, R.layout.item_icon, null);
            ImageView menuImage = itemView.findViewById(R.id.menu_icon_iv);
            TextView menuText = itemView.findViewById(R.id.menu_text_tv);

            menuImage.setImageResource(menuIconItems[i]);
            menuText.setText(menuTextItems[i]);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            params.weight = 1;
            itemView.setLayoutParams(params);
            itemView.setVisibility(View.GONE);
            mMenuLayout.addView(itemView);
        }
        return view;
    }

    private void showMenu() {
        startAnimation();
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                //＋ 旋转动画
                cancelImageView.animate().rotation(90).setDuration(400);
            }
        });
        //菜单项弹出动画
        for (int i = 0; i < mMenuLayout.getChildCount(); i++) {
            final View child = mMenuLayout.getChildAt(i);
            child.setVisibility(View.INVISIBLE);
            mHandler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    child.setVisibility(View.VISIBLE);
                    ValueAnimator fadeAnim = ObjectAnimator.ofFloat(child, "translationY", 600, 0);
                    fadeAnim.setDuration(500);
                    KickBackAnimator kickAnimator = new KickBackAnimator();
                    kickAnimator.setDuration(500);
                    fadeAnim.setEvaluator(kickAnimator);
                    fadeAnim.start();
                }
            }, i * 50 + 100);
        }
    }

    private void startAnimation() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    //圆形扩展的动画
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                        int x = ScreenUtils.getScreenWidth() / 2;
                        int y = ScreenUtils.getScreenHeight() - SizeUtils.dp2px(25);
                        Animator animator = ViewAnimationUtils.createCircularReveal(mBnv.getAddViewLayout(), x,
                                y, 0, mBnv.getAddViewLayout().getHeight());
                        animator.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationStart(Animator animation) {
                                mBnv.getAddViewLayout().setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                //							layout.setVisibility(View.VISIBLE);
                            }
                        });
                        animator.setDuration(300);
                        animator.start();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 关闭window动画
     */
    private void closeAnimation() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                cancelImageView.animate().rotation(0).setDuration(400);
            }
        });
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                int x = ScreenUtils.getScreenWidth() / 2;
                int y = ScreenUtils.getScreenHeight() - SizeUtils.dp2px(25);
                Animator animator = ViewAnimationUtils.createCircularReveal(mBnv.getAddViewLayout(), x,
                        y, mBnv.getAddViewLayout().getHeight(), 0);
                animator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        //							layout.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mBnv.getAddViewLayout().setVisibility(View.GONE);
                        //dismiss();
                    }
                });
                animator.setDuration(300);
                animator.start();
            }
        } catch (Exception e) {
        }
    }
}
