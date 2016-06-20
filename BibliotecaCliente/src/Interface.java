import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;


public class Interface extends JFrame implements ActionListener{

	private JButton but;
	private JButton but2;
	
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
	private JTextField senha3;


	// ----------------------------Construtor--------------------------------
	public Interface() throws Exception{
		super("Teste");
		// Comunicacao
		endTest = false;
		//client = new Socket("192.168.182.91", 9669);
		//client = new Socket("127.0.0.1", 9669);
		//entrada = new Scanner(System.in);
		//saida = new PrintStream(client.getOutputStream());
		//server = new Scanner(client.getInputStream());
		
		
		// Interface
		this.setVisible(true);
		setResizable(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		pane = this.getContentPane();
		pane.setLayout(new BorderLayout(30, 30));
		
		
		pane.add(getWelcomeLayout(), BorderLayout.NORTH);
		
		pane.add(getUserPassLayout(), BorderLayout.CENTER);
		pane.add(this.getButtonPanel(), BorderLayout.SOUTH);
		
		//JScrollPane scroll = new JScrollPane(output);
		//scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		//scroll.setPreferredSize(new Dimension(600, 400));
		
		pack();
	}
	
	// Funcoes de interface
	//-----------------------------------------------------------------------
	//---------------------WelcomeLayout-----------------------------
	//-----------------------------------------------------------------------
	private JComponent getWelcomeLayout(){
		output = new JTextField("Digite Usuario e Senha");
		output.setEditable(false);
		
		welcome = new JPanel();
		welcome.setLayout(new FlowLayout(FlowLayout.CENTER));
		
		welcome.add(output);
		
		return welcome;
	}
	
	private JComponent getWelcome2Layout(){
		output = new JTextField("Crie Usuario e Senha");
		output.setEditable(false);
		
		welcome = new JPanel();
		welcome.setLayout(new FlowLayout(FlowLayout.CENTER));
		
		welcome.add(output);
		
		return welcome;
	}
	
	private JComponent getWelcome3Layout(){
		output = new JTextField("Usuario ou Senha Incompativel");
		output.setEditable(false);
		
		welcome = new JPanel();
		welcome.setLayout(new FlowLayout(FlowLayout.CENTER));
		
		welcome.add(output);
		
		return welcome;
	}
//-----------------------------------------------------------------------
//---------------------UserPassLayout-----------------------------
//-----------------------------------------------------------------------
	private JComponent getUserPassLayout(){
		userPass = new JPanel();
		userPass.setLayout(new GridLayout(1,2));
		userPass.setPreferredSize(new Dimension(200, 20));
		
		userPass.add(getUsuarioLayout());
		userPass.add(getSenhaLayout());
		
		return userPass;
	}
	
	private JComponent getUserPassLayout2(){
		userPass = new JPanel();
		userPass.setLayout(new GridLayout(1,3));
		//userPass.setPreferredSize(new Dimension(200, 20));
		
		userPass.add(getUsuarioLayout());
		userPass.add(getSenhaLayout());
		userPass.add(getSenhaLayout2());
		
		return userPass;
	}
	//-----------------------------------------------------------------------
	//---------------------UsuarioLayout-----------------------------
	//-----------------------------------------------------------------------
	private JComponent getUsuarioLayout(){
		usuario2 = new JTextField("Usuario:");
		usuario2.setEditable(false);
		usuario2.setPreferredSize(new Dimension(30, 10));
		
		usuario = new JTextField();
		usuario.setEditable(true);
		usuario.setPreferredSize(new Dimension(20, 10));
		
		userPanel = new JPanel();
		userPanel.setLayout(new GridLayout(1,2));
		
		userPanel.add(usuario2);
		userPanel.add(usuario);
		
		return userPanel;
	}
	//-----------------------------------------------------------------------
	//---------------------SenhasLayout-----------------------------
	//-----------------------------------------------------------------------
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
	
	private JComponent getSenhaLayout2(){
		senha2 = new JTextField("Confirma Senha:");
		senha2.setEditable(false);
		senha2.setPreferredSize(new Dimension(60, 30));
		
		senha3 = new JTextField();
		senha3.setEditable(true);
		senha3.setPreferredSize(new Dimension(100,30));
		
		senhaPanel = new JPanel();
		senhaPanel.setLayout(new GridLayout(1,2));
		
		senhaPanel.add(senha2);
		senhaPanel.add(senha3);
		
		return senhaPanel;
	}
	//-----------------------------------------------------------------------
	//---------------------ButtonPanel-----------------------------
	//-----------------------------------------------------------------------
	protected JComponent getButtonPanel(){
		botao = new JPanel();
		botao.setLayout(new GridLayout(1,2));
		
		but = new JButton("Create Account");

		
		but2 = new JButton(new AbstractAction("Login"){
			@Override
			public void actionPerformed(ActionEvent e){
				//metodo
			}
		});
		
		botao.add(but2);
		botao.add(but);
		but.addActionListener(this);
		return botao;
	}
	
	protected JComponent getButton2Panel(){
		botao = new JPanel();
		botao.setLayout(new GridLayout(1,2));
		
		but = new JButton(new AbstractAction("Cancel"){
			@Override
			public void actionPerformed(ActionEvent e){
				pane.setVisible(false);
				pane.remove(welcome);
				pane.remove(userPass);
				pane.remove(botao);
				pane.add(getWelcomeLayout(), BorderLayout.NORTH);
				pane.add(getUserPassLayout(), BorderLayout.CENTER);
				pane.add(getButtonPanel(), BorderLayout.SOUTH);
				pack();
				pane.setVisible(true);
			}
		});
		
		but2 = new JButton(new AbstractAction("Create"){
			@Override
			public void actionPerformed(ActionEvent e){
				if(senha.getText().equals(senha3.getText())==true && 
						senha.getText().isEmpty()==false && usuario.getText().isEmpty()==false){
					
					
					//create and enter
				}
				else{
					pane.setVisible(false);
					pane.remove(welcome);
					pane.add(getWelcome3Layout(), BorderLayout.NORTH);
					pack();
					pane.setVisible(true);
				}
			}
		});
		
		botao.add(but);
		botao.add(but2);

		
		return botao;
	}
	
	public static void main(String[] args) throws Exception{
		Interface oi = new Interface();
	}

	

	@Override
	public void actionPerformed(ActionEvent e) {
		pane.setVisible(false);
		pane.remove(welcome);
		pane.remove(userPass);
		pane.remove(botao);
		pane.add(getWelcome2Layout(), BorderLayout.NORTH);
		pane.add(getUserPassLayout2(), BorderLayout.CENTER);
		pane.add(getButton2Panel(), BorderLayout.SOUTH);
		//botao.setPreferredSize(new Dimension(50, 20));
		pack();
		pane.setVisible(true);
	}
	//------------------------------------------------------------------------
	//-----------------------------------------------------------------------
	//---------------------Threads-----------------------------
	//-----------------------------------------------------------------------
	//------------------------------------------------------------------------
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
	
}
