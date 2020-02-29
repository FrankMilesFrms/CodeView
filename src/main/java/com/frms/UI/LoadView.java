package com.frms.UI;


import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.logging.Handler;

/**
 * demo
 * 创建人 ： Frms
 * 创建人邮箱 ： 3505826836@qq.com
 * 创建时间 ：2020/2/14 14:22(ydt)
 */
public class LoadView
{
    
    public static Gradual load(final Context context,
                               String title,
                               final String subTitle, final int subLongTime,
                               final String sub2Title,final int sub2LongTime,
                               final String endTitle,

                               String firstTipText, int firstTipLongTime,
                               final String loadTipText, final int loadTipLongTime,
                               final String doneTipText, final int doneLongTime,
                               final View.OnClickListener onClickListener)
    {
        final Gradual gradual = new Gradual(context);
                      gradual.setOrientation(LinearLayout.VERTICAL);
        final float size ;
        final TextView textView = tipText(context, firstTipText);
        final TextView t = new TextView(context);
                 t.setText(title);
                 t.setTextColor(Color.WHITE);
                 t.setTypeface(Typeface.DEFAULT_BOLD);
                 t.setTextSize(70);
                 t.animate().scaleXBy(-0.3f).scaleYBy(-0.3f).setDuration(1000);
                 t.setGravity(Gravity.CENTER);
                 gradual.setGravity(Gravity.CENTER);
                 t.animate().translationYBy(-40).setDuration(1500);
                 t.postDelayed(new Runnable()
                 {
                     @Override
                     public void run()
                     {
                         t.setText(subTitle);
                         setShowAnimation(t, 1500);
                         t.postDelayed(new Runnable()
                         {
                             @Override
                             public void run()
                             {
                                 t.setText(sub2Title);
                                 setShowAnimation(t, 1500);
                                 textView.setText("");
                                 t.postDelayed(new Runnable()
                                 {
                                     @Override
                                     public void run()
                                     {
                                         t.setText(endTitle);
                                         setShowAnimation(t, 1500);
                                         textView.setText(doneTipText);
                                         setShowAnimation(textView, 1500);
                                         t.postDelayed(new Runnable()
                                         {
                                             @Override
                                             public void run()
                                             {
                                                 textView.setText("Tap button to continue...");
                                                 setShowAnimation(textView, 1500);
                                                 Button nb = nbButton(context, "Start", textView.getWidth()/2, textView.getHeight()*2, onClickListener);
            
                                                 setShowAnimation(nb, 1700);
                                                 gradual.addView(nb);
                                             }
                                         }, doneLongTime);
                                     }
                                 }, doneLongTime);
                             }
                         }, subLongTime);
                     }
                 }, firstTipLongTime + loadTipLongTime);
        
        gradual.addView(t);
        
                 textView.animate().translationYBy(-20).setDuration(50);
                 textView.setText(firstTipText);
                 textView.setTextSize(20);
                 textView.setTextColor(Color.WHITE);
                 textView.setText(firstTipText);
                 textView.setGravity(Gravity.CENTER);
                 textView.setTypeface(Typeface.DEFAULT_BOLD);
                 setShowAnimation(textView, 1500);
                 
                 textView.postDelayed(new Runnable()
                 {
                     @Override
                     public void run()
                     {
                            textView.setText(loadTipText);
                            setShowAnimation(textView, 1500);
                            textView.postDelayed(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                        textView.setText("");
                                }
                            }, loadTipLongTime);
                     }
                 }, firstTipLongTime);
        gradual.addView(textView);
        
        
        
        return gradual;
    }
    
    private static TextView tipText(Context context, String string)
    {
        TextView t = new TextView(context);
                 t.setText(string);
                 setShowAnimation(t, 1000);
        return t;
    }
    
    /**
     * View渐现动画效果
     */
    public static void setShowAnimation(View view, int duration)
    {
    
        AlphaAnimation mShowAnimation = new AlphaAnimation(0.0f, 1.0f);
        mShowAnimation.setDuration(duration);
        mShowAnimation.setFillAfter(true);
        view.startAnimation(mShowAnimation);
    }
    
    
    
    private static void move(View view, int duration, int afterX, int afterY)
    {
        TranslateAnimation translateAnimation = new TranslateAnimation(0, afterX, 0, afterY);
        translateAnimation.setDuration(duration);
        translateAnimation.setFillAfter(true);
        view.startAnimation(translateAnimation);
    }
    
    @SuppressLint("ClickableViewAccessibility")
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private static Button nbButton (Context cx, String text, final int w, final int h, View.OnClickListener onClickListener)
    {
        final boolean[] isRun = {false};
        final Button button = new Button(cx);
        button.setTextSize(24);
        final GradientDrawable backDrawable = new GradientDrawable();
        backDrawable.setCornerRadius(120);
        backDrawable.setColor(Color.rgb(216, 202, 175));
        button.setBackground(backDrawable);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(w, h);
        button.setText(text);
        button.setLayoutParams(lp);
        button.setTextColor(Color.BLACK);
        button.setOnClickListener(onClickListener);
        
        button.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if (!isRun[0] && event.getAction() == MotionEvent.ACTION_DOWN) {
                    ((Button)v).setText("✔");
                    
                    ValueAnimator valueAnimator = ValueAnimator.ofInt(w, h);
                    valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
                    {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation)
                        {
                            int value = (int) animation.getAnimatedValue();
                            int leftOffset = (w - value) / 2;
                            int rightOffset = w - leftOffset;
    
                            backDrawable.setBounds(leftOffset, 0, rightOffset, h);
                            isRun[0] = true;
                        }
    
                    });
                    ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(backDrawable, "cornerRadius", 120, h/2);
                    AnimatorSet animatorSet = new AnimatorSet();
                    animatorSet.setDuration(800);
                    animatorSet.playTogether(valueAnimator, objectAnimator);
                    animatorSet.start();
                    button.postDelayed(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            move(button, 300, 0, h + w);
                        }
                    }, 1700);
                }
                return false;
            }
        });
        
        return button;
        
    }
}
