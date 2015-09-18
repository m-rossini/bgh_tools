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
 * Created on 11/08/2006
 */
package br.com.auster.tim.om.invoice;

import br.com.auster.om.invoice.Section;
import br.com.auster.om.util.UnitCounter;

/**
 * @author framos
 * @version $Id$
 *
 */
public class ContractUsageGroupSubsection extends Section {


	private long quantity;
	private UnitCounter usedVolume;
	private UnitCounter roundedVolume;
	private double freeUnitsDiscount;
	private double discount;



	public double getDiscountAmount() {
		return discount;
	}
	public void setDiscountAmount(double discount) {
		this.discount = discount;
	}

	public double getFreeUnitsDiscountAmount() {
		return freeUnitsDiscount;
	}
	public void setFreeUnitsDiscountAmount(double freeUnitsDiscount) {
		this.freeUnitsDiscount = freeUnitsDiscount;
	}

	public long getQuantity() {
		return quantity;
	}
	public void setQuantity(long quantity) {
		this.quantity = quantity;
	}

	public UnitCounter getRoundedVolume() {
		return roundedVolume;
	}
	public void setRoundedVolume(UnitCounter roundedVolume) {
		this.roundedVolume = roundedVolume;
	}

	public UnitCounter getUsedVolume() {
		return usedVolume;
	}
	public void setUsedVolume(UnitCounter usedVolume) {
		this.usedVolume = usedVolume;
	}

	public final String getContractNumber() {
		if (this.getParentSection() != null) {
			return ((ContractInfoSection)this.getParentSection()).getContractNumber();
		}
		return null;
	}

	public final String getAccessNumber() {
		if (this.getParentSection() != null) {
			return ((ContractInfoSection)this.getParentSection()).getAccessNbr();
		}
		return null;
	}

	public boolean isMobile() {
		if (this.getParentSection() != null) {
			return this.getParentSection().isMobile();
		}
		return super.isMobile();
	}
}
