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
 * Created on 14/08/2006
 */
package br.com.auster.tim.bgh;

import junit.framework.TestCase;
import br.com.auster.tim.bgh.sax.BGHDataFormats;

/**
 * @author framos
 * @version $Id$
 *
 */
public class FormatterTest extends TestCase {

	
	
	public void testMinutesFormatterWhenNull() {
		assertNull(BGHDataFormats.parseTimeFromString(null));
			
	}
	
	public void testMinutesFormatterWhenNotOK() {
		assertNull(BGHDataFormats.parseTimeFromString("10:00"));
	}
	
	public void testMinutesFormatterWhenOK() {
		long[] time = BGHDataFormats.parseTimeFromString("10:04:21");
		assertEquals(10, time[0]);
		assertEquals(04, time[1]);
		assertEquals(21, time[2]);
	}
	
}
