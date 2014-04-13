package nl.xpagesandbeer;

import java.util.Map;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.View;

import nl.defrog.JSFUtil;
import nl.defrog.Utils;

public class Helper {
	static String getSetting(String name) {
		Map<String, Object> applicationScope = JSFUtil.getApplicationScope();

		if (!applicationScope.containsKey(name)) {
			try {
				Database cmsDb = JSFUtil.getCurrentSession().getDatabase(JSFUtil.getCurrentDatabase().getServer(), Utils.getResource("settings", "cms_db"));
				View settings = cmsDb.getView("settings");
				Document setting = settings.getDocumentByKey(name);
				if (setting != null) {
					applicationScope.put(name, setting.getItemValueString("value"));
				} else {
					throw new IllegalArgumentException("Setting " + name + " not found");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return (String) applicationScope.get(name);
	}
	
	static String getFirstName(String name) {
		int pos = name.indexOf(" ");
		if (pos >= 0) {
			return name.substring(0, pos);
		} else {
			return name;
		}
	}
	
	static String getLastName(String name) {
		int pos = name.indexOf(" ");
		if (pos >= 0) {
			return name.substring(pos + 1);
		} else {
			return "";
		}
	}
}
