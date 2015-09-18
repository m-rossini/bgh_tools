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
import br.com.auster.om.invoice.Address;
import br.com.auster.om.invoice.Identity;
import br.com.auster.om.invoice.Invoice;
import br.com.auster.om.invoice.Receipt;
import br.com.auster.om.util.ParserUtils;
import br.com.auster.tim.om.invoice.TIMReceiptDetail;

/**
 * @author framos
 * @version $Id$
 *
 */
public class NFHeaderHandler extends MultiHandlerReceiverBase {



	// -------------------
	// Class constants
	// -------------------

	private static final Logger log = Logger.getLogger(NFHeaderHandler.class);
	// NF for TIM and LDC
	public static final String BGH_TIMNF_ELEMENT = "NFHeader";
	public static final String BGH_LDNF_ELEMENT = "LDCNFHeader";
	// NF attributes
	public static final String BGH_NF_NUMBER = "invoiceNumber";
	public static final String BGH_NF_CARRIERSTATE = "carrierAddrState";
	public static final String BGH_NF_CARRIERNAME = "carrierAddrName";
	// address and indetities
	public static final String BGH_NF_ADDRESSS_STREET = "carrierAddrStreet";
	public static final String BGH_NF_ADDRESSS_NBR = "carrierAddrStreetNo";
	public static final String BGH_NF_ADDRESSS_ZIP = "carrierAddrZip";
	public static final String BGH_NF_ADDRESSS_CITY = "carrierAddrCity";
	public static final String BGH_NF_ADDRESSS_CNPJ = "carrierCNPJ";
	public static final String BGH_NF_ADDRESSS_IE = "carrierIE";

	// Digital Authentication Key
	public static final String BGH_NF_DIGITAL_AUTH_KEY = "DigitalAuthenticationKey";
	public static final String BGH_LDNF_DIGITAL_AUTH_KEY = "DigitalAuthKey";
	public static final String BGH_NF_DIGITAL_AUTH_KEY_VALUE = "digitalAuthenticationKey";
	public static final String BGH_LDNF_DIGITAL_AUTH_KEY_VALUE = "digitalAuthKey";

	// CNPJ/CPF of client associated with receipt invoice
	public static final String BGH_NF_CNPJ_CPF = "cnpjCPF";

	// NF total amount
	public static final String BGH_TIMNF_TOTAL_ELEMENT = "TIMTotalAmount";
	public static final String BGH_LDNF_TOTAL_ELEMENT = "TotalLDCAmount";
	// NF total attributes
	public static final String BGH_TIMNF_TOTALAMT = "invoiceAmountWithoutCoBilling";
	public static final String BGH_LDNF_TOTALAMT = "totalLDCGrossAmount";

	public static final String OM_CURRENT_NF = "receipt.current";

	public static final String BGH_LDC_NF_ID = "34100000";
	public static final String BGH_LDC_USAGEGROUP_CAPTION = "UsageGroup";



	// -------------------
	// Public methods
	// -------------------

