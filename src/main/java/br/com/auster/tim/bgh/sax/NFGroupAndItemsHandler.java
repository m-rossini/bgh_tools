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
 * Created on 03/08/2006
 */
package br.com.auster.tim.bgh.sax;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import br.com.auster.dware.sax.MultiHandlerReceiverBase;
import br.com.auster.om.invoice.ChargedTax;
import br.com.auster.om.invoice.Receipt;
import br.com.auster.om.util.ParserUtils;
import br.com.auster.tim.om.invoice.GroupItemReceiptDetail;
import br.com.auster.tim.om.invoice.TIMReceiptDetail;

/**
 * @author framos
 * @version $Id$
 *
 */
public class NFGroupAndItemsHandler extends MultiHandlerReceiverBase {



	// -------------------
	// Class constants
	// -------------------

	private static final Logger log = Logger.getLogger(NFGroupAndItemsHandler.class);

	public static final String BGH_TIMNF_HOMEGROUP_ELEMENT = "HomeCallsGroups";
	public static final String BGH_TIMNF_ROAMGROUP_ELEMENT = "RoamCallsGroups";
	public static final String BGH_TIMNF_SVCGROUP_ELEMENT = "ServiceUsageGroups";
	// group items attributes
	public static final String BGH_NF_GROUPITEM_ELEMENT = "UsageGroups";
	public static final String BGH_NF_GROUPITEM_NAME = "usageGroupName";
	public static final String BGH_NF_GROUPITEM_AREA = "roamingIndicator";
	public static final String BGH_NF_GROUPITEM_EVTS = "usageGroupTotEvents";
	public static final String BGH_NF_GROUPITEM_RND_DURATION = "usageGroupTotRoundedDuration";
	public static final String BGH_NF_GROUPITEM_DURATION = "usageGroupTotActualDuration";
	public static final String BGH_NF_GROUPITEM_AMTBEFORE = "usageGroupTotAmountBeforePromotionsDisc";
	public static final String BGH_NF_GROUPITEM_TOTAMT = "usageGroupTotAmountAfterPromotionsDisc";
	public static final String BGH_NF_GROUPITEM_DISCAMT = "usageGroupTotPromotionsDiscAmount";
	// group item taxes attributes
	public static final String BGH_NF_GROUPITEM_TAX_ELEMENT = "UsageGroupsTaxes";
	public static final String BGH_NF_GROUPITEM_TAXNAME = "taxName";
	public static final String BGH_NF_GROUPITEM_TAXRATE = "taxRate";
	public static final String BGH_NF_GROUPITEM_TAXAMT = "taxAmount";
	// group totals
	public static final String BGH_TIMNF_GROUP_CALLS_ELEMENT = "CallSummarySubTotals";
	public static final String BGH_TIMNF_GROUP_SVCS_ELEMENT = "UsageServiceTotalAmount";
	public static final String BGH_TIMNF_GROUP_SUBTOTAMT = "subTotalAmountAfterPromotionsDisc";
	public static final String BGH_TIMNF_GROUP_SUBDISCAMT = "subTotalPromotionsDiscAmount";

	public static final String OM_CURRENT_NF_GROUP = "receipt.group.current";
	public static final String OM_CURRENT_NF_GROUPITEM = "receipt.group.item.current";



	// -------------------
	// Public methods
	// -------------------

