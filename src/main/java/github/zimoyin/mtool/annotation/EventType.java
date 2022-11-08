package github.zimoyin.mtool.annotation;

import net.mamoe.mirai.event.Event;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
@Inherited

public @interface EventType {
    Class<? extends Event> value() default Event.class;
}
