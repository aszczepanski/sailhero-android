package put.sailhero.android.util;

import android.content.Context;

public class SailHeroService {
	private static SailHeroService instance;
	private SailHeroSettings settings;
	private Connection connection = new Connection();
	
	public static SailHeroService initialize(Context context) {
		if (instance == null) {
			SailHeroSettings settings = new SailHeroSharedPreferencesSettings(context);
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

	public void reset() {
		settings.clear();
		settings.save();
	}
	
	public Connection getConnection() {
		return connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}
}
