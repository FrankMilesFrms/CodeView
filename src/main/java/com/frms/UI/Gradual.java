/*
 * Copyright Frms
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.frms.UI;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * demo
 * 创建人 ： Frms
 * 创建人邮箱 ： 3505826836@qq.com
 * 创建时间 ：2020/2/14 15:23(ydt)
 */
public class Gradual extends LinearLayout
{
    private int animatedValue;
    private int colorEnd;
    private int colorStart;
    private Animator animator;
    
    public Gradual(Context context) {
        super(context);
        init();
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        setLayoutParams(lp);
        setBackgroundColor(Color.RED);
    }
    int width, height;
    
    
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);
        
        //获取View的宽高
        width = w;
        height = h;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            
            animator= ViewAnimationUtils.createCircularReveal(this, w/2, h/2,
                0,1111);
            animator.setDuration(2000);
            animator.start();
        }else
        {
            Toast.makeText(getContext(), "不支持的手机版本号", Toast.LENGTH_LONG).show();
        }
    }
    
    public void init() {
        postInvalidate();
        ValueAnimator animator=ValueAnimator.ofInt(0,255);
        animator.setDuration(10000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            //@RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                animatedValue = (int) animation.getAnimatedValue();
                if (animatedValue<255) {
                    colorStart = Color.rgb(255, animatedValue, 255 - animatedValue);
                    colorEnd = Color.rgb(animatedValue, 0, 255 - animatedValue);
                }else if (animatedValue==255){
                    ValueAnimator animator1=ValueAnimator.ofInt(0,255);
                    animator1.setDuration(2500);
                    animator1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        private int animatedValue1;
    
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            animatedValue1 = (int) animation.getAnimatedValue();
                            colorStart = Color.rgb(255- animatedValue1,255- animatedValue1, animatedValue1);
                            colorEnd = Color.rgb(255,0, animatedValue1);
                            if (animatedValue1==255){
                                ValueAnimator animator2=ValueAnimator.ofInt(0,255);
                                animator2.setDuration(2500);
                                animator2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                    @Override
                                    public void onAnimationUpdate(ValueAnimator animation) {
                                        int animatedValue2 = (int) animation.getAnimatedValue();
                                        colorStart = Color.rgb(animatedValue2,0,255);
                                        colorEnd = Color.rgb(255-animatedValue2,0,255);
                                        invalidate();
                                    }
                                });
                                animator2.start();
                            }
                            invalidate();
                        }
                    });
                    animator1.start();
                }
                invalidate();
            }
        });
        animator.start();
    }
   
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        Paint paint = new Paint();
        LinearGradient backGradient = new LinearGradient(width, 0, 0, height, new int[]{colorStart, colorEnd}, new float[]{0, 1f}, Shader.TileMode.CLAMP);
        paint.setShader(backGradient);
        canvas.drawRect(0, 0, width, height, paint);
    }
}
