/*
 * Copyright (C) 2008 Universidade Federal de Campina Grande
 *  
 * This file is part of OurGrid. 
 *
 * OurGrid is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free 
 * Software Foundation, either version 3 of the License, or (at your option) 
 * any later version. 
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT 
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or 
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details. 
 * 
 * You should have received a copy of the GNU Lesser General Public License 
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package org.ourgrid.matchers;

import org.easymock.IArgumentMatcher;
import org.easymock.classextension.EasyMock;

import br.edu.ufcg.lsd.commune.container.servicemanager.actions.RepetitionRunnable;


/**
 */
public class SaveAccountingRepetitionRunnableMatcher implements IArgumentMatcher {

	private RepetitionRunnable runnable;
	
	/**
	 * @param runnable
	 */
	public SaveAccountingRepetitionRunnableMatcher(RepetitionRunnable runnable) {
		this.runnable = runnable;
	}

	/* (non-Javadoc)
	 * @see org.easymock.IArgumentMatcher#appendTo(java.lang.StringBuffer)
	 */
	public void appendTo(StringBuffer arg0) {
		
	}

	/* (non-Javadoc)
	 * @see org.easymock.IArgumentMatcher#matches(java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	public boolean matches(Object arg0) {

		if ( !(RepetitionRunnable.class.isInstance(arg0)) ) {
			return false;
		}
		
		if (arg0 == null) {
			return false; 
		}
		
		RepetitionRunnable otherRunnable = (RepetitionRunnable) arg0;
		if (!runnable.getActionName().equals(otherRunnable.getActionName())) {
			return false;
		}

		return true;
	}

	public static RepetitionRunnable eqMatcher(RepetitionRunnable runnable) {
		EasyMock.reportMatcher(new SaveAccountingRepetitionRunnableMatcher(runnable));
		return null;
	}
	
}
