package github.zimoyin.mtool.config.application;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import github.zimoyin.mtool.uilt.JSONString;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 应用程序配置文件
 * 注意配置文件中不可出现中文，空格等特殊字符
 */
@Slf4j
public abstract class ApplicationConfig extends HashMap<String, Object> {
    private final HashMap<String, Object> config = this;
    public final String RootPath = "./data/config/application/";
    public Charset charset = StandardCharsets.UTF_8;
    private final  List<Field> Fields = getFields();

    public ApplicationConfig(boolean isInit) {
        if (isInit) init();
    }

    public ApplicationConfig(Charset charset) {
        this.charset = charset;
        init();
    }

    private void init() {
        try {
            buildConfig();
        } catch (IOException e) {
            log.error("读取配置文件失败", e);
        }
    }

    public String getName() {
        return this.getClass().getSimpleName();
    }

    public void save() throws IOException {
        File file = new File(RootPath + getName() + ".properties");
        StringBuffer buffer = new StringBuffer();
        this.forEach((s, s2) -> buffer.append(s).append("=").append(s2.toString()).append("\n"));
        Files.write(file.toPath(), buffer.toString().getBytes());
    }

    /**
     * 读取配置文件
     */
    private void buildConfig() throws IOException {
        if (getName() == null || getName().length() == 0)
            throw new IllegalArgumentException("文件名称不合法，请使用英文名称且没有任何标点符号");
        File file = new File(RootPath + getName() + ".properties");
        if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
        if (!file.exists()) newFile();
        try (FileInputStream inputStream = new FileInputStream(file)) {
            Properties test = new Properties();
            test.load(inputStream);
            for (Object key : test.keySet()) {
                caseType(key.toString().trim(), test.get(key).toString(), Fields);
            }
        }
    }

    @Override
    public Object put(String key, Object value) {
        String valArray = instanceofStringArray(value);
        if (valArray != null) value = valArray;
        caseType(key.trim(),value.toString().trim(),Fields);
        return value;
    }

    /**
     * 将数组转为 JSON 数组格式
     */
    private String instanceofStringArray(Object value){
        if (value instanceof Object[]){
//            Object[] array = (Object[]) value;
            return JSONArray.of(value).toString();
        }
        return null;
    }

    /**
     * 将键值对放入hashmap集合，并为相应字段赋值
     *
     * @param key 键
     * @param value 值
     * @param fields 字段列表
     */
    private void caseType(String key, String value, List<Field> fields) {
        Field field = findField(key, fields);
        if (field == null) {
            this.put(key, value);
            return;
        }
        Class<?> type = field.getType();
        if (type.equals(int.class) || type.equals(Integer.class)) {
            this.put(key, Integer.parseInt(value));
        } else if (type.equals(double.class) || type.equals(Double.class)) {
            this.put(key, Double.parseDouble(value));
        } else if (type.equals(boolean.class) || type.equals(Boolean.class)) {
            this.put(key, Boolean.parseBoolean(value));
        } else if (type.equals(short.class) || type.equals(Short.class)) {
            this.put(key, Short.parseShort(value));
        } else if (type.equals(byte.class) || type.equals(Byte.class)) {
            this.put(key, Byte.parseByte(value));
        } else if (type.equals(float.class) || type.equals(Float.class)) {
            this.put(key, Float.parseFloat(value));
        } else {
            this.put(key, value);
        }
    }

    /**
     * 查找字段
     * @param name 字段名称
     * @param fields 字段列表，请通过 getFields 获取
     */
    public Field findField(String name, List<Field> fields) {
        return fields.stream().filter(field -> field.getName().equals(name)).findFirst().orElse(null);
    }

    /**
     * 创建配置文件
     */
    private void newFile() throws IOException {
        initField();
        save();
    }

    /**
     * 为当前类中所有字段赋值
     * @param obj 当前类的实例对象
     */
    public void setFieldValue(Object obj) {
        for (Field field : Fields) {
            field.setAccessible(true);
            boolean isFinal = Modifier.isFinal(field.getModifiers());
            try {
                if (!isFinal) field.set(obj, get(field.getName()));
            } catch (IllegalAccessException e) {
                log.warn("无法为 {}类的{}字段的进行注入值", this.getClass().getCanonicalName(), field.getName());
            }
        }
    }

