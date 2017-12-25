package com.example.bill.systememojiproject;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

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
    private LinearLayout pageTurningPoint;
    private ViewPager viewPager;
    private int[] emojiCodes;

    private int mPendingShowPlaceHolder = 0;

    public CustomDialog(@NonNull Context context) {
        super(context);
    }

    public CustomDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        this.activity = (Activity) context;
        emojiCodes = activity.getResources().getIntArray(R.array.emoji_code);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.layout_dialog);
        manager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        initDialog(getContext());
        initView();
        showEmojiView();
        initPageTurningPoint();
    }

    private void initView() {
        pageTurningPoint = (LinearLayout) this.findViewById(R.id.page_turning_point);
        viewPager = (ViewPager) this.findViewById(R.id.view_pager);
        emojiLayout = this.findViewById(R.id.frame_emoji);
        editText = (EditText) this.findViewById(R.id.edit_text);
        keyBoardBtn = (Button) this.findViewById(R.id.btn_keyboard);
        cancelBtn = (Button) this.findViewById(R.id.btn_cancel);
        keyBoardBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                for (int i = 0; i < mPointViews.size(); i++) {
                    mPointViews.get(i).setImageResource(page_indicatorId[1]);
                    if (position != i) {
                        mPointViews.get(i).setImageResource(page_indicatorId[0]);
                    }
                }
            }
        });
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
                if (emojiLayout.getVisibility() == View.VISIBLE && isSoftInputShown()) {
                    emojiLayout.setVisibility(View.GONE);
                    return false;
                }
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

    private void initPageTurningPoint() {
        pageTurningPoint.removeAllViews();
        int length = 0;
        int quotient = emojiCodes.length / emojiPageNum;
        int remainder = emojiCodes.length % emojiPageNum;
        if (remainder > 0) {
            length = quotient + 1;
        }
        for (int count = 0; count < length; count++) {
            // 翻页指示的点
            ImageView pointView = new ImageView(getContext());
            pointView.setPadding(5, 0, 5, 0);
            if (mPointViews.isEmpty())
                pointView.setImageResource(page_indicatorId[1]);
            else
                pointView.setImageResource(page_indicatorId[0]);
            mPointViews.add(pointView);
            pageTurningPoint.addView(pointView);
        }

    }

    private int[] page_indicatorId = new int[]{R.drawable.emoji_point_normal_bg, R.drawable.emoji_point_press_bg};
    private ArrayList<ImageView> mPointViews = new ArrayList<>();
    private List<View> emojiLists;
    private int emojiPageNum = 20;

    private void showEmojiView() {
        if (emojiLists == null) {
            emojiLists = new ArrayList<>();
            int length = 0;
            int quotient = emojiCodes.length / emojiPageNum;
            int remainder = emojiCodes.length % emojiPageNum;
            if (remainder > 0) {
                length = quotient + 1;
            }
            for (int i = 0; i < length; i++) {
                emojiLists.add(getEmojiItemViews(i));
            }
            viewPager.setAdapter(new ExpressionPagerAdapter(emojiLists));
        }
    }

    private View getEmojiItemViews(int page) {
        List<EmojiEntity> list = new ArrayList<>();
        int start = emojiPageNum * page;
        int length = (page + 1) * emojiPageNum;
        for (int i = start; i < length; i++) {
            if (i < emojiCodes.length) {
                EmojiEntity entity = new EmojiEntity(getEmojiStringByUnicode(emojiCodes[i]));
                list.add(entity);
            } else {
                list.add(new EmojiEntity(""));
            }
        }
        list.add(new EmojiEntity(""));

        View view = View.inflate(activity, R.layout.layout_expression_gridview, null);
        ExpandGridView gridView = (ExpandGridView) view.findViewById(R.id.gridview);
        final ExpressionGridViewAdapter gridViewAdapter = new ExpressionGridViewAdapter(activity, list);
        gridView.setAdapter(gridViewAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int start = editText.getSelectionStart();
                int end = editText.getSelectionEnd();
                Editable editable = editText.getText();
                if (position == 20) {
                    if (editable.length() <= 0) {
                        return;
                    }
                    if (start == end) {
                        char lastStr = editable.charAt(editable.length() - 1);
                        int interval = 1;
                        if (!isEmojiCharacter(lastStr)) {
                            interval = 2;
                        }
                        editable.insert(start, gridViewAdapter.getItem(position).code);
                        editable.delete(start - interval, start);
                    } else {
                        editable.replace(start, end, "");
                    }
                } else {
                    if (start == end) {
                        editable.insert(start, gridViewAdapter.getItem(position).code);
                    } else {
                        editable.replace(start, end, gridViewAdapter.getItem(position).code);
                    }
                }
            }
        });

        return view;
    }

    private boolean isEmojiCharacter(char codePoint) {
        return (codePoint == 0x0) || (codePoint == 0x9) || (codePoint == 0xA) ||
                (codePoint == 0xD) || ((codePoint >= 0x20) && (codePoint <= 0xD7FF)) ||
                ((codePoint >= 0xE000) && (codePoint <= 0xFFFD)) || ((codePoint >= 0x10000)
                && (codePoint <= 0x10FFFF));
    }

    private String getEmojiStringByUnicode(int unicodeJoy) {
        return new String(Character.toChars(unicodeJoy));
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
            softInputHeight = softInputHeight - getSoftButtonsBarHeight();
        }

        if (softInputHeight < 0) {
            Log.w("Bill", "EmotionKeyboard--Warning: value of softInputHeight is below zero!");
        }
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
