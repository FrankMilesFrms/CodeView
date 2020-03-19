package com.frms.codeview.activity.view;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.frms.codeview.R;
import com.frms.codeview.activity.utils.FileAdapter;
import com.frms.codeview.activity.utils.FileSort;
import com.frms.codeview.activity.utils.FileTypeUtil;
import com.frms.codeview.activity.utils.FileUtils;
import com.frms.codeview.tools.Kit;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 项目名称 ： app
 * 创建人 ： Frms
 * 创建人邮箱 ： 3505826836@qq.com
 * 创建时间 ：2020/3/16 16:47(ydt)
 */
public class FileBrowser extends ListView
    implements AdapterView.OnItemClickListener,
               AdapterView.OnItemLongClickListener
{
    private final EditText editText;
    private FileAdapter fileAdapter;
    private ArrayList<FileTypeUtil> typeUtils;
    
    public static final String ROOT_PATH;
    public static File nowPath;
    
    AlertDialog builder, rename;
    String fileNameCache = "", fileRenameCache = "";
    
    static {
        ROOT_PATH = Environment.getExternalStorageDirectory().getPath();
        nowPath = new File(ROOT_PATH);
    }
    
    private TextView title;
    private ImageView back, home, like, add;
    private DrawerLayout drawerLayout;
    private File renameFile;
    private OnClickFile onClick;
    
    public FileBrowser(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        typeUtils = new ArrayList<>();
        init();
        setOnItemClickListener(this);
        setOnItemLongClickListener(this);
        final View addLayout = LayoutInflater.from(getContext()).inflate(R.layout.add_file_layout, null);
        
        builder = new AlertDialog.Builder(getContext())
                .setView(addLayout)
                .setNegativeButton("关闭", null)
                .setPositiveButton("创建", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        fileNameCache = ((EditText)addLayout.findViewById(R.id.editText)).getText().toString();
                        
                        if(fileNameCache.contains("/"))
                        {
                            Snackbar.make(drawerLayout, "不允许存在 / 字符", Snackbar.LENGTH_SHORT).show();
                            return;
                        }
                        
                        File file = new File(nowPath.getPath() + "/" + fileNameCache);
                        if(((RadioButton)addLayout.findViewById(R.id.createFile)).isChecked())
                        {
                            if(file.exists() && file.isFile())
                            {
                                ((EditText)addLayout.findViewById(R.id.editText)).setText(fileNameCache);
                                Snackbar.make(drawerLayout, "已存在此文件！", Snackbar.LENGTH_SHORT).show();
                            } else
                            {
                                try {
                                    
                                    FileUtils.newFile(file.getPath(), "");
                                    fileAdapter.onClick(-3, typeUtils, nowPath);
                                    title.setText(nowPath.getPath());
                                    fileAdapter.notifyDataSetChanged();
                                    Snackbar.make(drawerLayout, "已创建文件！", Snackbar.LENGTH_SHORT).show();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    Snackbar.make(drawerLayout, "不允许在此创建文件！" + e.toString(), Snackbar.LENGTH_SHORT).show();
                                }
                            }
                        } else
                        {
                            
//                            if(file.exists() )
//                            {
//                                ((EditText)addLayout.findViewById(R.id.editText)).setText(fileNameCache);
//                                Snackbar.make(drawerLayout, "已存在此文件夹！", Snackbar.LENGTH_SHORT).show();
//                            } else
                            {
                                try {
        
                                    file.mkdir();
                                    fileAdapter.onClick(-3, typeUtils, nowPath);
                                    title.setText(nowPath.getPath());
                                    fileAdapter.notifyDataSetChanged();
                                    Snackbar.make(drawerLayout, "已创建文件夹！", Snackbar.LENGTH_SHORT).show();
                                } catch (Throwable e) {
                                    e.printStackTrace();
                                    Snackbar.make(drawerLayout, "不允许在此创建文件夹！"+ e.toString(), Snackbar.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }
                }).create();
        
        editText = new EditText(getContext());
        editText.setHint("它的名字");
        rename = new AlertDialog.Builder(getContext())
        .setTitle("重命名")
        .setView(editText)
        .setPositiveButton("创建", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                fileRenameCache = editText.getText().toString();
                
                File file = new File(nowPath.getPath() + "/" + fileRenameCache);
                
                if(file.isDirectory())
                {
                    if(file.exists() || fileRenameCache.contains("/"))
                    {
                        Snackbar.make(drawerLayout, "重命名失败：非法字符 或 已有同名文件夹", Snackbar.LENGTH_LONG).show();
                    }else
                    {
                        // change rename
                        renameFile.renameTo(file);
                        nowPath = fileAdapter.onClick(-3, typeUtils, nowPath);
                        title.setText(nowPath.getPath());
                        fileAdapter.notifyDataSetChanged();
                    }
                } else
                {
                    if(file.exists() || fileRenameCache.contains("/"))
                    {
                        Snackbar.make(drawerLayout, "重命名失败：非法字符 或 已有同名文件", Snackbar.LENGTH_LONG).show();
                    }else
                    {
                        // change rename
                        renameFile.renameTo(file);
                        nowPath = fileAdapter.onClick(-3, typeUtils, nowPath);
                        title.setText(nowPath.getPath());
                        fileAdapter.notifyDataSetChanged();
                    }
                }
                
            }
        })
        .create();
    }
    
    public void setModules(final TextView title,
                           ImageView back,
                           ImageView home,
                           ImageView like,
                           ImageView add,
                           final SwipeRefreshLayout swipeRefreshLayout,
                           final DrawerLayout drawerLayout,
                           final SharedPreferences.Editor editor,
                           final ArrayList<String> arrayList)
    {
        this.title = title;
        this.back = back;
        this.home = home;
        this.add = add;
        this.drawerLayout = drawerLayout;
        
        title.setText(nowPath.getAbsolutePath());
        
        back.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                nowPath = fileAdapter.onClick(-1, typeUtils, nowPath);
                title.setText(nowPath.getPath());
                fileAdapter.notifyDataSetChanged();
            }
        });
        
        home.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                nowPath = fileAdapter.onClick(-2, typeUtils, nowPath);
                title.setText(nowPath.getPath());
                fileAdapter.notifyDataSetChanged();
            }
        });
    
        add.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(builder.isShowing())
                {
                    builder.dismiss();
                }else
                {
                    builder.show();
                }
            }
        });
        
        
        like.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(arrayList.indexOf(nowPath.getAbsolutePath()) < 0)
                {
                    arrayList.add(nowPath.getAbsolutePath());
                }
                editor.clear();
                editor.putString("data", new Gson().toJson(arrayList));
                editor.apply();
                Snackbar.make(drawerLayout, "已添加", Snackbar.LENGTH_SHORT).show();
            }
        });
        
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                nowPath = fileAdapter.onClick(-3, typeUtils, nowPath);
                title.setText(nowPath.getPath());
                fileAdapter.notifyDataSetChanged();
                
                Snackbar.make(drawerLayout, "已刷新当前路径内文件", Snackbar.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }
    
    
    public void init()
    {
        
        onClick = new OnClickFile()
        {
            @Override
            public void onClick(File file)
            {
            
            }
        };
        
        
        typeUtils.clear();
        File[] list = nowPath.listFiles();
        list = FileSort.orderByName(list);
        
        for(File f : list)
        {
            typeUtils.add(new FileTypeUtil(f.getName(), 0));
        }
        
        fileAdapter = new FileAdapter(getContext(), R.layout.file_util_layout, list, typeUtils, onClick);
        setAdapter(fileAdapter);
    }
    
    
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        nowPath = fileAdapter.onClick(position, typeUtils, nowPath);
        title.setText(nowPath.getPath());
        fileAdapter.notifyDataSetChanged();
    }
    
    
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
    {
        renameFile = fileAdapter.onLongClick(position, typeUtils);
        new AlertDialog.Builder(getContext())
            .setTitle("操作")
            .setItems(new String[]{"重命名", "删除"}, new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    if(which == 0)
                    {
                        editText.setText(renameFile.getName());
                        editText.setSelection(0, editText.getText().length());
                        rename.show();
                    } else
                    {
                        if(renameFile.isDirectory())
                        {
                            Snackbar.make(drawerLayout, "若文件夹内存在文件，则删除无效", Snackbar.LENGTH_LONG).show();
                        }
                        renameFile.delete();
                        fileAdapter.onClick(-3, typeUtils, nowPath);
                        title.setText(nowPath.getPath());
                        fileAdapter.notifyDataSetChanged();
                    }
                }
            })
        .setPositiveButton("关闭", null)
        .show();
       
        return true;
    }
    
    
    
    public interface OnClickFile
    {
        void onClick(File file);
    }
    
    
    public void setOnClickFile(OnClickFile onClickFile)
    {
        onClick = onClickFile;
        fileAdapter.setOnClick(onClickFile);
    }
    
    public void gotoPath(String str)
    {
        gotoPath(new File(str));
    }
    
    public void gotoPath(File file)
    {
        if(file.exists())
        {
            nowPath = file;
            fileAdapter.gotoPath(typeUtils, nowPath);
            title.setText(nowPath.getPath());
            fileAdapter.notifyDataSetChanged();
        }else
        {
            Kit.printout("FileBrowser Error", "can not find the path");
        }
    }
}
