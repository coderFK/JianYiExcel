package com.app.jianyiexcel.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

// 一个视图容器控件
// 阻止 拦截 ontouch事件传递给其子控件
public class LinearLayoutScrollContainer extends LinearLayout {

	public LinearLayoutScrollContainer(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}


	public LinearLayoutScrollContainer(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		return true;
	}

	
}
