package gash.obs.madis;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * MADIS mesonet data extraction - note not all fields are extracted (surface
 * temp, QC values, etc)
 * 
 * @author gash1
 * 
 */
@XmlRootElement(name = "MadisMesonetData")
@XmlAccessorType(XmlAccessType.FIELD)
public class MesonetData {
	public static final float missingValue = -9999;
	public static final float fillValue = 3.4028235e+38f;
	public static final int fillValueI = -2147483647;

	public enum PrecipType {
		None(0), Present(1), Rain(2), Snow(3), Mixed(4), Light(5), LightFreezing(6), FreezingRain(7), Sleet(8), Hail(
				9), Other(10), Unidentified(13), Unknown(12), Frozen(13), IcePellets(14), Recent(15), Malfunction(
						16), Malfunction2(29), Malfunction3(30);

		private int code;

		PrecipType(int code) {
			this.code = code;
		}

		public int getCode() {
			return code;
		}

		public static PrecipType convertCode(int code) {
			for (PrecipType t : PrecipType.values()) {
				if (t.getCode() == code)
					return t;
			}
			return Unknown;
		}
	};

	/** units: int, desc: numeric WMO identification */
	private Integer wmoId;

	/** units: not specified, desc: home WFO Id */
	private String wfoId;

	/** units: not specified, desc: alphanumeric station Id */
	private String stationID;

	/** units: not specified, desc: alphanumeric station identification */
	private String stationName;

	/** units: not specified, desc: LDAD station type */
	private String stationType;

	/** units: not specified, desc: local data provider */
	private String dataProvider;

	/** units: not specified, desc: geographic station location */
	private String locationName;

	/** units: degree_north, desc: latitude */
	private Float latitude;

	/** units: degree_east, desc: longitude */
	private Float longitude;

	/** units: meter, desc: elevation */
	private Double elevation;

	/** units: seconds since 1970-1-1 00:00:00.0, desc: time of observation */
	private Double timeObs;

	/**
	 * units: seconds since 1970-1-1 00:00:00.0, desc: time data was processed
	 * by the provider
	 */
	private Double timeReport;

	/**
	 * units: seconds since 1970-1-1 00:00:00.0, desc: time data was received
	 */
	private Double timeReceived;

	/** units: kelvin, desc: temperature */
	private Double temperature;

	/**
	 * units: char, desc: QC summary value: Z,C,S,V,X,Q,K,k,G, or B:
	 * 
	 * <pre>
	 * AWIPS Technique Specification Package (TSP) 88-21-R2
	 * 		Z = "No QC applied" 
	 * 		C = "Passed QC stage 1" 
	 * 		S = "Passed QC stages 1 and 2" 
	 * 		V = "Passed QC stages 1, 2 and 3" 
	 * 		X = "Failed QC stage 1" 
	 * 		Q = "Passed QC stage 1, but failed stages 2 or 3 " 
	 * 		K = "Passed QC stages 1, 2, 3, and 4" 
	 * 		k = "Passed QC stage 1,2, and 3, failed stage 4 " 
	 * 		G = "Included in accept list" 
	 * 		B = "Included in reject list"
	 * 
	 * </pre>
	 */
	private String temperatureQC;

	/** units: kelvin, desc: dewpoint */
	private Double dewpoint;

	/** units: percent, desc: relative humidity */
	private Double relHumidity;

	/** units: pascal, desc: station pressure */
	private Double stationPressure;

	/** units: pascal, desc: sea level pressure */
	private Double seaLevelPress;

	/** units: pascal, desc: altimeter setting */
	private Double altimeter;

	/** units: degree, desc: wind direction */
	private Double windDir;

	/** units: meter/sec, desc: wind speed */
	private Double windSpeed;

	/**
	 * units: not specified, desc: wind speed QC summary value:
	 * Z,C,S,V,X,Q,K,k,G, or B (see temperatureQC for code descriptions)
	 */
	private String windSpeedQC;

	/** units: meter/sec, desc: wind gust */
	private Double windGust;

	/** units: meter, desc: visibility */
	private Double visibility;

	/** units: mm, desc: raw precipitation */
	private Double precip;

	/** units: meter, desc: raw precipitation accumulation */
	private Double precipAccum;

	/** units: meter/sec, desc: raw precipitation rate */
	private Double precipRate;

