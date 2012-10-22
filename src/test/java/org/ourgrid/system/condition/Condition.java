package org.ourgrid.system.condition;

public interface Condition {

	public boolean isConditionMet() throws Exception;


	public String detailMessage();

}
