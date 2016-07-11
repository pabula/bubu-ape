package com.pabula.common.util;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.Random;


//out.println("<p>������10000000 �� 99999999֮��������:");
//RNUM.setRange("10000000-99999999");
//RNUM.generateRandomObject();
//out.println("<b>"+RNUM.getRandom().intValue()+"</b>");
//
//out.println("<p>��n 25 �� 100֮�䴴��һ�������:");
//RNUM.setRange("25-100");
//RNUM.generateRandomObject();
//out.println("<b>"+RNUM.getRandom().intValue()+"</b>");


public class RandomNum {

	private Long randomnum = null;

	private Float randomfloat = null;

	private boolean floatvalue = false;

	private long upper = 100;

	private long lower = 0;

	private String algorithm = null;

	private String provider = null;

	private boolean secure = false;

	private Random random = null;

	private SecureRandom secrandom = null;

	private final float getFloat() {
		if (random == null)
			return secrandom.nextFloat();
		else
			return random.nextFloat();
	}

	public final void generateRandomObject() throws Exception {

		// check to see if the object is a SecureRandom object
		if (secure) {
			try {
				// get an instance of a SecureRandom object
				if (provider != null)
					// search for algorithm in package provider
					secrandom = SecureRandom.getInstance(algorithm, provider);
				else
					secrandom = SecureRandom.getInstance(algorithm);
			} catch (NoSuchAlgorithmException ne) {
				throw new Exception(ne.getMessage());
			} catch (NoSuchProviderException pe) {
				throw new Exception(pe.getMessage());
			}
		} else
			random = new Random();
	}

	/**
	 * generate the random number
	 * 
	 */
	private final void generaterandom() {

		int tmprandom = 0; // temp storage for random generated number
		Integer rand;

		// check to see if float value is expected
		if (floatvalue)
			randomfloat = new Float(getFloat());
		else
			randomnum = new Long(lower
					+ (long) ((getFloat() * (upper - lower))));
	}

	public final Number getRandom() {
		generaterandom();
		if (floatvalue)
			return randomfloat;
		else
			return randomnum;
	}

	public final void setRange(long low, long up) {

		// set the upper and lower bound of the range
		lower = low;
		upper = up;

		// check to see if a float value is expected
		if ((lower == 0) && (upper == 1))
			floatvalue = true;
	}

	/**
	 * set the algorithm name
	 * 
	 * @param value
	 *            name of the algorithm to use for a SecureRandom object
	 * 
	 */
	public final void setAlgorithm(String value) {
		algorithm = value;
		secure = true; // a SecureRandom object is to be used
	}

	public final void setProvider(String value) {
		provider = value;
	}

	public final void setRange(String value) throws Exception {
		try {
			upper = new Integer(value.substring(value.indexOf('-') + 1))
					.longValue();
		} catch (Exception ex) {
			throw new Exception("upper attribute could not be"
					+ " turned into an Integer default value was used");
		}

		try {
			lower = new Integer(value.substring(0, value.indexOf('-')))
					.longValue();
		} catch (Exception ex) {
			throw new Exception("lower attribute could not be"
					+ " turned into an Integer default value was used");
		}

		if ((lower == 0) && (upper == 1))
			floatvalue = true;

		if (upper < lower)
			throw new Exception("You can't have a range where the lowerbound"
					+ " is higher than the upperbound.");

	}

}
