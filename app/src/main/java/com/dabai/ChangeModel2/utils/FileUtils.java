package com.dabai.ChangeModel2.utils;


/**
 * @author 故事与猫
 * @time 2019年5月20日 下午7:24:38
 */

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Scanner;
import java.io.*;

public class FileUtils {

    /**
     * @param 文件工具类 (非Windows端)
     * @throws IOException
     */



    public static String getPath(Context context, Uri uri) throws URISyntaxException {
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = { "_data" };
            Cursor cursor = null;

            try {
                cursor = context.getContentResolver().query(uri, projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
                // Eat it
            }
        }
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }



    /**
     * 删除文件，可以是文件或文件夹
     *
     * @param fileName 要删除的文件名
     * @return 删除成功返回true，否则返回false
     */


    public static boolean delete(String fileName) {
        File file = new File(fileName);
        if (!file.exists()) {
            System.out.println("删除文件失败:" + fileName + "不存在！");
            return false;
        } else {
            if (file.isFile())
                return deleteFile(fileName);
            else
                return deleteDirectory(fileName);
        }
    }

    /**
     * 删除单个文件
     *
     * @param fileName 要删除的文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    public static boolean deleteFile(String fileName) {
        File file = new File(fileName);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                System.out.println("删除单个文件" + fileName + "成功！");
                return true;
            } else {
                System.out.println("删除单个文件" + fileName + "失败！");
                return false;
            }
        } else {
            System.out.println("删除单个文件失败：" + fileName + "不存在！");
            return false;
        }
    }

    /**
     * 删除目录及目录下的文件
     *
     * @param dir 要删除的目录的文件路径
     * @return 目录删除成功返回true，否则返回false
     */
    public static boolean deleteDirectory(String dir) {
        // 如果dir不以文件分隔符结尾，自动添加文件分隔符
        if (!dir.endsWith(File.separator))
            dir = dir + File.separator;
        File dirFile = new File(dir);
        // 如果dir对应的文件不存在，或者不是一个目录，则退出
        if ((!dirFile.exists()) || (!dirFile.isDirectory())) {
            System.out.println("删除目录失败：" + dir + "不存在！");
            return false;
        }
        boolean flag = true;
        // 删除文件夹中的所有文件包括子目录
        File[] files = dirFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            // 删除子文件
            if (files[i].isFile()) {
                flag = deleteFile(files[i].getAbsolutePath());
                if (!flag)
                    break;
            }
            // 删除子目录
            else if (files[i].isDirectory()) {
                flag = deleteDirectory(files[i].getAbsolutePath());
                if (!flag)
                    break;
            }
        }
        if (!flag) {
            System.out.println("删除目录失败！");
            return false;
        }
        // 删除当前目录
        if (dirFile.delete()) {
            System.out.println("删除目录" + dir + "成功！");
            return true;
        } else {
            return false;
        }
    }

    /**
     * 创建新文件 会自动创建父目录
     *
     * @param filePath
     * @return
     * @throws IOException
     */

    public boolean createFile(String filePath) throws IOException {
        File file = new File(filePath);
        String fileParent = file.getParent();
        System.out.println(fileParent);

        File par = new File(fileParent);
        if (!par.exists()) {
            par.mkdirs();
        }

        if (!file.exists()) {
            if (file.createNewFile()) {
                return true;
            } else {
                return false;
            }

        } else {
            System.out.println("文件已存在");
        }

        return false;
    }

    /**
     * 创建新文件(带内容) 会自动创建父目录
     *
     * @param filePath
     * @param text
     * @return
     * @throws IOException
     */
    public boolean createFile(String filePath, String text) throws IOException {
        File file = new File(filePath);
        String fileParent = file.getParent();
        System.out.println(fileParent);

        File par = new File(fileParent);
        if (!par.exists()) {
            par.mkdirs();
        }

        if (!file.exists()) {
            if (file.createNewFile()) {
                // 文件只有在创建成功时，才会写入text
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(text.getBytes());
                fos.flush();
                fos.close();

                return true;
            } else {
                return false;
            }

        } else {
            System.out.println("文件已存在");
        }

        return false;
    }

    /**
     * 返回文件对象列表(文件数组)
     *
     * @param filePath
     * @return
     */
    public File[] getFileList(String filePath) {

        File file = new File(filePath);

        if (file.isDirectory() && file.exists()) {
            return file.listFiles();
        } else {
            System.out.println("此File对象不是文件夹或不存在");
        }

        return null;
    }

    /**
     * 返回文件对象列表(字符串数组)
     *
     * @param filePath
     * @return
     */
    public String[] getFileListString(String filePath) {

        File file = new File(filePath);

        if (file.isDirectory() && file.exists()) {
            return file.list();
        } else {
            System.out.println("此File对象不是文件夹或不存在");
        }

        return null;
    }

    public static String getString(InputStream inputStream) {
        InputStreamReader inputStreamReader = null;

        inputStreamReader = new InputStreamReader(inputStream);

        BufferedReader reader = new BufferedReader(inputStreamReader);
        StringBuffer sb = new StringBuffer("");
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }


    public static String read_file(String filepath) {
        File file = new File(filepath);
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return getString(fileInputStream);
    }

    /***
     * 写入文本
     *
     * @param filePath 文件路径 a 是否覆盖
     * @throws IOException
     */

    public void writeText(String filePath, String text, boolean a) throws IOException {

        if (a) {
            // 覆盖

            try {
                File fs = new File(filePath);
                FileOutputStream outputStream =new FileOutputStream(fs);
                outputStream.write(text.getBytes());
                outputStream.flush();
                outputStream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // 追加

            RandomAccessFile raf = new RandomAccessFile(filePath, "rw");
            raf.seek(raf.length());
            String tex = text + "";
            raf.write(tex.getBytes());
            raf.close();
        }
    }

    /***
     * FileReader读取文本方式
     *
     * @param filePath 文件路径
     * @return 返回内容
     * @throws IOException
     */

    public String readText(String filePath) throws IOException {

        File file = new File(filePath);

        FileReader fileReader = new FileReader(file);

        char data[] = new char[(int) file.length()];
        StringBuffer stringBuffer = new StringBuffer();
        while (-1 != fileReader.read(data)) {
            stringBuffer.append(data);
        }

        fileReader.close();
        return stringBuffer.toString();
    }

    /***
     * Scanner读取文本方式
     * @return 返回内容
     * @throws IOException
     */

    public String readText(File file) throws FileNotFoundException {

        Scanner sc = new Scanner(file);

        StringBuffer stringBuffer = new StringBuffer();

        while (sc.hasNextLine()) {
            stringBuffer.append(sc.nextLine() + "\n");
        }

        sc.close();
        return stringBuffer.toString();
    }

    /***
     *
     * @param oldFilePath 源文件路径
     * @param newFilePath 新文件路径
     * @param a           是否覆盖
     * @return 是否成功
     * @throws IOException
     */
    public boolean copyFile(String oldFilePath, String newFilePath, boolean a) throws IOException {

        File oldFile = new File(oldFilePath);
        File newFile = new File(newFilePath);

        // 覆盖复制
        if (a) {

            // io流 (两个文件)

            BufferedInputStream fisFileInputStream = new BufferedInputStream(new FileInputStream(oldFile));

            BufferedOutputStream fosFileOutputStream = new BufferedOutputStream(new FileOutputStream(newFile));

            // 源文件长度
            int total = fisFileInputStream.available();

            // 数据读取
            byte data[] = new byte[total];
            fisFileInputStream.read(data);

            fosFileOutputStream.write(data);
            // 关闭流
            fisFileInputStream.close();
            fosFileOutputStream.flush();
            fosFileOutputStream.close();

            System.out.println(new File(newFilePath).length());

            if (new File(oldFilePath).length() == new File(newFilePath).length()) {
                System.out.println("复制成功");

                return true;
            }

        } else {
            // 不覆盖复制

            if (!newFile.exists()) {
                // io流 (两个文件)
                BufferedInputStream fisFileInputStream = new BufferedInputStream(new FileInputStream(oldFile));
                BufferedOutputStream fosFileOutputStream = new BufferedOutputStream(new FileOutputStream(newFile));

                // 源文件长度
                int total = fisFileInputStream.available();

                // 数据读取
                byte data[] = new byte[total];
                fisFileInputStream.read(data);

                fosFileOutputStream.write(data);
                fisFileInputStream.close();
                fosFileOutputStream.flush();
                fosFileOutputStream.close();
                if (new File(oldFilePath).length() == new File(newFilePath).length()) {
                    System.out.println("复制成功");

                    return true;
                }

            } else {
                System.out.println("目标文件已存在");

                return false;
            }
        }

        System.out.println("未知错误");
        return false;
    }

}
