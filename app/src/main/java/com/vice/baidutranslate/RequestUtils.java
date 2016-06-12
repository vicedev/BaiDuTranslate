package com.vice.baidutranslate;

import android.os.AsyncTask;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Random;

//import static com.vice.test.MainActivity.handler;

/**
 * Created by Administrator on 2016/5/26 0026.
 */

public class RequestUtils {
    private static final String UTF8 = "utf-8";

    //申请者开发者id，实际使用时请修改成开发者自己的appid
    private static final String appId = "2015063000000001";

    //申请成功后的证书token，实际使用时请修改成开发者自己的token
    private static final String token = "12345678";

    private static final String url = "http://api.fanyi.baidu.com/api/trans/vip/translate";

    //随机数，用于生成md5值，开发者使用时请激活下边第四行代码
    private static final Random random = new Random();

    public  void translate(final String q, final String from, final String to, final HttpCallBack callBack) throws Exception{


        //用于md5加密
        //int salt = random.nextInt(10000);
        //本演示使用指定的随机数为1435660288
        final int salt = 1435660288;

        // 对appId+源文+随机数+token计算md5值
        //应该对 appid+q+salt+密钥 拼接成的字符串做MD5得到32位小写的sign。确保要翻译的文本q为UTF-8编码。
        String md5String=appId+new String(q.getBytes(),"utf-8")+salt+token;
        final String sign = MD5Encoder.encode(md5String.toString());

        //注意在生成签名拼接 appid+q+salt+密钥 字符串时，q不需要做URL encode，在生成签名之后，发送HTTP请求之前才需要对要发送的待翻译文本字段q做URL encode。
        final URL url1=new URL(url+"?"+"q="+URLEncoder.encode(q,"utf-8")+"&from="+from+"&to="+to+"&appid="+appId+"&salt="+salt+"&sign="+sign);

        new AsyncTask<Void, Integer, String>() {
            @Override
            protected String doInBackground(Void... params) {
                HttpURLConnection conn=null;
                String text=null;
                try {
                    conn= (HttpURLConnection) url1.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(8000);
                    conn.setConnectTimeout(8000);
                    InputStream in=conn.getInputStream();
                    BufferedReader reader=new BufferedReader(new InputStreamReader(in));
                    StringBuilder builder=new StringBuilder();
                    String str;
                    while ((str = reader.readLine()) != null) {
                        builder.append(str).append("\n");
                    }
                    System.out.println(builder.toString());

                    //转化为json对象，注：Json解析的jar包可选其它
                    JSONObject resultJson = new JSONObject(builder.toString());

                    //开发者自行处理错误，本示例失败返回为null
                    try {
                        String error_code = resultJson.getString("error_code");
                        if (error_code != null) {
                            System.out.println("出错代码:" + error_code);
                            System.out.println("出错信息:" + resultJson.getString("error_msg"));
                            callBack.onFailure("出错信息:" + resultJson.getString("error_msg"));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        //获取返回翻译结果
                        JSONArray array = (JSONArray) resultJson.get("trans_result");
                        JSONObject dst = (JSONObject) array.get(0);
                        text = dst.getString("dst");
                        text = URLDecoder.decode(text, UTF8);
                        System.out.println(text);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    if (conn!=null){
                        conn.disconnect();
                    }
                }
                return text;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                callBack.onSuccess(s);
            }

        }.execute();

    }
}
