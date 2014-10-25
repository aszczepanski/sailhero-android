package put.sailhero.android.util;

import java.util.LinkedList;

import put.sailhero.android.util.model.Port;

public class GetPortsResponse implements ProcessedResponse {

	private LinkedList<Port> ports;

	public LinkedList<Port> getPorts() {
		return ports;
	}

	public void setPorts(LinkedList<Port> ports) {
		this.ports = ports;
	}

}
