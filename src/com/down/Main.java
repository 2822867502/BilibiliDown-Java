package com.down;

import com.down.mp3.Audio;
import com.down.mp3.Down3;
import com.down.mp3.RunFfmpeg;
import com.down.mp4.Down4;
import com.down.mp4.Video;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) throws MalformedURLException {
        Down4 down4 = new Down4();
        Down3 down3 = new Down3(down4);
        ArrayList<String> urls = new ArrayList<>();
        Constructor constructor;
        try {
            switch (args[0]){
                case "file":
                    //urls = (ArrayList<String>) FileUtils.readLines(new File(args[2]), StandardCharsets.UTF_8);
                    urls = new ArrayList<>(FileUtils.readLines(new File(args[2]), StandardCharsets.UTF_8));
                    break;
                case "url":
                    urls.add(args[2]);
                    break;
                default:
                    throw new Exception();
            }
            switch (args[1]){
                case "mp3":
                    for (String str:urls){
                        down3.down(new Audio(new URL(str)));
                    }
                    break;
                case "mp4":
                    for (String str:urls){
                        down4.down(new Video(new URL(str)));
                    }
                    break;
                default:
                    throw new Exception();
            }
        }catch (Throwable e){
            e.printStackTrace();
            System.out.println("参数错误或出现异常！");
            System.out.println("正确参数：(源：file|url) (mp4|mp3) (文件|链接)");
        }
    }
}
