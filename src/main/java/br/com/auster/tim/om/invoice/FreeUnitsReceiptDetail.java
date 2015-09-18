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
 * Created on 01/08/2006
 */
package br.com.auster.tim.om.invoice;

import java.util.Date;

import br.com.auster.om.invoice.ReceiptDetail;


/**
 * @author framos
 * @version $Id$
 *
 */
public class FreeUnitsReceiptDetail extends ReceiptDetail {



	private String directoryNumber;
	private String contractNumber;
	
	private boolean shared;

	private String unitCarriedOver;
	private String unitUsed;
	private String unitUnused;
	private String accountID;

	private Date startDate;
	private String originalStartDate;
	private Date endDate;
	private String originalEndDate;


	public FreeUnitsReceiptDetail() {
		super();
	}

	public String getAccessNbr() {
		return this.directoryNumber;
	}

	public void setAccessNbr(String _number) {
		this.directoryNumber = _number;
	}

	public String getContractNumber() {
		return this.contractNumber;
	}

	public void setContractNumber(String _number) {
		this.contractNumber = _number;
	}

	public boolean isShared() {
		return shared;
	}
	
	public void setShared(boolean shared) {
		this.shared = shared;
	}
	
	public String getUnitCarriedOverCount() {
		return this.unitCarriedOver;
	}

	public void setUnitCarriedOverCount(String _units) {
		this.unitCarriedOver = _units;
	}

	public String getUnitUsedCount() {
		return this.unitUsed;
	}

	public void setUnitUsedCount(String _units) {
		this.unitUsed = _units;
	}

	public String getUnitUnusedCount() {
		return this.unitUnused;
	}

	public void setUnitUnusedCount(String _units) {
		this.unitUnused = _units;
	}

	public Date getStartDate() {
		return this.startDate;
	}

	public void setStartDate(Date _date) {
		this.startDate = _date;
	}

	/**
	 *   This attributes handles the unformatted version of the
	 * 	{@link #startDate}
	 */
	public String getOriginalStartDate() {
		return originalStartDate;
	}
	public void setOriginalStartDate(String startDate) {
		this.originalStartDate = startDate;
	}

	public Date getEndDate() {
		return this.endDate;
	}

	public void setEndDate(Date _date) {
		this.endDate = _date;
	}

	/**
	 *   This attributes handles the unformatted version of the
	 * 	{@link #endDate}
	 */
	public String getOriginalEndDate() {
		return originalEndDate;
	}
	public void setOriginalEndDate(String endDate) {
		this.originalEndDate = endDate;
	}

	public String getAccountID() { return this.accountID; }
	public void setAccountID(String _accountID) { this.accountID = _accountID; }
}
