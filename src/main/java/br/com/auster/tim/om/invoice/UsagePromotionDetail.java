/*
 * Copyright (c) 2004-2007 Auster Solutions. All Rights Reserved.
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
 * Created on 19/09/2007
 */
package br.com.auster.tim.om.invoice;



/**
 * This class handles each promotion part of a usage detail (voice, data or message).
 *
 * @author framos
 * @version $Id$
 */
public class UsagePromotionDetail extends TIMUsageDetailItem {


	private String promotionType;
	private String promotionPackage;
	private String promotionModel;
	private double amount;


	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object _other) { return (this.hashCode() - _other.hashCode());	}


	/**
	 * Getters and Setters
	 */
	public UsagePromotionDetail(TIMUsageDetail _detail) {
		super(_detail);
	}

	public final String getPromotionType() { return promotionType; }
	public final void setPromotionType(String promotionType) { this.promotionType = promotionType; }

	public final String getPromotionPackage() { return promotionPackage; }
	public final void setPromotionPackage(String promotionPackage) { this.promotionPackage = promotionPackage; }

	public final String getPromotionModel() { return promotionModel; }
	public final void setPromotionModel(String promotionModel) { this.promotionModel = promotionModel; }

	public final double getAmount() { return amount; }
	public final void setAmount(double amount) { this.amount = amount;	}

}
