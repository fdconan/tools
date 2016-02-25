import jm.tools.configuration.Configuration;
import jm.tools.configuration.ConfigurationHelper;


public class ConfigTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		/*
		Configuration config = ConfigurationHelper.getConfigurationInstance("config.properties");
		String ip = config.getString("ip");
		int port = config.getInt("port");
		System.out.println(ip + ":" + port);
		*/
		Configuration config = ConfigurationHelper.getConfigurationInstance("config.xml");
		//System.out.println(config.getString("//menu[id=tools_menu]"));
		System.out.println(config.getString("//menu/@id"));
		System.out.println(config.getString("//menu[@id='tools_menu']"));
		//System.out.println(config.getList("//menu"));
	}

}
