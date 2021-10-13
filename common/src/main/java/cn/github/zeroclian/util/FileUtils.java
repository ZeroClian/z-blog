package cn.github.zeroclian.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * 文件工具类
 *
 * @Author: qiyiguo
 * @Date: 2021-10-13 4:58 下午
 */
public class FileUtils {

    /**
     * 文件转为字符串
     *
     * @param
     * @return
     * @throws IOException
     */
    public static String file2String(String filePath) throws IOException {
        StringBuffer buffer = new StringBuffer();
        BufferedReader bf = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8));
        String s = null;
        //使用readLine方法，一次读一行
        while ((s = bf.readLine()) != null) {
            buffer.append(s + "\n");
        }
        return buffer.toString();
    }


}
