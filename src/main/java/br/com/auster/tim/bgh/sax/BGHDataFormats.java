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

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

import br.com.auster.om.util.ParserUtils;
import br.com.auster.om.util.UnitCounter;

/**
 * @author framos
 * @version $Id$
 *
 */
public abstract class BGHDataFormats {


	private static final Logger log = Logger.getLogger(BGHDataFormats.class);

	private static final String EMPTY_STRING = "";

	public static final String BGH_FILE_DATE_PATTERN = "dd/MM/yy";
	public static final String BGH_FILE_TIME_PATTERN = "HH:mm:ss";
	public static final String BGH_FILE_DATA_PATTERN = "###0.00";
	public static final String TIME_SEPARATOR = ":";



	private static final ThreadLocal dateParser = new ThreadLocal() {
		protected Object initialValue() {
			return new SimpleDateFormat(BGH_FILE_DATE_PATTERN);
		}
	};

	private static final ThreadLocal timeParser = new ThreadLocal() {
		protected Object initialValue() {
			return new SimpleDateFormat(BGH_FILE_TIME_PATTERN);
		}
	};

	private static final ThreadLocal dataParser = new ThreadLocal() {
		protected Object initialValue() {
			return new DecimalFormat(BGH_FILE_DATA_PATTERN);
		}
	};


	/**
	 * Returns which unit type the referenced value is related to.
	 */
	public static final String getUnitType(String _value) {
		if (_value == null) {
			return UnitCounter.UNIT_COUNTER;
		}
		if ((_value.indexOf(TIME_SEPARATOR) > 0) &&
			(_value.indexOf(TIME_SEPARATOR, _value.indexOf(TIME_SEPARATOR)+1) > 0)) {
			return UnitCounter.TIME_COUNTER;
		} else if (_value.endsWith("B")) {
			return UnitCounter.DATA_COUNTER;
		}
		return UnitCounter.UNIT_COUNTER;
	}

	public static final long[] parseTimeFromString(String _value) {
		if (_value == null) {
			return null;
		}
		long[] time = new long[3];
		int pos1 = _value.indexOf(TIME_SEPARATOR);
		int pos2 = _value.indexOf(TIME_SEPARATOR, pos1+1);
		// not in the correct format
		if ((pos1 <= 0) || (pos2 < pos1)) {
			return null;
		}
		time[0] = Long.parseLong(_value.substring(0, pos1));
		time[1] = Long.parseLong(_value.substring(pos1+1, pos2));
		time[2] = Long.parseLong(_value.substring(pos2+1));
		return time;
	}


	public static final UnitCounter buildUnitCounter(String _value) {
		return buildUnitCounter(getUnitType(_value), _value);
	}

	public static final UnitCounter buildUnitCounter(String _type, String _value) {
		if (_type == null) {
			_type = UnitCounter.UNIT_COUNTER;
		}
		// build time UC if format is HH:MM:SS
		if (UnitCounter.TIME_COUNTER.equals((_type))) {
			UnitCounter uc = new UnitCounter(UnitCounter.TIME_COUNTER);
			long[] time = parseTimeFromString(_value);
			uc.addHours(time[0]);
			uc.addMinutes(time[1]);
			uc.addSeconds(time[2]);
			return uc;
		// build data UC if format is NNNN KB/MB/GB
		} else if (UnitCounter.DATA_COUNTER.equals((_type))) {
			String value = _value.substring(0, _value.length()-2);
			String unit = _value.substring(_value.length()-2);
			return ParserUtils.getUnitCounter(unit, value);
		}
		// build unit UC otherwise
		UnitCounter uc = new UnitCounter(UnitCounter.UNIT_COUNTER);
		uc.addUnits(ParserUtils.getInt(_value));
		return uc;
	}

	public static Date getDate(String _value) {
		if ((_value == null) || (_value.trim().equals(EMPTY_STRING))) {
			return null;
		}
		try {
			SimpleDateFormat sdf = (SimpleDateFormat) dateParser.get();
			return sdf.parse(_value);
		} catch (Exception e) {
			log.error("Could not parse date " + _value);
			return null;
		}
	}

	public static Date getTime(String _value) {
		if ((_value == null) || (_value.trim().equals(EMPTY_STRING))) {
			return null;
		}
		try {
			SimpleDateFormat sdf = (SimpleDateFormat) timeParser.get();
			return sdf.parse(_value);
		} catch (Exception e) {
			log.error("Could not parse time " + _value);
			return null;
		}
	}

	public static String formatNumber(double _number) {
		try {
			DecimalFormat df = (DecimalFormat)dataParser.get();
			return df.format(_number);
		} catch (Exception e) {
			log.error("Exception while formatting", e);
		}
		return String.valueOf(_number);
	}
}
