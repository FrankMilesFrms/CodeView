package com.frms.codeview.tools;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;

import com.frms.UI.LoadView;
import com.frms.codeview.CodeView;
import com.frms.codeview.R;
import com.frms.codeview.activity.MainEditActivity;
import com.frms.lexer.TAG;

import java.util.ArrayList;

/**
 * 这是一个针对编辑器的ui插件，集成放大镜等功能，降低onDraw绘制时间，可以启用/关闭，不可移除。
 * @author  ： Frms, 3505826836@qq.com
 * 创建时间 ：2020/2/21 9:56(ydt)
 */

public class PluginUI
{
    private final AlphaAnimation mShowAnimation;
    private final CodeView codeview;
    private int width;
    private final AppCompatActivity mActivity;
    private View parent;
    
    private int rowheigth;
    private PopupWindow mPopupWindow;
    private ImageView mImageView;
    private boolean isShowMagnifier = false;
    
    private boolean isShowAuto = false;
    private PopupWindow autoPop;
    private ListView listView;
    private String[] i, newLetter;
    
    private TextView textView;// 统计布局
    private ScrollView recordLayout;
    private ImageView mVerticalScrollIMG;
    
    private int mCompensationValue = -1;// 显示位置的补偿值
    @SuppressLint("ResourceType")
    public PluginUI(AppCompatActivity activity, int width, CodeView cv)
    {
        mActivity = activity;
        this.width = width;
        parent = mActivity.getWindow().getDecorView();
        
        mShowAnimation = new AlphaAnimation(1.0f, 0.0f);
        mShowAnimation.setFillAfter(true);
        codeview = cv;
        
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        String s = sharedPreferences.getString("preference_auto_text", "");
        
        if(s.contains("\n") && MainEditActivity.mAuto)
        {
            //TODO @Error? '[\n]' > '\n'
            newLetter = s.split("[\n]");
        }else
        {
            newLetter = new String[0];
        }
    
        if(mCompensationValue < 0)
        {
            mCompensationValue = rowheigth * 2 + mActivity.getSupportActionBar().getHeight();
        }
    }
    
    /**
     * 启用放大镜，一定要放在加载完布局后调用！
     */
    public void canShowMagnifier()
    {
        isShowMagnifier = true;
        
        mPopupWindow = new PopupWindow();
        mImageView = new ImageView(mActivity);
        
        mPopupWindow.setFocusable(false);
        mPopupWindow.setContentView(mImageView);
        mPopupWindow.setWidth((int) (width / 2.5));
        mPopupWindow.setHeight(rowheigth * 4);
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setCornerRadius(30);
        mPopupWindow.setBackgroundDrawable(gradientDrawable);
    }
    
    /**
     * 显示放大镜
     * @param x
     * @param y
     * @param bitmap
     */
    public void showMagnifier(int x, int y, Bitmap bitmap)
    {
        if(isShowMagnifier)
        {
            y -= rowheigth;
            x -= width/5;
    
            if(y < 0)y=0;
            if(x < 0)x = 0;
    
            mImageView.setImageBitmap(bitmap);
            mPopupWindow.showAtLocation(parent, Gravity.LEFT | Gravity.TOP, x, y);
            mPopupWindow.update(x, y, -1, -1);
        }
        
    }
    
    /**
     * 关闭放大镜
     */
    public void dismissMagnifier() {
        if(isShowMagnifier)
        {
            if(mPopupWindow.isShowing())
            {
                mPopupWindow.dismiss();
            }
        }
        
    }
    
    /**
     * 设置文本行高度，用于适配放大镜，必须参数。
     * @param rowheigth
     */
    public void setRowheigth(int rowheigth)
    {
        this.rowheigth = rowheigth;
    }
    
    
    /**
     * @param view
     * @param duration
     */
    private void setHideAnimation(View view, int duration)
    {
        mShowAnimation.setDuration(duration);
        view.startAnimation(mShowAnimation);
    }
    
    String[] cache;
    private ArrayList<String> string;
    private int length = 0, position = 0, line ;
    
    /**
     * 加载提示框，一定要放在加载完布局后调用！
     * @param language
     */
    public void canAutomaticCompletion(int language)
    {
//        cache = switch (language)
//        {
//            case CodeView.LANGUAGE_NATIVE_JAVA -> TAG.JavaScript_keyWords;
//            case CodeView.LANGUAGE_NATIVE_JAVASCRIPT -> TAG.JAVA_KEYWORD;
//        }
        
        switch (language)
        {
            case CodeView.LANGUAGE_NATIVE_JAVA:
                cache = TAG.JAVA_KEYWORD;
                break;
            case CodeView.LANGUAGE_NATIVE_JAVASCRIPT:
                cache = TAG.JavaScript_keyWords;
                break;
            default:
                cache = new String[]{};
        }
        
        isShowAuto = true;
        string = new ArrayList<>();
        string.add("");
        mPopupWindow = new PopupWindow();
    
        listView = new ListView(mActivity);
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(mActivity, android.R.layout.simple_list_item_1, cache);
        listView.setAdapter(adapter);
        
        listView.setOnItemClickListener((parent, view, p, id) ->
        {
            autoPop.dismiss();
            codeview.delete(position - length , position, false, line, line, true);
            codeview.insert(position - length , i[p], false, line, line, true);
            
        });
    
        autoPop = new PopupWindow();
    
        autoPop.setFocusable(false);
        autoPop.setOutsideTouchable(true);
        autoPop.setContentView(listView);
        autoPop.setWidth(width);
        autoPop.setHeight(width/3);
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setCornerRadius(50);
        mPopupWindow.setBackgroundDrawable(gradientDrawable);
    }
    
