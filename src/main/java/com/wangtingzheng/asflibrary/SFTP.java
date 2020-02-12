package com.wangtingzheng.asflibrary;


import android.app.Activity;
import android.os.Environment;

import com.wangtingzheng.asflibrary.Utils.ListResultDealUtils;
import com.wangtingzheng.asflibrary.Utils.LoginDealUtils;
import com.wangtingzheng.asflibrary.Utils.PermisionUtils;
import com.wangtingzheng.asflibrary.Utils.ProgressMonitorUtils;
import com.wangtingzheng.asflibrary.Utils.SFTPUtils;

import java.io.InputStream;

public class SFTP {
    private SFTPUtils sftpUtils;
    private ProgressMonitorUtils sendProgressMonitor;
    private ProgressMonitorUtils getProgressMonitor;
    private ListResultDealUtils getlistResultDealUtils;

    private LoginDealUtils getloginDealUtils;

    public SFTP(String username, String password, String host, int port) {
        this.sftpUtils = new SFTPUtils(username, password, host, port);
    }

    /**
     *  send a file from android phone to sftp server
     * @param fileStream
     * @param fileName
     * @param filePath
     * @param directory
     */
    public void SFTP_Send(InputStream fileStream, String fileName, String filePath,  String directory){
        Send send = new Send(this.sftpUtils ,fileStream, fileName, filePath, directory);
    }

    /**
     * get a file from sftp server and save in android phone
     * @param downloadDirectory
     * @param downloadFile
     * @param saveFile
     * @param activity
     */
    public void SFTP_Get(String downloadDirectory, String downloadFile, String saveFile, Activity activity){
        PermisionUtils.verifyStoragePermissions(activity);
        saveFile = Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+saveFile;
        new Get(this.sftpUtils,downloadDirectory,downloadFile,saveFile);
    }

    /**
     * rename a file or directory, the file's new and old path must have suffix name
     * @param oldPath
     * @param newPath
     */
    public void SFTP_Rename(String oldPath, String newPath){
        new Rename(oldPath, newPath);
    }

    /**
     * delete file or directory in sftp server, the file's new and old path must have suffix name
     * @param deleteDirectory
     * @param deleteFile
     */
    public void SFTP_Delete(String deleteDirectory, String deleteFile){
        Delete delete = new Delete(deleteDirectory, deleteFile);
    }


    /**
     * list all file in the directory, save in vector
     * @param listDirectory
     */
    public void SFTP_List(String listDirectory){
        new List(listDirectory);
    }

    /**
     * new a folder in sftp server
     * @param newDirDirectory
     * @param newDirDir
     * @param isDeleteExist
     */
    public void SFTP_NewDir(String newDirDirectory,String newDirDir, boolean isDeleteExist){
         new NewDir(newDirDirectory, newDirDir, isDeleteExist);
    }


    public class Send{
        SFTPAction sftpAction;
        Send(SFTPUtils sftpUtils, InputStream fileStream, String fileName, String filePath, String directory){
            sftpAction = new SFTPAction(sftpUtils ,fileStream, fileName, filePath, directory,sendProgressMonitor,getloginDealUtils);
            sftpAction.execute();
        }
    }

    public class Get{
        SFTPAction sftpAction;
        Get(SFTPUtils sftpUtils, String downloadDirectory, String downloadFile, String saveFile){
            sftpAction = new SFTPAction(sftpUtils,downloadDirectory,downloadFile,saveFile,getProgressMonitor,getloginDealUtils);
            sftpAction.execute();
        }
    }

    public class Rename{
        SFTPAction sftpAction;
        Rename(String oldPath, String newPath){
            sftpAction = new SFTPAction(sftpUtils,oldPath,newPath,1,getloginDealUtils);
            sftpAction.execute();
        }
    }

    public class Delete{
        SFTPAction sftpAction;
        Delete(String deleteDirectory, String deleteFile){
            sftpAction = new SFTPAction(sftpUtils, deleteDirectory,deleteFile,getloginDealUtils);
            sftpAction.execute();
        }
    }

    public class List{
        SFTPAction sftpAction;
        List(String listDirectory){
            sftpAction = new SFTPAction(sftpUtils, listDirectory,getloginDealUtils, getlistResultDealUtils);
            sftpAction.execute();
        }
    }

    public class NewDir{
        SFTPAction sftpAction;
        NewDir(String newDirDirectory,String newDirDir, boolean isDeleteExist){
            sftpAction = new SFTPAction(sftpUtils,newDirDirectory, newDirDir, isDeleteExist,getloginDealUtils);
            sftpAction.execute();
        }
    }
    public void setSendProgressMonitor(ProgressMonitorUtils progressMonitor){
        sendProgressMonitor = progressMonitor;
    }

    public void setGetProgressMonitor(ProgressMonitorUtils progressMonitor){
        getProgressMonitor = progressMonitor;
    }

    public  void setLoginDeal(LoginDealUtils loginDeal){
        getloginDealUtils = loginDeal;
    }

    public void setListResultDeal(ListResultDealUtils listResultDealUtils){
        getlistResultDealUtils = listResultDealUtils;
    }
}
