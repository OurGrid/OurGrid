package org.ourgrid.matchers;

import org.easymock.EasyMock;
import org.easymock.IArgumentMatcher;
import org.ourgrid.common.interfaces.to.MessageHandle;

public class MessageHandleMatcher implements IArgumentMatcher {
	
	private MessageHandle handle;
	
	public MessageHandleMatcher(MessageHandle handle) {
		this.handle = handle;
	}
	
	/* (non-Javadoc)
	 * @see org.easymock.IArgumentMatcher#appendTo(java.lang.StringBuffer)
	 */
	public void appendTo(StringBuffer arg0) {
	}

	/* (non-Javadoc)
	 * @see org.easymock.IArgumentMatcher#matches(java.lang.Object)
	 */
	public boolean matches(Object arg0) {
		
		if ( !(MessageHandle.class.isInstance(arg0)) ) {
			return false;
		}
		
		if (arg0 == null) return false; 
		
		MessageHandle otherHandle = (MessageHandle) arg0;
		
		return this.handle.getActionName().equals(otherHandle.getActionName());
	}

	public static MessageHandle eqMatcher(MessageHandle handle) {
		EasyMock.reportMatcher(new MessageHandleMatcher(handle));
		return null;
	}
}
