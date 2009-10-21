package eu.sweetlygeek.parsers;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.net.URLCodec;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.appengine.api.urlfetch.URLFetchService;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;
import com.google.wave.api.Wavelet;

import eu.sweetlygeek.handlers.TumblrHandler;

public class TumblrGetter extends BlipParser {
	
	private static final Logger LOGGER = Logger.getLogger(TumblrGetter.class);
	
	public static final String TUMBLR_TAG = "tumblr:";
	
	private static final String TUMBLR_URL = "http://romv.tumblr.com/api/read";
	
	private static TumblrGetter instance;
	private URLFetchService fetcher;
	private SAXParser parser;
	private URLCodec codec;
	
	private TumblrGetter() throws ParserConfigurationException, SAXException
	{
		this.fetcher = URLFetchServiceFactory.getURLFetchService();
		SAXParserFactory spf = SAXParserFactory.newInstance();
		this.parser = spf.newSAXParser();
		this.codec = new URLCodec();
	}
	
	public static synchronized TumblrGetter getInstance() throws ParserConfigurationException, SAXException
	{
		if (instance == null)
		{
			instance = new TumblrGetter();
		}
		return instance;
	}

	@Override
	protected void analyzeRequest(String request, Wavelet currentWavelet) {
		String[] params = request.split(":");
		if (params.length >= 2)
		{
			boolean hasNumber = true;
			int results = -1;
			try {
				results = Math.abs(Integer.parseInt(params[1]));
			} catch (NumberFormatException e) {
				results = 10;
				hasNumber = false;
			}
			String tag = null;
			if (params.length >= 3)
			{
				tag = params[2];
			}
			else if (!hasNumber)
			{
				tag = params[1];
			}
			
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("num", "50");
			paramMap.put("type", "photo");
			if (tag != null)
			{
				paramMap.put("search", tag);
			}
			try {
				URL url = getURLWithParams(paramMap);
				HTTPResponse res= this.fetcher.fetch(url);
				if (res != null)
				{
					parseResponse(res.getContent(), results, currentWavelet);
				}
			} catch (MalformedURLException e) {
				LOGGER.error("Error while analyzing request", e);
			} catch (IOException e) {
				LOGGER.error("Error while analyzing request", e);
			} catch (SAXException e) {
				LOGGER.error("Error while analyzing request", e);
			}
		}
	}
	
	private void parseResponse(byte[] content, int results, Wavelet wavelet) throws SAXException, IOException {
		parseXMLResponse(content, wavelet, parser, new TumblrHandler(results));		
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

	@Override
	protected String getTag() {
		return TUMBLR_TAG;
	}

}
