package com.down.mp4;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
//TODO: 2023/7/10
//todo 酌情考虑添加多p支持
//todo 添加方式：
/*todo 1.获取cid时获取json中的pages项(JSONArray)
       2.获取每个page的cid
 */
public class Video {
    private static final String API = "https://api.bilibili.com/x/web-interface/view?bvid=";
    private final String bvid;
    private final URL url;
    private long cid;
    private String title;
    private JSONObject json;
    private boolean right;
    private String titleNo;

    public Video(URL url) {
        this.url = url;
        this.bvid = getBvid(this.url);
        OkHttpClient okhttp = new OkHttpClient();
        try {
            Request request = new Request
                                .Builder()
                                .url(new URL(API + bvid))
                                .addHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36 Edg/114.0.1823.67")
                                .build();
            Response response = okhttp.newCall(request).execute();
            if (right = (response.body() != null)) {
                json = new JSONObject(response.body().string());
            }
            response.close();
        } catch (JSONException | IOException e) {
            System.out.println("初始化视频" + bvid + "异常！");
            right = false;
        }
        if (right = (json != null && json.has("data"))) {
            //System.out.println(json);
            JSONObject dataJson = json.getJSONObject("data");
            if (right = (dataJson != null && dataJson.has("title") && dataJson.has("cid"))) {
                this.title = dataJson.getString("title");
                this.cid = dataJson.getLong("cid");
            }
        }
        if(right){
            System.out.printf("获取到视频：%s\nbvid：%s\ncid：%d%n", title, bvid, cid);
        }else {
            System.out.println("初始化视频" + bvid + "异常！%n");
        }
        this.titleDel();
    }

    /**
     * 删除标题中不可用于目录或可能出错的字符
     * @return 安全的标题
     */
    private void titleDel(){
        String[] strings = new String[]{"/","\\",":","*","\"","<",">","|","?"," "};
        this.titleNo = title;
        StringBuilder stringBuilder = new StringBuilder(titleNo);
        for(String s : strings){
            int i;
            while ((i = stringBuilder.indexOf(s))!=-1){
                stringBuilder.delete(i,i+1);
            }
        }
        this.titleNo = new String(stringBuilder);
//        for (String s : strings){
//            if(titleNo.contains(s)){
//               for(String titleCache : titleNo.split(s)){
//
//               }
//            }
//        }
    }
    public static String getBvid(URL url) {
        String urlStr = url.toString();
        String bvid = subStringFromString("/BV.+/", urlStr);
        return bvid.substring(1, bvid.length() - 1);
    }

    private static String subStringFromString(@NotNull String regex, @NotNull String str) {
        if ("".equals(str) || "".equals(regex)) return "";
        regex = ".*" + regex + ".*";
        int point = 0;
        while (point < str.length() && str.substring(point + 1).matches(regex)) {
            point++;
        }
        str = str.substring(point);
        point = str.length() - 1;
        while (point > 0 && str.substring(0, point - 1).matches(regex)) {
            point--;
        }
        str = str.substring(0, point);
        return str;
    }

    public String getBvid() {
        return this.bvid;
    }

    public URL getUrl() {
        return url;
    }

    public long getCid() {
        return cid;
    }

    public String getTitleNoErrChar() {
        return titleNo;
    }

    public boolean isRight() {
        return right;
    }
}
