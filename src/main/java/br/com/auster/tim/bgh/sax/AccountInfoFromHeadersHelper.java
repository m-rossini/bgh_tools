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

import br.com.auster.dware.sax.MultiHandlerContext;
import br.com.auster.om.invoice.Account;
import br.com.auster.om.invoice.Address;
import br.com.auster.om.invoice.Invoice;
import br.com.auster.om.util.ParserUtils;
import br.com.auster.tim.om.invoice.TIMAccount;

/**
 * @author framos
 * @version $Id$
 *
 */
public final class AccountInfoFromHeadersHelper {

	
	// -------------------
	// Class constants
	// -------------------

	private static final Logger log = Logger.getLogger(AccountInfoFromHeadersHelper.class);
	
	public static final String BGH_HEADER_CYCLESTART = "billingPeriodStart";
	public static final String BGH_HEADER_CYCLEEND = "billingPeriodEnd";
	public static final String BGH_HEADER_ISSUEDATE = "invoiceDate";
	public static final String BGH_HEADER_DUEDATE = "dueDate";	
	public static final String BGH_HEADER_TOTALAMT = "totalBillAmount";
	public static final String BGH_HEADER_ACCNAME = "custAddrName";
	public static final String BGH_HEADER_ACCSTATE = "custAddrState";
	public static final String BGH_HEADER_ACCNUMBER = "custCode";
	
	public static final String BGH_HEADER_ADDRESSS_STREET = "carrierAddrStreet";
	public static final String BGH_HEADER_ADDRESSS_NBR = "carrierAddrStreetNo";
	public static final String BGH_HEADER_ADDRESSS_ZIP = "carrierAddrZip";
	public static final String BGH_HEADER_ADDRESSS_CITY = "carrierAddrCity";
	public static final String BGH_HEADER_ADDRESSS_STATE = "carrierAddrState";
	public static final String BGH_HEADER_CARRIER_NAME = "carrierAddrName";
	

	
	// -------------------
	// Public methods
	// -------------------
	
	public void complementInfo(MultiHandlerContext _context, Attributes _attributes) throws SAXException {
		Invoice invoice = (Invoice) _context.getAttribute(InvoiceAndAccountHandler.OM_CURRENT_INVOICE);
		Account account = (Account) _context.getAttribute(InvoiceAndAccountHandler.OM_CURRENT_ACCOUNT);
		if ((invoice == null) || (account == null)) {
			throw new SAXException("BGH file not well-formed. No Invoice/Account found.");
		}
		// complementing Invoice information
		invoice.setCycleStartDate(BGHDataFormats.getDate(_attributes.getValue(BGH_HEADER_CYCLESTART)));
		invoice.setCycleEndDate(BGHDataFormats.getDate(_attributes.getValue(BGH_HEADER_CYCLEEND)));
		invoice.setIssueDate(BGHDataFormats.getDate(_attributes.getValue(BGH_HEADER_ISSUEDATE)));
		invoice.setOriginalCycleStartDate(_attributes.getValue(BGH_HEADER_CYCLESTART));
		invoice.setOriginalCycleEndDate(_attributes.getValue(BGH_HEADER_CYCLEEND));
		invoice.setOriginalIssueDate(_attributes.getValue(BGH_HEADER_ISSUEDATE));
		invoice.setTotalAmount(ParserUtils.getDouble(_attributes.getValue(BGH_HEADER_TOTALAMT)));
		// complementing Account information
		account.setAccountName(_attributes.getValue(BGH_HEADER_ACCNAME));
		account.setAccountState(_attributes.getValue(BGH_HEADER_ACCSTATE));
		account.setCarrierName(_attributes.getValue(BGH_HEADER_CARRIER_NAME));
		account.setTag(_attributes.getValue(TIMOMLoader.BGH_ELEMENT_KEY));
		// setting Account address
		Address addr = new Address();
		addr.setAddressStreet(_attributes.getValue(BGH_HEADER_ADDRESSS_STREET));
		addr.setAddressNumber(_attributes.getValue(BGH_HEADER_ADDRESSS_NBR));
		addr.setPostalCode(_attributes.getValue(BGH_HEADER_ADDRESSS_ZIP));
		addr.setCity(_attributes.getValue(BGH_HEADER_ADDRESSS_CITY));
		addr.setState(_attributes.getValue(BGH_HEADER_ADDRESSS_STATE));
		((TIMAccount)account).setCarrierAddress(addr);
		// debugging attributes
		log.debug("Found invoice cycle startDate=" + invoice.getCycleStartDate());
		log.debug("Found invoice cycle endDate=" + invoice.getCycleEndDate());
		log.debug("Found invoice issueDate=" + invoice.getIssueDate());
		log.debug("Found invoice totalAmount=" + invoice.getTotalAmount());
		log.debug("Found account name=" + account.getAccountName());
		log.debug("Found account state=" + account.getAccountState());
		log.debug("Found account carrier name=" + account.getCarrierName());
		log.debug("Found receipt address=" + ((TIMAccount)account).getCarrierAddress().getAddressStreet());
		log.debug("Found receipt address number=" +  ((TIMAccount)account).getCarrierAddress().getAddressNumber());
		log.debug("Found receipt address city=" +  ((TIMAccount)account).getCarrierAddress().getCity());
		log.debug("Found receipt address state=" +  ((TIMAccount)account).getCarrierAddress().getState());
		log.debug("Found receipt address zip=" +  ((TIMAccount)account).getCarrierAddress().getPostalCode());
	}	
}
