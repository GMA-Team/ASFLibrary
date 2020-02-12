package com.wangtingzheng.asflibrary.Utils;

public class ProgressPeriod {
    public static String START = "start";
    public static String RUN = "run";
    public static String STOP = "stop";
    public static String inPROGRESS = "inProgress";
    public static String END = "end";

    public static START START_ACTION = new START();
    public static RUN RUN_ACTION = new RUN();
    public static STOP STOP_ACTION = new STOP();
    public static inPROGRESS inPROGRESS_ACTION = new inPROGRESS();

    public static class START{
        public String TRY = "try";
        public String DONE = "done";
    }
    public static class RUN{
        public String IN = "in";
        public String BTYES = "bytes";
        public String DONE = "done";
        public String EXIT = "exit";
    }
    public static class STOP{
        public String TRY = "try";
        public String DONE = "done";
    }
    public static class inPROGRESS{
        public String PERCENT = "%";
        public String EMPTY = "empty";
    }
}
