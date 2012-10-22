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

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

public class ReplicaProgressTableCellRendered extends DefaultTableCellRenderer implements TableCellRenderer {

	private static final long serialVersionUID = 1L;


	@Override
	public Component getTableCellRendererComponent( JTable table, Object value, boolean isSelected, boolean hasFocus,
													int row, int column ) {

		if ( value instanceof JLabel ) {
			JLabel label = (JLabel) value;
			label.setHorizontalAlignment( JLabel.CENTER );
			label.validate();
			return label;
		} else if ( value instanceof JProgressBar ) {
			JProgressBar bar = (JProgressBar) value;
			bar.validate();
			return bar;
		}

		return null;
	}
}
