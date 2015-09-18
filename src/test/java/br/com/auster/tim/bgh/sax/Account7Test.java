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
public class Account7Test extends AccountTestBase {



	/**
	 * @see br.com.auster.tim.bgh.sax.AccountTestBase#validateInvoice(Invoice)
	 */
	protected void validateInvoice(Invoice _invoice) {
		super.validateInvoice(_invoice);
		assertEquals("24/07/06", this.dateToString(_invoice.getCycleEndDate()));
		assertEquals("25/06/06", this.dateToString(_invoice.getCycleStartDate()));
		assertEquals("15/08/06", this.dateToString(_invoice.getDueDate()));
		assertEquals("01/08/06", this.dateToString(_invoice.getIssueDate()));
		assertEquals(7373.04, _invoice.getTotalAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(0, _invoice.getChargesAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(0, _invoice.getAdjustmentsAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
//		 validates Account
		validateAccount(_invoice.getAccount());
		// validates NFs
		assertNotNull(_invoice.getReceipts());
		assertEquals(4, _invoice.getReceipts().size());
		validateTIMReceipt((Receipt)_invoice.getReceipts().get(0));
		validateLDCNFHeaderBrasilTelecom((Receipt)_invoice.getReceipts().get(1));
		validateLDCHeaderTelemar((Receipt)_invoice.getReceipts().get(2));
		validateLDCNFHeaderEmbratel((Receipt)_invoice.getReceipts().get(3));
		assertEquals(8, _invoice.getSections().size());
	}
	protected void validateAccount(Account _account) {
		assertEquals("PREFEITURA MUNICIPAL DE ITAGUAI", _account.getAccountName());
		assertEquals("6.106990", _account.getAccountNumber());
		assertEquals("RJ", _account.getAccountState());
		assertEquals("L", _account.getAccountType());
		assertEquals("RJ", _account.getCarrierState());
	//	assertEquals("41", _account.getCarrierCode());
	}
	protected void validateTIMReceipt(Receipt _rcpt) {
		assertEquals("000.000.306-AB",  _rcpt.getReceiptNbr());
		assertEquals("RJ", _rcpt.getCarrierState());
		assertEquals("TIM Celular S.A.", _rcpt.getCarrierName());
	//	assertEquals("41", _rcpt.getCarrierCode());
		assertEquals(7063.11, _rcpt.getTotalAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertNotNull(_rcpt.getDetails());
		assertEquals(7, _rcpt.getDetails().size());

		int counter = 0;
		TIMReceiptDetail trd = (TIMReceiptDetail)_rcpt.getDetails().get(counter ++);
		assertEquals("Subscriptions", trd.getCaption());
		validateSubscription(trd);

		trd =(TIMReceiptDetail) _rcpt.getDetails().get(counter ++);
		assertEquals("ContractServices", trd.getCaption());
		validateContractServices(trd);

		trd =(TIMReceiptDetail) _rcpt.getDetails().get(counter ++);
		assertEquals("HomeCallsGroups", trd.getCaption());
		validateHomeGroup(trd);

		trd = (TIMReceiptDetail) _rcpt.getDetails().get(counter ++);
		assertEquals("RoamCallsGroups", trd.getCaption());
		validateRoamGroup(trd);

		trd = (TIMReceiptDetail)_rcpt.getDetails().get(counter ++);
		assertEquals("ServiceUsageGroups", trd.getCaption());
		validateServiceUsageGroup(trd);

		trd = (TIMReceiptDetail) _rcpt.getDetails().get(counter++);
		assertEquals("Current FreeUnits", trd.getCaption());
		validateFU(trd);


		assertNotNull(_rcpt.getTaxes());
		assertEquals(5,_rcpt.getTaxes().size());

		ChargedTax tax = (ChargedTax) _rcpt.getTaxes().get(0);
		assertEquals("COFINS",tax.getTaxName());
		assertEquals(3, tax.getTaxRate(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(7063.11, tax.getTaxableAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(211.89, tax.getTaxAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);

		tax = (ChargedTax) _rcpt.getTaxes().get(1);
		assertEquals("ICMS", tax.getTaxName());
		assertEquals(30, tax.getTaxRate(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(7063.11, tax.getTaxableAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(2118.93, tax.getTaxAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);

		tax = (ChargedTax) _rcpt.getTaxes().get(2);
		assertEquals("FUST", tax.getTaxName());
		assertEquals(1, tax.getTaxRate(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(4843.66, tax.getTaxableAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(48.43, tax.getTaxAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);

		tax = (ChargedTax) _rcpt.getTaxes().get(3);
		assertEquals("PIS", tax.getTaxName());
		assertEquals(0.65, tax.getTaxRate(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(7063.11, tax.getTaxableAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(45.91, tax.getTaxAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);

		tax = (ChargedTax) _rcpt.getTaxes().get(4);
		assertEquals("FUNTTEL", tax.getTaxName());
		assertEquals(0.5, tax.getTaxRate(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(4843.66, tax.getTaxableAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(24.21, tax.getTaxAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
	}

	protected void validateSubscription(TIMReceiptDetail _trd) {
		assertNotNull(_trd.getReceiptGroupItems());
		assertEquals(1, _trd.getReceiptGroupItems().size());

		ServiceReceiptDetail srd = (ServiceReceiptDetail) _trd.getReceiptGroupItems().get(0);
		assertEquals("Plano Básico", srd.getServiceId());
		assertEquals("1", srd.getUnitCount());
		assertEquals(30, srd.getNumberOfDays());
		assertEquals(1, srd.getProRateFactor(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(43.90, srd.getTotalAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		// taxes
		assertNotNull(srd.getTaxes());
		assertEquals(1, srd.getTaxes().size());

		ChargedTax tax = (ChargedTax)srd.getTaxes().get(0);
		assertEquals("ICMS", tax.getTaxName());
		assertEquals(30, tax.getTaxRate(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(13.17, tax.getTaxAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
	}

	protected void validateContractServices(TIMReceiptDetail _trd) {
		assertNotNull(_trd.getReceiptGroupItems());
		assertEquals(1, _trd.getReceiptGroupItems().size());
		assertEquals(3, _trd.getTotalAmount(),BGH2SAX.MAX_VALUES_DIFFERENCE);

		ServiceReceiptDetail srd = ((ServiceReceiptDetail)_trd.getReceiptGroupItems().get(0));
		assertEquals("Identificador de Chamadas", srd.getServiceId());
		assertEquals(30, srd.getNumberOfDays());
		assertEquals(1, srd.getProRateFactor(),BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(3, srd.getTotalAmount(),BGH2SAX.MAX_VALUES_DIFFERENCE);

		assertNotNull(srd.getTaxes());
		assertEquals(1,srd.getTaxes().size());

		ChargedTax tax = ((ChargedTax)srd.getTaxes().get(0));
		assertEquals("ICMS",tax.getTaxName());
		assertEquals(30, tax.getTaxRate(),BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(0.9,tax.getTaxAmount(),BGH2SAX.MAX_VALUES_DIFFERENCE);
	}

	protected void validateHomeGroup(TIMReceiptDetail _trd) {
		assertNotNull(_trd.getReceiptGroupItems());
		assertEquals(7, _trd.getReceiptGroupItems().size());
		assertEquals(5390.84, _trd.getTotalAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		// FIRST ITEM

		GroupItemReceiptDetail srd = (GroupItemReceiptDetail) _trd.getReceiptGroupItems().get(0);
		assertEquals("Chamadas Locais para Celulares TIM", srd.getCaption());
		assertEquals("D", srd.getAreaIndicator());
		assertEquals(1438, srd.getEventCount());
		assertEquals(0, srd.getPromotionAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(658.20, srd.getTotalAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		// taxes
		assertNotNull(srd.getTaxes());
		assertEquals(1, srd.getTaxes().size());

		ChargedTax tax = (ChargedTax)srd.getTaxes().get(0);
		assertEquals("ICMS", tax.getTaxName());
		assertEquals(30, tax.getTaxRate(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(197.46, tax.getTaxAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);

		// SECOND ITEM
		 srd = (GroupItemReceiptDetail) _trd.getReceiptGroupItems().get(1);
		assertEquals("Chamadas Locais para Outros Celulares", srd.getCaption());
		assertEquals("D", srd.getAreaIndicator());
		assertEquals(5460, srd.getEventCount());
		assertEquals(0, srd.getPromotionAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(2666.33, srd.getTotalAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		// taxes
		assertNotNull(srd.getTaxes());
		assertEquals(1, srd.getTaxes().size());

		tax = (ChargedTax)srd.getTaxes().get(0);
		assertEquals("ICMS", tax.getTaxName());
		assertEquals(30, tax.getTaxRate(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(799.89, tax.getTaxAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);

		// THIRD ITEM
		 srd = (GroupItemReceiptDetail) _trd.getReceiptGroupItems().get(2);
		assertEquals("Chamadas Locais para Telefones Fixos", srd.getCaption());
		assertEquals("D", srd.getAreaIndicator());
		assertEquals(1915, srd.getEventCount());
		assertEquals(0, srd.getPromotionAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(1364.38, srd.getTotalAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		// taxes
		assertNotNull(srd.getTaxes());
		assertEquals(1, srd.getTaxes().size());

		tax = (ChargedTax)srd.getTaxes().get(0);
		assertEquals("ICMS", tax.getTaxName());
		assertEquals(30, tax.getTaxRate(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(409.31, tax.getTaxAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);

		srd = (GroupItemReceiptDetail) _trd.getReceiptGroupItems().get(3);
		assertEquals("Chamadas de Acesso *100", srd.getCaption());
		assertEquals("D", srd.getAreaIndicator());
		assertEquals(172, srd.getEventCount());
		assertEquals(0, srd.getPromotionAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(67.08, srd.getTotalAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		// taxes
		assertNotNull(srd.getTaxes());
		assertEquals(1, srd.getTaxes().size());

		tax = (ChargedTax)srd.getTaxes().get(0);
		assertEquals("ICMS", tax.getTaxName());
		assertEquals(30, tax.getTaxRate(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(20.12, tax.getTaxAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);

		srd = (GroupItemReceiptDetail) _trd.getReceiptGroupItems().get(4);
		assertEquals("AD-O", srd.getCaption());
		assertEquals("D", srd.getAreaIndicator());
		assertEquals(82, srd.getEventCount());
		assertEquals(0, srd.getPromotionAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(41, srd.getTotalAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		// taxes
		assertNotNull(srd.getTaxes());
		assertEquals(1, srd.getTaxes().size());

		tax = (ChargedTax)srd.getTaxes().get(0);
		assertEquals("ICMS", tax.getTaxName());
		assertEquals(30, tax.getTaxRate(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(12.30, tax.getTaxAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);

		srd = (GroupItemReceiptDetail) _trd.getReceiptGroupItems().get(5);
		assertEquals("AD-R", srd.getCaption());
		assertEquals("D", srd.getAreaIndicator());
		assertEquals(79, srd.getEventCount());
		assertEquals(0, srd.getPromotionAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(39.5, srd.getTotalAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		// taxes
		assertNotNull(srd.getTaxes());
		assertEquals(1, srd.getTaxes().size());

		tax = (ChargedTax)srd.getTaxes().get(0);
		assertEquals("ICMS", tax.getTaxName());
		assertEquals(30, tax.getTaxRate(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(11.85, tax.getTaxAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);

		srd = (GroupItemReceiptDetail) _trd.getReceiptGroupItems().get(6);
		assertEquals("Chamadas Longa Distância: TIM LD 41", srd.getCaption());
		assertEquals("R", srd.getAreaIndicator());
		assertEquals(169, srd.getEventCount());
		assertEquals(1.28, srd.getPromotionAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(554.35, srd.getTotalAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		// taxes
		assertNotNull(srd.getTaxes());
		assertEquals(1, srd.getTaxes().size());

		tax = (ChargedTax)srd.getTaxes().get(0);
		assertEquals("ICMS", tax.getTaxName());
		assertEquals(30, tax.getTaxRate(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(166.3, tax.getTaxAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
	}

	protected void validateRoamGroup(TIMReceiptDetail _trd) {
		assertNotNull(_trd.getReceiptGroupItems());
		assertEquals(2, _trd.getReceiptGroupItems().size());
		assertEquals(1563.13, _trd.getTotalAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);

		GroupItemReceiptDetail srd = ((GroupItemReceiptDetail) _trd.getReceiptGroupItems().get(0));
		assertEquals("Roaming Outra Operadora-AD por Chamada Originada", srd.getCaption());
		assertEquals("N", srd.getAreaIndicator());
		assertEquals(2, srd.getEventCount());
		assertEquals(2.40, srd.getTotalAmount(),BGH2SAX.MAX_VALUES_DIFFERENCE);

		assertNotNull(srd.getTaxes());
		assertEquals(1, srd.getTaxes().size());

		ChargedTax tax = ((ChargedTax)srd.getTaxes().get(0));
		assertEquals("ICMS", tax.getTaxName());
		assertEquals(30 , tax.getTaxRate(),BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(0.72, tax.getTaxAmount(),BGH2SAX.MAX_VALUES_DIFFERENCE);


		srd =((GroupItemReceiptDetail)_trd.getReceiptGroupItems().get(1));
		assertEquals("Roaming Internacional - Chamadas Originadas", srd.getCaption());
		assertEquals("I", srd.getAreaIndicator());
		assertEquals(44, srd.getEventCount());
		assertEquals(1560.73, srd.getTotalAmount(),BGH2SAX.MAX_VALUES_DIFFERENCE);

		assertNotNull(srd.getTaxes());
		assertEquals(1, srd.getTaxes().size());

		tax= ((ChargedTax)srd.getTaxes().get(0));
		assertEquals("ICMS", tax.getTaxName());
		assertEquals(30, tax.getTaxRate(),BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(468.21, tax.getTaxAmount(),BGH2SAX.MAX_VALUES_DIFFERENCE);
	}

	protected void validateServiceUsageGroup(TIMReceiptDetail _trd) {
		assertNotNull(_trd.getReceiptGroupItems());
		assertEquals(6, _trd.getReceiptGroupItems().size());
		assertEquals(62.24, _trd.getTotalAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		// FIRST ITEM
		GroupItemReceiptDetail srd = (GroupItemReceiptDetail) _trd.getReceiptGroupItems().get(5);
		assertEquals("TIM Wap Fast", srd.getCaption());
		assertEquals("D", srd.getAreaIndicator());
		assertEquals(39, srd.getEventCount());
		assertEquals(0, srd.getPromotionAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(16.56, srd.getTotalAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		// taxes
		assertNotNull(srd.getTaxes());
		assertEquals(1, srd.getTaxes().size());

		ChargedTax tax = (ChargedTax)srd.getTaxes().get(0);
		assertEquals("ICMS", tax.getTaxName());
		assertEquals(30, tax.getTaxRate(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(4.96, tax.getTaxAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
	}

	protected void validateFU(TIMReceiptDetail _trd) {
		assertNotNull(_trd.getReceiptGroupItems());
		assertEquals(70, _trd.getReceiptGroupItems().size());

		FreeUnitsReceiptDetail frd = ((FreeUnitsReceiptDetail)_trd.getReceiptGroupItems().get(1));
		assertEquals("DELETE-PACOTE SMS PROMOCIONAL", frd.getServiceId());
		assertEquals("021-8209-9795", frd.getAccessNbr());
		assertEquals("2671893", frd.getContractNumber());
		assertEquals("25/06/06", dateToString(frd.getStartDate()));
		assertEquals("24/07/06", dateToString(frd.getEndDate()));
		assertEquals("Msg", frd.getUnitType());
		assertEquals("0", frd.getUnitCarriedOverCount());
		assertEquals("99999997", frd.getUnitUnusedCount());
		assertEquals("2", frd.getUnitUsedCount());
		assertEquals("99999999", frd.getUnitCount());
	}

	protected void validateLDCNFHeaderBrasilTelecom(Receipt rcpt) {

		assertEquals("000.000.009-C",rcpt.getReceiptNbr());
		assertEquals("RJ",rcpt.getCarrierState());
		assertEquals("Brasil Telecom S/A", rcpt.getCarrierName());
	//	assertEquals("14", rcpt.getCarrierCode());
		assertEquals(2.16, rcpt.getTotalAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);

		assertNotNull(rcpt.getDetails());
		assertEquals(1,rcpt.getDetails().size());

		TIMReceiptDetail trd = (TIMReceiptDetail) rcpt.getDetails().get(0);
		assertNotNull(trd.getReceiptGroupItems());
		assertEquals(1, trd.getReceiptGroupItems().size());

		GroupItemReceiptDetail grd = ((GroupItemReceiptDetail)trd.getReceiptGroupItems().get(0));
		assertEquals("Chamadas Longa Distância: Brasil Telecom", grd.getCaption());
		assertEquals("R", grd.getAreaIndicator());
		assertEquals(1, grd.getEventCount());
		assertEquals(2.16, grd.getTotalAmount(),BGH2SAX.MAX_VALUES_DIFFERENCE);

		assertNotNull(rcpt.getTaxes());
		assertEquals(3, rcpt.getTaxes().size());
		ChargedTax tax = (ChargedTax) rcpt.getTaxes().get(0);
		assertEquals("ICMS BR Telecom", tax.getTaxName());
		assertEquals(30, tax.getTaxRate(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(2.16, tax.getTaxableAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(0.64, tax.getTaxAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		tax = (ChargedTax) rcpt.getTaxes().get(1);
		assertEquals("PIS-BR Telecom", tax.getTaxName());
		assertEquals(0.65, tax.getTaxRate(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(2.16, tax.getTaxableAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(0.01, tax.getTaxAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		tax = (ChargedTax) rcpt.getTaxes().get(2);
		assertEquals("COFINS-BR Telecom", tax.getTaxName());
		assertEquals(3, tax.getTaxRate(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(2.16, tax.getTaxableAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(0.06, tax.getTaxAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
	}

	protected void validateLDCHeaderTelemar(Receipt rcpt) {

		assertEquals("000.000.111-5", rcpt.getReceiptNbr());
		assertEquals("RJ", rcpt.getCarrierState());
	//	assertEquals("31", rcpt.getCarrierCode());
		assertEquals("TELEMAR  NORTE LESTE  S/A ", rcpt.getCarrierName());
		assertEquals(90.28, rcpt.getTotalAmount(),BGH2SAX.MAX_VALUES_DIFFERENCE);

		assertNotNull(rcpt.getDetails());
		assertEquals(1, rcpt.getDetails().size());

		TIMReceiptDetail trd = (TIMReceiptDetail) rcpt.getDetails().get(0);
		assertNotNull(trd.getReceiptGroupItems());
		assertEquals(1,trd.getReceiptGroupItems().size());
		GroupItemReceiptDetail grd = ((GroupItemReceiptDetail)trd.getReceiptGroupItems().get(0));

		assertEquals("Chamadas Longa Distância: Telemar", grd.getCaption());
		assertEquals("R",grd.getAreaIndicator());
		assertEquals(42, grd.getEventCount());
		assertEquals(90.28,grd.getTotalAmount(),BGH2SAX.MAX_VALUES_DIFFERENCE);

		assertNotNull(rcpt.getTaxes());
		assertEquals(3, rcpt.getTaxes().size());

		ChargedTax tax = (ChargedTax)rcpt.getTaxes().get(0);
		assertEquals("ICMS Telemar",tax.getTaxName());
		assertEquals(30, tax.getTaxRate(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(90.28, tax.getTaxableAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(27.08, tax.getTaxAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);

		tax = (ChargedTax)rcpt.getTaxes().get(1);
		assertEquals("PIS-Telemar",tax.getTaxName());
		assertEquals(0.65, tax.getTaxRate(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(90.28, tax.getTaxableAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(0.58, tax.getTaxAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);

		tax = (ChargedTax)rcpt.getTaxes().get(2);
		assertEquals("COFINS-Telemar",tax.getTaxName());
		assertEquals(3, tax.getTaxRate(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(90.28, tax.getTaxableAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(2.7, tax.getTaxAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
	}

	protected void validateLDCNFHeaderEmbratel(Receipt rcpt) {
		assertEquals("000.000.226-A-5a", rcpt.getReceiptNbr());
		assertEquals("RJ", rcpt.getCarrierState());
	//	assertEquals("21", rcpt.getCarrierCode());
		assertEquals("EMBRATEL", rcpt.getCarrierName());
		assertEquals(217.49, rcpt.getTotalAmount(),BGH2SAX.MAX_VALUES_DIFFERENCE);

		assertNotNull(rcpt.getDetails());
		assertEquals(1, rcpt.getDetails().size());

		TIMReceiptDetail trd = (TIMReceiptDetail) rcpt.getDetails().get(0);
		assertNotNull(trd.getReceiptGroupItems());
		assertEquals(1, trd.getReceiptGroupItems().size());

		GroupItemReceiptDetail grd = ((GroupItemReceiptDetail)trd.getReceiptGroupItems().get(0));
		assertEquals("Chamadas Longa Distância: Embratel", grd.getCaption());
		assertEquals("R",grd.getAreaIndicator());
		assertEquals(68, grd.getEventCount());
		assertEquals(217.49,grd.getTotalAmount(),BGH2SAX.MAX_VALUES_DIFFERENCE);

		assertNotNull(rcpt.getTaxes());
		assertEquals(3,rcpt.getTaxes().size());

		ChargedTax tax = ((ChargedTax)rcpt.getTaxes().get(0));
		assertEquals("ICMS Embratel",tax.getTaxName());
		assertEquals(30, tax.getTaxRate(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(217.49, tax.getTaxableAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(65.24, tax.getTaxAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);

		tax = ((ChargedTax)rcpt.getTaxes().get(1));
		assertEquals("PIS-Embratel",tax.getTaxName());
		assertEquals(0.65, tax.getTaxRate(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(217.49, tax.getTaxableAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(1.41, tax.getTaxAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);


		tax = ((ChargedTax)rcpt.getTaxes().get(2));
		assertEquals("COFINS-Embratel",tax.getTaxName());
		assertEquals(3, tax.getTaxRate(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(217.49, tax.getTaxableAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(6.52, tax.getTaxAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
	}
		/**
	 * @see br.com.auster.tim.bgh.sax.AccountTestBase#getAccountFile()
	 */
	protected String getAccountFile() {
		return "examples/ACCOUNT7.BGH";
	}

}


