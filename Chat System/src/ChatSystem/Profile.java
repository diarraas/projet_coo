//package ChatSystem;
import java.io.BufferedReader;
import java.io.*;
import java.io.InputStreamReader;
import java.net.*;
import java.util.*;

class Profile {
    
    private int id ;
    
    private String login ;
    
    private boolean status ;
    
    private Socket client_socket ;
    
    private ServerSocket server_socket ;
        
    private int server_port ;
    
    private InetAddress server_address ;
    
    public static int BROADCAST_PORT = 4445 ;
    
    public TCP_Server listening_server ;
        
    
    public Profile(String log) {
        try {
	    	login = log ;
	        id = 65535 *((int) Math.random()) ;
	        server_port = 1024 + id ;
	        server_address = InetAddress.getLocalHost();
	        listening_server = new TCP_Server(this);	
	        Runnable login_listener = new Runnable() {
	            public void run() {
	            	try {
	            		DatagramSocket socket = new DatagramSocket(BROADCAST_PORT);
		                byte[] buf = new byte[256];
	         	        while (true) {
					    DatagramPacket packet = new DatagramPacket(buf, buf.length);
			 	            socket.receive(packet);
			 	            InetAddress address = packet.getAddress();
			 	            int port = packet.getPort();
			 	            packet = new DatagramPacket(buf, buf.length, address, port);
			 	            String received = new String(packet.getData(), 0, packet.getLength());
			 	            socket.send(packet);
	         	        }
	         	        
	                  } catch ( Exception e) {
	                  	System.out.println("Erreur création du serveur d'écoute connexion");
	                  }
	                 
	            }
	        };
	        /*
	        Runnable listening_server = new Runnable() {
	        	public void run(){
	        		try {
	        			while()
	        		}
	        	}
	        };*/
	        new Thread(login_listener).start();
	        new Thread(listening_server).start();
	        System.out.println("serveur login ok");
        }catch(Exception e) {
        	System.out.println("Erreur creation socket serveur");
        }
    }
    
    public static Profile create_account(){
    	String log = "" ;
    	Profile new_account = null;
    	try {
	       System.out.println("Choisissez un login");
	       BufferedReader reader =
	                   new BufferedReader(new InputStreamReader(System.in));
	         log = reader.readLine(); 
	       /* boolean isUnic = SystemRegister.verify_unicity(log);
	        while(!isUnic){
	            //choose as many times as desired ....
	            System.out.println("Login existant");
	            System.out.println("Entrez un nouveau login");
	            reader = new BufferedReader(new InputStreamReader(System.in));
	            log = reader.readLine() ;
	            isUnic = SystemRegister.verify_unicity(log);
	        }*/
	        
	        new_account = new Profile(log);
	        
    	}catch (Exception e) {
			System.out.println("Erreur creation de compte");
			System.exit(-1);
    	}
    	
    	//if(new_account != null)	SystemRegister.add_user(new_account);
    	return new_account ;
    }
    
    
   /* public void change_login() {
    	try {
	        // test unicity of login ------ 
	        System.out.println("Entrez un nouveau login");
	        BufferedReader reader =
	                   new BufferedReader(new InputStreamReader(System.in));
	        String newLog = reader.readLine(); 
	        boolean isUnic = SystemRegister.verify_unicity(newLog);
	        while(!isUnic){
	            //choose as many times as desired ....
	            System.out.println("Login existant");
	            System.out.println("Entrez un nouveau login");
	            reader = new BufferedReader(new InputStreamReader(System.in));
	            newLog = reader.readLine() ;
	            isUnic = SystemRegister.verify_unicity(newLog);
	        }
	        
	        login = newLog ;
	
	    }catch( Exception e) {
			System.out.println("Erreur changement de login");
	    }
    }*/
    
    public void authentify () {
    	/*try {
	    	if(SystemRegister.users.contains(this)){
		    	System.out.println("Entrez un login pour se connecter");
		        BufferedReader reader =
		                   new BufferedReader(new InputStreamReader(System.in));
		        String log = reader.readLine();
		        
		        while(!this.login.equalsIgnoreCase(log)){
		        	System.out.println("Erreur de connexion");
		            reader =
		                       new BufferedReader(new InputStreamReader(System.in));
		            log = reader.readLine();
		        }
		            status = true ;
		    		SystemRegister.add_online_user(this);
	        } else {
	            System.out.println("Veuillez cr�er un compte");
	        }
    	} catch(Exception e) {
			System.out.println("Erreur de connexion");
    	}
    	*/
    	try {
    		System.out.println("Authentification");
    		DatagramSocket socket = new DatagramSocket(server_port + 10);
	    	InetAddress broadcast = null ;
	    	socket.setBroadcast(true);
    		DatagramPacket packet = null ;
    		Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
		while (en.hasMoreElements()) {
		      NetworkInterface ni = en.nextElement();
		      List<InterfaceAddress> list = ni.getInterfaceAddresses();
		      Iterator<InterfaceAddress> it = list.iterator();
		      while (it.hasNext()) {
				InterfaceAddress ia = it.next();
				broadcast = ia.getBroadcast();
				if(broadcast !=null){
					packet = new DatagramPacket("online".getBytes(), "online".getBytes().length, broadcast, BROADCAST_PORT);
	       				socket.send(packet);
					socket.setBroadcast(false);
					socket.close();
					status = true ;
		      		}
			}
		}    		
	        
    	} catch(Exception e) {
 
    		System.out.println("Erreur connection");
    	
    	}
    	
    }
    
