package eu.sweetlygeek.parsers;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.net.URLCodec;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;

import com.google.appengine.api.urlfetch.URLFetchService;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;
import com.google.wave.api.Blip;
import com.google.wave.api.Wavelet;

public class TumblrGetter extends BlipParser {
	
	private static final Logger LOGGER = Logger.getLogger(TumblrGetter.class);
	
	public static final String TUMBLR_TAG = "tumblr:";
	
	private static final String TUMBLR_URL = "http://romv.tumblr.com/api/read";
	
	private static TumblrGetter instance;
	private URLFetchService fetcher;
	private SAXParser parser;
	private URLCodec codec;
	
	private TumblrGetter()
	{
		this.fetcher = URLFetchServiceFactory.getURLFetchService();
		SAXParserFactory spf = SAXParserFactory.newInstance();
		this.parser = spf.newSAXParser();
		this.codec = new URLCodec();
	}

	@Override
	public void analyzeBlip(Blip blip, Wavelet currentWavelet) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void analyzeRequest(String request, Wavelet currentWavelet) {
		// TODO Auto-generated method stub

	}
	
	private URL getURLWithParams(Map<String, String> params) throws MalformedURLException
	{
		StringBuffer buf = new StringBuffer(TUMBLR_URL);
		try {
			Iterator<Map.Entry<String, String>> eIt = params.entrySet().iterator();
			while (eIt.hasNext())
			{
				Map.Entry<String, String> e = eIt.next();
				buf.append(codec.encode(e.getKey()) + "=" + codec.encode(e.getValue()));
				if (eIt.hasNext())
				{
					buf.append("&");
				}
			}
		} catch (EncoderException e) {
			LOGGER.error("Error while building url", e);
		}
		return new URL(buf.toString());
	}

}
