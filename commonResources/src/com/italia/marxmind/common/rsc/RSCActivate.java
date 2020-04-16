package com.italia.marxmind.common.rsc;

/**
 * 
 * @author Mark Italia
 * @version 1.0
 * @since 04/06/2020
 *
 */
public class RSCActivate {
	private static volatile RSCActivate rsc;
	private RSCActivate() {}
	private String appExpiration;
	private String licenseFileName;
	private String licenseDataFile;
	
	public static RSCActivate getInstance() {
		
		if(rsc == null) {
			synchronized(RSCActivate.class) {
				if(rsc ==  null) {
					rsc = new RSCActivate();
				}
			}
		}
		return rsc;
	}

	public String getAppExpiration() {
		return appExpiration;
	}

	public void setAppExpiration(String appExpiration) {
		this.appExpiration = appExpiration;
	}

	public String getLicenseFileName() {
		return licenseFileName;
	}

	public void setLicenseFileName(String licenseFileName) {
		this.licenseFileName = licenseFileName;
	}

	public String getLicenseDataFile() {
		return licenseDataFile;
	}

	public void setLicenseDataFile(String licenseDataFile) {
		this.licenseDataFile = licenseDataFile;
	}
}
