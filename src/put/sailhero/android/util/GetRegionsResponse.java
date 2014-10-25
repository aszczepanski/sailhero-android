package put.sailhero.android.util;

import java.util.LinkedList;

import put.sailhero.android.util.model.Region;

public class GetRegionsResponse implements ProcessedResponse {
	
	private LinkedList<Region> regions;

	public LinkedList<Region> getRegions() {
		return regions;
	}

	public void setRegions(LinkedList<Region> regions) {
		this.regions = regions;
	}

}
