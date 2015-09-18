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
 * Created on 03/08/2006
 */
package br.com.auster.tim.bgh.sax;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import br.com.auster.dware.sax.MultiHandlerReceiverBase;
import br.com.auster.om.invoice.ChargedTax;
import br.com.auster.om.invoice.Receipt;
import br.com.auster.om.util.ParserUtils;
import br.com.auster.tim.om.invoice.PromotionPackageDiscount;
import br.com.auster.tim.om.invoice.PromotionPackageReceiptDetail;
import br.com.auster.tim.om.invoice.TIMReceiptDetail;

/**
 * TODO What this class is responsible for
 *
 * @author William Soares
 * @version $Id$
 * @since JDK1.4
 */
public class PromotionPackageHandler extends MultiHandlerReceiverBase {

	// -------------------
	// Class constants
	// -------------------
	
	private static final Logger log = Logger.getLogger(PromotionPackageHandler.class);
	
	private static final String BGH_TIMNF_PROMOTIONPACKAGE_ELEMENT = "PromotionPackage";
	
	// group items attributes
	public static final String BGH_TIMNF_PROMOTIONPACKAGE_DESCRIPTION = "description";

	// group item discounts attributes
	public static final String BGH_TIMNF_PROMOTIONPACKAGE_DISCOUNTS_ELEMENT = "Discounts";
	public static final String BGH_TIMNF_PROMOTIONPACKAGE_DISCOUNTSDISCMODELDESC = "discountModelDescription";
	public static final String BGH_TIMNF_PROMOTIONPACKAGE_DISCOUNTSDISCRATE = "discountRate";
	public static final String BGH_TIMNF_PROMOTIONPACKAGE_DISCOUNTSTOTALDISCAMNT = "totalDiscountAmount";
	
	// group item taxes attributes
	public static final String BGH_TIMNF_PROMOTIONPACKAGE_DISCOUNTS_DISCOUNTTAXES_ELEMENT = "DiscountTaxes";
	public static final String BGH_TIMNF_PROMOTIONPACKAGE_DISCOUNTS_DISCOUNTTAXESTAXNAME = "taxName";
	public static final String BGH_TIMNF_PROMOTIONPACKAGE_DISCOUNTS_DISCOUNTTAXESTAXRATE = "taxRate";
	
	public static final String OM_PROMOTIONPACKAGE_CAPTION = "PromotionPackage";	
	
	public static final String OM_CURRENT_NF_PROMOTIONPACKAGE = "receipt.promotionpackage.current";
	public static final String OM_CURRENT_NF_PROMOTIONPACKAGEDETAIL = "receipt.promotionpackage.detail.current";
	
	
	// -------------------
	// Public methods
	// -------------------
	
