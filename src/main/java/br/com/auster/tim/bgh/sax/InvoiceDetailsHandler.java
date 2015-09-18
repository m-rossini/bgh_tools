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
import br.com.auster.om.invoice.Invoice;
import br.com.auster.om.invoice.Section;
import br.com.auster.om.util.ParserUtils;

/**
 * @author framos
 * @version $Id$
 *
 */
public class InvoiceDetailsHandler extends MultiHandlerReceiverBase  {

	
	
	// -------------------
	// Class constants
	// -------------------
	
	private static final Logger log = Logger.getLogger(InvoiceDetailsHandler.class);
	
	public static final String BGH_INVOICE_DETAILS_ELEMENT = "Details"; 
	public static final String BGH_INVOICE_DETAILS_CUSTNAME = "customerName";
	public static final String BGH_INVOICE_DETAILS_TOTAMOUNT = "totalAmount";
	
	public static final String OM_CURRENT_INVOICE_DETAILS_SECTION = "invoice.details.current";
	
	
	
	// -------------------
	// Public methods
	// -------------------
	
	public void startElement(String _namespace, String _localName, String _qName, Attributes _attributes) throws SAXException {
		
		if (BGH_INVOICE_DETAILS_ELEMENT.equals(_localName)) {
			if (!this.context.hasAttribute(InvoiceAndAccountHandler.OM_CURRENT_INVOICE)) {
				throw new SAXException("Cannot handle Details without current Invoice");
			}
			Invoice invoice = (Invoice)  this.context.getAttribute(InvoiceAndAccountHandler.OM_CURRENT_INVOICE);
			// handles NF
			Section section = new Section();
			section.setTag(_attributes.getValue(TIMOMLoader.BGH_ELEMENT_KEY));
			section.setCaption(_localName);
			section.setSectionName(_attributes.getValue(BGH_INVOICE_DETAILS_CUSTNAME));
			section.setTotalAmount(ParserUtils.getDouble(_attributes.getValue(BGH_INVOICE_DETAILS_TOTAMOUNT)));
			// saving Details section into Context and current Invoice
			this.context.setAttribute(OM_CURRENT_INVOICE_DETAILS_SECTION, section);
			invoice.addSection(section);
			// debugging attributes
			log.debug("Found Details section with tot.Amount=" + section.getTotalAmount());
		}
	}
	
	public void endElement(String _namespace, String _localName, String _uriName) throws SAXException {
		if (BGH_INVOICE_DETAILS_ELEMENT.equals(_localName)) {
			// removing Details section from context
			this.context.removeAttribute(OM_CURRENT_INVOICE_DETAILS_SECTION);
		}
	}
}
