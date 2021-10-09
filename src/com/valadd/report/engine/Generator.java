/*
 * Created on Apr 1, 2008 by Pete Helgren pete@valadd.com 
 * Updated on July 16th, 2021 (one of many) to accomodate the new Jasper libs
 * Little done on the RPG side just Java updates and some jar consolidation 
 *
 *============================================================================
   
   Copyright (c) 2008 - Value Added Software, Inc.  All rights reserved.
  
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
 * ============================================================================
 * 
 * Ideas - Add email option
 * 		 - Output to web server (OutputStream)
 *       - More parameters - images, page sizes for text, pagination suppression
 * 		 
 * Version 0.2 Released April 14th, 2008
 * Version 0.4 Released April 27th, 2008
 * Version 0.4.1 Released January 7th, 2008
 * 	I added a couple of new methods that allow a subject and body text to be passed to the email routine so 
 *  that each email could be more customized.  The text can contain some "variables" to help with customization
 *  use the following "place holders":
 *  $reportName to have the report name print as part of the text in either the subject or body text
 *  $reportOutput to have the report full path output name print as part of the text in either the subject or body text
 *  	Note the report output doesn't include the file type or extension so add the following if needed:
 *  $reportFormat to have the report 'extension' (e.g. PDF, HTML, CSV etc) print as part of the text in either the subject or body text
 * 
 * Version 0.5 Released April 22nd, 2010
 *  Added iText support (ReportView and ReportColumnView classes)
 *  
 *  Version 0.6 Released April 24th, 2010
 *  (Yeah short release cycle but I thought it would take longer)
 *  Added BIRT support (WAHOO!)
 *  
 *  Version 1.0 Released July 16th, 2021  Updated the Jasper libs and consolidated the jars
 *  RRE supports the following output formats:
 *  PDF, XLS, HTML, TXT, RTF, CSV, ODT
 */
package com.valadd.report.engine;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.BasicConfigurator;

import com.valadd.util.EzMailer;
import com.valadd.util.UtilURL;

import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.export.HtmlExporter;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRRtfExporter;
import net.sf.jasperreports.engine.export.JRTextExporter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.oasis.JROdtExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.export.SimpleCsvExporterConfiguration;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleHtmlExporterOutput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;
import net.sf.jasperreports.export.SimpleTextReportConfiguration;
import net.sf.jasperreports.export.SimpleWriterExporterOutput;
import net.sf.jasperreports.export.SimpleXlsExporterConfiguration;

public class Generator {
	
	  private static final String DB_USER_NAME = "database.userName";

	  String rrehome = System.getenv("RRE_HOME");
	  
	  //Default location if no RRE_HOME property found
	  private static final String DB_PROPERTIES_FILE_NAME = File.separator +"rre" + File.separator + "rre.properties";
	  
	  private static final String DB_USEPROPERTIES = "database.useProperties";

	  private static final String DB_PASSWORD = "database.password";

	  private static final String DB_URL = "database.url";

	  private static final String DB_DRIVER = "database.driverName";

	  private static final String SMTP_HOST = "smtp.host";
	  
	  // Birt curiously requires a HOME location  - might want to explore using an ENV variable as well.
	  private static final String BIRT_HOME = "birt.home";
	  
	  private static final String DEBUG = "debug";

	  private Properties _props = new Properties();
	  
	  private  boolean _useProperties;

	  private static String _userName;

	  private static String _password;

	  private static String _dbURL;
	  
	  private static String _smtpHost;
	  
	  private static String _debug;
	
	  private static String _birt_home;
	  
	  
	  private Connection setConnect(){
			Connection conn = null;
			//URL url = UtilURL.fromHomePath();
			//if(url == null)
		    //url = UtilURL.fromFilename(DB_PROPERTIES_FILE_NAME);
			//String currentDir = System.getProperty("user.dir");
			//System.out.println(currentDir+DB_PROPERTIES_FILE_NAME);
			URL url = UtilURL.fromFilename(DB_PROPERTIES_FILE_NAME);
		    
		    if (url == null)
		        throw new IllegalStateException("Unable to find the properties file '" + DB_PROPERTIES_FILE_NAME + "'.");
		    try
		    {
		      _props.load(url.openStream());
		      _props.list(System.out);

		      Class.forName(_props.getProperty(DB_DRIVER));

		      _useProperties = _props.getProperty(DB_USEPROPERTIES, "false").equals("true");
		      _userName = _props.getProperty(DB_USER_NAME);
		      _password = _props.getProperty(DB_PASSWORD);
		      _dbURL = _props.getProperty(DB_URL);
		      _debug = _props.getProperty(DEBUG);
		      _birt_home = _props.getProperty(BIRT_HOME);
		      
				try{
					if(_debug.equals("true"))
						System.out.println("User: "+_userName + " password: "+_password);
					
					conn = DriverManager.getConnection(_dbURL,_userName,_password);
					
				}

				catch (SQLException sqle){
					sqle.printStackTrace();
				}
				
		    }
		    catch (IOException ioe)
		    {
		      ioe.printStackTrace();

		    }
		    catch (ClassNotFoundException cnfe)
		    {
		      cnfe.printStackTrace();

		    }
		    
		    return conn;
		}
	
