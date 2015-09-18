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

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import br.com.auster.dware.sax.MultiHandlerReceiverBase;
import br.com.auster.om.invoice.Receipt;
import br.com.auster.tim.om.invoice.FreeUnitsReceiptDetail;
import br.com.auster.tim.om.invoice.TIMReceiptDetail;

/**
 * @author framos
 * @version $Id$
 */
public class NFFreeUnitHandler extends MultiHandlerReceiverBase {



	// -------------------
	// Class constants
	// -------------------

	private static final Logger log = Logger.getLogger(NFFreeUnitHandler.class);

	public static final String BGH_FU_FORMERBILLS = "FUPreviousBill";
	public static final String BGH_FU_CURRENTBILLS = "FUCurrentBill";

	public static final String BGH_FU_DIRNUMBER = "directoryNumber";
	public static final String BGH_FU_CONTRACT = "contractNumber";
	public static final String BGH_FU_PACKAGENAME = "fuPackageName";
	public static final String BGH_FU_UNITTYPE = "fuMeasure";
	public static final String BGH_FU_UNIT_CARRIEDOVER = "fuCarriedOverFromPrevious";
	public static final String BGH_FU_UNIT_ORIGINAL = "fuOriginalAvailable";
	public static final String BGH_FU_UNIT_USED = "fuUsed";
	public static final String BGH_FU_UNIT_UNUSED = "fuUnused";
	public static final String BGH_FU_STARTDATE = "startDateOfAccountingPeriod";
	public static final String BGH_FU_ENDDATE = "endDateOfAccountingPeriod";
	public static final String BGH_FU_ACCOUNTID = "fuAccountId";

	public static final String OM_CURRENT_FUGROUP_CURRPERIOD = "receipt.fu.group.now.current";
	public static final String OM_CURRENT_FUGROUP_PREVPERIOD = "receipt.fu.group.prev.current";

	public static final String OM_FUGROUP_CURRPERIOD_CAPTION = "Current FreeUnits";
	public static final String OM_FUGROUP_PREVPERIOD_CAPTION = "Previous FreeUnits";



	// -------------------
	// Public methods
	// -------------------

	public void startElement(String _namespace, String _localName, String _qName, Attributes _attributes) throws SAXException {

		TIMReceiptDetail trd;
		if (BGH_FU_FORMERBILLS.equals(_localName) || BGH_FU_CURRENTBILLS.equals(_localName)) {
			// must have NF
			if (!this.context.hasAttribute(NFHeaderHandler.OM_CURRENT_NF)) {
				throw new SAXException("Cannot handle NF detail without current TIM NF");
			}
			Receipt rcpt = (Receipt) this.context.getAttribute(NFHeaderHandler.OM_CURRENT_NF);
			// creating FU group if it doesnt exist yet
			String groupToken = OM_CURRENT_FUGROUP_CURRPERIOD;
			String groupName = OM_FUGROUP_CURRPERIOD_CAPTION;
			if (BGH_FU_FORMERBILLS.equals(_localName)) {
				groupToken = OM_CURRENT_FUGROUP_PREVPERIOD;
				groupName = OM_FUGROUP_PREVPERIOD_CAPTION;
			}
			// FU groups are removed from context at NF end
			if (!this.context.hasAttribute(groupToken)) {
				trd = new TIMReceiptDetail();
				trd.setCaption(groupName);
				rcpt.addDetail(trd);
				this.context.setAttribute(groupToken, trd);
			} else {
				trd = (TIMReceiptDetail) this.context.getAttribute(groupToken);
			}
			// handled FU detail
			FreeUnitsReceiptDetail furd = new FreeUnitsReceiptDetail();
			furd.setTag(_attributes.getValue(TIMOMLoader.BGH_ELEMENT_KEY));
			furd.setContractNumber(_attributes.getValue(BGH_FU_CONTRACT));
			furd.setAccessNbr(_attributes.getValue(BGH_FU_DIRNUMBER));
			furd.setServiceId(_attributes.getValue(BGH_FU_PACKAGENAME));
			furd.setUnitCarriedOverCount(_attributes.getValue(BGH_FU_UNIT_CARRIEDOVER));
			furd.setUnitCount(_attributes.getValue(BGH_FU_UNIT_ORIGINAL));
			furd.setUnitUnusedCount(_attributes.getValue(BGH_FU_UNIT_UNUSED));
			furd.setUnitUsedCount(_attributes.getValue(BGH_FU_UNIT_USED));
			furd.setUnitType(_attributes.getValue(BGH_FU_UNITTYPE));
			furd.setStartDate(BGHDataFormats.getDate(_attributes.getValue(BGH_FU_STARTDATE)));
			furd.setOriginalStartDate(_attributes.getValue(BGH_FU_STARTDATE));
			furd.setEndDate(BGHDataFormats.getDate(_attributes.getValue(BGH_FU_ENDDATE)));
			furd.setOriginalEndDate(_attributes.getValue(BGH_FU_ENDDATE));
			furd.setAccountID(_attributes.getValue(BGH_FU_ACCOUNTID));
			//sets the shared package flag
			furd.setShared((furd.getAccessNbr() == null || furd.getAccessNbr().trim().equals("")) ? true : false);
			// saves the new FU Detail into the current NF
			trd.addGroupItem(furd);
			// debugging attributes
			log.debug("Found FU package=" + furd.getServiceId());
			log.debug("Found FU contract=" + furd.getContractNumber());
			log.debug("Found FU dirNumber=" + furd.getAccessNbr());
			log.debug("Found FU unitType=" + furd.getUnitType());
			log.debug("Found FU unitCarriedOver=" + furd.getUnitCarriedOverCount());
			log.debug("Found FU unitOriginal=" + furd.getUnitCount());
			log.debug("Found FU unitUsed=" + furd.getUnitUsedCount());
			log.debug("Found FU unitUnused=" + furd.getUnitUnusedCount());
			log.debug("Found FU startDate=" + furd.getStartDate());
			log.debug("Found FU endDate=" + furd.getEndDate());
			log.debug("Found FU AccountID=" + furd.getAccountID());
		}

	}

	public void endElement(String _namespace, String _localName, String _uriName) throws SAXException {
		// when NF is done, remove FreeUnits group since they dont have a final TAG
		if (NFHeaderHandler.BGH_TIMNF_ELEMENT.equals(_localName) || NFHeaderHandler.BGH_LDNF_ELEMENT.equals(_localName)) {
			this.context.removeAttribute(NFFreeUnitHandler.OM_CURRENT_FUGROUP_CURRPERIOD);
			this.context.removeAttribute(NFFreeUnitHandler.OM_CURRENT_FUGROUP_PREVPERIOD);
		}
	}
}