	/**
	 * units: not specified, desc: precipitation type:
	 * 
	 * value created from:
	 * 
	 * <pre>
	 * 	    0 = "no precipitation" 
	 * 		1 = "precipitation present but unclassified" 
	 * 		2 = "rain" 
	 * 		3 = "snow" 
	 * 		4 = "mixed rain and snow" 
	 * 		5 = "light" 
	 * 		6 = "light freezing" 
	 * 		7 = "freezing rain" 
	 * 		8 = "sleet" 
	 * 		9 = "hail" 
	 * 		10 = "other" 
	 * 		11 = "unidentified" 
	 * 		12 = "unknown" 
	 * 		13 = "frozen" 
	 * 		14 = "ice pellets" 
	 * 		15 = "recent" 
	 * 		16 = "lens dirty" 
	 * 		29 = "RPU-to-maxSensor communications failure" 
	 * 		30 = "sensor failure" 
	 * 		FillValue = -32767s 
	 * 		missing_value = -9999s
	 * </pre>
	 */
	private PrecipType precipType;

	/**
	 * units: none specified, desc: precipitation intensity
	 * 
	 * <pre>
	 * 		0 = "precipitation intensity info not available" 
	 * 		1 = "none" 
	 * 		2 = "light" 
	 * 		3 = "moderate" 
	 * 		4 = "heavy" 
	 * 		5 = "slight" 
	 * 		6 = "other" 
	 * 		FillValue = -32767s 
	 * 		missing_value = -9999s
	 * </pre>
	 */
	private Short precipIntensity;

	/** units: watt/meter2, desc: solar radiation */
	private Float solarRadiation;

	/** units: kelvin, desc: sea surface temperature */
	private Double tempSeaSurface;

	/** units: kelvin, desc: soil temperature */
	private Double tempSoil;

	/** units: not specified, desc: sky cover (fraction) */
	private String skyCover;

	/** units: meter, desc: sky cover layer base */
	private Double skyLayerBase;

	/** units: centimeters, desc: Total column precipitable water vapor (PWV) */
	private Double totalColumnPWV;

	/**
	 * return obs time (sec) as a Date object
	 * 
	 * @return
	 */
	public Date getTimeObsAsDate() {
		if (timeObs != null)
			return new Date(timeObs.longValue() * 1000);
		else
			return null;
	}

	/**
	 * return received time (sec) as a Date object
	 * 
	 * @return
	 */
	public Date getTimeReceivedAsDate() {
		if (timeReceived != null)
			return new Date(timeReceived.longValue() * 1000);
		else
			return null;
	}

	/**
	 * @return the wmoId
	 */
	public Integer getWmoId() {
		return wmoId;
	}

	/**
	 * @param wmoId
	 *            the wmoId to set
	 */
	public void setWmoId(Integer wmoId) {
		this.wmoId = wmoId;
	}

	/**
	 * @return the wfoId
	 */
	public String getWfoId() {
		return wfoId;
	}

	/**
	 * @param wfoId
	 *            the wfoId to set
	 */
	public void setWfoId(String wfoId) {
		this.wfoId = wfoId;
	}

	/**
	 * @return the stationID
	 */
	public String getStationID() {
		return stationID;
	}

	/**
	 * @param stationID
	 *            the stationID to set
	 */
	public void setStationID(String stationID) {
		this.stationID = stationID;
	}

	/**
	 * @return the stationName
	 */
	public String getStationName() {
		return stationName;
	}

	/**
	 * @param stationName
	 *            the stationName to set
	 */
	public void setStationName(String stationName) {
		this.stationName = stationName;
	}

	/**
	 * @return the stationType
	 */
	public String getStationType() {
		return stationType;
	}

	/**
	 * @param stationType
	 *            the stationType to set
	 */
	public void setStationType(String stationType) {
		this.stationType = stationType;
	}

	/**
	 * @return the dataProvider
	 */
	public String getDataProvider() {
		return dataProvider;
	}

	/**
	 * @param dataProvider
	 *            the dataProvider to set
	 */
	public void setDataProvider(String dataProvider) {
		this.dataProvider = dataProvider;
	}

	/**
	 * @return the locationName
	 */
	public String getLocationName() {
		return locationName;
	}

	/**
	 * @param locationName
	 *            the locationName to set
	 */
	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

	/**
	 * @return the latitude
	 */
	public Float getLatitude() {
		return latitude;
	}

	/**
	 * @param latitude
	 *            the latitude to set
	 */
	public void setLatitude(Float latitude) {
		this.latitude = latitude;
	}

	/**
	 * @return the longitude
	 */
	public Float getLongitude() {
		return longitude;
	}

