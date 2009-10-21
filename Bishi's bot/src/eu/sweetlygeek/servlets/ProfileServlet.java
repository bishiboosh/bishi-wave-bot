package eu.sweetlygeek.servlets;

/** Profile servlet
 * @author bishiboosh
 *
 */
@SuppressWarnings("serial")
public class ProfileServlet extends com.google.wave.api.ProfileServlet {

	@Override
	public String getRobotAvatarUrl() {
		return "http://bishibot.appspot.com/boobs.png";
	}

	@Override
	public String getRobotName() {
		return "Bishi's bot";
	}

	@Override
	public String getRobotProfilePageUrl() {
		return "http://code.google.com/p/bishi-wave-bot/";
	}

}
