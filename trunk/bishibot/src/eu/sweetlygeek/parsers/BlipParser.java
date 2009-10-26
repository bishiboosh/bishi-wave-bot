package eu.sweetlygeek.parsers;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParser;

import org.apache.commons.lang.StringUtils;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.google.wave.api.Blip;
import com.google.wave.api.Image;
import com.google.wave.api.TextView;
import com.google.wave.api.Wavelet;

import eu.sweetlygeek.handlers.ImageHandler;

/** Interface for all blip parsers
 * @author bishiboosh
 *
 */
public abstract class BlipParser {
	
	protected abstract String getTag();
	
	public void analyzeBlip(Blip blip, Wavelet currentWavelet)
	{
		analyzeBlip(blip, currentWavelet, getTag());
	}
	
	protected void addImage(Blip blip, String littleUrl, String bigUrl)
	{
		TextView doc = blip.getDocument();
		Image image = new Image();
		image.setUrl(littleUrl);
		doc.appendElement(image);
		doc.appendMarkup("<a href='" + bigUrl + "'>" + bigUrl + "</a>");
		doc.append("\n");
	}
	
	protected abstract void analyzeRequest(String request, Wavelet currentWavelet);
	
	protected void analyzeBlip(Blip blip, Wavelet wavelet, String tag)
	{
		String text = blip.getDocument().getText();
		List<String> words = Arrays.asList(StringUtils.split(text));
		List<String> requests = new ArrayList<String>();
		boolean found = false;
		for (String word : words)
		{
			if (StringUtils.contains(word, tag))
			{
				found = true;
				requests.add(word);
			}
		}
		if (!found)
		{
			return;
		}
		else
		{
			for (String request : requests)
			{
				analyzeRequest(request, wavelet);
			}
		}
	}
	
	protected void parseXMLResponse(byte[] content, Wavelet wavelet, SAXParser parser, ImageHandler handler) throws SAXException, IOException {
		StringBuffer xmlBuf = new StringBuffer();
		BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(content)));
		String line = br.readLine();
		while (line != null)
		{
			xmlBuf.append(line);
			line = br.readLine();
		}
		// On enlève les & qui trainent
		String cleanXML = StringUtils.replace(xmlBuf.toString(), "&", "&amp;");
		
		InputSource is = new InputSource(new StringReader(cleanXML));
		parser.parse(is, handler);
		
		Blip blip = wavelet.appendBlip();
		Map<String, String> bigMap = handler.getBigMap();
		Map<String, String> miniMap = handler.getMiniMap();
		for (String dropId : bigMap.keySet())
		{
			addImage(blip, miniMap.get(dropId), bigMap.get(dropId));
		}
	}

}
