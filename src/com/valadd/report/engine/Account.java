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
package com.valadd.report.engine;

import java.math.BigDecimal;
/**
 * @author Pete
 *
 */
public class Account {

	/**
	 * 
	 */
	String type;
	String account;
	private BigDecimal balance;
	
	public Account(String type, String account) {
		// TODO Auto-generated constructor stub
		
		this.type = type;
		this.account = account;
		this.balance = new BigDecimal("0");
	}

	public boolean addMoney(BigDecimal deposit){
		boolean success = false;
		
		this.balance.add(deposit);
		
		return success;
		
	}
	
	public boolean takeMoney(BigDecimal withdrawal){
		boolean success = false;
		
		this.balance.subtract(withdrawal);
		
		return success;
		
	}
	
	public BigDecimal getBalance(String type, String number){
		
		return this.balance;
		
	}
}