	public void startElement(String _namespace, String _localName, String _qName, Attributes _attributes) throws SAXException {

		TIMReceiptDetail rd;
		GroupItemReceiptDetail grd;
		if (BGH_TIMNF_HOMEGROUP_ELEMENT.equals(_localName) ||
			BGH_TIMNF_ROAMGROUP_ELEMENT.equals(_localName) ||
			BGH_TIMNF_SVCGROUP_ELEMENT.equals(_localName)) {

			// must have NF
			if (!this.context.hasAttribute(NFHeaderHandler.OM_CURRENT_NF)) {
				throw new SAXException("Cannot handle NF Group without current NF");
			}
			Receipt rcpt = (Receipt)  this.context.getAttribute(NFHeaderHandler.OM_CURRENT_NF);
			// creating the new NF group
			rd = new TIMReceiptDetail();
			rd.setTag(_attributes.getValue(TIMOMLoader.BGH_ELEMENT_KEY));
			rd.setCaption(_localName);
			rd.setFromBGH(true);
			// debbuging attributes
			log.debug("Found NFDetail caption=" + rd.getCaption());
			// saves the new NF Group into the Context and in the current NF
			this.context.setAttribute(OM_CURRENT_NF_GROUP, rd);
			rcpt.addDetail(rd);

		} else if (BGH_NF_GROUPITEM_ELEMENT.equals(_localName)) {
			// must have NF Group
			if (!this.context.hasAttribute(OM_CURRENT_NF_GROUP)) {
				throw new SAXException("Cannot handle NF GroupItem without current NF Group");
			}
			// handles the NF Group Item
			rd = (TIMReceiptDetail) this.context.getAttribute(OM_CURRENT_NF_GROUP);
			grd = new GroupItemReceiptDetail();
			grd.setTag(_attributes.getValue(TIMOMLoader.BGH_ELEMENT_KEY));
			grd.setCaption(_attributes.getValue(BGH_NF_GROUPITEM_NAME));
			String roamingIndicator = _attributes.getValue(BGH_NF_GROUPITEM_AREA);

			if ((roamingIndicator != null) && (roamingIndicator.length() > 0) &&
				Character.isDigit(roamingIndicator.charAt(0))
			   ) {
				// in this case we dont have the roaming indicator field,
				//   and all the BGH record is shifted left by 1
				grd.setAreaInidicator(null);
				grd.setEventCount(ParserUtils.getInt(roamingIndicator));
				grd.setUnitCount(_attributes.getValue(BGH_NF_GROUPITEM_RND_DURATION));
				grd.setUnitType(BGHDataFormats.getUnitType(grd.getUnitCount()));
				if ("34101000".equals(grd.getTag())) {
					grd.setTotalAmount(ParserUtils.getDouble(_attributes.getValue(BGH_NF_GROUPITEM_DURATION)));
				} else {
					grd.setTotalAmount(ParserUtils.getDouble(_attributes.getValue(BGH_NF_GROUPITEM_AMTBEFORE)));
				}
				grd.setPromotionAmount(ParserUtils.getDouble(_attributes.getValue(BGH_NF_GROUPITEM_TOTAMT)));
			} else {
				grd.setAreaInidicator(roamingIndicator);
				grd.setEventCount(ParserUtils.getInt(_attributes.getValue(BGH_NF_GROUPITEM_EVTS)));
				grd.setUnitCount(_attributes.getValue(BGH_NF_GROUPITEM_DURATION));
				grd.setUnitType(BGHDataFormats.getUnitType(grd.getUnitCount()));
				grd.setTotalAmount(ParserUtils.getDouble(_attributes.getValue(BGH_NF_GROUPITEM_TOTAMT)));
				grd.setPromotionAmount(ParserUtils.getDouble(_attributes.getValue(BGH_NF_GROUPITEM_DISCAMT)));

			}
			// debugging attributes
			log.debug("Found NF GroupItem caption=" + grd.getCaption());
			log.debug("Found NF GroupItem units=" + grd.getUnitCount());
			log.debug("Found NF GroupItem unitType=" + grd.getUnitType());
			log.debug("Found NF GroupItem total=" + grd.getTotalAmount());
			log.debug("Found NF GroupItem discouts=" + grd.getPromotionAmount());
			log.debug("Found NF GroupItem areaIndicator=" + grd.getAreaIndicator());
			log.debug("Found NF GroupItem counts=" + grd.getEventCount());
			// saves the new NF GroupItem into the Context and in the current NF Detail
			this.context.setAttribute(OM_CURRENT_NF_GROUPITEM, grd);
			rd.addGroupItem(grd);

		} else if (BGH_NF_GROUPITEM_TAX_ELEMENT.equals(_localName)) {
			// must have NF Group Item
			if (!this.context.hasAttribute(OM_CURRENT_NF_GROUPITEM)) {
				throw new SAXException("Cannot handle NF GroupItem Tax without current NF GroupItem");
			}
			grd = (GroupItemReceiptDetail)this.context.getAttribute(OM_CURRENT_NF_GROUPITEM);
			// handles GroupItem tax
			ChargedTax tax = new ChargedTax();
			tax.setTag(_attributes.getValue(TIMOMLoader.BGH_ELEMENT_KEY));
			tax.setTaxName(_attributes.getValue(BGH_NF_GROUPITEM_TAXNAME));
			tax.setTaxRate(ParserUtils.getDouble(_attributes.getValue(BGH_NF_GROUPITEM_TAXRATE)));
			tax.setTaxAmount(ParserUtils.getDouble(_attributes.getValue(BGH_NF_GROUPITEM_TAXAMT)));
			// debugging attributes
			log.debug("Found NF GroupItem taxName=" + tax.getTaxName());
			log.debug("Found NF GroupItem taxRate=" + tax.getTaxRate());
			log.debug("Found NF GroupItem taxAmount=" + tax.getTaxAmount());
			// saves the new NF GroupItem Tax into the current NF GroupItem
			grd.addTax(tax);

		} else if (BGH_TIMNF_GROUP_CALLS_ELEMENT.equals(_localName) ||
				   BGH_TIMNF_GROUP_SVCS_ELEMENT.equals(_localName)) {

			// must have NF Group
			if (!this.context.hasAttribute(OM_CURRENT_NF_GROUP)) {
				throw new SAXException("Cannot handle NF Group subTotals without current NF Group");
			}
			// handles the NF Group Item
			rd = (TIMReceiptDetail) this.context.removeAttribute(OM_CURRENT_NF_GROUP);
			rd.setTotalAmount(ParserUtils.getDouble(_attributes.getValue(BGH_TIMNF_GROUP_SUBTOTAMT)));
			rd.setPromotionAmount(ParserUtils.getDouble(_attributes.getValue(BGH_TIMNF_GROUP_SUBDISCAMT)));
			// debugging attributes
			log.debug("Found NF Group total=" + rd.getTotalAmount());
			log.debug("Found NF Group discounts=" + rd.getPromotionAmount());
		}
	}
}
