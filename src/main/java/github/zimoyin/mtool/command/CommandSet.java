package github.zimoyin.mtool.command;

import java.util.HashMap;
import java.util.function.BiConsumer;

/**
 * 命令集合<String,CommandObj>
 * key:命令名称
 * value:命令具体信息
 */
public class CommandSet<S,C> extends HashMap<String,CommandObj> {
    private volatile static  CommandSet<String,CommandObj> INSTANCE = null;
    private CommandSet(){
        //初始化
//        new CommandLoad().init();
    }
    public static synchronized CommandSet<String,CommandObj> getInstance(){
        if (INSTANCE == null){
            INSTANCE = new CommandSet<String,CommandObj>();
        }
        return INSTANCE;
    }


    /**
     * 获取此命令的执行类
     * @param data
     * @return
     */
    public CommandObj get(CommandData data) {
        return super.get(data.getHeader());
    }

    @Override
    public void forEach(BiConsumer<? super String, ? super CommandObj> action) {
        super.forEach(action);
    }
}
