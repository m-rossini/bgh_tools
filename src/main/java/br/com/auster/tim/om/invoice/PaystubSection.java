/*
 * Copyright (c) 2004-2006 Auster Solutions. All Rights Reserved.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * Created on 15/08/2006
 */
package br.com.auster.tim.om.invoice;

import java.util.Date;

import br.com.auster.om.invoice.Address;
import br.com.auster.om.invoice.BarCode;
import br.com.auster.om.invoice.Section;

/**
 * @author framos
 * @version $Id$
 *
 */
public class PaystubSection extends Section {
	
	private Address carrierAddress;
	private String carrierName;
	private String customerCode;
	private String customerName;
	private Date cycleStartDate;
	private String originalCycleStartDate;
	private Date cycleEndDate;
	private String originalCycleEndDate;
	private Date issueDate;
	private String originalIssueDate;
	private Date dueDate;
	private String originalDueDate;
	
	private String directDebitInd;
	private String directDebitInfo;
	
	private double previousBalance;
	private double finalBalance;
	private String originalTotalAmount;
	private String originalFinalBalance;
	
	
	
	
	public Address getCarrierAddress() {
		return carrierAddress;
	}
	public void setCarrierAddress(Address carrierAddress) {
		this.carrierAddress = carrierAddress;
	}
	
	public String getCustomerCode() {
		return customerCode;
	}
	public void setCustomerCode(String customerCode) {
		this.customerCode = customerCode;
	}
	
	public Date getCycleEndDate() {
		return cycleEndDate;
	}
	public void setCycleEndDate(Date cycleEndDate) {
		this.cycleEndDate = cycleEndDate;
	}
	
	/**
	 *   This attributes handles the unformatted version of the
	 * 	{@link #cycleEndDate} 
	 */
	public String getOriginalCycleEndDate() {
		return originalCycleEndDate;
	}
	public void setOriginalCycleEndDate(String endDate) {
		this.originalCycleEndDate = endDate;
	}
	
	public Date getCycleStartDate() {
		return cycleStartDate;
	}
	public void setCycleStartDate(Date cycleStartDate) {
		this.cycleStartDate = cycleStartDate;
	}
	
	/**
	 *   This attributes handles the unformatted version of the
	 * 	{@link #cycleStartDate} 
	 */
	public String getOriginalCycleStartDate() {
		return originalCycleStartDate;
	}
	public void setOriginalCycleStartDate(String CycleStartDate) {
		this.originalCycleStartDate = CycleStartDate;
	}
	
	public Date getDueDate() {
		return dueDate;
	}
	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}
	
	/**
	 *   This attributes handles the unformatted version of the
	 * 	{@link #dueDate} 
	 */
	public String getOriginalDueDate() {
		return originalDueDate;
	}
	public void setOriginalDueDate(String dueDate) {
		this.originalDueDate = dueDate;
	}
	
	public double getFinalBalance() {
		return finalBalance;
	}
	public void setFinalBalance(double finalBalance) {
		this.finalBalance = finalBalance;
	}
	
	public String getOriginalFinalBalance() {
		return originalFinalBalance;
	}
	
	public void setOriginalFinalBalance(String _value) {
		this.originalFinalBalance = _value;
	}

	public String getOriginalTotalAmount() {
		return originalTotalAmount;
	}
	
	public void setOriginalTotalAmount(String _value) {
		this.originalTotalAmount = _value;
	}
	
	public Date getIssueDate() {
		return issueDate;
	}
	public void setIssueDate(Date issueDate) {
		this.issueDate = issueDate;
	}
	
	/**
	 *   This attributes handles the unformatted version of the
	 * 	{@link #issueDate} 
	 */
	public String getOriginalIssueDate() {
		return originalIssueDate;
	}
	public void setOriginalIssueDate(String issueDate) {
		this.originalIssueDate = issueDate;
	}
	
	public double getPreviousBalance() {
		return previousBalance;
	}
	public void setPreviousBalance(double previousBalance) {
		this.previousBalance = previousBalance;
	}
	
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	
	public BarCode getBarCode() {
		return this.getInvoice().getBarCode();
	}

	public String getCarrierName() {
		return carrierName;
	}
	public void setCarrierName(String carrierName) {
		this.carrierName = carrierName;
	}
	
	public String getDirectDebitIndicator() {
		return this.directDebitInd;
	}
	public void setDirectDebitIndicator(String _ind) {
		this.directDebitInd = _ind;
	}
	
	public String getDirectDebitInfo() {
		return this.directDebitInfo;
	}
	public void setDirectDebitInfo(String _info) {
		this.directDebitInfo = _info;
	}
}
