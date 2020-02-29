package com.frms.codeview.tools;

import com.frms.codeview.CodeView;
import java.util.LinkedList;

/**
 * 说明 ： 用于控制文本编辑命令的撤销和重做，允许在一定时间范围差内执行多个编辑命令，此插件不可移除。
 * 创建人 ： Frms
 * 创建人邮箱 ： 3505826836@qq.com
 * 创建时间 ：2020/2/22 11:44(ydt)
 */
public class UndoStack
{
    private long MERGE_LONGEST_TIME = 300; // 合并操作的操作时间差限制(ms)。
    private int index;
    private LinkedList<Command> commands;
    private CodeView codeView;
    private boolean megreCommands = true;
  
    public UndoStack(CodeView codeView)
    {
        index = 0;
        commands = new LinkedList<>();
        this.codeView = codeView;
        
    }
    
    boolean canRedo() {
        return index < commands.size();
    }
    
    boolean canUndo()
    {
        return index > 0;
    }
    
    /**
     * 设置相邻命令动作合并执行的最大时间差，如果是 -1 则关闭此功能，默认开启。
     * @param maxFlag ms
     */
    public void setMergeTime(long maxFlag)
    {
        megreCommands = maxFlag > 0;
        MERGE_LONGEST_TIME = maxFlag;
    }
    /**
     * 撤销，指针后移。
     */
    public void undo()
    {
        Command command = undoCommand();
        long t = 0;
        if(command != null)
        {
            command.undo();
            t = command.time;
            if(megreCommands)
            {
                while (canUndo())
                {
                    command = commands.get(--index);
        
                    if(t - command.time < MERGE_LONGEST_TIME)
                    {
                        command.undo();
                    } else
                    {
                        index++;
                        return;
                    }
                }
            }
        }
    }
    
    /**
     * 重做，指针前移。
     */
    public void redo()
    {
        Command command = redoCommand();
        long t = 0;
        if(command != null)
        {
            command.redo();
            t = command.time;
            
            if(megreCommands)
            {
                while (canRedo())
                {
                    command = commands.get(index++);
        
                    if(command.time - t < MERGE_LONGEST_TIME)
                    {
                        command.redo();
                    } else
                    {
                        index--;
                        return;
                    }
                }
            }
        }
    }
    
    /**
     * 获取将要撤销的命令，无则返回 null。
     * @return
     */
    private Command undoCommand()
    {
        if(canUndo())
        {
            return commands.get(--index);
        }
        return null;
    }
    
    /**
     * 获取将要重做的命令，无则返回 null。
     * @return
     */
    private Command redoCommand()
    {
        if(canRedo())
        {
            return commands.get(index++);
        }
        return null;
    }
    
    /**
     * 添加命令到当前指针，但仍保持指针对应空值
     * @param command 命令
     */
    public void addCommand(Command command)
    {
        commands.add(index++, command);
        cleanRedo(index);
    }
    
    public void addCommand(int position, String string, int l1, int l2, long time, boolean isInsert)
    {
        Command command = isInsert?
                          new Insert(position, string, l1, l2, time)
                        : new Delete(position, string, l1, l2, time);
        addCommand(command);
    }
    
    
    /**
     * 清空所有命令
     */
    public void empty()
    {
        commands.clear();
        index = 0;
    }
    
    /**
     *	含参
     */
    public void cleanRedo(int p) {
        int size = commands.size();
        while(p < size) {
            commands.removeLast();
            p++;
        }
    }
    
    public static abstract class Command
    {
        protected int Tag = -1;
        protected String text;
        protected int position;
        
        protected long time; // 编辑时候的时间差
        protected long dtime;
        protected int line1, line2;
        
        public abstract void redo();
        public abstract void undo();
        
        public void setLine(int l)
        {
            line1 = l;
        }
        public void setLine2(int l)
        {
            line2 = l;
        }
    
        @Override
        public String toString()
        {
            return "[type = " + (Tag > 0? (Tag == 1? "Insert" : "Delete")  : "Unknown ")+" Mode," +
                   " text = " + text.replace("\n", "\\n") +"," +
                   " position = " + position+"," +
                   " time = " + time +"," +
                   "start line = " +line1+"," +
                   "end line =" +line2+"]";
        }
    }
    
    class Insert extends Command
    {
        
        public Insert(int position, String string, int l1, int l2, long time)
        {
            line1 = l1;
            line2 = l2;
            
            this.Tag = 1;
            this.text = string;
            this.time = time;
            this.position = position;
        }
        
        
        @Override
        public void redo()
        {
            if(line2 < 0)
            {
                codeView.insertChar(position, text.charAt(0), false, line1, false);
            } else
            {
                codeView.insert(position, text, false, line1, line2, false);
            }
        }
    
        @Override
        public void undo()
        {
            if(line2 < 0)
            {
                codeView.deleteChar(position + 1, false, line1 + (text.equals("\n")?1:0),false);
            } else
            {
                codeView.delete(position, text.length() + position, false, line1, line2,false);
            }
        }
    }
    
    class Delete extends Command
    {
    
        public Delete(int position, String string, int l1, int l2, long time)
        {
            line1 = l1;
            line2 = l2;
        
            this.Tag = 2;
            this.text = string;
            this.time = time;
            this.position = position;
        }
        
        @Override
        public void redo()
        {
            if(line2 < 0)
            {
                codeView.deleteChar(position + 1, false, line1 + (text.equals("\n")?1:0), false);
            } else
            {
                codeView.delete(position, position + text.length(), false, line1, line2, false);
            }
        }
    
        @Override
        public void undo()
        {
            if(line2 < 0)
            {
                codeView.insertChar(position, text.charAt(0), false, line1, false);
            } else
            {
                codeView.insert(position, text, false, line1, line2,false);
            }
        }
    
    }
}
