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
public class TIMNFSubscriptionHandler extends MultiHandlerReceiverBase {

	
	
	// -------------------
	// Class constants
	// -------------------
	
	private static final Logger log = Logger.getLogger(TIMNFSubscriptionHandler.class);
			
	public static final String BGH_TIMNF_SVC_SUMMARY_SUBSC_ELEMENT = "Subscription";
	public static final String BGH_TIMNF_SVC_SUMMARY_SUBSC_SVCID = "ratePlan";
	public static final String BGH_TIMNF_SVC_SUMMARY_SUBSC_TAX_ELEMENT = "SubscriptionTaxes";
	public static final String BGH_TIMNF_SVC_SUMMARY_SUBSC_TOTAL_ELEMENT = "SubscriptionSubTotal";
	// named the same for both Subscription 
	public static final String BGH_TIMNF_SVC_SUBSC_UNITS = "noOfContracts";
	public static final String BGH_TIMNF_SVC_SUBSC_DAYS = "noOfDays";
	public static final String BGH_TIMNF_SVC_SUBSC_PRORATE_FACTOR = "proRationFactor";
	public static final String BGH_TIMNF_SVC_SUBSC_TOTALAMT = "grossAmount";
	public static final String BGH_TIMNF_SVC_SUBSC_ANATELCODE = "codigoAnatel";
	public static final String BGH_TIMNF_SVC_SUBSC_RATEPLAN_SHDES = "rateplanShdes";
	// named the same for both Subscription total amount
	public static final String BGH_TIMNF_SVC_SUMMARY_TOTALAMT = "subtotalGrossAmount";
	// named the same for both Subscription taxes
	public static final String BGH_TIMNF_SVC_SUMMARY_TAXNAME = "taxName";
	public static final String BGH_TIMNF_SVC_SUMMARY_TAXRATE = "taxRate";
	public static final String BGH_TIMNF_SVC_SUMMARY_TAXAMT = "taxAmount";
	
	public static final String OM_CURRENT_TIMNF_SUBSCRIPTION_GROUP = "receipt.subscription.group.current";
	public static final String OM_CURRENT_TIMNF_SUBSCRIPTION_DETAIL = "receipt.subscription.detail.current";
	
	public static final String OM_SUBSCRIPTIONS_GROUP_CAPTION = "Subscriptions";
	
	

	// -------------------
	// Public methods
	// -------------------
	
