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
 * Created on 06/12/2006
 */
package br.com.auster.tim.om.invoice;

import java.lang.ref.WeakReference;
import java.util.BitSet;

import br.com.auster.om.invoice.InvoiceModelObject;

/**
 * @author framos
 * @version $Id$
 *
 */
// TODO remove the OM-Invoice interface once the customization of assertion workers is finished
//TODO Analyze if it should be made final. Final will disallow Rule engine to create shadow facts for
// this objects
public class TIMUsageGuidingFlag extends InvoiceModelObject {

	
	public static final int SERVICE_EXISTS = 0;
	public static final int PLAN_EXISTS = 1;
	public static final int TARIFFZONE_EXISTS = 2;
	public static final int USAGEGROUP_EXISTS = 3;
	public static final int RATEZONE_EXISTS = 4;
	public static final int TARIFFZONE_ALLOWED_IN_USAGEGROUP = 5;
	public static final int PLAN_CONTAINS_SERVICE = 6;
	public static final int IS_EXPIRED = 7;
	public static final int IS_LATE = 8;
	public static final int IS_COBILLING = 9;
	public static final int TARIFFZONE_ALLOWED_IN_LD_USAGEGROUP = 10;
	
	public static final int BITSET_SIZE = 11;
	
	
	
	protected WeakReference usage;
	protected BitSet flags; 
	
	public TIMUsageGuidingFlag() {		
		//For Dynamic Proxy Creation.
	}
	
	public TIMUsageGuidingFlag(TIMUsageDetail _detail) {
		this.usage = new WeakReference(_detail);
		this.flags = new BitSet(BITSET_SIZE);
	}
	
	
	public final boolean getFlag(int _flagIdx) {
		return this.flags.get(_flagIdx);
	}
	public final void setFlag(int _flagIdx, boolean _enabled) {
		this.flags.clear(_flagIdx);
		if (_enabled) {
			this.flags.set(_flagIdx);
		}
	}

	public TIMUsageDetail getRelatedUsage() {
		return (TIMUsageDetail)this.usage.get();
	}
	
	
	
	//
	// helper methods
	//
	
	public final void setServiceExists(boolean _enabled) {
		this.setFlag(SERVICE_EXISTS, _enabled);
	}
	public final boolean isServiceExists() {
		return this.getFlag(SERVICE_EXISTS);
	}
	
	public final void setRateplanExists(boolean _enabled) {
		this.setFlag(PLAN_EXISTS, _enabled);
	}
	public final boolean isRateplanExists() {
		return this.getFlag(PLAN_EXISTS);
	}
	
	public final void setTariffZoneExists(boolean _enabled) {
		this.setFlag(TARIFFZONE_EXISTS, _enabled);
	}
	public final boolean isTariffZoneExists() {
		return this.getFlag(TARIFFZONE_EXISTS);
	}
	
	public final void setUsageGroupExists(boolean _enabled) {
		this.setFlag(USAGEGROUP_EXISTS, _enabled);
	}
	public final boolean isUsageGroupExists() {
		return this.getFlag(USAGEGROUP_EXISTS);
	}

	public final void setRateZoneExists(boolean _enabled) {
		this.setFlag(RATEZONE_EXISTS, _enabled);
	}
	public final boolean isRateZoneExists() {
		return this.getFlag(RATEZONE_EXISTS);
	}
	
	public final void setTariffZoneAllowedInUsageGroup(boolean _enabled) {
		this.setFlag(TARIFFZONE_ALLOWED_IN_USAGEGROUP, _enabled);
	}
	public final boolean isTariffZoneAllowedInUsageGroup() {
		return this.getFlag(TARIFFZONE_ALLOWED_IN_USAGEGROUP);
	}

	public final void setTariffZoneAllowedInLDUsageGroup(boolean _enabled) {
		this.setFlag(TARIFFZONE_ALLOWED_IN_LD_USAGEGROUP, _enabled);
	}
	public final boolean isTariffZoneAllowedInLDUsageGroup() {
		return this.getFlag(TARIFFZONE_ALLOWED_IN_LD_USAGEGROUP);
	}
	
	public final void setPlanContainsService(boolean _enabled) {
		this.setFlag(PLAN_CONTAINS_SERVICE, _enabled);
	}
	public final boolean isPlanContainsService() {
		return this.getFlag(PLAN_CONTAINS_SERVICE);
	}
  
	public final void setExpired(boolean _enabled) {
		this.setFlag(IS_EXPIRED, _enabled);
	}

	public final boolean isExpired() {
		return this.getFlag(IS_EXPIRED);
	}

	public final void setLate(boolean _enabled) {
		this.setFlag(IS_LATE, _enabled);
	}

	public final boolean isLate() {
		return this.getFlag(IS_LATE);
	}

	public final void setCobilling(boolean _enabled) {
		this.setFlag(IS_COBILLING, _enabled);
	}

	public final boolean isCobilling() {
		return this.getFlag(IS_COBILLING);
	}

	// nothing to do here
	public int compareTo(Object _other) {
		return 1;
	}
}
