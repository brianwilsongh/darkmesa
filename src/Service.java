import java.util.HashMap;
import java.util.Map;

public class Service {
	Map<String, Integer> serviceMap = new HashMap<>(); //name of service, designated port number
	
	public void Service(){
		serviceMap.put("ndspider", 3421);
		
	}
	
	private void startService(String serviceName){
		//TODO: start the service here with the service name
		
		Thread tryClient = new Thread(new Runnable(){
			@Override
			public void run() {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Client client = new Client();
				client.run();
				System.out.println("Client made");
			}
		});
		tryClient.start();
	}
}
