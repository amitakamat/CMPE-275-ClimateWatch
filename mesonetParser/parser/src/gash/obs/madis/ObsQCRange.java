package gash.obs.madis;

/**
 * @TODO complete QC methods using the ranges below
 * 
 * @author gash
 * 
 *         <pre>
  -----------------------------------------
  Validity Checks
  -----------------------------------------
  Dewpoint temperature       -90 -   90   F
  Relative humidity            0 -  100  %
  Relative humidity 1hr chng -50 -   50  %
  Altimeter                  568 - 1100  mb
  Altimeter 1hr change       -10 -   10  mb
  Pressure change              0 - 30.5  mb
  Sea level pressure         846 - 1100  mb
  Station pressure           568 - 1100  mb
  Air temperature            -60 -  130   F
  Air temperature 1hr change -35 -   35   F
  Wind Direction               0 -  360  deg
  Wind Speed                   0 -  250  kts
  Visibility                   0 -  100 miles
  Accumulated precip - *h      0 -   44  in
  Precipitation rate           0 -   44  in
  Soil moisture percent        0 -  100  %
  Soil temperature           -40 -  150   F
  Wind dir at gust             0 -  360  deg
  Wind gust                    0 -  287  mph
  24 hour min temperature    -60 -  130   F
  24 hour max temperature    -60 -  130   F
  Wind dir at hourly max       0 -  360  deg
  Wind speed                   0 -  287  mph
  Hourly maximum wind speed    0 -  287  mph
  Snow cover                   0 -   25  ft
  Snow fall - 6h               0 -   50  in
  Snow fall - 24h              0 -  300  in
  Sea surface temperature   28.4 -  104   F

  ---------------------------------------------
  Temporal Consistency Checks
  ---------------------------------------------
  Dewpoint temperature             35  F/hour
  Sea level pressure               15  mb/hour
  Air temperature                  35  F/hour
  Wind speed                       20 kts/hour
  Soil temperature                  5  F/hour
  Sea surface temperature           9  F/hour
 *         </pre>
 * 
 */
public class ObsQCRange {
	private ObsQCRange() {
	}

	public static String trimString(String s) {
		// placeholder for advanced trimming (non-printable, i18n, ...)
		if (s == null)
			return s;
		else
			return s.trim();
	}

	public static boolean checkName(String n) {
		if (n == null || n.trim().length() == 0)
			return false;
		else
			return true;
	}

	public static boolean checkLatitude(double lat) {
		if (lat > 90.0 || lat < -90.0)
			return false;
		else
			return true;
	}

	public static boolean checkLongitude(double lon) {
		if (lon > 180.0 || lon < -180.0)
			return false;
		else
			return true;
	}

	public static boolean checkDate(long d) {
		if (d < 0)
			return false;
		else
			return true;
	}

	public static boolean checkStationPressure(double p) {
		return true;
	}

	public static boolean checkAltimeter(double a) {
		return true;
	}

	public static boolean checkSeaLevelPressure(double p) {
		return true;
	}

	public static boolean checkTemperature(double t) {
		return true;
	}

	public static boolean checkDewpoint(double t) {
		return true;
	}

	public static boolean checkRelativeHumidity(double h) {
		return true;
	}

	public static boolean checkWindDirection(double d) {
		return true;
	}

	public static boolean checkWindSpeed(double s) {
		return true;
	}

	public static boolean checkWindGust(double s) {
		return true;
	}

	public static boolean checkVisibility(double v) {
		return true;
	}

	public static boolean checkPrecipRate(double r) {
		return true;
	}

	public static boolean checkSeaTemperature(double t) {
		return true;
	}

	public static boolean checkSoilTemperature(double t) {
		return true;
	}
}
