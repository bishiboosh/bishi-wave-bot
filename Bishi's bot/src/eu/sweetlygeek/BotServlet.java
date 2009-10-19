package eu.sweetlygeek;

import org.apache.commons.lang.StringUtils;

import com.google.wave.api.AbstractRobotServlet;
import com.google.wave.api.Blip;
import com.google.wave.api.Event;
import com.google.wave.api.RobotMessageBundle;
import com.google.wave.api.TextView;
import com.google.wave.api.Wavelet;

/** Global bot servlet
 * @author bishiboosh
 *
 */
@SuppressWarnings("serial")
public class BotServlet extends AbstractRobotServlet {

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
			Blip b = e.getBlip();
			TextView tv = b.getDocument();
			String text = tv.getText();
			if (StringUtils.contains(text, "test:bishi"))
			{
				Blip nBlip = wavelet.appendBlip();
				nBlip.getDocument().append("Hello world !");
			}
		}
	}

}
