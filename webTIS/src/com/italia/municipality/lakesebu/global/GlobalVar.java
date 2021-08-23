package com.italia.municipality.lakesebu.global;

import java.io.File;

/**
 * 
 * @author Mark Italia
 * @version 1.0
 * @since 04/09/2020
 *
 */
public class GlobalVar {
	public static final String SEP = File.separator;
	public static final String APP_CONF_DIR = "C:"+SEP+"webtis"+SEP+"conf"+SEP;
	public static final String APP_DATABASE_CONF = "C:"+SEP+"webtis"+SEP+"conf"+SEP+"dbconf.max";
	public static final String LOG_FOLDER = "C:"+SEP+"webtis"+SEP+"log"+SEP;
	public static final boolean LOG_ENABLE = true;
	public static final String LICENSE_DATA_FILE = "C:"+SEP+"webtis"+SEP+"conf"+SEP+"dbconf.max";
	public static final String LICENSE_FILE_NAME= "C:"+SEP+"webtis"+SEP+"conf"+SEP+"data.max";
	public static final String LICENSE_EXP = "";
	public static final String UPLOADED_EXCEL_PATH_FOLDER = "C:"+SEP+"webtis"+SEP+"upload"+SEP;
	public static final String BARANGAY_CODE = "MUN ";
	public static final String BARANGAY="Poblaction";
	public static final String MUNICIPALITY="Lake Sebu";
	public static final String PROVINCE="South Cotabato";
	public static final String OWNER_IMG_FOLDER="C:"+SEP+"webtis"+SEP+"license"+SEP+"img"+SEP;
	public static final String OWNER_IMG_PATH="C:"+SEP+"webtis"+SEP+"license"+SEP;
	public static final String WATER_RENTAL_CERTIFICATE_RPT="water_rental_certificate";
	public static final String WATER_RENTAL_PAYMENT_HISTORY_RPT="water_rental_ors";
	public static final String MTO_OR_CEDULA_SIGNATORY="FERDINAND L. LOPEZ";
	//public static final String CEDULA_LQ310_PRINT="cedula_LQ310";
	//public static final String CEDULA_FX2175_PRINT="cedula_FX2175";
	//public static final String OR51_LQ310_PRINT="OR51_LQ310";
	//public static final String OR51_FX2175_PRINT="OR51_FX2175";
	public static final String QRCODE_CITIZEN = "citizen-qrcode";
	public static final int DEFAULT_BUFFER_SIZE = 10240; // 10KB.
	public static final String COUNTER_REPORT_FILE = "counter_report.tis";
	public static final String COLLECTOR_MODE_FILE="collector-mode.tis";
	public static final String UPLOAD_XML = "C:"+SEP+"webtis"+SEP+"upload-xml"+SEP;
	
}
