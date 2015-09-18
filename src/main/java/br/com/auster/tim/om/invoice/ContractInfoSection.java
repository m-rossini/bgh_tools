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
 * Created on 11/08/2006
 */
package br.com.auster.tim.om.invoice;

import java.util.Comparator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import br.com.auster.om.invoice.Section;

/**
 * @author framos
 * @version $Id$
 *
 */
public class ContractInfoSection extends Section {


	private static final long serialVersionUID = 1L;

	private String contractNumber;
	private String rateplan;
	private String userName;
	private String anatelCode;
	private String rateplanShdes;

	private SortedSet<TIMUsageDetail> contractCalls;



	public ContractInfoSection() {
		super();
		this.contractCalls = new TreeSet<TIMUsageDetail>(new TIMUsageDetailComparatorByDatetime());
	}


	public String getRatePlan() {
		return rateplan;
	}
	public void setRatePlan(String rateplan) {
		this.rateplan = rateplan;
	}

	public String getContractNumber() {
		return contractNumber;
	}
	public void setContractNumber(String contractNumber) {
		this.contractNumber = contractNumber;
	}

	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getAnatelCode() {
		return this.anatelCode;
	}
	
	public void setAnatelCode(String _code) {
		this.anatelCode = _code;
	}

	public String getRateplanShdes() {
		return this.rateplanShdes;
	}
	
	public void setRateplanShdes(String _code) {
		this.rateplanShdes = _code;
	}
	

	public void addContractCall(TIMUsageDetail _detail) {
		this.contractCalls.add(_detail);
	}

	/**
	 * IMPORTANT: This method should never change to a get-like naming
	 * 			or it will duplicate asserted usage details into working
	 * 			memory. This is a huge performance bottleneck, increasing
	 * 			assert() time more than two times.
	 */
	public Set<TIMUsageDetail> returnContractCalls() {
//		Collections.sort(this.contractCalls, );
		return this.contractCalls;
	}



	class TIMUsageDetailComparatorByDatetime implements Comparator<TIMUsageDetail> {

		public int compare(TIMUsageDetail _first, TIMUsageDetail _second) {
			if ((_first != null) && (_second != null) && (_first.getDatetime() != null)) {
				return _first.getDatetime().compareTo(_second.getDatetime());
			}
			return 0;
		}

	}
}
