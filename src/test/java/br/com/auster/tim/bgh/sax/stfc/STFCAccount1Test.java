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
package br.com.auster.tim.bgh.sax.stfc;

import java.util.Iterator;
import java.util.List;

import br.com.auster.om.invoice.Account;
import br.com.auster.om.invoice.ChargedTax;
import br.com.auster.om.invoice.Invoice;
import br.com.auster.om.invoice.Receipt;
import br.com.auster.om.invoice.Section;
import br.com.auster.om.util.UnitCounter;
import br.com.auster.tim.bgh.BGH2SAX;
import br.com.auster.tim.bgh.sax.AccountTestBase;
import br.com.auster.tim.om.invoice.ContractInfoSection;
import br.com.auster.tim.om.invoice.ContractInfoSubtotalDetail;
import br.com.auster.tim.om.invoice.ContractTotalsDetail;
import br.com.auster.tim.om.invoice.ContractUsageGroupSubsection;
import br.com.auster.tim.om.invoice.GroupItemReceiptDetail;
import br.com.auster.tim.om.invoice.PaystubSection;
import br.com.auster.tim.om.invoice.ServiceReceiptDetail;
import br.com.auster.tim.om.invoice.TIMReceiptDetail;

/**
 * @author framos
 * @version $Id$
 *
 */
public class STFCAccount1Test extends AccountTestBase {



