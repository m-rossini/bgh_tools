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
 * Created on 14/08/2006
 */
package br.com.auster.tim.om.invoice;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import br.com.auster.om.invoice.ChargedTax;
import br.com.auster.om.invoice.UsageDetail;
import br.com.auster.om.util.UnitCounter;


/**
 * @author framos
 * @version $Id$
 *
 */
public class TIMUsageDetail extends UsageDetail {


	private static final int UNKOWN_USAGE = -1;
	private static final int DATA_USAGE = 0;
	private static final int VOICE_USAGE = 1;
	private static final int EVENT_USAGE = 2;

	private static final int UNKOWN_AREA = -1;
	private static final int INVALID_AREA = 0;
	private static final int LOCAL_AREA = 1;
	private static final int LDN_AREA = 2;
	private static final int LDI_AREA = 3;


	private int usageTimeInSeconds;
	private Date usageTime;
	private String originalUsageTime;
	private Date usageDate;
	private String originalUsageDate;
	private UnitCounter roundedDuration;
	private double discounts;
	private double fuDiscounts;
	private String fuIndicator;
	private double callIdNumber;
	private String typeOfNumber;
	private List taxes;
	private int typeOfUsage;
	private int areaIndicator;
	private String serviceDesc;
	private String microCellDesc;
	private String microCellScalingFactor;
	private double amountBeforePromotions;
	
	private boolean onnet;


	// Variables to be lazy populates
	private String originAreaCode = null;
	private String destinationAreaCode = null;

	private TIMUsageGuidingFlag flags;

	/**
	 * each free unit part for this usage event
	 */
	private List<UsageFUDetail> freeUnitsDetails;
	/**
	 * each promotion part for this usage event
	 */
	private List<UsagePromotionDetail> promotionsDetails;




	public TIMUsageDetail() {
		super();
		this.flags = new TIMUsageGuidingFlag(this);
		this.taxes = new LinkedList();
		this.freeUnitsDetails = new LinkedList<UsageFUDetail>();
		this.promotionsDetails = new LinkedList<UsagePromotionDetail>();

		this.typeOfUsage = UNKOWN_USAGE;
		this.areaIndicator = UNKOWN_AREA;
	}


	public void setGuidingFlags(TIMUsageGuidingFlag _flags) {
		this.flags = _flags;
	}
	public TIMUsageGuidingFlag getGuidingFlags() {
		return this.flags;
	}


	public double getDiscountAmount() {
		return discounts;
	}
	public void setDiscountAmount(double discounts) {
		this.discounts = discounts;
	}

	public double getFreeUnitDiscountAmount() {
		return fuDiscounts;
	}
	public void setFreeUnitDiscountAmount(double fuDiscounts) {
		this.fuDiscounts = fuDiscounts;
	}

	public String getFreeUnitIndicator() {
		return fuIndicator;
	}
	public void setFreeUnitIndicator(String fuIndicator) {
		this.fuIndicator = fuIndicator;
	}

	public UnitCounter getRoundedDuration() {
		return roundedDuration;
	}
	public void setRoundedDuration(UnitCounter roundedDuration) {
		this.roundedDuration = roundedDuration;
	}

	public Date getUsageDate() {
		return usageDate;
	}
	public void setUsageDate(Date usageDate) {
		this.usageDate = usageDate;
	}

	public final String getServiceDescription() {
		return this.serviceDesc;
	}
	public final void setServiceDescription(String _desc) {
		this.serviceDesc = _desc;
	}

	/**
	 *   This attributes handles the unformatted version of the
	 * 	{@link #usageDate}
	 */
	public String getOriginalUsageDate() {
		return originalUsageDate;
	}
	public void setOriginalUsageDate(String usageDate) {
		this.originalUsageDate = usageDate;
	}

  public int getUsageTimeInSeconds() {
    return this.usageTimeInSeconds;
  }
	public Date getUsageTime() {
		return this.usageTime;
	}
	public void setUsageTime(Date usageTime) {
		this.usageTime = usageTime;
    int sec = 0;
    if (usageTime != null) {
      Calendar cal = Calendar.getInstance();
      cal.setTime(usageTime);
      sec = cal.get(Calendar.HOUR_OF_DAY) * 3600;
      sec += cal.get(Calendar.MINUTE) * 60;
      sec += cal.get(Calendar.SECOND);
    }
    this.usageTimeInSeconds = sec;
	}

	/**
	 *   This attributes handles the unformatted version of the
	 * 	{@link #usageTime}
	 */
	public String getOriginalUsageTime() {
		return originalUsageTime;
	}
	public void setOriginalUsageTime(String usageTime) {
		this.originalUsageTime = usageTime;
	}

	public void addTax(ChargedTax tax) {
		this.taxes.add(tax);
	}

	public void setTaxes(List _taxes) {
		this.taxes = _taxes;
	}
	public List getTaxes() {
		return this.taxes;
	}

	public Date getDatetime() {
		Calendar c = Calendar.getInstance();
		if (this.usageDate != null) {
			c.setTime(this.usageDate);
		}
		if (this.usageTime != null) {
			Calendar c2 = Calendar.getInstance();
			c2.setTime(this.usageTime);
			c.add(Calendar.HOUR_OF_DAY, c2.get(Calendar.HOUR_OF_DAY));
			c.add(Calendar.MINUTE, c2.get(Calendar.MINUTE));
			c.add(Calendar.SECOND, c2.get(Calendar.SECOND));
		}
		return c.getTime();
	}



	public double getCallIdNumber() {
		return callIdNumber;
	}

