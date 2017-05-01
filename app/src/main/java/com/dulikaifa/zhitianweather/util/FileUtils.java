package com.dulikaifa.zhitianweather.util;

import android.content.Context;

import com.orhanobut.logger.Logger;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {

    private static Context mContext;

    private static FileUtils fileUtil;

    private FileUtils() {
    }

    public static FileUtils getInstance(Context context) {
        mContext = context;
        if (fileUtil == null) {
            fileUtil = new FileUtils();
        }
        return fileUtil;
    }

    /**
     * 获取文件内容
     *
     * @throws JSONException
     */
    public List<String> getFileContent(String fileName) {
        List<String> list = new ArrayList<>();
        BufferedReader reader = null;
        try {
            String path = getFilePath(fileName);
            File files=new File(path);
            reader = new BufferedReader(new FileReader(files));
            String line = null;
            while ((line = reader.readLine()) != null) {
                list.add(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    /**
     * 将集合内容保存到文件
     *
     * @throws IOException
     */
    public void saveFileContent(String fileName, List<String> list) {
        BufferedWriter writer = null;
        try {
            String filePath = getFilePath(fileName);
            File files = new File(filePath);

            if (list.isEmpty()) {
                files.delete();
            } else {
                File file1 = new File(filePath);
                writer = new BufferedWriter(new FileWriter(file1));
                for (String name : list) {
                    writer.write(name);
                    writer.newLine();
                    writer.flush();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public long getFileLength(String fileName) {
        String filePath = null;
        try {
            filePath = getFilePath(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        File file = new File(filePath);
        return file.length();

    }

    /**
     * 判断文件是否存在
     *
     * @throws IOException
     */
    public boolean isExist(String fileName) {
        String filePath = null;
        try {
            filePath = getFilePath(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        File file = new File(filePath);
        return file.exists();

    }

    private String getFilePath(String fileName) throws IOException {
        String parentPath = getParentPath();
        File file = new File(parentPath, fileName);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file.getAbsolutePath();
    }

    /**
     * 获取根路径
     *
     * @return
     */
    private String getParentPath() {
        String path = mContext.getFilesDir().getAbsolutePath();
        Logger.d("我的",path);
        Logger.d(path);

        return path;
    }


    /**
     * 递归删除 文件夹（包含里面的文件）
     *
     * @throws IOException
     */
    public void deleteFolder(String fileName) throws IOException {
        File file = new File(getFilePath(fileName));
        if (!file.exists())
            return;
        if (file.isDirectory()) {
            File files[] = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                deleteFolder(fileName);
            }
        }
        file.delete();
    }
}
