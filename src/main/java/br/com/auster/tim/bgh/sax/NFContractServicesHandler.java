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
import br.com.auster.om.invoice.ChargedTax;
import br.com.auster.om.invoice.Receipt;
import br.com.auster.om.util.ParserUtils;
import br.com.auster.tim.om.invoice.ServiceReceiptDetail;
import br.com.auster.tim.om.invoice.TIMReceiptDetail;

/**
 * @author framos
 * @version $Id$
 *
 */
public class NFContractServicesHandler extends MultiHandlerReceiverBase {

	
	
	// -------------------
	// Class constants
	// -------------------
	
	private static final Logger log = Logger.getLogger(NFContractServicesHandler.class);
	// each Service detail for the current NF
	public static final String BGH_NF_SVC_ELEMENT = "Services";
	public static final String BGH_NF_SVC_NAME = "serviceName";
	public static final String BGH_NF_SVC_CONTRACTNUMBER = "contractNumber";
	public static final String BGH_NF_SVC_DIRNUMBER = "directoryNumber";
	public static final String BGH_NF_SVC_DAYS = "noOfDays";
	public static final String BGH_NF_SVC_PRORATE = "proRationFactor";
	public static final String BGH_NF_SVC_TOTALAMT = "grossAmount";
	public static final String BGH_NF_SVC_ANATELCODE = "codigoAnatel";
	public static final String BGH_NF_SVC_PACKAGE_NAME = "packageShortName";
	public static final String BGH_NF_SVC_RATEPLAN_SHDES = "rateplanShdes";
	
	// the SubTotal for all Services
	public static final String BGH_NF_SVC_SUBTOTAL_ELEMENT = "ServicesSubTotal";
	public static final String BGH_NF_SVC_SUBTOTAL_AMT = "subTotalGrossAmount";
	// Taxes information for the current Service
	public static final String BGH_NF_SVC_TAX_ELEMENT = "ServicesTaxes";
	public static final String BGH_NF_SVC_TAXNAME = "taxName";
	public static final String BGH_NF_SVC_TAXRATE = "taxRate";
	public static final String BGH_NF_SVC_TAXAMT = "taxAmount";
	
	public static final String OM_CURRENT_NF_SVCDETAIL_GROUP = "receipt.svcgroup.current";
	public static final String OM_CURRENT_NF_SVCDETAIL_DETAIL = "receipt.svcdetail.current";
	
	public static final String OM_CONTRACTSERVICES_GROUP_CAPTION = "ContractServices";
	

	// -------------------
	// Public methods
	// -------------------
	
