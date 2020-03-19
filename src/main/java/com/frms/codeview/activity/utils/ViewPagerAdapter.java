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

package com.frms.codeview.activity.utils;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.frms.codeview.CodeView;

import java.util.ArrayList;

/**
 * 项目名称 ： app
 * 创建人 ： Frms
 * 创建人邮箱 ： 3505826836@qq.com
 * 创建时间 ：2020/3/16 23:47(ydt)
 */
public class ViewPagerAdapter extends PagerAdapter
{
    private final ArrayList<String> title;
    private final ArrayList<CodeView> layout;
    
    public ViewPagerAdapter(ArrayList<String> tabFileName, ArrayList<CodeView> tabEditView)
    {
        super();
        this.title = tabFileName;
        this.layout = tabEditView;
    }
    
    
    @Override
    public int getCount()
    {
        return title.size();
    }
    
    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o)
    {
        return view == o;
    }
    
    @SuppressLint("all")
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        
        View  view = layout.get(position);
        container.addView(view);
        
        // 返回填充的View对象
        return view;
        
        
    }
    
    @Nullable
    @Override
    public CharSequence getPageTitle(int position)
    {
        return title.get(position);
    }
    
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        
        container.removeView((View) object);
    }
    
    public void removeIndex(int closeIndex)
    {
        title.remove(closeIndex);
        layout.remove(closeIndex);
        notifyDataSetChanged();
    }
    
    public CodeView getCodeViewAt(int position)
    {
        return layout.get(position);
    }
    
}
