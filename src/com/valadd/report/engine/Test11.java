/*
 * Created on Apr 12, 2008 by Pete Helgren
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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Test11{

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
	    // Test 11 is Email an uncompiled chart with Parameters
		
    	boolean didItWork = true;
    	
    	didItWork = runTest();
    	
	}
	
    public static boolean runTest(){
    	
    	boolean success = true;
    	
    	String reportName = "/reports/report_templates/Employee_Gender_Chart.jrxml";
    	String reportOutput = "/reports/report_output/Employee_Gender_Chart_test11";
    	String sender = "pete@valadd.com";
    	String recipient = "pete@opensource4i.com";
    	HashMap reportParameters = new HashMap();
    	reportParameters.put("selectZip", "84039");
    	String outputFormat = "PDF";
    	

    		Generator gen = new Generator();
    		
			success = gen.iEmailUnCompiledReport(sender,recipient,reportName, reportOutput, reportParameters, outputFormat);
  	
    	return success;
    }

}