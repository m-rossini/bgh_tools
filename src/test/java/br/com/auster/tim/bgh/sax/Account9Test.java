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
import br.com.auster.om.invoice.ChargedTax;
import br.com.auster.om.invoice.Invoice;
import br.com.auster.om.invoice.Receipt;
import br.com.auster.tim.bgh.BGH2SAX;
import br.com.auster.tim.om.invoice.FreeUnitsReceiptDetail;
import br.com.auster.tim.om.invoice.GroupItemReceiptDetail;
import br.com.auster.tim.om.invoice.ServiceReceiptDetail;
import br.com.auster.tim.om.invoice.TIMReceiptDetail;

/**
 * @author framos
 * @version $Id$
 *
 */
public class Account9Test extends AccountTestBase {



	/**
	 * @see br.com.auster.tim.bgh.sax.AccountTestBase#validateInvoice(Invoice)
	 */
	protected void validateInvoice(Invoice _invoice) {
		super.validateInvoice(_invoice);
		// validates NFs
		assertNotNull(_invoice.getReceipts());
		assertEquals(1, _invoice.getReceipts().size());
		validateTIMReceipt((Receipt)_invoice.getReceipts().get(0));
	}

	protected void validateAccount(Account _account) {
		assertEquals("ROBERTO AKIO HAYASHI", _account.getAccountName());
		assertEquals("1.17567813", _account.getAccountNumber());
	}

	protected void validateTIMReceipt(Receipt _rcpt) {
		assertNotNull(_rcpt.getDetails());
		assertEquals(6, _rcpt.getDetails().size());
		int counter = 0;
		TIMReceiptDetail trd = (TIMReceiptDetail)_rcpt.getDetails().get(counter ++);
		assertNull(trd.getCaption());
		assertNotNull(trd.getReceiptGroupItems());
		assertEquals(2, trd.getReceiptGroupItems().size());
		trd =(TIMReceiptDetail) _rcpt.getDetails().get(counter ++);
		assertEquals("ContractServices", trd.getCaption());
		assertNotNull(trd.getReceiptGroupItems());
		assertEquals(1, trd.getReceiptGroupItems().size());
		trd =(TIMReceiptDetail) _rcpt.getDetails().get(counter ++);
		assertEquals("HomeCallsGroups", trd.getCaption());
		assertNotNull(trd.getReceiptGroupItems());
		assertEquals(6, trd.getReceiptGroupItems().size());

		// check each HomeCallGroup to check if amount is OK
		Iterator it = trd.getReceiptGroupItems().iterator();
		while (it.hasNext()) {
			GroupItemReceiptDetail grd = (GroupItemReceiptDetail) it.next();
			assertTrue("|9|4|73|".contains(grd.getEventCount()+""));
		}

		trd = (TIMReceiptDetail)_rcpt.getDetails().get(counter ++);
		assertEquals("OCCs", trd.getCaption());
		assertNotNull(trd.getReceiptGroupItems());
		assertEquals(2, trd.getReceiptGroupItems().size());
		trd = (TIMReceiptDetail) _rcpt.getDetails().get(counter++);
		assertEquals("Current FreeUnits", trd.getCaption());
		assertNotNull(trd.getReceiptGroupItems());
		assertEquals(3, trd.getReceiptGroupItems().size());
		trd = (TIMReceiptDetail) _rcpt.getDetails().get(counter ++);
		assertEquals("PromotionPackage", trd.getCaption());
		assertNotNull(trd.getReceiptGroupItems());
		assertEquals(1, trd.getReceiptGroupItems().size());
	}

	/**
	 * @see br.com.auster.tim.bgh.sax.AccountTestBase#getAccountFile()
	 */
	protected String getAccountFile() {
		return "examples/ACCOUNT9.BGH";
	}

}


