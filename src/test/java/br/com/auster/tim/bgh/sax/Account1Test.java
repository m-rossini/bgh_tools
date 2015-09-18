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

import br.com.auster.om.invoice.Account;
import br.com.auster.om.invoice.ChargedTax;
import br.com.auster.om.invoice.Invoice;
import br.com.auster.om.invoice.Receipt;
import br.com.auster.om.invoice.Section;
import br.com.auster.om.util.UnitCounter;
import br.com.auster.tim.bgh.BGH2SAX;
import br.com.auster.tim.om.invoice.ContractInfoSection;
import br.com.auster.tim.om.invoice.ContractInfoSubtotalDetail;
import br.com.auster.tim.om.invoice.ContractTotalsDetail;
import br.com.auster.tim.om.invoice.ContractUsageGroupSubsection;
import br.com.auster.tim.om.invoice.GroupItemReceiptDetail;
import br.com.auster.tim.om.invoice.PaystubSection;
import br.com.auster.tim.om.invoice.ServiceReceiptDetail;
import br.com.auster.tim.om.invoice.TIMReceiptDetail;
import br.com.auster.tim.om.invoice.TIMUsageDetail;

/**
 * @author framos
 * @version $Id$
 *
 */
public class Account1Test extends AccountTestBase {



	/**
	 * @see br.com.auster.tim.bgh.sax.AccountTestBase#validateInvoice(Invoice)
	 */
	protected void validateInvoice(Invoice _invoice) {
		super.validateInvoice(_invoice);
		// validates Invoice attributes
		assertEquals("24/07/06", this.dateToString(_invoice.getCycleEndDate()));
		assertEquals("25/06/06", this.dateToString(_invoice.getCycleStartDate()));
		assertEquals("15/08/06", this.dateToString(_invoice.getDueDate()));
		assertEquals("01/08/06", this.dateToString(_invoice.getIssueDate()));
		assertEquals(116.46, _invoice.getTotalAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(0, _invoice.getChargesAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(0, _invoice.getAdjustmentsAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals("22371529850", _invoice.getCnpjCpf());
		// validates Account
		validateAccount(_invoice.getAccount());
		// validates NFs
		assertNotNull(_invoice.getReceipts());
		assertEquals(2, _invoice.getReceipts().size());
		validateReceipts(_invoice.getReceipts());
		assertEquals(5, _invoice.getSections().size());
		validatePaystubSection((Section)_invoice.getSections().get(0));
		validateDetailsSection((Section)_invoice.getSections().get(1), _invoice.getAccount());
		validateTotalsSubscriptionSection((Section)_invoice.getSections().get(2));
		validateTotalsFUSection((Section)_invoice.getSections().get(3));
		validateTotalsGranTotalSection((Section)_invoice.getSections().get(4));
	}
	
	protected void validateAccount(Account _account) {
		assertEquals("MARCIA REGINA CHEFER BOTTENCOURT", _account.getAccountName());
		assertEquals("1.12727509", _account.getAccountNumber());
		assertEquals("PR", _account.getAccountState());
		assertEquals("F", _account.getAccountType());
		assertEquals("PR", _account.getCarrierState());
		//assertEquals("41", _account.getCarrierCode());
	}
	
	protected void validateReceipts(List _receipts) {
		
		for (Iterator it=_receipts.iterator(); it.hasNext();) {
			Receipt rcpt = (Receipt) it.next();
			
			if (rcpt.getTag().equals("11200000")) {
				// NF TIM
				assertEquals("000.000.088-AB", rcpt.getReceiptNbr());
				assertEquals("PR", rcpt.getCarrierState());
				assertEquals("Tim Sul S/A", rcpt.getCarrierName());
				assertEquals(114.83, rcpt.getTotalAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
				//assertEquals("41", rcpt.getCarrierCode());
				assertEquals("4183.D411.457F.6E54.0029.97BC.9F30.3809", rcpt.getDigitalAuthKey());
				// has 3 groups
				assertNotNull(rcpt.getDetails());
				assertEquals(4, rcpt.getDetails().size());
				
				for (Iterator it2=rcpt.getDetails().iterator(); it2.hasNext();) {
					TIMReceiptDetail trd = (TIMReceiptDetail) it2.next();
					if ("Subscriptions".equals(trd.getCaption())) {
						validateSubscription(trd);
					} else if ("HomeCallsGroups".equals(trd.getCaption())) {
						validateHomeGroup(trd);
					} else if ("ServiceUsageGroups".equals(trd.getCaption())) {
						validateServiceGroup(trd);
					} else if ("Current FreeUnits".equals(trd.getCaption())) {
						// will not validate FreeUnits here
					} else {
						fail();
					}
				}
				
				assertNotNull(rcpt.getTaxes());
				assertEquals(5, rcpt.getTaxes().size());
				for (Iterator it2=rcpt.getTaxes().iterator(); it2.hasNext();) {
					ChargedTax tax = (ChargedTax) it2.next();
					if ("FUNTTEL".equals(tax.getTaxName())) {
						assertEquals(0.5, tax.getTaxRate(), BGH2SAX.MAX_VALUES_DIFFERENCE);
						assertEquals(78.82, tax.getTaxableAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
						assertEquals(0.39, tax.getTaxAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
					} else if ("FUST".equals(tax.getTaxName())) {
						assertEquals(1, tax.getTaxRate(), BGH2SAX.MAX_VALUES_DIFFERENCE);
						assertEquals(78.82, tax.getTaxableAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
						assertEquals(0.78, tax.getTaxAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
					} else if ("ICMS".equals(tax.getTaxName())) {
						assertEquals(27, tax.getTaxRate(), BGH2SAX.MAX_VALUES_DIFFERENCE);
						assertEquals(114.83, tax.getTaxableAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
						assertEquals(31, tax.getTaxAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
					} else if ("COFINS".equals(tax.getTaxName())) {
						assertEquals(3, tax.getTaxRate(), BGH2SAX.MAX_VALUES_DIFFERENCE);
						assertEquals(114.83, tax.getTaxableAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
						assertEquals(3.44, tax.getTaxAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
					} else if ("PIS".equals(tax.getTaxName())) {
						assertEquals(0.65, tax.getTaxRate(), BGH2SAX.MAX_VALUES_DIFFERENCE);
						assertEquals(114.83, tax.getTaxableAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
						assertEquals(0.74, tax.getTaxAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
					} else {
						fail();
					}
				}
				
			} else if (rcpt.getTag().equals("34100000")) {
				// NF LD
				assertEquals("000.000.044-C",  rcpt.getReceiptNbr());
				assertEquals("PR", rcpt.getCarrierState());
				assertEquals("Brasil Telecom S.A", rcpt.getCarrierName());
				assertEquals(1.63, rcpt.getTotalAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
				//assertEquals("14", rcpt.getCarrierCode());
				assertEquals("2973.BC46.783D.5713.8C41.E346.4707.FDD1", rcpt.getDigitalAuthKey());
				// has 1 group
				assertNotNull(rcpt.getDetails());
				assertEquals(1, rcpt.getDetails().size());
				
				TIMReceiptDetail rd = (TIMReceiptDetail) rcpt.getDetails().get(0);
				assertNotNull(rd.getReceiptGroupItems());
				assertEquals(1, rd.getReceiptGroupItems().size());
			    GroupItemReceiptDetail gird = (GroupItemReceiptDetail)rd.getReceiptGroupItems().get(0);
				assertEquals("Chamadas Longa Distância: Brasil Telecom", gird.getCaption());
				assertEquals("R", gird.getAreaIndicator());
				assertEquals(1, gird.getEventCount());
				assertEquals(1.63, gird.getTotalAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
				
				assertNotNull(gird.getTaxes());
				assertEquals(1, gird.getTaxes().size());
				
				assertNotNull(rcpt.getTaxes());
				assertEquals(3, rcpt.getTaxes().size());
				for (Iterator it2=rcpt.getTaxes().iterator(); it2.hasNext();) {
					ChargedTax tax = (ChargedTax) it2.next();
					if ("ICMS BR Telecom".equals(tax.getTaxName())) {
						assertEquals(27, tax.getTaxRate(), BGH2SAX.MAX_VALUES_DIFFERENCE);
						assertEquals(1.63, tax.getTaxableAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
						assertEquals(0.44, tax.getTaxAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
					} else if ("PIS-BR Telecom".equals(tax.getTaxName())) {
						assertEquals(0.65, tax.getTaxRate(), BGH2SAX.MAX_VALUES_DIFFERENCE);
						assertEquals(1.63, tax.getTaxableAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
						assertEquals(0.01, tax.getTaxAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
					} else if ("COFINS-BR Telecom".equals(tax.getTaxName())) {
						assertEquals(3, tax.getTaxRate(), BGH2SAX.MAX_VALUES_DIFFERENCE);
						assertEquals(1.63, tax.getTaxableAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
						assertEquals(0.04, tax.getTaxAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
					} else {
						fail();
					}
				}
				
			} else {
				fail();
			}
		}
	}
	
	protected void validateSubscription(TIMReceiptDetail _trd) {
		assertNotNull(_trd.getReceiptGroupItems());
		assertEquals(1, _trd.getReceiptGroupItems().size());
		
		ServiceReceiptDetail srd = (ServiceReceiptDetail) _trd.getReceiptGroupItems().get(0);
		assertEquals("Plano T Você GSM PR", srd.getServiceId());
		assertEquals("1", srd.getUnitCount());
		assertEquals(30, srd.getNumberOfDays());
		assertEquals(1.00, srd.getProRateFactor(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(26.9, srd.getTotalAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		// taxes
		assertNotNull(srd.getTaxes());
		assertEquals(1, srd.getTaxes().size());
		
		ChargedTax tax = (ChargedTax)srd.getTaxes().get(0);
		assertEquals("ICMS", tax.getTaxName());
		assertEquals(27, tax.getTaxRate(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(7.26, tax.getTaxAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
	}
	
	protected void validateHomeGroup(TIMReceiptDetail _trd) {
		assertNotNull(_trd.getReceiptGroupItems());
		assertEquals(3, _trd.getReceiptGroupItems().size());
		assertEquals(76.11, _trd.getTotalAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		// FIRST ITEM
		GroupItemReceiptDetail srd = (GroupItemReceiptDetail) _trd.getReceiptGroupItems().get(0);
		assertEquals("Chamadas Locais para Celulares TIM", srd.getCaption());
		assertEquals("D", srd.getAreaIndicator());
		assertEquals(51, srd.getEventCount());
		assertEquals(00, srd.getPromotionAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(34.35, srd.getTotalAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		// taxes
		assertNotNull(srd.getTaxes());
		assertEquals(1, srd.getTaxes().size());
		
		ChargedTax tax = (ChargedTax)srd.getTaxes().get(0);
		assertEquals("ICMS", tax.getTaxName());
		assertEquals(27, tax.getTaxRate(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(9.27, tax.getTaxAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		
		// SECOND ITEM
		 srd = (GroupItemReceiptDetail) _trd.getReceiptGroupItems().get(1);
		assertEquals("Chamadas Locais para Outros Celulares", srd.getCaption());
		assertEquals("D", srd.getAreaIndicator());
		assertEquals(3, srd.getEventCount());
		assertEquals(00, srd.getPromotionAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(1.93, srd.getTotalAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		// taxes
		assertNotNull(srd.getTaxes());
		assertEquals(1, srd.getTaxes().size());
		
		tax = (ChargedTax)srd.getTaxes().get(0);
		assertEquals("ICMS", tax.getTaxName());
		assertEquals(27, tax.getTaxRate(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(0.52, tax.getTaxAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);	
		
		// THIRD ITEM
		 srd = (GroupItemReceiptDetail) _trd.getReceiptGroupItems().get(2);
		assertEquals("Chamadas Locais para Telefones Fixos", srd.getCaption());
		assertEquals("D", srd.getAreaIndicator());
		assertEquals(35, srd.getEventCount());
		assertEquals(00, srd.getPromotionAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(39.83, srd.getTotalAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		// taxes
		assertNotNull(srd.getTaxes());
		assertEquals(1, srd.getTaxes().size());
		
		tax = (ChargedTax)srd.getTaxes().get(0);
		assertEquals("ICMS", tax.getTaxName());
		assertEquals(27, tax.getTaxRate(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(10.75, tax.getTaxAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);	
	}

	protected void validateServiceGroup(TIMReceiptDetail _trd) {
		assertNotNull(_trd.getReceiptGroupItems());
		assertEquals(2, _trd.getReceiptGroupItems().size());
		assertEquals(11.82, _trd.getTotalAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		// FIRST ITEM
		GroupItemReceiptDetail srd = (GroupItemReceiptDetail) _trd.getReceiptGroupItems().get(0);
		assertEquals("TIM Torpedo", srd.getCaption());
		assertEquals("D", srd.getAreaIndicator());
		assertEquals(4, srd.getEventCount());
		assertEquals(00, srd.getPromotionAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(1.44, srd.getTotalAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		// taxes
		assertNotNull(srd.getTaxes());
		assertEquals(1, srd.getTaxes().size());
				
		ChargedTax tax = (ChargedTax)srd.getTaxes().get(0);
		assertEquals("ICMS", tax.getTaxName());
		assertEquals(27, tax.getTaxRate(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(0.38, tax.getTaxAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);	
		
		// SECOND ITEM
		srd = (GroupItemReceiptDetail) _trd.getReceiptGroupItems().get(1);
		assertEquals("TIM Wap Fast", srd.getCaption());
		assertEquals("D", srd.getAreaIndicator());
		assertEquals(18, srd.getEventCount());
		assertEquals(00, srd.getPromotionAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(10.38, srd.getTotalAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		// taxes
		assertNotNull(srd.getTaxes());
		assertEquals(1, srd.getTaxes().size());
				
		tax = (ChargedTax)srd.getTaxes().get(0);
		assertEquals("ICMS", tax.getTaxName());
		assertEquals(27, tax.getTaxRate(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(2.80, tax.getTaxAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
	}
	
	protected void validateDetailsSection(Section _details, Account _account) {
		assertNotNull(_details);
		assertEquals("Details", _details.getCaption());
		assertEquals("MARCIA REGINA CHEFER BOTTENCOURT", _details.getSectionName());
		assertEquals(116.46, _details.getTotalAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		// has only one ContractInfo subsection
		assertEquals(1, _details.getSubSections().size());
		validateContractInfoDetails((Section)_details.getSubSections().get(0), _account);
	}
	
	protected void validateContractInfoDetails(Section _contract, Account _account) {
		assertTrue(_contract instanceof ContractInfoSection);
		ContractInfoSection info = (ContractInfoSection)_contract;
		assertEquals("044-9923-4443", info.getAccessNbr());
		assertEquals("Plano T Você GSM PR", info.getRatePlan());
		assertEquals("4618517", info.getContractNumber());
		assertEquals("MARCIA REGINA CHEFER BOTTENCOURT", info.getUserName());
		
		//assertEquals(info.getCarrierCode(), _account.getCarrierCode());
		assertEquals(info.getCarrierState(), _account.getCarrierState());
		
		assertTrue(info.isMobile());
		
		assertNotNull(info.getSubSections());
		assertEquals(6, info.getSubSections().size());
		validateUsageGroupsSection(info.getSubSections());
		
		assertNotNull(info.getDetails());
		assertEquals(3, info.getDetails().size());
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
		// 00:45:06
		assertEquals(2706, usageGroup.getRoundedVolume().getSeconds(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		// 00:38:36
		assertEquals(2316, usageGroup.getUsedVolume().getSeconds(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(51, usageGroup.getQuantity());
		assertEquals(34.35, usageGroup.getTotalAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(1.92, usageGroup.getFreeUnitsDiscountAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(0, usageGroup.getDiscountAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(usageGroup.getQuantity(), usageGroup.getDetails().size());
		validateFirstCallForVoiceGroup(usageGroup.getDetails().get(0));
		// validating group 2
		s = _groups.get(counter++);
		assertTrue(s instanceof ContractUsageGroupSubsection);
		usageGroup = (ContractUsageGroupSubsection)s;
		assertEquals("Chamadas Locais para Outros Celulares", usageGroup.getSectionName());
		assertEquals(UnitCounter.TIME_COUNTER, usageGroup.getUnitType());
		// 00:02:24
		assertEquals(144, usageGroup.getRoundedVolume().getSeconds());
		// 00:01:58
		assertEquals(118, usageGroup.getUsedVolume().getSeconds());
		assertEquals(3, usageGroup.getQuantity());
		assertEquals(1.93, usageGroup.getTotalAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(0, usageGroup.getFreeUnitsDiscountAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(0, usageGroup.getDiscountAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(usageGroup.getQuantity(), usageGroup.getDetails().size());
		// validating group 3
		s = _groups.get(counter++);
		assertTrue(s instanceof ContractUsageGroupSubsection);
		usageGroup = (ContractUsageGroupSubsection)s;
		assertEquals("Chamadas Locais para Telefones Fixos", usageGroup.getSectionName());
		assertEquals(UnitCounter.TIME_COUNTER, usageGroup.getUnitType());
		// 00:57:00
		assertEquals(3420, usageGroup.getRoundedVolume().getSeconds());
		// 00:54:21
		assertEquals(3261, usageGroup.getUsedVolume().getSeconds());
		assertEquals(35, usageGroup.getQuantity());
		assertEquals(39.83, usageGroup.getTotalAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(6.14, usageGroup.getFreeUnitsDiscountAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(0, usageGroup.getDiscountAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(usageGroup.getQuantity(), usageGroup.getDetails().size());
		// validating group 4
		s = _groups.get(counter++);
		assertTrue(s instanceof ContractUsageGroupSubsection);
		usageGroup = (ContractUsageGroupSubsection)s;
		assertEquals("TIM Torpedo", usageGroup.getSectionName());
		assertEquals(UnitCounter.UNIT_COUNTER, usageGroup.getUnitType());
		assertEquals(usageGroup.getRoundedVolume().getUnits(), 0);
		assertEquals(usageGroup.getUsedVolume().getUnits(), 0);
		assertEquals(4, usageGroup.getQuantity());
		assertEquals(1.44, usageGroup.getTotalAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(0, usageGroup.getFreeUnitsDiscountAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(0, usageGroup.getDiscountAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(usageGroup.getQuantity(), usageGroup.getDetails().size());
		validateFirstCallForEventGroup(usageGroup.getDetails().get(0));
		// validating group 5
		s = _groups.get(counter++);
		assertTrue(s instanceof ContractUsageGroupSubsection);
		usageGroup = (ContractUsageGroupSubsection)s;
		assertEquals("TIM Wap Fast", usageGroup.getSectionName());
		assertEquals(UnitCounter.DATA_COUNTER, usageGroup.getUnitType());
		// 523.70 KB
		assertEquals(523.7, usageGroup.getRoundedVolume().getKBytes(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		// 523.70 KB
		assertEquals(523.7, usageGroup.getUsedVolume().getKBytes(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(18, usageGroup.getQuantity());
		assertEquals(10.38, usageGroup.getTotalAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(9.99, usageGroup.getFreeUnitsDiscountAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(0, usageGroup.getDiscountAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(usageGroup.getQuantity(), usageGroup.getDetails().size());
		validateFirstCallForDataGroup(usageGroup.getDetails().get(0));
		// validating group6
		s = _groups.get(counter++);
		assertTrue(s instanceof ContractUsageGroupSubsection);
		usageGroup = (ContractUsageGroupSubsection)s;
		assertEquals("Chamadas Longa Distância: Brasil Telecom", usageGroup.getSectionName());
		assertEquals(UnitCounter.TIME_COUNTER, usageGroup.getUnitType());
		// 00:01:06
		assertEquals(66, usageGroup.getRoundedVolume().getSeconds());
		// 00:01:06
		assertEquals(66, usageGroup.getUsedVolume().getSeconds());
		assertEquals(1, usageGroup.getQuantity());
		assertEquals(1.63, usageGroup.getTotalAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
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
		assertEquals("044-9923-4443", detail.getAccessNbr());
		assertEquals("4618517", detail.getContractNumber());
		assertEquals(26.90, detail.getTotalAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
	}
	
	protected void validateTotalsFUSection(Section _section) {
		assertEquals(1, _section.getDetails().size());
		assertEquals("ContractFU", _section.getCaption());
		// testing detail
		assertTrue(_section.getDetails().get(0) instanceof ContractTotalsDetail);
		ContractTotalsDetail detail = (ContractTotalsDetail)_section.getDetails().get(0);
		assertEquals("044-9923-4443", detail.getAccessNbr());
		assertEquals("4618517", detail.getContractNumber());
		assertEquals(600, detail.getTotalCount());
	}

	protected void validateTotalsGranTotalSection(Section _section) {
		assertEquals(1, _section.getDetails().size());
		assertEquals("ContractTotal", _section.getCaption());
		// testing detail
		assertTrue(_section.getDetails().get(0) instanceof ContractTotalsDetail);
		ContractTotalsDetail detail = (ContractTotalsDetail)_section.getDetails().get(0);
		assertEquals("044-9923-4443", detail.getAccessNbr());
		assertEquals("4618517", detail.getContractNumber());
		assertEquals(26.90, detail.getTotalAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
	}
	
	protected void validateFirstCallForVoiceGroup(Object _detail) {
		assertTrue(_detail instanceof TIMUsageDetail);
		TIMUsageDetail usage = (TIMUsageDetail) _detail;
		assertEquals("27/06/06", this.dateToString(usage.getUsageDate()));
		assertEquals("16:03:53", this.timeToString(usage.getUsageTime()));
		assertEquals("PR AREA 44", usage.getOriginCity());
		assertEquals("PR", usage.getOriginState());
		assertEquals("44", usage.getOriginAreaCode());
		assertEquals("PR AREA 44", usage.getDestinationCity());
		assertEquals("PR", usage.getDestinationState());
		assertEquals("44", usage.getDestinationAreaCode());
		assertEquals("9978-2377", usage.getCalledNumber());
		assertEquals("N", usage.getTariffClass());
		assertNull(usage.getType());
		assertEquals(30, usage.getRoundedDuration().getSeconds());
		assertEquals(29, usage.getUsageDuration().getSeconds());
		assertEquals("TS11", usage.getSvcId());
		assertEquals("VCTS", usage.getTariff());
		assertEquals("H", usage.getArea());
		assertEquals("N/C", usage.getCallClass());
		assertEquals("F", usage.getFreeUnitIndicator());
		assertEquals(0.4, usage.getFreeUnitDiscountAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(0, usage.getDiscountAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(0, usage.getTotalAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(1, usage.getTaxes().size());
		ChargedTax tax = (ChargedTax)usage.getTaxes().get(0);
		assertEquals("ICMS", tax.getTaxName());
		assertEquals(27, tax.getTaxRate(), BGH2SAX.MAX_VALUES_DIFFERENCE);
	}

	protected void validateFirstCallForEventGroup(Object _detail) {
		assertTrue(_detail instanceof TIMUsageDetail);
		TIMUsageDetail usage = (TIMUsageDetail) _detail;
		assertEquals("11/07/06", this.dateToString(usage.getUsageDate()));
		assertEquals("07:04:23", this.timeToString(usage.getUsageTime()));
		assertEquals("PR AREA 44", usage.getOriginCity());
		assertEquals("PR", usage.getOriginState());
		assertEquals("44", usage.getOriginAreaCode());
		assertEquals("PR AREA 44", usage.getDestinationCity());
		assertEquals("PR", usage.getDestinationState());
		assertEquals("44", usage.getDestinationAreaCode());
		assertEquals("554499690564", usage.getCalledNumber());
		assertEquals("N", usage.getTariffClass());
		assertNull(usage.getType());
		assertEquals(0, usage.getRoundedDuration().getUnits());
		assertEquals(0, usage.getUsageDuration().getUnits());
		assertEquals("TS22", usage.getSvcId());
		assertEquals("TORT", usage.getTariff());
		assertEquals("H", usage.getArea());
		assertEquals("N/C", usage.getCallClass());
		assertEquals("N", usage.getFreeUnitIndicator());
		assertEquals(0, usage.getFreeUnitDiscountAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(0, usage.getDiscountAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(0.36, usage.getTotalAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(1, usage.getTaxes().size());
		ChargedTax tax = (ChargedTax)usage.getTaxes().get(0);
		assertEquals("ICMS", tax.getTaxName());
		assertEquals(27, tax.getTaxRate(), BGH2SAX.MAX_VALUES_DIFFERENCE);
	}
	
	protected void validateFirstCallForDataGroup(Object _detail) {
		assertTrue(_detail instanceof TIMUsageDetail);
		TIMUsageDetail usage = (TIMUsageDetail) _detail;
		assertEquals("29/06/06", this.dateToString(usage.getUsageDate()));
		assertEquals("16:25:58", this.timeToString(usage.getUsageTime()));
		assertEquals("ÁREA CENTRO SUL", usage.getOriginCity());
		assertEquals(null, usage.getDestinationState());
		assertEquals(null, usage.getDestinationAreaCode());
		assertEquals("-", usage.getDestinationCity());
		assertEquals(null, usage.getDestinationState());
		assertEquals(null, usage.getDestinationAreaCode());
		assertEquals("-", usage.getCalledNumber());
		assertEquals("N", usage.getTariffClass());
		assertEquals("-", usage.getType());
		assertEquals(81.92, usage.getRoundedDuration().getKBytes(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(81.92, usage.getUsageDuration().getKBytes(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals("BSG1", usage.getSvcId());
		assertEquals("GPR2", usage.getTariff());
		assertEquals("H", usage.getArea());
		assertEquals("N/C", usage.getCallClass());
		assertEquals("N", usage.getFreeUnitIndicator());
		assertEquals(1.57, usage.getFreeUnitDiscountAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(0, usage.getDiscountAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(1.63, usage.getTotalAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(1, usage.getTaxes().size());
		ChargedTax tax = (ChargedTax)usage.getTaxes().get(0);
		assertEquals("ICMS", tax.getTaxName());
		assertEquals(27, tax.getTaxRate(), BGH2SAX.MAX_VALUES_DIFFERENCE);
	}

	protected void validateContractSubtotals(List _details) {
		int counter=0;
		Object d = _details.get(counter++);
		assertTrue(d instanceof ContractInfoSubtotalDetail);
		// testing Data totals
		ContractInfoSubtotalDetail detail = (ContractInfoSubtotalDetail) d;
		assertEquals("ContractUsageTotalBytes", detail.getCaption());
		assertEquals(523.7, detail.getRoundedVolume().getKBytes(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(523.7, detail.getUsedVolume().getKBytes(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(18, detail.getQuantity());
		assertEquals(10.38, detail.getTotalAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(9.99, detail.getFreeUnitsDiscountAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(0, detail.getDiscountAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		// testing Messages totals
		d = _details.get(counter++);
		assertTrue(d instanceof ContractInfoSubtotalDetail);
		detail = (ContractInfoSubtotalDetail) d;
		assertEquals("ContractUsageTotalMessages", detail.getCaption());
		assertEquals(0, detail.getRoundedVolume().getSeconds());
		assertEquals(0, detail.getUsedVolume().getSeconds());
		assertEquals(4, detail.getQuantity());
		assertEquals(1.44, detail.getTotalAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(0, detail.getFreeUnitsDiscountAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(0, detail.getDiscountAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		// testing Voice totals
		d = _details.get(counter++);
		assertTrue(d instanceof ContractInfoSubtotalDetail);
		detail = (ContractInfoSubtotalDetail) d;
		assertEquals("ContractUsageTotalSeconds", detail.getCaption());
		assertEquals(6336, detail.getRoundedVolume().getSeconds());
		assertEquals(5761, detail.getUsedVolume().getSeconds());
		assertEquals(90, detail.getQuantity());
		assertEquals(77.74, detail.getTotalAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(8.06, detail.getFreeUnitsDiscountAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(0, detail.getDiscountAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
	}

	protected void validatePaystubSection(Section _paystub) {
		assertTrue(_paystub instanceof PaystubSection);
		PaystubSection section = (PaystubSection) _paystub;
		assertEquals("Tim Sul S/A", section.getCarrierName());
		assertNotNull(section.getCarrierAddress());
		assertEquals("Rua Comendador Araujo", section.getCarrierAddress().getAddressStreet());
		assertEquals("299 - Centro", section.getCarrierAddress().getAddressNumber());
		assertEquals("80420-000", section.getCarrierAddress().getPostalCode());
		assertEquals("Curitiba", section.getCarrierAddress().getCity());
		assertEquals("PR", section.getCarrierAddress().getState());
		assertEquals("MARCIA REGINA CHEFER BOTTENCOURT", section.getCustomerName());
		assertEquals("1.12727509", section.getCustomerCode());
		assertEquals("25/06/06", this.dateToString(section.getCycleStartDate()));
		assertEquals("24/07/06", this.dateToString(section.getCycleEndDate()));
		assertEquals("01/08/06", this.dateToString(section.getIssueDate()));
		assertEquals("15/08/06", this.dateToString(section.getDueDate()));
		assertEquals(116.46, section.getTotalAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(57.91, section.getPreviousBalance(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(116.46, section.getFinalBalance(), BGH2SAX.MAX_VALUES_DIFFERENCE);
	}
	
	/**
	 * @see br.com.auster.tim.bgh.sax.AccountTestBase#getAccountFile()
	 */
	protected String getAccountFile() {
		return "examples/ACCOUNT1.BGH";
	}

}
