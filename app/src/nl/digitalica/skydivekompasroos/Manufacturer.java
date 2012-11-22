package nl.digitalica.skydivekompasroos;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;

public class Manufacturer {

	public String name;
	public String url;
	public String remarks;

	public Manufacturer(String mName, String mUrl, String mRemarks) {
		this.name = mName;
		this.url = mUrl;
		this.remarks = mRemarks;

	}

	/***
	 * Returns a hash containing all manufacturers by name
	 * 
	 * @return
	 */
	static public HashMap<String, Manufacturer> getManufacturerHash(
			Context context) {
		XmlResourceParser manufacturersParser = context.getResources().getXml(
				R.xml.manufacturers);
		int eventType = -1;

		HashMap<String, Manufacturer> manufacturerHashMap = new HashMap<String, Manufacturer>();
		while (eventType != XmlResourceParser.END_DOCUMENT) {
			if (eventType == XmlResourceParser.START_TAG) {
				String strName = manufacturersParser.getName();
				if (strName.equals("manufacturer")) {
					String manufacturerName = manufacturersParser
							.getAttributeValue(null, "name");
					String manufacturerUrl = manufacturersParser
							.getAttributeValue(null, "url");
					String manufacturerRemarks = manufacturersParser
							.getAttributeValue(null, "remarks");
					Manufacturer manufacturer = new Manufacturer(
							manufacturerName, manufacturerUrl,
							manufacturerRemarks);
					manufacturerHashMap.put(manufacturer.name, manufacturer);
				}

			}
			try {
				eventType = manufacturersParser.next();
			} catch (XmlPullParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return manufacturerHashMap;

	}

}
