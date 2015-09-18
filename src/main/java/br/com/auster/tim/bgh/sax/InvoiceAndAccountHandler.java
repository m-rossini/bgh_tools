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
 * Created on 31/07/2006
 */
package br.com.auster.tim.bgh.sax;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import br.com.auster.dware.sax.MultiHandlerReceiverBase;
import br.com.auster.om.invoice.Identity;
import br.com.auster.om.invoice.Invoice;
import br.com.auster.om.util.ParserUtils;
import br.com.auster.tim.om.invoice.TIMAccount;

/**
 * This ContentHandler extracts and creates the <code>Invoice</code> and <code>Account</code>
 *   information for the current BGH file.
 * <p>
 * For more information on which fields are stored in those OM objects, read the BGH-2-OM
 *   mapping document.
 *
 * @author framos
 * @version $Id$
 *
 */
public class InvoiceAndAccountHandler extends MultiHandlerReceiverBase  {



	// -------------------
	// Class constants
	// -------------------

	private static final Logger log = Logger.getLogger(InvoiceAndAccountHandler.class);

	public static final String BGH_GENERAL_INFORMATION_ELEMENT = "GeneralInformation";
	public static final String BGH_GENERAL_INFORMATION_DUEDATE = "dueDate";
	public static final String BGH_GENERAL_INFORMATION_CUSTCODE = "custCode";
	public static final String BGH_GENERAL_INFORMATION_CUSTID = "customerID";
	public static final String BGH_GENERAL_INFORMATION_ACCOUNTTYPE = "typeOfCustomer";
	public static final String BGH_GENERAL_INFORMATION_DOCTYPE = "typeOfDocument";
	public static final String BGH_GENERAL_INFORMATION_INVOICENBR = "invoiceIdentification";
	public static final String BGH_GENERAL_INFORMATION_TIMOP = "timOperatorID";
	public static final String BGH_GENERAL_INFORMATION_TAX_CATEGORY = "customerTaxCategory";

	public static final String BGH_INVOICE_TOTALOCC_ELEMENT = "TotalOCCsDirectlyAttachedToCustomer";
	public static final String BGH_INVOICE_TOTALADJUST_ELEMENT = "TotalAdjustmentsApplyedToCustomer";
	public static final String BGH_INVOICE_TOTALOCC_ADJUST_AMT = "totalAmount";

	public static final String BGH_INVOICE_TOTALAMOUNT_ELEMENT = "InvoiceTotal";
	public static final String BGH_INVOICE_TOTALAMOUNT_AMT = "invoiceTotal";

	public static final String BGH_HEADER_ELEMENT = "Header";

	public static final String OM_CURRENT_INVOICE = "invoice.current";
	public static final String OM_CURRENT_ACCOUNT = "account.current";
	public static final String FOUND_HEADER = "header.found";



	// -------------------
	// Public methods
	// -------------------