	public void setCallIdNumber(double callIdNumber) {
		this.callIdNumber = callIdNumber;
	}

	public String toString() {
		return "Calling[" + this.getChannelId() + "]"
		+ ".Called[" + this.getCalledNumber() + "]"
		+ ".Amount[" + this.getTotalAmount() + "]"
		+ ".Date[" + this.getUsageDate() + "]"
		+ ".Time[" + this.getUsageTime() + "]"
		+ ".Duration[" + this.getUsageDuration() + "]"
		+ ".EndDateTime[" + this.getUsageEndDatetime() + "]"
		;
	}


	public String getDestinationAreaCode() {
		return this.destinationAreaCode;
	}
	public void setDestinationAreaCode(String destinationAreaCode) {
		this.destinationAreaCode = destinationAreaCode;
	}

	public String getOriginAreaCode() {
		return this.originAreaCode;
	}
	public void setOriginAreaCode(String originAreaCode) {
		this.originAreaCode = originAreaCode;
	}

	public String getTypeOfNumber() {
		return this.typeOfNumber;
	}
	public void setTypeOfNumber(String typeOfNumber) {
		this.typeOfNumber = typeOfNumber;
	}

	public boolean isDataUsage() {
		return (this.typeOfUsage == DATA_USAGE);
	}
	public void markAsDataUsage() {
		this.typeOfUsage = DATA_USAGE;
	}

	public boolean isVoiceUsage() {
		return (this.typeOfUsage == VOICE_USAGE);
	}
	public void markAsVoiceUsage() {
		this.typeOfUsage = VOICE_USAGE;
	}

	public boolean isEventUsage() {
		return (this.typeOfUsage == EVENT_USAGE);
	}
	public void markAsEventUsage() {
		this.typeOfUsage = EVENT_USAGE;
	}

	// flags for LOCAL, LDN and LDI
	public boolean isLocalCall() {
		if (this.areaIndicator == UNKOWN_AREA) {
			this.areaIndicator = calculateArea();
		}
		return (this.areaIndicator == LOCAL_AREA);
	}

	public boolean isLDNCall() {
		if (this.areaIndicator == UNKOWN_AREA) {
			this.areaIndicator = calculateArea();
		}
		return (this.areaIndicator == LDN_AREA);
	}

	public boolean isLDICall() {
		if (this.areaIndicator == UNKOWN_AREA) {
			this.areaIndicator = calculateArea();
		}
		return (this.areaIndicator == LDI_AREA);
	}

	public final String getSectionName() {
		if (this.getSection() != null) {
			return ((ContractUsageGroupSubsection) this.getSection()).getSectionName();
		}
		return null;
	}

	public final String getContractNumber() {
		if ((this.getSection() != null) && (this.getSection().getParentSection() != null)) {
			return ((ContractInfoSection)this.getSection().getParentSection()).getContractNumber();
		}
		return null;
	}

	public final String getRatePlan() {
		if ((this.getSection() != null) && (this.getSection().getParentSection() != null)) {
			return ((ContractInfoSection)this.getSection().getParentSection()).getRatePlan();
		}
		return null;
	}
	
	public final List<UsagePromotionDetail> getPromotionDetails() {
		return this.promotionsDetails;
	}

	public final List<UsageFUDetail> getFreeUnitDetails() {
		return this.freeUnitsDetails;
	}

	/**
	 * Defaults to TRUE if usage does not have a parent section
	 */
	public boolean isMobile() {
		if (this.getSection() != null) {
			return this.getSection().isMobile();
		}
		return true;
	}

	private int calculateArea() {
		// both NOT NULL == LOCAL or LDN call
		if ((this.originAreaCode != null) && (this.destinationAreaCode != null)) {
			if (this.originAreaCode.equals(this.destinationAreaCode)) {
				return LOCAL_AREA;
			} else {
				return LDN_AREA;
			}
		// only 1 NULL == LDI
		} else if ((this.originAreaCode != null) || (this.destinationAreaCode != null)) {
			return LDI_AREA;
		}
		// both NULL == INVALID
		return INVALID_AREA;
	}

	/**
	 * Getter for the amountBeforePromotions.
	 *
	 * @return amountBeforePromotions
	 */
	public double getAmountBeforePromotions() {
		return amountBeforePromotions;
	}

	/**
	 * Setter for the amountBeforePromotions.
	 *
	 * @param amountBeforePromotions
	 */
	public void setAmountBeforePromotions(double amountBeforePromotions) {
		this.amountBeforePromotions = amountBeforePromotions;
	}

	/**
	 * Getter for the microCellDesc.
	 *
	 * @return
	 */
	public String getMicroCellDesc() {
		return microCellDesc;
	}

	/**
	 * Setter for the microCellDesc.
	 *
	 * @param microCellDesc
	 */
	public void setMicroCellDesc(String microCellDesc) {
		this.microCellDesc = microCellDesc;
	}

	/**
	 * Getter for the microCellScalingFactor.
	 *
	 * @return
	 */
	public String getMicroCellScalingFactor() {
		return microCellScalingFactor;
	}

	/**
	 * Setter for the microCellScalingFactor.
	 *
	 * @param microCellScalingFactor
	 */
	public void setMicroCellScalingFactor(String microCellScalingFactor) {
		this.microCellScalingFactor = microCellScalingFactor;
	}
	
	public void setOnNet(boolean _onnet) {
		this.onnet = _onnet;
	}
	
	public boolean isOnNet() {
		return this.onnet;
	}
}

