package com.example.bill.systememojiproject;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by yuanweibiao on 2017/12/24.
 */

public class CustomDialog extends Dialog implements View.OnClickListener {

    private Activity activity;
    private InputMethodManager manager;
    private EditText editText;
    private Button keyBoardBtn;
    private Button cancelBtn;
    private View emojiLayout;

    private int mPendingShowPlaceHolder = 0;

    public CustomDialog(@NonNull Context context) {
        super(context);
    }

    public CustomDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        this.activity = (Activity) context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.layout_dialog);
        manager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        initDialog(getContext());
        initView();
    }

    private void initView() {
        emojiLayout = this.findViewById(R.id.frame_emoji);
        editText = (EditText) this.findViewById(R.id.edit_text);
        keyBoardBtn = (Button) this.findViewById(R.id.btn_keyboard);
        cancelBtn = (Button) this.findViewById(R.id.btn_cancel);
        keyBoardBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
        editText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (emojiLayout.getVisibility() == View.VISIBLE)
                    emojiLayout.setVisibility(View.GONE);
                return false;
            }
        });
        editText.getViewTreeObserver().addOnPreDrawListener(listener);
    }

    private ViewTreeObserver.OnPreDrawListener listener = new ViewTreeObserver.OnPreDrawListener() {

        @Override
        public boolean onPreDraw() {
            if (mPendingShowPlaceHolder == 0) {
                /*if (emojiLayout.getVisibility() == View.VISIBLE && isSoftInputShown()) {
                    emojiLayout.setVisibility(View.GONE);
                    return false;
                }*/
            } else {
                if (isSoftInputShown()) {
                    /*ViewGroup.LayoutParams params = emojiLayout.getLayoutParams();
                    int distance = getSupportSoftInputHeight();
                    // 调整PlaceHolder高度
                    if (distance != params.height) {
                        params.height = distance;
                        emojiLayout.setLayoutParams(params);
                    }*/
                    return false;
                } else {
                    if (mPendingShowPlaceHolder == 1) {
                        emojiLayout.setVisibility(View.VISIBLE);
                        mPendingShowPlaceHolder = 0;
                    }
                    return false;
                }

            }
            return true;
        }
    };

    private void initDialog(Context context) {
        this.setCanceledOnTouchOutside(true);
        WindowManager windowManager = this.getWindow().getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = this.getWindow().getAttributes();
        lp.width = display.getWidth();
        lp.gravity = Gravity.BOTTOM;
        this.getWindow().setAttributes(lp);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_keyboard:
                mPendingShowPlaceHolder = 1;
                hideSoftInput(editText);
                break;
            case R.id.btn_cancel:
                if (this.isShowing()) {
                    this.dismiss();
                }
                break;

        }
    }

    public void showDialog() {
        if (!this.isShowing()) {
            if (editText != null) {
                editText.getViewTreeObserver().addOnPreDrawListener(listener);
            }
            this.show();
        }
    }

    public static class Builder {
        public Activity activity;

        public Builder(Activity activity) {
            this.activity = activity;
        }

        public CustomDialog create() {
            CustomDialog dialog = new CustomDialog(activity, R.style.NoAnimBottom);
            return dialog;
        }
    }

    /**
     * 编辑框获取焦点，并显示软件盘
     */
    private void showSoftInput(View view) {
        view.requestFocus();
        manager.showSoftInput(view, 0);
    }

    /**
     * 隐藏软件盘
     */
    private void hideSoftInput(View view) {
        manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * 是否显示软件盘
     *
     * @return
     */
    private boolean isSoftInputShown() {
        return getSupportSoftInputHeight() != 0;
    }

    /**
     * 获取软件盘的高度
     *
     * @return
     */
    private int getSupportSoftInputHeight() {
        Rect r = new Rect();
        /**
         * decorView是window中的最顶层view，可以从window中通过getDecorView获取到decorView。
         * 通过decorView获取到程序显示的区域，包括标题栏，但不包括状态栏。
         */
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
        //获取屏幕的高度
        int screenHeight = activity.getWindow().getDecorView().getRootView().getHeight();
        //计算软件盘的高度
        int softInputHeight = screenHeight - r.bottom;

        /**
         * 某些Android版本下，没有显示软键盘时减出来的高度总是144，而不是零，
         * 这是因为高度是包括了虚拟按键栏的(例如华为系列)，所以在API Level高于20时，
         * 我们需要减去底部虚拟按键栏的高度（如果有的话）
         */
        if (Build.VERSION.SDK_INT >= 20) {
            // When SDK Level >= 20 (Android L), the softInputHeight will contain the height of softButtonsBar (if has)
//            Log.d("Bill", "getSoftButtonsBarHeight:" + getSoftButtonsBarHeight());
            softInputHeight = softInputHeight - getSoftButtonsBarHeight();
        }

        if (softInputHeight < 0) {
//            Log.w("Bill", "EmotionKeyboard--Warning: value of softInputHeight is below zero!");
        }
//        Log.d("Bill", "softInputHeight:" + softInputHeight);
        return softInputHeight;
    }

    /**
     * 底部虚拟按键栏的高度
     *
     * @return
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private int getSoftButtonsBarHeight() {
        DisplayMetrics metrics = new DisplayMetrics();
        //这个方法获取可能不是真实屏幕的高度
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int usableHeight = metrics.heightPixels;
        //获取当前屏幕的真实高度
        activity.getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        int realHeight = metrics.heightPixels;
        if (realHeight > usableHeight) {
            return realHeight - usableHeight;
        } else {
            return 0;
        }
    }

}
