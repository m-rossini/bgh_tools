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
import br.com.auster.om.invoice.Address;
import br.com.auster.om.invoice.BarCode;
import br.com.auster.om.invoice.Invoice;
import br.com.auster.om.util.ParserUtils;
import br.com.auster.tim.om.invoice.PaystubSection;

/**
 * This ContentHandler extracts the barcode, in numeric format, from the BGH file and stores
 *   it in a <code>BarCode</code> object.
 * <p>
 * For more information on which fields are stored in those OM objects, read the BGH-2-OM 
 *   mapping document.
 *  
 * @author framos
 * @version $Id$
 *
 */
public class PaystubHandler extends MultiHandlerReceiverBase {

	
	
	// -------------------
	// Class constants
	// -------------------
	
	private static final Logger log = Logger.getLogger(PaystubHandler.class);
			
	public static final String BGH_PAYSTUB_ELEMENT = "PayStubInformation";
	public static final String BGH_PAYSTUB_ACCESSNUMBER = "csrNumber";
	public static final String BGH_PAYSTUB_OP_ADDRNAME = "operatorAddrName";
	public static final String BGH_PAYSTUB_OP_ADDRSTREET = "operatorAddrStreet";
	public static final String BGH_PAYSTUB_OP_ADDRSTREET_NO = "operatorAddrStreetNo";
	public static final String BGH_PAYSTUB_OP_ADDRZIP = "operatorAddrZip";
	public static final String BGH_PAYSTUB_OP_ADDRCITY = "operatorAddrCity";
	public static final String BGH_PAYSTUB_OP_ADDRSTATE = "operatorAddrState";
	public static final String BGH_PAYSTUB_USERNAME = "clientName";
	public static final String BGH_PAYSTUB_CUSTCODE = "custCode";
	public static final String BGH_PAYSTUB_BILLSTART_DATE = "billPeriodStart";
	public static final String BGH_PAYSTUB_BILLEND_DATE = "billPeriodEnd";
	public static final String BGH_PAYSTUB_ISSUE_DATE = "issueDate";
	public static final String BGH_PAYSTUB_DUE_DATE = "dueDate";
	public static final String BGH_PAYSTUB_TOTALAMOUNT = "totalAmountDue";
	public static final String BGH_PAYSTUB_PREV_BALANCE = "previousBalance";
	public static final String BGH_PAYSTUB_FINAL_BALANCE = "finalBalance";
	public static final String BGH_PAYSTUB_DIRECTDEBIT_IND = "autoDebitInd";
	public static final String BGH_PAYSTUB_DIRECTDEBIT_INFO = "autoDebitCodeAutomatic";

	public static final String BGH_PAYSTUB_BARCODE_ELEMENT = "BarCode";
	public static final String BGH_BARCODE_NUMBER = "barCode";

	
	
	// -------------------
	// Public methods
	// -------------------
	
