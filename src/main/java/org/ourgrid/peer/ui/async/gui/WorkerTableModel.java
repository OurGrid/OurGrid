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
package org.ourgrid.peer.ui.async.gui;

import java.net.URL;
import java.util.Collection;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.table.AbstractTableModel;

import org.ourgrid.common.interfaces.to.LocalWorkerState;
import org.ourgrid.common.interfaces.to.WorkerInfo;
import org.ourgrid.common.specification.OurGridSpecificationConstants;

/**
 * It represents the table that displays the status of the workers.
 */
public class WorkerTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;
	
	private static final int COLUMN_NUMBER = 3;
    public static final int USER_COLUMN = 1;
    public static final int DELETE_COLUMN = 2;
    public static final int STATUS_COLUMN = 0;
    
    private static final String USER_COLUMN_NAME = "Worker ID";
    private static final String STATUS_COLUMN_NAME = "Status";
    private static final String DELETE_COLUMN_NAME = "";
    
    private static final String IMAGES_PATH = "/resources/images/";
    public static final URL WORKER_IDLE_IMAGE_PATH = WorkerTableModel.class.
				getResource(IMAGES_PATH + "worker_idle.gif");
    public static final URL WORKER_CONTACTING_IMAGE_PATH = WorkerTableModel.class.
				getResource(IMAGES_PATH + "worker_contacting.gif");
    public static final URL WORKER_OWNER_IMAGE_PATH = WorkerTableModel.class.
				getResource(IMAGES_PATH + "worker_owner.gif");
    public static final URL WORKER_DONATED_IMAGE_PATH = WorkerTableModel.class.
				getResource(IMAGES_PATH + "worker_donated.gif");
    public static final URL WORKER_INUSE_IMAGE_PATH = WorkerTableModel.class.
				getResource(IMAGES_PATH + "worker_inuse.gif");
    public static final URL WORKER_ERROR_IMAGE_PATH = WorkerTableModel.class.
				getResource(IMAGES_PATH + "worker_error.gif");
    public static final URL WORKER_UNAVAILABLE_IMAGE_PATH = WorkerTableModel.class.
				getResource(IMAGES_PATH + "worker_unavailable.gif");
    private static final URL DELETE_ICON_IMAGE_PATH = WorkerTableModel.class.
    			getResource(IMAGES_PATH + "delete.gif");
    
    
    private ImageIcon workerIdleIcon;
    private ImageIcon workerContactingIcon;
    private ImageIcon workerDonatedIcon;
    private ImageIcon workerOwnerIcon;
    private ImageIcon workerInuseIcon;
    private ImageIcon workerErrorIcon;
    private ImageIcon workerUnavailableIcon;
    
    private Vector<WorkerInfo> data;

	private ImageIcon deleteIcon;
    
    /**
     * Creates new form WorkerTableModel 
     * @param data The information about the workers.
     */
    public WorkerTableModel(Collection<WorkerInfo> data) {
        setData(data);
        loadStatusIcons();
        loadDeleteIcon();
    }
    
    /**
	 * Load the icon that represents the deleted users.
	 */
    private void loadDeleteIcon() {
    	try { 
    		deleteIcon = new ImageIcon(DELETE_ICON_IMAGE_PATH);
    	} catch (Exception e) {
    		deleteIcon = null;
    		return;
    	}
    	
    }
    
    /**
     * Defines the informations about the workers.
     * @param data The informations about the workers.
     */
    public void setData(Collection<WorkerInfo> data) {
    	if (data != null) {
    		this.data = new Vector<WorkerInfo>(data);
    	} else {
    		this.data = new Vector<WorkerInfo>();
    	}
    }
    
    /**
     * Returns the informations about the workers.
     * @return The informations about the workers.
     */
    public Collection<WorkerInfo> getData() {
    	return this.data;
    }
    
    /**
	 * Load the icons that represent the status of the workers.
	 */
    private void loadStatusIcons() {
		try {
			workerIdleIcon = new ImageIcon(WORKER_IDLE_IMAGE_PATH, "IDLE");
			workerContactingIcon = new ImageIcon(WORKER_CONTACTING_IMAGE_PATH, "CONTACTING");
			workerDonatedIcon = new ImageIcon(WORKER_DONATED_IMAGE_PATH, "DONATED");
			workerOwnerIcon = new ImageIcon(WORKER_OWNER_IMAGE_PATH, "OWNER");
			workerInuseIcon = new ImageIcon(WORKER_INUSE_IMAGE_PATH, "IN_USE");
			workerErrorIcon = new ImageIcon(WORKER_ERROR_IMAGE_PATH, "ERROR");
			workerUnavailableIcon = new ImageIcon(WORKER_UNAVAILABLE_IMAGE_PATH, "UNAVAILABLE");
		} catch (Exception e) {
			workerIdleIcon = null;
			workerContactingIcon = null;
			workerDonatedIcon = null;
			workerOwnerIcon = null;
			workerInuseIcon = null;
			workerErrorIcon = null;
			workerUnavailableIcon = null;
		}
	}
    
    /**
     * Verify if the workers icons exist.
     * @return <code>true</code> if the workers icon exist, <code>false</code>
     * if they are <code>null</code>.
     */
    private boolean useIcons() {
    	return workerIdleIcon != null && workerContactingIcon != null &&
    			workerInuseIcon != null && workerDonatedIcon != null &&
    			workerOwnerIcon != null;
    }

    /**
     * Return the number of the workers.
     */
	public int getRowCount() {
        if (data != null) {
            return data.size();
        }
        
        return 0;
    }

	/**
	 * Return the number of columns of this table.
	 */
    public int getColumnCount() {
        return COLUMN_NUMBER;
    }
    
    @Override
    /**
     * Return the description of the specified column.
     * @return The description of the specified column.
     */
    public String getColumnName(int column) {
        switch (column) {
            case USER_COLUMN:
                return USER_COLUMN_NAME;
            case STATUS_COLUMN:
                return STATUS_COLUMN_NAME;
            case DELETE_COLUMN:
            	return DELETE_COLUMN_NAME;
            default:
                return "";
        }
    }
    
    /**
     * Return the value at the  specified table cell.
     * @param row The row of the cell.
     * @param column The column of the cell.
     */
    public Object getValueAt(int row, int column) {
        WorkerInfo worker = data.get(row);
        
        switch (column) {
            case USER_COLUMN:
                return worker.getWorkerSpec().getAttribute(OurGridSpecificationConstants.ATT_USERNAME) + "@" + 
                		worker.getWorkerSpec().getAttribute(OurGridSpecificationConstants.ATT_SERVERNAME);
            case STATUS_COLUMN:
            	if (useIcons()) {
            		LocalWorkerState state = worker.getStatus();
            		if (state == LocalWorkerState.IDLE) {
            			return workerIdleIcon;
            		} else if (state == LocalWorkerState.DONATED) {
            			return workerDonatedIcon;
            		} else if (state == LocalWorkerState.IN_USE) {
            			return workerInuseIcon;
            		} else if (state == LocalWorkerState.OWNER) {
            			return workerOwnerIcon;
            		} else if (state == LocalWorkerState.ERROR) {
            			return workerErrorIcon;
            		}
            	}
                return worker.getStatus().toString();
            case DELETE_COLUMN:
            	if (deleteIcon != null)
            		return deleteIcon;
            	else
            		return "Delete";
            default:
                return null;
        }
    }
    
    @Override
    /*
     * (non-Javadoc)
     * @see javax.swing.table.AbstractTableModel#getColumnClass(int)
     */
    public Class<?> getColumnClass(int column) {
        switch (column) {
            case USER_COLUMN:
                return String.class;
            case DELETE_COLUMN:
            	if (deleteIcon != null) {
            		return ImageIcon.class;
            	} else {
            		return String.class;
            	}
            case STATUS_COLUMN:
            	if (useIcons()) {
            		return ImageIcon.class;
            	}
                return String.class;
            default:
                return Object.class;
        }
    }

    /**
     * Removes the specified row of this table.
     * @param selectedRow The row to be removed.
     */
	public WorkerInfo removeRow(int selectedRow) {
		return data.remove(selectedRow);		
	}
	
	/**
     * Removes the specified row of this table.
     * @param selectedRow The row to be removed.
     */
	public WorkerInfo getWorkerInfo(int selectedRow) {
		return data.get(selectedRow);		
	}

}
