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

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.widget.Button;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 项目名称 ： app
 * 创建人 ： Frms
 * 创建人邮箱 ： 3505826836@qq.com
 * 创建时间 ：2020/4/6 21:10(ydt)
 */
public class Other
{
    private ValueAnimator valueAnimator;
    private ObjectAnimator objectAnimator;
    
    @SuppressLint("SetTextI18n")
    public void function1(Context context)
    {
        AtomicInteger w = new AtomicInteger();
        AtomicInteger h = new AtomicInteger();
        
        GradientDrawable gradientDrawable =new GradientDrawable();
        gradientDrawable.setCornerRadius(120);
        gradientDrawable.setColor(Color.BLACK);
        final Button button = new Button(context);
        button.setText("        Frms        ");
        button.setOnClickListener((v)  -> {
            w.set(button.getWidth());
            h.set(button.getHeight());
    
             valueAnimator = ValueAnimator.ofInt(w.get(), h.get());
             valueAnimator.addUpdateListener((animation -> {
                 int value = (int) animation.getAnimatedValue();
                 int l = (w.get() - value)/2;
                 int r = w.get() - l;
                 gradientDrawable.setBounds(l, 0, r, h.get());
             }));
    
            objectAnimator = ObjectAnimator.ofFloat(gradientDrawable,"cornerRadius",120, h.get() / 2);
            objectAnimator.setRepeatMode(ObjectAnimator.REVERSE);
            objectAnimator.setRepeatCount(1);
            objectAnimator.start();
            
            valueAnimator.setRepeatCount(1);
            valueAnimator.setRepeatMode(ValueAnimator.REVERSE);
            valueAnimator.start();
        });
    }
}
