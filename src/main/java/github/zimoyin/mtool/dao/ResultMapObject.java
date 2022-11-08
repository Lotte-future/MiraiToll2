package github.zimoyin.mtool.dao;

import com.alibaba.fastjson.JSONObject;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 将查询结果转换为对象
 */
public class ResultMapObject<T> {
    public List<T> parseObject(ResultSet resultSet, Class<T> cls) throws SQLException {
        List<T> objectList = new ArrayList<>();
        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();
        // 将ResultSet对象的列名和值存到map中，再将map转换为json字符串，最后将json字符串转换为实体类对象
        Map<String, Object> rowData = new HashMap<>();
        while (resultSet.next()) {
            for (int i = 1; i <= columnCount; i++) {
                rowData.put(metaData.getColumnLabel(i), resultSet.getObject(i));
            }
            String jsonStr = JSONObject.toJSONString(rowData);
            objectList.add(JSONObject.parseObject(jsonStr, cls));
        }
        return objectList;
    }

    public List<T> parseObject(long botid, String sql, Class<T> cls) throws SQLException {
        H2Connection connectionpool = H2Connection.getInstance();
        try (Connection connection = connectionpool.getConnection(botid); Statement statement = H2Connection.getInstance().getStatement(connection)) {
            ResultSet resultSet = statement.executeQuery(sql);
            return parseObject(resultSet, cls);
        }
    }

    public List<T> parseObject(String sql, Class<T> cls) throws SQLException {
        H2Connection connectionpool = H2Connection.getInstance();
        try (Connection connection = connectionpool.getConnection(); Statement statement = H2Connection.getInstance().getStatement(connection)) {
            ResultSet resultSet = statement.executeQuery(sql);
            return parseObject(resultSet, cls);
        }
    }
}
