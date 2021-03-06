package HawkClient;

/*
 * This class implements the object HawkCredentials, that includes the user identifier 
 * and the shared key between the server and the iOS client.
 * 
 * @see		https://github.com/hueniverse/hawk
 * 
 * @author 	Ricardo Sousa
 * */
public class HawkCredentials {
	
	private String _identifier;
	private String _key;
	
	/*
	 * Public constructor.
	 * 
	 * @param identifier	the user identifier.
	 * @param key			the shared key.
	 * @return				new instance of HawkCredentials.
	 * */
	public HawkCredentials(String identifier, String key) {
		_identifier = identifier;
		_key = key;
	}
	
	/*
	 * Returns the user identifier.
	 * 
	 * @return		User identifier.
	 * */
	public String get_identifier() {
		return _identifier;
	}
	
	/*
	 * Returns the shared key between server and client.
	 * 
	 * @return		Shared key.
	 * */
	public String get_key() {
		return _key;
	}
}