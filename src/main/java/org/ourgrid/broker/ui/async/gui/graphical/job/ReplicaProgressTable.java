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
package org.ourgrid.broker.ui.async.gui.graphical.job;

import java.text.DecimalFormat;
import java.util.Enumeration;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;

import org.ourgrid.broker.business.scheduler.extensions.GenericTransferProgress;
import org.ourgrid.common.interfaces.to.GenericTransferHandle;
import org.ourgrid.common.job.GridProcess;
import org.ourgrid.common.util.CommonUtils;


public class ReplicaProgressTable extends AbstractTableModel {

	public static final int BAR_MIN = 0;

	public static final int BAR_MAX = 100;

	private static final long serialVersionUID = 1L;

	private final Map<Integer,GenericTransferHandle> indexToHandle;

	private final Map<GenericTransferHandle,JProgressBar> progressBarsMap;

	private JTable table;

	private final GridProcess replica;

	/** Array of column names */
	protected static final String[ ] columnNames = { "Type", "File name", "Progress", "%", "Rate (kb/s)" };

	protected static final int colType = 0;

	protected static final int colName = 1;

	protected static final int colProgress = 2;

	protected static final int colPercent = 3;

	protected static final int colRate = 4;

	private boolean active;


	public ReplicaProgressTable( GridProcess replica ) {

		this.replica = replica;
		this.indexToHandle = CommonUtils.createSerializableMap();
		this.progressBarsMap = CommonUtils.createSerializableMap();
		table = new JTable( this );
		table.setEnabled( true );
		table.setColumnSelectionAllowed( false );
		table.setCellSelectionEnabled( false );
		table.setRowSelectionAllowed( false );

		Enumeration<TableColumn> cols = table.getColumnModel().getColumns();
		while ( cols.hasMoreElements() ) {
			cols.nextElement().setCellRenderer( new ReplicaProgressTableCellRendered() );
		}

		TableColumn columnType = table.getColumnModel().getColumn( colType );
		columnType.setMaxWidth( 100 );
		TableColumn columnName = table.getColumnModel().getColumn( colName );
		columnName.setMinWidth( 100 );
		TableColumn columnProgress = table.getColumnModel().getColumn( colProgress );
		columnProgress.setMinWidth( 100 );
		TableColumn columnPercent = table.getColumnModel().getColumn( colPercent );
		columnPercent.setMaxWidth( 30 );
		TableColumn columnRate = table.getColumnModel().getColumn( colRate );
		columnRate.setMaxWidth( 100 );
		this.active = true;
	}


	public int getColumnCount() {

		return 5;
	}


	public int getRowCount() {

		return progressBarsMap.size();
	}


	public Object getValueAt( int rowIndex, int columnIndex ) {

		GenericTransferHandle handle = indexToHandle.get( rowIndex );
		
		if ( handle != null ) {
			GenericTransferProgress transferProgress = replica.getTransferProgress( handle );
			if ( transferProgress != null ) {
				switch ( columnIndex ) {
					case colType:
						return new JLabel( transferProgress.isOutgoing() ? "Upload" : "Download" );
					case colName:
						return new JLabel( transferProgress.getFileName() );
					case colProgress:
						return progressBarsMap.get( handle );
					case colPercent:
						return new JLabel( getProgressString( transferProgress.getProgress() ) );
					case colRate:
						return new JLabel( Long.toString( Math.round( transferProgress.getTransferRate() ) ) );
				}
			}
		}
		
		return null;
	}


	private String getProgressString( double progress ) {

		DecimalFormat format = new DecimalFormat();
		format.setMaximumFractionDigits( 1 );
		return format.format( progress * 100D );
	}


	@Override
	public String getColumnName( int column ) {

		return columnNames[column];
	}


	@Override
	public boolean isCellEditable( int rowIndex, int columnIndex ) {

		return false;
	}


	private void updateProgressBar( GenericTransferHandle handle, double value, boolean outgoing ) {

		JProgressBar bar;
		if ( progressBarsMap.containsKey( handle ) ) {
			bar = progressBarsMap.get( handle );
		} else {
			bar = new JProgressBar();
			progressBarsMap.put( handle, bar );
			int pos = progressBarsMap.size() - 1;
			indexToHandle.put( pos, handle );
			bar.setMinimum( BAR_MIN );
			bar.setMaximum( BAR_MAX );
		}
		bar.setValue( (int) Math.round( value * BAR_MAX ) );
	}


	public JTable getTable() {

		return table;
	}


	public void update() {

		Map<GenericTransferHandle, GenericTransferProgress> transfersProgresses = replica.getTransfersProgress();


		
		for ( GenericTransferProgress progress : transfersProgresses.values() ) {
			updateProgressBar( progress.getHandle(), progress.getProgress(), progress.isOutgoing() );
		}

		this.active = true;
		
	}


	public void markInactive() {

		this.active = false;
	}


	public boolean isActive() {

		return active;
	}


	@Override
	public String toString() {

		return "Progress table of replica: " + replica.getHandle() + ", transfers: "
				+ replica.getTransfersProgress().size();
	}

}
