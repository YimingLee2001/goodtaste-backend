package team.goodtaste.backend.common;

import java.util.HashMap;
import java.util.Map;

import lombok.Data;

/**
 * 通用返回结果，服务端响应的数据最终都会封装成此对象
 * 
 * @param <T>
 */
@Data
public class R<T> {

    private Integer code; // 编码：1成功，0和其他数字为失败

    private String msg; // 错误消息

    private T data; // 数据

    private Map<String, Object> map = new HashMap<>(); // 动态数据

    public static <T> R<T> success(T object) {
        R<T> r = new R<T>();
        r.data = object;
        r.code = 1;
        return r;
    }

    public static <T> R<T> error(String msg) {
        R<T> r = new R<T>();
        r.msg = msg;
        r.code = 0;
        return r;
    }

    public R<T> add(String key, Object value) {
        this.map.put(key, value);
        return this;
    }
}
