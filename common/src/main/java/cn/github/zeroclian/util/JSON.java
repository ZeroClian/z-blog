package cn.github.zeroclian.util;

import cn.github.zeroclian.jackson.deserializer.LocalDateExtDeserializer;
import cn.github.zeroclian.jackson.deserializer.LocalDateTimeExtDeserializer;
import cn.github.zeroclian.jackson.serializer.LocalDateExtSerializer;
import cn.github.zeroclian.jackson.serializer.LocalDateTimeExtSerializer;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * @Author: ZeroClian
 * @Date: 2021-09-08 2:07 下午
 */

public abstract class JSON {
    public static final boolean WRITE_DATES_AS_TIMESTAMPS = false;
    private static final ObjectMapper mapper;
    private static final ObjectMapper withEmptyMapper;
    private static final ObjectMapper withOutAnnotationMapper;
    private static final SimpleModule dateModule;
    private static final Logger logger = LoggerFactory.getLogger(JSON.class);

    static {
        //datetime parse
        dateModule = new SimpleModule();
        //配置序列化
        dateModule.addSerializer(LocalDateTime.class, new LocalDateTimeExtSerializer(WRITE_DATES_AS_TIMESTAMPS));
        dateModule.addSerializer(LocalDate.class, new LocalDateExtSerializer(WRITE_DATES_AS_TIMESTAMPS));
        dateModule.addSerializer(LocalTime.class, new LocalTimeSerializer(DateTimeFormatter.ofPattern("HH:mm:ss")));
//        dateModule.addSerializer(Date.class, new DateSerializer(false, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")));
        //配置反序列化
        dateModule.addDeserializer(LocalDateTime.class, new LocalDateTimeExtDeserializer());
        dateModule.addDeserializer(LocalDate.class, new LocalDateExtDeserializer());
        dateModule.addDeserializer(LocalTime.class, new LocalTimeDeserializer(DateTimeFormatter.ofPattern("HH:mm:ss")));
//        DateDeserializers.DateDeserializer dateDeserializer = new DateDeserializers.DateDeserializer();
//        ReflectUtils.setFieldValue(dateDeserializer, "_customFormat", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
//        dateModule.addDeserializer(Date.class, dateDeserializer);
        //without empty
        mapper = new ObjectMapper();
        mapper.setSerializationInclusion(Include.NON_EMPTY);
        buldCommonMapper(mapper);
        //within empty
        withEmptyMapper = new ObjectMapper();
        withEmptyMapper.setSerializationInclusion(Include.ALWAYS);
        buldCommonMapper(withEmptyMapper);
        withOutAnnotationMapper = new ObjectMapper();
        withOutAnnotationMapper.setSerializationInclusion(Include.ALWAYS);
        buldCommonMapper(withOutAnnotationMapper);
        withOutAnnotationMapper.configure(MapperFeature.USE_ANNOTATIONS, false);
    }

    private JSON() {
    }

    /**
     * 设置mappepr的通用属性
     */
    private static void buldCommonMapper(ObjectMapper mapper) {
        mapper.registerModule(dateModule);
        // 允许反序列化空json对象, example: {}
        mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
        // 允许key没有引号
        mapper.configure(Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        // 允许单引号
        mapper.configure(Feature.ALLOW_SINGLE_QUOTES, true);
        // 大小写不敏感
        mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
        // 当序列化存在未知属性时不报错
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static <T> T convert(Object origin, Class<T> targetClass) {
        return mapper.convertValue(origin, targetClass);
    }

    /**
     * 将list转为另一个list
     *
     * @param originList  原始List
     * @param targetClass 目标类型
     * @return 目标类型List
     */
    public static <O extends Collection<?>, T> List<T> convertList(O originList, Class<T> targetClass) {
        JavaType javaType = mapper.getTypeFactory().constructParametricType(originList.getClass(), targetClass);
        return mapper.convertValue(originList, javaType);
    }

    /**
     * 将对象转换成json
     *
     * @param originalObject 要转换的对象
     */
    public static String toJSON(Object originalObject) {

        if (originalObject == null) {
            return null;
        }
        if (originalObject instanceof String) {
            return String.valueOf(originalObject);
        }

        return unwrapError(() -> mapper.writeValueAsString(originalObject));
    }

    /**
     * 将对象转换成json, 忽略字段注解
     *
     * @param originalObject 要转换的对象
     */
    public static String toJSONIgnoreAnnotation(Object originalObject) {

        if (originalObject == null) {
            return null;
        }
        if (originalObject instanceof String) {
            return String.valueOf(originalObject);
        }

        return unwrapError(() -> withOutAnnotationMapper.writeValueAsString(originalObject));
    }

    /**
     * 将对象转换成json字节数组
     *
     * @param originalObject 要转换的对象
     */
    public static byte[] toJsonByte(Object originalObject) {
        if (originalObject == null) {
            return null;
        }
        return unwrapError(() -> mapper.writeValueAsBytes(originalObject));
    }


    /**
     * 将对象转换成json,并包含空属性
     *
     * @param originalObject 要转换的对象
     */
    public static String toJsonWithEmpty(Object originalObject) {
        if (originalObject == null) {
            return null;
        }
        return unwrapError(() -> withEmptyMapper.writeValueAsString(originalObject));
    }

    /**
     * 将json转成List
     *
     * @param json      json字符串
     * @param valueType list泛型
     */
    public static <T> List<T> parseList(String json, Class<T> valueType) {

        return (List<T>) parseCollection(json, List.class, valueType);
    }

    /**
     * 将json转成List
     *
     * @param json      json字符串
     * @param valueType list泛型
     */
    public static <T, E extends Collection<?>> Collection<T> parseCollection(String json, Class<E> collectionClass, Class<T> valueType) {

        if (StringUtils.isEmpty(json) || valueType == null) {
            return null;
        }

        JavaType javaType = mapper.getTypeFactory().constructParametricType(collectionClass, valueType);

        return unwrapError(() -> mapper.readValue(json, javaType));
    }

    /**
     * 将json转成指定的类对象
     */
    @SuppressWarnings("unchecked")
    public static <T> T parse(String json, Class<T> type) {

        if (StringUtils.isEmpty(json) || type == null) {
            return null;
        }
        if (type == String.class) {
            return (T) json;
        }

        return unwrapError(() -> mapper.readValue(json, type));
    }

    /**
     * 将json字节流转成指定的类对象
     */
    @SuppressWarnings("unchecked")
    public static <T> T parse(byte[] jsonBytes, Class<T> type) {
        if (jsonBytes == null || jsonBytes.length == 0 || type == null) {
            return null;
        }
        return unwrapError(() -> mapper.readValue(jsonBytes, type));
    }

    public static SimpleModule getDateModule() {
        return dateModule;
    }

    /**
     * 转换受检隐藏异常
     *
     * @param runnable
     * @param <T>
     * @return
     */
    private static <T> T unwrapError(Callable<T> runnable) {
        try {
            return runnable.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}

