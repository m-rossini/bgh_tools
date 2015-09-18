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
package br.com.auster.tim.bgh.sax;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import br.com.auster.dware.sax.MultiHandlerReceiverBase;
import br.com.auster.om.invoice.Account;
import br.com.auster.om.invoice.Section;
import br.com.auster.om.util.ParserUtils;
import br.com.auster.tim.om.invoice.ContractInfoSection;
import br.com.auster.tim.om.invoice.ContractInfoSubtotalDetail;

/**
 * @author framos
 * @version $Id$
 *
 */
public class InvoiceContractDetailsHandler extends MultiHandlerReceiverBase  {

	
	
	// -------------------
	// Class constants
	// -------------------
	
	private static final Logger log = Logger.getLogger(InvoiceContractDetailsHandler.class);
	
	public static final String BGH_INVOICE_CONTRACTINFO_ELEMENT = "ContractInfo"; 
	public static final String BGH_INVOICE_CONTRACTINFO_DIRNUM = "directoryNumber";
	public static final String BGH_INVOICE_CONTRACTINFO_RATEPLAN = "ratePlan";
	public static final String BGH_INVOICE_CONTRACTINFO_CONTRACTNBR = "contractNumber";
	public static final String BGH_INVOICE_CONTRACTINFO_USERNAME = "userName";
	public static final String BGH_INVOICE_CONTRACTINFO_ANATELCODE = "codigoAnatel";
	public static final String BGH_INVOICE_CONTRACTINFO_RATEPLAN_SHDES = "rateplanShdes";
	
	public static final String BGH_CONTRACTINFO_SUBTOT_BYTES_ELEMENT = "ContractUsageTotalBytes";
	public static final String BGH_CONTRACTINFO_SUBTOT_EVTS_ELEMENT = "ContractUsageTotalMessages";
	public static final String BGH_CONTRACTINFO_SUBTOT_CALLS_ELEMENT = "ContractUsageTotalSeconds";
	// common attributes for all ContractInfo SubTotals
	public static final String BGH_CONTRACTINFO_SUBTOT_ROUNDEDVOLUME = "subTotalRoundedVolumeUsed";
	public static final String BGH_CONTRACTINFO_SUBTOT_REALVOLUME = "subTotalVolumeUsed";
	public static final String BGH_CONTRACTINFO_SUBTOT_QUANTITY = "quantity";
	public static final String BGH_CONTRACTINFO_SUBTOT_TOTALAMT = "subTotalAmountAfterPromotionsDisc";
	public static final String BGH_CONTRACTINFO_SUBTOT_FREEUNITS_DISC = "subTotalFreeUnitsDiscAmount";
	public static final String BGH_CONTRACTINFO_SUBTOT_DISCOUNT = "subTotalPromotionsDiscAmount";
	
	public static final String BGH_CONTRACTINFO_STFC_INSTALLADDRESS = "InstallAddress";
	
	public static final String OM_CURRENT_CONTRACT_INFO_SECTION = "invoice.details.contract.current";
	
	
	
	// -------------------
	// Public methods
	// -------------------
	
