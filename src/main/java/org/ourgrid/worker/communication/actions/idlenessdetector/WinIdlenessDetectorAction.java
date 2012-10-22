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
package org.ourgrid.worker.communication.actions.idlenessdetector;

import java.io.Serializable;

import org.ourgrid.common.internal.OurGridRequestControl;
import org.ourgrid.worker.request.WinIdlenessDetectorActionRequestTO;

import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;
import br.edu.ufcg.lsd.commune.container.servicemanager.actions.RepeatedAction;

import com.sun.jna.Native;
import com.sun.jna.Structure;
import com.sun.jna.win32.StdCallLibrary;

/**
 */
public class WinIdlenessDetectorAction implements RepeatedAction {

	public void run(Serializable handler, ServiceManager serviceManager) {
		
		WinIdlenessDetectorActionRequestTO to = new WinIdlenessDetectorActionRequestTO();
		
		OurGridRequestControl.getInstance().execute(to, serviceManager);
	}

	public interface Kernel32 extends StdCallLibrary { 
		Kernel32 INSTANCE = (Kernel32) Native.loadLibrary("kernel32", Kernel32.class);
		
		/**
		 * Retrieves the number of milliseconds that have elapsed since the system was started.
		 * @see http://msdn2.microsoft.com/en-us/library/ms724408.aspx
		 * @return number of milliseconds that have elapsed since the system was started.
		 */
		public int GetTickCount();
	};
	
	public interface User32 extends StdCallLibrary { 
		User32 INSTANCE = (User32)Native.loadLibrary("user32", User32.class);
		
		/**
		 * Contains the time of the last input.
		 * @see http://msdn.microsoft.com/library/default.asp?url=/library/en-us/winui/winui/windowsuserinterface/userinput/keyboardinput/keyboardinputreference/keyboardinputstructures/lastinputinfo.asp
		 */
		public static class LASTINPUTINFO extends Structure {
			public int cbSize = 8;
			
			/// Tick count of when the last input event was received.
			public int dwTime;
		}
		
		/**
		 * Retrieves the time of the last input event.
		 * @see http://msdn.microsoft.com/library/default.asp?url=/library/en-us/winui/winui/windowsuserinterface/userinput/keyboardinput/keyboardinputreference/keyboardinputfunctions/getlastinputinfo.asp
		 * @return time of the last input event, in milliseconds
		 */
		public boolean GetLastInputInfo(LASTINPUTINFO result);
	};
}