	public void startElement(String _namespace, String _localName, String _qName, Attributes _attributes) throws SAXException {

		Receipt rcpt;
		if (BGH_TIMNF_ELEMENT.equals(_localName) || BGH_LDNF_ELEMENT.equals(_localName)) {
			if (!this.context.hasAttribute(InvoiceAndAccountHandler.OM_CURRENT_INVOICE)) {
				throw new SAXException("Cannot handle NF without current Invoice");
			}
			Invoice invoice = (Invoice)  this.context.getAttribute(InvoiceAndAccountHandler.OM_CURRENT_INVOICE);
			// handles NF
			rcpt = new Receipt();
			rcpt.setTag(_attributes.getValue(TIMOMLoader.BGH_ELEMENT_KEY));
			rcpt.setReceiptNbr(_attributes.getValue(BGH_NF_NUMBER));
			rcpt.setCarrierName(_attributes.getValue(BGH_NF_CARRIERNAME));
			rcpt.setCarrierState(_attributes.getValue(BGH_NF_CARRIERSTATE));
			//rcpt.setCarrierCode(getCarrierCode(rcpt.getCarrierName()));
			// checking if its LDC NF
			rcpt.setLocal(BGH_TIMNF_ELEMENT.equals(_localName));
			// if this is the TIM NF, then complement Invoice & Account
			//   and set the carrierCode from the Account OM
			if (BGH_TIMNF_ELEMENT.equals(_localName)) {
				complementInvoiceAndAccount(_attributes);
			} else {
				// created here since LDC has no UsageGroup grouper section
				TIMReceiptDetail rd = new TIMReceiptDetail();
				rd.setTag(_attributes.getValue(TIMOMLoader.BGH_ELEMENT_KEY));
				rd.setCaption(BGH_LDC_USAGEGROUP_CAPTION);
				rcpt.addDetail(rd);
				this.context.setAttribute(NFGroupAndItemsHandler.OM_CURRENT_NF_GROUP, rd);
			}
			// getting address
			Address addr = new Address();
			addr.setAddressStreet(_attributes.getValue(BGH_NF_ADDRESSS_STREET));
			addr.setAddressNumber(_attributes.getValue(BGH_NF_ADDRESSS_NBR));
			addr.setPostalCode(_attributes.getValue(BGH_NF_ADDRESSS_ZIP));
			addr.setCity(_attributes.getValue(BGH_NF_ADDRESSS_CITY));
			rcpt.setAddress(addr);
			// getting identities
			Identity id = new Identity();
			id.setIdentityType(BGH_NF_ADDRESSS_CNPJ);
			id.setIdentityAttrib1(_attributes.getValue(BGH_NF_ADDRESSS_CNPJ));
			rcpt.setCNPJ(id);
			id = new Identity();
			id.setIdentityType(BGH_NF_ADDRESSS_IE);
			id.setIdentityAttrib1(_attributes.getValue(BGH_NF_ADDRESSS_IE));
			rcpt.setIE(id);

			// handles CNPJ/CPF of client
			// TODO Check if this is OK
			// this attribute actually belongs to invoice, instead of receipt
			String clientCnpjCpf = _attributes.getValue(BGH_NF_CNPJ_CPF);
			if( clientCnpjCpf != null ) {
				invoice.setCnpjCpf( clientCnpjCpf );
				log.debug("Found invoice cnpjCPF=" + invoice.getCnpjCpf());
			}

			// saves the new NF into the Context and in the current Invoice
			this.context.setAttribute(OM_CURRENT_NF, rcpt);
			invoice.addReceipt(rcpt);
			// debugging attributes
			log.debug("Found receipt number=" + rcpt.getReceiptNbr());
			log.debug("Found receipt carrierCode=" + rcpt.getCarrierCode());
			log.debug("Found receipt carrierName=" + rcpt.getCarrierName());
			log.debug("Found receipt carrierState=" + rcpt.getCarrierState());
			log.debug("Found receipt address=" + rcpt.getAddress().getAddressStreet());
			log.debug("Found receipt address number=" + rcpt.getAddress().getAddressNumber());
			log.debug("Found receipt address city=" + rcpt.getAddress().getCity());
			log.debug("Found receipt address zip=" + rcpt.getAddress().getPostalCode());
			log.debug("Found receipt cnpj=" + rcpt.getCNPJ().getIdentityAttrib1());
			log.debug("Found receipt ie=" + rcpt.getIE().getIdentityAttrib1());

		} else if (BGH_TIMNF_TOTAL_ELEMENT.equals(_localName)) {
			if (!this.context.hasAttribute(OM_CURRENT_NF)) {
				throw new SAXException("Cannot handle TIM NF total without current NF");
			}
			rcpt = (Receipt) this.context.getAttribute(OM_CURRENT_NF);
			rcpt.setTotalAmount(ParserUtils.getDouble(_attributes.getValue(BGH_TIMNF_TOTALAMT)));
			// debugging attributes
			log.debug("Found receipt totalAmount=" + rcpt.getTotalAmount());

		} else if (BGH_NF_DIGITAL_AUTH_KEY.equals(_localName)) {
			if (!this.context.hasAttribute(OM_CURRENT_NF)) {
				throw new SAXException("Cannot handle Digital Authentication Key without current NF");
			}
			rcpt = (Receipt) this.context.getAttribute(OM_CURRENT_NF);
			rcpt.setDigitalAuthKey(_attributes.getValue(BGH_NF_DIGITAL_AUTH_KEY_VALUE));
			// debugging attributes
			log.debug("Found receipt digitalAuthKey=" + rcpt.getDigitalAuthKey());

		} else if (BGH_LDNF_DIGITAL_AUTH_KEY.equals(_localName)) {
			if (!this.context.hasAttribute(OM_CURRENT_NF)) {
				throw new SAXException("Cannot handle LD Digital Authentication Key without current NF");
			}
			rcpt = (Receipt) this.context.getAttribute(OM_CURRENT_NF);
			rcpt.setDigitalAuthKey(_attributes.getValue(BGH_LDNF_DIGITAL_AUTH_KEY_VALUE));
			// debugging attributes
			log.debug("Found receipt digitalAuthKey=" + rcpt.getDigitalAuthKey());

		} else if (BGH_LDNF_TOTAL_ELEMENT.equals(_localName)) {
			if (!this.context.hasAttribute(OM_CURRENT_NF)) {
				throw new SAXException("Cannot handle LDC NF total without current NF");
			}
			rcpt = (Receipt) this.context.getAttribute(OM_CURRENT_NF);
			rcpt.setTotalAmount(ParserUtils.getDouble(_attributes.getValue(BGH_LDNF_TOTALAMT)));
			// added when LDC NF was created
			this.context.removeAttribute(NFGroupAndItemsHandler.OM_CURRENT_NF_GROUP);
			// debugging attributes
			log.debug("Found receipt totalAmount=" + rcpt.getTotalAmount());
		}
	}

