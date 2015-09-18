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

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import br.com.auster.om.invoice.Account;
import br.com.auster.om.invoice.ChargedTax;
import br.com.auster.om.invoice.Invoice;
import br.com.auster.om.invoice.Receipt;
import br.com.auster.om.invoice.Section;
import br.com.auster.om.invoice.UsageDetail;
import br.com.auster.om.util.UnitCounter;
import br.com.auster.tim.bgh.BGH2SAX;
import br.com.auster.tim.om.invoice.ContractInfoSection;
import br.com.auster.tim.om.invoice.ContractInfoSubtotalDetail;
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
public class Account2Test extends AccountTestBase {



	/**
	 * @see br.com.auster.tim.bgh.sax.AccountTestBase#validateInvoice(Invoice)
	 */
	protected void validateInvoice(Invoice _invoice) {
		super.validateInvoice(_invoice);
		// validates Invoice attributes
		assertEquals("24/07/06", this.dateToString(_invoice.getCycleEndDate()));
		assertEquals("25/06/06", this.dateToString(_invoice.getCycleStartDate()));
		assertEquals("13/08/06", this.dateToString(_invoice.getDueDate()));
		assertEquals("01/08/06", this.dateToString(_invoice.getIssueDate()));
		assertEquals(0, _invoice.getTotalAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(0, _invoice.getChargesAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(-20.82, _invoice.getAdjustmentsAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		// validates Account
		validateAccount(_invoice.getAccount());
		// validates NFs
		assertNotNull(_invoice.getReceipts());
		assertEquals(1, _invoice.getReceipts().size());
		validateReceipts((Receipt)_invoice.getReceipts().get(0));
		assertEquals(5, _invoice.getSections().size());
		validateDetailsSection((Section)_invoice.getSections().get(0));
		validateTotalsSubscriptionSection((Section)_invoice.getSections().get(1));
		validateTotalsOCCSection((Section)_invoice.getSections().get(2));
		validateTotalsFUSection((Section)_invoice.getSections().get(3));
		validateTotalsGranTotalSection((Section)_invoice.getSections().get(4));
		
	}
	
	protected void validateAccount(Account _account) {
		assertEquals("LUIZ CARLOS CAVALCANTI", _account.getAccountName());
		assertEquals("1.10752124", _account.getAccountNumber());
		assertEquals("RJ", _account.getAccountState());
		assertEquals("F", _account.getAccountType());
		assertEquals("RJ", _account.getCarrierState());
		//assertEquals("41", _account.getCarrierCode());
	}
	
	protected void validateReceipts(Receipt _receipt) {
		
		// NF TIM
		assertEquals("000.000.108-AA", _receipt.getReceiptNbr());
		assertEquals("RJ", _receipt.getCarrierState());
		assertEquals("TIM Celular S.A.", _receipt.getCarrierName());
		//assertEquals("41", _receipt.getCarrierCode());
		assertEquals(0, _receipt.getTotalAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		// has 4 groups
		assertNotNull(_receipt.getDetails());
		assertEquals(6, _receipt.getDetails().size());
		
		for (Iterator it = _receipt.getDetails().iterator(); it.hasNext();) {
			TIMReceiptDetail trd = (TIMReceiptDetail) it.next();
			if ("Subscriptions".equals(trd.getCaption())) {
				validateSubscription(trd);
			} else if ("HomeCallsGroups".equals(trd.getCaption())) {
				validateHomeGroup(trd);
			} else if ("ServiceUsageGroups".equals(trd.getCaption())) {
				validateServiceGroup(trd);
			} else if ("OCCs".equals(trd.getCaption())) {
				validateOCC(trd);
			} else if ("Current FreeUnits".equals(trd.getCaption())) {
				validateCurrentFU(trd);
			} else if ("Previous FreeUnits".equals(trd.getCaption())) {
				validatePreviousFU(trd);
			} else {
				System.out.println(trd.getCaption());
				fail();
			}
		}	
	}
	
	protected void validateCurrentFU(TIMReceiptDetail _trd) {
		assertNotNull(_trd.getReceiptGroupItems());
		assertEquals(4, _trd.getReceiptGroupItems().size());
		// FIRST ITEM
		FreeUnitsReceiptDetail srd = (FreeUnitsReceiptDetail) _trd.getReceiptGroupItems().get(0);
		assertEquals("Pacote Plano Light 40", srd.getServiceId());
		assertEquals("021-8222-0292", srd.getAccessNbr());
		assertEquals("1090173", srd.getContractNumber());
		assertEquals("25/06/06", dateToString(srd.getStartDate()));
		assertEquals("24/07/06", dateToString(srd.getEndDate()));
		assertEquals("Sec", srd.getUnitType());
		assertEquals("00:00:00", srd.getUnitCarriedOverCount());
		assertEquals("00:40:00", srd.getUnitCount());
		assertEquals("00:36:36", srd.getUnitUsedCount());
		assertEquals("00:03:24", srd.getUnitUnusedCount());
		// SECOND ITEM
		srd = (FreeUnitsReceiptDetail) _trd.getReceiptGroupItems().get(1);
		assertEquals("Pcte Domingos - 500min", srd.getServiceId());
		assertEquals("021-8222-0292", srd.getAccessNbr());
		assertEquals("1090173", srd.getContractNumber());
		assertEquals("25/06/06", dateToString(srd.getStartDate()));
		assertEquals("24/07/06", dateToString(srd.getEndDate()));
		assertEquals("Sec", srd.getUnitType());
		assertEquals("00:00:00", srd.getUnitCarriedOverCount());
		assertEquals("08:20:00", srd.getUnitCount());
		assertEquals("00:03:30", srd.getUnitUsedCount());
		assertEquals("08:16:30", srd.getUnitUnusedCount());
		// THIRD ITEM
		srd = (FreeUnitsReceiptDetail) _trd.getReceiptGroupItems().get(2);
		assertEquals("DELETE-PACOTE SMS PROMOCIONAL", srd.getServiceId());
		assertEquals("021-8222-0292", srd.getAccessNbr());
		assertEquals("1090173", srd.getContractNumber());
		assertEquals("25/06/06", dateToString(srd.getStartDate()));
		assertEquals("24/07/06", dateToString(srd.getEndDate()));
		assertEquals("Msg", srd.getUnitType());
		assertEquals("0", srd.getUnitCarriedOverCount());
		assertEquals("99999999", srd.getUnitCount());
		assertEquals("2", srd.getUnitUsedCount());
		assertEquals("99999997", srd.getUnitUnusedCount());
		// FOURTH ITEM
		srd = (FreeUnitsReceiptDetail) _trd.getReceiptGroupItems().get(3);
		assertEquals("DELETE-PACOTE SMS PROMOCIONAL", srd.getServiceId());
		assertEquals("021-8222-0292", srd.getAccessNbr());
		assertEquals("1090173", srd.getContractNumber());
		assertEquals("25/06/06", dateToString(srd.getStartDate()));
		assertEquals("24/07/06", dateToString(srd.getEndDate()));
		assertEquals("Msg", srd.getUnitType());
		assertEquals("0", srd.getUnitCarriedOverCount());
		assertEquals("99999999", srd.getUnitCount());
		assertEquals("1", srd.getUnitUsedCount());
		assertEquals("99999998", srd.getUnitUnusedCount());
	}
	
	protected void validatePreviousFU(TIMReceiptDetail _trd) {
		assertNotNull(_trd.getReceiptGroupItems());
		assertEquals(2, _trd.getReceiptGroupItems().size());
		// FIRST ITEM
		FreeUnitsReceiptDetail srd = (FreeUnitsReceiptDetail) _trd.getReceiptGroupItems().get(0);
		assertEquals("Pcte Domingos - 41min LD41", srd.getServiceId());
		assertEquals("021-8222-0292", srd.getAccessNbr());
		assertEquals("1090173", srd.getContractNumber());
		assertEquals("22/04/06", dateToString(srd.getStartDate()));
		assertEquals("22/04/06", dateToString(srd.getEndDate()));
		assertEquals("Sec", srd.getUnitType());
		assertEquals("00:00:00", srd.getUnitCarriedOverCount());
		assertEquals("00:41:00", srd.getUnitCount());
		assertEquals("00:00:00", srd.getUnitUsedCount());
		assertEquals("00:41:00", srd.getUnitUnusedCount());
		// SECOND ITEM
		srd = (FreeUnitsReceiptDetail) _trd.getReceiptGroupItems().get(1);
		assertEquals("Mega TIM Torpedos 2006", srd.getServiceId());
		assertEquals("021-8222-0292", srd.getAccessNbr());
		assertEquals("1090173", srd.getContractNumber());
		assertEquals("08/03/06", dateToString(srd.getStartDate()));
		assertEquals("08/03/06", dateToString(srd.getEndDate()));
		assertEquals("Msg", srd.getUnitType());
		assertEquals("0", srd.getUnitCarriedOverCount());
		assertEquals("176", srd.getUnitCount());
		assertEquals("0", srd.getUnitUsedCount());
		assertEquals("176", srd.getUnitUnusedCount());
	}
	
	protected void validateSubscription(TIMReceiptDetail _trd) {
		assertNotNull(_trd.getReceiptGroupItems());
		assertEquals(1, _trd.getReceiptGroupItems().size());
		assertEquals(41.9, _trd.getTotalAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		
		ServiceReceiptDetail srd = (ServiceReceiptDetail) _trd.getReceiptGroupItems().get(0);
		assertEquals("Plano Light 40", srd.getServiceId());
		assertEquals("1", srd.getUnitCount());
		assertEquals(30, srd.getNumberOfDays());
		assertEquals(1.00, srd.getProRateFactor(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(41.9, srd.getTotalAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		// taxes
		assertNotNull(srd.getTaxes());
		assertEquals(1, srd.getTaxes().size());
		
		ChargedTax tax = (ChargedTax)srd.getTaxes().get(0);
		assertEquals("ICMS", tax.getTaxName());
		assertEquals(30, tax.getTaxRate(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(12.57, tax.getTaxAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
	}
	
	protected void validateHomeGroup(TIMReceiptDetail _trd) {
		assertNotNull(_trd.getReceiptGroupItems());
		assertEquals(3, _trd.getReceiptGroupItems().size());
		assertEquals(0, _trd.getTotalAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		// FIRST ITEM
		GroupItemReceiptDetail srd = (GroupItemReceiptDetail) _trd.getReceiptGroupItems().get(0);
		assertEquals("Chamadas Locais para Celulares TIM", srd.getCaption());
		assertEquals("D", srd.getAreaIndicator());
		assertEquals(12, srd.getEventCount());
		assertEquals(0, srd.getPromotionAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(0, srd.getTotalAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		// taxes
		assertNotNull(srd.getTaxes());
		assertEquals(1, srd.getTaxes().size());
		
		ChargedTax tax = (ChargedTax)srd.getTaxes().get(0);
		assertEquals("ICMS", tax.getTaxName());
		assertEquals(30, tax.getTaxRate(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(0, tax.getTaxAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		
		// SECOND ITEM
		 srd = (GroupItemReceiptDetail) _trd.getReceiptGroupItems().get(1);
		assertEquals("Chamadas Locais para Outros Celulares", srd.getCaption());
		assertEquals("D", srd.getAreaIndicator());
		assertEquals(8, srd.getEventCount());
		assertEquals(0, srd.getPromotionAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(0, srd.getTotalAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		// taxes
		assertNotNull(srd.getTaxes());
		assertEquals(1, srd.getTaxes().size());
		
		tax = (ChargedTax)srd.getTaxes().get(0);
		assertEquals("ICMS", tax.getTaxName());
		assertEquals(30, tax.getTaxRate(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(0, tax.getTaxAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);	
		
		// THIRD ITEM
		 srd = (GroupItemReceiptDetail) _trd.getReceiptGroupItems().get(2);
		assertEquals("Chamadas Locais para Telefones Fixos", srd.getCaption());
		assertEquals("D", srd.getAreaIndicator());
		assertEquals(12, srd.getEventCount());
		assertEquals(0, srd.getPromotionAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(0, srd.getTotalAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		// taxes
		assertNotNull(srd.getTaxes());
		assertEquals(1, srd.getTaxes().size());
		
		tax = (ChargedTax)srd.getTaxes().get(0);
		assertEquals("ICMS", tax.getTaxName());
		assertEquals(30, tax.getTaxRate(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(0, tax.getTaxAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);	
	}

	protected void validateServiceGroup(TIMReceiptDetail _trd) {
		assertNotNull(_trd.getReceiptGroupItems());
		assertEquals(2, _trd.getReceiptGroupItems().size());
		assertEquals(8.82, _trd.getTotalAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		// FIRST ITEM
		GroupItemReceiptDetail srd = (GroupItemReceiptDetail) _trd.getReceiptGroupItems().get(0);
		assertEquals("TIM Torpedo", srd.getCaption());
		assertEquals("D", srd.getAreaIndicator());
		assertEquals(22, srd.getEventCount());
		assertEquals(0, srd.getPromotionAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(6.84, srd.getTotalAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		// taxes
		assertNotNull(srd.getTaxes());
		assertEquals(1, srd.getTaxes().size());
				
		ChargedTax tax = (ChargedTax)srd.getTaxes().get(0);
		assertEquals("ICMS", tax.getTaxName());
		assertEquals(30, tax.getTaxRate(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(2.05, tax.getTaxAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);	
		
		// SECOND ITEM
		srd = (GroupItemReceiptDetail) _trd.getReceiptGroupItems().get(1);
		assertEquals("TIM VideoMensagem/FotoMensagem para Celular", srd.getCaption());
		assertEquals("D", srd.getAreaIndicator());
		assertEquals(2, srd.getEventCount());
		assertEquals(0, srd.getPromotionAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(1.98, srd.getTotalAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		// taxes
		assertNotNull(srd.getTaxes());
		assertEquals(1, srd.getTaxes().size());
				
		tax = (ChargedTax)srd.getTaxes().get(0);
		assertEquals("ICMS", tax.getTaxName());
		assertEquals(30, tax.getTaxRate(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(0.59, tax.getTaxAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
	}
		
	protected void validateOCC(TIMReceiptDetail _trd) {
		assertNotNull(_trd.getReceiptGroupItems());
		assertEquals(2, _trd.getReceiptGroupItems().size());
		assertEquals(-50.72, _trd.getTotalAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		// FIRST ITEM
		GroupItemReceiptDetail srd = (GroupItemReceiptDetail) _trd.getReceiptGroupItems().get(0);
		assertEquals("Desconto Serviços TIM", srd.getCaption());
		assertEquals(0, srd.getPromotionAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(-29.90, srd.getTotalAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		// taxes
		assertNotNull(srd.getTaxes());
		assertEquals(1, srd.getTaxes().size());
				
		ChargedTax tax = (ChargedTax)srd.getTaxes().get(0);
		assertEquals("ICMS", tax.getTaxName());
		assertEquals(30, tax.getTaxRate(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(-8.97, tax.getTaxAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		
		// SECOND ITEM
		srd = (GroupItemReceiptDetail) _trd.getReceiptGroupItems().get(1);
		assertEquals("OVPMT", srd.getCaption());
		assertEquals(0, srd.getPromotionAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(-20.82, srd.getTotalAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		// taxes
		assertNotNull(srd.getTaxes());
		assertEquals(0, srd.getTaxes().size());
	}	
	
	protected void validateDetailsSection(Section _details) {
		assertNotNull(_details);
		assertEquals("Details", _details.getCaption());
		assertEquals("LUIZ CARLOS CAVALCANTI", _details.getSectionName());
		assertEquals(0, _details.getTotalAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		// has only one ContractInfo subsection
		assertEquals(1, _details.getSubSections().size());
		validateContractInfoDetails((Section)_details.getSubSections().get(0));
	}

	protected void validateContractInfoDetails(Section _contract) {
		assertTrue(_contract instanceof ContractInfoSection);
		ContractInfoSection info = (ContractInfoSection)_contract;
		assertEquals("021-8222-0292", info.getAccessNbr());
		assertEquals("Plano Light 40", info.getRatePlan());
		assertEquals("1090173", info.getContractNumber());
		assertEquals("LUIZ CARLOS CAVALCANTI", info.getUserName());
		
		assertNotNull(info.getSubSections());
		assertEquals(5, info.getSubSections().size());
		validateUsageGroupsSection(info.getSubSections());
		
		assertNotNull(info.getDetails());
		assertEquals(2, info.getDetails().size());
		validateContractSubtotals(info.getDetails());
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
		// 00:11:36
		assertEquals(696, usageGroup.getRoundedVolume().getSeconds());
		// 00:10:17
		assertEquals(617, usageGroup.getUsedVolume().getSeconds());
		assertEquals(12, usageGroup.getQuantity());
		assertEquals(0, usageGroup.getTotalAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(10.27, usageGroup.getFreeUnitsDiscountAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(0, usageGroup.getDiscountAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(usageGroup.getQuantity(), usageGroup.getDetails().size());
		// validating group 2
		s = _groups.get(counter++);
		assertTrue(s instanceof ContractUsageGroupSubsection);
		usageGroup = (ContractUsageGroupSubsection)s;
		assertEquals("Chamadas Locais para Outros Celulares", usageGroup.getSectionName());
		assertEquals(UnitCounter.TIME_COUNTER, usageGroup.getUnitType());
		// 00:10:48
		assertEquals(648, usageGroup.getRoundedVolume().getSeconds());
		// 00:10:02
		assertEquals(602, usageGroup.getUsedVolume().getSeconds());
		assertEquals(8, usageGroup.getQuantity());
		assertEquals(0, usageGroup.getTotalAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(9.58, usageGroup.getFreeUnitsDiscountAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(0, usageGroup.getDiscountAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(usageGroup.getQuantity(), usageGroup.getDetails().size());
		// validating group 3
		s = _groups.get(counter++);
		assertTrue(s instanceof ContractUsageGroupSubsection);
		usageGroup = (ContractUsageGroupSubsection)s;
		assertEquals("Chamadas Locais para Telefones Fixos", usageGroup.getSectionName());
		assertEquals(UnitCounter.TIME_COUNTER, usageGroup.getUnitType());
		// 00:17:42
		assertEquals(1062, usageGroup.getRoundedVolume().getSeconds());
		// 00:16:32
		assertEquals(992, usageGroup.getUsedVolume().getSeconds());
		assertEquals(12, usageGroup.getQuantity());
		assertEquals(0, usageGroup.getTotalAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(15.70, usageGroup.getFreeUnitsDiscountAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(0, usageGroup.getDiscountAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(usageGroup.getQuantity(), usageGroup.getDetails().size());
		// validating group 4
		s = _groups.get(counter++);
		assertTrue(s instanceof ContractUsageGroupSubsection);
		usageGroup = (ContractUsageGroupSubsection)s;
		assertEquals("TIM Torpedo", usageGroup.getSectionName());
		assertEquals(UnitCounter.UNIT_COUNTER, usageGroup.getUnitType());
		// null
		assertEquals(0, usageGroup.getRoundedVolume().getUnits());
		// null
		assertEquals(0, usageGroup.getUsedVolume().getUnits());
		assertEquals(22, usageGroup.getQuantity());
		assertEquals(6.84, usageGroup.getTotalAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(1.08, usageGroup.getFreeUnitsDiscountAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(0, usageGroup.getDiscountAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(usageGroup.getQuantity(), usageGroup.getDetails().size());
		// will check sort of usage details
		Iterator it = usageGroup.getDetails().iterator();
		Date dt1 = ((UsageDetail)it.next()).getDatetime();
		for (; it.hasNext();) {
			Date dt2 = ((UsageDetail)it.next()).getDatetime();
			assertTrue( dt2.after(dt1) );
			dt1 = dt2;
		}
		// validating group 5
		s = _groups.get(counter++);
		assertTrue(s instanceof ContractUsageGroupSubsection);
		usageGroup = (ContractUsageGroupSubsection)s;
		assertEquals("TIM VideoMensagem/FotoMensagem para Celular", usageGroup.getSectionName());
		assertEquals(UnitCounter.UNIT_COUNTER, usageGroup.getUnitType());
		// null
		assertEquals(0, usageGroup.getRoundedVolume().getUnits());
		// null
		assertEquals(0, usageGroup.getUsedVolume().getUnits());
		assertEquals(2, usageGroup.getQuantity());
		assertEquals(1.98, usageGroup.getTotalAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(0, usageGroup.getFreeUnitsDiscountAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(0, usageGroup.getDiscountAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(usageGroup.getQuantity(), usageGroup.getDetails().size());
	}

	protected void validateTotalsSubscriptionSection(Section _section) {
		assertEquals(1, _section.getDetails().size());
		assertEquals("ContractSubscription", _section.getCaption());
		// testing detail
		assertTrue(_section.getDetails().get(0) instanceof ContractTotalsDetail);
		ContractTotalsDetail detail = (ContractTotalsDetail)_section.getDetails().get(0);
		assertEquals("021-8222-0292", detail.getAccessNbr());
		assertEquals("1090173", detail.getContractNumber());
		assertEquals(41.90, detail.getTotalAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
	}
	
	protected void validateTotalsOCCSection(Section _section) {
		assertEquals(1, _section.getDetails().size());
		assertEquals("ContractOCC", _section.getCaption());
		// testing detail
		assertTrue(_section.getDetails().get(0) instanceof ContractTotalsDetail);
		ContractTotalsDetail detail = (ContractTotalsDetail)_section.getDetails().get(0);
		assertEquals("021-8222-0292", detail.getAccessNbr());
		assertEquals("1090173", detail.getContractNumber());
		assertEquals(-29.90, detail.getTotalAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
	}
	
	protected void validateTotalsFUSection(Section _section) {
		assertEquals(1, _section.getDetails().size());
		assertEquals("ContractFU", _section.getCaption());
		// testing detail
		assertTrue(_section.getDetails().get(0) instanceof ContractTotalsDetail);
		ContractTotalsDetail detail = (ContractTotalsDetail)_section.getDetails().get(0);
		assertEquals("021-8222-0292", detail.getAccessNbr());
		assertEquals("1090173", detail.getContractNumber());
		assertEquals(2409, detail.getTotalCount());
	}
		
	protected void validateTotalsGranTotalSection(Section _section) {
		assertEquals(1, _section.getDetails().size());
		assertEquals("ContractTotal", _section.getCaption());
		// testing detail
		assertTrue(_section.getDetails().get(0) instanceof ContractTotalsDetail);
		ContractTotalsDetail detail = (ContractTotalsDetail) _section.getDetails().get(0);
		assertEquals("021-8222-0292", detail.getAccessNbr());
		assertEquals("1090173", detail.getContractNumber());
		assertEquals(12.00, detail.getTotalAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
	}
	
	protected void validateContractSubtotals(List _details) {
		int counter=0;
		Object d = _details.get(counter++);
		assertTrue(d instanceof ContractInfoSubtotalDetail);
		// testing Messages totals
		ContractInfoSubtotalDetail detail = (ContractInfoSubtotalDetail) d;
		assertEquals("ContractUsageTotalMessages", detail.getCaption());
		assertEquals(0, detail.getRoundedVolume().getUnits());
		assertEquals(0, detail.getUsedVolume().getUnits());
		assertEquals(24, detail.getQuantity());
		assertEquals(8.82, detail.getTotalAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(1.08, detail.getFreeUnitsDiscountAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(0, detail.getDiscountAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		// testing Voice totals
		d = _details.get(counter++);
		assertTrue(d instanceof ContractInfoSubtotalDetail);
		detail = (ContractInfoSubtotalDetail) d;
		assertEquals("ContractUsageTotalSeconds", detail.getCaption());
		assertEquals(2406, detail.getRoundedVolume().getSeconds());
		assertEquals(2211, detail.getUsedVolume().getSeconds());
		assertEquals(32, detail.getQuantity());
		assertEquals(0, detail.getTotalAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(35.55, detail.getFreeUnitsDiscountAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(0, detail.getDiscountAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
	}
	
	/**
	 * @see br.com.auster.tim.bgh.sax.AccountTestBase#getAccountFile()
	 */
	protected String getAccountFile() {
		return "examples/ACCOUNT2.BGH";
	}

}
