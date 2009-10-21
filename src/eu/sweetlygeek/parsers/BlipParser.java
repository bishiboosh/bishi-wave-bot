package eu.sweetlygeek.parsers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.google.wave.api.Blip;
import com.google.wave.api.Image;
import com.google.wave.api.TextView;
import com.google.wave.api.Wavelet;

/** Interface for all blip parsers
 * @author bishiboosh
 *
 */
public abstract class BlipParser {
	
	public abstract void analyzeBlip(Blip blip, Wavelet currentWavelet);
	
	protected void addImage(Blip blip, String littleUrl, String bigUrl)
	{
		TextView doc = blip.getDocument();
		Image image = new Image();
		image.setUrl(littleUrl);
//		String img = doc.appendElement(image);
		doc.appendMarkup("<a href='" + bigUrl + "'><img src='" + littleUrl + "' /></a>");
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

}
