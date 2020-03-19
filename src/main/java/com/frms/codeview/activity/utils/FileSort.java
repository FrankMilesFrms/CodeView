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

/**
 * 项目名称 ： app
 * 创建人 ： Frms
 * 创建人邮箱 ： 3505826836@qq.com
 * 创建时间 ：2020/3/16 17:59(ydt)
 */

import java.io.File;

import java.util.Arrays;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class FileSort {
    
    /**
     * 已过时
     * @param file
     */
    public void sort(File file)
    {
        
        if (file.isDirectory()) { // 判断file是否为目录
            
            String[] fileNames = file.list();
            
            Arrays.sort(fileNames, new Comparator<String>() {
                
                @Override
                public int compare(String s1, String s2) {
                    
                    if(returnDouble(s1)<returnDouble(s2))
                        
                        return -1;
                    
                    else if(returnDouble(s1)>returnDouble(s2))
                        
                        return 1;
                    
                    else
                        
                        return 0;
                }
                
                public  double returnDouble(String str)
                {
                    
                    StringBuffer sb = new StringBuffer();
                    
                    for(int i=0;i<str.length();i++){
                        
                        if(Character.isDigit(str.charAt(i)))
                            
                            sb.append(str.charAt(i));
                        
                        else if(str.charAt(i)=='.'&&i<str.length()-1&&Character.isDigit(str.charAt(i+1)))
                            
                            sb.append(str.charAt(i));
                        
                        else break;
                        
                    }
                    
                    if(sb.toString().isEmpty())
                        
                        return 0;
                    
                    else
                        
                        return Double.parseDouble(sb.toString());
                    
                }
            });
            
            for (int i = 0; i < fileNames.length; i++) {
                System.out.println(fileNames[i]);
            }
            
        }
        
    }
    
    
    public static File[] orderByName(File[] list) {
        
        if(list == null || list.length < 1) {
            return new File[0];
        }
        
        List<File> files = Arrays.asList(list);
        
        Collections.sort(files, new Comparator<File>() {
            
            @Override
            
            public int compare(File o1, File o2) {
                
                if (o1.isDirectory() && o2.isFile())
                    
                    return -1;
                
                if (o1.isFile() && o2.isDirectory())
                    
                    return 1;
                
                return o1.getName().compareTo(o2.getName());
                
            }
            
        });
        
        return files.toArray(list);
    }
    
    
}
