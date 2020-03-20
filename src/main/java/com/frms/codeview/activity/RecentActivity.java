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

package com.frms.codeview.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.frms.codeview.R;
import com.frms.codeview.activity.view.BookmarksListView;
import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;

/**
 * 项目名称 ： app
 * 创建人 ： Frms
 * 创建人邮箱 ： 3505826836@qq.com
 * 创建时间 ：2020/3/19 11:59(ydt)
 */
public class RecentActivity extends AppCompatActivity
{
    private BookmarksListView listView;
    private ArrayList<String> recentFiles;
    private boolean isChange = false;
    private SharedPreferences readCollection;
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        if (MainEditActivity.mTheme) {
            setTheme(R.style.frms_Dark);
        } else {
            setTheme(android.support.v7.appcompat.R.style.Theme_AppCompat_Light_NoActionBar);
        }
        
        setContentView(R.layout.recent_layout);
        
        Toolbar toolbar = findViewById(R.id.recent_toolbar);
        
        setSupportActionBar(toolbar);
        
        listView = findViewById(R.id.recent);
        
        
        if(MainEditActivity.mTheme)
        {
            listView.setBackgroundColor(0xff555555);
        }
    
    
        setTitle("最近打开的文件");
    
        
        
        recentFiles = MainEditActivity.recentFiles;
        readCollection = MainEditActivity.readCollection;
        
        if(recentFiles.size() == 0)
        {
            Toast.makeText(RecentActivity.this, "你没有打开过任何有效文件", Toast.LENGTH_SHORT).show();
        }
        
        listView.setAdapter(new mRecentAdapter());
    
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                MainEditActivity.fileBrowser.gotoPath(new File(recentFiles.get(position)).getParent());
                MainEditActivity.drawerLayout1.openDrawer(Gravity.RIGHT);
            
                //if(isChange)
                {
                    SharedPreferences.Editor editor =  readCollection.edit();
                
                    editor.clear();
                    editor.putString("recent", new Gson().toJson(recentFiles));
                    editor.apply();
                
                    Toast.makeText(RecentActivity.this, "数据已保存", Toast.LENGTH_SHORT).show();
                }
                finish();
            }
        });
   }
    
    private class mRecentAdapter extends BaseAdapter
    {
        @Override
        public int getCount()
        {
            return recentFiles.size();
        }
        
        @Override
        public Object getItem(int position)
        {
            return recentFiles.get(position);
        }
        
        @Override
        public long getItemId(int position)
        {
            return position;
        }
        
        RecentActivity.mRecentAdapter.ViewHolder viewHolder;
        
        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            
            
            if (convertView == null)
            {
                convertView = View.inflate(RecentActivity.this, R.layout.listview_util_layout, null);
                viewHolder = new RecentActivity.mRecentAdapter.ViewHolder();
                viewHolder.textView = (TextView) convertView.findViewById(R.id.list_textView);
                viewHolder.list_delete = (TextView) convertView.findViewById(R.id.list_delete);
                convertView.setTag(viewHolder);
            } else
            {
                viewHolder = (RecentActivity.mRecentAdapter.ViewHolder) convertView.getTag();
            }
            
            viewHolder.textView.setText(recentFiles.get(position));
            final int pos = position;
            viewHolder.list_delete.setOnClickListener(new View.OnClickListener()
            {
                
                @Override
                public void onClick(View v)
                {
                    isChange = true;
                    recentFiles.remove(pos);
                    if(recentFiles.size() == 0)
                    {
                        Toast.makeText(RecentActivity.this, "现在啥都没有哦", Toast.LENGTH_SHORT).show();
                    }
                    notifyDataSetChanged();
                    listView.turnNormal();
                }
            });
            return convertView;
        }
        
        class ViewHolder
        {
            public TextView textView;
            public TextView list_delete;
        }
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if(event.getKeyCode() == KeyEvent.KEYCODE_BACK
           //&& isChange
        )
        {
            
            SharedPreferences.Editor editor =  readCollection.edit();
            
            editor.clear();
            editor.putString("recent", new Gson().toJson(recentFiles));
            editor.apply();
            
            Toast.makeText(this, "数据已保存", Toast.LENGTH_SHORT).show();
        }
        return super.onKeyDown(keyCode, event);
    }
    
}
