package de.amberhome.quickaction;

import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.BA.Hide;

public class RHelper {

	@Hide
	public static int getResourceId(String type, String resourceName) {
		return BA.applicationContext.getResources().getIdentifier(resourceName, type, BA.packageName);
	}
}
