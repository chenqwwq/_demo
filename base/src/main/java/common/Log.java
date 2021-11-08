package common;

/**
 * @author chen
 * @date 2021/11/6
 **/
public class Log {

	private static final boolean DEBUG = true;

	public static void log(String par, Object... params) {
		if (DEBUG) {
			System.out.printf(par + "\n", params);
		}
	}
}
