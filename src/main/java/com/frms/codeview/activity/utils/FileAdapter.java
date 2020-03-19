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
import android.app.AlertDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;

import com.frms.codeview.R;
import com.frms.codeview.activity.view.FileBrowser;
import com.frms.codeview.tools.Kit;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 项目名称 ： app
 * 创建人 ： Frms
 * 创建人邮箱 ： 3505826836@qq.com
 * 创建时间 ：2020/3/16 16:25(ydt)
 */
public class FileAdapter extends ArrayAdapter<FileTypeUtil>
{
    private FileBrowser.OnClickFile onClick;
    public int type;
    private File[] allFiles;
    
    public FileAdapter(@NonNull Context context, int resource, File[] objects, List<FileTypeUtil> fileAdapters, FileBrowser.OnClickFile onClickFile)
    {
        super(context, resource, fileAdapters);
        type = resource;
        allFiles = objects;
        onClick = onClickFile;
    }
    
    // 频繁创建变量会导致内存使用效率减低，故放在外部。
    FileHolder viewHolder;
    View view;
    File file;
    
    @SuppressLint("all")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        file = allFiles[position];
        
        if(convertView == null)
        {
            view = LayoutInflater.from(getContext()).inflate(type, null);
            
            viewHolder = new FileHolder();
            
            viewHolder.imageView = view.findViewById(R.id.file_image);
            viewHolder.name = view.findViewById(R.id.file_textView);
            
            view.setTag(viewHolder);
        }
        else
        {
            view = convertView;
            viewHolder = (FileHolder) convertView.getTag();
        }
        String type = file.getName();
        viewHolder.name.setText(type);
        
        if(file.isDirectory())
        {
            // 隐藏文件标志
            if(type.charAt(0) == '.')
            {
                viewHolder.imageView.setBackgroundResource(R.drawable.folder_hidden);
            }else
            {
                viewHolder.imageView.setBackgroundResource(R.drawable.folder);
            }
        }else
        {
            if(type.contains("."))
            {
                type = type.substring(type.lastIndexOf('.') + 1, type.length());
                
                switch (type)
                {
                    case "java":
                        viewHolder.imageView.setBackgroundResource(R.drawable.file_type_java);
                    break;
                    case "js":
                        viewHolder.imageView.setBackgroundResource(R.drawable.file_type_js);
                    break;
                    case "txt":
                    case "TXT":
                        viewHolder.imageView.setBackgroundResource(R.drawable.file_type_txt);
                    break;
                    default:
                        viewHolder.imageView.setBackgroundResource(R.drawable.file_type_unknown);
                }
                
            }else
            {
                viewHolder.imageView.setBackgroundResource(R.drawable.file_type_unknown);
            }
        }
        return view;
    }
    
    public File onClick(int position, List<FileTypeUtil> fileTypeUtils, File path)
    {
       
        if(position < 0)
        {
            if(position == -1)
            {
                // back
                file = path.getParentFile();
               
            } else if(position == -2)
            {
                file  = new File(FileBrowser.ROOT_PATH);
                
            }else if(allFiles.length > 0)
            {
                file = path;
                
            }
            
        } else
        {
            file = allFiles[position];
        }
        
        if(file == null)
        {
            file = new File(FileBrowser.ROOT_PATH);
        }
        
        if(file.isDirectory())
        {
            allFiles = file.listFiles();
            allFiles = FileSort.orderByName(allFiles);
            fileTypeUtils.clear();
            
            for(File f : allFiles)
            {
                fileTypeUtils.add(new FileTypeUtil(f.getName(), 0));
            }
            
            return file;
        }else
        {
            onClick.onClick(file);
        }
        return path;
    }
    
    public File onLongClick(int position, ArrayList<FileTypeUtil> typeUtils)
    {
        return allFiles[position];
    }
    
    public void setOnClick(FileBrowser.OnClickFile onClickFile)
    {
        onClick = onClickFile;
    }
    
    public void gotoPath(List<FileTypeUtil> fileTypeUtils, File file)
    {
        if(file.isDirectory())
        {
            allFiles = file.listFiles();
            allFiles = FileSort.orderByName(allFiles);
            fileTypeUtils.clear();
        
            for(File f : allFiles)
            {
                fileTypeUtils.add(new FileTypeUtil(f.getName(), 0));
            }
        }else
        {
            onClick.onClick(file);
        }
    }
    
}
