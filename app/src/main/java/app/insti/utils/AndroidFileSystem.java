package app.insti.utils;

import android.os.StatFs;
import android.util.Log;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;

/**
 * Created by Shivam Sharma on 13-08-2018.
 */

public class AndroidFileSystem
{
    private static final String TAG = "AndroidFileSystem";

    public static void writeByteArrayToFile(byte[] bytes, File targetFile) throws IOException
    {
        FileUtils.writeByteArrayToFile(targetFile, bytes);
    }

    public static byte[] readFileToByteArray(File sourceFile) throws IOException
    {
        return FileUtils.readFileToByteArray(sourceFile);
    }

    public static void closeQuietly(InputStream inputStream)
    {
        IOUtils.closeQuietly(inputStream);
    }

    public static Collection<File> listFiles(File directory, String[] extensions, boolean recursive)
    {
        Log.i(TAG, "directory = " + directory);
        return FileUtils.listFiles(directory, extensions, recursive);
    }

    public static void closeQuietly(OutputStream outputStream)
    {
        IOUtils.closeQuietly(outputStream);
    }

    public static void forceMkdir(File directory) throws IOException
    {
        FileUtils.forceMkdir(directory);
    }

    public static void copy(InputStream inputStream, OutputStream outputStream) throws IOException
    {
        IOUtils.copy(inputStream, outputStream);
    }

    public static void forceDelete(File file) throws IOException
    {
        FileUtils.forceDelete(file);
    }

    public static long getUsableSpaceKb(String pathName)
    {
        StatFs statFs = new StatFs(pathName);
        return Math.abs(statFs.getAvailableBlocks() * statFs.getBlockSize() / 1024);
    }

    public static long getTotalSpaceKb(String pathName)
    {
        StatFs statFs = new StatFs(pathName);
        return Math.abs(statFs.getBlockCount() * statFs.getBlockSize() / 1024);
    }

    public static Collection<File> getAllFiles(String mediaFilesDir)
    {
        return FileUtils.listFiles(new File(mediaFilesDir), null, false);
    }

    public static void deleteFileQuietly(File file)
    {
        try
        {
            FileUtils.forceDelete(file);
        }
        catch (IOException e)
        {
            Log.e(TAG, "Fail to delete file [" + file.getName() + "] . Error: " + e, e);
        }
    }


    public static void cleanDirectory(File file)
    {

        try {
            FileUtils.cleanDirectory(file);
        } catch (IOException e) {
            Log.e(TAG,"Fail to clean directory "+file.getAbsolutePath());
            e.printStackTrace();
        }
    }

}
