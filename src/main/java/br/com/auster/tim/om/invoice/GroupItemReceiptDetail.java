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
package br.com.auster.tim.om.invoice;

import br.com.auster.om.invoice.ReceiptDetail;


/**
 * @author framos
 * @version $Id$
 *
 */
public class GroupItemReceiptDetail extends ReceiptDetail {

	
	private double promotionAmount;
	private String areaIndicator;
	private long eventCount;	
	private boolean occ;
	
	
	public GroupItemReceiptDetail() {
		super();
		this.occ = false;
	}
	
	public String getAreaIndicator() {
		return this.areaIndicator;
	}
	
	public void setAreaInidicator(String _area) {
		this.areaIndicator = _area;
	}
	
	public long getEventCount() {
		return this.eventCount;
	}
	
	public void setEventCount(long _count) {
		this.eventCount = _count;
	}
	
	public double getPromotionAmount() { 
		return this.promotionAmount;
	}
	
	public void setPromotionAmount(double _amount) {
		this.promotionAmount = _amount;
	}
	
	public boolean isOCC() {
		return this.occ;
	}
	
	public void setOCC(boolean _isOCC) {
		this.occ = _isOCC;
	}
}