	public void startElement(String _namespace, String _localName, String _qName, Attributes _attributes) throws SAXException {

		if (BGH_GENERAL_INFORMATION_ELEMENT.equals(_localName)) {
			// removing all previous attributes from Context
			this.context.removeAllAttributes();
			// setting the invoice information
			Invoice invoice = new Invoice();
			invoice.setTag(_attributes.getValue(TIMOMLoader.BGH_ELEMENT_KEY));
			invoice.setInvoiceNumber(_attributes.getValue(BGH_GENERAL_INFORMATION_INVOICENBR));
			invoice.setDueDate(BGHDataFormats.getDate(_attributes.getValue(BGH_GENERAL_INFORMATION_DUEDATE)));
			invoice.setOriginalDueDate(_attributes.getValue(BGH_GENERAL_INFORMATION_DUEDATE));
			invoice.setDocumentType(_attributes.getValue(BGH_GENERAL_INFORMATION_DOCTYPE));
			this.context.setAttribute(OM_CURRENT_INVOICE, invoice);
			// setting the account information
			TIMAccount account = new TIMAccount();
			account.setAccountNumber(_attributes.getValue(BGH_GENERAL_INFORMATION_CUSTCODE));
			account.setAccountType(_attributes.getValue(BGH_GENERAL_INFORMATION_ACCOUNTTYPE));
			String timOp = _attributes.getValue(BGH_GENERAL_INFORMATION_TIMOP);
			if ((timOp == null) || (timOp.length() != 4)) {
				throw new SAXException("TIM Operator ID not well-formed: " + timOp);
			}
			account.setTaxCategory( ParserUtils.getInt(_attributes.getValue(BGH_GENERAL_INFORMATION_TAX_CATEGORY)) );
			account.setCarrierState(new String(timOp.substring(2, 4)));
			account.setCustomerServiceArea(timOp);
			account.setCarrierCode(TIMOMLoader.TIM_CARRIERCODE);
			// creating Account identities
			Identity id = new Identity();
			id.setIdentityType(BGH_GENERAL_INFORMATION_CUSTCODE);
			id.setIdentityAttrib1(_attributes.getValue(BGH_GENERAL_INFORMATION_CUSTCODE));
			id.setInvoice(invoice);
			account.addIdentity(id);
			id = new Identity();
			id.setIdentityType(BGH_GENERAL_INFORMATION_CUSTID);
			id.setIdentityAttrib1(_attributes.getValue(BGH_GENERAL_INFORMATION_CUSTID));
			id.setInvoice(invoice);
			account.addIdentity(id);
			// saving account to current invoice
			account.addInvoice(invoice);
			this.context.setAttribute(OM_CURRENT_ACCOUNT, account);
			// debugging attributes
			log.debug("Found invoice dueDate=" + invoice.getDueDate());
			log.debug("Found account number=" + account.getAccountNumber());
			log.debug("Found account type=" + account.getAccountType());
			log.debug("Found account carrierState=" + account.getCarrierState());
			log.debug("Found account carrierCode=" + account.getCarrierCode());
			log.debug("Found account taxCategory=" + account.getTaxCategory());
			log.debug("Found account id=" + _attributes.getValue(BGH_GENERAL_INFORMATION_CUSTID));

		} else if (BGH_HEADER_ELEMENT.equals(_localName)) {
			this.context.setAttribute(FOUND_HEADER, "TRUE");
			// complements Invoice and Account OMs
			AccountInfoFromHeadersHelper helper = new AccountInfoFromHeadersHelper();
			helper.complementInfo(this.context, _attributes);

//			COMMENTED DUE TO TICKET #11
//		} else if (BGH_INVOICE_TOTALAMOUNT_ELEMENT.equals(_localName)) {
//			if (!this.context.hasAttribute(OM_CURRENT_INVOICE)) {
//				throw new SAXException("Cannot handle Invoice totalAmount without current Invoice");
//			}
//			Invoice invoice = (Invoice)  this.context.getAttribute(InvoiceAndAccountHandler.OM_CURRENT_INVOICE);
//			// saving totalAmount into current Invoice
//			invoice.setTotalAmount(ParserUtils.getDouble(_attributes.getValue(BGH_INVOICE_TOTALAMOUNT_AMT)));
//			// debugging attributes
//			log.debug("Found invoice totalAmount=" + invoice.getTotalAmount());

		} else if (BGH_INVOICE_TOTALOCC_ELEMENT.equals(_localName)) {
			if (!this.context.hasAttribute(OM_CURRENT_INVOICE)) {
				throw new SAXException("Cannot handle Invoice totalOCC without current Invoice");
			}
			Invoice invoice = (Invoice)  this.context.getAttribute(InvoiceAndAccountHandler.OM_CURRENT_INVOICE);
			// saving totalAmount into current Invoice
			invoice.setChargesAmount(ParserUtils.getDouble(_attributes.getValue(BGH_INVOICE_TOTALOCC_ADJUST_AMT)));
			// debugging attributes
			log.debug("Found invoice totalOCC=" + invoice.getChargesAmount());

		} else if (BGH_INVOICE_TOTALADJUST_ELEMENT.equals(_localName)) {
			if (!this.context.hasAttribute(OM_CURRENT_INVOICE)) {
				throw new SAXException("Cannot handle Invoice totalAdjust without current Invoice");
			}
			Invoice invoice = (Invoice)  this.context.getAttribute(InvoiceAndAccountHandler.OM_CURRENT_INVOICE);
			// saving totalAmount into current Invoice
			invoice.setAdjustmentsAmount(ParserUtils.getDouble(_attributes.getValue(BGH_INVOICE_TOTALOCC_ADJUST_AMT)));
			// debugging attributes
			log.debug("Found invoice totalAdjust=" + invoice.getAdjustmentsAmount());
		}
	}
}
