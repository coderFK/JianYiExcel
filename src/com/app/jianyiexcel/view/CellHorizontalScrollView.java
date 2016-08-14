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
	

	//�����¼�
	public boolean onTouchEvent(MotionEvent ev) {
		return super.onTouchEvent(ev);
	}
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		/*
		 * ���������ƶ������� �����¼���֪ͨ���۲��ߣ��۲��߻ᴫ�����������Ŀ��
		 */
		if(scrollViewOberver!=null){
			scrollViewOberver.NotifyOnScrollChanged(l, t, oldl, oldt);
		}
		super.onScrollChanged(l, t, oldl, oldt);
	}
	//��ӹ��������¼�
	public void addOnScrollChangedListener(CellOnScrollChangedListener listener){
		scrollViewOberver.AddOnScrollChangedListener(listener);
	}
	//ȡ�����������¼�
	public void removeOnScrollChangedListener(CellOnScrollChangedListener listener){
		scrollViewOberver.AddOnScrollChangedListener(listener);
	}
	
	//�����ӿڣ����������¼�
	public static interface CellOnScrollChangedListener{
		public void onScrollChanged(int l, int t, int oldl, int oldt);
	}
	
	//�۲���
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
		//��������һ���Ƿ�������������������һ�����
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