	public void startElement(String _namespace, String _localName, String _qName, Attributes _attributes) throws SAXException {
		
		TIMReceiptDetail trd;
		ServiceReceiptDetail srd;
		if (BGH_NF_SVC_ELEMENT.equals(_localName)) {
			// must have NF
			if (!this.context.hasAttribute(NFHeaderHandler.OM_CURRENT_NF)) {
				throw new SAXException("Cannot handle NF ServiceDetail without current NF");
			}
			Receipt rcpt = (Receipt) this.context.getAttribute(NFHeaderHandler.OM_CURRENT_NF);
			// getting current ServiceDetail group, or creating if this is the first detail
			if (!this.context.hasAttribute(OM_CURRENT_NF_SVCDETAIL_GROUP)) {
				trd = new TIMReceiptDetail();
				rcpt.addDetail(trd);
				this.context.setAttribute(OM_CURRENT_NF_SVCDETAIL_GROUP, trd);
				log.debug("NF ServiceDetail group created");
			} else {
				trd = (TIMReceiptDetail) this.context.getAttribute(OM_CURRENT_NF_SVCDETAIL_GROUP);
			}
			srd = new ServiceReceiptDetail();
			srd.setTag(_attributes.getValue(TIMOMLoader.BGH_ELEMENT_KEY));
			srd.setCaption(_attributes.getValue(BGH_NF_SVC_NAME));
			srd.setServiceId(_attributes.getValue(BGH_NF_SVC_NAME));
			srd.setContractNumber(_attributes.getValue(BGH_NF_SVC_CONTRACTNUMBER));
			srd.setAccessNbr(_attributes.getValue(BGH_NF_SVC_DIRNUMBER));
			srd.setNumberOfDays(ParserUtils.getInt(_attributes.getValue(BGH_NF_SVC_DAYS)));
			srd.setProRateFactor(ParserUtils.getDouble(_attributes.getValue(BGH_NF_SVC_PRORATE)));
			srd.setTotalAmount(ParserUtils.getDouble(_attributes.getValue(BGH_NF_SVC_TOTALAMT)));
			srd.setAnatelCode(_attributes.getValue(BGH_NF_SVC_ANATELCODE));
			srd.setPackageShortName(_attributes.getValue(BGH_NF_SVC_PACKAGE_NAME));
			srd.setRateplanShdes(_attributes.getValue(BGH_NF_SVC_RATEPLAN_SHDES));
			// debugging attributes
			log.debug("Found receipt ServiceDetail id=" + srd.getServiceId());
			log.debug("Found receipt ServiceDetail contract=" + srd.getContractNumber());
			log.debug("Found receipt ServiceDetail dirNum=" + srd.getAccessNbr());
			log.debug("Found receipt ServiceDetail days=" + srd.getNumberOfDays());
			log.debug("Found receipt ServiceDetail proRate=" + srd.getProRateFactor());
			log.debug("Found receipt ServiceDetail total=" + srd.getTotalAmount());
			log.debug("Found receipt ServiceDetail anatelCode=" + srd.getAnatelCode());
			log.debug("Found receipt ServiceDetail packageShortname="+ srd.getPackageShortName());
			log.debug("Found receipt ServiceDetail rateplanShdes="+ srd.getRateplanShdes());
			// saves the new ServiceDetail info in the Context and the ServiceDetail group
			this.context.setAttribute(OM_CURRENT_NF_SVCDETAIL_DETAIL, srd);
			trd.addGroupItem(srd);
			
		} else if (BGH_NF_SVC_TAX_ELEMENT.equals(_localName)) {
			
			if (!this.context.hasAttribute(OM_CURRENT_NF_SVCDETAIL_DETAIL)) {
				throw new SAXException("Cannot handle NF ServiceDetail tax without NF ServiceDetail");
			}
			srd = (ServiceReceiptDetail) this.context.getAttribute(OM_CURRENT_NF_SVCDETAIL_DETAIL);
			// handles OCCItem tax
			ChargedTax tax = new ChargedTax();
			tax.setTag(_attributes.getValue(TIMOMLoader.BGH_ELEMENT_KEY));
			tax.setTaxName(_attributes.getValue(BGH_NF_SVC_TAXNAME));
			tax.setTaxRate(ParserUtils.getDouble(_attributes.getValue(BGH_NF_SVC_TAXRATE)));
			tax.setTaxAmount(ParserUtils.getDouble(_attributes.getValue(BGH_NF_SVC_TAXAMT)));
			// debugging attributes
			log.debug("Found NF ServiceDetail taxName=" + tax.getTaxName());
			log.debug("Found NF ServiceDetail taxRate=" + tax.getTaxRate());
			log.debug("Found NF ServiceDetail taxAmount=" + tax.getTaxAmount());
			// saves the new NF OCC Tax into the current NF OCC
			srd.addTax(tax);
			
		} else if (BGH_NF_SVC_SUBTOTAL_ELEMENT.equals(_localName)) {
			
			if (!this.context.hasAttribute(OM_CURRENT_NF_SVCDETAIL_GROUP)) {
				throw new SAXException("Cannot handle NF ServiceDetail subTotal without NF ServiceDetail group");
			}
			trd = (TIMReceiptDetail) this.context.removeAttribute(OM_CURRENT_NF_SVCDETAIL_GROUP);
			trd.setTotalAmount(ParserUtils.getDouble(_attributes.getValue(BGH_NF_SVC_SUBTOTAL_AMT)));
			trd.setCaption(OM_CONTRACTSERVICES_GROUP_CAPTION);
			trd.setFromBGH(true);
			// debugging attributes
			log.debug("Found NF ServiceDetail group total=" + trd.getTotalAmount());
		}
	}
	
	public void endElement(String _namespace, String _localName, String _uriName) throws SAXException {

		if (BGH_NF_SVC_ELEMENT.equals(_localName)) {
			// removing ServiceDetail since it has finished
			this.context.removeAttribute(OM_CURRENT_NF_SVCDETAIL_DETAIL);
		}
	}
}
