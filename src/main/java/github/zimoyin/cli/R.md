## 创建命令
```java
@Shell(value = "login",alias={})
public class ShellTest implements IShell {

    @Shell.Parameter("-user")
    private String user;

    @Override
    public void execute() {
        System.out.println("login: "+user);
    }
}
```

```java
@Shell(value = "login",alias={})
public class ShellTest {

    @Shell.Parameter("-user")
    private String user;

    @Shell.Main
    public void execute() {
        System.out.println("login: "+user);
    }
}
```
## 创建子命令
```java
@Shell(value = "woc",parentCommand = ShellTest.class)
public class ShellTest3 extends ShellTest2{
    @Shell.Parameter("-a")
    private boolean isLogin = false;

    @Shell.Main
    @Override
    public void execute() {
        super.execute();
    }
}
```

## 解析应用启动时传入的命令
```java
public class MainCLI {
    public static void main(String[] args) throws NoSuchMethodException, IllegalAccessException {
        new MainArgs(ShellTest.class,args);
    }
}
```

```
设置的程序参数： -user zimo
```