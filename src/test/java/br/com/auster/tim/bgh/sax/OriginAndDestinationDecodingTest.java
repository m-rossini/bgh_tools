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
 * Created on 12/12/2006
 */
package br.com.auster.tim.bgh.sax;

import br.com.auster.tim.om.invoice.TIMUsageDetail;
import junit.framework.TestCase;

/**
 * @author framos
 * @version $Id$
 *
 */
public class OriginAndDestinationDecodingTest extends TestCase {

	
	public void testDataUsage() {
		TIMUsageDetail detail = new TIMUsageDetail();
		detail.setOriginCity("TIM AREA 11");
		detail.setDestinationCity("-");
		ContractUsageDetailsHandler handler = new ContractUsageDetailsHandler();
		handler.setStateAndArea(detail);
		assertEquals("11", detail.getOriginAreaCode());
		assertEquals(null, detail.getDestinationAreaCode());
		assertEquals("TIM", detail.getOriginState());
		assertEquals(null, detail.getDestinationState());
	}

	public void testInternationalUsage() {
		TIMUsageDetail detail = new TIMUsageDetail();
		detail.setOriginCity("TIM AREA 11");
		detail.setDestinationCity("BRAZIL");
		ContractUsageDetailsHandler handler = new ContractUsageDetailsHandler();
		handler.setStateAndArea(detail);
		assertEquals("11", detail.getOriginAreaCode());
		assertEquals(null, detail.getDestinationAreaCode());
		assertEquals("TIM", detail.getOriginState());
		assertEquals(null, detail.getDestinationState());
	}
	
	public void testVC1UsageOK() {
		TIMUsageDetail detail = new TIMUsageDetail();
		detail.setOriginCity("SP AREA 11");
		detail.setDestinationCity("SP AREA 11");
		ContractUsageDetailsHandler handler = new ContractUsageDetailsHandler();
		handler.setStateAndArea(detail);
		assertEquals("11", detail.getOriginAreaCode());
		assertEquals("11", detail.getDestinationAreaCode());
		assertEquals("SP", detail.getOriginState());
		assertEquals("SP", detail.getDestinationState());
	}
	
	public void testVC1UsageNOK() {
		TIMUsageDetail detail = new TIMUsageDetail();
		detail.setOriginCity("SP AREA 11");
		detail.setDestinationCity("SP-AREA-11");
		ContractUsageDetailsHandler handler = new ContractUsageDetailsHandler();
		handler.setStateAndArea(detail);
		assertEquals("11", detail.getOriginAreaCode());
		assertEquals(null, detail.getDestinationAreaCode());
		assertEquals("SP", detail.getOriginState());
		assertEquals(null, detail.getDestinationState());
	}	

	public void testVC1FixoUsageOK() {
		TIMUsageDetail detail = new TIMUsageDetail();
		detail.setOriginCity("SP AREA 11");
		detail.setDestinationCity("SP FIXO - AREA 11");
		ContractUsageDetailsHandler handler = new ContractUsageDetailsHandler();
		handler.setStateAndArea(detail);
		assertEquals("11", detail.getOriginAreaCode());
		assertEquals("11", detail.getDestinationAreaCode());
		assertEquals("SP", detail.getOriginState());
		assertEquals("SP", detail.getDestinationState());
	}
	
}