	public void startElement(String _namespace, String _localName, String _qName, Attributes _attributes) throws SAXException {
		
		if (BGH_INVOICE_CONTRACTINFO_ELEMENT.equals(_localName)) {
			if (!this.context.hasAttribute(InvoiceDetailsHandler.OM_CURRENT_INVOICE_DETAILS_SECTION)) {
				throw new SAXException("Cannot handle ContractInfo without current Details section");
			}
			Section details = (Section)this.context.getAttribute(InvoiceDetailsHandler.OM_CURRENT_INVOICE_DETAILS_SECTION);
			if (!this.context.hasAttribute(InvoiceAndAccountHandler.OM_CURRENT_ACCOUNT)) {
				throw new SAXException("Cannot handle ContractInfo without current Account");
			}
			Account account = (Account) this.context.getAttribute(InvoiceAndAccountHandler.OM_CURRENT_ACCOUNT);
			// handles contract info
			ContractInfoSection section = new ContractInfoSection();
			section.setTag(_attributes.getValue(TIMOMLoader.BGH_ELEMENT_KEY));
			section.setAccessNbr(_attributes.getValue(BGH_INVOICE_CONTRACTINFO_DIRNUM));
			section.setRatePlan(_attributes.getValue(BGH_INVOICE_CONTRACTINFO_RATEPLAN));
			section.setContractNumber(_attributes.getValue(BGH_INVOICE_CONTRACTINFO_CONTRACTNBR));
			section.setUserName(_attributes.getValue(BGH_INVOICE_CONTRACTINFO_USERNAME));
			section.setAnatelCode(_attributes.getValue(BGH_INVOICE_CONTRACTINFO_ANATELCODE));
			section.setRateplanShdes(_attributes.getValue(BGH_INVOICE_CONTRACTINFO_RATEPLAN_SHDES));
			// adding account info to contract
			section.setCarrierCode(account.getCarrierCode());
			section.setCarrierState(account.getCarrierState());
			// although it's the default, will explicitly set mobile flag to TRUE 
			section.setMobile(true);
			// saving ContractInfo into Context and current Details section
			details.addSection(section);
			this.context.setAttribute(OM_CURRENT_CONTRACT_INFO_SECTION, section);
			// debugging attributes
			log.debug("Found contract number=" + section.getContractNumber());
			log.debug("Found contract accNbr=" + section.getAccessNbr());
			log.debug("Found contract username=" + section.getUserName());
			log.debug("Found contract rateplan=" + section.getRatePlan());
			log.debug("Found contract anatelCode=" + section.getAnatelCode());
			log.debug("Found contract rateplanShdes=" + section.getRateplanShdes());
			
		} else if (BGH_CONTRACTINFO_SUBTOT_BYTES_ELEMENT.equals(_localName) ||
				   BGH_CONTRACTINFO_SUBTOT_EVTS_ELEMENT.equals(_localName) ||
				   BGH_CONTRACTINFO_SUBTOT_CALLS_ELEMENT.equals(_localName)) {
			// must have ContractInfo section
			if (!this.context.hasAttribute(OM_CURRENT_CONTRACT_INFO_SECTION)) {
				throw new SAXException("Cannot handle ContractInfo Totals without current ContractInfo section");
			}
			Section contractInfo = (Section)this.context.getAttribute(OM_CURRENT_CONTRACT_INFO_SECTION);
			// handles ContractInfo Totals
			ContractInfoSubtotalDetail detail = new ContractInfoSubtotalDetail();
			detail.setTag(_attributes.getValue(TIMOMLoader.BGH_ELEMENT_KEY));
			detail.setCaption(_localName);
			detail.setRoundedVolume(BGHDataFormats.buildUnitCounter(_attributes.getValue(BGH_CONTRACTINFO_SUBTOT_ROUNDEDVOLUME)));
			detail.setUsedVolume(BGHDataFormats.buildUnitCounter(_attributes.getValue(BGH_CONTRACTINFO_SUBTOT_REALVOLUME)));
			detail.setQuantity(ParserUtils.getInt(_attributes.getValue(BGH_CONTRACTINFO_SUBTOT_QUANTITY)));
			detail.setTotalAmount(ParserUtils.getDouble(_attributes.getValue(BGH_CONTRACTINFO_SUBTOT_TOTALAMT)));
			detail.setFreeUnitsDiscountAmount(ParserUtils.getDouble(_attributes.getValue(BGH_CONTRACTINFO_SUBTOT_FREEUNITS_DISC)));
			detail.setDiscountAmount(ParserUtils.getDouble(_attributes.getValue(BGH_CONTRACTINFO_SUBTOT_DISCOUNT)));
			// saves new ContractInfo total into current ContractInfo
			contractInfo.addDetail(detail);
			// debugging attributes
			log.debug("Found contract totals for " + detail.getCaption());
			log.debug("Found contract totals roundedVolume=" + detail.getRoundedVolume());
			log.debug("Found contract totals actualVolume=" + detail.getUsedVolume());
			log.debug("Found contract totals quantity=" + detail.getQuantity());
			log.debug("Found contract totals totalAmount=" + detail.getTotalAmount());
			log.debug("Found contract totals fuDiscount=" + detail.getFreeUnitsDiscountAmount());
			log.debug("Found contract totals discount=" + detail.getDiscountAmount());
		} else if (BGH_CONTRACTINFO_STFC_INSTALLADDRESS.equals(_localName)) {
			// must have ContractInfo section
			if (!this.context.hasAttribute(OM_CURRENT_CONTRACT_INFO_SECTION)) {
				throw new SAXException("Cannot handle ContractInfo Totals without current ContractInfo section");
			}
			Section contractInfo = (Section)this.context.getAttribute(OM_CURRENT_CONTRACT_INFO_SECTION);
			contractInfo.setMobile(false);
			log.debug("Contract install address found! This is a non-mobile contract.");
		}
	}
	
	
	public void endElement(String _namespace, String _localName, String _uriName) throws SAXException {
		if (BGH_INVOICE_CONTRACTINFO_ELEMENT.equals(_localName)) {
			this.context.removeAttribute(OM_CURRENT_CONTRACT_INFO_SECTION);
		}
	}
}
