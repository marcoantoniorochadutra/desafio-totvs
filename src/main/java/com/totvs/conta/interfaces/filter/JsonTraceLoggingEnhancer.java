package com.totvs.conta.interfaces.filter;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.contrib.json.classic.JsonLayout;

import java.util.Map;


public class JsonTraceLoggingEnhancer extends JsonLayout {

	@Override
	protected void addCustomDataToJsonMap(Map<String, Object> map, ILoggingEvent event) {
		 map.put("severity", event.getLevel().levelStr);
		 map.remove("timestamp");
		 map.remove("level");
		 map.remove("context");
	}
		
	
}
