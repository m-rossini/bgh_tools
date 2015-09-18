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
public class IndividualNFHandler extends MultiHandlerReceiverBase {

	
	
	// -------------------
	// Class constants
	// -------------------
	
	private static final Logger log = Logger.getLogger(IndividualNFHandler.class);
	// IndividualNotaFiscal element
	public static final String BGH_INDIV_NF_ELEMENT = "IndividualNotaFiscal";
	// IndividualNotaFiscal attributes
	public static final String BGH_INDIV_NF_AMOUNT = "nfAmount";
	public static final String BGH_INDIV_NF_CUST_NAME = "nfCustAddrName";

	
		
	// -------------------
	// Public methods
	// -------------------
	
	public void startElement(String _namespace, String _localName, String _qName, Attributes _attributes) throws SAXException {

		if (BGH_INDIV_NF_ELEMENT.equals(_localName)) {
			if (!this.context.hasAttribute(InvoiceAndAccountHandler.OM_CURRENT_INVOICE)) {
				throw new SAXException("Cannot handle Individual NF without current Invoice");
			}
			Invoice invoice = (Invoice)  this.context.getAttribute(InvoiceAndAccountHandler.OM_CURRENT_INVOICE);
			// handing section info
			Section indivNF = new Section();
			indivNF.setTag(_attributes.getValue(TIMOMLoader.BGH_ELEMENT_KEY));
			indivNF.setSectionName(BGH_INDIV_NF_ELEMENT);
			indivNF.setCaption(_attributes.getValue(BGH_INDIV_NF_CUST_NAME));
			indivNF.setTotalAmount(ParserUtils.getDouble(_attributes.getValue(BGH_INDIV_NF_AMOUNT)));
			
			// adding Individual NF sections
			invoice.addSection(indivNF);
			// debugging attributes
			log.debug("Found receipt number=" + indivNF.getSectionName());
			log.debug("Found receipt carrierCode=" + indivNF.getCaption());
			log.debug("Found receipt carrierName=" + indivNF.getTotalAmount());			
		}
	}	
}
