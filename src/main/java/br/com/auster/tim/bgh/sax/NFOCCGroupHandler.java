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
import br.com.auster.tim.om.invoice.OCCGroupReceiptDetail;
import br.com.auster.tim.om.invoice.TIMReceiptDetail;

/**
 * @author framos
 * @version $Id$
 *
 */
public class NFOCCGroupHandler extends MultiHandlerReceiverBase {



	// -------------------
	// Class constants
	// -------------------

	private static final Logger log = Logger.getLogger(NFOCCGroupHandler.class);

	public static final String BGH_TIMNF_OCCSUBSC_ELEMENT = "OCCSubscription";
	public static final String BGH_TIMNF_OCC_ELEMENT = "OCCOther";
	public static final String BGH_TIMNF_OCCADJUST_ELEMENT = "OCCAdjustments";
	// group items attributes
	public static final String BGH_TIMNF_OCC_DESCRIPTION = "occDescription";
	public static final String BGH_TIMNF_OCC_EVTS = "noOfEvents";
	public static final String BGH_TIMNF_OCC_TOTAMT = "grossAmount";
	// group item taxes attributes
	public static final String BGH_TIMNF_OCC_TAX_ELEMENT = "OCCTaxes";
	public static final String BGH_TIMNF_OCC_TAXNAME = "taxName";
	public static final String BGH_TIMNF_OCC_TAXRATE = "taxRate";
	public static final String BGH_TIMNF_OCC_TAXAMT = "taxAmount";
	// group totals
	public static final String BGH_TIMNF_OCC_SUBTOTAL_ELEMENT = "OCCTotalAmount";
	public static final String BGH_TIMNF_OCC_SUBTOTAL_AMT = "grossAmount";

	public static final String OM_OCC_GROUP_CAPTION = "OCCs";

	public static final String OM_CURRENT_NF_OCCGROUP = "receipt.occ.current";
	public static final String OM_CURRENT_NF_OCCDETAIL = "receipt.occ.detail.current";



	// -------------------
	// Public methods
	// -------------------

