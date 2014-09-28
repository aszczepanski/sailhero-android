package put.sailhero.android;

public class SailHeroService {
	private static SailHeroService instance;
	private SailHeroSettings settings;
	
	public static SailHeroService initialize() {
		if (instance == null) {
			SailHeroSettings settings = new SailHeroFileSettings();
			SailHeroService service = new SailHeroService(settings);
			instance = service;
		}
		return instance;
	}
	
	public static SailHeroService initialize(SailHeroSettings settings) {
		SailHeroService service = new SailHeroService(settings);
		instance = service;
		return instance;
	}
	
	private SailHeroService(SailHeroSettings settings) {
		this.settings = settings;
	}
	
	public static SailHeroService getInstance() {
		return instance;
	}
	
	public SailHeroSettings getSettings() {
		return settings;
	}
}
