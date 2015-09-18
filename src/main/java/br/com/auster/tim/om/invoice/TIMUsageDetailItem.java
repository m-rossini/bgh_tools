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

import java.lang.ref.WeakReference;
import java.util.Date;

import br.com.auster.om.invoice.InvoiceModelObject;
import br.com.auster.om.util.UnitCounter;

/**
 *
 * @author framos
 * @version $Id$
 */
public abstract class TIMUsageDetailItem extends InvoiceModelObject {



	protected WeakReference<TIMUsageDetail> usage;


	public TIMUsageDetailItem(TIMUsageDetail _detail) {
		this.usage = new WeakReference<TIMUsageDetail>(_detail);
	}

	public final Date getUsageDatetime() { return this.usage.get().getDatetime();	}
	public final UnitCounter getUsageRoundedDuration() { return this.usage.get().getRoundedDuration(); }
	public final double getUsageTotalAmount() { return this.usage.get().getTotalAmount(); }
	public final String getUsageChannelId() { return this.usage.get().getChannelId(); }
	public final double getUsageAmountBeforePromotions() { return this.usage.get().getAmountBeforePromotions(); }
	public final double getUsageDiscountAmount() { return this.usage.get().getDiscountAmount(); }
}