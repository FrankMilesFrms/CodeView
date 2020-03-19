package com.frms.codeview.activity.view;



/**
 * 项目名称 ： app
 * 创建人 ： Frms, JunkChen
 * 创建人邮箱 ： 3505826836@qq.com
 * 创建时间 ：2020/3/18 21:55(ydt)
 * 实现一个可左滑删除的ListView
 */

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;

public class BookmarksListView extends ListView
{
    
    private int mScreenWidth;//屏幕的宽度
    private boolean isDeleteShow;//删除组件是否显示
    private ViewGroup mPointChild;//手指按下位置的item组件
    private int mDeleteWidth;//删除组件的宽度
    private LinearLayout.LayoutParams mItemLayoutParams;//手指按下时所在的item的布局参数
    
    private int mDownX;//手指初次按下的X坐标
    private int mDownY;//手指初次按下的Y坐标
    private int mPointPosition;//手指按下位置所在的item位置
    private boolean isAllowItemClick;//是否允许item点击
    private int clickIndex = 0;
    
    public BookmarksListView(Context context)
    {
        super(context);
        init(context);
    }
    
    public BookmarksListView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context);
    }
    
    public BookmarksListView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init(context);
    }
    
    private void init(Context context)
    {
        // 获取屏幕宽度
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        mScreenWidth = dm.widthPixels;
    }
    
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev)
    {
        if(getChildCount() <1)return super.onInterceptTouchEvent(ev);
        //事件拦截
        switch (ev.getAction()) {
            
            case MotionEvent.ACTION_DOWN:
                {
                    isAllowItemClick = true;
                    
                    //侧滑删除
                    mDownX = (int) ev.getX();
                    mDownY = (int) ev.getY();
                    mPointPosition = pointToPosition(mDownX, mDownY);
                    
                    
                    if (mPointPosition != -1) {
                        if (isDeleteShow)
                        {
                            ViewGroup tmpViewGroup = (ViewGroup) getChildAt(mPointPosition - getFirstVisiblePosition());
                            if (!mPointChild.equals(tmpViewGroup))
                            {
                                turnNormal();
                            }
                        }
                        //获取当前的item
                        mPointChild = (ViewGroup) getChildAt(mPointPosition - getFirstVisiblePosition());
                        
                        mDeleteWidth = mPointChild.getChildAt(1).getLayoutParams().width;
                        mItemLayoutParams = (LinearLayout.LayoutParams) mPointChild.getChildAt(0).getLayoutParams();
                        
                       
                        mItemLayoutParams.width = mScreenWidth;
                        mPointChild.getChildAt(0).setLayoutParams(mItemLayoutParams);
                    }
                    break;
                }
            case MotionEvent.ACTION_MOVE:
                {
                    int nowX = (int) ev.getX();
                    int nowY = (int) ev.getY();
                    int diffX = nowX - mDownX;
                    
                    
                    if (Math.abs(diffX) > dp2px(4) || Math.abs(nowY - mDownY) > dp2px(4))
                    {
                        return true;//避免子布局中有点击的控件时滑动无效
                    }
                    break;
                }
        }
        return super.onInterceptTouchEvent(ev);
    }
    
    public float dp2px(int dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
            getContext().getResources().getDisplayMetrics());
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent ev)
    {
        if(getChildCount() <1)return super.onTouchEvent(ev);
        
        //事件响应
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                performActionDown(ev);
                break;
            case MotionEvent.ACTION_MOVE:
                performActionMove(ev);
                break;
            case MotionEvent.ACTION_UP:
                performActionUp(ev);
                break;
        }
        return super.onTouchEvent(ev);
    }
    
    private void performActionDown(MotionEvent ev)
    {
        
        mDownX = (int) ev.getX();
        mDownY = (int) ev.getY();
        clickIndex = pointToPosition(mDownX, mDownY);
    
        if(clickIndex == AbsListView.INVALID_POSITION)
        {
            clickIndex = getChildCount() - 1;
        }
        
        if (isDeleteShow) {
            
            
            ViewGroup tmpViewGroup = (ViewGroup) getChildAt(clickIndex - getFirstVisiblePosition());
            
            if (!mPointChild.equals(tmpViewGroup)) {
                turnNormal();
            }
        }
        
        //获取当前的item
        mPointChild = (ViewGroup) getChildAt( clickIndex  - getFirstVisiblePosition());
        
        if(mPointChild != null)
        {
            mDeleteWidth = mPointChild.getChildAt(1).getLayoutParams().width; //获取删除组件的宽度
    
            mItemLayoutParams = (LinearLayout.LayoutParams) mPointChild.getChildAt(0).getLayoutParams();
    
            mItemLayoutParams.width = mScreenWidth;
    
            mPointChild.getChildAt(0).setLayoutParams(mItemLayoutParams);
        }
    }
    
    private boolean performActionMove(MotionEvent ev)
    {
        int nowX = (int) ev.getX();
        int nowY = (int) ev.getY();
        int diffX = nowX - mDownX;
        
        if (Math.abs(diffX) > Math.abs(nowY - mDownY) && Math.abs(nowY - mDownY) < 20)
        {
            if (!isDeleteShow && nowX < mDownX)
            {
                //删除按钮未显示时向左滑
                if (-diffX >= mDeleteWidth)
                {
                    //如果滑动距离大于删除组件的宽度时进行偏移的最大处理
                    diffX = -mDeleteWidth;
                }
                mItemLayoutParams.leftMargin = diffX;
                mPointChild.getChildAt(0).setLayoutParams(mItemLayoutParams);
                isAllowItemClick = false;
            } else if (isDeleteShow && nowX > mDownX) {
                
                //删除按钮显示时向右滑
                if (diffX >= mDeleteWidth) {
                    diffX = mDeleteWidth;
                }
                
                mItemLayoutParams.leftMargin = diffX - mDeleteWidth;
                mPointChild.getChildAt(0).setLayoutParams(mItemLayoutParams);
                isAllowItemClick = false;
            }
            return true;
        }
        return super.onTouchEvent(ev);
    }
    
    private void performActionUp(MotionEvent ev)
    {
        //如果向左滑出超过隐藏的二分之一就全部显示
        if (-mItemLayoutParams.leftMargin >= mDeleteWidth / 2)
        {
            mItemLayoutParams.leftMargin = -mDeleteWidth;
            isDeleteShow = true;
            mPointChild.getChildAt(0).setLayoutParams(mItemLayoutParams);
        } else {
            turnNormal();
        }
    }
    
    /**
     * 转换为正常隐藏情况
     */
    public void turnNormal()
    {
        mItemLayoutParams.leftMargin = 0;
        isDeleteShow = false;
        if(mPointChild != null)
        {
            mPointChild.getChildAt(0).setLayoutParams(mItemLayoutParams);
        }
    }
    
    /**
     * 是否允许Item点击
     *
     * @return
     */
    public boolean isAllowItemClick()
    {
        return isAllowItemClick;
    }

}