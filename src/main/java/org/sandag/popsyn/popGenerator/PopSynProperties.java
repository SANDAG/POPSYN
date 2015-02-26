package org.sandag.popsyn.popGenerator;
/**
 * A class for user controlled properties and for logging run version
 * @author Wu Sun wsu@sandag.org
 *
 */

public class PopSynProperties {
	protected int landUseVersion;
	protected int dataSource;
	protected int puma;
	protected int taz;
	protected int version;
	protected String analyst;
	protected String startTime;
	protected String endTime;
	
	public int getLandUseVersion() {
		return landUseVersion;
	}
	public void setLandUseVersion(int landUseVersion) {
		this.landUseVersion = landUseVersion;
	}
	public int getDataSource() {
		return dataSource;
	}
	public void setDataSource(int dataSource) {
		this.dataSource = dataSource;
	}
	public int getPuma() {
		return puma;
	}
	public void setPuma(int puma) {
		this.puma = puma;
	}
	public int getTaz() {
		return taz;
	}
	public void setTaz(int taz) {
		this.taz = taz;
	}
	public int getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
	}
	public String getAnalyst() {
		return analyst;
	}
	public void setAnalyst(String analyst) {
		this.analyst = analyst;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
}
