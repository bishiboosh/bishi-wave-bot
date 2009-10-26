package eu.sweetlygeek.parsers;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.google.wave.api.Blip;
import com.google.wave.api.Wavelet;

/** Enumeration containing all parsers
 * @author bishiboosh
 *
 */
public enum Parser {
	
	dropular (DropularGetter.class),
	tumblr (TumblrGetter.class),
	flickr (FlickrGetter.class),
	randomFap (RandomFap.class);
	
	private BlipParser parser;
	
	@SuppressWarnings("unchecked")
	private Parser(Class clazz)
	{
		Logger logger = Logger.getLogger(Parser.class);
		try {
			this.parser = (BlipParser) clazz.newInstance();
		} catch (InstantiationException e) {
			logger.error("Error while instantiating", e);
		} catch (IllegalAccessException e) {
			logger.error("Error while instantiating", e);
		}
	}
	
	/** Launch the analysis of this blip in this parser
	 * @param blip
	 * @param currentWavelet
	 */
	public void analyzeBlip(Blip blip, Wavelet currentWavelet)
	{
		String text = blip.getDocument().getText();
		if (StringUtils.contains(text, parser.getTag()))
		{
			parser.analyzeBlip(blip, currentWavelet);
		}
	}
}
