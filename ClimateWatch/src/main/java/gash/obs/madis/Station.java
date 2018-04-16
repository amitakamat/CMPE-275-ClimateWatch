package gash.obs.madis;

import java.io.Serializable;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The station data (lat, lon, ...)
 * 
 * @author gash1
 * 
 */

@XmlRootElement(name = "station")
@XmlAccessorType(XmlAccessType.FIELD)
public class Station implements Comparable<Station>, Cloneable, Serializable {
	private static final long serialVersionUID = 1L;
	public static final double sMissingData = -9999.0f;
	public static final String sMissingDataStr = "-9999.0";
	
	public enum MeasurementType {
		// type of data measurement
		Surface(1), Missing(-9999);

		private int value;

		private MeasurementType(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

		public static MeasurementType toEnum(int v) {
			if (v == 1)
				return Surface;
			else
				return Missing;
		}
	};

	/**
	 * ID as assigned by the collecting org or mesonet. This ID may not be
	 * unique across all met data
	 **/
	protected String id;

	/** latitude in digital degrees **/
	protected double lat = sMissingData;

	/** longitude in digital degrees */
	protected double lon = sMissingData;

	/** in meters **/
	protected double agl = sMissingData;

	/** in meters **/
	protected double elevation = sMissingData;

	protected String name;
	protected MeasurementType type = MeasurementType.Missing;

	/** mesonet ID, mesonet is the string name of the ID */
	protected int mesonetID;

	/** mesonet the station belongs to **/
	protected String mesonet;

	/** the source of the data, the publisher (e.g., mesowest) **/
	protected String source;

	/** is data being collected **/
	protected boolean active = true;

	/** when this data first was collected by this system **/
	protected Date dataFrom;

	/** when this data was last updated **/
	protected Date dataTo;

	protected String state;
	protected String city;
	protected String country;

	/**
	 * Empty constructor
	 */
	public Station() {
		// id = String.valueOf(sMissingData);
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the lat
	 */
	public double getLat() {
		return lat;
	}

	/**
	 * @param lat
	 *            the lat to set
	 */
	public void setLat(double lat) {
		this.lat = lat;
	}

	/**
	 * @return the lon
	 */
	public double getLon() {
		return lon;
	}

	/**
	 * @param lon
	 *            the lon to set
	 */
	public void setLon(double lon) {
		this.lon = lon;
	}

	/**
	 * @return the agl in m
	 */
	public double getAgl() {
		return agl;
	}

	/**
	 * @param agl
	 *            the agl to set
	 */
	public void setAgl(double agl) {
		this.agl = agl;
	}

	/**
	 * @return the elevation in m
	 */
	public double getElevation() {
		return elevation;
	}

	/**
	 * @param elevation
	 *            the elevation to set
	 */
	public void setElevation(double elevation) {
		this.elevation = elevation;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the type
	 */
	public MeasurementType getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(MeasurementType type) {
		this.type = type;
	}

	/**
	 * @return the mesonet that collected the data
	 */
	public String getMesonet() {
		return mesonet;
	}

	/**
	 * @param mesonet
	 *            the mesonet that collected the data
	 */
	public void setMesonet(String mesonet) {
		this.mesonet = mesonet;
	}

	/**
	 * @return agency that collected the data (source)
	 */
	public String getSource() {
		return source;
	}

	/**
	 * @param source
	 * the agency that collected the data (source)
	 */
	public void setSource(String source) {
		this.source = source;
	}

	/**
	 * @return the active
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * @param active
	 *            the active to set
	 */
	public void setActive(boolean active) {
		this.active = active;
	}

	/**
	 * @return the oldest date stored for the data
	 */
	public Date getDataFrom() {
		return dataFrom;
	}

	/**
	 * @param dataFrom
	 *            the oldest date stored for the data
	 */
	public void setDataFrom(Date dataFrom) {
		this.dataFrom = dataFrom;
	}

	/**
	 * @return the latest date that data was collected
	 */
	public Date getDataTo() {
		return dataTo;
	}

	/**
	 * @param dataTo
	 *            the latest date that data was collected
	 */
	public void setDataTo(Date dataTo) {
		this.dataTo = dataTo;
	}

	/**
	 * @return the state
	 */
	public String getState() {
		return state;
	}

	/**
	 * @param state
	 *            the state to set
	 */
	public void setState(String state) {
		this.state = state;
	}

	/**
	 * @return the city
	 */
	public String getCity() {
		return city;
	}

	/**
	 * @param city
	 *            the city to set
	 */
	public void setCity(String city) {
		this.city = city;
	}

	/**
	 * @return the country
	 */
	public String getCountry() {
		return country;
	}

	/**
	 * @param country
	 *            the country to set
	 */
	public void setCountry(String country) {
		this.country = country;
	}

	@Override
	public int compareTo(Station to) {
		return id.compareTo(to.id);
	}

	/**
	 * @return the mesonetID
	 */
	public int getMesonetID() {
		return mesonetID;
	}

	/**
	 * @param mesonetID
	 *            the mesonetID to set
	 */
	public void setMesonetID(int mesonetID) {
		this.mesonetID = mesonetID;
	}
}
