package com.down.mp4;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class Down4 {
    private static final String API = "https://api.bilibili.com/x/player/playurl?otype=json&fnver=0&fnval=2&player=1&qn=64&bvid=%s&cid=%d";
    private String SAVE_PATH = ".\\down\\video\\";
    private static final OkHttpClient okhttp = new OkHttpClient();
    public String getSAVE_PATH() {
        return SAVE_PATH;
    }

    public void setSAVE_PATH(String SAVE_PATH) {
        this.SAVE_PATH = SAVE_PATH;
    }
    public Down4() {
        try {
            FileUtils.deleteDirectory(new File(SAVE_PATH));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public File down(Video video){
        if(video==null|| !video.isRight()) return null;
        URL url = getDownUrl(video);
        final long begin = System.currentTimeMillis();
        Request request = new Request
                .Builder()
                .url(url)
                .addHeader("Referer","https://www.bilibili.com")
                .addHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36 Edg/114.0.1823.67")
                .addHeader("Sec-Fetch-Mode", "no-cors")
                .build();
        try {
            System.out.printf("开始下载视频\"%s\"！%n",video.getTitle());
            Response response = okhttp.newCall(request).execute();
            InputStream in = response.body().byteStream();
            FileUtils.copyToFile(in,new File(SAVE_PATH+video.getTitle()+".mp4"));
            //// TODO: 2023/7/10 自定义文件名
            final long end = System.currentTimeMillis();
            System.out.printf("下载完成！用时：%d秒%n",(end-begin)/1000);
        }catch (IOException e){
            System.out.printf("下载视频\"%s\"失败%n！",video.getTitle());
        }
        return new File(SAVE_PATH+video.getTitle()+".mp4");
    }
    private void createFolder(File file){

    }
    public URL getDownUrl(Video video){
        String utlStr = String.format(API,video.getBvid(),video.getCid());
        URL url = null;
        long size = 0;
        try {
            Request request = new Request
                    .Builder()
                    .url(new URL(utlStr))
                    .addHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36 Edg/114.0.1823.67")
                    .build();
            Response response = okhttp.newCall(request).execute();
            JSONObject json = null;
            boolean ok;
            if (ok = (response.body() != null)) {
                json = new JSONObject(response.body().string());
            }
            if(ok = (json!=null&&json.has("data"))){
                // TODO: 2023/7/10 备用链接支持
                JSONObject dataJson = json.getJSONObject("data");
                if(ok = (dataJson!=null&&dataJson.has("durl"))){
                    JSONArray durlJSONArray = dataJson.getJSONArray("durl");
                    if(ok = (durlJSONArray!=null&&!durlJSONArray.isEmpty())){
                        JSONObject urlJson = durlJSONArray.getJSONObject(0);
                        if(ok = (urlJson!=null&&urlJson.has("url")&&urlJson.has("size"))){
                            url = new URL(urlJson.getString("url"));
                            size = urlJson.getLong("size");
                        }
                    }
                }
            }
            if(ok){
                System.out.printf("成功获取视频\"%s\"下载链接！文件大小：%dKB%n",video.getTitle(),size/1024);
            }else {
                System.out.printf("获取视频\"%s\"下载链接失败！%n",video.getTitle());
            }
            response.close();
        }catch (IOException | JSONException e){
            System.out.printf("获取视频\"%s\"下载链接失败！%n",video.getTitle());
        }
        return url;
    }
}
