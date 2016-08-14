package com.app.jianyiexcel.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;


public class CellHorizontalScrollView extends HorizontalScrollView {
	ScrollViewOberver scrollViewOberver=new ScrollViewOberver();
	public CellHorizontalScrollView(Context context) {
		super(context);
	}
	public CellHorizontalScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	public CellHorizontalScrollView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}
	

	//触摸事件
	public boolean onTouchEvent(MotionEvent ev) {
		return super.onTouchEvent(ev);
	}
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		/*
		 * 当滚动条移动后，引发 滚动事件。通知给观察者，观察者会传达给其他的项目。
		 */
		if(scrollViewOberver!=null){
			scrollViewOberver.NotifyOnScrollChanged(l, t, oldl, oldt);
		}
		super.onScrollChanged(l, t, oldl, oldt);
	}
	//添加滚动监听事件
	public void addOnScrollChangedListener(CellOnScrollChangedListener listener){
		scrollViewOberver.AddOnScrollChangedListener(listener);
	}
	//取消滚动监听事件
	public void removeOnScrollChangedListener(CellOnScrollChangedListener listener){
		scrollViewOberver.AddOnScrollChangedListener(listener);
	}
	
	//监听接口，监听滚动事件
	public static interface CellOnScrollChangedListener{
		public void onScrollChanged(int l, int t, int oldl, int oldt);
	}
	
	//观察者
	public static class ScrollViewOberver{
		List<CellOnScrollChangedListener> list_listener;

		public ScrollViewOberver() {
			super();
			list_listener=new ArrayList<CellOnScrollChangedListener>();
		}
		public void AddOnScrollChangedListener(CellOnScrollChangedListener listener){
			list_listener.add(listener);
		}
		public void RemoveOnScrollChangedListener(CellOnScrollChangedListener listener){
			list_listener.remove(listener);
		}
		//监听其中一行是否滚动，是则带动其他行一起滚动
		public void NotifyOnScrollChanged(int l, int t, int oldl, int oldt){
			if(list_listener==null||list_listener.size()==0){
				return;
			}
			for (int i = 0; i < list_listener.size(); i++) {
				if(list_listener.get(i)!=null){
					list_listener.get(i).onScrollChanged(l, t, oldl, oldt);
				}
			}
		}
		
	}

}
