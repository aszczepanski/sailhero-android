package put.sailhero.model;

import org.json.simple.JSONObject;

public class Yacht {
	private Integer id;
	private String name;
	private Integer length;
	private Integer width;
	private Integer crew;

	public Yacht() {
	}

	public Yacht(JSONObject yachtObject) {
		this();

		setId(Integer.valueOf(yachtObject.get("id").toString()));
		setName(yachtObject.get("name").toString());
		setLength(Integer.valueOf(yachtObject.get("length").toString()));
		setWidth(Integer.valueOf(yachtObject.get("width").toString()));
		setCrew(Integer.valueOf(yachtObject.get("crew").toString()));
	}

	@SuppressWarnings("unchecked")
	public JSONObject toJSONObject() {
		JSONObject yachtObject = new JSONObject();

		yachtObject.put("name", name);
		yachtObject.put("length", length);
		yachtObject.put("width", width);
		yachtObject.put("crew", crew);

		return yachtObject;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getLength() {
		return length;
	}

	public void setLength(Integer length) {
		this.length = length;
	}

	public Integer getWidth() {
		return width;
	}

	public void setWidth(Integer width) {
		this.width = width;
	}

	public Integer getCrew() {
		return crew;
	}

	public void setCrew(Integer crew) {
		this.crew = crew;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Yacht)) {
			return false;
		}

		Yacht yachtToCompare = (Yacht) o;

		return getId().equals(yachtToCompare.getId()) && getName().equals(yachtToCompare.getName())
				&& getLength().equals(yachtToCompare.getLength()) && getWidth().equals(yachtToCompare.getWidth())
				&& getCrew().equals(yachtToCompare.getCrew());
	}
}
