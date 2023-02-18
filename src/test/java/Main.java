import com.alibaba.fastjson2.JSONObject;
import github.zimoyin.application.command.chatgpt.api.cofig.ChatGPTConfig;
import github.zimoyin.application.command.chatgpt.api.cofig.ChatGPTQuota;
import github.zimoyin.application.command.chatgpt.api.server.ChatAPI;
import github.zimoyin.application.dao.chat.Thesaurus;
import github.zimoyin.application.dao.chat.ThesaurusDao;
import github.zimoyin.application.uilts.MybatisUtils;
import github.zimoyin.mtool.command.filter.AbstractFilter;
import github.zimoyin.mtool.command.filter.GlobalFilterInitOrExecute;
import github.zimoyin.mtool.command.filter.impl.LevelFilter;
import github.zimoyin.solver.gui.LoginSolverGuiRun;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;
import java.util.function.BiConsumer;


@Slf4j
public class Main {
    public static void main(String[] args) throws NoSuchFieldException, InstantiationException, IllegalAccessException {
        //获取SqlSession 对象
        SqlSession sqlSession = null;

        try {
            //获取SqlSession 对象
            sqlSession = MybatisUtils.getSqlSession();
            //执行SQL


            //方式一:getMapper
            ThesaurusDao mapper = sqlSession.getMapper(ThesaurusDao.class);
//            boolean b = mapper.addThesaurus(new Thesaurus("agag", "age", "ega"));
            for (Thesaurus thesaurus : mapper.getThesaurus()) {
                System.out.println(thesaurus);
            }
        }finally {
            //关闭sqlsession
            sqlSession.close();
        }
    }


}
