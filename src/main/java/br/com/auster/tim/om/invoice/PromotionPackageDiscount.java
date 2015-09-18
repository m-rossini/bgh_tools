/*
 * Copyright (c) 2004-2008 Auster Solutions. All Rights Reserved.
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
 * Created on 14/04/2008
 */
package br.com.auster.tim.om.invoice;

import br.com.auster.om.invoice.InvoiceModelObject;

/**
 * TODO What this class is responsible for
 *
 * @author William Soares
 * @version $Id$
 * @since JDK1.4
 */
public class PromotionPackageDiscount extends InvoiceModelObject {
	
	/** 
	 * Used to store the values of  <code>serialVersionUID</code>.
	 */
	private static final long serialVersionUID = 1L;

	
	private String discountModelDescription;
	
	private double discountRate;
	
	private double totalDiscountAmount;

	
	public String getDiscountModelDescription() {
		return discountModelDescription;
	}

	public void setDiscountModelDescription(String discountModelDescription) {
		this.discountModelDescription = discountModelDescription;
	}

	public double getDiscountRate() {
		return discountRate;
	}

	public void setDiscountRate(double discountRate) {
		this.discountRate = discountRate;
	}

	public double getTotalDiscountAmount() {
		return totalDiscountAmount;
	}

	public void setTotalDiscountAmount(double totalDiscountAmount) {
		this.totalDiscountAmount = totalDiscountAmount;
	}

	public int compareTo(Object o) {
		int ret = 0;
		if(o instanceof PromotionPackageDiscount) {
			PromotionPackageDiscount disc = (PromotionPackageDiscount) o;
			if(disc.getDiscountModelDescription() != null)
			{
				ret = this.getDiscountModelDescription().compareTo(disc.getDiscountModelDescription());
				if(ret == 0) {
					ret = (this.getDiscountRate() > disc.getDiscountRate()) ?  1 : -1;
				}
				if(ret == 0) {
					ret = (this.getTotalDiscountAmount() > disc.getTotalDiscountAmount()) ?  1 : -1;
				}
			} 
		} else {
			throw new ClassCastException("Can't compare an "+this.getClass().getName()+" to an "+o.getClass().getName());
		}
		return ret;
	}
	
}
