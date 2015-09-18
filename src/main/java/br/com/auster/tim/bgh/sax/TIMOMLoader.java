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
package br.com.auster.tim.bgh.sax;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import br.com.auster.dware.sax.MultiHandlerForwarder;
import br.com.auster.om.invoice.InvoiceObjectModelLoader;

/**
 * @author framos
 * @version $Id$
 *
 */
public class TIMOMLoader implements InvoiceObjectModelLoader, ContentHandler {

	
	public static final String TIM_CARRIERCODE = "41";
	public static final String BGH_ELEMENT_KEY = "key";
	
	private MultiHandlerForwarder forwarder;

	
	public TIMOMLoader() {
		this.forwarder = new MultiHandlerForwarder();
	}

	public void characters(char[] ch, int start, int length) throws SAXException {
		this.forwarder.characters(ch, start, length);
	}

	public void endDocument() throws SAXException {
		this.forwarder.endDocument();
	}
	
	public void endElement(String uri, String localName, String qName) throws SAXException {
		this.forwarder.endElement(uri, localName, qName);
	}
	
	public void endPrefixMapping(String prefix) throws SAXException {
		this.forwarder.endPrefixMapping(prefix);
	}
	
	public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
		this.forwarder.ignorableWhitespace(ch, start, length);
	}
	
	public void processingInstruction(String target, String data) throws SAXException {
		this.forwarder.processingInstruction(target, data);
	}
	
	public void setDocumentLocator(Locator locator) {
		this.forwarder.setDocumentLocator(locator);
	}
	
	public void skippedEntity(String name) throws SAXException {
		this.forwarder.skippedEntity(name);
	}
	
	public void startPrefixMapping(String prefix, String uri) throws SAXException {
		this.forwarder.startPrefixMapping(prefix, uri);
	}
	
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		this.forwarder.startElement(uri, localName, qName, attributes);
	}
	
	public void startDocument() throws SAXException {
		this.forwarder.startDocument();
	}	

	/**
	 * @see br.com.auster.om.invoice.InvoiceObjectModelLoader#getObjects()
	 */
	public List getObjects() {
		ArrayList list = new ArrayList();
		list.add(this.forwarder.getCurrentContext().getAttribute(InvoiceAndAccountHandler.OM_CURRENT_ACCOUNT));
		return list;
	}

	/**
	 * @see br.com.auster.om.invoice.InvoiceObjectModelLoader#cleanup()
	 */
	public void cleanup() {
		this.forwarder.getCurrentContext().reset();
	}

	/**
	 * @see br.com.auster.om.invoice.InvoiceObjectModelLoader#configure(Element)
	 */
	public void configure(Element _config) {
		this.forwarder.init(_config);
	}
		
	
}
