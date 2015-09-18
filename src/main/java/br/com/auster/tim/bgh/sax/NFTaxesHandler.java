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
package br.com.auster.tim.bgh.sax;

import java.util.Iterator;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import br.com.auster.dware.sax.MultiHandlerReceiverBase;
import br.com.auster.om.invoice.ChargedTax;
import br.com.auster.om.invoice.Invoice;
import br.com.auster.om.invoice.Receipt;
import br.com.auster.om.invoice.Section;
import br.com.auster.om.util.ParserUtils;
import br.com.auster.tim.om.invoice.RetentionTaxesSection;

/**
 * @author framos
 * @version $Id$
 */
public class NFTaxesHandler extends MultiHandlerReceiverBase {



	// -------------------
	// Class constants
	// -------------------

	private static final Logger log = Logger.getLogger(NFTaxesHandler.class);

	public static final String BGH_RECEIPT_TAX_ELEMENT = "TaxSummary";
	public static final String BGH_INVOICE_RETENTION_ELEMENT = "RetentionTaxes";

	public static final String BGH_TAX_TAXNAME = "taxName";
	public static final String BGH_TAX_TAXRATE = "taxRate";
	public static final String BGH_TAX_TAXAMOUNT = "taxAmount";
	public static final String BGH_TAX_BASEAMOUNT = "baseGrossAmount";



	// -------------------
	// Public methods
	// -------------------

	public void startElement(String _namespace, String _localName, String _qName, Attributes _attributes) throws SAXException {

		if (BGH_RECEIPT_TAX_ELEMENT.equals(_localName)) {
			// must have a current NF
			Receipt rcpt = null;
			if (!this.context.hasAttribute(NFHeaderHandler.OM_CURRENT_NF)) {
				// when its TIM NF, the Receipt object will be already removed from the Context
				if (!this.context.hasAttribute(InvoiceAndAccountHandler.OM_CURRENT_INVOICE)) {
					throw new SAXException("Cannot handle TIM NF taxes without current Invoice");
				}
				Invoice invoice = (Invoice)this.context.getAttribute(InvoiceAndAccountHandler.OM_CURRENT_INVOICE);
				for (Iterator it=invoice.getReceipts().iterator(); it.hasNext();) {
					Receipt r = (Receipt) it.next();
					if (!NFHeaderHandler.BGH_LDC_NF_ID.equals(r.getTag())) {
						rcpt = r;
						break;
					}
				}
				if (rcpt == null) {
					throw new SAXException("Cannot handle TIM NF taxes without current TIM NF");
				}
			} else {
				rcpt = (Receipt)this.context.getAttribute(NFHeaderHandler.OM_CURRENT_NF);
			}
			// handles tax info
			ChargedTax tax = buildTax(_attributes);
			// save tax into current NF
			rcpt.addTax(tax);
		} else if (BGH_INVOICE_RETENTION_ELEMENT.equals(_localName)) {
			if (!this.context.hasAttribute(InvoiceAndAccountHandler.OM_CURRENT_INVOICE)) {
				throw new SAXException("Cannot handle TIM NF taxes without current Invoice");
			}
			Invoice invoice = (Invoice)this.context.getAttribute(InvoiceAndAccountHandler.OM_CURRENT_INVOICE);
			Section retentionSection = null;
			// searching for retention section
			for (Iterator it = invoice.getSections().iterator(); it.hasNext(); ) {
				Section sec = (Section) it.next();
				if (BGH_INVOICE_RETENTION_ELEMENT.equals(sec.getSectionName())) {
					retentionSection = sec;
					break;
				}
			}
			// if not found, we build a new one and add it to the current invoice
			if (retentionSection == null) {
				retentionSection = new RetentionTaxesSection();
				retentionSection.setTag(_attributes.getValue(TIMOMLoader.BGH_ELEMENT_KEY));
				retentionSection.setSectionName(_localName);
				invoice.addSection(retentionSection);
			}
			// handles retention tax info
			ChargedTax tax = buildTax(_attributes);
			// save tax into current NF
			((RetentionTaxesSection)retentionSection).getTaxes().add(tax);
		}
	}



	private ChargedTax buildTax(Attributes _attributes) {
		ChargedTax tax = new ChargedTax();
		tax.setTag(_attributes.getValue(TIMOMLoader.BGH_ELEMENT_KEY));
		tax.setTaxName(_attributes.getValue(BGH_TAX_TAXNAME));
		tax.setTaxRate(ParserUtils.getDouble(_attributes.getValue(BGH_TAX_TAXRATE)));
		tax.setTaxAmount(ParserUtils.getDouble(_attributes.getValue(BGH_TAX_TAXAMOUNT)));
		tax.setTaxableAmount(ParserUtils.getDouble(_attributes.getValue(BGH_TAX_BASEAMOUNT)));
		// debugging attributes
		log.debug("Found NF tax name=" + tax.getTaxName());
		log.debug("Found NF tax rate=" + tax.getTaxRate());
		log.debug("Found NF tax base amt=" + tax.getTaxableAmount());
		log.debug("Found NF tax amt=" + tax.getTaxAmount());
		// returning
		return tax;
	}
}
