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
import java.util.List;

import br.com.auster.om.invoice.ChargedTax;
import br.com.auster.om.invoice.Invoice;
import br.com.auster.om.invoice.Receipt;
import br.com.auster.om.invoice.Section;
import br.com.auster.om.util.UnitCounter;
import br.com.auster.tim.bgh.BGH2SAX;
import br.com.auster.tim.om.invoice.ContractInfoSection;
import br.com.auster.tim.om.invoice.ContractTotalsDetail;
import br.com.auster.tim.om.invoice.ContractUsageGroupSubsection;
import br.com.auster.tim.om.invoice.FreeUnitsReceiptDetail;
import br.com.auster.tim.om.invoice.GroupItemReceiptDetail;
import br.com.auster.tim.om.invoice.ServiceReceiptDetail;
import br.com.auster.tim.om.invoice.TIMReceiptDetail;

/**
 * @author framos
 * @version $Id$
 *
 */
public class Account3Test extends AccountTestBase {



	/**
	 * @see br.com.auster.tim.bgh.sax.AccountTestBase#validateInvoice(Invoice)
	 */
	protected void validateInvoice(Invoice _invoice) {
		super.validateInvoice(_invoice);
		// validates Invoice attributes
//		assertEquals("24/07/06", this.dateToString(_invoice.getCycleEndDate()));
//		assertEquals("25/06/06", this.dateToString(_invoice.getCycleStartDate()));
//		assertEquals("13/08/06", this.dateToString(_invoice.getDueDate()));
//		assertEquals("01/08/06", this.dateToString(_invoice.getIssueDate()));
		assertEquals(66.28, _invoice.getTotalAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(3.24, _invoice.getChargesAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(0, _invoice.getAdjustmentsAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
//		// validates Account
//		validateAccount(_invoice.getAccount());
		// validates NFs
		assertNotNull(_invoice.getReceipts());
		assertEquals(2, _invoice.getReceipts().size());
		validateReceipts(_invoice.getReceipts());
		assertEquals(6, _invoice.getSections().size());
		validateDetailsSection((Section)_invoice.getSections().get(1));
		validateTotalsServicesSection((Section)_invoice.getSections().get(2));
		validateTotalsOCCSection((Section)_invoice.getSections().get(3));
		validateTotalsFUSection((Section)_invoice.getSections().get(4));
		validateTotalsGranTotalSection((Section)_invoice.getSections().get(5));
	}
	
//	protected void validateAccount(Account _account) {
//		assertEquals("LUIZ CARLOS CAVALCANTI", _account.getAccountName());
//		assertEquals("1.10752124", _account.getAccountNumber());
//		assertEquals("RJ", _account.getAccountState());
//		assertEquals("1", _account.getAccountType());
//		assertEquals("RJ", _account.getCarrierState());
//		assertEquals("20", _account.getCarrierCode());
//	}
	
	protected void validateReceipts(List _receipts) {
		
		Receipt rcpt = (Receipt) _receipts.get(0);
		// NF TIM
		assertEquals("000.000.001-AB", rcpt.getReceiptNbr());
		assertEquals("AP", rcpt.getCarrierState());
		assertEquals("TIM Celular S.A.", rcpt.getCarrierName());
		assertEquals(60.04, rcpt.getTotalAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		//assertEquals("41", rcpt.getCarrierCode());
		// has 4 groups
		assertNotNull(rcpt.getDetails());
		assertEquals(5, rcpt.getDetails().size());
		
		for (Iterator it = rcpt.getDetails().iterator(); it.hasNext();) {
			TIMReceiptDetail trd = (TIMReceiptDetail) it.next();
			if ("ContractServices".equals(trd.getCaption())) {
				validateContractServices(trd);
			} else if ("HomeCallsGroups".equals(trd.getCaption())) {
				validateHomeGroup(trd);
			} else if ("ServiceUsageGroups".equals(trd.getCaption())) {
				validateServiceGroup(trd);
			} else if ("OCCs".equals(trd.getCaption())) {
				validateOCC(trd);
			} else if ("Current FreeUnits".equals(trd.getCaption())) {
				validateFU(trd);
			} else {
				System.out.println(trd.getCaption());
				fail();
			}
		}
		
		// NF LD 
		rcpt = (Receipt) _receipts.get(1);
		assertNotNull(rcpt);
		assertNotNull(rcpt.getDetails());
		assertEquals(1, rcpt.getDetails().size());
	}
	
	protected void validateFU(TIMReceiptDetail _trd) {
		assertNotNull(_trd.getReceiptGroupItems());
		assertEquals(5, _trd.getReceiptGroupItems().size());
		// FIRST ITEM
		FreeUnitsReceiptDetail srd = (FreeUnitsReceiptDetail) _trd.getReceiptGroupItems().get(0);
		assertEquals("Pacote TIM BRASIL 60 min", srd.getServiceId());
		assertEquals("096-8114-7725", srd.getAccessNbr());
		assertEquals("1007142", srd.getContractNumber());
		assertEquals("25/06/06", dateToString(srd.getStartDate()));
		assertEquals("24/07/06", dateToString(srd.getEndDate()));
		assertEquals("Sec", srd.getUnitType());
		assertEquals("00:00:00", srd.getUnitCarriedOverCount());
		assertEquals("01:00:00", srd.getUnitCount());
		assertEquals("00:16:12", srd.getUnitUsedCount());
		assertEquals("00:43:48", srd.getUnitUnusedCount());
		// SECOND ITEM
		srd = (FreeUnitsReceiptDetail) _trd.getReceiptGroupItems().get(1);
		assertEquals("Pacote VIAGEM 20min", srd.getServiceId());
		assertEquals("096-8114-7725", srd.getAccessNbr());
		assertEquals("1007142", srd.getContractNumber());
		assertEquals("25/06/06", dateToString(srd.getStartDate()));
		assertEquals("24/07/06", dateToString(srd.getEndDate()));
		assertEquals("Sec", srd.getUnitType());
		assertEquals("00:00:00", srd.getUnitCarriedOverCount());
		assertEquals("00:20:00", srd.getUnitCount());
		assertEquals("00:02:06", srd.getUnitUsedCount());
		assertEquals("00:17:54", srd.getUnitUnusedCount());
		// THIRD ITEM
		srd = (FreeUnitsReceiptDetail) _trd.getReceiptGroupItems().get(2);
		assertEquals("Pacote Dados 250 KB", srd.getServiceId());
		assertEquals("096-8114-7725", srd.getAccessNbr());
		assertEquals("1007142", srd.getContractNumber());
		assertEquals("25/06/06", dateToString(srd.getStartDate()));
		assertEquals("24/07/06", dateToString(srd.getEndDate()));
		assertEquals("Byte", srd.getUnitType());
		assertEquals("0 B", srd.getUnitCarriedOverCount());
		assertEquals("250.00 KB", srd.getUnitCount());
		assertEquals("0 B", srd.getUnitUsedCount());
		assertEquals("250.00 KB", srd.getUnitUnusedCount());
		// FOURTH ITEM
		srd = (FreeUnitsReceiptDetail) _trd.getReceiptGroupItems().get(3);
		assertEquals("Pacote 30 TIM TORPEDOS", srd.getServiceId());
		assertEquals("096-8114-7725", srd.getAccessNbr());
		assertEquals("1007142", srd.getContractNumber());
		assertEquals("25/06/06", dateToString(srd.getStartDate()));
		assertEquals("24/07/06", dateToString(srd.getEndDate()));
		assertEquals("Msg", srd.getUnitType());
		assertEquals("0", srd.getUnitCarriedOverCount());
		assertEquals("30", srd.getUnitCount());
		assertEquals("6", srd.getUnitUsedCount());
		assertEquals("24", srd.getUnitUnusedCount());
		// FIFTH ITEM
		srd = (FreeUnitsReceiptDetail) _trd.getReceiptGroupItems().get(4);
		assertEquals("Pacote 30 TIM FOTOMENSAGENS", srd.getServiceId());
		assertEquals("096-8114-7725", srd.getAccessNbr());
		assertEquals("1007142", srd.getContractNumber());
		assertEquals("25/06/06", dateToString(srd.getStartDate()));
		assertEquals("24/07/06", dateToString(srd.getEndDate()));
		assertEquals("Msg", srd.getUnitType());
		assertEquals("0", srd.getUnitCarriedOverCount());
		assertEquals("30", srd.getUnitCount());
		assertEquals("0", srd.getUnitUsedCount());
		assertEquals("30", srd.getUnitUnusedCount());
	}
		
	
	protected void validateContractServices(TIMReceiptDetail _trd) {
		assertNotNull(_trd.getReceiptGroupItems());
		assertEquals(1, _trd.getReceiptGroupItems().size());
		assertEquals(46.90, _trd.getTotalAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		
		ServiceReceiptDetail srd = (ServiceReceiptDetail) _trd.getReceiptGroupItems().get(0);
		assertEquals("Pacote TIM BRASIL 60 min", srd.getServiceId());
		assertEquals("096-8114-7725", srd.getAccessNbr());
		assertEquals("1007142", srd.getContractNumber());
		assertEquals(30, srd.getNumberOfDays());
		assertEquals(1.00, srd.getProRateFactor(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(46.9, srd.getTotalAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		// taxes
		assertNotNull(srd.getTaxes());
		assertEquals(1, srd.getTaxes().size());
		
		ChargedTax tax = (ChargedTax)srd.getTaxes().get(0);
		assertEquals("ICMS", tax.getTaxName());
		assertEquals(25, tax.getTaxRate(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(11.72, tax.getTaxAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
	}
	
	protected void validateHomeGroup(TIMReceiptDetail _trd) {
		assertNotNull(_trd.getReceiptGroupItems());
		assertEquals(3, _trd.getReceiptGroupItems().size());
		assertEquals(0, _trd.getTotalAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		// FIRST ITEM
		GroupItemReceiptDetail srd = (GroupItemReceiptDetail) _trd.getReceiptGroupItems().get(0);
		assertEquals("Chamadas Locais para Celulares TIM", srd.getCaption());
		assertEquals("D", srd.getAreaIndicator());
		assertEquals(5, srd.getEventCount());
		assertEquals(0, srd.getPromotionAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(0, srd.getTotalAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		// taxes
		assertNotNull(srd.getTaxes());
		assertEquals(1, srd.getTaxes().size());
		
		ChargedTax tax = (ChargedTax)srd.getTaxes().get(0);
		assertEquals("ICMS", tax.getTaxName());
		assertEquals(25, tax.getTaxRate(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(0, tax.getTaxAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		
		// SECOND ITEM
		 srd = (GroupItemReceiptDetail) _trd.getReceiptGroupItems().get(1);
		assertEquals("Chamadas Locais para Outros Celulares", srd.getCaption());
		assertEquals("D", srd.getAreaIndicator());
		assertEquals(12, srd.getEventCount());
		assertEquals(0, srd.getPromotionAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(0, srd.getTotalAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		// taxes
		assertNotNull(srd.getTaxes());
		assertEquals(1, srd.getTaxes().size());
		
		tax = (ChargedTax)srd.getTaxes().get(0);
		assertEquals("ICMS", tax.getTaxName());
		assertEquals(25, tax.getTaxRate(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(0, tax.getTaxAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);	
		
		// THIRD ITEM
		 srd = (GroupItemReceiptDetail) _trd.getReceiptGroupItems().get(2);
		assertEquals("Chamadas Longa Distância: TIM LD 41", srd.getCaption());
		assertEquals("R", srd.getAreaIndicator());
		assertEquals(2, srd.getEventCount());
		assertEquals(0, srd.getPromotionAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(0, srd.getTotalAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		// taxes
		assertNotNull(srd.getTaxes());
		assertEquals(1, srd.getTaxes().size());
		
		tax = (ChargedTax)srd.getTaxes().get(0);
		assertEquals("ICMS", tax.getTaxName());
		assertEquals(25, tax.getTaxRate(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(0, tax.getTaxAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);	
	}

	protected void validateServiceGroup(TIMReceiptDetail _trd) {
		assertNotNull(_trd.getReceiptGroupItems());
		assertEquals(1, _trd.getReceiptGroupItems().size());
		assertEquals(0, _trd.getTotalAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		// FIRST ITEM
		GroupItemReceiptDetail srd = (GroupItemReceiptDetail) _trd.getReceiptGroupItems().get(0);
		assertEquals("TIM Torpedo", srd.getCaption());
		assertEquals("D", srd.getAreaIndicator());
		assertEquals(6, srd.getEventCount());
		assertEquals(0, srd.getPromotionAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(0, srd.getTotalAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		// taxes
		assertNotNull(srd.getTaxes());
		assertEquals(1, srd.getTaxes().size());
				
		ChargedTax tax = (ChargedTax)srd.getTaxes().get(0);
		assertEquals("ICMS", tax.getTaxName());
		assertEquals(25, tax.getTaxRate(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(0, tax.getTaxAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);	

	}
		
	protected void validateOCC(TIMReceiptDetail _trd) {
		assertNotNull(_trd.getReceiptGroupItems());
		assertEquals(5, _trd.getReceiptGroupItems().size());
		assertEquals(13.14, _trd.getTotalAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		// FIRST ITEM
		GroupItemReceiptDetail srd = (GroupItemReceiptDetail) _trd.getReceiptGroupItems().get(0);
		assertEquals("Oferta 3 Prediletos - Taxa", srd.getCaption());
		assertEquals(0, srd.getPromotionAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(9.90, srd.getTotalAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		// taxes
		assertNotNull(srd.getTaxes());
		assertEquals(1, srd.getTaxes().size());
				
		ChargedTax tax = (ChargedTax)srd.getTaxes().get(0);
		assertEquals("ICMS", tax.getTaxName());
		assertEquals(25, tax.getTaxRate(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(2.47, tax.getTaxAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		
		// SECOND ITEM
		srd = (GroupItemReceiptDetail) _trd.getReceiptGroupItems().get(1);
		assertEquals("Juros (Venc 15/05/06, Pago em 06/07/06)", srd.getCaption());
		assertEquals(0, srd.getPromotionAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(0.88, srd.getTotalAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		
		// THRID ITEM
		srd = (GroupItemReceiptDetail) _trd.getReceiptGroupItems().get(2);
		assertEquals("Multa (Venc 15/05/06, Pago em 06/07/06)", srd.getCaption());
		assertEquals(0, srd.getPromotionAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(1.02, srd.getTotalAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		
		// FORTH ITEM
		srd = (GroupItemReceiptDetail) _trd.getReceiptGroupItems().get(3);
		assertEquals("Juros (Venc 15/06/06, Pago em 06/07/06)", srd.getCaption());
		assertEquals(0, srd.getPromotionAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(0.33, srd.getTotalAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		
		// FIFTH ITEM
		srd = (GroupItemReceiptDetail) _trd.getReceiptGroupItems().get(4);
		assertEquals("Multa (Venc 15/06/06, Pago em 06/07/06)", srd.getCaption());
		assertEquals(0, srd.getPromotionAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(1.01, srd.getTotalAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
	}	

	protected void validateDetailsSection(Section _details) {
		assertNotNull(_details);
		assertEquals("Details", _details.getCaption());
		assertEquals("ADRIANY DE ARAUJO LOPES", _details.getSectionName());
		assertEquals(66.28, _details.getTotalAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		// has only one ContractInfo subsection
		assertEquals(1, _details.getSubSections().size());
		validateContractInfoDetails((Section)_details.getSubSections().get(0));
	}

	protected void validateContractInfoDetails(Section _contract) {
		assertTrue(_contract instanceof ContractInfoSection);
		ContractInfoSection info = (ContractInfoSection)_contract;
		assertEquals("096-8114-7725", info.getAccessNbr());
		assertEquals("Plano TIM BRASIL 60", info.getRatePlan());
		assertEquals("1007142", info.getContractNumber());
		assertEquals("ADRIANY DE ARAUJO LOPES", info.getUserName());
		
		assertNotNull(info.getSubSections());
		assertEquals(5, info.getSubSections().size());
		validateUsageGroupsSection(info.getSubSections());
	}

	protected void validateUsageGroupsSection(List _groups) {
		int counter = 0;
		ContractUsageGroupSubsection usageGroup;
		// validating group 1
		Object s = _groups.get(counter++);
		assertTrue(s instanceof ContractUsageGroupSubsection);
		usageGroup = (ContractUsageGroupSubsection)s;
		assertEquals("Chamadas Locais para Celulares TIM", usageGroup.getSectionName());
		assertEquals(UnitCounter.TIME_COUNTER, usageGroup.getUnitType());
		// 00:03:12
		assertEquals(192, usageGroup.getRoundedVolume().getSeconds());
		// 00:02:52
		assertEquals(172, usageGroup.getUsedVolume().getSeconds());
		assertEquals(5, usageGroup.getQuantity());
		assertEquals(0, usageGroup.getTotalAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(1.85, usageGroup.getFreeUnitsDiscountAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(0, usageGroup.getDiscountAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(usageGroup.getQuantity(), usageGroup.getDetails().size());
		// validating group 2
		s = _groups.get(counter++);
		assertTrue(s instanceof ContractUsageGroupSubsection);
		usageGroup = (ContractUsageGroupSubsection)s;
		assertEquals("Chamadas Locais para Outros Celulares", usageGroup.getSectionName());
		assertEquals(UnitCounter.TIME_COUNTER, usageGroup.getUnitType());
		// 00:13:00
		assertEquals(780, usageGroup.getRoundedVolume().getSeconds());
		// 00:12:09
		assertEquals(729, usageGroup.getUsedVolume().getSeconds());
		assertEquals(12, usageGroup.getQuantity());
		assertEquals(0, usageGroup.getTotalAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(7.49, usageGroup.getFreeUnitsDiscountAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(0, usageGroup.getDiscountAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(usageGroup.getQuantity(), usageGroup.getDetails().size());
		// validating group 3
		s = _groups.get(counter++);
		assertTrue(s instanceof ContractUsageGroupSubsection);
		usageGroup = (ContractUsageGroupSubsection)s;
		assertEquals("TIM Torpedo", usageGroup.getSectionName());
		assertEquals(UnitCounter.UNIT_COUNTER, usageGroup.getUnitType());
		// null
		assertEquals(0, usageGroup.getRoundedVolume().getUnits());
		// null
		assertEquals(0, usageGroup.getUsedVolume().getUnits());
		assertEquals(6, usageGroup.getQuantity());
		assertEquals(0, usageGroup.getTotalAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(2.16, usageGroup.getFreeUnitsDiscountAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(0, usageGroup.getDiscountAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(usageGroup.getQuantity(), usageGroup.getDetails().size());
		// validating group 4
		s = _groups.get(counter++);
		assertTrue(s instanceof ContractUsageGroupSubsection);
		usageGroup = (ContractUsageGroupSubsection)s;
		assertEquals("Chamadas Longa Distância: Telemar", usageGroup.getSectionName());
		assertEquals(UnitCounter.TIME_COUNTER, usageGroup.getUnitType());
		// 00:04:36
		assertEquals(276, usageGroup.getRoundedVolume().getSeconds());
		// 00:04:36
		assertEquals(276, usageGroup.getUsedVolume().getSeconds());
		assertEquals(1, usageGroup.getQuantity());
		assertEquals(6.24, usageGroup.getTotalAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(0, usageGroup.getFreeUnitsDiscountAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(0, usageGroup.getDiscountAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(usageGroup.getQuantity(), usageGroup.getDetails().size());
		// validating group 5
		s = _groups.get(counter++);
		assertTrue(s instanceof ContractUsageGroupSubsection);
		usageGroup = (ContractUsageGroupSubsection)s;
		assertEquals("Chamadas Longa Distância: TIM LD 41", usageGroup.getSectionName());
		assertEquals(UnitCounter.TIME_COUNTER, usageGroup.getUnitType());
		// 00:02:06
		assertEquals(126, usageGroup.getRoundedVolume().getSeconds());
		// 00:01:58
		assertEquals(118, usageGroup.getUsedVolume().getSeconds());
		assertEquals(2, usageGroup.getQuantity());
		assertEquals(0, usageGroup.getTotalAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(2.61, usageGroup.getFreeUnitsDiscountAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(0, usageGroup.getDiscountAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(usageGroup.getQuantity(), usageGroup.getDetails().size());
	}

	protected void validateTotalsServicesSection(Section _section) {
		assertEquals(1, _section.getDetails().size());
		assertEquals("ContractServices", _section.getCaption());
		// testing detail
		assertTrue(_section.getDetails().get(0) instanceof ContractTotalsDetail);
		ContractTotalsDetail detail = (ContractTotalsDetail)_section.getDetails().get(0);
		assertEquals("096-8114-7725", detail.getAccessNbr());
		assertEquals("1007142", detail.getContractNumber());
		assertEquals(46.90, detail.getTotalAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
	}
	
	protected void validateTotalsOCCSection(Section _section) {
		assertEquals(1, _section.getDetails().size());
		assertEquals("ContractOCC", _section.getCaption());
		// testing detail
		assertTrue(_section.getDetails().get(0) instanceof ContractTotalsDetail);
		ContractTotalsDetail detail = (ContractTotalsDetail)_section.getDetails().get(0);
		assertEquals("096-8114-7725", detail.getAccessNbr());
		assertEquals("1007142", detail.getContractNumber());
		assertEquals(9.90, detail.getTotalAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
	}
	
	protected void validateTotalsFUSection(Section _section) {
		assertEquals(1, _section.getDetails().size());
		assertEquals("ContractFU", _section.getCaption());
		// testing detail
		assertTrue(_section.getDetails().get(0) instanceof ContractTotalsDetail);
		ContractTotalsDetail detail = (ContractTotalsDetail)_section.getDetails().get(0);
		assertEquals("096-8114-7725", detail.getAccessNbr());
		assertEquals("1007142", detail.getContractNumber());
		assertEquals(1104, detail.getTotalCount());
	}
	
	protected void validateTotalsGranTotalSection(Section _section) {
		assertEquals(1, _section.getDetails().size());
		assertEquals("ContractTotal", _section.getCaption());
		// testing detail
		assertTrue(_section.getDetails().get(0) instanceof ContractTotalsDetail);
		ContractTotalsDetail detail = (ContractTotalsDetail) _section.getDetails().get(0);
		assertEquals("096-8114-7725", detail.getAccessNbr());
		assertEquals("1007142", detail.getContractNumber());
		assertEquals(56.80, detail.getTotalAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
	}	
	
	/**
	 * @see br.com.auster.tim.bgh.sax.AccountTestBase#getAccountFile()
	 */
	protected String getAccountFile() {
		return "examples/ACCOUNT3.BGH";
	}

}
