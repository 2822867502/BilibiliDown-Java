package com.down.mp3;

import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class RunFfmpeg {
    private final Runtime runtime = Runtime.getRuntime();
    private final String command;
    public static final int FFMPEG_NOT_FOUND = -2147483648;
    public static final int FFMPEG_ERR = -2147483647;
    public RunFfmpeg(String command) {
        this.command = command;
        try {
            FileUtils.delete(new File(".\\debug\\out.txt"));
            FileUtils.delete(new File(".\\debug\\out.txt"));
        }catch (IOException e){
        }
    }
    public int start(){
        return this.run();
    }
    protected int run(){
        try {
            Process process = runtime.exec(command);
            new Thread(){
                @Override
                public void run() {
                    try {
                        FileUtils.copyToFile(process.getInputStream(),new File(".\\debug\\out.txt"));
                    } catch (IOException e) {
                        System.out.println("出现错误！调试信息：");
                        e.printStackTrace();
                    }
                }
            }.start();
            new Thread(){
                @Override
                public void run() {
//                    BufferedReader bfr = new BufferedReader(new InputStreamReader(process.getErrorStream(), StandardCharsets.UTF_8));
//                    String inStr;
//                    try {
//                        while (bfr.ready()) {
//                            inStr = bfr.readLine();
//                            System.out.println(inStr);
//                            if(inStr.matches("File '+.' already exists. Overwrite? \\[y/N\\]")){
//                                process.getOutputStream().write('y');
//                            }
//                        }
//                    }catch (IOException e){
//                        System.out.println("出现错误！调试信息：");
//                         e.printStackTrace();
//                    }
                    try {
                        FileUtils.copyToFile(process.getErrorStream(),new File(".\\debug\\outErr.txt"));
                    } catch (IOException e) {
                        System.out.println("出现错误！调试信息：");
                        e.printStackTrace();
                    }
                }
            }.start();
            return process.waitFor();
        }catch (IOException e){
            //e.printStackTrace();
            return FFMPEG_NOT_FOUND;
        } catch (InterruptedException e) {
            return FFMPEG_ERR;
        }
    }
}
