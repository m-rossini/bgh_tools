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
 * Created on 04/08/2006
 */
package br.com.auster.tim.bgh.sax;






import java.util.Iterator;

import br.com.auster.om.invoice.Account;
import br.com.auster.om.invoice.Invoice;
import br.com.auster.om.invoice.Receipt;
import br.com.auster.tim.om.invoice.GroupItemReceiptDetail;
import br.com.auster.tim.om.invoice.TIMReceiptDetail;

/**
 * @author framos
 * @version $Id$
 *
 */
public class Account10Test extends AccountTestBase {



	/**
	 * @see br.com.auster.tim.bgh.sax.AccountTestBase#validateInvoice(Invoice)
	 */
	protected void validateInvoice(Invoice _invoice) {
		super.validateInvoice(_invoice);
		// validates NFs
		assertNotNull(_invoice.getReceipts());
		assertEquals(2, _invoice.getReceipts().size());
		validateTIMReceipt((Receipt)_invoice.getReceipts().get(0));
		validateEmbratelReceipt((Receipt)_invoice.getReceipts().get(1));
	}

	protected void validateAccount(Account _account) {
		assertEquals("CEREALISTA MARANHÃO LTDA", _account.getAccountName());
		assertEquals("6.142865.10", _account.getAccountNumber());
	}

	protected void validateTIMReceipt(Receipt _rcpt) {
		assertNotNull(_rcpt.getDetails());

		Iterator it2 = _rcpt.getDetails().iterator();
		boolean found = false;
		while (it2.hasNext()) {
			TIMReceiptDetail trd = (TIMReceiptDetail) it2.next();
			if (! "HomeCallsGroups".equals(trd.getCaption())) {
				continue;
			}
			found = true;
			// check each HomeCallGroup to check if amount is OK
			Iterator it = trd.getReceiptGroupItems().iterator();
			while (it.hasNext()) {
				GroupItemReceiptDetail grd = (GroupItemReceiptDetail) it.next();
				assertTrue("|8|16|21|53|13|".contains(grd.getEventCount()+""));
			}
			break;
		}
		assertTrue(found);

	}

	protected void validateEmbratelReceipt(Receipt _rcpt) {
		assertNotNull(_rcpt.getDetails());

		TIMReceiptDetail trd = (TIMReceiptDetail) _rcpt.getDetails().get(0);
		// check each HomeCallGroup to check if amount is OK
		Iterator it = trd.getReceiptGroupItems().iterator();
		while (it.hasNext()) {
			GroupItemReceiptDetail grd = (GroupItemReceiptDetail) it.next();
			assertTrue("|10|".contains(grd.getEventCount()+""));
			assertTrue("|37.80|".contains(grd.getTotalAmount()+""));
		}

	}

	/**
	 * @see br.com.auster.tim.bgh.sax.AccountTestBase#getAccountFile()
	 */
	protected String getAccountFile() {
		return "examples/ACCOUNT10.BGH";
	}

}


