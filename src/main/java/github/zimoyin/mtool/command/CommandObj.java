package github.zimoyin.mtool.command;

import github.zimoyin.mtool.annotation.Command;
import github.zimoyin.mtool.command.filter.CommandFilter;
import github.zimoyin.mtool.control.ListenerSet;
import github.zimoyin.mtool.dao.H2Connection;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.event.Event;
import net.mamoe.mirai.event.events.MessageEvent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;


/**
 * 命令对象
 */
@Data
@Slf4j
public class CommandObj {
    /**
     * 是否运行执行此项命令,为true运行执行，false 不允许执行
     */
    private boolean isExecute = true;
    /**
     * key: 命令方法的名称
     */
    private String name;
    /**
     * value: 方法对象
     */
    private Method method;
    /**
     * 命令所需的参数类型
     */
    private Class<? extends Event> eventClass;

    /**
     * 命令类的 new 对象，用于反射调用方法
     */
    private Object commandObject;
    /**
     * 命令所在类的class
     */
    private Class<?> commandClass;

    /**
     * 该命令的Help
     */
    private CommandHelp help;

    /**
     * @param name          命令的名称
     * @param method        命令方法
     * @param eventClass    命令方法所在的类
     * @param commandObject 命令类的对象
     * @param commandClass  命令类的class
     */
    public CommandObj(String name,
                      Method method,
                      Class<? extends Event> eventClass,
                      Object commandObject,
                      Class<?> commandClass) {
        this.name = name;
        this.method = method;
        this.eventClass = eventClass;
        this.commandObject = commandObject;
        this.commandClass = commandClass;
    }


    public CommandObj(String name,
                      Method method,
                      Class<? extends Event> eventClass,
                      Object commandObject) {
        this.name = name;
        this.method = method;
        this.eventClass = eventClass;
        this.commandObject = commandObject;
        this.commandClass = commandObject.getClass();
    }

    public CommandObj(String name, Method method, Class<? extends Event> eventClass, Class<?> commandClass) throws InstantiationException, IllegalAccessException {
        this.name = name;
        this.method = method;
        this.eventClass = eventClass;
        this.commandClass = commandClass;
        this.commandObject = commandClass.newInstance();
    }

    public CommandObj(Method method, Class<?> commandClass) throws InstantiationException, IllegalAccessException {
        this.method = method;
        this.commandClass = commandClass;

        Command annotation = method.getAnnotation(Command.class);
        this.eventClass = annotation.eventType();
        this.name = annotation.value();
        this.commandObject = commandClass.newInstance();
    }

    /**
     * 执行该命令方法
     *
     * @param event
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public void execute(MessageEvent event) throws InvocationTargetException, IllegalAccessException, InstantiationException {
        if (!isExecute) {
            log.debug("{} 命令已经被关闭无法被执行 --- {}", getName(), getMethod());
            return;
        }
        if (commandObject == null) commandObject = commandClass.newInstance();
//        Class<?>[] parameterTypes = method.getParameterTypes();
//        if (parameterTypes.length != 1) throw new IllegalArgumentException("不合法的方法参数，参数不是为恒值1个");
//        if (parameterTypes[0].isAssignableFrom(event.getClass())) method.invoke(commandObject, event);
//        else if (parameterTypes[0].isAssignableFrom(CommandData.class)) method.invoke(commandObject, new CommandData(event));
       //上面代码更新为下面代码
        CommandData commandData = new CommandData(event);
        invoke(
                //机器人相关
                event,//事件
                commandData,//事件封装
                event.getMessage(),//消息
                event.getBot(),//机器人
                event.getSubject(),//联系人
                //命令相关
                this,
                new CommandFilter(commandData),
                CommandSet.getInstance(),
                ListenerSet.getInstance(),
                //dao
                H2Connection.getInstance()
        );
    }

    /**
     * 根据方法参数自动注入参数并执行
     *
     * @param args 参数列表
     */
    public void invoke(Object... args) throws InvocationTargetException, IllegalAccessException, InstantiationException {
        if (commandObject == null) commandObject = commandClass.newInstance();
        Class<?>[] types = method.getParameterTypes();
        Object[] objects = sortObjects(types, objectToMap(args));
        method.invoke(commandObject, objects);
    }

    /**
     * 将对象排序
     *
     * @param types       排序标准
     * @param objectToMap 对象列表
     */
    private Object[] sortObjects(Class<?>[] types, HashMap<Class<?>, Object> objectToMap) {
        Object[] objs = new Object[types.length];//排序好的参数列表
        for (int i = 0; i < types.length; i++) {
            objs[i] = objectToMap.get(types[i]);
            //如果找不到 types 里面一致的数据，就去找其子类，或父类
            if (objs[i] == null) {
                for (Class<?> cls : objectToMap.keySet()) {
                    //判断是否存在继承关系
                    if (cls.isAssignableFrom(types[i])|| types[i].isAssignableFrom(cls)) {
                        objs[i] = objectToMap.get(cls);
                        break;
                    }
                }
            }
            if (objs[i] == null) throw new IllegalArgumentException("参数列表无法找到的参数:" + types[i]);
        }
        return objs;
    }

    /**
     * 参数对象列表转为 map
     *
     * @param args 参数列表
     */
    private HashMap<Class<?>, Object> objectToMap(Object... args) {
        HashMap<Class<?>, Object> obj = new HashMap<Class<?>, Object>();
        for (Object arg : args) {
            if (obj.containsKey(arg.getClass())) throw new IllegalArgumentException("参数列表存在相同的参数类型");
            obj.put(arg.getClass(), arg);
        }
        return obj;
    }

    /**
     * 禁止执行命令
     */
    public void notExecute() {
        this.isExecute = false;
    }

    /**
     * 获取命令帮助
     *
     * @return
     */
    public CommandHelp getHelp() {
        if (help == null) help = CommandHelp.create(this.method);
        return help;
    }

    @Data
    public static class CommandHelp {
        /**
         * 命令帮助
         */
        private String help;
        /**
         * 命令描述
         */
        private String description;


        public CommandHelp(String help, String description) {
            this.help = help;
            this.description = description;
        }

        public static CommandHelp create(Method commandMethod) {
            Command annotation = commandMethod.getAnnotation(Command.class);
            String help = annotation.help();
            String description = annotation.description();

            return new CommandHelp(help, description);
        }
    }
}
