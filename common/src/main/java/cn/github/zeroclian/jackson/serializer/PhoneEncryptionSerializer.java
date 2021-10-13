package cn.github.zeroclian.jackson.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.util.Objects;

/**
 * 手机号码加密序列化器
 *
 * @author ZeroClian
 */
public class PhoneEncryptionSerializer extends JsonSerializer<String> {

    // 手机加密正则表达式
    public static final String PHONE_ENCRYPTION_REGULAR_EXPRESSION = "(\\d{3})\\d{5}(\\d{3})";

    @Override
    public void serialize(String value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (Objects.isNull(value)) {
            gen.writeNull();
            return;
        }
        String text = value.replaceAll(PHONE_ENCRYPTION_REGULAR_EXPRESSION, "$1*****$2");
        gen.writeString(text);
    }
}
