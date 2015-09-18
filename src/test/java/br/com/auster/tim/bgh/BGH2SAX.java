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
 * Created on 01/08/2006
 */
package br.com.auster.tim.bgh;

import java.io.InputStream;
import java.nio.channels.ReadableByteChannel;

import org.apache.log4j.xml.DOMConfigurator;

import br.com.auster.common.io.IOUtils;
import br.com.auster.common.io.NIOUtils;
import br.com.auster.common.xml.DOMUtils;
import br.com.auster.common.xml.sax.NIOInputSource;
import br.com.auster.om.invoice.Account;
import br.com.auster.om.invoice.Invoice;
import br.com.auster.tim.bgh.sax.TIMOMLoader;
import br.com.auster.udd.reader.TaggedFileReader;

/**
 * @author framos
 * @version $Id$
 *
 */
public class BGH2SAX {

	
	public static final String UDD_FILE = "bgh/ifd-udd.xml";
	public static final String HANDLER_FILE = "omloader.xml";
	public static final String LOGGER_FILE = "log4j.xml";
	
	public static final double MAX_VALUES_DIFFERENCE = 0.009;
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		if (args.length != 1) {
			System.out.println("Usage: BGH2SAX <bgh-file>");
			System.exit(1);
		}
		BGH2SAX main = new BGH2SAX();
		try {
			main.run(args[0]);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Invoice run(String _file) throws Exception {
		DOMConfigurator.configure(DOMUtils.openDocument(LOGGER_FILE, false));
		// udd reader
		TaggedFileReader reader = new TaggedFileReader(DOMUtils.openDocument(IOUtils.openFileForRead(UDD_FILE)));
		// om loader
		TIMOMLoader loader = new TIMOMLoader();
		loader.configure(DOMUtils.openDocument(IOUtils.openFileForRead(HANDLER_FILE)));
		
		reader.setContentHandler(loader);
		
		// parsing document
		InputStream i = IOUtils.openFileForRead(_file);
		ReadableByteChannel rbc = NIOUtils.openFileForRead(_file);
		NIOInputSource in = new NIOInputSource(rbc);
		reader.parse(in);

		Account account = (Account)loader.getObjects().get(0);
		if ((account == null)               || 
			(account.getInvoices() == null) ||
			(account.getInvoices().size() <= 0)) {
			return null;
		}
		// should have only one invoice. Always!!!
		return (Invoice) account.getInvoices().values().iterator().next();
	}	

}