	  public Generator(){ // Default Constructor
		  BasicConfigurator.configure();
	  }
		// This wrapper assumes that connection from properties settings will be used and that file is 
		// will be emailed to recipient
		  
		public boolean iEmailReportAlt(String sender, String recipient, String subject, String bodyText, String reportName,String reportOutput,HashMap reportParameters, String outputFormat, boolean compileFirst){
			Connection aConn = null;
			boolean success = true;
			
			aConn = setConnect();
			
			String rn = reportName.replaceAll("\\\\", "\\\\\\\\");
			String ro = reportOutput.replaceAll("\\\\", "\\\\\\\\");
			String of = outputFormat.replaceAll("\\\\", "\\\\\\\\");
			
			String finalText = bodyText.replaceAll("\\$reportName", rn).replaceAll("\\$reportOutput", ro).replaceAll("\\$outputFormat", of);
			String finalSubject = subject.replaceAll("\\$reportName", rn).replaceAll("\\$reportOutput", ro).replaceAll("\\$outputFormat", of);

			success = printReport(aConn, reportName, reportOutput, reportParameters, outputFormat, compileFirst);

			if(success){
				
				
				 try {
					 _smtpHost = _props.getProperty(SMTP_HOST);
					EzMailer.sendMessageAttach(_smtpHost,
					   		 sender, recipient,
					   		 finalSubject, finalText, reportOutput + "." + outputFormat);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			disconnect(aConn);
			
			return success;
		}
		// This wrapper assumes that connection from properties settings will be used and that file is 
		// will be emailed to recipient
		  
		public boolean iEmailReport(String sender, String recipient, String reportName,String reportOutput,HashMap reportParameters, String outputFormat, boolean compileFirst){
			Connection aConn = null;
			boolean success = true;
			
			aConn = setConnect();

			success = printReport(aConn, reportName, reportOutput, reportParameters, outputFormat, compileFirst);

			if(success){
				
				
				 try {
					 _smtpHost = _props.getProperty(SMTP_HOST);
					EzMailer.sendMessageAttach(_smtpHost,
					   		 sender, recipient,
					   		 "Your report completed normally", "Your report " + reportName + " ran and completed normally and is located here: " + reportOutput + "." + outputFormat,
					   		 reportOutput + "." + outputFormat);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			disconnect(aConn);
			
			return success;
		}
		// This wrapper assumes that connection from properties settings will be used and that file is 
		// is a compiled .jasper file (needs no compiling) and will be emailed
		// for BIRT this is the usual call
		  
		public boolean iEmailCompiledReport(String sender, String recipient, String reportName,String reportOutput,HashMap reportParameters, String outputFormat){

			boolean success = true;
			
			success = iEmailReport(sender, recipient, reportName, reportOutput, reportParameters, outputFormat, false );

			return success;
		}
		// This wrapper assumes that connection from properties settings will be used and that file is 
		// is an uncompiled .jrxml file (needs to be compiled) and will be emailed
		  
		public boolean iEmailUnCompiledReport(String sender, String recipient, String reportName,String reportOutput,HashMap reportParameters, String outputFormat){

			boolean success = true;
			
			success = iEmailReport(sender, recipient, reportName, reportOutput, reportParameters, outputFormat, true );
			
			return success;
		}
		
		// This wrapper assumes that connection from properties settings will be used and that file is 
		// is a compiled .jasper file (needs no compiling) and will be emailed
		//  The "ALT" allows you to include some body in the text of the email sent to the recipient of the report
		  
		public boolean iEmailCompiledReportAlt(String sender, String recipient, String subject, String bodyText, String reportName,String reportOutput,HashMap reportParameters, String outputFormat){

			boolean success = true;
			
			success = iEmailReportAlt(sender, recipient, subject, bodyText, reportName, reportOutput, reportParameters, outputFormat, false);

			return success;
		}
		// This wrapper assumes that connection from properties settings will be used and that file is 
		// is an uncompiled .jrxml file (needs to be compiled) and will be emailed
		//  The "ALT" allows you to include some body in the text of the email sent to the recipient of the report
		  
		public boolean iEmailUnCompiledReportAlt(String sender, String recipient, String subject, String bodyText, String reportName,String reportOutput,HashMap reportParameters, String outputFormat){

			boolean success = true;
			
			success = iEmailReportAlt(sender, recipient, subject, bodyText, reportName, reportOutput, reportParameters, outputFormat, true );
			
			return success;
		}
		
	// This wrapper assumes that connection from properties settings will be used and that file is 
	// is a compiled .jasper file (needs no compiling)
	  
	public boolean iPrintCompiledReport(String reportName,String reportOutput,HashMap reportParameters, String outputFormat){
		Connection aConn = null;
		boolean success = true;
		
		aConn = setConnect();

		success = printReport(aConn, reportName, reportOutput, reportParameters, outputFormat, false);
		
		disconnect(aConn);
		
		return success;
	}
	
	// This wrapper assumes that connection from properties settings will be used and that file is 
	// is NOT compiled .jasper file.  Passed file should be a .jrxml file and will be compiled before printing
	  
	public boolean iPrintUnCompiledReport(String reportName,String reportOutput,HashMap reportParameters, String outputFormat){
		Connection aConn = null;
		boolean success = true;
		
		aConn = setConnect();

		success = printReport(aConn, reportName, reportOutput, reportParameters, outputFormat, true);
		
		disconnect(aConn);
		
		return success;
	}
	
	public boolean iPrintReport(String host, String libraryList, String userID, String password, String reportName, String reportOutput, HashMap reportParameters, String outputFormat, boolean compileFirst) throws IOException
	{
		Connection aConn = null;
		boolean success = true;
		
		// If the properties file is used skip credentials
		StringBuffer urlBuffer =  new StringBuffer().append("jdbc:as400://").append(host).append(";libraries=").append(libraryList).append(";user=").append(userID).append(";password=").append(password);

		String url = urlBuffer.toString();
		
		Map parmMap = new HashMap();

		try {
			Class.forName("com.ibm.as400.access.AS400JDBCDriver");

			if(_useProperties)
				aConn = setConnect();
			else
				aConn = DriverManager.getConnection(url);	
			success = printReport(aConn, reportName, reportOutput, reportParameters, outputFormat, compileFirst );
		
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			success = false;
			e.printStackTrace();
		}
		catch (SQLException e) {
			// TODO Auto-generated catch block
			success = false;
			e.printStackTrace();
		}
		
		disconnect(aConn);
		
		return success;
		
	}
		public boolean printReport(Connection pConnection, String reportName, String reportOutput, HashMap reportParams, String outputFormat, boolean compileFirst ) {

	    boolean success = true;
	    
	    System.out.println("Ignoring Font errors");
	    
	    JasperReportsContext jasperReportsContext = DefaultJasperReportsContext.getInstance();
	    
	    jasperReportsContext.setProperty("net.sf.jasperreports.awt.ignore.missing.font", "true");
		    		
		JasperPrint jasperPrint = returnReportPrint(pConnection, reportName,reportParams ,compileFirst);

		    
			File out = null;
			
				
		    //ByteArrayOutputStream baos = new ByteArrayOutputStream();

		    if(outputFormat.equals("PDF")){
		    	JRPdfExporter exporter = new JRPdfExporter();
		    	out = new File(reportOutput+".pdf");
		    	exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
		    	exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(out));
		    	
		    	SimplePdfExporterConfiguration configuration = new SimplePdfExporterConfiguration();
		    	
		    	exporter.setConfiguration(configuration);

        		    try {
						exporter.exportReport();
					} catch (JRException e) {
						// TODO Auto-generated catch block
						success = false;
						e.printStackTrace();
					}
		    }
		    if(outputFormat.equals("HTML")){
		    	HtmlExporter exporter = new HtmlExporter();
		    	out = new File(reportOutput+".html");
		    	exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
		    	SimpleHtmlExporterOutput output = new SimpleHtmlExporterOutput(out);
		    	exporter.setExporterOutput(output);
		    	
    		    try {
					exporter.exportReport();
				} catch (JRException e) {
					// TODO Auto-generated catch block
					success = false;
					e.printStackTrace();
				}
		    }
		    if(outputFormat.equals("XLS"))
		    {
		    	JRXlsExporter exporter = new JRXlsExporter();
		    	out = new File(reportOutput+".xls");
		    	exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
		    	exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(out));
		    	
		    	SimpleXlsExporterConfiguration configuration = new SimpleXlsExporterConfiguration();
		    	
		    	exporter.setConfiguration(configuration);
		    	
        		    try {
						exporter.exportReport();
					} catch (JRException e) {
						// TODO Auto-generated catch block
						success = false;
						e.printStackTrace();
					}
		    }
		    if(outputFormat.equals("TXT"))
		    {
		    	JRTextExporter exporter = new JRTextExporter();
		    	out = new File(reportOutput+".txt");
		    	exporter.setExporterInput(new SimpleExporterInput(jasperPrint));

		    	SimpleWriterExporterOutput exporterOutput = new SimpleWriterExporterOutput(out);
		    	exporter.setExporterOutput(exporterOutput);
		    	
		    		// To do: Perhaps a separate API to run the text reports with more options
		    	SimpleTextReportConfiguration txtConfiguration = new SimpleTextReportConfiguration();
		    	
		    	txtConfiguration.setPageWidthInChars(132);
		    	txtConfiguration.setPageHeightInChars(66);

		    	exporter.setConfiguration(txtConfiguration);

        		    try {
						exporter.exportReport();
					} catch (JRException e) {
						// TODO Auto-generated catch block
						success = false;
						e.printStackTrace();
					}
		    }
		    if(outputFormat.equals("RTF"))
		    {
		    	JRRtfExporter exporter = new JRRtfExporter();
		    	
		    	out = new File(reportOutput+".rtf");
		    	SimpleWriterExporterOutput exporterOutput = new SimpleWriterExporterOutput(out);
		    	exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
		    	exporter.setExporterOutput(exporterOutput);
		    	
                    
        		    try {
						exporter.exportReport();
					} catch (JRException e) {
						// TODO Auto-generated catch block
						success = false;
						e.printStackTrace();
					}
		    }
		    if(outputFormat.equals("CSV"))
		    {
		    	JRCsvExporter exporter = new JRCsvExporter();
		    	out = new File(reportOutput+".csv");
		    	exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
		    	SimpleWriterExporterOutput exporterOutput = new SimpleWriterExporterOutput(out);
		    	exporter.setExporterOutput(exporterOutput);
		    	
		    	SimpleCsvExporterConfiguration configuration = new SimpleCsvExporterConfiguration();
		    	
		    	exporter.setConfiguration(configuration);
		    	
        		    try {
						exporter.exportReport();
					} catch (JRException e) {
						// TODO Auto-generated catch block
						success = false;
						e.printStackTrace();
					}
		    }
		    if(outputFormat.equals("ODT"))
		    {
		    	JROdtExporter exporter = new JROdtExporter();
		    	out = new File(reportOutput+".odt");
		    	
		    	exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
		    	exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(out));
		    	
        		    try {
						exporter.exportReport();
					} catch (JRException e) {
						// TODO Auto-generated catch block
						success = false;
						e.printStackTrace();
					}
		    }
		 
		    return success;
	}
		
	
    public JasperPrint returnReportPrint(Connection pConnection, String fileName, HashMap reportParams, boolean compile) {
    	JasperPrint jasperPrint = null;
    	
    	if(reportParams == null)
    		reportParams = new HashMap();
    	
    	
    	if(_debug!=null && _debug.equals("true")){
    		if(reportParams==null)
    			System.out.println("Object is null");
    		else
    		{
    		 Set s;
    		 Iterator it;
    		 s = reportParams.entrySet();
    		 it = s.iterator();
    		 System.out.println("[HashMap]");
    		 while (it.hasNext()){
    			 String obj = (String) it.next();
    			 if(obj!=null)
    				 System.out.println(obj);
    			 else
    				 System.out.println("Value pair is null");
    		 }
    	  }
    	}
    		
            try{
            	// There is a corner case where someone might request a compiled report that hasn't been
            	// compiled yet.  The tricky part is that there will be an assumption that an uncompiled report
            	// will have a jrxml extension.  So, check to see if there is a .jasper file and, if not, TRY
            	// to compile a file substituting jrxml as the extension
            	
            	File f = new File(fileName);
            	
            	if(!compile && !f.exists())
            	{
        			String newFilename = fileName.substring(0, fileName.length() - 6) + "jrxml";
                    JasperCompileManager.compileReportToFile(newFilename,fileName);
            	}
            	
            	// In any case that file should be there
            	InputStream inputStream = JRLoader.getFileInputStream(fileName);
            	
            		if(compile) {
            				// So, build the jasper file on a compile JUST in case its to be used later
                        JasperReport jasperReport = JasperCompileManager.compileReport(fileName);
                        jasperPrint = JasperFillManager.fillReport(jasperReport, reportParams, pConnection);
            		}
            		else {// already compiled
            			jasperPrint = JasperFillManager.fillReport(inputStream, reportParams, pConnection);
            			//jasperPrint = (JasperPrint)JRLoader.loadObject(sourceFile);
            		}
            }catch(JRException ex) {
                String connectMsg = "Could not create the report stream " + ex.getMessage() + " " + ex.getLocalizedMessage();
                System.out.println(connectMsg);
            	ex.printStackTrace();
            }
            return jasperPrint;
        }
    
    private boolean disconnect(Connection conn){
    	
    	boolean status = true;
    	
    	if(conn != null){
    	try {
			conn.close();
		} catch (SQLException e) {
			status = false;
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	}
    	return status;
    	
    }
    
}
