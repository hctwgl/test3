package com.ald.fanbei.api.server.webapp;

import java.util.List;
/**
 * AutoConfig配置描述类
 * @author rongbo
 *
 */
public class AutoConfig {
	
	List<Property> groupList;
	
	List<String> scriptList;
	
	
	public List<Property> getGroupList() {
		return groupList;
	}

	public void setGroupList(List<Property> groupList) {
		this.groupList = groupList;
	}

	public List<String> getScriptList() {
		return scriptList;
	}

	public void setScriptList(List<String> scriptList) {
		this.scriptList = scriptList;
	}

	public static class Property {
		
		private String name;
		private String defaultValue;
		private String description;
		public String getName() {
			return name;
		}
		
		
		public Property(String name, String defaultValue, String description) {
			this.name = name;
			this.defaultValue = defaultValue;
			this.description = description;
		}

		public void setName(String name) {
			this.name = name;
		}
		public String getDefaultValue() {
			return defaultValue;
		}
		public void setDefaultValue(String defaultValue) {
			this.defaultValue = defaultValue;
		}
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
	}
}
