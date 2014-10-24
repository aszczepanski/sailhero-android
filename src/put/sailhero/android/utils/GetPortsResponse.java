package put.sailhero.android.utils;

import java.util.LinkedList;

public class GetPortsResponse implements ProcessedResponse {

	private LinkedList<Port> ports;

	public LinkedList<Port> getPorts() {
		return ports;
	}

	public void setPorts(LinkedList<Port> ports) {
		this.ports = ports;
	}

}
