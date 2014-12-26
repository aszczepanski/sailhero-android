package put.sailhero.util;

public class UnitUtils {

	public static Integer roundDistanceTo25(Float distance) {
		if (distance == null) {
			return null;
		}

		return Math.round(distance / 25.0f) * 25;
	}

	public static Float convertMetresToKnots(Float metres) {
		if (metres == null) {
			return null;
		}

		final Float ratio = 1.943844f;

		return metres * ratio;
	}

	public static Float roundSpeedToHalf(Float speed) {
		if (speed == null) {
			return null;
		}

		return (float) (Math.round(speed * 2.0f) / 2.0f);
	}

	// TODO: handle negative values
	public static DegMinSec decimalToDegMinSec(Double decimal) {
		Integer degrees = (int) decimal.floatValue();

		Double frac = 3600 * (decimal - degrees);
		Integer minutes = (int) (frac / 60);
		Integer seconds = (int) (frac % 60);

		Integer rest = (int) Math.round((frac - (60 * minutes + seconds)) * 1000);

		return new DegMinSec(degrees, minutes, seconds, rest);
	}

	public static class DegMinSec {
		private final Integer mDegrees;
		private final Integer mMinutes;
		private final Integer mSeconds;
		private final Integer mRest;

		public DegMinSec(Integer degrees, Integer minutes, Integer seconds, Integer rest) {
			mDegrees = degrees;
			mMinutes = minutes;
			mSeconds = seconds;
			mRest = rest;
		}

		public Integer getDegrees() {
			return mDegrees;
		}

		public Integer getMinutes() {
			return mMinutes;
		}

		public Integer getSeconds() {
			return mSeconds;
		}

		public Integer getRest() {
			return mRest;
		}
	}

}
