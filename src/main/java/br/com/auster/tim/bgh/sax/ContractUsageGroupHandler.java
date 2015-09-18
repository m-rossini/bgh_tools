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

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import br.com.auster.dware.sax.MultiHandlerReceiverBase;
import br.com.auster.om.invoice.Section;
import br.com.auster.om.invoice.UsageDetail;
import br.com.auster.om.util.ParserUtils;
import br.com.auster.tim.om.invoice.ContractUsageGroupSubsection;

/**
 * @author framos
 * @version $Id$
 *
 */
public class ContractUsageGroupHandler extends MultiHandlerReceiverBase  {



	// -------------------
	// Class constants
	// -------------------

	private static final Logger log = Logger.getLogger(ContractUsageGroupHandler.class);

	public static final String BGH_INVOICE_USAGEGROUP_DETAILED_INDICATOR = "D";

	public static final String BGH_INVOICE_USAGEGROUP_ELEMENT = "UsageGroup";
	public static final String BGH_INVOICE_USAGEGROUP_NAME = "groupName";
	public static final String BGH_INVOICE_USAGEGROUP_GROUP_INDICATOR = "groupIndicator";

	public static final String BGH_INVOICE_USAGEGROUP_TOT_ELEMENT = "UsageGroupSubtotal";
	public static final String BGH_INVOICE_USAGEGROUP_TOT_ACTUAL_DURATION = "subtotalActualCallDuration";
	public static final String BGH_INVOICE_USAGEGROUP_TOT_ROUNDED_DURATION = "subtotalRoundedCallDuration";
	public static final String BGH_INVOICE_USAGEGROUP_TOT_AMOUNT = "subTotalAmountAfterPromotionsDisc";
	public static final String BGH_INVOICE_USAGEGROUP_TOT_QUANTITY = "quantity";
	public static final String BGH_INVOICE_USAGEGROUP_TOT_FU = "subTotalFreeUnitsDiscAmount";
	public static final String BGH_INVOICE_USAGEGROUP_TOT_DISCOUNTS = "subTotalPromotionsDiscAmount";

	public static final String OM_CURRENT_CONTRACT_USAGEGROUP = "invoice.details.contract.usagegroup.current";



	// -------------------
	// Public methods
	// -------------------

	public void startElement(String _namespace, String _localName, String _qName, Attributes _attributes) throws SAXException {

		if (BGH_INVOICE_USAGEGROUP_ELEMENT.equals(_localName)) {
			// must have current ContractInfo
			if (!this.context.hasAttribute(InvoiceContractDetailsHandler.OM_CURRENT_CONTRACT_INFO_SECTION)) {
				throw new SAXException("Cannot handle UsageGroup without current ContractInfo section");
			}
			Section contractInfo = (Section) this.context.getAttribute(InvoiceContractDetailsHandler.OM_CURRENT_CONTRACT_INFO_SECTION);
			// handles group header
			ContractUsageGroupSubsection section = new ContractUsageGroupSubsection();
			section.setTag(_attributes.getValue(TIMOMLoader.BGH_ELEMENT_KEY));
			section.setSectionName(_attributes.getValue(BGH_INVOICE_USAGEGROUP_NAME));
			section.setUsage(BGH_INVOICE_USAGEGROUP_DETAILED_INDICATOR.equals(_attributes.getValue(BGH_INVOICE_USAGEGROUP_GROUP_INDICATOR)));
			section.setMobile(contractInfo.isMobile());
			// saving UsageGroup into context and current ContractInfo section
			this.context.setAttribute(OM_CURRENT_CONTRACT_USAGEGROUP, section);
			contractInfo.addSection(section);
			// debugging attributes
			log.debug("Found usageGroup " + section.getSectionName());

		} else if (BGH_INVOICE_USAGEGROUP_TOT_ELEMENT.equals(_localName)) {
			// must have current UsageGroup section
			if (!this.context.hasAttribute(OM_CURRENT_CONTRACT_USAGEGROUP)) {
				throw new SAXException("Cannot handle UsageGroup SubTotal without current UsageGroup section");
			}
			ContractUsageGroupSubsection section = (ContractUsageGroupSubsection) this.context.getAttribute(OM_CURRENT_CONTRACT_USAGEGROUP);
			// setting subTotals
			String unitType = BGHDataFormats.getUnitType(_attributes.getValue(BGH_INVOICE_USAGEGROUP_TOT_ACTUAL_DURATION));
			section.setTotalAmount(ParserUtils.getDouble(_attributes.getValue(BGH_INVOICE_USAGEGROUP_TOT_AMOUNT)));
			section.setUnitType(unitType);
			section.setRoundedVolume(BGHDataFormats.buildUnitCounter(unitType, _attributes.getValue(BGH_INVOICE_USAGEGROUP_TOT_ROUNDED_DURATION)));
			section.setUsedVolume(BGHDataFormats.buildUnitCounter(unitType, _attributes.getValue(BGH_INVOICE_USAGEGROUP_TOT_ACTUAL_DURATION)));
			section.setQuantity(ParserUtils.getInt(_attributes.getValue(BGH_INVOICE_USAGEGROUP_TOT_QUANTITY)));
			section.setFreeUnitsDiscountAmount(ParserUtils.getDouble(_attributes.getValue(BGH_INVOICE_USAGEGROUP_TOT_FU)));
			section.setDiscountAmount(ParserUtils.getDouble(_attributes.getValue(BGH_INVOICE_USAGEGROUP_TOT_DISCOUNTS)));

		}
	}

	public void endElement(String _namespace, String _localName, String _uriName) throws SAXException {

		if (BGH_INVOICE_USAGEGROUP_ELEMENT.equals(_localName)) {
			if (!this.context.hasAttribute(OM_CURRENT_CONTRACT_USAGEGROUP)) {
				throw new SAXException("Cannot handle UsageGroup sorting without current UsageGroup section");
			}
			ContractUsageGroupSubsection section = (ContractUsageGroupSubsection) this.context.getAttribute(OM_CURRENT_CONTRACT_USAGEGROUP);
			this.context.removeAttribute(OM_CURRENT_CONTRACT_USAGEGROUP);
			// must remove current usage too, since it cannot be removed on usageDetail handler. This is due to the need to
			//   set the nextCall attribute from usages
			this.context.removeAttribute(ContractUsageDetailsHandler.OM_CURRENT_USAGE_EVENT);

			// now, we will define sorting of events on this section, based on the datetime attribute
			if (section.getDetails() != null) {
				Collections.sort(section.getDetails(), new ByDateUsageDetailComparator());
				int sequence = 0;
				for (Iterator it = section.getDetails().iterator(); it.hasNext();) {
					UsageDetail detail = (UsageDetail) it.next();
					detail.setSeqNbr(sequence++);
				}
			}
		}
	}

	private static final class ByDateUsageDetailComparator implements Comparator {

		public int compare(Object o1, Object o2) throws ClassCastException {
			UsageDetail usage1 = (UsageDetail) o1;
			UsageDetail usage2 = (UsageDetail) o2;
			// gets objects attributes
			Date  date1 = usage1.getDatetime();
			Date  date2 = usage2.getDatetime();
			return date1.compareTo(date2);
		}
	}

}