	public void endElement(String _namespace, String _localName, String _uriName) throws SAXException {

		if (BGH_TIMNF_ELEMENT.equals(_localName) || BGH_LDNF_ELEMENT.equals(_localName)) {
			this.context.removeAttribute(OM_CURRENT_NF);
			this.context.removeAttribute(NFOCCGroupHandler.OM_CURRENT_NF_OCCGROUP);
		}
	}



	// -------------------
	// Private methods
	// -------------------

	private void complementInvoiceAndAccount(Attributes _attributes) throws SAXException {
		// should only get info from NFHeader if not HEADER was found
		if (this.context.hasAttribute(InvoiceAndAccountHandler.FOUND_HEADER)) {
			return;
		}
		AccountInfoFromHeadersHelper helper = new AccountInfoFromHeadersHelper();
		helper.complementInfo(this.context, _attributes);
	}

	// TODO rever os códigos
	// comentado pois esta parte foi externalizada como parametrização.
//	private String getCarrierCode(String _carrierName) {
//		if (_carrierName.toUpperCase().startsWith("EMBRATEL")) {
//			return "21";
//		} else if (_carrierName.toUpperCase().startsWith("INTELIG")) {
//			return "23";
//		} else if (_carrierName.toUpperCase().startsWith("TIM ")) {
//			return "41";
//		} else if (_carrierName.toUpperCase().startsWith("TELEMAR")) {
//			return "31";
//		} else if (_carrierName.toUpperCase().startsWith("TNL PCS")) {
//			return "97";
//		} else if (_carrierName.toUpperCase().indexOf("TELESP") >= 0) {
//			return "15";
//		} else if (_carrierName.toUpperCase().startsWith("CIA DE TELECOM")) {
//			return "12";
//		} else if (_carrierName.toUpperCase().startsWith("BRASIL TELECOM")) {
//			return "14";
//		} else if (_carrierName.toUpperCase().startsWith("GVT")) {
//			return "25";
//		} else if (_carrierName.toUpperCase().startsWith("MAXITEL")) {
//			return "41";
//		} else if (_carrierName.toUpperCase().startsWith("OI")) {
//			return "32";
//		} else if (_carrierName.toUpperCase().startsWith("CLARO")) {
//			return "36";
//		}
//		return "00";
//	}
}
