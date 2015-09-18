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
public class Account8Test extends AccountTestBase {



	/**
	 * @see br.com.auster.tim.bgh.sax.AccountTestBase#validateInvoice(Invoice)
	 */
	protected void validateInvoice(Invoice _invoice) {
		super.validateInvoice(_invoice);
		assertEquals("24/07/06", this.dateToString(_invoice.getCycleEndDate()));
		assertEquals("25/06/06", this.dateToString(_invoice.getCycleStartDate()));
		assertEquals("13/08/06", this.dateToString(_invoice.getDueDate()));
		assertEquals("01/08/06", this.dateToString(_invoice.getIssueDate()));
		assertEquals(1121.09, _invoice.getTotalAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(33.90, _invoice.getChargesAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(0, _invoice.getAdjustmentsAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);		
//		 validates Account
		validateAccount(_invoice.getAccount());
		// validates NFs
		assertNotNull(_invoice.getReceipts());
		assertEquals(4, _invoice.getReceipts().size());
		validateTIMReceipt((Receipt)_invoice.getReceipts().get(0));
		validateLDCEmbratel((Receipt)_invoice.getReceipts().get(1));
//		validateLDCBrT((Receipt)_invoice.getReceipts().get(2));
//		validateLDCTlemar((Receipt)_invoice.getReceipts().get(3));
		assertEquals(8, _invoice.getSections().size());
	}
	protected void validateAccount(Account _account) {
		assertEquals("LEALTA MONT E REF INDUSTRIAS LTDA", _account.getAccountName());
		assertEquals("6.121937", _account.getAccountNumber());
	}
	
	protected void validateTIMReceipt(Receipt _rcpt) {
		assertNotNull(_rcpt.getDetails());
		assertEquals(7, _rcpt.getDetails().size());
		int counter = 0;
		TIMReceiptDetail trd = (TIMReceiptDetail)_rcpt.getDetails().get(counter ++);
		assertEquals("Subscriptions", trd.getCaption());
		assertNotNull(trd.getReceiptGroupItems());
		assertEquals(1, trd.getReceiptGroupItems().size());
		trd =(TIMReceiptDetail) _rcpt.getDetails().get(counter ++);
		assertEquals("ContractServices", trd.getCaption());
		assertNotNull(trd.getReceiptGroupItems());
		assertEquals(11, trd.getReceiptGroupItems().size());
		trd =(TIMReceiptDetail) _rcpt.getDetails().get(counter ++);
		assertEquals("HomeCallsGroups", trd.getCaption());
		assertNotNull(trd.getReceiptGroupItems());
		assertEquals(6, trd.getReceiptGroupItems().size());
		trd = (TIMReceiptDetail) _rcpt.getDetails().get(counter ++);
		assertEquals("ServiceUsageGroups", trd.getCaption());
		assertNotNull(trd.getReceiptGroupItems());
		assertEquals(2, trd.getReceiptGroupItems().size());
		trd = (TIMReceiptDetail)_rcpt.getDetails().get(counter ++);
		assertEquals("OCCs", trd.getCaption());
		assertNotNull(trd.getReceiptGroupItems());
		assertEquals(2, trd.getReceiptGroupItems().size());
		trd = (TIMReceiptDetail) _rcpt.getDetails().get(counter++);
		assertEquals("Current FreeUnits", trd.getCaption());
		assertNotNull(trd.getReceiptGroupItems());
		assertEquals(2, trd.getReceiptGroupItems().size());
	}

	protected void validateLDCEmbratel(Receipt _rcpt) {
		assertNotNull(_rcpt.getDetails());
		assertEquals(2, _rcpt.getDetails().size());
		int counter = 1;
		TIMReceiptDetail trd = (TIMReceiptDetail)_rcpt.getDetails().get(counter ++);
		assertEquals("OCCs", trd.getCaption());
		assertNotNull(trd.getReceiptGroupItems());
		assertEquals(2, trd.getReceiptGroupItems().size());
	}	

		/**
	 * @see br.com.auster.tim.bgh.sax.AccountTestBase#getAccountFile()
	 */
	protected String getAccountFile() {
		return "examples/ACCOUNT8.BGH";
	}

}