	/**
	 * @see br.com.auster.tim.bgh.sax.AccountTestBase#validateInvoice(Invoice)
	 */
	protected void validateInvoice(Invoice _invoice) {
		super.validateInvoice(_invoice);
		// validates Invoice attributes
		assertEquals("08/09/07", this.dateToString(_invoice.getCycleEndDate()));
		assertEquals("10/08/07", this.dateToString(_invoice.getCycleStartDate()));
		assertEquals("19/09/07", this.dateToString(_invoice.getDueDate()));
		assertEquals("09/09/07", this.dateToString(_invoice.getIssueDate()));
		assertEquals(170.43, _invoice.getTotalAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(0, _invoice.getChargesAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(0, _invoice.getAdjustmentsAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals("45127172348", _invoice.getCnpjCpf());
		// validates Account
		validateAccount(_invoice.getAccount());
		// validates NFs
		assertNotNull(_invoice.getReceipts());
		assertEquals(2, _invoice.getReceipts().size());
		validateReceipts(_invoice.getReceipts());
		assertEquals(4, _invoice.getSections().size());
		validatePaystubSection((Section)_invoice.getSections().get(0));
		validateDetailsSection((Section)_invoice.getSections().get(1), _invoice.getAccount());
	}
	
	protected void validateAccount(Account _account) {
		assertEquals("CLIENTE MG", _account.getAccountName());
		assertEquals("1.15228369", _account.getAccountNumber());
		assertEquals("MG", _account.getAccountState());
		assertEquals("F", _account.getAccountType());
		assertEquals("MG", _account.getCarrierState());
	//	assertEquals("41", _account.getCarrierCode());
	}
	
	protected void validateReceipts(List _receipts) {
		
		for (Iterator it=_receipts.iterator(); it.hasNext();) {
			Receipt rcpt = (Receipt) it.next();
			
			if (rcpt.getTag().equals("11200000")) {
				// NF TIM
				assertEquals("0", rcpt.getReceiptNbr());
				assertEquals("MG", rcpt.getCarrierState());
				assertEquals("TIM Nordeste S.A.", rcpt.getCarrierName());
				assertEquals(77.74, rcpt.getTotalAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		//		assertEquals("41", rcpt.getCarrierCode());
				assertNull(rcpt.getDigitalAuthKey());
				// has 1 groups
				assertNotNull(rcpt.getDetails());
				assertEquals(2, rcpt.getDetails().size());
				
				Iterator itx = rcpt.getDetails().iterator();
				TIMReceiptDetail trd = (TIMReceiptDetail) itx.next();
				assertEquals("Subscriptions", trd.getCaption());
				validateSubscription(trd);
				
				trd = (TIMReceiptDetail) itx.next();
				assertEquals("HomeCallsGroups", trd.getCaption());
				
				assertNotNull(rcpt.getTaxes());
				assertEquals(5, rcpt.getTaxes().size());
				for (Iterator it2=rcpt.getTaxes().iterator(); it2.hasNext();) {
					ChargedTax tax = (ChargedTax) it2.next();
					if ("FUNTTEL".equals(tax.getTaxName())) {
						assertEquals(0.5, tax.getTaxRate(), BGH2SAX.MAX_VALUES_DIFFERENCE);
						assertEquals(83.44, tax.getTaxableAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
						assertEquals(0.41, tax.getTaxAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
					} else if ("FUST".equals(tax.getTaxName())) {
						assertEquals(1, tax.getTaxRate(), BGH2SAX.MAX_VALUES_DIFFERENCE);
						assertEquals(83.44, tax.getTaxableAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
						assertEquals(0.83, tax.getTaxAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
					} else if ("ICMS".equals(tax.getTaxName())) {
						assertEquals(25, tax.getTaxRate(), BGH2SAX.MAX_VALUES_DIFFERENCE);
						assertEquals(77.74, tax.getTaxableAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
						assertEquals(19.43, tax.getTaxAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
					} else if ("COFINS".equals(tax.getTaxName())) {
						assertEquals(3, tax.getTaxRate(), BGH2SAX.MAX_VALUES_DIFFERENCE);
						assertEquals(77.74, tax.getTaxableAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
						assertEquals(2.33, tax.getTaxAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
					} else if ("PIS".equals(tax.getTaxName())) {
						assertEquals(0.65, tax.getTaxRate(), BGH2SAX.MAX_VALUES_DIFFERENCE);
						assertEquals(77.74, tax.getTaxableAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
						assertEquals(0.50, tax.getTaxAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
					} else {
						fail();
					}
				}
				
			} else if (rcpt.getTag().equals("34100000")) {
				// NF LD
				assertEquals("0",  rcpt.getReceiptNbr());
				assertEquals("MG", rcpt.getCarrierState());
				assertEquals("TIM Celular S/A", rcpt.getCarrierName());
				assertEquals(92.69, rcpt.getTotalAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
			//	assertEquals("41", rcpt.getCarrierCode());
				assertNull(rcpt.getDigitalAuthKey());
				// has 1 group
				assertNotNull(rcpt.getDetails());
				assertEquals(1, rcpt.getDetails().size());
				
				TIMReceiptDetail rd = (TIMReceiptDetail) rcpt.getDetails().get(0);
				assertNotNull(rd.getReceiptGroupItems());
				assertEquals(1, rd.getReceiptGroupItems().size());
			    GroupItemReceiptDetail gird = (GroupItemReceiptDetail)rd.getReceiptGroupItems().get(0);
				assertEquals("Chamadas Longa Distância: TIM LD 41", gird.getCaption());
				assertEquals("D", gird.getAreaIndicator());
				assertEquals(11, gird.getEventCount());
				assertEquals(92.69, gird.getTotalAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
				
				assertNotNull(gird.getTaxes());
				assertEquals(1, gird.getTaxes().size());
				
				assertNotNull(rcpt.getTaxes());
				assertEquals(5, rcpt.getTaxes().size());
				for (Iterator it2=rcpt.getTaxes().iterator(); it2.hasNext();) {
					ChargedTax tax = (ChargedTax) it2.next();
					if ("ICMS".equals(tax.getTaxName())) {
						assertEquals(25, tax.getTaxRate(), BGH2SAX.MAX_VALUES_DIFFERENCE);
						assertEquals(92.69, tax.getTaxableAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
						assertEquals(23.17, tax.getTaxAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
					} else if ("PIS".equals(tax.getTaxName())) {
						assertEquals(0.65, tax.getTaxRate(), BGH2SAX.MAX_VALUES_DIFFERENCE);
						assertEquals(92.69, tax.getTaxableAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
						assertEquals(0.60, tax.getTaxAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
					} else if ("COFINS".equals(tax.getTaxName())) {
						assertEquals(3, tax.getTaxRate(), BGH2SAX.MAX_VALUES_DIFFERENCE);
						assertEquals(92.69, tax.getTaxableAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
						assertEquals(2.78, tax.getTaxAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
					} else if ("FUST".equals(tax.getTaxName())) {
					} else if ("FUNTTEL".equals(tax.getTaxName())) {
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
		assertEquals("Plano Básico MG", srd.getServiceId());
		assertEquals("1", srd.getUnitCount());
		assertEquals(30, srd.getNumberOfDays());
		assertEquals(0.97, srd.getProRateFactor(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(59.69, srd.getTotalAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		// taxes
		assertNotNull(srd.getTaxes());
		assertEquals(1, srd.getTaxes().size());
		
		ChargedTax tax = (ChargedTax)srd.getTaxes().get(0);
		assertEquals("ICMS", tax.getTaxName());
		assertEquals(25, tax.getTaxRate(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(14.92, tax.getTaxAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
	}
	
	protected void validateDetailsSection(Section _details, Account _account) {
		assertNotNull(_details);
		assertEquals("Details", _details.getCaption());
		assertEquals("CLIENTE MG", _details.getSectionName());
		assertEquals(170.43, _details.getTotalAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		// has only one ContractInfo subsection
		assertEquals(1, _details.getSubSections().size());
		validateContractInfoDetails((Section)_details.getSubSections().get(0), _account);
	}
	
	protected void validateContractInfoDetails(Section _contract, Account _account) {
		assertTrue(_contract instanceof ContractInfoSection);
		ContractInfoSection info = (ContractInfoSection)_contract;
		assertEquals("031-9189-0419", info.getAccessNbr());
		assertEquals("Plano Básico MG", info.getRatePlan());
		assertEquals("8801393", info.getContractNumber());
		assertEquals("CLIENTE MG", info.getUserName());
		assertTrue(info.isMobile());
		
	//	assertEquals(info.getCarrierCode(), _account.getCarrierCode());
		assertEquals(info.getCarrierState(), _account.getCarrierState());
		
		assertNotNull(info.getSubSections());
		assertEquals(2, info.getSubSections().size());
		validateUsageGroupsSection(info.getSubSections());
		
		assertNotNull(info.getDetails());
		assertEquals(1, info.getDetails().size());
		validateContractSubtotals(info.getDetails());
	}

	
	protected void validateUsageGroupsSection(List _groups) {
		int counter = 0;
		ContractUsageGroupSubsection usageGroup;
		// validating group 1
		Object s = _groups.get(counter++);
		assertTrue(s instanceof ContractUsageGroupSubsection);
		usageGroup = (ContractUsageGroupSubsection)s;
		assertEquals("Chamadas Locais para Outros Celulares", usageGroup.getSectionName());
		assertEquals(UnitCounter.TIME_COUNTER, usageGroup.getUnitType());
		// 00:42:00
		assertEquals(2520, usageGroup.getRoundedVolume().getSeconds(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		//00:41:40
		assertEquals(2500, usageGroup.getUsedVolume().getSeconds(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(5, usageGroup.getQuantity());
		assertEquals(18.05, usageGroup.getTotalAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(0, usageGroup.getFreeUnitsDiscountAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(0, usageGroup.getDiscountAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(usageGroup.getQuantity(), usageGroup.getDetails().size());
		// validating group 2
		s = _groups.get(counter++);
		assertTrue(s instanceof ContractUsageGroupSubsection);
		usageGroup = (ContractUsageGroupSubsection)s;
		assertEquals("Chamadas Longa Distância: TIM LD 41", usageGroup.getSectionName());
		assertEquals(UnitCounter.TIME_COUNTER, usageGroup.getUnitType());
		// 01:29:00
		assertEquals(5340, usageGroup.getRoundedVolume().getSeconds());
		// 01:28:20
		assertEquals(5300, usageGroup.getUsedVolume().getSeconds());
		assertEquals(11, usageGroup.getQuantity());
		assertEquals(92.69, usageGroup.getTotalAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
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
	
	protected void validateContractSubtotals(List _details) {
		Object d = _details.get(0);
		assertTrue(d instanceof ContractInfoSubtotalDetail);
		// testing Voice totals
		ContractInfoSubtotalDetail detail = (ContractInfoSubtotalDetail) d;
		assertEquals("ContractUsageTotalSeconds", detail.getCaption());
		// 02:11:00
		assertEquals(7860, detail.getRoundedVolume().getSeconds());
		// 02:10:00
		assertEquals(7800, detail.getUsedVolume().getSeconds());
		assertEquals(16, detail.getQuantity());
		assertEquals(110.74, detail.getTotalAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(0, detail.getFreeUnitsDiscountAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(0, detail.getDiscountAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
	}

	protected void validatePaystubSection(Section _paystub) {
		
		assertTrue(_paystub instanceof PaystubSection);
		PaystubSection section = (PaystubSection) _paystub;
		assertEquals("TIM Nordeste S.A.", section.getCarrierName());
		assertNotNull(section.getCarrierAddress());
		assertEquals("Av. Raja Gabaglia", section.getCarrierAddress().getAddressStreet());
		assertEquals("1781, 8º e 9º andar", section.getCarrierAddress().getAddressNumber());
		assertEquals("30350-540", section.getCarrierAddress().getPostalCode());
		assertEquals("B. Luxemburgo - Belo Horizonte", section.getCarrierAddress().getCity());
		assertEquals("MG", section.getCarrierAddress().getState());
		assertEquals("CLIENTE MG", section.getCustomerName());
		assertEquals("1.15228369", section.getCustomerCode());
		assertEquals("10/08/07", this.dateToString(section.getCycleStartDate()));
		assertEquals("08/09/07", this.dateToString(section.getCycleEndDate()));
		assertEquals("09/09/07", this.dateToString(section.getIssueDate()));
		assertEquals("19/09/07", this.dateToString(section.getDueDate()));
		assertEquals(170.43, section.getTotalAmount(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(0, section.getPreviousBalance(), BGH2SAX.MAX_VALUES_DIFFERENCE);
		assertEquals(170.43, section.getFinalBalance(), BGH2SAX.MAX_VALUES_DIFFERENCE);
	}
	
	/**
	 * @see br.com.auster.tim.bgh.sax.AccountTestBase#getAccountFile()
	 */
	protected String getAccountFile() {
		return "examples/stfc/ACCOUNT1.STFC.BGH";
	}

}
