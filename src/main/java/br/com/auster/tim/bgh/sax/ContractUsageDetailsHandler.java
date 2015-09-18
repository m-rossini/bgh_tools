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
package br.com.auster.tim.bgh.sax;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import br.com.auster.dware.sax.MultiHandlerReceiverBase;
import br.com.auster.om.invoice.ChargedTax;
import br.com.auster.om.invoice.Section;
import br.com.auster.om.invoice.UsageDetail;
import br.com.auster.om.util.ParserUtils;
import br.com.auster.tim.om.invoice.ContractInfoSection;
import br.com.auster.tim.om.invoice.TIMUsageDetail;
import br.com.auster.tim.om.invoice.UsageFUDetail;
import br.com.auster.tim.om.invoice.UsagePromotionDetail;

/**
 * @author framos
 * @version $Id$
 *
 */
public class ContractUsageDetailsHandler extends MultiHandlerReceiverBase {

	// -------------------
	// Class constants
	// -------------------

	private static final Logger	log	 = Logger.getLogger(ContractUsageDetailsHandler.class);

	public static final String	BGH_INVOICE_USAGE_ELEMENT	         = "OneCall";
	public static final String	BGH_INVOICE_USAGE_CALLTIME	       = "callTime";
	public static final String	BGH_INVOICE_USAGE_CALLDATE	       = "callDate";
	public static final String	BGH_INVOICE_USAGE_ORIGIN	         = "callOrigin";
	public static final String	BGH_INVOICE_USAGE_DESTINATION	     = "callDestination";
	public static final String	BGH_INVOICE_USAGE_CALLEDNUMBER	   = "destinationNumber";
	public static final String	BGH_INVOICE_USAGE_TARIFFINDICATOR	 = "tariffTimeIndicator";
	public static final String	BGH_INVOICE_USAGE_TYPEOFCALL	     = "typeOfCall";
	public static final String	BGH_INVOICE_USAGE_SVCDESCRIPTION	 = "serviceShortDescr";
	public static final String	BGH_INVOICE_USAGE_ACTUAL_DURATION	 = "callDuration";
	public static final String	BGH_INVOICE_USAGE_ROUNDED_DURATION	= "callDurationRounded";
	public static final String	BGH_INVOICE_USAGE_TOTALAMOUNT	     = "amountAfterPromotionsDisc";
	public static final String	BGH_INVOICE_USAGE_NETWORKIND	     = "networkIndicator";
	public static final String	BGH_INVOICE_USAGE_TARIFFZONE	     = "tariffZoneShortDescr";
	public static final String	BGH_INVOICE_USAGE_TYPEOFNUMBER	   = "typeOfNumber";
	public static final String	BGH_INVOICE_USAGE_FUINDICATOR	     = "fuIndicator";
	public static final String	BGH_INVOICE_USAGE_FUDISCOUNT	     = "freeUnitsDiscountAmount";
	public static final String	BGH_INVOICE_USAGE_DISCOUNT	       = "promotionsDiscAmount";
	public static final String	BGH_INVOICE_USAGE_CARRIERCODE	     = "cspBGH";
	public static final String	BGH_INVOICE_USAGE_CALL_ID_NUMBER	 = "ldcCallIdentificationNumber";
	public static final String BGH_INVOICE_USAGE_AMOUNT_BEFORE_PROMOTIONS = "amountBeforePromotionsDisc";
	public static final String BGH_INVOICE_USAGE_MICRO_CELL_DESCRIPTION = "microCellShortDescr";
	public static final String BGH_INVOICE_USAGE_MICRO_CELL_SCALING_FACTOR = "microCellScalingFactor";

	public static final String BGH_INVOICE_USAGE_PROMOTION_ELEMENT = "PromotionPackage";
	public static final String BGH_INVOICE_USAGE_PROMOTION_TYPE = "packageType";
	public static final String BGH_INVOICE_USAGE_PROMOTION_PACKAGE = "packageDescShort";
	public static final String BGH_INVOICE_USAGE_PROMOTION_MODEL = "packageDescLong";
	public static final String BGH_INVOICE_USAGE_PROMOTION_AMOUNT = "packageAmount";

	public static final String BGH_INVOICE_USAGE_FU_ELEMENT = "FUDetail";
	public static final String BGH_INVOICE_USAGE_FU_ACCOUNT_ID = "fuAccountID";
	public static final String BGH_INVOICE_USAGE_FU_SHDES = "fuPackageDescShort";
	public static final String BGH_INVOICE_USAGE_FU_LONGDES = "fuPackageDescLong";
	public static final String BGH_INVOICE_USAGE_FU_REDUCT_TIME = "roundedCallTimeReduction";


