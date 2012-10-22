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
package org.ourgrid.common.ui.gui;

import java.util.Scanner;

import org.ourgrid.common.config.Configuration;

/**
 *
 * @author  abmar
 */
public class AboutPanel extends javax.swing.JPanel {
    
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final String ABOUT_FILE = "/resources/ABOUT";
	public static final String VERSION_TOKEN = "$VERSION$";
	private static final String FILE_NOT_FOUND_ERROR = "About file not found"; 
    
    public AboutPanel() {
        initComponents();
        loadAboutText();
    }
    
    private void loadAboutText() {
    	String text = loadText();
    	textPane.setText(text);
	}

	private String loadText() {
		StringBuffer buf = new StringBuffer();
		String newline = System.getProperty("line.separator");
		Scanner scn;
		try {
			scn = new Scanner(getClass().getResourceAsStream(ABOUT_FILE));
		} catch (Exception e) {
			return FILE_NOT_FOUND_ERROR;
		}
		while(scn.hasNext()) {
			buf.append(scn.nextLine());
			buf.append(newline);
		}
		scn.close();
		
		return buf.toString().replace(VERSION_TOKEN, Configuration.VERSION.toString());
	}

	private void initComponents() {

        scrollPane = new javax.swing.JScrollPane();
        textPane = new javax.swing.JTextPane();

        textPane.setEditable(false);
        scrollPane.setViewportView(textPane);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(scrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(scrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 276, Short.MAX_VALUE)
                .addContainerGap())
        );
    }
    
    private javax.swing.JScrollPane scrollPane;
    private javax.swing.JTextPane textPane;
    
}
