package com.example.bill.systememojiproject;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * Created by 卫彪 on 2017/12/25.
 */
public class ExpandGridView extends GridView {

	public ExpandGridView(Context context) {
		super(context);
	}
	
	public ExpandGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}

}
