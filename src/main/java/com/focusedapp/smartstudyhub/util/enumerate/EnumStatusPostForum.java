package com.focusedapp.smartstudyhub.util.enumerate;

public enum EnumStatusPostForum {

	ACTIVE("ACTIVE"),
	DELETED("DELETED"),
	BANNED("BANNED");
	
	private final String value;
	
	EnumStatusPostForum(String value) {	
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}	
	
}
