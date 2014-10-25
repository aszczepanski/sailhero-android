package put.sailhero.android.util;

public interface Request {
	public String getUrl();
	public Header[] getHeaders();
	public String getBody();
	public Method getMethod();
	
	public class Header {
		private String name;
		private String value;
		
		public Header() {
			this(null,null);
		}
		
		public Header(String name, String value) {
			this.setName(name);
			this.setValue(value);
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}
	}
	
	public enum Method {
		POST,
		PUT,
		GET
	}
}