	public void startElement(String _namespace, String _localName, String _qName, Attributes _attributes) throws SAXException {
		
		TIMReceiptDetail rd;
		PromotionPackageReceiptDetail pprd;
		if (BGH_TIMNF_PROMOTIONPACKAGE_ELEMENT.equals(_localName)) {
			
			// must have NF
			if (!this.context.hasAttribute(NFHeaderHandler.OM_CURRENT_NF)) {
				throw new SAXException("Cannot handle NF Promotion Package without current NF");
			}
			Receipt rcpt = (Receipt)  this.context.getAttribute(NFHeaderHandler.OM_CURRENT_NF);
			// creating group when found first Promotion Package
			if (this.context.hasAttribute(OM_CURRENT_NF_PROMOTIONPACKAGE)) {
				rd = (TIMReceiptDetail) this.context.getAttribute(OM_CURRENT_NF_PROMOTIONPACKAGE);
			} else {
				rd = new TIMReceiptDetail();
				rd.setCaption(OM_PROMOTIONPACKAGE_CAPTION);
				rd.setTag(_attributes.getValue(TIMOMLoader.BGH_ELEMENT_KEY));
				rcpt.addDetail(rd);
				this.context.setAttribute(OM_CURRENT_NF_PROMOTIONPACKAGE, rd);
				log.debug("Created Promotion Package Group");
			} 
			// handles Promotion Package detail
			pprd = new PromotionPackageReceiptDetail();
			pprd.setTag(_attributes.getValue(TIMOMLoader.BGH_ELEMENT_KEY));
			pprd.setCaption(_attributes.getValue(BGH_TIMNF_PROMOTIONPACKAGE_DESCRIPTION));
	
			// debugging attributes
			log.debug("Found NF Promotion Package caption=" + pprd.getCaption());
			// saves the new NF Promotion Package into the Context and in the current NF Detail
			this.context.setAttribute(OM_CURRENT_NF_PROMOTIONPACKAGEDETAIL, pprd);
			rd.addGroupItem(pprd);
			
		} else if (BGH_TIMNF_PROMOTIONPACKAGE_DISCOUNTS_ELEMENT.equals(_localName)) {
			// must have NF Promotion Package
			if (!this.context.hasAttribute(OM_CURRENT_NF_PROMOTIONPACKAGEDETAIL)) {
				throw new SAXException("Cannot handle NF Promotion Package Discount without current NF Promotion Package");
			}
			pprd = (PromotionPackageReceiptDetail)this.context.getAttribute(OM_CURRENT_NF_PROMOTIONPACKAGEDETAIL);
			// handles Promotion Package discount
			PromotionPackageDiscount promoPackDiscount = new PromotionPackageDiscount();
			promoPackDiscount.setTag(_attributes.getValue(TIMOMLoader.BGH_ELEMENT_KEY));
			promoPackDiscount.setDiscountModelDescription(_attributes.getValue(BGH_TIMNF_PROMOTIONPACKAGE_DISCOUNTSDISCMODELDESC));
			promoPackDiscount.setDiscountRate(ParserUtils.getDouble(_attributes.getValue(BGH_TIMNF_PROMOTIONPACKAGE_DISCOUNTSDISCRATE)));
			promoPackDiscount.setTotalDiscountAmount(ParserUtils.getDouble(_attributes.getValue(BGH_TIMNF_PROMOTIONPACKAGE_DISCOUNTSTOTALDISCAMNT)));
			// debugging attributes
			log.debug("Found NF Promotion Package Discount description=" + promoPackDiscount.getDiscountModelDescription());
			log.debug("Found NF Promotion Package Discount rate=" + promoPackDiscount.getDiscountRate());
			log.debug("Found NF Promotion Package Discount totalAmount=" + promoPackDiscount.getTotalDiscountAmount());
			// saves the new NF OCC Tax into the current NF OCC
			pprd.addPromotionPackageDiscountList(promoPackDiscount);
			
		} else if (BGH_TIMNF_PROMOTIONPACKAGE_DISCOUNTS_DISCOUNTTAXES_ELEMENT.equals(_localName)) {
			
			// must have NF Promotion Package
			if (!this.context.hasAttribute(OM_CURRENT_NF_PROMOTIONPACKAGEDETAIL)) {
				throw new SAXException("Cannot handle NF Promotion Package Tax without current NF Promotion Package");
			}
			pprd = (PromotionPackageReceiptDetail)this.context.getAttribute(OM_CURRENT_NF_PROMOTIONPACKAGEDETAIL);
			// handles Promotion Package tax
			ChargedTax tax = new ChargedTax();
			tax.setTag(_attributes.getValue(TIMOMLoader.BGH_ELEMENT_KEY));
			tax.setTaxName(_attributes.getValue(BGH_TIMNF_PROMOTIONPACKAGE_DISCOUNTS_DISCOUNTTAXESTAXNAME));
			tax.setTaxRate(ParserUtils.getDouble(_attributes.getValue(BGH_TIMNF_PROMOTIONPACKAGE_DISCOUNTS_DISCOUNTTAXESTAXRATE)));
			// debugging attributes
			log.debug("Found NF Promotion Package taxName=" + tax.getTaxName());
			log.debug("Found NF Promotion Package taxRate=" + tax.getTaxRate());
			// saves the new NF Promotion Package Tax into the current NF Promotion Package
			pprd.addTax(tax);

		}
	}

	
}
