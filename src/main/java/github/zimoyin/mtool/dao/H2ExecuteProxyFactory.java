package github.zimoyin.mtool.dao;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Proxy;
import java.sql.Statement;
import java.util.Arrays;

/**
 * 创建动态代理对象
 * 动态代理不需要实现接口,但是需要指定接口类型
 */
@Slf4j
public class H2ExecuteProxyFactory {

    //维护一个目标对象
    private final Object target;

    public H2ExecuteProxyFactory(Statement target) {
        this.target = target;
    }

    //给目标对象生成代理对象
    public Statement getProxyInstance() {
        return (Statement) Proxy.newProxyInstance(
                target.getClass().getClassLoader(),
                target.getClass().getInterfaces(),
                (proxy, method, args) -> {
                    //执行目标对象方法
                    Object returnValue = null;
                    try {
                        returnValue = method.invoke(target, args);
                    } catch (Exception e) {
                        if (method.getName().equalsIgnoreCase("execute")) log.info("SQL Ages Error: {}", args != null ? Arrays.asList(args) : "");
                        else log.error("H2的动态代理类执行，执行被代理类方法时出现异常",e);
                    }
                    if (method.getName().equalsIgnoreCase("execute")) log.info("SQL Args: {}", args != null ? Arrays.asList(args) : "");
                    return returnValue;
                }
        );
    }

}

