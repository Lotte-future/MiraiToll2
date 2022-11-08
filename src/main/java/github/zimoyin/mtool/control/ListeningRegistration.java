package github.zimoyin.mtool.control;

import github.zimoyin.mtool.annotation.ThreadSpace;
import github.zimoyin.mtool.uilt.NewThreadPoolUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.event.Event;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.Listener;
import net.mamoe.mirai.event.events.MessageEvent;

import java.util.function.Consumer;

@Data
@Slf4j
public class ListeningRegistration {
    /**
     * 监听集合对象
     */
    private ListenerSet listeners = ListenerSet.getInstance();

    /**
     * 创建监听
     *
     * @param cls 带有处理方法的类（会通过cls创建对象）
     */
    public ListeningRegistration(Class<?> cls) {
        registration(new ListenerObj(cls));
    }


    /**
     * 注册监听
     */
    private void registration(ListenerObj listener) {
        //如果监听没有成功的添加进集合就禁止进行注册
        if (!listeners.add(listener)) return;
        log.debug(" [系统日志]加载控制器类: " + listener.getCls());
        //注册该(监听)类下所有的监听方法
        for (ListenerObj.ListenerMethod listenerMethod : listener.getMethods()) {
            ListeningRegistration.newListener0(listenerMethod);
        }
    }

    /**
     * 注册监听
     *
     * @param listenerMethod 监听
     */
    public static void newListener0(ListenerObj.ListenerMethod listenerMethod) {
        //创建监听
        //执行处理方法
        listenerMethod.setExecute(true);
        Listener<? extends Event> listener = GlobalEventChannel.INSTANCE.subscribeAlways(listenerMethod.getEventClas(), (Consumer<Event>) event -> {
            ThreadSpace annotation = listenerMethod.getCls().getAnnotation(ThreadSpace.class);
            ThreadSpace annotation2 = listenerMethod.getMethod().getAnnotation(ThreadSpace.class);
            if (annotation == null && annotation2 == null) listenerMethod.invoke(event);
            else NewThreadPoolUtils.getInstance().execute(() -> listenerMethod.invoke(event));
        });
        listenerMethod.setListener(listener);
    }

    /**
     * 创建临时监听对象
     *
     * @param eventClass 事件类型
     * @param runnable   任务类
     * @Title 创建临时监听
     * @例子 <p>
     * ListeningRegistration.newTempListener0(GroupMessageEvent.class, new EventTask<GroupMessageEvent>() {
     * public boolean run(GroupMessageEvent event) {
     * System.out.println(4);
     * return true; //返回true就结束监听
     * }
     * });
     * </p>
     */
    public static void newTempListener(Class<? extends Event> eventClass, EventTask<? extends MessageEvent> runnable) {
        TempListenerSet tempListenerSet = TempListenerSet.getInstance();
        //创建监听：允许监听自定义事件
        Listener<? extends Event> tempListener = GlobalEventChannel.INSTANCE.subscribeAlways(eventClass, event -> {
            //多线程执行处理方法，防止处理方法中有阻塞操作
            NewThreadPoolUtils.getInstance().execute(() -> {
                //执行任务类中的方法，并接受返回值
                boolean run = false;
                try {
                    run = runnable.run(event);
                } catch (Exception e) {
                    log.error("临时监听执行失败：TempListener[{}]", runnable, e);
                }
                //返回值为 true 时关闭临时监听,或者监听时间大于了监听存活时间时关闭监听
                if (run || System.currentTimeMillis() >= runnable.getTimeOfDeath()) {

                    //从临时监听集合中获取到监听并注销他
                    //监听的key就是一个任务类的实例对象，他在内存中是唯一的
                    tempListenerSet.get(runnable).complete();
                    try {
                        //移除临时监听
                        tempListenerSet.remove(runnable);
                    } catch (Exception e) {
                        log.error("TempListener[" + runnable + "]" + " 临时监听关闭失败", e);
                    }
                    log.debug(runnable + "  临时监听{}关闭:  ", (System.currentTimeMillis() >= runnable.getTimeOfDeath()) ? "超时" : "主动");
                }
            });
        });
        //添加监听到集合
        tempListenerSet.put(runnable, tempListener);
    }

}

