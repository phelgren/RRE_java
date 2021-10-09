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

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;



/**
 * @author Pete
 *
 */
public class ReportView {

	/**
	 * 
	 */
	String heading;
	ArrayList<ReportColumnView> columns; // ReportColumnView Objects
	String reportName;
	String fileName; // Output file name for report (if any
	PdfPTable basicDatatable = null;
	Document basicDoc = null;
	boolean trace = false;
	
	public ReportView() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the heading
	 */
	public String getHeading() {
		return heading;
	}

	/**
	 * @param heading the heading to set
	 */
	public void setHeading(String heading) {
		this.heading = heading;
	}

	/**
	 * @return the columns
	 */
	public List<ReportColumnView> getColumns() {
		return columns;
	}

	/**
	 * @param columns the columns to set
	 */
	public void setColumns(ArrayList<ReportColumnView> columns) {
		this.columns = columns;
	}

	/**
	 * @return the reportName
	 */
	public String getReportName() {
		return reportName;
	}

	/**
	 * @param reportName the reportName to set
	 */
	public void setReportName(String reportName) {
		this.reportName = reportName;
	}

	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * @return the basicDatatable
	 */
	public PdfPTable getBasicDatatable() {
		return basicDatatable;
	}

	/**
	 * @param fileName the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	
	
	/**
	 * @return the trace
	 */
	public boolean isTrace() {
		return trace;
	}

	/**
	 * @param trace the trace to set
	 */
	public void setTrace(boolean trace) {
		this.trace = trace;
	}

	/**
	 * @return the basicDoc
	 */
	public Document getBasicDoc() {
		return basicDoc;
	}

	/**
	 * @param heading
	 * @param columns
	 * @param reportName
	 * @param fileName
	 */
	public ReportView(String heading, ArrayList<ReportColumnView> columns,
			String reportName, String fileName) {
		super();
		this.heading = heading;
		this.columns = columns;
		this.reportName = reportName;
		this.fileName = fileName;
		this.basicDatatable = new PdfPTable(columns.size());
	}

	public void initialize(){
		// Build a PDF document and set it up 
		// Note that the PDFTable was created in the constructor
		basicDoc = new Document(PageSize.LETTER.rotate());
		try {

			PdfWriter.getInstance(basicDoc, new FileOutputStream(fileName));

		} catch (DocumentException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		 int numColumns = columns.size();
		 
		 //PdfPTable basicDatatable = new PdfPTable(numColumns);
		 
		 try {

				// The header starts with a cell that spans all columns
				PdfPCell cell = new PdfPCell(new Phrase(heading, FontFactory.getFont(FontFactory.HELVETICA, 18, Font.BOLD)));
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell.setBorderWidth(2);
				cell.setColspan(numColumns);
				cell.setUseDescender(true);
				basicDatatable.addCell(cell);
				// Header stuff ends here

				int headerwidths[] = new int[numColumns]; // percentage
				basicDatatable.setWidthPercentage(100); // percentage
				basicDatatable.getDefaultCell().setPadding(3);
				basicDatatable.getDefaultCell().setBorderWidth(2);
				basicDatatable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
				
				Iterator<ReportColumnView> i = columns.iterator();
				int j = 0;
				while(i.hasNext()){
					ReportColumnView cv = (ReportColumnView) i.next();
					headerwidths[j]= cv.getPercentWidth();
					basicDatatable.addCell(cv.getTitle());
					j++;
				}
				basicDatatable.setWidths(headerwidths);
				basicDatatable.setHeaderRows(1); // this is the end of the table header
				basicDatatable.getDefaultCell().setBorderWidth(1);
				

			} catch (DocumentException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
	}
	
public int finalize(int rows){
		// We have some documents to print, maybe.  Test for a row count > 0
		int result = 0;
		if(rows>0){
			basicDoc.open();
			
			if(rows>0){
				try {

					
					basicDoc.add(this.basicDatatable);
					basicDoc.newPage();

				} catch (DocumentException e) {
					result = -1;
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		if(rows>0)
			basicDoc.close(); // Should flush the contents 
		
		return result;
		
	}

public void setGrayFill(float fill){
	// Convenience method for RPG wrapper
	this.basicDatatable.getDefaultCell().setGrayFill(fill);
	
}

public void addCell(String cellContent){
	// Convenience method for RPG wrapper
	this.basicDatatable.addCell(cellContent);
	
}

}
