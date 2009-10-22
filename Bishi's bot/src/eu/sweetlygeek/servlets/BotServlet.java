package eu.sweetlygeek.servlets;

import org.apache.log4j.Logger;

import com.google.wave.api.AbstractRobotServlet;
import com.google.wave.api.Blip;
import com.google.wave.api.Event;
import com.google.wave.api.RobotMessageBundle;
import com.google.wave.api.TextView;
import com.google.wave.api.Wavelet;

import eu.sweetlygeek.parsers.Parser;

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
			Parser[] parsers = Parser.values();
			for (int i = 0; i < parsers.length; i++)
			{
				parsers[i].analyzeBlip(blip, wavelet);
			}
		}
	}

}
