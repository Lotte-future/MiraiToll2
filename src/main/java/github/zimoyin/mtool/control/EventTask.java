package github.zimoyin.mtool.control;

import github.zimoyin.mtool.annotation.EventType;
import net.mamoe.mirai.event.Event;

public interface EventTask<T extends Event> {

    long lifeLength = 5*60*1000;//临时变量存活的时间 5min
    final long createTime = System.currentTimeMillis();//获取到创建时间
    long timeOfDeath = createTime +lifeLength;//变量预计在什么时间死亡
    /**
     * 临时事件执行方法
     *
     * @param event
     * @return 返回true就结束这个事件
     */
    @EventType
    public boolean run(T event);

    public default boolean run(Object obj) {
        if (obj == null) throw  new NullPointerException("事件为空");
        return run((T) obj);
    }

    public default long getTimeOfDeath(){return timeOfDeath;}
    public default long getCreateTime(){return createTime;}
    public default long getLifeLength(){return lifeLength;}
}
