## 创建命令
当命令有一个或零个时，允许省略命令参数名称直接附带参数值即可，如：help -help login  -> help login 。此表示下前一种为错误表示
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