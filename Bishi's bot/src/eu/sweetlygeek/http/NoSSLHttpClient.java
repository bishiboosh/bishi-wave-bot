package eu.sweetlygeek.http;

import org.apache.http.client.params.ClientPNames;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ClientConnectionManagerFactory;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.params.HttpParams;

public class NoSSLHttpClient extends DefaultHttpClient {

	@Override
	protected ClientConnectionManager createClientConnectionManager() {
		SchemeRegistry registry = new SchemeRegistry();
		registry.register(
				new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		
		ClientConnectionManager connManager = null;     
		HttpParams params = getParams();

		ClientConnectionManagerFactory factory = null;

		// Try first getting the factory directly as an object.
		factory = (ClientConnectionManagerFactory) params
		.getParameter(ClientPNames.CONNECTION_MANAGER_FACTORY);
		if (factory == null) { // then try getting its class name.
			String className = (String) params.getParameter(
					ClientPNames.CONNECTION_MANAGER_FACTORY_CLASS_NAME);
			if (className != null) {
				try {
					Class<?> clazz = Class.forName(className);
					factory = (ClientConnectionManagerFactory) clazz.newInstance();
				} catch (ClassNotFoundException ex) {
					throw new IllegalStateException("Invalid class name: " + className);
				} catch (IllegalAccessException ex) {
					throw new IllegalAccessError(ex.getMessage());
				} catch (InstantiationException ex) {
					throw new InstantiationError(ex.getMessage());
				}
			}
		}

		if(factory != null) {
			connManager = factory.newInstance(params, registry);
		} else {
			connManager = new SingleClientConnManager(getParams(), registry); 
		}

		return connManager;
	}
}
