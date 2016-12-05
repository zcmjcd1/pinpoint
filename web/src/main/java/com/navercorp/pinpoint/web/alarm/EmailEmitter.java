package com.navercorp.pinpoint.web.alarm;



import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;


public class EmailEmitter {
	
	 	private static Logger logger = LoggerFactory.getLogger(EmailEmitter.class);

	    private static String FROMUSER = "zhaocm34@chinaunicom.cn";

	    private static String FROMPASS = "zcmYYDSD1";

	    
	    
	    /**
	     * 发送邮件(文本) 多个,通过rest接口发送邮件20160513
	     * 
	     * @param subject
	     * @param mess
	     * @param toAddressList
	     * @throws MessagingException
	     */
	    public static void sendMessage(String subject, String content, List<String> toAddressList) {
	       
    		JSONObject json= new JSONObject();
    		StringBuffer sendAddress = new StringBuffer();
    		URL url = null;
    		HttpURLConnection conn = null;
    		
	    	try {
				url = new URL("http://10.161.11.182:8082/monitor/rest/email/emailService");
				conn = (HttpURLConnection) url.openConnection();

	    		conn.setDoOutput(true);
	    		conn.setRequestMethod("POST");
	    		conn.setRequestProperty("Content-Type", "application/json");

	            for (String toAddress : toAddressList) {
	            	sendAddress.append(toAddress).append(",");
	            }
	            sendAddress.deleteCharAt(sendAddress.length()-1);
	            
	    		json.put("toAddress", sendAddress);
	    		json.put("subject", subject);
	    		json.put("userName", FROMUSER);
	    		json.put("password", FROMPASS);
	    		json.put("content", content);
	    		
	    		String input = json.toJSONString();

	    		OutputStream os = conn.getOutputStream();
	    		os.write(input.getBytes());
	    		os.flush();

	    		BufferedReader br = new BufferedReader(new InputStreamReader(
	    				(conn.getInputStream())));

	    		String output = null;
	    		StringBuffer buffer = new StringBuffer();
	    		while ((output = br.readLine()) != null) {
	    			buffer.append(output);
	    		}

	    		JSONObject jsonObject = JSONObject.parseObject(buffer.toString());
	    		
	    		if (null != jsonObject) {
	    			int code = jsonObject.getIntValue("code");
	    			String msg = jsonObject.getString("msg");
	    			if (200 == code) {
	    				logger.info("邮件发送成功："+jsonObject.toJSONString());
	    			}else {
	    				logger.error("邮件发送失败："+jsonObject.toJSONString());
	    			}		
	    		}
	        	
	            logger.info("======================================");
	            logger.info("sendMessage 发送前转邮件成功：\n标题:" + subject + " 内容:" + content);
	            logger.info("======================================");
	        } catch (Exception e) {
	            logger.error("sendMessage  发送前转邮件失败：标题[" + subject + "],内容[" + content + "]"+e.getLocalizedMessage(), e);
	           
	        } finally {
				if (null != conn) {
					conn.disconnect();
				}
			}
	    }
	    public static void main(String[] args) {
	    	List<String> list =new ArrayList<String>();
	    	list.add("chenx132@chinaunicom.cn");
	    	list.add("wuwj97@chinaunicom.cn");
	    	sendMessage("test","test",list);
		}
}