	public void startElement(String _namespace, String _localName, String _qName, Attributes _attributes) throws SAXException {

		TIMReceiptDetail rd;
		OCCGroupReceiptDetail grd;
		if (BGH_TIMNF_OCCSUBSC_ELEMENT.equals(_localName) ||
			BGH_TIMNF_OCC_ELEMENT.equals(_localName) ||
			BGH_TIMNF_OCCADJUST_ELEMENT.equals(_localName)) {

			// must have NF
			if (!this.context.hasAttribute(NFHeaderHandler.OM_CURRENT_NF)) {
				throw new SAXException("Cannot handle NF OCC without current NF");
			}
			Receipt rcpt = (Receipt)  this.context.getAttribute(NFHeaderHandler.OM_CURRENT_NF);
			// creating group when found first OCC
			if (this.context.hasAttribute(OM_CURRENT_NF_OCCGROUP)) {
				rd = (TIMReceiptDetail) this.context.getAttribute(OM_CURRENT_NF_OCCGROUP);
			} else {
				rd = new TIMReceiptDetail();
				rd.setCaption(OM_OCC_GROUP_CAPTION);
				rd.setTag(_attributes.getValue(TIMOMLoader.BGH_ELEMENT_KEY));
				rcpt.addDetail(rd);
				this.context.setAttribute(OM_CURRENT_NF_OCCGROUP, rd);
				log.debug("Created OCC Group");
			}
			// handles OCC detail
			grd = new OCCGroupReceiptDetail();
			grd.setTag(_attributes.getValue(TIMOMLoader.BGH_ELEMENT_KEY));
			grd.setCaption(_attributes.getValue(BGH_TIMNF_OCC_DESCRIPTION));
			grd.setTotalAmount(ParserUtils.getDouble(_attributes.getValue(BGH_TIMNF_OCC_TOTAMT)));
			grd.setEventCount(ParserUtils.getInt(_attributes.getValue(BGH_TIMNF_OCC_EVTS)));
			grd.setOCC(true);

			//verify if it is a penalty(multa) or interest(juros) OCC. If so, set its attributes
			if (verifyIfPenalty(grd.getCaption())) {
				grd.setPenalty(true);
				grd.setDueDate(getDueDateFromOCCDesc(grd.getCaption()));
				grd.setPayDate(getPayDateFromOCCDesc(grd.getCaption()));
			} else if (verifyIfInterest(grd.getCaption())) {
				grd.setInterest(true);
				grd.setDueDate(getDueDateFromOCCDesc(grd.getCaption()));
				grd.setPayDate(getPayDateFromOCCDesc(grd.getCaption()));
			}

			// debugging attributes
			log.debug("Found NF OCC caption=" + grd.getCaption());
			log.debug("Found NF OCC total=" + grd.getTotalAmount());
			log.debug("Found NF OCC count=" + grd.getEventCount());
			log.debug("OCC flag set to true");
			// saves the new NF OCC into the Context and in the current NF Detail
			this.context.setAttribute(OM_CURRENT_NF_OCCDETAIL, grd);
			rd.addGroupItem(grd);

		} else if (BGH_TIMNF_OCC_TAX_ELEMENT.equals(_localName)) {
			// must have NF OCCItem
			if (!this.context.hasAttribute(OM_CURRENT_NF_OCCDETAIL)) {
				throw new SAXException("Cannot handle NF OCC Tax without current NF OCC");
			}
			grd = (OCCGroupReceiptDetail)this.context.getAttribute(OM_CURRENT_NF_OCCDETAIL);
			// handles OCCItem tax
			ChargedTax tax = new ChargedTax();
			tax.setTag(_attributes.getValue(TIMOMLoader.BGH_ELEMENT_KEY));
			tax.setTaxName(_attributes.getValue(BGH_TIMNF_OCC_TAXNAME));
			tax.setTaxRate(ParserUtils.getDouble(_attributes.getValue(BGH_TIMNF_OCC_TAXRATE)));
			tax.setTaxAmount(ParserUtils.getDouble(_attributes.getValue(BGH_TIMNF_OCC_TAXAMT)));
			// debugging attributes
			log.debug("Found NF OCC taxName=" + tax.getTaxName());
			log.debug("Found NF OCC taxRate=" + tax.getTaxRate());
			log.debug("Found NF OCC taxAmount=" + tax.getTaxAmount());
			// saves the new NF OCC Tax into the current NF OCC
			grd.addTax(tax);

		} else if (BGH_TIMNF_OCC_SUBTOTAL_ELEMENT.equals(_localName)) {

			// must have NF OCCGroup
			if (!this.context.hasAttribute(OM_CURRENT_NF_OCCGROUP)) {
				throw new SAXException("Cannot handle NF OCC subTotal without NF OCC group");
			}
			rd = (TIMReceiptDetail) this.context.removeAttribute(OM_CURRENT_NF_OCCGROUP);
			// handles the NF Group Item
			rd.setTotalAmount(ParserUtils.getDouble(_attributes.getValue(BGH_TIMNF_OCC_SUBTOTAL_AMT)));
			// debugging attributes
			log.debug("Found NF OCC Group total=" + rd.getTotalAmount());

		}
	}

	/**
	 *
	 * TODO what this method is responsible for
	 *
	 * @param occDescription
	 * @return boolean
	 * 			true if it is a penalty OCC
	 * 			false otherwise
	 */
	private final boolean verifyIfPenalty(String occDescription) {
		return (occDescription != null) && occDescription.contains("MULTAS");
	}

	/**
	 *
	 * TODO what this method is responsible for
	 *
	 * @param occDescription
	 * @return boolean
	 * 			true if it is an interest OCC
	 * 			false otherwise
	 */
	private final boolean verifyIfInterest(String occDescription) {
		return (occDescription != null) && occDescription.contains("JUROS");
	}

	/**
	 *
	 * Get the OCC penalty/interest due date from the occDescription.
	 *
	 * @param occDescription
	 * @return dueDate
	 */
	private final String getDueDateFromOCCDesc(String occDescription) {
		try {
			return  new String(occDescription.substring(occDescription.indexOf("VENC")+5, occDescription.indexOf("VENC")+13));
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 *
	 * Get the OCC penalty/interest pay date from the occDescription.
	 *
	 * @param occDescription
	 * @return payDate
	 */
	private final String getPayDateFromOCCDesc(String occDescription) {
		try {
			return new String(occDescription.substring(occDescription.indexOf("PAGO EM")+8, occDescription.indexOf("PAGO EM")+16));
		} catch (Exception e) {
			return "";
		}
	}

}
