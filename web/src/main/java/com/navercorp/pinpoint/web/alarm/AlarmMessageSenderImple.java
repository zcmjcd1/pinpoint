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
        String  s = checker.getEmailMessage();
		String[] n = s.split("\'");
		JSONObject x = (JSONObject) JSONObject.parse(n[n.length-2]);
        String action = HttpRequestUrl.sendGet("http://10.161.55.79:8899/appscaling/"+checker.getRule().getApplicationId()+"/"+checker.getRule()+"/"+x.get("number").toString()+"/", "");
//        EmailEmitter.sendMessage("pinpoint-alarm", checker.getEmailMessage(), receivers);
//        String url = "http://10.161.55.69:8080/v2/apps/"+getAppId("id",checker.getRule().getApplicationId());  
//        int currentInstanceNum= Integer.parseInt(getAppId("instances",checker.getRule().getApplicationId()));
//        int newInstanceNum = currentInstanceNum;
//        String params = "";
//        String  s = checker.getEmailMessage();
//		String[] n = s.split("\'");
//		JSONObject x = (JSONObject) JSONObject.parse(n[n.length-2]);
//		if(currentInstanceNum>=1){
//		if(Integer.parseInt(x.get("number").toString())>=(currentInstanceNum*200)){
//			newInstanceNum = currentInstanceNum+1;
//			params = "{\"instances\":" +newInstanceNum+ "}";
//			post(url,params);
//		}else if (Integer.parseInt(x.get("number").toString())<=((currentInstanceNum-1)*200)){
//			newInstanceNum = currentInstanceNum-1;
//			params = "{\"instances\":" +newInstanceNum+ "}";
//			post(url,params);
//		}
//		}
        
        
        
        
		
	}
	

}

