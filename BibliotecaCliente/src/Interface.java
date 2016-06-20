import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class Interface extends JFrame implements ActionListener{
	public class answerGetter extends Thread{
		public void run(){
			String answer = new String("");
			try{
				while(server.hasNextLine() && !endTest){
					lastLine = server.nextLine();
					System.out.println(lastLine);
					if(lastLine.equals("disconnect")){
						disconnect();
						return;
					}
					//System.out.println(lastLine);
					commandReceived = true;
					commandProcessed = false;
//					while(!commandProcessed){
//					}
				}
			}catch(Exception e){
			}
		}
	}
	private Socket client;
	public volatile boolean endTest;
	private PrintStream saida;
	private Scanner entrada;
	private Scanner server;
	public volatile String lastLine;
	private answerGetter read;
	private volatile boolean commandReceived;
	private volatile boolean commandProcessed;

	private JFrame frame;
	private JButton but;
	
	private Container pane;
	private JPanel botao;
	
	private JTextField output;

	private JPanel userPanel;
	private JPanel senhaPanel;
	private JPanel userPass;
	private JPanel welcome;
	
	private JTextField usuario2;
	private JTextField usuario;
	
	private JTextField senha;
	private JTextField senha2;

	// Funcoes de comunicacao
	// Envia uma string para o servidor
	public void sendCommand(String s){
		saida.println(s);
	}
	
	public void getAnswers(){
		read = new answerGetter();
		read.start();
	}

	public void disconnect() throws IOException{
		saida.close();
		entrada.close();
		server.close();
		client.close();
	}

	// Construtor
	public Interface() throws Exception{
		// Comunicacao
		endTest = false;
		//client = new Socket("192.168.182.91", 9669);
		client = new Socket("127.0.0.1", 9669);
		entrada = new Scanner(System.in);
		saida = new PrintStream(client.getOutputStream());
		server = new Scanner(client.getInputStream());
		super("Teste");
		
		// Interface
		this.setVisible(true);
		setResizable(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setPreferredSize(new Dimension(20, 20));
		
		pane = this.getContentPane();
		pane.setLayout(new BorderLayout(30, 30));
		pane.setPreferredSize(new Dimension(200, 200));
		
		
		pane.add(getWelcomeLayout(), BorderLayout.NORTH);
		
		pane.add(getUserPassLayout(), BorderLayout.CENTER);
		pane.add(this.getButtonPanel(), BorderLayout.SOUTH);
		
		//JScrollPane scroll = new JScrollPane(output);
		//scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		//scroll.setPreferredSize(new Dimension(600, 400));
		
		//input = new JTextField(25);
		//input.addActionListener(this);
		//pane.add(input, BorderLayout.CENTER);
		pack();
	}
	
	// Funcoes de interface
	private JComponent getWelcomeLayout(){
		output = new JTextField("Digite Usuario e Senha");
		output.setEditable(false);
		
		welcome = new JPanel();
		welcome.setLayout(new FlowLayout(FlowLayout.CENTER));
		
		welcome.add(output);
		
		return welcome;
	}
	
	private JComponent getUserPassLayout(){
		userPass = new JPanel();
		userPass.setLayout(new GridLayout(1,2));
		userPass.setPreferredSize(new Dimension(80, 20));
		
		userPass.add(getUsuarioLayout());
		userPass.add(getSenhaLayout());
		
		return userPass;
	}
	
	private JComponent getUsuarioLayout(){
		usuario2 = new JTextField("Usuario:");
		usuario2.setEditable(false);
		usuario2.setPreferredSize(new Dimension(20, 10));
		
		usuario = new JTextField();
		usuario.setEditable(true);
		usuario.setPreferredSize(new Dimension(20, 10));
		
		userPanel = new JPanel();
		userPanel.setLayout(new GridLayout(1,2));
		
		userPanel.add(usuario2);
		userPanel.add(usuario);
		
		return userPanel;
	}
	
	private JComponent getSenhaLayout(){
		senha2 = new JTextField("Senha:");
		senha2.setEditable(false);
		senha2.setPreferredSize(new Dimension(20, 10));
		
		senha = new JTextField();
		senha.setEditable(true);
		senha.setPreferredSize(new Dimension(20,10));
		
		senhaPanel = new JPanel();
		senhaPanel.setLayout(new GridLayout(1,2));
		
		senhaPanel.add(senha2);
		senhaPanel.add(senha);
		
		return senhaPanel;
	}
	
	protected JComponent getButtonPanel(){
		botao = new JPanel();
		botao.setLayout(new GridLayout(1,1));
		but = new JButton("LOGIN");
		botao.add(but);
		but.addActionListener(this);
		return botao;
	}
	
	public static void main(String[] args) throws Exception{
		Interface oi = new Interface();
	}
	/*
	public static void main(String[] args) throws Exception{
		TestClient tc = new TestClient();
		tc.getAnswers();
		String testCommand = "login";
		tc.sendCommand(testCommand);
		testCommand = "12345";
		tc.sendCommand(testCommand);
		testCommand = "senha";
		tc.sendCommand(testCommand);
		testCommand = "open";
		tc.sendCommand(testCommand);
		testCommand = "teste";
		tc.sendCommand(testCommand);
		testCommand = "disconnect";
		tc.sendCommand(testCommand);
//		String input = EntradaTeclado.leString();
//		while(!input.equals("sair")){
//			tc.sendCommand(input);
//		}
		while(tc.endTest){
		}
//		tc.disconnect();
	}
	*/
	

	@Override
	public void actionPerformed(ActionEvent e) {
		pane.remove(welcome);
		pane.remove(userPass);
		pane.remove(botao);
		pane.setVisible(false);
		pane.add(botao);
		botao.setPreferredSize(new Dimension(400, 400));
		pack();
		pane.setVisible(true);
	}

}
