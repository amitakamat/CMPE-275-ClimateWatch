package gash.obs.madis;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DataSet {
	private Date startDate;
	private Date endDate;
	private String fileName;
	private ArrayList<MesonetData> data;
	private Catalog catalog;

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public ArrayList<MesonetData> getData() {
		return data;
	}

	public void addData(MesonetData data) {
		this.data.add(data);
	}

	public void addData(List<MesonetData> dataset) {
		if (dataset == null)
			return;

		this.data.addAll(dataset);
	}

	public Catalog getCatalog() {
		return catalog;
	}

	public void setCatalog(Catalog catalog) {
		this.catalog = catalog;
	}

}
