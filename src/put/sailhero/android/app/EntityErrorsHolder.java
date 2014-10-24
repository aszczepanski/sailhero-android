package put.sailhero.android.app;

import java.util.LinkedList;

import org.json.simple.JSONArray;

public abstract class EntityErrorsHolder {
	protected void addJsonArrayErrorsToLinkedList(JSONArray array, LinkedList<String> list) {
		if (list == null) {
			list = new LinkedList<String>();
		}

		if (array != null && !array.isEmpty()) {
			for (int i = 0; i < array.size(); i++) {
				Object error = array.get(i);
				if (error != null) {
					list.add(error.toString());
				}
			}
		}
	}
}
