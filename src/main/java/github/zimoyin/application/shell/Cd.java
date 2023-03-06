package github.zimoyin.application.shell;

import github.zimoyin.cli.annotation.Shell;
import github.zimoyin.cli.command.IShell;
import net.mamoe.mirai.Bot;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Shell(value = "cd",description = "进入指定的会话窗口")
public class Cd implements IShell {

    @Shell.Parameter("-p")
    private String path;

    @Override
    public void execute() {
        if (path == null || path.isEmpty()) {
            ShellParametersCentre.getInstance().put("pwd","/");
            return;
        }
        ShellParametersCentre.getInstance().putIfAbsent("pwd", "/");
        try {
            setPath(path);
        }catch (Exception e) {
            System.err.println("无法进入目录,请检查是否存在该会话: "+path);
        }

    }
    private final List<String> paths = new ArrayList<String>();
    private boolean setPath(String path){
        paths.clear();
        Path pathPwd = Paths.get(ShellParametersCentre.getInstance().get("pwd").toString());
        Path path0 = Paths.get(path);

        for (Path val : pathPwd) paths.add(val.toString());
        for (Path val : path0) {
            if (val.toString().equalsIgnoreCase(".")) continue;
            if (val.toString().equalsIgnoreCase("..")) {
                if (paths.size() == 1) continue;
                paths.remove(paths.size() - 1);
                continue;
            }
            paths.add(val.toString());
        }
        StringBuffer buffer = new StringBuffer();
        for (String val : paths) buffer.append("/").append(val);
        Pwd pwd = new Pwd(buffer.toString());
        ShellParametersCentre.getInstance().put("pwd", pwd);

        return true;
    }
}
