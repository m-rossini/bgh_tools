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

import br.com.auster.om.invoice.ReceiptDetail;


/**
 * @author framos
 * @version $Id$
 *
 */
public class ServiceReceiptDetail extends ReceiptDetail {

	
	private double proRateFactor;
	private int noOfDays;
	private String directoryNumber;
	private String contractNumber;
	private String anatelCode;
	private String packageShortname;
	private String rateplanShdes;
	private boolean subscription;
	
	
	public ServiceReceiptDetail() {
		super();
		this.subscription = false;
	}
	
	public int getNumberOfDays() {
		return this.noOfDays;
	}
	
	public void setNumberOfDays(int _noOfDays) {
		this.noOfDays = _noOfDays;
	}
	
	public double getProRateFactor() {
		return this.proRateFactor;
	}
	
	public void setProRateFactor(double _rateFactor) {
		this.proRateFactor = _rateFactor;
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
	
	public boolean isSubscription() {
		return this.subscription;
	}
	
	public void setSubscription(boolean _flag) {
		this.subscription = _flag;
	}
	
	public String getAnatelCode() {
		return this.anatelCode;
	}
	
	public void setAnatelCode(String _code) {
		this.anatelCode = _code;
	}
	
	public String getPackageShortName() {
		return this.packageShortname;
	}
	
	public void setPackageShortName(String _code) {
		this.packageShortname = _code;
	}
	
	public String getRateplanShdes() {
		return this.rateplanShdes;
	}
	
	public void setRateplanShdes(String _code) {
		this.rateplanShdes = _code;
	}
		
}
