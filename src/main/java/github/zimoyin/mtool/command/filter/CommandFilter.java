package github.zimoyin.mtool.command.filter;


import github.zimoyin.mtool.annotation.Filter;
import github.zimoyin.mtool.command.CommandData;
import github.zimoyin.mtool.command.CommandObj;
import github.zimoyin.mtool.command.CommandSet;
import github.zimoyin.mtool.command.filter.impl.LevelFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 执行命令过滤器
 */
public class CommandFilter {
    private final CommandData data;
    private final CommandSet<String, CommandObj> commands = CommandSet.getInstance();
    private Filter annotation;
    private final Logger logger = LoggerFactory.getLogger(CommandFilter.class);

    public CommandFilter(CommandData data) {
        this.data = data;
        init();
    }

    private void init() {
        //命令对象
        CommandObj commandObj = commands.get(data);
        if (commandObj == null) return;
        annotation = commandObj.getMethod().getAnnotation(Filter.class);
    }


    public boolean execute() {
        if (annotation == null) return true;//没有过滤器注解就默认放行
        boolean result = true;
        Class<? extends AbstractFilter>[] filterCls = annotation.filterCls();
        for (Class<? extends AbstractFilter> cls : filterCls) {
            try {
                boolean filter = (boolean) cls.getMethod("filter", CommandData.class).invoke(cls.newInstance(), data);
                result = result && filter;
                logger.debug("过滤器:{}  放行: {}",cls,result);
                if (!result) return false;
            } catch (Exception e) {
                logger.error("过滤器执行失败",e);
            }
        }
        //执行默认实现过滤器
        boolean filter = new LevelFilter().filter(data);
        result = result && filter;
        return result;
    }
}
