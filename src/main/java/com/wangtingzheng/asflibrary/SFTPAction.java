package com.wangtingzheng.asflibrary;

import android.os.AsyncTask;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import com.wangtingzheng.asflibrary.Utils.ListResultDealUtils;
import com.wangtingzheng.asflibrary.Utils.LoginDealUtils;
import com.wangtingzheng.asflibrary.Utils.ProgressMonitorUtils;
import com.wangtingzheng.asflibrary.Utils.SFTPUtils;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

public class SFTPAction extends AsyncTask<Void,Void,Void> {
    private String Action;

    private SFTPUtils sftpUtils;
    private LoginDealUtils loginDealUtils;

    private InputStream fileStream;
    private String fileName;
    private String filePath;
    private String uploadDirectory;
    private ProgressMonitorUtils uploadProgressMonitorUtils;

    private String downloadDirectory;
    private String downloadFile;
    private String saveFile;
    private ProgressMonitorUtils downloadProgressMonitorUtils;

    private String oldPath;
    private String newPath;

    private String deleteDirectory;
    private String deleteFile;

    private String newDirDirectory;
    private String newDirDir;
    private boolean isDeleteExist;

    private String listDirectory;
    private ListResultDealUtils listResultDealUtils;


    SFTPAction(SFTPUtils sftpUtils, InputStream fileStream, String fileName, String filePath, String directory, ProgressMonitorUtils progressMonitorUtils, LoginDealUtils loginDealUtils) {
        Action = "upload";
        this.sftpUtils = sftpUtils;
        this.fileStream = fileStream;
        this.fileName = fileName;
        this.filePath = filePath;
        this.uploadDirectory = directory;
        this.uploadProgressMonitorUtils  = progressMonitorUtils;
        this.loginDealUtils = loginDealUtils;
    }

    SFTPAction(SFTPUtils sftpUtils, String downloadDirectory, String downloadFile, String saveFile, ProgressMonitorUtils downloadProgressMonitorUtils, LoginDealUtils loginDealUtils) {
        Action = "download";
        this.sftpUtils = sftpUtils;
        this.downloadDirectory = downloadDirectory;
        this.downloadFile = downloadFile;
        this.saveFile = saveFile;
        this.downloadProgressMonitorUtils = downloadProgressMonitorUtils;
        this.loginDealUtils = loginDealUtils;
    }

    public SFTPAction(SFTPUtils sftpUtils, String oldPath, String newPath, int useless, LoginDealUtils loginDealUtils) {
        Action = "rename";
        this.sftpUtils = sftpUtils;
        this.oldPath = oldPath;
        this.newPath = newPath;
        this.loginDealUtils = loginDealUtils;
    }

    public SFTPAction(SFTPUtils sftpUtils, String deleteDirectory, String deleteFile, LoginDealUtils loginDealUtils) {
        Action = "delete";
        this.sftpUtils = sftpUtils;
        this.deleteDirectory = deleteDirectory;
        this.deleteFile = deleteFile;
        this.loginDealUtils = loginDealUtils;
    }

    public SFTPAction(SFTPUtils sftpUtils, String newDirDirectory, String newDirDir, boolean isDeleteExist, LoginDealUtils loginDealUtils){
        Action = "newDir";
        this.sftpUtils = sftpUtils;
        this.newDirDirectory = newDirDirectory;
        this.newDirDir = newDirDir;
        this.isDeleteExist = isDeleteExist;
        this.loginDealUtils = loginDealUtils;
    }

    public SFTPAction(SFTPUtils sftpUtils, String listDirectory, LoginDealUtils loginDealUtils, ListResultDealUtils listResultDealUtils){
        Action ="list";
        this.sftpUtils = sftpUtils;
        this.listDirectory = listDirectory;
        this.loginDealUtils = loginDealUtils;
        this.listResultDealUtils = listResultDealUtils;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            if(Action.equals("upload")) {
                sendFileToGameShell(sftpUtils);
            }else if(Action.equals("download")){
                getFileFromGameShell(sftpUtils);
            }else if(Action.equals("rename")){
                reName(sftpUtils);
            }else if(Action.equals("delete")){
                delete(sftpUtils);
            }else if(Action.equals("newDir")){
                newDir(sftpUtils);
            }else if(Action.equals("list")){
                listFiles(sftpUtils);
            }
        } catch (JSchException | SftpException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void sendFileToGameShell(SFTPUtils sftp) throws JSchException, SftpException, IOException {
        boolean isLogin = sftp.login();
        loginDealUtils.dealLoginResult(isLogin);
        LoginDealUtils loginDealUtils = new LoginDealUtils();
        sftp.upload(filePath, uploadDirectory, fileName,fileStream,uploadProgressMonitorUtils);
        sftp.logout();
    }
    private void getFileFromGameShell(SFTPUtils sftp) throws IOException, SftpException {
        boolean isLogin = sftp.login();
        loginDealUtils.dealLoginResult(isLogin);
        sftp.download(downloadDirectory,downloadFile,saveFile,downloadProgressMonitorUtils);
        sftp.logout();
    }
    private void reName(SFTPUtils sftp) {
        boolean isLogin = sftp.login();
        loginDealUtils.dealLoginResult(isLogin);
        sftp.rename(oldPath,newPath);
        sftp.logout();
    }
    private void delete(SFTPUtils sftp) throws SftpException {
        boolean isLogin = sftp.login();
        loginDealUtils.dealLoginResult(isLogin);
        sftp.delete(deleteDirectory,deleteFile);
        sftp.logout();
    }
    private void newDir(SFTPUtils sftp) throws SftpException {
        boolean isLogin = sftp.login();
        loginDealUtils.dealLoginResult(isLogin);
        sftp.newDir(newDirDirectory,newDirDir,isDeleteExist);
        sftp.logout();
    }
    private void listFiles(SFTPUtils sftp) throws SftpException {
        boolean isLogin = sftp.login();
        loginDealUtils.dealLoginResult(isLogin);
        Vector<?> data = sftp.listFiles(listDirectory);
        sftp.logout();
        listResultDealUtils.dealListResult(data);
    }
}