package external;

public class ExternalAPIFactory {
	private static final String TICKETMASTER_API = "TICKETMASTER";
	
	/**
	 * Get ExternalAPI objects basing on the given value;
	 * */
	public static ExternalAPI getExternalAPI(String APIName){
		ExternalAPI api = null;
		switch (APIName) {
		case TICKETMASTER_API:
			api = new TicketMasterAPI();
			break;

		default:
			 throw new IllegalArgumentException("Invalid pipeline " + APIName);
		}
		return api;
	}
}
