package put.sailhero.android;


public class ResponseCreator {
	static public <T extends ProcessedResponse> T createFrom(HttpResponse response, Class<T> cls) throws Exception {
		T processedResponse = cls.newInstance();
		
		processedResponse.createFrom(response);
		
		return processedResponse;
	}
	
}