	public static final String	BGH_INVOICE_USAGE_TAX_ELEMENT	     = "TaxesOneCall";
	public static final String	BGH_INVOICE_USAGE_TAX_NAME	       = "taxName";
	public static final String	BGH_INVOICE_USAGE_TAX_RATE	       = "taxRate";

	public static final String	OM_CURRENT_USAGE_EVENT	           = "invoice.contract.usage.current";

	// -------------------
	// Public methods
	// -------------------

	public void startElement(String _namespace, String _localName, String _qName, Attributes _attributes) throws SAXException {

		if (BGH_INVOICE_USAGE_ELEMENT.equals(_localName)) {
			// must have current ContractInfo
			if (!this.context.hasAttribute(ContractUsageGroupHandler.OM_CURRENT_CONTRACT_USAGEGROUP)) {
				throw new SAXException("Cannot handle Usage without current UsageGroup section");
			}
			Section usageGroup = (Section) this.context.getAttribute(ContractUsageGroupHandler.OM_CURRENT_CONTRACT_USAGEGROUP);
			// must also have a current ContractInfo, from where we will get the
			// callingNumber (channelID)
			if (!this.context.hasAttribute(InvoiceContractDetailsHandler.OM_CURRENT_CONTRACT_INFO_SECTION)) {
				throw new SAXException("Cannot handle Usage without current ContractInfo section");
			}
			Section contractInfo = (Section) this.context.getAttribute(InvoiceContractDetailsHandler.OM_CURRENT_CONTRACT_INFO_SECTION);
			// handles usage
			TIMUsageDetail detail = new TIMUsageDetail();
			detail.setTag(_attributes.getValue(TIMOMLoader.BGH_ELEMENT_KEY));
			detail.setUsageDate(BGHDataFormats.getDate(_attributes.getValue(BGH_INVOICE_USAGE_CALLDATE)));
			detail.setOriginalUsageDate(_attributes.getValue(BGH_INVOICE_USAGE_CALLDATE));
			detail.setUsageTime(BGHDataFormats.getTime(_attributes.getValue(BGH_INVOICE_USAGE_CALLTIME)));
			detail.setOriginalUsageTime(_attributes.getValue(BGH_INVOICE_USAGE_CALLTIME));

			// Lets Handle Origin and Destination
			detail.setDestinationCity(_attributes.getValue(BGH_INVOICE_USAGE_DESTINATION));
			detail.setOriginCity(_attributes.getValue(BGH_INVOICE_USAGE_ORIGIN));
//			if (isVoiceDestination(detail.getDestinationCity())) {
				setStateAndArea(detail);
//			}

			detail.setCalledNumber(_attributes.getValue(BGH_INVOICE_USAGE_CALLEDNUMBER));
			detail.setTariff(_attributes.getValue(BGH_INVOICE_USAGE_TARIFFZONE));
			detail.setTariffClass(_attributes.getValue(BGH_INVOICE_USAGE_TARIFFINDICATOR));
			detail.setType(_attributes.getValue(BGH_INVOICE_USAGE_TYPEOFCALL));
			detail.setSvcId(_attributes.getValue(BGH_INVOICE_USAGE_SVCDESCRIPTION));
			detail.setUsageDuration(BGHDataFormats.buildUnitCounter(_attributes.getValue(BGH_INVOICE_USAGE_ACTUAL_DURATION)));
			detail.setOriginalUsageDuration(_attributes.getValue(BGH_INVOICE_USAGE_ACTUAL_DURATION));
			detail.setRoundedDuration(BGHDataFormats.buildUnitCounter(_attributes.getValue(BGH_INVOICE_USAGE_ROUNDED_DURATION)));
			detail.setArea(_attributes.getValue(BGH_INVOICE_USAGE_NETWORKIND));
			detail.setCallClass("N/C");
			detail.setCallSubclass("N/C");
			detail.setTypeOfNumber(_attributes.getValue(BGH_INVOICE_USAGE_TYPEOFNUMBER));
			detail.setFreeUnitIndicator(_attributes.getValue(BGH_INVOICE_USAGE_FUINDICATOR));
			detail.setTotalAmount(ParserUtils.getDouble(_attributes.getValue(BGH_INVOICE_USAGE_TOTALAMOUNT)));
			detail.setOriginalTotalAmount(_attributes.getValue(BGH_INVOICE_USAGE_TOTALAMOUNT));
			detail.setFreeUnitDiscountAmount(ParserUtils.getDouble(_attributes.getValue(BGH_INVOICE_USAGE_FUDISCOUNT)));
			detail.setDiscountAmount(ParserUtils.getDouble(_attributes.getValue(BGH_INVOICE_USAGE_DISCOUNT)));
			detail.setChannelId(contractInfo.getAccessNbr());
			detail.setAmountBeforePromotions(ParserUtils.getDouble(_attributes.getValue(BGH_INVOICE_USAGE_AMOUNT_BEFORE_PROMOTIONS)));
			detail.setMicroCellDesc(_attributes.getValue(BGH_INVOICE_USAGE_MICRO_CELL_DESCRIPTION));
			detail.setMicroCellScalingFactor(_attributes.getValue(BGH_INVOICE_USAGE_MICRO_CELL_SCALING_FACTOR));
			detail.setCarrierCode(_attributes.getValue(BGH_INVOICE_USAGE_CARRIERCODE));
//			// TODO WHY CAN WE HAVE CSPBGH CODES > 100 ????
//			if ((detail.getCarrierCode() != null) && (detail.getCarrierCode().length() > 2)) {
//				detail.setCarrierCode(TIMOMLoader.TIM_CARRIERCODE);
//			}
			detail.setCallIdNumber(ParserUtils.getDouble(_attributes.getValue(BGH_INVOICE_USAGE_CALL_ID_NUMBER)));
			// saving sequence of events
			if (this.context.hasAttribute(OM_CURRENT_USAGE_EVENT)) {
				UsageDetail dt = (UsageDetail) this.context.getAttribute(OM_CURRENT_USAGE_EVENT);
				dt.setNextUsage(detail);
			}
			// save new UsageDetail to context and current UsageGroup
			this.context.setAttribute(OM_CURRENT_USAGE_EVENT, detail);
			usageGroup.addDetail(detail);
			// now adding to contract so it can access ALL its calls from a single place
			((ContractInfoSection)contractInfo).addContractCall(detail);
			// debugging attributes
			log.debug("Found usage event callDate=" + detail.getUsageDate());
			log.debug("Found usage event callTime=" + detail.getUsageTime());
			log.debug("Found usage event origin=" + detail.getOriginCity());
			log.debug("Found usage event destination=" + detail.getDestinationCity());
			log.debug("Found usage event calledNumber=" + detail.getCalledNumber());
			log.debug("Found usage event tariff=" + detail.getTariff());
			log.debug("Found usage event tariffClass=" + detail.getTariffClass());
			log.debug("Found usage event type=" + detail.getType());
			log.debug("Found usage event serviceId=" + detail.getSvcId());
			log.debug("Found usage event actualDuration=" + detail.getUsageDuration());
			log.debug("Found usage event roundedDuration=" + detail.getRoundedDuration());
			log.debug("Found usage event area=" + detail.getArea());
			log.debug("Found usage event class=" + detail.getCallClass());
			log.debug("Found usage event freeUnit?=" + detail.getFreeUnitIndicator());
			log.debug("Found usage event totalAmount=" + detail.getTotalAmount());
			log.debug("Found usage event totalAmount(Original)=" + detail.getOriginalTotalAmount());
			log.debug("Found usage event fuDiscount=" + detail.getFreeUnitDiscountAmount());
			log.debug("Found usage event discount=" + detail.getDiscountAmount());
			log.debug("Found usage event carrier=" + detail.getCarrierCode());
			log.debug("Found usage event CallID#=" + detail.getCallIdNumber());

		} else if (BGH_INVOICE_USAGE_TAX_ELEMENT.equals(_localName)) {
			// must have current TIMUsageDetail
			if (!this.context.hasAttribute(OM_CURRENT_USAGE_EVENT)) {
				throw new SAXException("Cannot handle UsageDetail tax without current UsageDetail");
			}
			TIMUsageDetail detail = (TIMUsageDetail) this.context.getAttribute(OM_CURRENT_USAGE_EVENT);
			// handles tax
			ChargedTax tax = new ChargedTax();
			tax.setTag(_attributes.getValue(TIMOMLoader.BGH_ELEMENT_KEY));
			tax.setTaxName(_attributes.getValue(BGH_INVOICE_USAGE_TAX_NAME));
			tax.setTaxRate(ParserUtils.getDouble(_attributes.getValue(BGH_INVOICE_USAGE_TAX_RATE)));
			// saving ChargedTax to current UsageDetail
			detail.addTax(tax);
			// debugging attributes
			log.debug("Found UsageDetail tax=" + tax.getTaxName());
			log.debug("Found UsageDetail rate=" + tax.getTaxRate());

		} else if (BGH_INVOICE_USAGE_PROMOTION_ELEMENT.equals(_localName)) {
			// must have current TIMUsageDetail
			if (!this.context.hasAttribute(OM_CURRENT_USAGE_EVENT)) {
				throw new SAXException("Cannot handle UsageDetail promotions without current UsageDetail");
			}
			TIMUsageDetail detail = (TIMUsageDetail) this.context.getAttribute(OM_CURRENT_USAGE_EVENT);
			// handles promotion
			UsagePromotionDetail promotionDetail = new UsagePromotionDetail(detail);
			promotionDetail.setTag(_attributes.getValue(TIMOMLoader.BGH_ELEMENT_KEY));
			promotionDetail.setPromotionType(_attributes.getValue(BGH_INVOICE_USAGE_PROMOTION_TYPE));
			promotionDetail.setPromotionPackage(_attributes.getValue(BGH_INVOICE_USAGE_PROMOTION_PACKAGE));
			promotionDetail.setPromotionModel(_attributes.getValue(BGH_INVOICE_USAGE_PROMOTION_MODEL));
			promotionDetail.setAmount(ParserUtils.getDouble(_attributes.getValue(BGH_INVOICE_USAGE_PROMOTION_AMOUNT)));
			// saving promotion detail to current event
			detail.getPromotionDetails().add(promotionDetail);
			// debuging attributes
			log.debug("Found usage promotionType=" + promotionDetail.getPromotionType());
			log.debug("Found usage promotionPackage=" + promotionDetail.getPromotionPackage());
			log.debug("Found usage promotionModel=" + promotionDetail.getPromotionModel());
			log.debug("Found usage promotionAmount=" + promotionDetail.getAmount());
		} else if (BGH_INVOICE_USAGE_FU_ELEMENT.equals(_localName)) {
			// must have current TIMUsageDetail
			if (!this.context.hasAttribute(OM_CURRENT_USAGE_EVENT)) {
				throw new SAXException("Cannot handle UsageDetail promotions without current UsageDetail");
			}
			TIMUsageDetail detail = (TIMUsageDetail) this.context.getAttribute(OM_CURRENT_USAGE_EVENT);
			// handles FU element
			UsageFUDetail fuDetail = new UsageFUDetail(detail);
			fuDetail.setTag(_attributes.getValue(TIMOMLoader.BGH_ELEMENT_KEY));
			fuDetail.setPackageShortDescription(_attributes.getValue(BGH_INVOICE_USAGE_FU_SHDES));
			fuDetail.setPackageLongDescription(_attributes.getValue(BGH_INVOICE_USAGE_FU_LONGDES));
			fuDetail.setAccountID(_attributes.getValue(BGH_INVOICE_USAGE_FU_ACCOUNT_ID));
			fuDetail.setReductionVolume(BGHDataFormats.buildUnitCounter(_attributes.getValue(BGH_INVOICE_USAGE_FU_REDUCT_TIME)));
			// saving promotion detail to current event
			detail.getFreeUnitDetails().add(fuDetail);
			// debuging attributes
			log.debug("Found usage FU accountID=" + fuDetail.getAccountID());
			log.debug("Found usage FU shortDescription=" + fuDetail.getPackageShortDescription());
			log.debug("Found usage FU longDescription=" + fuDetail.getPackageLongDescription());
			log.debug("Found usage FU reductionVolume=" + fuDetail.getReductionVolume());
		}
	}


//	protected boolean isVoiceDestination(String _dest) {
//		return ((_dest != null) && (_dest.indexOf("AREA") > 0));
//	}
//
	protected void setStateAndArea(TIMUsageDetail _detail) {
		String origin = _detail.getOriginCity();
		String destination = _detail.getDestinationCity();
		// removing 'FIXO -' token
		if (origin != null) {
			origin = origin.replace("FIXO - ", "");
			// spliting
			String[] originInfo = origin.split("\\sAREA\\s");
			if ((originInfo != null) && (originInfo.length == 2)) {
				_detail.setOriginState(originInfo[0]);
				_detail.setOriginAreaCode(originInfo[1]);
			} else {
				log.debug("Could not parse origin information : " + origin);
			}
		}
		if (destination != null) {
			destination = destination.replace("FIXO - ", "");
			String[] destinationInfo = destination.split("\\sAREA\\s");
			// setting info
			if ((destinationInfo != null) && (destinationInfo.length == 2)) {
				_detail.setDestinationState(destinationInfo[0]);
				_detail.setDestinationAreaCode(destinationInfo[1]);
			} else {
				log.debug("Could not parse destination information : " + destination);
			}
		}
	}
}
