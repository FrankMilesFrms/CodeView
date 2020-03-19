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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.WindowManager;
import android.widget.TextView;

import com.frms.codeview.R;

/**
 * 项目名称 ： app
 * 创建人 ： Frms
 * 创建人邮箱 ： 3505826836@qq.com
 * 创建时间 ：2020/3/16 20:26(ydt)
 */
public class AboutActivity extends AppCompatActivity
{
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = getSharedPreferences("info", MODE_PRIVATE);
        int mTheme = sharedPreferences.getInt("theme", 0);
    
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        setContentView(R.layout.about_layout);
        ((TextView)findViewById(R.id.about_tv)).setText("\n\n编辑器开发人员\n" +
                                                        "\n" +
                                                        "Frms（3505-8268-36@qq.com）\n" +
                                                        "\n" +
                                                        "特别感谢\n" +
                                                        "\n" +
                                                        "虚青海儿(1575-1571-46@qq.com)\n" +
                                                        "《致》开源组(QQ 781-097-903)\n" +
                                                        "\n" +
                                                        "部分资源名称&(or)来源\n" +
                                                        "\n" +
                                                        "字体\n" +
                                                        "DejaVuSansMono.ttf\n" +
                                                        "图标\n" +
                                                        "阿里巴巴矢量图标库\n" +
                                                        "AIDE\n" +
                                                        "ES\n\n\n时间\n2020年3月19日傍晚");
    }
    
    public boolean onCreateOptionsMenu(Menu menu) {
        
        return true;
    }
}
