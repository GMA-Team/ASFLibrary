
package com.wangtingzheng.asflibrary.Utils;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Vector;

public class SFTPUtils {
	private String Type = "sftp";
	private ChannelSftp sftp;    
    private Session session; 
    private String username;
    private String password;
    private String privateKey;
    private String host;
    private int port;
   
     /**   
     * Constructing SFTP object based on password authentication
     */      
    public SFTPUtils(String username, String password, String host, int port) {//port=22
        this.username = username;    
        this.password = password;    
        this.host = host;    
        this.port = port;    
    }    
    /**   
     * Constructing SFTP object based on password authentication
     */    
    public SFTPUtils(String username, String host, int port, String privateKey)  {
        this.username = username;    
        this.host = host;    
        this.port = port;    
        this.privateKey = privateKey;    
    }  

    
    /**  
     * connect to sftp server
     */    
    public boolean login(){
        try {    
            JSch jsch = new JSch();    
            if (privateKey != null) {    
                jsch.addIdentity(privateKey);// set  private Key
            }    
            session = jsch.getSession(username, host, port);    
            if (password != null) {    
                session.setPassword(password);      
            }   
            session.setTimeout(100000);

            Properties config = new Properties();    
            config.put("StrictHostKeyChecking", "no");    
            session.setConfig(config);
            session.connect(); 

            Channel channel = session.openChannel(Type);    
            channel.connect();    
            sftp = (ChannelSftp) channel;    
        } catch (JSchException e) {    
        	e.printStackTrace();
        	return false;
        }
        return true;
    } 

    /**  
     * close server
     */    
    public void logout(){
        if (sftp != null) {    
            if (sftp.isConnected()) {    
                sftp.disconnect();    
            }    
        }    
        if (session != null) {    
            if (session.isConnected()) {    
                session.disconnect();    
           }    
        }
    }
    /**   
     * Upload the data of the input stream to SFTP as a file   full file path=basePath+directory
     * @param basePath  the base path of upload
     * @param directory  upload directory
     * @param sftpFileName  sftp file name
     * @param input   input stream
     */    

    public void upload(String basePath,String directory, String sftpFileName, InputStream input, ProgressMonitorUtils progressMonitorUtils) throws SftpException, IOException {
	    try {     
	    	sftp.cd(basePath);  
	    	sftp.cd(directory);
	    } catch (SftpException e) {
			   //if not exist, create
			   String [] dirs=directory.split("/");  
			   String tempPath=basePath;  
			   for(String dir:dirs){  
			       if(null== dir || "".equals(dir)) continue;  
			       tempPath+="/"+dir;  
			       try{   
			           sftp.cd(tempPath);  
			       }catch(SftpException ex){  
			           sftp.mkdir(tempPath);  
			           sftp.cd(tempPath);  
			       }  
			   }  
	    }
	    progressMonitorUtils.setFileSize(input.available());
        sftp.put(input, sftpFileName, progressMonitorUtils);
    }

    /**  
     * download file
     * @param directory download directory
     * @param downloadFile the file name
     * @param saveFile the save path which the file download
     */      

    public void download(String directory, String downloadFile, String saveFile, ProgressMonitorUtils progressMonitorUtils) throws SftpException, IOException {
        if (directory != null && !"".equals(directory)) {    
        	sftp.cd(directory);    
        }
        File file = new File(saveFile);
        SftpATTRS sftpATTRS = sftp.lstat(directory+"/"+downloadFile);
        progressMonitorUtils.setFileSize(sftpATTRS.getSize());
        sftp.get(downloadFile, new FileOutputStream(file),progressMonitorUtils);
    }

    /**   
     * download file
     * @param directory download  directory
     * @param downloadFile download file name
     * @return byte array
     */    

    public byte[] download(String directory, String downloadFile) throws SftpException, IOException{
        if (directory != null && !"".equals(directory)) {    
            sftp.cd(directory);    
        }    
        InputStream is = sftp.get(downloadFile);       
        byte[] fileData = IOUtils.toByteArray(is);    
        return fileData;    
    } 

    /**  
     * delete file
     * @param directory To delete the directory where the file is located
     * @param deleteFile Files to delete
     */    
    public void delete(String directory, String deleteFile) throws SftpException{
        sftp.cd(directory);
        SftpATTRS sftpATTRS = sftp.lstat(directory+"/"+deleteFile);
        if(sftpATTRS.isDir()){
            // @TODO don't know if it can delete a no-full dir
            sftp.rmdir(deleteFile); //delete dir
        }else {
            sftp.rm(deleteFile); //delete file
        }
    }

    /**
     * new a directory
     * @param directory
     * @param dir
     * @param isDeleteExist
     * @throws SftpException
     */
    public void newDir(String directory, String dir, boolean isDeleteExist) throws SftpException {
        sftp.cd(directory);
        try{
            SftpATTRS sftpATTRS = sftp.lstat(directory+"/"+dir);
            if(sftpATTRS.isDir()){
                if(isDeleteExist==true){
                    delete(directory, dir);
                }
            }else {
                sftp.mkdir(directory+"/"+dir);
            }
        }catch (Exception e){
            //*@Todo: add new one operation
            //System.out.println("No Folder, will new one!");
        }finally {
            sftp.mkdir(directory+"/"+dir);
        }

    }
    /**
     * @param directory
     * @return a vector of ChannelSftp.LsEntry objects.
     * @throws SftpException
     */
    public Vector<?> listFiles(String directory) throws SftpException {
        return sftp.ls(directory);    
    }

    /**
     * rename a file or directory
     * @param oldPath  the old file or directory path
     * @param newPath  the new file or directory path
     */
    public void rename(String oldPath, String newPath){
        try {
            sftp.rename(oldPath,newPath);
        } catch (SftpException e) {
            e.printStackTrace();
        }
    }
}