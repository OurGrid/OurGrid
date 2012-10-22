package org.ourgrid.worker.business.requester;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.response.LoggerResponseTO;
import org.ourgrid.worker.WorkerConstants;
import org.ourgrid.worker.business.controller.IdlenessDetectorController;
import org.ourgrid.worker.business.dao.IdlenessDetectorDAO;
import org.ourgrid.worker.business.dao.WorkerDAOFactory;
import org.ourgrid.worker.request.LinuxIdlenessDetectorActionRequestTO;

import com.sun.jna.Library;
import com.sun.jna.Native;

public class LinuxIdlenessDetectorActionRequester extends AbstractScheduledIdlenessDetectorActionRequester<LinuxIdlenessDetectorActionRequestTO> {
	
	private LinkedList<String[ ]> currentStatus = new LinkedList<String[ ]>();

	private LinkedList<String[ ]> oldStatus = new LinkedList<String[ ]>();
	
	
	public List<IResponseTO> execute(LinuxIdlenessDetectorActionRequestTO request) {
		List<IResponseTO> responses = new ArrayList<IResponseTO>();
		
		if (isIdle(responses, request.getXIdleTimeLibPath())) {
			IdlenessDetectorController.getInstance().resumeWorker(responses);
		} else {
			IdlenessDetectorController.getInstance().pauseWorker(responses);
		}
		
		return responses;
	}
	
	private interface XIdleTime extends Library {
		public long getIdleTime();
	}
	
	private boolean isIdle(List<IResponseTO> responses, String xIdleTimeLibPath) {
		
		IdlenessDetectorDAO idlenessDetectorDAO = WorkerDAOFactory.getInstance().getIdlenessDetectorDAO();
		
		boolean idle = super.isIdle();
		
		if (!idlenessDetectorDAO.isActive()) {
			return idle;
		}

		try {
			//make sure that xIdleTimeLibPath is an absolute path
			
			File libXIdleTimeFile = new File(xIdleTimeLibPath);
			
			if (!libXIdleTimeFile.isAbsolute()) {
				xIdleTimeLibPath = libXIdleTimeFile.getAbsolutePath();
			}
			
			XIdleTime xIdleTime = (XIdleTime) Native.loadLibrary(xIdleTimeLibPath, XIdleTime.class);
			
			long idleTime = xIdleTime.getIdleTime();
			
			if (idleTime > -1) {
				return  idle && (idleTime >= idlenessDetectorDAO.getIdlenessTime());
			}
		} catch ( Exception e ) {
			responses.add(new LoggerResponseTO("Error on load the XIdleTime lib.", LoggerResponseTO.ERROR, e));
		}
		
		responses.add(new LoggerResponseTO("Wasn't able to use XIdleTime lib idleness" +
				" detector. Trying the old method.", LoggerResponseTO.DEBUG));
		
		try {
			if ( !currentStatus.isEmpty() ) {
				this.oldStatus = currentStatus;	
			}
			this.currentStatus = new LinkedList<String[ ]>();
			this.readInterruptsFile(currentStatus);
		} catch ( IOException ioe ) {
			responses.add(new LoggerResponseTO("Error on reading interrupt files.", LoggerResponseTO.ERROR, ioe));
		}
		
		if ( this.checkIdleness() ) {
			idlenessDetectorDAO.incrementTime(WorkerConstants.IDLENESSDETECTOR_VERIFICATION_TIME);
		} else {
			idlenessDetectorDAO.resetTime();
		}
		
		return idle && (idlenessDetectorDAO.getTime() >= idlenessDetectorDAO.getIdlenessTime());
	}
	
	/**
	 * Complets the work of this.isIdle() method. It makes the comparation
	 * itself.
	 * 
	 * @return <code>true</code> if the computer is idle and
	 *         <code>false</code> otherwise.
	 */
	private boolean checkIdleness() {

		if (this.oldStatus.isEmpty()) {
			try {
				readInterruptsFile(oldStatus);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return false;
		}
		
		boolean idle = true;
		Iterator<String[ ]> it = currentStatus.iterator();
		while ( it.hasNext() && idle == true ) {

			String[ ] device = it.next();

			if ( device.length == 4 ) {
				String name = device[3];
				String deviceId = device[0];
				if ( name.equals( "keyboard" ) || name.equals( "PS/2Mouse" ) || deviceId.equals( "1:" )
						|| deviceId.equals( "12:" ) ) {
					String interrupt = device[1];
					int index = currentStatus.indexOf( device );
					String[ ] oldStatusObj = oldStatus.get( index );
					String oldInterrupt = oldStatusObj[1];
					if ( !interrupt.equals( oldInterrupt ) )
						idle = false;
				}
			}
		}
		return idle;
	}
	
	/**
	 * This method will get the last status of interruptions provided by the
	 * linux system. It reads the file "/proc/interrupts" and stores the actual
	 * state at a data structure.
	 * 
	 * @throws IOException signalize the non existence of the "/proc/interrupts"
	 *         file or if it could not be correctly used. Note that the file
	 *         should exist if you are using Linux system. If not PLEASE
	 *         REPORT!!!
	 */
	private void readInterruptsFile(LinkedList<String[]> status) throws IOException {

		BufferedReader interruptState = null;
		try {
			interruptState = new BufferedReader( new FileReader( "/proc/interrupts" ) );

			interruptState.readLine(); // Ignoring the first line that is not a
			// device.
			String deviceStatus = interruptState.readLine();
			while ( deviceStatus != null ) {
				this.storeStatus( deviceStatus, status );
				deviceStatus = interruptState.readLine();
			}
			interruptState.close();
		} catch ( IOException e ) {
			throw e;
		} finally {
			if ( interruptState != null ) {
				try {
					interruptState.close();
				} catch ( IOException e1 ) {
					
				}
			}
		}
	}
	
	/**
	 * Stores informations about a device at a array and puts it at the List
	 * mantained by this class. The number of informations about the devices can
	 * vary and because of this the algorithm is a bit more complicated.
	 * 
	 * @param interruptFileLine A line readed from the interruptions file.
	 */
	private void storeStatus( String interruptFileLine, LinkedList<String[]> status ) {

		String[ ] info;
		StringTokenizer dsTokenizer = new StringTokenizer( interruptFileLine, " " );
		int tokensNumber = dsTokenizer.countTokens();
		if ( tokensNumber > 4 ) {
			info = new String[ 4 ];
		} else {
			info = new String[ tokensNumber ];
		}
		for ( int counter = 0; counter < info.length; counter++ ) {
			info[counter] = dsTokenizer.nextToken().trim();
		}
		if ( tokensNumber == 5 ) {
			info[3] = info[3] + dsTokenizer.nextToken().trim();
		}
		// Store the readed informations always at the actualStatus list.
		status.addLast( info );
	}
}
