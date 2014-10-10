package put.sailhero.android;

import java.util.LinkedList;

public class GetRegionsResponse implements ProcessedResponse {
	
	private LinkedList<Region> regions;

	public LinkedList<Region> getRegions() {
		return regions;
	}

	public void setRegions(LinkedList<Region> regions) {
		this.regions = regions;
	}

}