    public void show_users() {
    
    }
    /*
    public String messaging() {
    	String msg = "" ;
        try {
	    	System.out.println("Entrez un message");
	        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
	    	msg = reader.readLine() ;
        } catch(Exception e) {
        	System.out.println("Erreur generation du message");
        }
    	return msg ;
    }*/
    
    public void send_message(String dest){
    		try {
    			Profile rec = SystemRegister.findProfileByLogin(dest);
	        	client_socket = new Socket(rec.getServer_address(),rec.getServer_port(),InetAddress.getLocalHost(),server_port);
	    
            		System.out.println("Entrez un message");
	    		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
	    		String msg = reader.readLine() ;
	    	
	    		PrintWriter out = new PrintWriter(client_socket.getOutputStream(),true);
	    		out.println(msg);
	    		
    		}catch (Exception e) {
			System.out.println("Erreur envoi de message");
    	}
    }
    
   /* public void receive_message() {
    	try {	    
	    	BufferedReader reader = new BufferedReader(new InputStreamReader(client_socket.getInputStream()));
	    	String msg = reader.readLine() ;
	    	System.out.println(msg);
    	}catch (Exception e) {
			System.out.println("Erreur reception de message");
    	}
    }*/
    
    
    /*public void end_session() {
    	try {
    		server_socket.close();
    		status = false ;
    	}catch(Exception e) {
    		System.out.println("Erreur fermeture de connexion");
    	}
    }*/
    
    public int getId(){
        return id ;
    }
    
    
    public String getLogin(){
        return login ;
    }
    
    public boolean getStatus(){
        return status ;
    }
    
    public void setLogin(String login) {
		this.login = login;
	}


	public void setStatus(boolean status) {
		this.status = status;
	}


	public int getServer_port() {
		return server_port;
	}


	public void setServer_port(int server_port) {
		this.server_port = server_port;
	}


	public InetAddress getServer_address() {
		return server_address;
	}


	public void setServer_address(InetAddress server_address) {
		this.server_address = server_address;
	}

	public ServerSocket getServer_socket() {
		return server_socket;
	}
    
}

class WorkerRunnable implements Runnable {

    protected Socket clientSocket = null;
    protected String serverText   = null;

    public WorkerRunnable(Socket clientSocket, String serverText) {
        this.clientSocket = clientSocket;
        this.serverText   = serverText;
    }

    public void run() {
        try {
            InputStream input  = clientSocket.getInputStream();
            OutputStream output = clientSocket.getOutputStream();
            long time = System.currentTimeMillis();
            output.write((this.serverText).getBytes());
            output.close();
            System.out.println("New Message");
            System.out.println(new BufferedReader(new InputStreamReader(input)).readLine());
            input.close();
            System.out.println("Request processed: " + time);
        } catch (IOException e) {
            //report exception somewhere.
            e.printStackTrace();
        }
    }
}


class TCP_Server implements Runnable{

    protected int serverPort = 0;
    protected ServerSocket serverSocket = null;
    protected boolean isStopped = false;
    protected Thread runningThread = null;
    protected Profile current_user ;
    
    public TCP_Server (Profile who){
    	current_user = who ;
        this.serverPort = current_user.getServer_port();
    }
   
    public void run(){
        synchronized(this){
            this.runningThread = Thread.currentThread();
        }
        openServerSocket();
        while(! isStopped()){
            Socket clientSocket = null;
            try {
                clientSocket = this.serverSocket.accept();
            } catch (IOException e) {
                if(isStopped()) {
                    System.out.println("Server Stopped.") ;
                    return;
                }
                throw new RuntimeException(
                    "Error accepting client connection", e);
            }
            new Thread(new WorkerRunnable(clientSocket, "Message transmis")).start();
        }
        System.out.println("Server Stopped.") ;
    }

    private synchronized boolean isStopped() {
        return this.isStopped;
    }

    public synchronized void stop(){
        this.isStopped = true;
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException("Error closing server", e);
        }
    }

    private void openServerSocket() {
         this.serverSocket = current_user.getServer_socket();
        
    }

}