	public void startElement(String _namespace, String _localName, String _qName, Attributes _attributes) throws SAXException {
		
		Invoice invoice;
		if (BGH_PAYSTUB_ELEMENT.equals(_localName)) {
			// must always have an Invoice
			if (!this.context.hasAttribute(InvoiceAndAccountHandler.OM_CURRENT_INVOICE)) {
				throw new SAXException("Cannot handle Paystub section without current Invoice");
			}
			invoice = (Invoice)  this.context.getAttribute(InvoiceAndAccountHandler.OM_CURRENT_INVOICE);
			// handles section
			PaystubSection paystub = new PaystubSection();
			paystub.setTag(_attributes.getValue(TIMOMLoader.BGH_ELEMENT_KEY));
			paystub.setCaption(_localName);
			paystub.setAccessNbr(_attributes.getValue(BGH_PAYSTUB_ACCESSNUMBER));
			paystub.setCustomerName(_attributes.getValue(BGH_PAYSTUB_USERNAME));
			paystub.setCustomerCode(_attributes.getValue(BGH_PAYSTUB_CUSTCODE));
			paystub.setCycleStartDate(BGHDataFormats.getDate(_attributes.getValue(BGH_PAYSTUB_BILLSTART_DATE)));
			paystub.setOriginalCycleStartDate(_attributes.getValue(BGH_PAYSTUB_BILLSTART_DATE));
			paystub.setCycleEndDate(BGHDataFormats.getDate(_attributes.getValue(BGH_PAYSTUB_BILLEND_DATE)));
			paystub.setOriginalCycleEndDate(_attributes.getValue(BGH_PAYSTUB_BILLEND_DATE));
			paystub.setIssueDate(BGHDataFormats.getDate(_attributes.getValue(BGH_PAYSTUB_ISSUE_DATE)));
			paystub.setOriginalIssueDate(_attributes.getValue(BGH_PAYSTUB_ISSUE_DATE));
			paystub.setDueDate(BGHDataFormats.getDate(_attributes.getValue(BGH_PAYSTUB_DUE_DATE)));
			paystub.setOriginalDueDate(_attributes.getValue(BGH_PAYSTUB_DUE_DATE));
			// direct debit info
			paystub.setDirectDebitIndicator(_attributes.getValue(BGH_PAYSTUB_DIRECTDEBIT_IND));
			paystub.setDirectDebitInfo(_attributes.getValue(BGH_PAYSTUB_DIRECTDEBIT_INFO));
			paystub.setTotalAmount(ParserUtils.getDouble(_attributes.getValue(BGH_PAYSTUB_TOTALAMOUNT)));
			paystub.setOriginalTotalAmount(_attributes.getValue(BGH_PAYSTUB_TOTALAMOUNT));
			paystub.setPreviousBalance(ParserUtils.getDouble(_attributes.getValue(BGH_PAYSTUB_PREV_BALANCE)));
			paystub.setOriginalFinalBalance(_attributes.getValue(BGH_PAYSTUB_FINAL_BALANCE));
			paystub.setFinalBalance(ParserUtils.getDouble(_attributes.getValue(BGH_PAYSTUB_FINAL_BALANCE)));
			// setting carrier address
			paystub.setCarrierName(_attributes.getValue(BGH_PAYSTUB_OP_ADDRNAME));
			Address address = new Address();
			address.setTag(_attributes.getValue(TIMOMLoader.BGH_ELEMENT_KEY));
			address.setAddressStreet(_attributes.getValue(BGH_PAYSTUB_OP_ADDRSTREET));
			address.setAddressNumber(_attributes.getValue(BGH_PAYSTUB_OP_ADDRSTREET_NO));
			address.setPostalCode(_attributes.getValue(BGH_PAYSTUB_OP_ADDRZIP));
			address.setCity(_attributes.getValue(BGH_PAYSTUB_OP_ADDRCITY));
			address.setState(_attributes.getValue(BGH_PAYSTUB_OP_ADDRSTATE));
			paystub.setCarrierAddress(address);
			// saves paystub section into current Invoice
			invoice.addSection(paystub);
			// debugging attributes
			log.debug("Found paystub accessNbr=" + paystub.getAccessNbr());
			log.debug("Found paystub customerName=" + paystub.getCustomerName());
			log.debug("Found paystub custCode=" + paystub.getCustomerCode());
			log.debug("Found paystub cycleStart=" + paystub.getCycleStartDate());
			log.debug("Found paystub cyecleEnd=" + paystub.getCycleEndDate());
			log.debug("Found paystub issueDate=" + paystub.getIssueDate());
			log.debug("Found paystub dueDate=" + paystub.getDueDate());
			log.debug("Found paystub totalAmount=" + paystub.getTotalAmount());
			log.debug("Found paystub prev.Balance=" + paystub.getPreviousBalance());
			log.debug("Found paystub finalBalance=" + paystub.getFinalBalance());
			log.debug("Found paystub originalFinalBalance=" + paystub.getOriginalFinalBalance());
			log.debug("Found paystub carrierName=" + paystub.getCarrierName());
			log.debug("Found paystub carrierStreet=" + address.getAddressStreet());
			log.debug("Found paystub carrierStreetNumber=" + address.getAddressNumber());
			log.debug("Found paystub carrierZIP=" + address.getPostalCode());
			log.debug("Found paystub carrierCity=" + address.getCity());
			log.debug("Found paystub carrierState=" + address.getState());
			
		} else if (BGH_PAYSTUB_BARCODE_ELEMENT.equals(_localName)) {
			// must always have an Invoice
			if (!this.context.hasAttribute(InvoiceAndAccountHandler.OM_CURRENT_INVOICE)) {
				throw new SAXException("Cannot handle BarCode without current Invoice");
			}
			invoice = (Invoice)  this.context.getAttribute(InvoiceAndAccountHandler.OM_CURRENT_INVOICE);
			// handles barcode
			log.debug("Found barcode=" + _attributes.getValue(BGH_BARCODE_NUMBER));
			BarCode barcode = new BarCode();
			barcode.setTag(_attributes.getValue(TIMOMLoader.BGH_ELEMENT_KEY));
			barcode.setOCRLeftLine(_attributes.getValue(BGH_BARCODE_NUMBER));
			barcode.setOCRRightLine(_attributes.getValue(BGH_BARCODE_NUMBER));
			// saving barcode into current Invoice
			invoice.setBarCode(barcode);
			// debugging attributes
			log.debug("Found barcode=" + barcode.getOCRLeftLine());
		}
	}
}
