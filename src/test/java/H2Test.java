import github.zimoyin.mtool.config.global.H2Config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * @Description: 内嵌数据库H2的使用
 * @author： zxt
 * @time: 2019年6月4日 下午3:30:13
 */
public class H2Test {
    
    /**
     * 以嵌入式(本地)连接方式连接H2数据库
     */
//    private static final String JDBC_URL = "jdbc:h2:./data/db/200";
    private static final String JDBC_URL = H2Config.getInstance().getH2().getGlobal_jdbc();

    /**
     * 使用TCP/IP的服务器模式(远程连接)方式连接H2数据库(推荐)
     */
    // private static final String JDBC_URL = "jdbc:h2:tcp://10.35.14.122/C:/H2/user";

    private static final String USER = "root";
    private static final String PASSWORD = "root";
    private static final String DRIVER_CLASS = "org.h2.Driver";

    public static void main(String[] args) throws Exception {
        // TODO Auto-generated method stub
        Class.forName(DRIVER_CLASS);
        Connection conn = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
        Statement statement = conn.createStatement();
        statement.execute("DROP TABLE IF EXISTS USER_INF");
        statement.execute("CREATE TABLE USER_INF(id INTEGER PRIMARY KEY, name VARCHAR(100), sex VARCHAR(2))");

        statement.executeUpdate("INSERT INTO USER_INF VALUES(1, 'tom', '男') ");
        statement.executeUpdate("INSERT INTO USER_INF VALUES(2, 'jack', '女') ");
        statement.executeUpdate("INSERT INTO USER_INF VALUES(3, 'marry', '男') ");
        statement.executeUpdate("INSERT INTO USER_INF VALUES(4, 'lucy', '男') ");

//        ResultSet resultSet = statement.executeQuery("select * from USER_INF");
        ResultSet resultSet = statement.executeQuery("SELECT * FROM INFORMATION_SCHEMA.TABLES");

        while (resultSet.next()) {
            System.out.println();
//            System.out.println(
//                    resultSet.getInt("id") + ", " +
//                    resultSet.getString("name") + ", " +
//                    resultSet.getString("sex"));
        }

//        ResultMapObject<Test> mapObject = new ResultMapObject<>();
//        List<Test> list = mapObject.parseObject(resultSet, Test.class);
//        for (Test test : list) {
//            System.out.println(test);
//        }

        statement.close();
        conn.close();
    }

}