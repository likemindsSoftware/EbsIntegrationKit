package com.likeminds.custom.adapters.sp.utils;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EBSSQLConstants {

	public static List<String> preloaded_profiles = Arrays
			.asList(new String[] { "APPS_SSO", "ICX_LANGUAGE", "APPS_OVERRIDE_SSO_LANG", "APPS_SERVLET_AGENT",
					"APPS_SSO_AUTO_LINK_USER", "APPS_WEB_AGENT", "ICX_SESSION_COOKIE_DOMAIN" });

	public static Map<String, String> html2OracleLangMapping;

	static {
		Map<String, String> tempMap = new HashMap<String, String>();
		tempMap.put("ar", "ar");
		tempMap.put("as", "as");
		tempMap.put("bg", "bg");
		tempMap.put("bn", "bn");
		tempMap.put("ca", "ca");
		tempMap.put("cs", "cs");
		tempMap.put("da", "dk");
		tempMap.put("de", "d");
		tempMap.put("el", "el");
		tempMap.put("en", "us");
		tempMap.put("es", "e");
		tempMap.put("es-ar", "esa");
		tempMap.put("es-bo", "esa");
		tempMap.put("es-cl", "esa");
		tempMap.put("es-co", "esa");
		tempMap.put("es-cr", "esa");
		tempMap.put("es-do", "esa");
		tempMap.put("es-ec", "esa");
		tempMap.put("es-gt", "esa");
		tempMap.put("es-hn", "esa");
		tempMap.put("es-mx", "esa");
		tempMap.put("es-ni", "esa");
		tempMap.put("es-pa", "esa");
		tempMap.put("es-pe", "esa");
		tempMap.put("es-pr", "esa");
		tempMap.put("es-py", "esa");
		tempMap.put("es-sv", "esa");
		tempMap.put("es-us", "esa");
		tempMap.put("es-uy", "esa");
		tempMap.put("es-ve", "esa");
		tempMap.put("et", "et");
		tempMap.put("fi", "sf");
		tempMap.put("fr", "f");
		tempMap.put("fr-ca", "frc");
		tempMap.put("gu", "gu");
		tempMap.put("he", "iw");
		tempMap.put("iw", "iw");
		tempMap.put("hi", "hi");
		tempMap.put("hr", "hr");
		tempMap.put("hu", "hu");
		tempMap.put("in", "in");
		tempMap.put("id", "in");
		tempMap.put("is", "is");
		tempMap.put("it", "i");
		tempMap.put("ja", "ja");
		tempMap.put("kk", "ckk");
		tempMap.put("kk-cyrl", "ckk");
		tempMap.put("kn", "kn");
		tempMap.put("ko", "ko");
		tempMap.put("lt", "lt");
		tempMap.put("lv", "lv");
		tempMap.put("ml", "ml");
		tempMap.put("mr", "mr");
		tempMap.put("ms", "ms");
		tempMap.put("nl", "nl");
		tempMap.put("no", "n");
		tempMap.put("or", "or");
		tempMap.put("pa", "pa");
		tempMap.put("pl", "pl");
		tempMap.put("pt", "pt");
		tempMap.put("pt-br", "ptb");
		tempMap.put("ro", "ro");
		tempMap.put("ru", "ru");
		tempMap.put("sh", "lsr");
		tempMap.put("sk", "sk");
		tempMap.put("sl", "sl");
		tempMap.put("sq", "sq");
		tempMap.put("sr", "csr");
		tempMap.put("sr-cyrl", "csr");
		tempMap.put("sr-latn", "lsr");
		tempMap.put("sv", "s");
		tempMap.put("ta", "ta");
		tempMap.put("te", "te");
		tempMap.put("th", "th");
		tempMap.put("tr", "tr");
		tempMap.put("uk", "uk");
		tempMap.put("vi", "vn");
		tempMap.put("zh", "zhs");
		tempMap.put("zh-cn", "zhs");
		tempMap.put("zh-hk", "zht");
		tempMap.put("zh-mo", "zht");
		tempMap.put("zh-tw", "zht");
		html2OracleLangMapping = Collections.unmodifiableMap(tempMap);
	}

	public static final String CONST_AutoLink = "APPS_SSO_AUTO_LINK_USER";
	public static final String CONST_LocalLogin = "APPS_SSO_LOCAL_LOGIN";
	public static final String CONST_MaintenanceMode = "APPS_SECURITY_CONFIG_MAINTENANCE_MODE";
	public static final String CONST_IcxLanguage = "ICX_LANGUAGE";

	public static final String SQL_profile = "declare\n   v varchar2(4000):= null;\n   n varchar2(100):= :1;\nbegin\n  if (fnd_profile.defined(n)) \n   then \n       v:= fnd_profile.value(n);\n   end if;\n  :2:=v;\nend;";
	public static final String SQL_userstatus = "select  user_name, start_date, end_date,  decode((end_date - sysdate) - abs(end_date - sysdate), 0, 'N', 'Y') as EXPIRY_END_DATE_FLAG, decode(start_date, to_date('1', 'j'), 'Y', 'N') as START_DATE_PENDING_FLAG, nvl(fnd_preference.get(user_name,'APPS_SSO','DEFAULT_USER'),'No') as DEFAULT_USER_FLAG FROM FND_USER WHERE USER_GUID = hextoraw(:1)";
	public static final String SQL_ssouserlinkcheck = "DECLARE\n" + "   username varchar2(200) :=  :1 ;\n"
			+ "   uid number;\n" + "   prefVal varchar2(100);\n" + "   def boolean;\n" + "   profVal varchar2(100);\n"
			+ "BEGIN\n" + "   BEGIN\n"
			+ "           select user_id INTO uid FROM FND_USER WHERE user_name = username AND ( user_guid IS NULL  OR user_guid=HEXTORAW('1') ) ;\n"
			+ "           prefVal := NVL(fnd_preference.get(username,'APPS_SSO','AUTOLINKABLE'),'Yes') ;\n"
			+ "           fnd_profile.get_specific(\n" + "				   NAME_Z=>'APPS_SSO_LOCAL_LOGIN',\n"
			+ "                USER_ID_Z=>uid,\n" + "                RESPONSIBILITY_ID_Z=>-1,\n"
			+ "                APPLICATION_ID_Z=>-1,\n" + "                VAL_Z=>profVal,\n"
			+ "                DEFINED_Z=>def,\n" + "                ORG_ID_Z => -1,\n"
			+ "                SERVER_ID_Z =>-1);\n" + "            IF NOT def THEN\n"
			+ "                profVal := null;\n" + "            END IF;\n" + "    EXCEPTION WHEN NO_DATA_FOUND THEN\n"
			+ "        uid:=null;\n" + "        prefVal:=null;\n" + "        profVal:=null;\n"
			+ "        dbms_output.put_line('NO DATA FOUND');\n" + "    END;\n" + "    :2 := uid;\n"
			+ "    :3 := prefVal;\n" + "    :4 := profVal;\n" + "END;";

	public static final String SQL_userProfile = "DECLARE\n" + "  username varchar2(200) :=  :1;\n" + "  uid number;\n"
			+ "  pname varchar2(100) := :2;\n  def boolean;\n" + "  val varchar2(100);\n" + "BEGIN\n"
			+ " select user_id INTO uid FROM FND_USER WHERE user_name = username ; \n" + " fnd_profile.get_specific(\n"
			+ "        NAME_Z=>pname,\n" + "        USER_ID_Z=>uid,\n" + "        RESPONSIBILITY_ID_Z=>-1,\n"
			+ "        APPLICATION_ID_Z=>-1,\n" + "        VAL_Z=>val,\n" + "        DEFINED_Z=>def,\n"
			+ "        ORG_ID_Z => -1,\n" + "        SERVER_ID_Z =>-1);\n" + " IF NOT def THEN\n"
			+ "        val := null;\n" + " END IF;\n" + " :3 := val;\n" + "END;";

	public static final String SQL_updateguid = "DECLARE\n" + "user_guid fnd_user.user_guid%type:=:1;\n"
			+ "user_name fnd_user.user_name%type:=:2;\n" + "BEGIN\n" + "fnd_user_pkg.UpdateUser(\n"
			+ "x_user_name=>user_name\n" + ",x_owner=>null\n" + ",x_user_guid=>user_guid);\n" + "commit;\n" + "END;\n";

	public static void main(String[] args) {
		System.out.println(SQL_userProfile);
	}
}
