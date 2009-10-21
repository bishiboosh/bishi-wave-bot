package eu.sweetlygeek.servlets;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import com.google.wave.api.AbstractRobotServlet;
import com.google.wave.api.Blip;
import com.google.wave.api.Event;
import com.google.wave.api.RobotMessageBundle;
import com.google.wave.api.TextView;
import com.google.wave.api.Wavelet;

import eu.sweetlygeek.parsers.DropularGetter;
import eu.sweetlygeek.parsers.FlickrGetter;
import eu.sweetlygeek.parsers.TumblrGetter;

/** Global bot servlet
 * @author bishiboosh
 *
 */
@SuppressWarnings("serial")
public class BotServlet extends AbstractRobotServlet {
	
	public static final Logger LOGGER = Logger.getLogger(BotServlet.class);

	/* (non-Javadoc)
	 * @see com.google.wave.api.AbstractRobotServlet#processEvents(com.google.wave.api.RobotMessageBundle)
	 */
	@Override
	public void processEvents(RobotMessageBundle bundle) {
		Wavelet wavelet = bundle.getWavelet();
		
		if (bundle.wasSelfAdded())
		{
			Blip blip = wavelet.appendBlip();
			TextView tv = blip.getDocument();
			tv.append("Salut les moches !");
		}
		
		for (Event e : bundle.getBlipSubmittedEvents())
		{
			Blip blip = e.getBlip();
			TextView tv = blip.getDocument();
			String text = tv.getText();
			if (StringUtils.contains(text, DropularGetter.DROPULAR_TAG))
			{
				try {
					DropularGetter.getInstance().analyzeBlip(blip, wavelet);
				} catch (ParserConfigurationException ex) {
					LOGGER.error("Error while analyzing blip", ex);
				} catch (SAXException ex) {
					LOGGER.error("Error while analyzing blip", ex);
				}
			}
			else if (StringUtils.contains(text, FlickrGetter.FLICKR_TAG))
			{
				FlickrGetter.getInstance().analyzeBlip(blip, wavelet);
			}
			else if (StringUtils.contains(text, TumblrGetter.TUMBLR_TAG))
			{
				try {
					TumblrGetter.getInstance().analyzeBlip(blip, wavelet);
				} catch (ParserConfigurationException ex) {
					LOGGER.error("Error while analyzing blip", ex);
				} catch (SAXException ex) {
					LOGGER.error("Error while analyzing blip", ex);
				}
			}
		}
	}

}
