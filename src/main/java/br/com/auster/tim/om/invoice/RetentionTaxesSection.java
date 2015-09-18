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

import br.com.auster.om.invoice.ChargedTax;
import br.com.auster.om.invoice.Section;

import java.util.HashSet;
import java.util.Set;


/**
 * @author framos
 * @version $Id$
 *
 */
public class RetentionTaxesSection extends Section {


	private static final long serialVersionUID = 485290819197188388L;

	/**
	 * list of taxes retained
	 */
	private Set<ChargedTax> taxes;



	public RetentionTaxesSection() {
		super();
		taxes = new HashSet<ChargedTax>();
	}



	public final void setTaxes(Set<ChargedTax> _taxes) {
		if (_taxes != null) {
			taxes = _taxes;
		}
	}

	public final Set<ChargedTax> getTaxes() {
		 return this.taxes;
	}

}
