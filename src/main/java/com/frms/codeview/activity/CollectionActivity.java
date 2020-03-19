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
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.frms.codeview.MainActivity;
import com.frms.codeview.R;
import com.frms.codeview.activity.view.BookmarksListView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 项目名称 ： app
 * 创建人 ： Frms
 * 创建人邮箱 ： 3505826836@qq.com
 * 创建时间 ：2020/3/18 22:04(ydt)
 */
public class CollectionActivity extends AppCompatActivity
{
    private BookmarksListView listView;
    private ArrayList<String> collectionList;
    private SharedPreferences readCollection;

    private boolean isChange = false;
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.collection_layout);
        setTitle("收藏集");
        listView = findViewById(R.id.collection);
        if (MainEditActivity.mTheme) {
            setTheme(R.style.frms_Dark);
            setTitleColor(0xff555555);
            listView.setBackgroundColor(0xff555555);
        } else {
            setTheme(android.support.v7.appcompat.R.style.Theme_AppCompat_Light_NoActionBar);
        }
    
    
        
        
        readCollection = MainEditActivity.readCollection;
        collectionList = MainEditActivity.collectionList;
        
        if(collectionList.size() == 0)
        {
            Toast.makeText(CollectionActivity.this, "你没有收藏过任何地址", Toast.LENGTH_SHORT).show();
        }
        
        listView.setAdapter(new mCollectionAdapter());
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                MainEditActivity.fileBrowser.gotoPath(collectionList.get(position));
                MainEditActivity.drawerLayout1.openDrawer(Gravity.RIGHT);
                
                if(isChange)
                {
                    SharedPreferences.Editor editor =  readCollection.edit();
    
                    editor.clear();
                    editor.putString("data", new Gson().toJson(collectionList));
                    editor.apply();
    
                    Toast.makeText(CollectionActivity.this, "书签数据已保存", Toast.LENGTH_SHORT).show();
                }
                finish();
            }
        });
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if(event.getKeyCode() == KeyEvent.KEYCODE_BACK && isChange)
        {
            
            SharedPreferences.Editor editor =  readCollection.edit();
            
            editor.clear();
            editor.putString("data", new Gson().toJson(collectionList));
            editor.apply();
            
            Toast.makeText(this, "书签数据已保存", Toast.LENGTH_SHORT).show();
        }
        return super.onKeyDown(keyCode, event);
    }
    
    private class mCollectionAdapter extends BaseAdapter
    {
        @Override
        public int getCount()
        {
            return collectionList.size();
        }
        
        @Override
        public Object getItem(int position)
        {
            return collectionList.get(position);
        }
        
        @Override
        public long getItemId(int position)
        {
            return position;
        }
        
        ViewHolder viewHolder;
        
        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            
            
            if (convertView == null)
            {
                convertView = View.inflate(CollectionActivity.this, R.layout.listview_util_layout, null);
                viewHolder = new ViewHolder();
                viewHolder.textView = (TextView) convertView.findViewById(R.id.list_textView);
                viewHolder.list_delete = (TextView) convertView.findViewById(R.id.list_delete);
                convertView.setTag(viewHolder);
            } else
            {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            
            viewHolder.textView.setText(collectionList.get(position));
            final int pos = position;
            viewHolder.list_delete.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    isChange = true;
                    collectionList.remove(pos);
                    if(collectionList.size() == 0)
                    {
                        Toast.makeText(CollectionActivity.this, "现在啥都没有哦", Toast.LENGTH_SHORT).show();
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
    
    
    
    
}
