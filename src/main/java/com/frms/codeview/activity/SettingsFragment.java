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
import android.content.Intent;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.view.View;
import android.widget.Toast;

import com.frms.codeview.R;
import com.frms.codeview.tools.Kit;


import static android.os.ParcelFileDescriptor.MODE_WORLD_READABLE;

/**
 * 项目名称 ： app
 * 创建人 ： Frms
 * 创建人邮箱 ： 3505826836@qq.com
 * 创建时间 ：2020/3/18 14:46(ydt)
 */
public class SettingsFragment extends PreferenceFragment
    implements Preference.OnPreferenceChangeListener,
               Preference.OnPreferenceClickListener
{
    static String[] language_value = {
        "Text", "JavaScript", "Java"
    };
    
    static String[] typeface_value =
        {
            "DejaVuSansMono","Monospace", "Sans Serif", "Serif"
        };
    static String[] save_time_value =
        {
            "30 s",
            "1 min",
            "3 min",
            "5 min",
            "10 min",
            "仅手动保存"
        };
    SwitchPreference theme, auto, bar;
    EditTextPreference textsize, autoMessage, barMessage;
    ListPreference language, typeface, saveTime;
    
    @SuppressLint("WorldReadableFiles")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        getPreferenceManager().setSharedPreferencesMode(MODE_WORLD_READABLE);
        addPreferencesFromResource(R.xml.pre_set);
        
        theme = (SwitchPreference) findPreference("preference_theme");
        auto = (SwitchPreference) findPreference("preference_switch_auto_text");
        bar = (SwitchPreference) findPreference("preference_auxiliary_keyboard");
//        <EditTextPreference
//        android:defaultValue=""
//        android:key="preference_auxiliary_keyboard_symbols"
//        android:inputType="none"
//        android:selectAllOnFocus="false"
//        android:singleLine="false"
//        android:title="辅助键盘符号(分行分割)" />
        
        //barMessage = (EditTextPreference) findPreference("preference_auxiliary_keyboard_symbols");
        //autoMessage = (EditTextPreference) findPreference("preference_auto_text");
        textsize = (EditTextPreference) findPreference("preference_text_size");
        
        language = (ListPreference) findPreference("preference_language");
        typeface = (ListPreference) findPreference("preference_typeface");
//        saveTime = (ListPreference) findPreference("preference_automatically_saved");
//        <PreferenceCategory android:title="文本存储">
//
//        <ListPreference
//            android:defaultValue="-1"
//            android:entries="@array/save_file"
//            android:entryValues="@array/save_file_time"
//            android:key="preference_automatically_saved"
//            android:summary="仅手动保存"
//            android:title="自动保存时间间隔" />
//    </PreferenceCategory>
        
        
        theme.setOnPreferenceChangeListener(this);
        auto.setOnPreferenceChangeListener(this);
        bar.setOnPreferenceChangeListener(this);
        
        //barMessage.setOnPreferenceChangeListener(this);
        //autoMessage.setOnPreferenceChangeListener(this);
        textsize.setOnPreferenceChangeListener(this);
        
        language.setOnPreferenceChangeListener(this);
        typeface.setOnPreferenceChangeListener(this);
        //saveTime.setOnPreferenceChangeListener(this);
        
//        language.setOnPreferenceClickListener(this);
//        typeface.setOnPreferenceClickListener(this);
    }
    
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        language.setSummary(language_value[Integer.parseInt(language.getValue())]);
        typeface.setSummary(typeface_value[Integer.parseInt(typeface.getValue())]);
//        saveTime.setSummary(save_time_value[Math.abs(Integer.parseInt(saveTime.getValue())) ]);
    }
    
    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue)
    {
        switch (preference.getKey())
        {
            case "preference_language":
                language.setSummary(language_value[Integer.parseInt((String) newValue)]);
                break;
            case "preference_typeface":
                typeface.setSummary(typeface_value[Integer.parseInt((String) newValue)]);
                break;
//            case "preference_automatically_saved":
//                saveTime.setSummary(save_time_value[Integer.parseInt((String) newValue)]);
//            break;
            
            case "preference_theme":
                Kit.printout(theme.getSwitchTextOff(), theme.getSwitchTextOn());
                break;
        }
        return true;
    }
    
    
    @Override
    public boolean onPreferenceClick(Preference preference)
    {
        return true;
    }
    
}