	/**
	 * @param longitude
	 *            the longitude to set
	 */
	public void setLongitude(Float longitude) {
		this.longitude = longitude;
	}

	/**
	 * @return the elevation
	 */
	public Double getElevation() {
		return elevation;
	}

	/**
	 * @param elevation
	 *            the elevation to set
	 */
	public void setElevation(Double elevation) {
		this.elevation = elevation;
	}

	/**
	 * @return the timeObs
	 */
	public double getTimeObs() {
		return timeObs;
	}

	/**
	 * @param timeObs
	 *            the timeObs to set
	 */
	public void setTimeObs(Double timeObs) {
		this.timeObs = timeObs;
	}

	/**
	 * @return the timeReport
	 */
	public double getTimeReport() {
		return timeReport;
	}

	/**
	 * @param timeReport
	 *            the timeReport to set
	 */
	public void setTimeReport(Double timeReport) {
		this.timeReport = timeReport;
	}

	/**
	 * @return the timeReceived
	 */
	public Double getTimeReceived() {
		return timeReceived;
	}

	/**
	 * @param timeReceived
	 *            the timeReceived to set
	 */
	public void setTimeReceived(Double timeReceived) {
		this.timeReceived = timeReceived;
	}

	/**
	 * @return the temperature
	 */
	public Double getTemperature() {
		return temperature;
	}

	/**
	 * @param temperature
	 *            the temperature to set
	 */
	public void setTemperature(Double temperature) {
		this.temperature = temperature;
	}

	/**
	 * @return the temperatureQC
	 */
	public String getTemperatureQC() {
		return temperatureQC;
	}

	/**
	 * @param temperatureQC
	 *            the temperatureQC to set
	 */
	public void setTemperatureQC(String temperatureQC) {
		this.temperatureQC = temperatureQC;
	}

	/**
	 * @return the dewpoint
	 */
	public Double getDewpoint() {
		return dewpoint;
	}

	/**
	 * @param dewpoint
	 *            the dewpoint to set
	 */
	public void setDewpoint(Double dewpoint) {
		this.dewpoint = dewpoint;
	}

	/**
	 * @return the relHumidity
	 */
	public Double getRelHumidity() {
		return relHumidity;
	}

	/**
	 * @param relHumidity
	 *            the relHumidity to set
	 */
	public void setRelHumidity(Double relHumidity) {
		this.relHumidity = relHumidity;
	}

	/**
	 * @return the stationPressure
	 */
	public Double getStationPressure() {
		return stationPressure;
	}

	/**
	 * @param stationPressure
	 *            the stationPressure to set
	 */
	public void setStationPressure(Double stationPressure) {
		this.stationPressure = stationPressure;
	}

	/**
	 * @return the seaLevelPress
	 */
	public Double getSeaLevelPress() {
		return seaLevelPress;
	}

	/**
	 * @param seaLevelPress
	 *            the seaLevelPress to set
	 */
	public void setSeaLevelPress(Double seaLevelPress) {
		this.seaLevelPress = seaLevelPress;
	}

	/**
	 * @return the altimeter
	 */
	public Double getAltimeter() {
		return altimeter;
	}

	/**
	 * @param altimeter
	 *            the altimeter to set
	 */
	public void setAltimeter(Double altimeter) {
		this.altimeter = altimeter;
	}

	/**
	 * @return the windDir
	 */
	public Double getWindDir() {
		return windDir;
	}

	/**
	 * @param windDir
	 *            the windDir to set
	 */
	public void setWindDir(Double windDir) {
		this.windDir = windDir;
	}

	/**
	 * @return the windSpeed
	 */
	public Double getWindSpeed() {
		return windSpeed;
	}

	/**
	 * @param windSpeed
	 *            the windSpeed to set
	 */
	public void setWindSpeed(Double windSpeed) {
		this.windSpeed = windSpeed;
	}

	/**
	 * @return the windSpeedQC
	 */
	public String getWindSpeedQC() {
		return windSpeedQC;
	}

	/**
	 * @param windSpeedQC
	 *            the windSpeedQC to set
	 */
	public void setWindSpeedQC(String windSpeedQC) {
		this.windSpeedQC = windSpeedQC;
	}

	/**
	 * @return the windGust
	 */
	public Double getWindGust() {
		return windGust;
	}

	/**
	 * @param windGust
	 *            the windGust to set
	 */
	public void setWindGust(Double windGust) {
		this.windGust = windGust;
	}