    /**
     * 将子类的字段保存至集合内
     */
    private void initField() {
        Class<? extends ApplicationConfig> thisClass = this.getClass();
        Constructor<? extends ApplicationConfig> constructor = null;
        try {
            constructor = thisClass.getConstructor(boolean.class);
        } catch (NoSuchMethodException e) {
            log.warn("{} 类没有参数类型为 'boolean' 的构造方法，这将导致系统无法获取到该类的字段值以至于数据产生误差", thisClass.getCanonicalName());
        }
        for (Field field : Fields) {
            Object value = null;
            field.setAccessible(true);
            try {
                if (constructor != null) value = field.get(constructor.newInstance(false));
                else value = field.get(this);
            } catch (Exception e) {
                log.warn("无法获取到 {} 类的 {}成员变量的值", thisClass.getCanonicalName(), field.getName(), e);
            }
            if (value == null && constructor != null)
                log.warn("{} 类的 {}属性为 null", thisClass.getCanonicalName(), field.getName());
            this.put(field.getName(), value);
        }
    }

    /**
     * 获取当前类中字段列表
     */
    public List<Field> getFields() {
        Class<? extends ApplicationConfig> thisClass = getClass();
        List<Field> fields = new ArrayList<Field>();
        fields.addAll(Arrays.asList(thisClass.getFields()));
        fields.addAll(Arrays.asList(thisClass.getDeclaredFields()));
        //去重
        fields = (List<Field>) fields.stream().distinct().collect(Collectors.toList());
        //除去黑名单内的属性
        List<Field> remFields = new ArrayList<Field>();
        for (Field field : fields) {
            if (field.getName().equalsIgnoreCase("RootPath")) remFields.add(field);
            if (field.getName().equalsIgnoreCase("charset")) remFields.add(field);
            if (field.getName().equalsIgnoreCase("config")) remFields.add(field);
            if (field.getName().equalsIgnoreCase("log")) remFields.add(field);
            if (field.getName().equalsIgnoreCase("logger")) remFields.add(field);
            if (field.getName().equalsIgnoreCase("Fields")) remFields.add(field);
        }
        for (Field field : remFields) {
            fields.remove(field);
        }
        return fields;
    }

    public Object get(String key) {
        return super.get(key);
    }

    public String getString(String key) {
        return super.get(key).toString();
    }

    public int getInt(String key) {
        return Integer.parseInt(getString(key));
    }

    public long getLong(String key) {
        return Long.parseLong(getString(key));
    }

    public float getFloat(String key) {
        return Float.parseFloat(getString(key));
    }

    public double getDouble(String key) {
        return Double.parseDouble(getString(key));
    }

    public byte getByte(String key) {
        return Byte.parseByte(getString(key));
    }

    public short getShort(String key) {
        return Short.parseShort(getString(key));
    }

    /**
     * 返回该键值对对应的数组
     */
    public Object[] getArray(String key) {
        String string = getString(key);
        if (string == null) return null;
        return JSON.parseArray(key).toArray();
    }

    /**
     * 返回该键值对对应的数组
     */
    public String[] getArrayString(String key) {
        String string = getString(key);
        if (string == null) return null;
        List<String> strings;
        try {
            strings = JSON.parseArray(key).toList(String.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("配置文件中的数组不是一个正确的JSON数组", e);
        }
        return strings.toArray(new String[0]);
    }

    /**
     * 返回该键值对对应的数组
     */
    public String[] getArrayString2(String key) {
        String string = getString(key);
        if (string == null) return null;
        List<String> strings;
        try {
            strings = JSON.parseArray(JSONString.wrapElementsInQuotes2(string)).toList(String.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("配置文件中的数组不是一个正确的JSON数组", e);
        }
        return strings.toArray(new String[0]);
    }
}
