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

import java.util.LinkedList;
import java.util.List;

import br.com.auster.om.invoice.ReceiptDetail;

/**
 * 
 * @author framos
 * @version $Id$
 */
public class TIMReceiptDetail extends ReceiptDetail {

	
	private double promotionAmount;
	private List groupItems;
	private boolean fromBGH;
	
	
	
	public TIMReceiptDetail() {
		super();
		this.groupItems = new LinkedList();
		this.fromBGH = false;
	}
	
	public boolean isFromBGH() {
		return this.fromBGH;
	}
	
	public void setFromBGH(boolean _flag) {
		this.fromBGH = _flag;
	}
	
	public double getPromotionAmount() { 
		return this.promotionAmount;
	}
	
	public void setPromotionAmount(double _amount) {
		this.promotionAmount = _amount;
	}
	
	public List getReceiptGroupItems() {
		return this.groupItems;
	}
	
	public void setReceiptGroupItems(List _newList) {
		this.groupItems = _newList;
	}
	
	public void addGroupItem(ReceiptDetail _detail) {
		this.groupItems.add(_detail);
		_detail.setInvoice(this.getInvoice());
		_detail.setReceipt(this.getReceipt());
	}
}