    /**
     * 显示并提示对应文本
     * 这里只是一个小demo，没做任何性能优化，同理，我们只对在屏幕同步的光标移位。
     * @param y 绘制文字的 y坐标位置
     * @param ey 屏幕底部坐标
     * @param c 加入的文字
     * @param mc 总文字
     * @param p 所在加入文字的位置
     * @param l 所在加入文字的行位置
     */
    public void showAuto(int y, int ey, char c, char[] mc, int p, int l)
    {
        if(!isShowAuto || !Character.isLetter(c))return;
    
        String t = String.valueOf(c);
        position = p;
        p -=2;
        length = 1;
        
        while (p > 0)
        {
            c = mc[--p];
            if(Character.isLetter(c))
            {
                t = c + t;
                length++;
            }
            else
                break;
        }
        string.clear();
        
        for(String o : cache)
        {
            if(o.toLowerCase().contains(t.toLowerCase()))
                string.add(o);
        }
    
        if(newLetter.length > 0)
        {
            for(String o : newLetter)
            {
                if(o.toLowerCase().contains(t.toLowerCase()))
                    string.add(o);
            }
        }
        
        
        if(string.size() == 0)return;
        
        line = l;
        i = new String[string.size()];
        string.toArray(i);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(mActivity, android.R.layout.simple_list_item_1, i);
        listView.setAdapter(adapter);
        autoPop.setContentView(listView);
        
        if(ey - y - width/3 < 0)
            codeview.scrollBy(0, width/3 - ey + y);
        
        
        if(codeview.getScrollY() > y)
        {
            codeview.scrollTo(codeview.getScrollX(), Math.max(codeview.getScrollY() - y - rowheigth, 0));
        }
        
        int sy = y - codeview.getScrollY();
        
        LoadView.setShowAnimation(listView, 200);
        
        autoPop.showAtLocation(parent, Gravity.LEFT | Gravity.TOP, 0, sy + mCompensationValue);
    }
    
    /**
     * 显示统计记录
     * @param totalLength
     * @param totalLineCounts
     * @param c
     */
    @SuppressLint("SetTextI18n")
    public void showRecord(int totalLength, int totalLineCounts, int[] c)
    {
        int index = -2;// 这里不算 TAG.EOL
        
        if(totalLength > 0)
        {
            for(int i : c)
            {
                if(i > 0){
                    index++;
                }
            }
        }else {
            index = 0;
        }
        
    
        LinearLayout linearLayout = new LinearLayout(mActivity);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
    
        textView = new TextView(mActivity);
        textView.setTypeface(codeview.getTypeface());
    
        linearLayout.addView(textView);
    
        HorizontalScrollView horizontalScrollView = new HorizontalScrollView(mActivity);
        horizontalScrollView.addView(linearLayout);
    
        recordLayout = new ScrollView(mActivity);
        recordLayout.addView(horizontalScrollView);
        
        textView.setText("总字符数 = "+ totalLength +"(约"+totalLength/1024+"KB，"+totalLength/1024/1024+"MB，非真实大小)\n" +
                         "总记行数 = "+ totalLineCounts+"\n" +
                         "异字符数 = "+ index+"\n");
        
        
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mActivity);
        alertDialog.setTitle("统计");
        alertDialog.setNegativeButton("关闭", null);
        alertDialog.setView(recordLayout);
        alertDialog.show();
    }
    
    private PopupWindow mVerticalScrollBar;
    
    /**
     * 已过时
     * 创建垂直显示条，一定要放在加载完布局后调用！
     */
    public void canVerticalScrollBar()
    {
        mVerticalScrollBar = new PopupWindow();
        mVerticalScrollIMG = new ImageView(mActivity);
        mVerticalScrollIMG.setBackgroundResource(R.drawable.a_ve);
        
        mVerticalScrollBar.setFocusable(false);
        mVerticalScrollBar.setHeight(codeview.getHeight() / 8);
        mVerticalScrollBar.setWidth(codeview.getWidth() / 16);
        mVerticalScrollBar.setContentView(mVerticalScrollIMG);
        // mVerticalScrollBar.showAtLocation(parent, Gravity.RIGHT | Gravity.LEFT, 0, 0);
        mVerticalScrollBar.showAtLocation(parent,
            Gravity.RIGHT | Gravity.TOP,
            codeview.getHeight() / 8,
            mCompensationValue);
    }
    
    /**
     * 已过时
     * 更新位置
     * @param position
     */
    public void setUpdateVerticalScrollBar(int position)
    {
        if(mVerticalScrollBar == null)return;
        
        if(mVerticalScrollBar.isShowing())
        {
            mVerticalScrollBar.update(
                codeview.getCharChineseUtilWidth() * 3,
                Math.round(codeview.getWidth() / position) * codeview.getHeight() + mCompensationValue - rowheigth * 2,
                -1,
                -1);
            return;
        }
        
        mVerticalScrollBar.showAtLocation(
            parent,
            codeview.getCharChineseUtilWidth() * 3,
            Math.round(codeview.getWidth() / position) * codeview.getHeight() + mCompensationValue - rowheigth * 2,
            Math.round(codeview.getWidth() / position));
    }
    
    /**
     * 已过时，不可用。
     * 关闭垂直。
     */
    public void dismissVerticalScrollBar()
    {
        if(mVerticalScrollBar == null || !mVerticalScrollBar.isShowing())return;
        mVerticalScrollBar.dismiss();
    }
}
