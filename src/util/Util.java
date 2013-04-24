package util;

public class Util {
	public static Boolean isInteger(String string) {
		
		try {
			Integer.parseInt(string);
			return true;
		} catch (Exception e){
			return false;
		}
		
	}
}
