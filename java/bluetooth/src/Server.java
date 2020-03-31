import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;  
import javax.microedition.io.StreamConnection;  
import javax.microedition.io.StreamConnectionNotifier;  
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Date; 


public class Server{
	  
    public static void main(String[] args){ 
    	
    	
		log("Local Bluetooth device...\n");
        
    	LocalDevice local = null;
		try {
			
			local = LocalDevice.getLocalDevice();
		} catch (BluetoothStateException e2) {
			
		}   
		
    	log( "address: " + local.getBluetoothAddress() );
    	log( "name: " + local.getFriendlyName() );
    	
    	
    	Runnable r = new ServerRunable();
    	Thread thread = new Thread(r);
    	thread.start();
    	
    }
    
      
    private static void log(String msg) {  
    	
        System.out.println("["+(new Date()) + "] " + msg);  
    }

}

class Mecro {
	private Robot r;
	private int[] key = { KeyEvent.VK_Z, KeyEvent.VK_E, KeyEvent.VK_C, KeyEvent.VK_U, KeyEvent.VK_COMMA,
			KeyEvent.VK_O, KeyEvent.VK_SLASH, };
	private boolean[] memory = {false, false, false, false, false, false, false};
	private byte[] mask = {0x01, 0x02, 0x04, 0x08, 0x10, 0x20, 0x40};
	private byte predata = 0;
	private long pretime = 0;
	private long newtime = 0;
	
	public Mecro() {
		try {
			r = new Robot();
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}

	public void dataCheck(byte data) {		
		newtime = System.currentTimeMillis();
		if (newtime - pretime > 40) {
			if (newtime - pretime > 100) System.out.print("Over 100!!======== ");
			else System.out.print("Over 40!! -- ");
			System.out.println(newtime - pretime);
		}
		if (data != predata) for (int i = 0; i < 7; i++) keyMange(i, data);
		predata = data;
		pretime = newtime;
	}
	
	public void keyMange(int index, byte data) {
		boolean bool = (data & mask[index]) != 0;
		if (memory[index] != bool) {
			if (bool) Press(key[index]);
			else Release(key[index]);
			memory[index] = bool;
		}
	}
	
	private void Press(int _key) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				r.keyPress(_key);
			}
		}).start();
	}
	
	private void Release(int _key) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				r.keyRelease(_key);
			}
		}).start();
	}
}

class ServerRunable implements Runnable{
	  
	//UUID for SPP
	final UUID uuid = new UUID("0000808900001000800000805F9B34FB", false);
    final String CONNECTION_URL_FOR_SPP = "btspp://localhost:"
    			+ uuid +";name=SPP Server";
  
    private StreamConnectionNotifier mStreamConnectionNotifier = null;  
    private StreamConnection mStreamConnection = null; 
    private int count = 0;
    
	@Override
	public void run() {

    	try {
    		
			mStreamConnectionNotifier = (StreamConnectionNotifier) Connector
						.open(CONNECTION_URL_FOR_SPP);
			
			log("Opened connection successful.");
		} catch (IOException e) {
			
			log("Could not open connection: " + e.getMessage());
			return;
		}
   

    	log("Server is now running.");

    	
    	
        while(true){
        	
        	log("wait for client requests...");

			try {
				
				mStreamConnection = mStreamConnectionNotifier.acceptAndOpen();
			} catch (IOException e1) {
				
				log("Could not open connection: " + e1.getMessage() );
			}
			
        	
			count++;
			log("현재 접속 중인 클라이언트 수: " + count);
			
								
	        new Receiver(mStreamConnection).start();
        }
		
	}
	
        
    
    class Receiver extends Thread {
    	
    	private InputStream mInputStream = null; 
        private OutputStream mOutputStream = null; 
        private String mRemoteDeviceString = null;
        private StreamConnection mStreamConnection = null;
        private Mecro m = new Mecro();;
        
        Receiver(StreamConnection streamConnection){
        	
        	mStreamConnection = streamConnection;

			try {
			    	
				mInputStream = mStreamConnection.openInputStream();
				mOutputStream = mStreamConnection.openOutputStream();
									
				log("Open streams...");
			} catch (IOException e) {
				
				log("Couldn't open Stream: " + e.getMessage());

				Thread.currentThread().interrupt();		
				return;
			}
			
			
			try {
		        	
					RemoteDevice remoteDevice 
						= RemoteDevice.getRemoteDevice(mStreamConnection);
					
			        mRemoteDeviceString = remoteDevice.getBluetoothAddress();
			        
					log("Remote device");
					log("address: "+ mRemoteDeviceString);
			        
				} catch (IOException e1) {
					
					log("Found device, but couldn't connect to it: " + e1.getMessage());
					return;
			}
			
			log("Client is connected...");
        }
        
        
    	@Override
		public void run() {
    		
			try {
				
	    		//Reader mReader = new BufferedReader(new InputStreamReader ( mInputStream, Charset.forName(StandardCharsets.UTF_8.name())));
	    		Reader mReader = new BufferedReader(new InputStreamReader ( mInputStream));
				    		
				//Sender("에코 서버에 접속하셨습니다.");
				//Sender( "보내신 문자를 에코해드립니다.");
	    		
				while(true){
			        
		            //StringBuilder stringBuilder = new StringBuilder();		            
		            /*
					while ( '\n' != (char)( c = mReader.read()) ) {
						
						if ( c == -1){
							
							log("Client has been disconnected");
							
							count--;
							log("현재 접속 중인 클라이언트 수: " + count);
							
							isDisconnected = true;
							Thread.currentThread().interrupt();
							
							break;
						}
						
						stringBuilder.append((char) c);
					}
		            */
		            //String recvMessage = stringBuilder.toString();
		            //Sender(recvMessage);
					byte c = (byte)mReader.read();
					if(c == -1) break;
					m.dataCheck(c);
				}
				
				log("Receiver closed in -1");
				
			} catch (IOException e) {
				
				log("Receiver closed" + e.getMessage());
			}
		}
    	

    	void Sender(String msg){
        	
            PrintWriter printWriter = new PrintWriter(new BufferedWriter
            		(new OutputStreamWriter(mOutputStream, 
            				Charset.forName(StandardCharsets.UTF_8.name()))));
        	
    		printWriter.write(msg+"\n");
    		printWriter.flush();
    		
    		log( "Me : " + msg );
    	}
	}
    
    
    private static void log(String msg) {  
    	
        System.out.println("["+(new Date()) + "] " + msg);  
    }
        
}  