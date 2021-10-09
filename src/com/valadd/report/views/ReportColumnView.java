/* ============================================================================
 * 
 * Copyright (c) 2010 Value Added Software, Inc.  All rights reserved.
 * 
 * This program is provided as open source under a Limited Distribution-Open 
 * Public License.  You can modify it for your own use without restriction; it 
 * can only be re-distributed to others through Value Added Software, Inc.  
 * 
 * The program is distributed with the hope that it will be useful and 
 * beneficial, but WITHOUT ANY WARRANTY, without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the license 
 * agreement for further details.  
 * 
 * Any warranty is provided only by a separate service contracting party. You 
 * should have received a copy of the Open Public License, the Limited 
 * Distribution—Open Public License, and the LD-OPL End-user License along 
 * with this program.  
 * 
 * If you did not, please visit www.valadd.com to obtain a copy.
 * ============================================================================
 */
package com.valadd.report.views;

/**
 * @author Pete
 *
 */
public class ReportColumnView {

	/**
	 * 
	 */
	String title;
	int percentWidth;
	
	public ReportColumnView() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the percentWidth
	 */
	public int getPercentWidth() {
		return percentWidth;
	}

	/**
	 * @param percentWidth the percentWidth to set
	 */
	public void setPercentWidth(int percentWidth) {
		this.percentWidth = percentWidth;
	}

	/**
	 * @param title
	 * @param percentWidth
	 */
	public ReportColumnView(String title, int percentWidth) {
		super();
		this.title = title;
		this.percentWidth = percentWidth;
	}

	
}
