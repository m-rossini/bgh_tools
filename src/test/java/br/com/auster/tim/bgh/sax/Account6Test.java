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
public class Account6Test extends AccountTestBase {



	/**
	 * @see br.com.auster.tim.bgh.sax.AccountTestBase#validateInvoice(Invoice)
	 */
	protected void validateInvoice(Invoice _invoice) {
		super.validateInvoice(_invoice);
		assertEquals("24/07/06", this.dateToString(_invoice.getCycleEndDate()));
		assertEquals("25/06/06", this.dateToString(_invoice.getCycleStartDate()));
		assertEquals("15/08/06", this.dateToString(_invoice.getDueDate()));
		assertEquals("01/08/06", this.dateToString(_invoice.getIssueDate()));
		assertEquals(1493.36, _invoice.getTotalAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(0, _invoice.getChargesAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(0, _invoice.getAdjustmentsAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
//		 validates Account
		validateAccount(_invoice.getAccount());
		// validates NFs
		assertNotNull(_invoice.getReceipts());
		assertEquals(3, _invoice.getReceipts().size());
		validateTIMReceipt((Receipt)_invoice.getReceipts().get(0));
		assertEquals(8, _invoice.getSections().size());
		validateLDCNFHeaderTelemar((Receipt)_invoice.getReceipts().get(1));
		validateLDCNFHeaderEmbratel((Receipt)_invoice.getReceipts().get(2));
	}

	protected void validateAccount(Account _account) {
		assertEquals("CENTRAL DISTRIBUIDORA DE PROD LTDA - RJ", _account.getAccountName());
		assertEquals("6.156442.15", _account.getAccountNumber());
		assertEquals("RJ", _account.getAccountState());
		assertEquals("L", _account.getAccountType());
		assertEquals("RJ", _account.getCarrierState());
		//assertEquals("41", _account.getCarrierCode());
	}

	protected void validateTIMReceipt(Receipt _rcpt) {
		assertEquals("000.000.308-AB", _rcpt.getReceiptNbr());
		assertEquals("RJ", _rcpt.getCarrierState());
		assertEquals("TIM Celular S.A.", _rcpt.getCarrierName());
		//assertEquals("41", _rcpt.getCarrierCode());
		assertEquals(1414.11, _rcpt.getTotalAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);

		assertNotNull(_rcpt.getDetails());
		assertEquals(7, _rcpt.getDetails().size());

		int counter=0;
		TIMReceiptDetail trd = (TIMReceiptDetail) _rcpt.getDetails().get(counter++);
		assertEquals("Subscriptions", trd.getCaption());
		validateSubscription(trd);
		trd = (TIMReceiptDetail) _rcpt.getDetails().get(counter++);
		assertEquals("ContractServices", trd.getCaption());
		validateContractServices(trd);
		trd = (TIMReceiptDetail) _rcpt.getDetails().get(counter++);
		assertEquals("HomeCallsGroups", trd.getCaption());
		validateHomeGroup(trd);
		trd = (TIMReceiptDetail) _rcpt.getDetails().get(counter++);
		assertEquals("ServiceUsageGroups", trd.getCaption());
		validateServiceUsageGroup(trd);
		trd = (TIMReceiptDetail) _rcpt.getDetails().get(counter++);
		assertEquals("OCCs", trd.getCaption());
		validateOCCSubscription(trd);
		trd = (TIMReceiptDetail) _rcpt.getDetails().get(counter++);
		assertEquals("Current FreeUnits", trd.getCaption());
		validateCurrentFreeUnits(trd);

		assertNotNull(_rcpt.getTaxes());
		assertEquals(5, _rcpt.getTaxes().size());

		assertNotNull(_rcpt.getTaxes());
		assertEquals(5,_rcpt.getTaxes().size());

		ChargedTax tax = (ChargedTax) _rcpt.getTaxes().get(0);
		assertEquals("COFINS",tax.getTaxName());
		assertEquals(3, tax.getTaxRate(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(1414.11, tax.getTaxableAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(42.42, tax.getTaxAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);

		tax = (ChargedTax) _rcpt.getTaxes().get(1);
		assertEquals("ICMS", tax.getTaxName());
		assertEquals(30, tax.getTaxRate(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(1414.11, tax.getTaxableAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(424.23, tax.getTaxAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);

		tax = (ChargedTax) _rcpt.getTaxes().get(2);
		assertEquals("FUST", tax.getTaxName());
		assertEquals(1, tax.getTaxRate(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(1193.99, tax.getTaxableAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(11.93, tax.getTaxAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);

		tax = (ChargedTax) _rcpt.getTaxes().get(3);
		assertEquals("PIS", tax.getTaxName());
		assertEquals(0.65, tax.getTaxRate(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(1414.11, tax.getTaxableAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(9.19, tax.getTaxAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);

		tax = (ChargedTax) _rcpt.getTaxes().get(4);
		assertEquals("FUNTTEL", tax.getTaxName());
		assertEquals(0.5, tax.getTaxRate(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(1193.54, tax.getTaxableAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(5.96, tax.getTaxAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
	}

	protected void validateSubscription(TIMReceiptDetail _trd) {
		assertNotNull(_trd.getReceiptGroupItems());
		assertEquals(1, _trd.getReceiptGroupItems().size());

		ServiceReceiptDetail srd = (ServiceReceiptDetail) _trd.getReceiptGroupItems().get(0);
		assertEquals("Plano Nosso Modo", srd.getServiceId());
		assertEquals("3", srd.getUnitCount());
		assertEquals(30, srd.getNumberOfDays());
		assertEquals(1.00, srd.getProRateFactor(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(2.55, srd.getTotalAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		// taxes
		assertNotNull(srd.getTaxes());
		assertEquals(1, srd.getTaxes().size());

		ChargedTax tax = (ChargedTax)srd.getTaxes().get(0);
		assertEquals("ICMS", tax.getTaxName());
		assertEquals(30, tax.getTaxRate(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(0.76, tax.getTaxAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
	}

	protected void validateContractServices(TIMReceiptDetail _trd) {
		assertNotNull(_trd.getReceiptGroupItems());
		assertEquals(5,_trd.getReceiptGroupItems().size());
		assertEquals(122.15, _trd.getTotalAmount(),BGH2SAX.MAX_VALUES_DIFFERENCE);

		ServiceReceiptDetail srd= ((ServiceReceiptDetail)_trd.getReceiptGroupItems().get(0));
		assertEquals("Tarifa Zero", srd.getServiceId());
		assertEquals(30, srd.getNumberOfDays());
		assertEquals(1, srd.getProRateFactor(),BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(39, srd.getTotalAmount(),BGH2SAX.MAX_VALUES_DIFFERENCE);

		assertNotNull(srd.getTaxes());
		assertEquals(1, srd.getTaxes().size());

		ChargedTax tax = ((ChargedTax)srd.getTaxes().get(0));
		assertEquals("ICMS", tax.getTaxName());
		assertEquals(30, tax.getTaxRate(),BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(11.70, tax.getTaxAmount(),BGH2SAX.MAX_VALUES_DIFFERENCE);
	}

	protected void validateHomeGroup(TIMReceiptDetail _trd) {
		assertNotNull(_trd.getReceiptGroupItems());
		assertEquals(6, _trd.getReceiptGroupItems().size());
		assertEquals(1284.40, _trd.getTotalAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		// FIRST ITEM
		GroupItemReceiptDetail grd = (GroupItemReceiptDetail) _trd.getReceiptGroupItems().get(0);
		assertEquals("Chamadas Locais para Celulares TIM", grd.getCaption());
		assertEquals("D", grd.getAreaIndicator());
		assertEquals(7, grd.getEventCount());
		assertEquals(00, grd.getPromotionAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(1.04, grd.getTotalAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		// taxes
		assertNotNull(grd.getTaxes());
		assertEquals(1, grd.getTaxes().size());

		ChargedTax tax = (ChargedTax)grd.getTaxes().get(0);
		assertEquals("ICMS", tax.getTaxName());
		assertEquals(30, tax.getTaxRate(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(0.31, tax.getTaxAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);

		// SECOND ITEM
		 grd = (GroupItemReceiptDetail) _trd.getReceiptGroupItems().get(1);
		assertEquals("Chamadas Locais para Outros Celulares", grd.getCaption());
		assertEquals("D", grd.getAreaIndicator());
		assertEquals(237, grd.getEventCount());
		assertEquals(00, grd.getPromotionAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(146.86, grd.getTotalAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		// taxes
		assertNotNull(grd.getTaxes());
		assertEquals(1, grd.getTaxes().size());

		tax = (ChargedTax)grd.getTaxes().get(0);
		assertEquals("ICMS", tax.getTaxName());
		assertEquals(30, tax.getTaxRate(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(44.05, tax.getTaxAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		// THIRD ITEM
		 grd = (GroupItemReceiptDetail) _trd.getReceiptGroupItems().get(2);
		assertEquals("Chamadas Locais para Telefones Fixos", grd.getCaption());
		assertEquals("D", grd.getAreaIndicator());
		assertEquals(243, grd.getEventCount());
		assertEquals(00, grd.getPromotionAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(133.50, grd.getTotalAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		// taxes
		assertNotNull(grd.getTaxes());
		assertEquals(1, grd.getTaxes().size());

		tax = (ChargedTax)grd.getTaxes().get(0);
		assertEquals("ICMS", tax.getTaxName());
		assertEquals(30, tax.getTaxRate(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(40.05, tax.getTaxAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);

		grd = (GroupItemReceiptDetail) _trd.getReceiptGroupItems().get(3);
		assertEquals("Chamadas Tarifa Zero", grd.getCaption());
		assertEquals("D", grd.getAreaIndicator());
		assertEquals(30, grd.getEventCount());
		assertEquals(0, grd.getPromotionAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(0, grd.getTotalAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		// taxes
		assertNotNull(grd.getTaxes());
		assertEquals(1, grd.getTaxes().size());

		tax = (ChargedTax)grd.getTaxes().get(0);
		assertEquals("ICMS", tax.getTaxName());
		assertEquals(30, tax.getTaxRate(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(0, tax.getTaxAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);

		grd = (GroupItemReceiptDetail) _trd.getReceiptGroupItems().get(4);
		assertEquals("Chamadas de Acesso *100", grd.getCaption());
		assertEquals("D", grd.getAreaIndicator());
		assertEquals(15, grd.getEventCount());
		assertEquals(0, grd.getPromotionAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(4.59, grd.getTotalAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		// taxes
		assertNotNull(grd.getTaxes());
		assertEquals(1, grd.getTaxes().size());

		tax = (ChargedTax)grd.getTaxes().get(0);
		assertEquals("ICMS", tax.getTaxName());
		assertEquals(30, tax.getTaxRate(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(1.37, tax.getTaxAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
	}

	protected void validateServiceUsageGroup(TIMReceiptDetail _trd) {
		assertNotNull(_trd.getReceiptGroupItems());
		assertEquals(1, _trd.getReceiptGroupItems().size());
		assertEquals(1.01, _trd.getTotalAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		// FIRST ITEM
		GroupItemReceiptDetail grd = (GroupItemReceiptDetail) _trd.getReceiptGroupItems().get(0);
		assertEquals("TIM Wap Fast", grd.getCaption());
		assertEquals("D", grd.getAreaIndicator());
		assertEquals(2, grd.getEventCount());
		assertEquals(00, grd.getPromotionAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(1.01, grd.getTotalAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		// taxes
		assertNotNull(grd.getTaxes());
		assertEquals(1, grd.getTaxes().size());

		ChargedTax tax = (ChargedTax)grd.getTaxes().get(0);
		assertEquals("ICMS", tax.getTaxName());
		assertEquals(30, tax.getTaxRate(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(0.3, tax.getTaxAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
	}

	protected void validateOCCSubscription(TIMReceiptDetail _trd) {
		assertNotNull(_trd.getReceiptGroupItems());
		assertEquals(1,_trd.getReceiptGroupItems().size());
		assertEquals(4.0, _trd.getTotalAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);

		GroupItemReceiptDetail grd = ((GroupItemReceiptDetail)_trd.getReceiptGroupItems().get(0));
		assertEquals("IR-Bloqueio Chamada Originada", grd.getCaption());
		assertEquals(1, grd.getEventCount());
		assertEquals(4,grd.getTotalAmount(),BGH2SAX.MAX_VALUES_DIFFERENCE);

		assertNotNull(grd.getTaxes());
		assertEquals(1, grd.getTaxes().size());

		ChargedTax tax = ((ChargedTax)grd.getTaxes().get(0));
		assertEquals("ICMS", tax.getTaxName());
		assertEquals(30,tax.getTaxRate(),BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(1.2,tax.getTaxAmount(),BGH2SAX.MAX_VALUES_DIFFERENCE);

	}

	protected void validateCurrentFreeUnits(TIMReceiptDetail _trd) {
		assertNotNull(_trd.getReceiptGroupItems());
		assertEquals(6, _trd.getReceiptGroupItems().size());

		FreeUnitsReceiptDetail srd = ((FreeUnitsReceiptDetail)_trd.getReceiptGroupItems().get(5));
		assertEquals("DELETE-PACOTE SMS PROMOCIONAL", srd.getServiceId());
		assertEquals("021-8138-0521", srd.getAccessNbr());
		assertEquals("3645552",srd.getContractNumber());
		assertEquals("25/06/06", dateToString(srd.getStartDate()));
		assertEquals("24/07/06", dateToString(srd.getEndDate()));
		assertEquals("Msg", srd.getUnitType());
		assertEquals("0",srd.getUnitCarriedOverCount());
		assertEquals("99999999", srd.getUnitCount());
		assertEquals("0", srd.getUnitUsedCount());
		assertEquals("99999999", srd.getUnitUnusedCount());
	}

	protected void validateLDCNFHeaderTelemar(Receipt rcpt) {
		assertEquals("000.000.112-5",rcpt.getReceiptNbr());
		assertEquals("RJ",rcpt.getCarrierState());
		//assertEquals("31", rcpt.getCarrierCode());
		assertEquals("TELEMAR  NORTE LESTE  S/A ", rcpt.getCarrierName());
		assertEquals(0.56, rcpt.getTotalAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);

		assertNotNull(rcpt.getDetails());
		assertEquals(1,rcpt.getDetails().size());

		TIMReceiptDetail trd = (TIMReceiptDetail) rcpt.getDetails().get(0);
		assertNotNull(trd.getReceiptGroupItems());
		assertEquals(1, trd.getReceiptGroupItems().size());

		GroupItemReceiptDetail grd = ((GroupItemReceiptDetail)trd.getReceiptGroupItems().get(0));
		assertEquals("Chamadas Longa Distância: Telemar", grd.getCaption());
		assertEquals("R", grd.getAreaIndicator());
		assertEquals(1, grd.getEventCount());
		assertEquals(0.56, grd.getTotalAmount(),BGH2SAX.MAX_VALUES_DIFFERENCE);

		assertNotNull(rcpt.getTaxes());
		assertEquals(3, rcpt.getTaxes().size());
		ChargedTax tax = (ChargedTax) rcpt.getTaxes().get(0);
		assertEquals("ICMS Telemar", tax.getTaxName());
		assertEquals(30, tax.getTaxRate(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(0.56, tax.getTaxableAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(0.16, tax.getTaxAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		tax = (ChargedTax) rcpt.getTaxes().get(1);
		assertEquals("PIS-Telemar", tax.getTaxName());
		assertEquals(0.65, tax.getTaxRate(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(0.56, tax.getTaxableAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(0.0, tax.getTaxAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		tax = (ChargedTax) rcpt.getTaxes().get(2);
		assertEquals("COFINS-Telemar", tax.getTaxName());
		assertEquals(3, tax.getTaxRate(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(0.56, tax.getTaxableAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(0.01, tax.getTaxAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
	}

	protected void validateLDCNFHeaderEmbratel(Receipt rcpt) {
		assertEquals("000.000.227-A-5a", rcpt.getReceiptNbr());
		assertEquals("RJ", rcpt.getCarrierState());
		assertEquals("EMBRATEL", rcpt.getCarrierName());
		//assertEquals("21", rcpt.getCarrierCode());
		assertEquals(78.69, rcpt.getTotalAmount(),BGH2SAX.MAX_VALUES_DIFFERENCE);

		assertNotNull(rcpt.getDetails());
		assertEquals(1, rcpt.getDetails().size());

		TIMReceiptDetail trd = (TIMReceiptDetail) rcpt.getDetails().get(0);
		assertNotNull(trd.getReceiptGroupItems());
		assertEquals(1, trd.getReceiptGroupItems().size());

		GroupItemReceiptDetail grd = ((GroupItemReceiptDetail)trd.getReceiptGroupItems().get(0));
		assertEquals("Chamadas Longa Distância: Embratel", grd.getCaption());
		assertEquals("R",grd.getAreaIndicator());
		assertEquals(16, grd.getEventCount());
		assertEquals(78.69,grd.getTotalAmount(),BGH2SAX.MAX_VALUES_DIFFERENCE);

		assertNotNull(rcpt.getTaxes());
		assertEquals(3,rcpt.getTaxes().size());

		ChargedTax tax = ((ChargedTax)rcpt.getTaxes().get(0));
		assertEquals("ICMS Embratel",tax.getTaxName());
		assertEquals(30, tax.getTaxRate(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(78.69, tax.getTaxableAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(23.6, tax.getTaxAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);

		tax = ((ChargedTax)rcpt.getTaxes().get(1));
		assertEquals("PIS-Embratel",tax.getTaxName());
		assertEquals(0.65, tax.getTaxRate(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(78.69, tax.getTaxableAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(0.51, tax.getTaxAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);


		tax = ((ChargedTax)rcpt.getTaxes().get(2));
		assertEquals("COFINS-Embratel",tax.getTaxName());
		assertEquals(3, tax.getTaxRate(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(78.69, tax.getTaxableAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(2.36, tax.getTaxAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);

	}

	/**
	 * @see br.com.auster.tim.bgh.sax.AccountTestBase#getAccountFile()
	 */
	protected String getAccountFile() {
		return "examples/ACCOUNT6.BGH";
	}

}
