package eu.vranckaert.worktime.security.utils;

import java.util.UUID;

/**
 * This is a utility class for generating keys. 
 * @author Dirk Vranckaert
 */
public class KeyGenerator {
	/**
	 * Generates a new unique key.
	 * @return The newly generated key.
	 */
	public static String getNewKey() {
		return UUID.randomUUID().toString();
	}
}
