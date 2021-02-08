package com.italia.municipality.lakesebu.utils;

import com.italia.municipality.lakesebu.enm.AppConf;

public final class AppGlobalVar {
	
	public static final String REPORT_FOLDER = AppConf.PRIMARY_DRIVE.getValue() +  AppConf.SEPERATOR.getValue() + 
			AppConf.APP_CONFIG_FOLDER_NAME.getValue() + AppConf.SEPERATOR.getValue() + AppConf.REPORT_FOLDER.getValue() + AppConf.SEPERATOR.getValue();
	public static final String TREASURER_COLLECTION_DEPOSIT_REPORT = "treasury-collection-deposit";
	public static final String TREASURER_COLLECTION_DEPOSIT_OVER_32_REPORT = "treasury-collection-deposit-over-32";
	public static final String TREASURER_COLLECTION_DEPOSIT_OVER_35_REPORT = "treasury-collection-deposit-over-35";
	
}
