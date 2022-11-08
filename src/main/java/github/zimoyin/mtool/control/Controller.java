package github.zimoyin.mtool.control;

import github.zimoyin.mtool.uilt.FindClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;


/**
 * 控制器注册类
 * 注册控制器方法：
 * 1. 继承本类
 * 2. 使用注释：@Controller
 */
public abstract class Controller {
    private static final Logger logger = LoggerFactory.getLogger(Controller.class);
    /**
     * 查找本应用的类，并实例化,如果类是controller的子类那么实例化的时候会执行构造方法创建监听
     *
     * @事件执行顺序 同一类型的事件因为创建的先后顺序不同，执行顺序也不同，假设 事件A 先创建 事件B后创建 ,那么每次都是A执行完后才执行B，如果A被阻塞那么B就执行不了(因为是用线程执行的方法所以这点级看不出来),
     * 所以如果有创建你的事件头创建顺序建议冲下面硬编码下，在for循环外面创建，在for循环里面如果到了创建的时候加个if跳过去，不进行创建
     * 或者把优先执行的类名在字符串排名中比低优先的排名要高才行
     * 或者在NewListener类中把执行处理器方法的代码放到其他线程里去执行
     */
    public static void init() {

        //扫描控制器
//        List<String> clazzName = FindClass.getClazzName(FindClassConfig.path, FindClassConfig.flag);//递归扫描 github 包下的所有类
        List<String> clazzName = FindClass.getResults();

        //对集合进行排序
        Collections.sort(clazzName);


        for (String clsName : clazzName) {
            try {
                //如果是Controller的子类说明是处理器类
                Class<?> cls = Class.forName(clsName);
                if (cls.getSuperclass() == Controller.class) {
//                    aClass.newInstance();
//                    NewListener newListener = new NewListener(cls);
                    new ListeningRegistration(cls);
                }
                //被@Controller修饰就说明是处理器类
                if (cls.isAnnotationPresent(github.zimoyin.mtool.annotation.Controller.class)) {
//                    NewListener newListener = new NewListener(cls);
                    new ListeningRegistration(cls);
                }

            } catch (Exception e) {
                logger.error(clsName + " 类加载失败,该类下的监听无法被创建：(可能子类覆盖了无参构造或子类不是一个具体的实现类导致异常)", e);
            }
        }
    }
}
