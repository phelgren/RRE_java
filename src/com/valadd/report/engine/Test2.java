/*
 * Updated on July 16th, 2021 with new Jasper version and some tweaks to the testing suite
 *
 *  *============================================================================
   
   Copyright (c) 2008-2021 - Value Added Software, Inc.  All rights reserved.
  
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
    
    Got a question for Pete Helgren (the author)?  Try pete@valadd.com or pete@petesworkshop.com
 * ============================================================================
 */

package com.valadd.report.engine;

import java.util.HashMap;

public class Test2{

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
	    // Test 3 is Print a compiled report
		
    	boolean didItWork = true;
    	
    	didItWork = runTest();
    	
	}
	
    public static boolean runTest(){
    	
    	boolean success = true;
    	
    	String reportName = "/reports/report_templates/Employee_Listing_new.jasper";
    	String reportOutput = "/reports/report_output/Employee_List";
 
    	HashMap reportParameters = null;
    	String outputFormat = "PDF";
    	

    		Generator gen = new Generator();
    		
			success = gen.iPrintCompiledReport(reportName, reportOutput, reportParameters, outputFormat);
  	
    	return success;
    }

}