	public void startElement(String _namespace, String _localName, String _qName, Attributes _attributes) throws SAXException {
		
		ServiceReceiptDetail srd;
		TIMReceiptDetail trd;
		if (BGH_TIMNF_SVC_SUMMARY_SUBSC_ELEMENT.equals(_localName)) {
			
			if (!this.context.hasAttribute(NFHeaderHandler.OM_CURRENT_NF)) {
				throw new SAXException("Cannot handle NF detail without current TIM NF");
			}
			Receipt rcpt = (Receipt) this.context.getAttribute(NFHeaderHandler.OM_CURRENT_NF);
			// getting current Subscription group, or creating if this is the first detail
			if (!this.context.hasAttribute(OM_CURRENT_TIMNF_SUBSCRIPTION_GROUP)) {
				trd = new TIMReceiptDetail();
				rcpt.addDetail(trd);
				this.context.setAttribute(OM_CURRENT_TIMNF_SUBSCRIPTION_GROUP, trd);
				log.debug("NF ServiceDetail group created");
			} else {
				trd = (TIMReceiptDetail) this.context.getAttribute(OM_CURRENT_TIMNF_SUBSCRIPTION_GROUP);
			}			
			// handles details
			srd = new ServiceReceiptDetail();
			srd.setSubscription(true);
			srd.setTag(_attributes.getValue(TIMOMLoader.BGH_ELEMENT_KEY));
			srd.setServiceId(_attributes.getValue(BGH_TIMNF_SVC_SUMMARY_SUBSC_SVCID));
			srd.setUnitCount(_attributes.getValue(BGH_TIMNF_SVC_SUBSC_UNITS));
			srd.setNumberOfDays(ParserUtils.getInt(_attributes.getValue(BGH_TIMNF_SVC_SUBSC_DAYS)));
			srd.setProRateFactor(ParserUtils.getDouble(_attributes.getValue(BGH_TIMNF_SVC_SUBSC_PRORATE_FACTOR)));
			srd.setTotalAmount(ParserUtils.getDouble(_attributes.getValue(BGH_TIMNF_SVC_SUBSC_TOTALAMT)));
			srd.setAnatelCode(_attributes.getValue(BGH_TIMNF_SVC_SUBSC_ANATELCODE));
			srd.setRateplanShdes(_attributes.getValue(BGH_TIMNF_SVC_SUBSC_RATEPLAN_SHDES));
			// saves the new NF Detail into the Context and in the current NF
			this.context.setAttribute(OM_CURRENT_TIMNF_SUBSCRIPTION_DETAIL, srd);
			trd.addGroupItem(srd);
			// debugging attributes
			log.debug("Found receipt subscription id=" + srd.getServiceId());
			log.debug("Found receipt subscription units=" + srd.getUnitCount());
			log.debug("Found receipt subscription days=" + srd.getNumberOfDays());
			log.debug("Found receipt subscription proRate=" + srd.getProRateFactor());
			log.debug("Found receipt subscription total=" + srd.getTotalAmount());
			log.debug("Found receipt subscription anatelCode=" + srd.getAnatelCode());
			log.debug("Found receipt subscription rateplanShdes=" + srd.getRateplanShdes());
			
		} else if (BGH_TIMNF_SVC_SUMMARY_SUBSC_TAX_ELEMENT.equals(_localName)) {
			
			if (!this.context.hasAttribute(OM_CURRENT_TIMNF_SUBSCRIPTION_DETAIL)) {
				throw new SAXException("Cannot handle NF detail total without current NF detail");
			}
			srd = (ServiceReceiptDetail) this.context.getAttribute(OM_CURRENT_TIMNF_SUBSCRIPTION_DETAIL);
			ChargedTax tax = new ChargedTax();
			tax.setTag(_attributes.getValue(TIMOMLoader.BGH_ELEMENT_KEY));
			tax.setTaxName(_attributes.getValue(BGH_TIMNF_SVC_SUMMARY_TAXNAME));
			tax.setTaxRate(ParserUtils.getDouble(_attributes.getValue(BGH_TIMNF_SVC_SUMMARY_TAXRATE)));
			tax.setTaxAmount(ParserUtils.getDouble(_attributes.getValue(BGH_TIMNF_SVC_SUMMARY_TAXAMT)));
			srd.addTax(tax);
			// debugging attributes
			log.debug("Found receipt subscription taxName=" + tax.getTaxName());
			log.debug("Found receipt subscription taxRate=" + tax.getTaxRate());
			log.debug("Found receipt subscription taxAmount=" + tax.getTaxAmount());
			
		} else if (BGH_TIMNF_SVC_SUMMARY_SUBSC_TOTAL_ELEMENT.equals(_localName)) {
			
			if (!this.context.hasAttribute(OM_CURRENT_TIMNF_SUBSCRIPTION_GROUP)) {
				throw new SAXException("Cannot handle NF Subscription subTotal without NF Subscription group");
			}
			trd = (TIMReceiptDetail) this.context.removeAttribute(OM_CURRENT_TIMNF_SUBSCRIPTION_GROUP);
			trd.setTotalAmount(ParserUtils.getDouble(_attributes.getValue(BGH_TIMNF_SVC_SUMMARY_TOTALAMT)));
			trd.setCaption(OM_SUBSCRIPTIONS_GROUP_CAPTION);
			trd.setFromBGH(true);
			// debugging attributes
			log.debug("Found receipt subscription totalAmount=" + trd.getTotalAmount());
		}
	}
	
	
	public void endElement(String _namespace, String _localName, String _uriName) throws SAXException {

		if (BGH_TIMNF_SVC_SUMMARY_SUBSC_ELEMENT.equals(_localName)) {
			// removing ServiceDetail since it has finished
			this.context.removeAttribute(OM_CURRENT_TIMNF_SUBSCRIPTION_DETAIL);
		}
	}	
}
