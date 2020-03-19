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

package com.frms.codeview.activity.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 项目名称 ： app
 * 创建人 ： Frms
 * 创建人邮箱 ： 3505826836@qq.com
 * 创建时间 ：2020/3/19 17:28(ydt)
 */
public class EditSymolBar extends LinearLayout
{
    public static String[] symbols = {};
    public static final String defaultSymbol = "\t{}():,.+\"\\|&![]<>+-/*?:_";
    private onClick onclick;
    
    public EditSymolBar(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        refresh(context);
    }
    
    public EditSymolBar(Context context) {
        super(context);
        refresh(context);
    }
    
    public void init()
    {
        if(onclick == null)
        {
            onclick = new onClick()
            {
                @Override
                public void run(String str)
                {
        
                }
            };
        }
    }
    
    private void refresh(Context cx)
    {
        if(symbols.length == 0)
        {
            symbols = defaultSymbol.split("");
        }
        
        removeAllViews();
        Button button;
        
        for(String t : symbols)
        {
            if(t .equals("\t") )
            {
                t = "\\t";
            }
            button = new Button(cx);
            button.setBackgroundColor(0);
            button.setText(t);
            button.setOnClickListener(new OnClickListener()
            {
                String text;
                @Override
                public void onClick(View v)
                {
                    text = ((TextView)v).getText().toString();
                    
                    if(text.equals("\\t"))
                    {
                        onclick.run("\t");
                    }else
                    {
                        onclick.run(text);
                    }
                }
            });
            addView(button);
        }
    }
    
    public interface onClick
    {
        void run(String str);
    }
    
    public void setOnClick(onClick click)
    {
        this.onclick = click;
    }
}
