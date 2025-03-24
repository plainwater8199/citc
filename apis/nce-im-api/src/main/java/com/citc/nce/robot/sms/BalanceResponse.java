package com.citc.nce.robot.sms;

import java.io.Serializable;


/**
 * 余额数据
 * @author ping chen
 *
 */
public class BalanceResponse implements Serializable{
	
	private static final long serialVersionUID = 1L;

	private long balance;// 余额


	public BalanceResponse() {

	}

	public BalanceResponse(long balance) {
		this.balance = balance;
	}

	public long getBalance() {
		return balance;
	}

	public void setBalance(long balance) {
		this.balance = balance;
	}

}
