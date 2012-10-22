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

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.table.AbstractTableModel;

import org.ourgrid.common.interfaces.to.UserInfo;
import org.ourgrid.common.interfaces.to.UserState;

/**
 * It represents the table that displays the status of the peer
 * users.
 */
public class BrokerTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;
	
	private static final int COLUMN_NUMBER = 5;
	public static final int BROKER_COLUMN = 1;
    public static final int SERVER_COLUMN = 2;
    public static final int PUBLIC_KEY_COLUMN = 3;
    public static final int DELETE_COLUMN = 4;
    public static final int STATUS_COLUMN = 0;
    
    private static final String BROKER_COLUMN_NAME = "Broker";
    private static final String STATUS_COLUMN_NAME = "Status";
    private static final String PUBLIC_KEY_COLUMN_NAME = "Public Key";
    private static final String SERVER_COLUMN_NAME = "Server";
    private static final String DELETE_COLUMN_NAME = "";
    
    private static final String IMAGES_PATH = "/resources/images/";
    public static final URL BROKER_ONLINE_IMAGE_PATH = BrokerTableModel.class.
				getResource(IMAGES_PATH + "user_online.gif");
    public static final URL BROKER_OFFLINE_IMAGE_PATH = BrokerTableModel.class.
				getResource(IMAGES_PATH + "user_offline.gif");
    private static final URL DELETE_ICON_IMAGE_PATH = BrokerTableModel.class.
    			getResource(IMAGES_PATH + "delete.gif");
    
    private Vector<UserInfo> data;
    private Icon userOnlineIcon;
    private Icon userOfflineIcon;
    
	private ImageIcon deleteIcon;
    
    /**
     * Creates new form PeerUserTableModel. 
     * @param data The information about the peer users.
     */
    public BrokerTableModel(Collection<UserInfo> data) {
        setData(data);
        loadIcons();
        loadDeleteIcon();
    }
    
    /**
     * Creates new form PeerUserTableModel.
     */
    public BrokerTableModel() {
		this(null);
	}
    
    /**
	 * Load the icon that represents the deleted workers.
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
     * Defines the informations about the peer users.
     * @param data The informations about the peer users.
     */
	public void setData(Collection<UserInfo> data) {
    	if (data != null) {
    		this.data = new Vector<UserInfo>(data);
    	} else {
    		this.data = new Vector<UserInfo>();
    	}
    	fireTableDataChanged();
    }
    
	/**
	 * Load the icons that represents the status of the users.
	 */
    private void loadIcons() {
		try {
			userOnlineIcon = new ImageIcon(BROKER_ONLINE_IMAGE_PATH, "ONLINE");
			userOfflineIcon = new ImageIcon(BROKER_OFFLINE_IMAGE_PATH, "OFFLINE");
		} catch (Exception e) {
			userOnlineIcon = null;
			userOfflineIcon = null;
		}
	}
    
    /**
     * Verify if the users icons exist.
     * @return <code>true</code> if the users icon exist, <code>false</code>
     * if they are <code>null</code>.
     */
    private boolean useIcons() {
    	return userOnlineIcon != null && userOfflineIcon != null;
    }

    /**
     * Return the number of the peer users.
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
    
    /**
     * Return the description of the specified column.
     * @return The description of the specified column.
     */
    public String getColumnName(int column) {
        switch (column) {
            case BROKER_COLUMN:
                return BROKER_COLUMN_NAME;
            case SERVER_COLUMN:
                return SERVER_COLUMN_NAME;
            case PUBLIC_KEY_COLUMN:
                return PUBLIC_KEY_COLUMN_NAME;
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
    	UserInfo user = data.get(row);
        
        switch (column) {
            case BROKER_COLUMN:
                return user.getUsername();
            case SERVER_COLUMN:
                return user.getXMPPServer();
            case PUBLIC_KEY_COLUMN:
                return user.getPublicKey();
            case STATUS_COLUMN:
            	if (useIcons()) {
            		if (isUserOnline(user)) {
            			return userOnlineIcon;
            		} else {
            			return userOfflineIcon;
            		}
            	}
                return isUserOnline(user) ? "ONLINE" : "OFFLINE";
            case DELETE_COLUMN:
            	if (deleteIcon != null)
            		return deleteIcon;
            	else
            		return "Delete";
            default:
                return null;
        }
    }
    
    /**
     * Verify if the specified user is online.
     * @param user The user to verify.
     * @return <code>true</code> if the user is online, <code>false</code>
     * 		   if it is not.
     */
    private boolean isUserOnline(UserInfo user) {
		
    	return user.getStatus().equals(UserState.LOGGED) || 
			user.getStatus().equals(UserState.CONSUMING);
	}

    @Override
	/*
	 * @see javax.swing.table.AbstractTableModel#getColumnClass(int)
	 */
    public Class<?> getColumnClass(int column) {
        switch (column) {
            case BROKER_COLUMN:
            case SERVER_COLUMN:
            case PUBLIC_KEY_COLUMN:
                return String.class;
            case STATUS_COLUMN:
            	if (useIcons()) {
            		return ImageIcon.class;
            	}
            	return String.class;
            case DELETE_COLUMN:
            	if (deleteIcon != null) {
            		return ImageIcon.class;
            	} else {
            		return String.class;
            	}
            default:
                return Object.class;
        }
    }
    
    /**
     * Removes the specified row of this table.
     * @param selectedRow The row to be removed.
     */
	public void removeRow(int selectedRow) {
		data.remove(selectedRow);		
	}
	
    /**
     * Returns the informations about the users.
     * @return The informations about the users.
     */
    public Collection<UserInfo> getData() {
    	return this.data;
    }

}
