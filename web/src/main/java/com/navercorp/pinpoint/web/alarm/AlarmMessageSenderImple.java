package com.navercorp.pinpoint.web.alarm;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.navercorp.pinpoint.web.alarm.checker.AlarmChecker;
import com.navercorp.pinpoint.web.service.UserGroupService;
import com.navercorp.pinpoint.web.controller.HttpRequestUrl;

public class AlarmMessageSenderImple implements AlarmMessageSender{
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private UserGroupService userGroupService;	

	@Override
	public void sendSms(AlarmChecker checker, int sequenceCount) {
		List<String> receivers = userGroupService.selectPhoneNumberOfMember(checker.getuserGroupId());

        if (receivers.size() == 0) {
            return;
        }

        for (String message : checker.getSmsMessage()) {
            logger.info("send SMS : {}", message);

            // TODO Implement logic for sending SMS
        }
		
	}

	@Override
	public void sendEmail(AlarmChecker checker, int sequenceCount) {
		List<String> receivers = userGroupService.selectEmailOfMember(checker.getuserGroupId());

        if (receivers.size() == 0) {
            return;
        }
        
        logger.info("send email : {}", checker.getEmailMessage());
//        EmailEmitter.sendMessage("pinpoint-alarm", checker.getEmailMessage(), receivers);
        String url = "http://10.161.55.69:8080/v2/apps/"+getAppId("id",checker.getRule().getApplicationId());  
        int currentInstanceNum= Integer.parseInt(getAppId("instances",checker.getRule().getApplicationId()));
        int newInstanceNum = currentInstanceNum;
        String params = "";
        String  s = checker.getEmailMessage();
		String[] n = s.split("\'");
		JSONObject x = (JSONObject) JSONObject.parse(n[n.length-2]);
		if(currentInstanceNum>=1){
		if(Integer.parseInt(x.get("number").toString())>=(currentInstanceNum*200)){
			newInstanceNum = currentInstanceNum+1;
			params = "{\"instances\":" +newInstanceNum+ "}";
			post(url,params);
		}else if (Integer.parseInt(x.get("number").toString())<=((currentInstanceNum-1)*200)){
			newInstanceNum = currentInstanceNum-1;
			params = "{\"instances\":" +newInstanceNum+ "}";
			post(url,params);
		}
		}
        
        
        
        
		
	}
	public String getAppId(String param,String scalename){
		String s=HttpRequestUrl.sendGet("http://10.161.55.69:8080/v2/apps","label=SCALE_NAME=="+scalename);

		JSONObject json = JSONObject.parseObject(s);
		JSONArray array = json.getJSONArray("apps");
		JSONObject app = array.getJSONObject(0);
		
		return app.get(param).toString().replaceAll("/", "");
	}
	public static String post(String strURL, String params) {  
        System.out.println(strURL);  
        System.out.println(params);  
        try {  
            URL url = new URL(strURL);// 创建连接  
            HttpURLConnection connection = (HttpURLConnection) url  
                    .openConnection();  
            connection.setDoOutput(true);  
            connection.setDoInput(true);  
            connection.setUseCaches(false);  
            connection.setInstanceFollowRedirects(true);  
            connection.setRequestMethod("PUT"); // 设置请求方式  
            connection.setRequestProperty("Accept", "application/json"); // 设置接收数据的格式  
            connection.setRequestProperty("Content-Type", "application/json"); // 设置发送数据的格式  
            connection.connect();  
            OutputStreamWriter out = new OutputStreamWriter(  
                    connection.getOutputStream(), "UTF-8"); // utf-8编码  
            out.append(params);  
            out.flush();  
            out.close();  
            // 读取响应  
            int length = (int) connection.getContentLength();// 获取长度  
            InputStream is = connection.getInputStream();  
            if (length != -1) {  
                byte[] data = new byte[length];  
                byte[] temp = new byte[512];  
                int readLen = 0;  
                int destPos = 0;  
                while ((readLen = is.read(temp)) > 0) {  
                    System.arraycopy(temp, 0, data, destPos, readLen);  
                    destPos += readLen;  
                }  
                String result = new String(data, "UTF-8"); // utf-8编码  
                System.out.println(result);  
                return result;  
            }  
        } catch (IOException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        }  
        return "error"; // 自定义错误信息  
    }  

}

