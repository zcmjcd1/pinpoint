package com.navercorp.pinpoint.web.controller;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

@Controller
public class LogController {

    @RequestMapping(value = "/showlog", produces="text/plain;charset=utf-8",method = RequestMethod.GET)
    @ResponseBody
    public String NeloLogForTransactionId(@RequestParam (value= "transactionId", required=true) String transactionId,
                                            @RequestParam(value= "spanId" , required=false) String spanId,
                                            @RequestParam(value="time" , required=true) long time ) {
    	String result = "";
    	
    	String x = "%22TxId%20:%20"+transactionId+"%22";
	SimpleDateFormat sdf=new SimpleDateFormat("yyyy.MM.dd");       
	String sd = sdf.format(new Date(time));
	String q=HttpRequestUrl.sendGet("http://10.161.55.68:9200/logstash-"+sd+"/logs/_count","q=message:"+x);
	String s=HttpRequestUrl.sendGet("http://10.161.55.68:9200/logstash-"+sd+"/logs/_search","size="+getCount(q)+"&q=message:"+x);
	result = getAppList(s);
    	StringBuffer sb = new StringBuffer();
	sb.append(getCount(q)).append("\n").append(result);
    	return sb.toString();
          
    }
    public static String getAppList(String process){
		StringBuffer sb = new StringBuffer();
		JSONObject json= JSONObject.parseObject(process);
		JSONObject hits = json.getJSONObject("hits");
		JSONArray hitss = hits.getJSONArray("hits");
		for (int i = 0; i < hitss.size(); i++) {
			JSONObject jsonInstance = hitss.getJSONObject(i);
			JSONObject source = jsonInstance.getJSONObject("_source");
			sb.append(source.getString("message")).append("\n");
		}
		
		return sb.toString();
		
	}
    public static String getCount(String process){
		String res = "";
		JSONObject json= JSONObject.parseObject(process);
		res = json.getString("count");
		
		return res;
	}
    
}

