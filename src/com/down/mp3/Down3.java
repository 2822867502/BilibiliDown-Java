package com.down.mp3;

import com.down.mp4.Down4;
import net.lingala.zip4j.ZipFile;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

public class Down3 {
    private static final String ARGUMENT = "-i %s %s -y";
    private String FFMPEG_PATH = ".\\ffmpeg-win\\";
    private String SAVE_PATH = ".\\down\\audio\\";
    private String FFMPEG_EXE_PATH = FFMPEG_PATH + "bin\\ffmpeg.exe";

    public String getFFMPEG_PATH() {
        return FFMPEG_PATH;
    }

    public String getSAVE_PATH() {
        return SAVE_PATH;
    }

    public String getFFMPEG_EXE_PATH() {
        return FFMPEG_EXE_PATH;
    }

    public void setFFMPEG_PATH(String FFMPEG_PATH) {
        this.FFMPEG_PATH = FFMPEG_PATH;
        this.FFMPEG_EXE_PATH = FFMPEG_PATH + "bin\\ffmpeg.exe";
        if(!checkFfmpeg()) putFfmpeg();
    }

    public void setSAVE_PATH(String SAVE_PATH) {
        this.SAVE_PATH = SAVE_PATH;
        if(!checkFolder()) createFolder();
    }

    public void setFFMPEG_EXE_PATH(String FFMPEG_EXE_PATH) {
        this.FFMPEG_EXE_PATH = FFMPEG_EXE_PATH;
        if(!checkFfmpeg()) putFfmpeg();
    }

    private final Down4 down4;
    public Down3(@NotNull Down4 down4) {
        this.down4 = down4;
//        try {
//            FileUtils.deleteDirectory(new File(SAVE_PATH));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        if(!checkFfmpeg()){
            System.out.println("未检测到ffmpeg，将自动释放文件！");
            putFfmpeg();
            System.out.println("释放完成！");
        }
        if(!checkFolder()){
            System.out.println("输出文件夹不存在，将自动创建！");
            createFolder();
            System.out.println("创建完成！");
        }
    }
    public File down(Audio audio){
        File mp4 = down4.down(audio);
        if(mp4==null) return null;
        System.out.printf("开始转换音频\"%s\"！%n",audio.getTitleNoErrChar());
        final long begin = System.currentTimeMillis();
        String filename4 = mp4.getName();
        String filename3 = filename4.substring(0,filename4.length()-1)+"3";
        File mp3 = new File(SAVE_PATH+filename3);
        String command = String.format(FFMPEG_EXE_PATH+" "+ARGUMENT,mp4,mp3);
        if(new RunFfmpeg(command).start()==0){
            final long end = System.currentTimeMillis();
            System.out.printf("转换完毕！用时：%d秒%n",(end - begin)/1000);
            return mp3;
        }else {
            System.out.printf("转换音频\"%s\"失败！%n",filename3);
            return null;
        }
    }
    private boolean checkFfmpeg(){
        return new RunFfmpeg(this.FFMPEG_EXE_PATH).start()!=RunFfmpeg.FFMPEG_NOT_FOUND;
    }
    private void putFfmpeg(){
        try {
            //FileUtils.copyDirectory(new File(getClass().getClassLoader().getResource("ffmpeg-win").toURI()),new File(FFMPEG_PATH));
//            InputStream in = getClass().getClassLoader().getResourceAsStream("ffmpeg-win");
//            FileUtils.copyInputStreamToFile(in,new File(FFMPEG_PATH));
//            ZipInputStream zipIn = new ZipInputStream(getClass().getClassLoader().getResourceAsStream("ffmpeg-win.zip"), StandardCharsets.UTF_8);
//            FileUtils.copyInputStreamToFile(new ByteArrayInputStream( zipIn.getNextEntry().getExtra()),new File(FFMPEG_PATH));
            File cacheZip = new File(new File(FFMPEG_PATH).getParentFile(),"cache.zip");
            FileUtils.copyToFile(getClass().getClassLoader().getResourceAsStream("ffmpeg-win.zip"),cacheZip);
            ZipFile zip = new ZipFile(cacheZip);
            zip.extractFile("ffmpeg-win/",new File(FFMPEG_PATH).getCanonicalFile().getParentFile().toString());
            FileUtils.delete(cacheZip);
        } catch (IOException e) {
            //e.printStackTrace();
            System.out.println("初始化ffmpeg失败，程序自动退出");
            System.exit(1);
        }
    }
    private boolean checkFolder(){
        File folder = new File(SAVE_PATH);
        return folder.exists();
    }
    private void createFolder(){
        try {
            FileUtils.createParentDirectories(new File(SAVE_PATH));
        } catch (IOException e) {
            System.out.println("创建输出文件夹失败，程序将自动退出");
            System.exit(1);
        }
        new File(SAVE_PATH).mkdir();
    }
}
