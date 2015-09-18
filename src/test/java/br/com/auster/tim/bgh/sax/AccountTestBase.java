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

import java.text.SimpleDateFormat;
import java.util.Date;

import br.com.auster.om.invoice.Invoice;
import br.com.auster.tim.bgh.BGH2SAX;
import junit.framework.TestCase;

/**
 * @author framos
 * @version $Id$
 */
public abstract class AccountTestBase extends TestCase {

	
	public void testAccount() {
		BGH2SAX loader = new BGH2SAX();
		try {
			// load file into OM
			Invoice invoice = loader.run(getAccountFile());
			validateInvoice(invoice);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	protected void validateInvoice(Invoice _invoice) {
		assertNotNull(_invoice);
		assertNotNull(_invoice.getAccount());
	}
	
	protected abstract String getAccountFile();
	
	protected String dateToString(Date _date) {
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yy");
		return formatter.format(_date);
	}

	protected String timeToString(Date _date) {
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
		return formatter.format(_date);
	}
	
	protected String datetimeToString(Date _date) {
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
		return formatter.format(_date);
	}	
}
