/*
 * Copyright (c) 2004-2007 Auster Solutions. All Rights Reserved.
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
 * Created on 19/09/2007
 */
package br.com.auster.tim.om.invoice;

import br.com.auster.om.util.UnitCounter;

/**
 * This class handles each free unit reduction a call received
 *
 * @author framos
 * @version $Id$
 */
public class UsageFUDetail extends TIMUsageDetailItem {


	private String shortDescription;
	private String longDescription;
	private String accountID;
	private UnitCounter fuVolume;


	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object _other) { return (this.hashCode() - _other.hashCode());	}


	/**
	 * Getters and Setters
	 */

	public UsageFUDetail(TIMUsageDetail _detail) {
		super(_detail);
	}

	public final String getPackageShortDescription() { return this.shortDescription; }
	public final void setPackageShortDescription(String _shortDescription) { this.shortDescription = _shortDescription; }

	public final String getPackageLongDescription() { return this.longDescription; }
	public final void setPackageLongDescription(String _description) { this.longDescription = _description; }

	public final String getAccountID() { return this.accountID; }
	public final void setAccountID(String _accountID) { this.accountID = _accountID; }

	public final UnitCounter getReductionVolume() { return this.fuVolume; }
	public final void setReductionVolume(UnitCounter _counter) { this.fuVolume = _counter; }
}
