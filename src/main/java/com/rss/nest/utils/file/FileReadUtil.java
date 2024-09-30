package com.rss.nest.utils.file;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * @author Lemon695
 * @date: 2024/9/30 下午3:39:15
 * @description:
 */
public class FileReadUtil {


    /**
     * 读取文件内容(txt,json,html)
     *
     * @param filePathName 文件路径+名称
     * @return: java.lang.String
     * @author Lemon695
     * @date 2024/9/30 下午3:43
     */
    public static String readFile(String filePathName) {
        String jsonStr = "";
        try {
            File jsonFile = new File(filePathName);
            FileReader fileReader = new FileReader(jsonFile);
            Reader reader = new InputStreamReader(new FileInputStream(jsonFile), StandardCharsets.UTF_8);
            int ch = 0;
            StringBuilder sb = new StringBuilder();
            while ((ch = reader.read()) != -1) {
                sb.append((char) ch);
            }
            fileReader.close();
            reader.close();
            jsonStr = sb.toString();
            return jsonStr;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
