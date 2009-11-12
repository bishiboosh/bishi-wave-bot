package eu.sweetlygeek;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.google.wave.api.Blip;
import com.google.wave.api.Wavelet;

import eu.sweetlygeek.bots.BlipParser;
import eu.sweetlygeek.bots.DeezerBot;
import eu.sweetlygeek.bots.DropularGetter;
import eu.sweetlygeek.bots.FlickrGetter;
import eu.sweetlygeek.bots.RandomFap;
import eu.sweetlygeek.bots.TumblrGetter;

/** Enumeration containing all parsers
 * @author bishiboosh
 *
 */
public enum Bots {
	
	dropular (DropularGetter.class),
	tumblr (TumblrGetter.class),
	flickr (FlickrGetter.class),
	randomFap (RandomFap.class),
	deezer (DeezerBot.class);
	
	private BlipParser parser;
	
	@SuppressWarnings("unchecked")
	private Bots(Class clazz)
	{
		Logger logger = Logger.getLogger(Bots.class);
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
