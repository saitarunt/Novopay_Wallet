package com.wallet.util;

/**
 * Utility File
 * @author Sai Tarun
 *
 */
public class WalletUtil {

	/**
	 * Checks if Object is null or not.
	 * @param o
	 * @return
	 */
	public static boolean isVoid(Object o) {
		if(o == null)
			return true;
		if(o instanceof String) {
			return ((String)o).trim().length()==0;
		}
		return false;
	}
}
