package github.zimoyin.mtool.command;

import github.zimoyin.mtool.annotation.Command;
import github.zimoyin.mtool.annotation.CommandClass;
import github.zimoyin.mtool.uilt.FindClass;
import net.mamoe.mirai.event.Event;
import net.mamoe.mirai.event.events.MessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.List;

/**
 * 由 commandSet 进行初始化操作，可以通过 initMethod 方法进行后续添加，通过 commandSet 进行维护
 * 加载所有的命令类，并加以解析
 * 扫描所有类的注解（@Command）并加载
 */
public class CommandLoad {
    //    private ArrayList<CommandObj> commands = new ArrayList<CommandObj>();
    private final Logger logger = LoggerFactory.getLogger(CommandLoad.class);
    private final CommandSet<String, CommandObj> commandSet = CommandSet.getInstance();


    /**
     * 初始化
     */
    public void init() {
        //遍历类
//        List<String> clazzName = FindClass.getClazzName(FindClassConfig.path, FindClassConfig.flag);
        List<String> clazzName = FindClass.getResults();
        for (String clzName : clazzName) {
            try {
                if (clzName.equalsIgnoreCase("github.zimoyin.tool.mirai.config.BotConfigurationImpl")) continue;
                //获取是被命令注解修饰的类
                Class<?> clz = Class.forName(clzName);
                CommandClass annotation = clz.getAnnotation(CommandClass.class);
                if (annotation == null) continue;
                //解析
                initMethod(clz);
                logger.debug("[系统日志]加载命令类：{}",clz);
            } catch (Exception e) {
                logger.error("无法完全解析的类路径:{}", clzName, e);
            }
        }
    }
    /**
     * 解析出命令类中的命令方法
     */
    public void initMethod(Class<?> clz) throws InstantiationException, IllegalAccessException {
        //获取是被命令注解修饰的类
        CommandClass annotation0 = clz.getAnnotation(CommandClass.class);
        if (annotation0 == null) throw new IllegalStateException("传入的类不是一个命令类");
        //命令方法
        Method[] methods = clz.getMethods();
        for (Method method : methods) {
            //解析命令方法
            Command annotation = method.getAnnotation(Command.class);
            if (annotation == null) continue;
//            if (!isSecurity(method)) logger.warn("警告：方法中定义的事件类型是否与注解中定义事件类型的不相融");
            isSecurity(method);
            //解析
            //命令的名称
            String name = annotation.value();
            //命令所需的事件
            Class<? extends Event> eventClass = annotation.eventType();
            //命令对象
            CommandObj commandObj = new CommandObj(name, method, eventClass, clz);
            //放入集合
            commandSet.put(name, commandObj);
        }
    }

    /**
     * 判断方法中定义的事件类型是否与注解中定义的相融
     */
    public void isSecurity(Method method) {
        Command annotation = method.getAnnotation(Command.class);
        //如果不是命令类
        if (annotation == null) return;

        //如果方法的参数不是信息事件
        Class<?>[] parameterTypes = method.getParameterTypes();
        //不是 MessageEvent （被动收到消息）的子类
        if (!MessageEvent.class.isAssignableFrom(parameterTypes[0]) && !CommandData.class.isAssignableFrom(parameterTypes[0])) {
            logger.warn("警告：命令方法的参数不是 MessageEvent （被动收到消息）的子类 与 CommandData 的子类:{}",method);
            return;
        }
        //如果没给 eventType 赋值 就返回true，因为这是事件的最高父类
        Class<? extends MessageEvent> eventType = annotation.eventType();
        if (eventType.equals(MessageEvent.class)) return;
        //如果 eventType 的类型是 方法中参数的父类，就发出转型警告
        if (eventType.isAssignableFrom(parameterTypes[0]))
            logger.warn("注意@Command 描述的事件类型 与 方法参数中事件类型不一致，这将导致从注解的数据类型向下转型到方法的事件类型：{}",method);
    }

    public CommandSet<String,CommandObj> getCommandSet() {
        return commandSet;
    }
}
