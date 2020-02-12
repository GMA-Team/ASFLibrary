package com.wangtingzheng.asflibrary.Utils;

import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

import com.jcraft.jsch.SftpProgressMonitor;

public class ProgressMonitorUtils extends TimerTask implements SftpProgressMonitor {
    private long progressInterval; // 默认间隔时间为5秒
    private boolean isEnd = false; // 记录传输是否结束
    private long transfered; // 记录已传输的数据总大小
    private long fileSize; // 记录文件总大小
    private Timer timer; // 定时器对象
    private boolean isScheduled = false; // 记录是否已启动timer记时器

    public ProgressMonitorUtils(long progressInterval){
        this.progressInterval = progressInterval ;
    }

    public void setProgressInterval(long progressInterval) {
        this.progressInterval = progressInterval;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    @Override
    public void run() {
        if (!isEnd()) { // 判断传输是否已结束
            dealProgressData("run","in",-1,-1);
            long transfered = getTransfered();

            if (transfered != fileSize) { // 判断当前已传输数据大小是否等于文件总大小
                dealProgressData("run","bytes",-1,transfered);
                sendProgressMessage(transfered);
            } else {
                dealProgressData("run","done",-1,-1);
                setEnd(true); // 如果当前已传输数据大小等于文件总大小，说明已完成，设置end
            }
        } else {
            dealProgressData("run","exit",-1,-1);
            stop(); // 如果传输结束，停止timer记时器
            return;
        }
    }

    public void stop() {
        dealProgressData("stop","try",-1,-1);
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
            isScheduled = false;
        }
        dealProgressData("stop","done",-1,-1);
    }

    public void start() {
        dealProgressData("start","try",-1,-1);
        if (timer == null) {
            timer = new Timer();
        }
        timer.schedule(this, 1000, progressInterval);
        isScheduled = true;
        dealProgressData("start","done",-1,-1);
    }

    /**
     * 打印progress信息
     * @param transfered
     */
    private void sendProgressMessage(long transfered) {
        if (fileSize != 0) {
            double d = ((double)transfered * 100)/(double)fileSize;
            dealProgressData("inProgress","%",d,-1);
        } else {
            dealProgressData("inProgress","empty",-1,transfered);
        }
    }

    /**
     * 实现了SftpProgressMonitor接口的count方法
     */
    public boolean count(long count) {
        if (isEnd()) return false;
        if (!isScheduled) {
            start();
        }
        add(count);
        return true;
    }

    /**
     * 实现了SftpProgressMonitor接口的end方法
     */
    public void end() {
        setEnd(true);
        dealProgressData("end","",0,0);
    }

    private synchronized void add(long count) {
        transfered = transfered + count;
    }

    private synchronized long getTransfered() {
        return transfered;
    }

    public synchronized void setTransfered(long transfered) {
        this.transfered = transfered;
    }

    private synchronized void setEnd(boolean isEnd) {
        this.isEnd = isEnd;
    }

    private synchronized boolean isEnd() {
        return isEnd;
    }

    public void init(int op, String src, String dest, long max) {
        // Not used for putting InputStream
    }

    public void dealProgressData(String stage, String action, double percent, double transfered){

    }
    public  void dealProgressDataDefault(String stage, String action, double percent, double transfered){
        if(stage.equals(ProgressPeriod.RUN)){
            if(action.equals(ProgressPeriod.RUN_ACTION.IN)){
                System.out.println("The downloading is running");
            }else if(action.equals(ProgressPeriod.RUN_ACTION.BTYES)){
                System.out.println("Sending progress message: "+transfered );
            }else if (action.equals(ProgressPeriod.RUN_ACTION.DONE)){
                System.out.println("The downloading is done");
            }else if(action.equals(ProgressPeriod.RUN_ACTION.EXIT)){
                System.out.println("The downloading is exit");
            }
        } else if(stage.equals(ProgressPeriod.STOP)){
            if (action.equals(ProgressPeriod.STOP_ACTION.TRY)) {
                System.out.println("Try to stop progress monitor.");
            }else if(action.equals(ProgressPeriod.STOP_ACTION.DONE)){
                System.out.println("Progress monitor stoped.");
            }
        }else if(stage.equals(ProgressPeriod.inPROGRESS)){
            if(action.equals(ProgressPeriod.inPROGRESS_ACTION.PERCENT)){
                DecimalFormat df = new DecimalFormat( "#.##");
                System.out.println("Sending progress message: " + df.format(percent) + "%");
            }else if(action.equals(ProgressPeriod.inPROGRESS_ACTION.EMPTY)){
                System.out.println("Sending progress message: " + transfered);
            }
        }else if(stage.equals(ProgressPeriod.END)){
            System.out.println("transfering end.");
        }
    }
}