	/**
	 * @return the visibility
	 */
	public Double getVisibility() {
		return visibility;
	}

	/**
	 * @param visibility
	 *            the visibility to set
	 */
	public void setVisibility(Double visibility) {
		this.visibility = visibility;
	}

	/**
	 * @return the precip
	 */
	public Double getPrecip() {
		return precip;
	}

	/**
	 * @param precip
	 *            the precip to set
	 */
	public void setPrecip(Double precip) {
		this.precip = precip;
	}

	/**
	 * @return the precipAccum
	 */
	public Double getPrecipAccum() {
		return precipAccum;
	}

	/**
	 * @param precipAccum
	 *            the precipAccum to set
	 */
	public void setPrecipAccum(Double precipAccum) {
		this.precipAccum = precipAccum;
	}

	/**
	 * This is in m/s, which can be converted to mass/area/time using: one
	 * millimeter of rainfall is the equivalent of one liter of water per square
	 * meter and 1 liter of water weights 1 kg
	 * 
	 * @return the precipRate
	 */
	public Double getPrecipRate() {
		return precipRate;
	}

	/**
	 * @param precipRate
	 *            the precipRate to set
	 */
	public void setPrecipRate(Double precipRate) {
		this.precipRate = precipRate;
	}

	/**
	 * @return the precipType
	 */
	public PrecipType getPrecipType() {
		return precipType;
	}

	/**
	 * @param precipType
	 *            the precipType to set
	 */
	public void setPrecipType(PrecipType precipType) {
		this.precipType = precipType;
	}

	/**
	 * converts data (raw) value to enum
	 * 
	 * @param code
	 */
	public void setPrecipType(Short code) {
		this.precipType = PrecipType.convertCode(code);
	}

	/**
	 * this is an encoded description - see attribute comments
	 * 
	 * @return the precipIntensity
	 */
	public Short getPrecipIntensity() {
		return precipIntensity;
	}

	/**
	 * @param precipIntensity
	 *            the precipIntensity to set
	 */
	public void setPrecipIntensity(Short precipIntensity) {
		this.precipIntensity = precipIntensity;
	}

	/**
	 * @return the solarRadiation
	 */
	public Float getSolarRadiation() {
		return solarRadiation;
	}

	/**
	 * @param solarRadiation
	 *            the solarRadiation to set
	 */
	public void setSolarRadiation(Float solarRadiation) {
		this.solarRadiation = solarRadiation;
	}

	/**
	 * @return the tempSeaSurface
	 */
	public Double getTempSeaSurface() {
		return tempSeaSurface;
	}

	/**
	 * @param tempSeaSurface
	 *            the tempSeaSurface to set
	 */
	public void setTempSeaSurface(Double tempSeaSurface) {
		this.tempSeaSurface = tempSeaSurface;
	}

	/**
	 * @return the tempSoil
	 */
	public Double getTempSoil() {
		return tempSoil;
	}

	/**
	 * @param tempSoil
	 *            the tempSoil to set
	 */
	public void setTempSoil(Double tempSoil) {
		this.tempSoil = tempSoil;
	}

	/**
	 * @return the skyCover
	 */
	public String getSkyCover() {
		return skyCover;
	}

	/**
	 * @param skyCover
	 *            the skyCover to set
	 */
	public void setSkyCover(String skyCover) {
		if (skyCover == null)
			this.skyCover = null;
		else if (skyCover.equalsIgnoreCase("SKC"))
			this.skyCover = "CLR";
		else {
			this.skyCover = skyCover.trim();
		}
	}

	/**
	 * @return the skyLayerBase
	 */
	public Double getSkyLayerBase() {
		return skyLayerBase;
	}

	/**
	 * @param skyLayerBase
	 *            the skyLayerBase to set
	 */
	public void setSkyLayerBase(Double skyLayerBase) {
		this.skyLayerBase = skyLayerBase;
	}

	/**
	 * @return the totalColumnPWV
	 */
	public Double getTotalColumnPWV() {
		return totalColumnPWV;
	}

	/**
	 * @param totalColumnPWV
	 *            the totalColumnPWV to set
	 */
	public void setTotalColumnPWV(Double totalColumnPWV) {
		this.totalColumnPWV = totalColumnPWV;
	}

	/**
	 * @return the missingvalue
	 */
	public static float getMissingvalue() {
		return missingValue;
	}

	/**
	 * @return the fillvalue
	 */
	public static float getFillvalue() {
		return fillValue;
	}

}
