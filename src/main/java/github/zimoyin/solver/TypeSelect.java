package github.zimoyin.solver;

import github.zimoyin.mtool.uilt.OSinfo;
import github.zimoyin.solver.gui.LoginSolverGuiRun;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TypeSelect {
    private TypeSelect() {
    }

    public static void open() {
        if (OSinfo.isWindows()) LoginSolverGuiRun.getInstance();
        else log.error("不支持的系统类型");
    }
}
