package github.zimoyin.mtool.annotation;

import github.zimoyin.mtool.command.filter.AbstractFilter;
import github.zimoyin.mtool.command.filter.Level;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
@Inherited
public @interface Filter {
    Level value() default Level.UNLevel;//过滤等级，注意此属性不受过滤器影响
    Class<? extends AbstractFilter>[] filterCls() default {AbstractFilter.class};//过滤器
}
