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
import br.com.auster.tim.om.invoice.ContractTotalsDetail;

/**
 * @author framos
 * @version $Id$
 *
 */
public class TotalsPerContractHandler extends MultiHandlerReceiverBase  {

	
	
	// -------------------
	// Class constants
	// -------------------
	
	private static final Logger log = Logger.getLogger(TotalsPerContractHandler.class);
	
	public static final String BGH_CONTRTOTAL_SUBSCRIPTION_ELEMENT = "ContractSubscription";
	public static final String BGH_CONTRTOTAL_SERVICES_ELEMENT = "ContractServices";
	public static final String BGH_CONTRTOTAL_OCC_ELEMENT = "ContractOCC";
	public static final String BGH_CONTRTOTAL_DISCOUNT_ELEMENT = "ContractDiscounts";
	public static final String BGH_CONTRTOTAL_FREEUNITS_ELEMENT = "ContractFU";
	public static final String BGH_CONTRTOTAL_TOTAL_ELEMENT = "ContractTotal";
	// common attributes for all contractTotal elements
	public static final String BGH_CONTRTOTAL_DIRNUM = "directoryNumber";
	public static final String BGH_CONTRTOTAL_CONTRACTNUMBER = "contractNumber";
	public static final String BGH_CONTRTOTAL_SUBTOTAL = "subtotal";
	public static final String BGH_CONTRTOTAL_FU_TOTALCOUNT = "FUTotals";
	
	public static final String OM_CURRENT_CONTRACTTOTAL_SECTION = "invoice.totals.section.current";
	public static final String OM_CURRENT_CONTRACTTOTAL_KEY = "invoice.totals.section.id";
	
	
	
	// -------------------
	// Public methods
	// -------------------
	
	public void startElement(String _namespace, String _localName, String _qName, Attributes _attributes) throws SAXException {
		
		if (BGH_CONTRTOTAL_SUBSCRIPTION_ELEMENT.equals(_localName) || 
			BGH_CONTRTOTAL_SERVICES_ELEMENT.equals(_localName)     ||
			BGH_CONTRTOTAL_OCC_ELEMENT.equals(_localName)          ||
			BGH_CONTRTOTAL_DISCOUNT_ELEMENT.equals(_localName)     ||
			BGH_CONTRTOTAL_FREEUNITS_ELEMENT.equals(_localName)    ||
			BGH_CONTRTOTAL_TOTAL_ELEMENT.equals(_localName)) {
			
			Section totalsSection = buildTotalsSection(_localName, _attributes.getValue(TIMOMLoader.BGH_ELEMENT_KEY));
			// handles contract Totals
			ContractTotalsDetail detail = new ContractTotalsDetail();
			detail.setTag(_attributes.getValue(TIMOMLoader.BGH_ELEMENT_KEY));
			detail.setAccessNbr(_attributes.getValue(BGH_CONTRTOTAL_DIRNUM));
			detail. setContractNumber(_attributes.getValue(BGH_CONTRTOTAL_CONTRACTNUMBER));
			if (BGH_CONTRTOTAL_FREEUNITS_ELEMENT.equals(_localName)) {
				detail.setTotalCount(ParserUtils.getInt(_attributes.getValue(BGH_CONTRTOTAL_FU_TOTALCOUNT))); 
			} else { 
				detail.setTotalAmount(ParserUtils.getDouble(_attributes.getValue(BGH_CONTRTOTAL_SUBTOTAL)));
			}
			// saving Contract Totals into current Details section
			totalsSection.addDetail(detail);
			// debugging attributes
			log.debug("Found Contract " + _localName + " Totals");
			log.debug("Found ContractTotals dirNUm=" + detail.getAccessNbr());
			log.debug("Found ContractTotals contract=" + detail.getContractNumber());
			log.debug("Found ContractTotals acmount=" + detail.getTotalAmount());
		}
	}
	
	
	private Section buildTotalsSection(String _localName, String _tag) throws SAXException {
		// If has current Section, check if XML Tag has changed 
		if ((this.context.hasAttribute(OM_CURRENT_CONTRACTTOTAL_SECTION)) && 
			(this.context.hasAttribute(OM_CURRENT_CONTRACTTOTAL_KEY))) {
			String currentKey = (String)this.context.getAttribute(OM_CURRENT_CONTRACTTOTAL_KEY);
			// if its still the same, then keep using the section already created
			if (_localName.equals(currentKey)) {
				return (Section) this.context.getAttribute(OM_CURRENT_CONTRACTTOTAL_SECTION);
			}
		}
		// otherwise, build the new section for ContractTotals detais
		if (!this.context.hasAttribute(InvoiceAndAccountHandler.OM_CURRENT_INVOICE)) {
			throw new SAXException("Cannot handle NF without current Invoice");
		}
		Invoice invoice = (Invoice)  this.context.getAttribute(InvoiceAndAccountHandler.OM_CURRENT_INVOICE);
		// creates new TotalsSection section
		Section totalsSection = new Section();
		totalsSection.setCaption(_localName);
		totalsSection.setTag(_tag);
		// saving new section into Context and current Invoice
		invoice.addSection(totalsSection);
		this.context.setAttribute(OM_CURRENT_CONTRACTTOTAL_SECTION, totalsSection);
		this.context.setAttribute(OM_CURRENT_CONTRACTTOTAL_KEY, _localName);
		return totalsSection;
	}
}
