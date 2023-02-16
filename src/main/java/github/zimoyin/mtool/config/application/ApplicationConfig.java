package github.zimoyin.mtool.config.application;

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
 */
@Slf4j
public abstract class ApplicationConfig extends HashMap<String, Object> {
    private final HashMap<String, Object> config = this;
    public final String RootPath = "./data/config/application/";
    public Charset charset = StandardCharsets.UTF_8;

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
            List<Field> fields = getFields();
            for (Object key : test.keySet()) {
                caseType(key.toString().trim(), test.get(key).toString(), fields);
            }
        }
    }

    /**
     * 将键值对放入hashmap集合，并为相应字段赋值
     *
     * @param key
     * @param value
     * @param fields
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

    public Field findField(String name, List<Field> fields) {
        return fields.stream().filter(field -> field.getName().equals(name)).findFirst().orElse(null);
    }

    private void newFile() throws IOException {
        initField();
        save();
    }

    public void setFieldValue(Object obj) {
        List<Field> fields = getFields();
        for (Field field : fields) {
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
        for (Field field : getFields()) {
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
        }
        for (Field field : remFields) {
            fields.remove(field);
        }
        return fields;
    }
}
