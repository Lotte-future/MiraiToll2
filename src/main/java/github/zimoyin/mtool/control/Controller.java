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
     */
    public static void init() {
        for (Class<?> cls : FindClass.getResultsToClasses()) {
            //如果是Controller的子类说明是处理器类
            if (cls.getSuperclass() == Controller.class) {
                ListeningRegistration.registration(cls);
            }
            //被@Controller修饰就说明是处理器类
            else if (cls.isAnnotationPresent(github.zimoyin.mtool.annotation.Controller.class)) {
                ListeningRegistration.registration(cls);
            }
        }

        List<String> clazzName = FindClass.getResults();
        //对集合进行排序
        Collections.sort(clazzName);
//        for (String clsName : clazzName) {
//            try {
//                //如果是Controller的子类说明是处理器类
//                Class<?> cls = Class.forName(clsName);
//                if (cls.getSuperclass() == Controller.class) {
////                    aClass.newInstance();
////                    NewListener newListener = new NewListener(cls);
//                    new ListeningRegistration(cls);
//                }
//                //被@Controller修饰就说明是处理器类
//                if (cls.isAnnotationPresent(github.zimoyin.mtool.annotation.Controller.class)) {
////                    NewListener newListener = new NewListener(cls);
//                    new ListeningRegistration(cls);
//                }
//
//            } catch (Exception e) {
//                logger.error(clsName + " 类加载失败,该类下的监听无法被创建：(可能子类覆盖了无参构造或子类不是一个具体的实现类导致异常)", e);
//            }
//        }
    }
}
