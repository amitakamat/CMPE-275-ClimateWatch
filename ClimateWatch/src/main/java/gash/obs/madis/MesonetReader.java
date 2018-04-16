package gash.obs.madis;

import java.awt.Rectangle;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ucar.nc2.Attribute;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;
import ucar.nc2.dataset.NetcdfDataset;

/**
 * List of providers: http://madis.noaa.gov/mesonet_providers.html
 * 
 * Note: only the mandatory levels are extracted. If the significant levels are
 * required they should be processed/added similar to mandatory.
 * 
 * @author gash1
 * 
 */
public class MesonetReader {
	protected static Logger log = LoggerFactory.getLogger("mesonet");

	private String WFOTranslationFile;
	private Properties WFOMap;

	// private boolean enableTimeCheck = false;
	private Catalog catalog;

	/**
	 * System.out - information about a netcdf file. Used to construct the class
	 * attributes
	 */
	@SuppressWarnings("deprecation")
	public void info(File f) {
		NetcdfFile nf = null;
		try {
			nf = NetcdfFile.open(f.getAbsolutePath());
			NetcdfDataset ds = new NetcdfDataset(nf);

			System.out.println("Title: " + ds.getTitle());
			System.out.println("Convention: " + ds.getConventionUsed());

			System.out.println("\nVariables:");
			List<Variable> vars = ds.getVariables();
			for (Variable v : vars) {
				if (v.getName().indexOf("QC") != -1 || v.getName().indexOf("DD") != -1
						|| v.getName().indexOf("IC") != -1)
					continue;

				System.out.println(v.getName() + " : " + v.getDescription());
				System.out.println("    units: " + v.getUnitsString());
				System.out.println("    attributes:");
				List<Attribute> attrs = v.getAttributes();
				if (attrs != null) {
					for (Attribute a : attrs)
						System.out.println("       " + a.getName() + " = " + a.getStringValue());
				}

				// System.out.println("\n/** " + v.getDescription() +
				// ", units = " + v.getUnitsString() + " **/");
				// System.out.println("private " + v.getDataType().toString() +
				// " " + v.getName() + ";");
			}

			// System.out.println("\nDimensions:");
			// List<Dimension> dims = ds.getDimensions();
			// for (Dimension d : dims) {
			// System.out.println("Name: " + d.getName());
			// System.out.println("length: " + d.getLength());
			// }

		} catch (IOException ioe) {
			throw new RuntimeException("Failed to open " + f.getAbsolutePath(), ioe);
		} finally {
			if (null != nf)
				try {
					nf.close();
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
		}
	}

	/**
	 * extract profile data
	 * 
	 * TODO need to ensure (non) existence of parameters (e.g., tropo)
	 * 
	 * TODO use variable units
	 * 
	 * @param f
	 * @param upperLeftLat
	 * @param upperLeftLon
	 * @param lowerRightLat
	 * @param lowerRightLon
	 * @return
	 * @throws Exception
	 */
	public List<MesonetData> extract(File f, Date startDate, Date endDate, Rectangle region,
			Set<String> stationIds) throws Exception {
		List<MesonetData> rtn = new ArrayList<MesonetData>();
		NetcdfFile nf = null;
		try {
			nf = NetcdfFile.open(f.getAbsolutePath());

			String[] stationName = null;
			char[][] tmp = (char[][]) nf.findVariable("stationName").read().copyToNDJavaArray();
			stationName = new String[tmp.length];
			for (int n = 0; n < tmp.length; n++)
				stationName[n] = String.valueOf(tmp[n]).trim();

			String[] stationId = null;
			tmp = (char[][]) nf.findVariable("stationId").read().copyToNDJavaArray();
			stationId = new String[tmp.length];
			for (int n = 0; n < tmp.length; n++)
				stationId[n] = String.valueOf(tmp[n]).trim();

			// trimString() trims a string if embedded nulls are found (netcdf
			// classic style). This is different from the check() QCs.
			String[] stationType = null;
			tmp = (char[][]) nf.findVariable("stationType").read().copyToNDJavaArray();
			stationType = new String[tmp.length];
			for (int n = 0; n < tmp.length; n++)
				stationType[n] = String.valueOf(tmp[n]).trim();

			String[] dataProvider = null;
			tmp = (char[][]) nf.findVariable("dataProvider").read().copyToNDJavaArray();
			dataProvider = new String[tmp.length];
			for (int n = 0; n < tmp.length; n++)
				dataProvider[n] = String.valueOf(tmp[n]).trim();

			String[] skyCvr = null;
			try {
				char[][][] tmp2 = (char[][][]) nf.findVariable("skyCvr").read().copyToNDJavaArray();
				skyCvr = new String[tmp2.length];
				for (int n = 0; n < tmp2.length; n++)
					skyCvr[n] = String.valueOf(tmp2[n][0]).trim();
			} catch (Exception ex) {
				; // now skycover available
			}

			// pressure as pascal, spd as m/s, temp as K
			float[] lat = (float[]) nf.findVariable("latitude").read().copyTo1DJavaArray();
			float[] lon = (float[]) nf.findVariable("longitude").read().copyTo1DJavaArray();
			float[] elev = (float[]) nf.findVariable("elevation").read().copyTo1DJavaArray();
			int[] wmo = (int[]) nf.findVariable("numericWMOid").read().copyTo1DJavaArray();
			double[] obstime = (double[]) nf.findVariable("observationTime").read().copyTo1DJavaArray();
			double[] rcvtime = (double[]) nf.findVariable("receivedTime").read().copyTo1DJavaArray();
			float[] pres = (float[]) nf.findVariable("stationPressure").read().copyTo1DJavaArray();
			float[] altimeter = (float[]) nf.findVariable("altimeter").read().copyTo1DJavaArray();
			float[] seaPres = (float[]) nf.findVariable("seaLevelPressure").read().copyTo1DJavaArray();
			float[] temp = (float[]) nf.findVariable("temperature").read().copyTo1DJavaArray();
			char[] tempQC = (char[]) nf.findVariable("temperatureDD").read().copyTo1DJavaArray();
			float[] dewpt = (float[]) nf.findVariable("dewpoint").read().copyTo1DJavaArray();
			float[] relHum = (float[]) nf.findVariable("relHumidity").read().copyTo1DJavaArray();
			float[] windspd = (float[]) nf.findVariable("windSpeed").read().copyTo1DJavaArray();
			float[] windgust = (float[]) nf.findVariable("windGust").read().copyTo1DJavaArray();
			char[] windspdQC = (char[]) nf.findVariable("windSpeedDD").read().copyTo1DJavaArray();
			float[] winddir = (float[]) nf.findVariable("windDir").read().copyTo1DJavaArray();
			float[] visibility = (float[]) nf.findVariable("visibility").read().copyTo1DJavaArray();
			float[] rawPrecip = (float[]) nf.findVariable("rawPrecip").read().copyTo1DJavaArray();
			float[] precipAccum = (float[]) nf.findVariable("precipAccum").read().copyTo1DJavaArray();
			float[] precipRate = (float[]) nf.findVariable("precipRate").read().copyTo1DJavaArray();
			short[][] precipType = (short[][]) nf.findVariable("precipType").read().copyToNDJavaArray();
			short[][] precipIntensity = (short[][]) nf.findVariable("precipIntensity").read().copyToNDJavaArray();
			float[] solarRadiation = (float[]) nf.findVariable("solarRadiation").read().copyTo1DJavaArray();

			float[] seaSurfaceTemp = null;
			try {
				seaSurfaceTemp = (float[]) nf.findVariable("seaSurfaceTemp").read().copyTo1DJavaArray();
			} catch (Exception ex) {
				; // no sea surface available
			}

			float[] totalColumnPWV = null;
			try {
				totalColumnPWV = (float[]) nf.findVariable("totalColumnPWV").read().copyTo1DJavaArray();
			} catch (Exception ex) {
				; // no sea surface available
			}

			float[] soilTemperature = null;
			try {
				soilTemperature = (float[]) nf.findVariable("soilTemperature").read().copyTo1DJavaArray();
			} catch (Exception ex) {
				; // no soil temp available
			}

			float[][] skyCovLayerBase = null;
			try {
				skyCovLayerBase = (float[][]) nf.findVariable("skyCovLayerBase").read().copyToNDJavaArray();
			} catch (Exception ex) {
				; // now skycover available
			}

			for (int r = 0; r < lat.length; r++) {
				// invalid name/ID
				if (stationName[r] == null || !ObsQCRange.checkName(stationName[r]) || stationId[r] == null
						|| !ObsQCRange.checkName(stationId[r]))
					continue;

				// missing location
				if (!ObsQCRange.checkLatitude((double) lat[r]) || !ObsQCRange.checkLongitude((double) lon[r])) {
					log.warn("Rejected record (bad lat,lon): " + wmo[r] + ", from file: " + f);
					continue;
				}

				// missing time
				if (obstime[r] <= MesonetData.missingValue || obstime[r] >= MesonetData.fillValue
						|| !ObsQCRange.checkDate((long) obstime[r]))
					continue;

				// spatial bounding
				if (region != null && !region.contains(lat[r], lon[r])) {
					// System.out.println("Rejected point " + lat[r] + ", " +
					// lon[r]);
					continue;
				}

				// temporal bounding
				Date obsDate = new Date((long) obstime[r] * 1000);
				if (startDate != null && obsDate.before(startDate))
					continue; // too early
				if (endDate != null && obsDate.after(endDate))
					continue; // too late

				MesonetData record = new MesonetData();

				record.setStationID(stationId[r]);
				record.setStationName(stationName[r]);
				record.setStationType(stationType[r]); // not sure if this is
														// the correct field
				record.setDataProvider(dataProvider[r]);

				if (wmo[r] != MesonetData.missingValue && wmo[r] != -2147483647)
					record.setWmoId(wmo[r]);

				if (skyCvr != null)
					record.setSkyCover(skyCvr[r]);

				// validated above
				record.setLatitude(lat[r]);
				record.setLongitude(lon[r]);
				record.setTimeObs(obstime[r]);

				if (elev[r] != MesonetData.missingValue && elev[r] != MesonetData.fillValue)
					record.setElevation((double) elev[r]);

				if (rcvtime[r] != MesonetData.missingValue && rcvtime[r] != MesonetData.fillValue)
					record.setTimeReceived(rcvtime[r]);

				if (ObsQCRange.checkStationPressure((double) pres[r]))
					record.setStationPressure((double) pres[r]);

				if (ObsQCRange.checkAltimeter((double) altimeter[r]))
					record.setAltimeter((double) altimeter[r]);

				if (ObsQCRange.checkSeaLevelPressure((double) seaPres[r]))
					record.setSeaLevelPress((double) seaPres[r]);

				if (ObsQCRange.checkTemperature((double) temp[r]))
					record.setTemperature((double) temp[r]);

				record.setTemperatureQC(String.valueOf(tempQC[r]));

				if (ObsQCRange.checkDewpoint((double) dewpt[r]))
					record.setDewpoint((double) dewpt[r]);

				if (ObsQCRange.checkRelativeHumidity((double) relHum[r]))
					record.setRelHumidity((double) relHum[r]);

				if (ObsQCRange.checkWindDirection((double) winddir[r]))
					record.setWindDir((double) winddir[r]);

				if (ObsQCRange.checkWindSpeed((double) windspd[r]))
					record.setWindSpeed((double) windspd[r]);

				record.setWindSpeedQC(String.valueOf(windspdQC[r]));

				if (ObsQCRange.checkWindGust((double) windgust[r]))
					record.setWindGust((double) windgust[r]);

				if (ObsQCRange.checkVisibility((double) visibility[r]))
					record.setVisibility((double) visibility[r]);

				if (ObsQCRange.checkPrecipRate((double) rawPrecip[r]))
					record.setPrecip((double) rawPrecip[r]);

				if (ObsQCRange.checkPrecipRate((double) precipAccum[r]))
					record.setPrecipAccum((double) precipAccum[r]);

				// cannot use range checks: they are just the height (implies
				// per hr)
				if (precipRate[r] != MesonetData.missingValue && precipRate[r] != MesonetData.fillValue) {
					record.setPrecipRate((double) precipRate[r]);
				}

				// TODO should do something with the array
				if (precipType[r][0] != MesonetData.missingValue && precipType[r][0] != -32767)
					record.setPrecipType(precipType[r][0]);

				// TODO should do something with the array
				if (precipIntensity[r][0] != MesonetData.missingValue && precipIntensity[r][0] != -32767)
					record.setPrecipIntensity(precipIntensity[r][0]);

				if (solarRadiation[r] != MesonetData.missingValue
						&& solarRadiation[r] != MesonetData.fillValue)
					record.setSolarRadiation(solarRadiation[r]);

				if (seaSurfaceTemp != null) {
					if (ObsQCRange.checkSeaTemperature((double) seaSurfaceTemp[r]))
						record.setTempSeaSurface((double) seaSurfaceTemp[r]);
				}

				if (totalColumnPWV != null && totalColumnPWV[r] != MesonetData.missingValue
						&& totalColumnPWV[r] != MesonetData.fillValue) {
					record.setTotalColumnPWV((double) totalColumnPWV[r]);
				}

				if (soilTemperature != null) {
					if (ObsQCRange.checkSoilTemperature((double) soilTemperature[r]))
						record.setTempSoil((double) soilTemperature[r]);
				}

				// TODO should do something with the array
				if (skyCovLayerBase != null && skyCovLayerBase[r][0] != MesonetData.missingValue
						&& skyCovLayerBase[r][0] != MesonetData.fillValue)
					record.setSkyLayerBase((double) skyCovLayerBase[r][0]);

				// Reject the collection agency arlfrd. This is duplicated in
				// the INEL collection
				// A better solution would be to have a downstream system check
				// locations and
				// remove that way
				if (stationType != null && "arlfrd".equals(stationType[r])) {
					continue;
				}

				// Need to check to make sure station id is one requested
				if (stationIds == null || stationIds.contains(record.getStationID()))
					rtn.add(record);
			}

		} catch (IOException ioe) {
			throw new RuntimeException("Failed to open " + f.getAbsolutePath(), ioe);
		} finally {
			if (null != nf)
				try {
					nf.close();
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
		}

		return rtn;
	}

	/**
	 * The MADIS mesonet data set does not have a catalog therefore, we need to
	 * create one from reading the data
	 * 
	 * @param f
	 * @return
	 */
	public List<Station> extractCatalog(File f) {
		if (f == null || !f.exists())
			return null;

		List<Station> rtn = new ArrayList<Station>();

		NetcdfFile nf = null;
		try {
			nf = NetcdfFile.open(f.getAbsolutePath());

			String[] stationName = null;
			char[][] tmp = (char[][]) nf.findVariable("stationName").read().copyToNDJavaArray();
			stationName = new String[tmp.length];
			for (int n = 0; n < tmp.length; n++)
				stationName[n] = String.valueOf(tmp[n]).trim();

			String[] stationId = null;
			tmp = (char[][]) nf.findVariable("stationId").read().copyToNDJavaArray();
			stationId = new String[tmp.length];
			for (int n = 0; n < tmp.length; n++)
				stationId[n] = String.valueOf(tmp[n]).trim();

			// city/state
			String[] homeWFO = null;
			tmp = (char[][]) nf.findVariable("homeWFO").read().copyToNDJavaArray();
			homeWFO = new String[tmp.length];
			for (int n = 0; n < tmp.length; n++)
				homeWFO[n] = String.valueOf(tmp[n]).trim();

			// trimString() trims a string if embedded nulls are found (netcdf
			// classic style). This is different from the check() QCs
			String[] originatingMeso = null;
			tmp = (char[][]) nf.findVariable("stationType").read().copyToNDJavaArray();
			originatingMeso = new String[tmp.length];
			for (int n = 0; n < tmp.length; n++)
				originatingMeso[n] = ObsQCRange.trimString(String.valueOf(tmp[n]).trim());

			float[] lat = (float[]) nf.findVariable("latitude").read().copyTo1DJavaArray();
			float[] lon = (float[]) nf.findVariable("longitude").read().copyTo1DJavaArray();
			float[] elev = (float[]) nf.findVariable("elevation").read().copyTo1DJavaArray();
			int[] wmo = (int[]) nf.findVariable("numericWMOid").read().copyTo1DJavaArray();

			for (int r = 0; r < lat.length; r++) {
				// invalid name
				if (stationName[r] == null || !ObsQCRange.checkName(stationName[r]))
					continue;

				// missing location
				if (!ObsQCRange.checkLatitude((double) lat[r]) || !ObsQCRange.checkLongitude((double) lon[r]))
					continue;

				Station stat = new Station();

				stat.setName(stationName[r]);
				stat.setActive(true);
				stat.setSource("madis-mesonet");
				stat.setMesonet(originatingMeso[r]);
				stat.setType(Station.MeasurementType.Surface);

				stat.setLat(lat[r]);
				stat.setLon(lon[r]);

				if (elev[r] != MesonetData.missingValue && elev[r] != MesonetData.fillValue)
					stat.setElevation(elev[r]); // meter

				// we don't allow the id to be null as it is one of the
				// ways to distinguish duplicate stations

				// NOTE: the stationId must match MadisKeys.java
				stat.setId(stationId[r]);
				if (stat.getId() == null) {
					if (wmo[r] != MesonetData.missingValue && wmo[r] != -2147483647)
						stat.setId(String.valueOf(wmo[r]));
					else
						stat.setId(lat[r] + "-" + lon[r]);
				}

				// invalid name
				if (ObsQCRange.checkName(stat.getId()))
					rtn.add(stat);

				// FSL seems to be the only WFO they add (also looks like the
				// location was appended to station name)
				/*
				 * if (WFOMap != null && homeWFO[r] != null) { String note =
				 * WFOMap.getProperty(homeWFO[r]); if (note != null)
				 * stat.setNote(note); else stat.setNote("Unknown WFO ID: " +
				 * homeWFO[r]); }
				 */
			}

		} catch (Exception ex) {
		} finally {
			if (null != nf)
				try {
					nf.close();
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
		}

		return rtn;
	}

	/**
	 * spatial query
	 * 
	 * @param f
	 * @param upperLeftLat
	 * @param upperLeftLon
	 * @param lowerRightLat
	 * @param lowerRightLon
	 * @return
	 */
	public List<MesonetData> read(File f, Date startDate, Date endDate, Rectangle region, Set<String> stationIds) {
		List<MesonetData> r = null;
		try {
			r = extract(f, startDate, endDate, region, stationIds);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return r;
	}

	/**
	 * read and return all data from the file
	 * 
	 * @param f
	 * @return
	 */
	public List<MesonetData> read(File f) {
		List<MesonetData> r = null;
		try {
			r = extract(f, null, null, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return r;
	}

	/**
	 * @return the wFOTranslationFile
	 */
	public String getWFOTranslationFile() {
		return WFOTranslationFile;
	}

	/**
	 * @param wFOTranslationFile
	 *            the wFOTranslationFile to set
	 */
	public void setWFOTranslationFile(String wFOTranslationFile) {
		WFOTranslationFile = wFOTranslationFile;

		if (WFOTranslationFile == null)
			return;

		File f = new File(WFOTranslationFile);
		if (!f.exists())
			return;

		WFOMap = new Properties();
		FileInputStream is = null;
		try {
			is = new FileInputStream(f);
			WFOMap.load(is);
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (is != null)
					is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public Catalog getCatalog() {
		return catalog;
	}

	public void setCatalog(Catalog catalog) {
		this.catalog = catalog;
	}

	public DataSet readFile(File obsfile) throws Exception {
		DataSet rtn = new DataSet();
		rtn.setFileName(obsfile.getAbsolutePath());

		List<Station> rawStations = extractCatalog(obsfile);
		if (rawStations != null) {
			if (catalog == null)
				catalog = new Catalog();

			// merge/update catalog data
			for (Station station : rawStations)
				catalog.addStation(station);

			rtn.setCatalog(catalog);
		}

		List<MesonetData> rawData = extract(obsfile, null, null, null, null);
		rtn.addData(rawData);

		return rtn;
	}
}
