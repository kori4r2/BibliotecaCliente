import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;


public class Interface extends JFrame implements ActionListener{
	public int quantEmprestimos;
	
	private JButton[] emprestimoBut;
	private JButton[] uploadBut;
	private JButton but;
	private JButton but2;
	private JButton emp;
	private JButton upl;
	
	private Container pane;
	private JPanel botao;
	
	private JTextField output;

	private JPanel userPanel;
	private JPanel senhaPanel;
	private JPanel userPass;
	private JPanel welcome;
	private JPanel usrScrn;
	private JPanel emprestimos;
	private JPanel emprestimosScreen;
	private JPanel uploads;
	private JPanel uploadsScreen;
	
	private JTextField usuario2;
	private JTextField usuario;
	
	private JPasswordField senha;
	private JTextField senha2;
	private JPasswordField senha3;
	
	private JScrollPane scroll;


	// ----------------------------Construtor--------------------------------
	public Interface() throws Exception{
		super("Teste");
		// Comunicacao
		/*
		//socket = new Socket("192.168.182.91", 9669);
		socket = new Socket("127.0.0.1", 9669);
		saida = new PrintStream(socket.getOutputStream());
		entrada = new Scanner(socket.getInputStream());
		commandReceived = false;
		*/
		
		// Interface
		quantEmprestimos = 0;
		this.setVisible(true);
		setResizable(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		pane = this.getContentPane();
		pane.setLayout(new BorderLayout(30, 30));
		
		
		pane.add(getWelcomeLayout("Digite Usuario e Senha"), BorderLayout.NORTH);
		
		pane.add(getUserPassLayout(), BorderLayout.CENTER);
		pane.add(this.getButtonPanel(), BorderLayout.SOUTH);
		
		
		
		pack();
	}
	
	// Funcoes de interface
	//-----------------------------------------------------------------------
	//---------------------WelcomeLayout-----------------------------
	//-----------------------------------------------------------------------
	private JComponent getWelcomeLayout(String msg){
		output = new JTextField(msg);
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
		
		senha = new JPasswordField();
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
		
		senha3 = new JPasswordField();
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
		
		but = new JButton("Criar Conta");

		
		but2 = new JButton(new AbstractAction("Login"){
			@Override
			public void actionPerformed(ActionEvent e){
				if(usuario.getText().isEmpty() == false && senha.getPassword()
						!=null
						/* && usuario e senha sao compativeis*/){
					senha.resetKeyboardActions();
					pane.setVisible(false);
					pane.remove(welcome);
					pane.remove(userPass);
					pane.remove(botao);
					userScreen(usuario.getText());
				}else{
					pane.remove(welcome);
					pane.setVisible(false);
					pane.setVisible(true);
					pane.add(getWelcomeLayout("Usuario ou Senha Incorretos"));
				}
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
				pane.add(getWelcomeLayout("Digite Usuario e Senha"), BorderLayout.NORTH);
				pane.add(getUserPassLayout(), BorderLayout.CENTER);
				pane.add(getButtonPanel(), BorderLayout.SOUTH);
				pack();
				pane.setVisible(true);
			}
		});
		
		
		but2 = new JButton(new AbstractAction("Create"){
			@Override
			public void actionPerformed(ActionEvent e){
				if(Arrays.equals(senha.getPassword(),senha3.getPassword())==true &&
						senha.getPassword()!=null && usuario.getText().isEmpty()==false){
					//if(senha corresponde)create and enter
						senha3.resetKeyboardActions();
						pane.setVisible(false);
						userScreen(usuario.getText());
					//else ...
					
				}
				else{
					pane.setVisible(false);
					pane.remove(welcome);
					pane.add(getWelcomeLayout("Usuario ou Senha Incompativel"), BorderLayout.NORTH);
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
		pane.add(getWelcomeLayout("Crie Usuario e Senha"), BorderLayout.NORTH);
		pane.add(getUserPassLayout2(), BorderLayout.CENTER);
		pane.add(getButton2Panel(), BorderLayout.SOUTH);
		//botao.setPreferredSize(new Dimension(50, 20));
		pack();
		pane.setVisible(true);
	}
	//-----------------------------------------------------------------
	//--------------------------------Tela de Uploads-----------
	//----------------------------------------------------------------
	private void uploadsScreen(){
		pane.add(getWelcomeLayout("Lista de Uploads"), BorderLayout.NORTH);
			
		pane.add(uploadsScreenLayout(), BorderLayout.SOUTH);
		pane.setVisible(true);
		pack();
	}
		
	private JComponent uploadsScreenLayout(){
		pane.removeAll();
		pane.setVisible(false);
		uploadsScreen = new JPanel();
		uploadsScreen.setLayout(new FlowLayout());
			
		uploads = new JPanel();
		uploads.setLayout(new GridLayout(20, 1));

		uploadBut = new JButton[20];
		for(int i=0; i<20; i+=1){
			uploadBut[i] = new JButton(new AbstractAction("teste " + i){
				@Override
				public void actionPerformed(ActionEvent e){
					pane.setVisible(false);
				}
			});
			uploadBut[i].setBounds(20, 5, 89, 23);
			uploads.add(uploadBut[i]);
		}
			
		uploadsScreen.add(uploads);
			
		scroll = new JScrollPane(uploads,
		        JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, 
		        JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setPreferredSize(new Dimension(1000, 200));
		scroll.setViewportView(uploads);
			
		add(scroll);
		return uploadsScreen;
	}

	//-----------------------------------------------------------------
	//--------------------------------Tela de Emprestimos-----------
	//----------------------------------------------------------------
	private void emprestimosScreen(){
		pane.setVisible(false);
		pane.removeAll();
		pane.add(getWelcomeLayout("Lista de Emprestimos"), BorderLayout.NORTH);
		
		pane.add(emprestimosScreenLayout(), BorderLayout.SOUTH);
		pane.setVisible(true);
		pack();
	}
	
	private JComponent emprestimosScreenLayout(){		
		emprestimosScreen = new JPanel();
		emprestimosScreen.setLayout(new FlowLayout());
		
		emprestimos = new JPanel();
		emprestimos.setLayout(new GridLayout(20, 1));

		emprestimoBut = new JButton[20];
		for(int i=0; i<20; i+=1){
			emprestimoBut[i] = new JButton(new AbstractAction("teste " + i){
				@Override
				public void actionPerformed(ActionEvent e){
					pane.setVisible(false);
				}
			});
			emprestimoBut[i].setBounds(20, 5, 89, 23);
			emprestimos.add(emprestimoBut[i]);
		}
		
		emprestimosScreen.add(emprestimos);
		
		scroll = new JScrollPane(emprestimos,
		        JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, 
		        JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setPreferredSize(new Dimension(1000, 200));
		scroll.setViewportView(emprestimos);
		
		add(scroll);
		return emprestimosScreen;
	}
	//------------------------------------------------------------------
	//--------------------------------Tela do Usuario-----------------
	//------------------------------------------------------------------
	
	private JComponent userScreenLayout(){
		usrScrn = new JPanel();
		usrScrn.setLayout(new GridLayout(1, 2));
		
		emp = new JButton(new AbstractAction("Emprestimos"){
			@Override
			public void actionPerformed(ActionEvent e){
				pane.setVisible(false);
				pane.remove(welcome);
				pane.remove(userPass);
				pane.remove(botao);
				pane.remove(upl);
				pane.remove(emp);
				
				emprestimosScreen();
			}
		});
		
		upl = new JButton(new AbstractAction("Uploads"){
			@Override
			public void actionPerformed(ActionEvent e){
				pane.setVisible(false);
				pane.remove(welcome);
				pane.remove(userPass);
				pane.remove(botao);
				pane.remove(upl);
				pane.remove(emp);
				
				uploadsScreen();
			}
		});
		
		usrScrn.add(upl);
		usrScrn.add(emp);
		return usrScrn;
	}
	
	private void userScreen(String ID){
		pane.removeAll();
		pane.add(getWelcomeLayout("ID: " + ID), BorderLayout.NORTH);
		
		pane.add(userScreenLayout(), BorderLayout.SOUTH);
		pane.setVisible(true);
		pack();
	}
	
	//------------------------------------------------------------------------
	//-----------------------------------------------------------------------
	//---------------------Threads-----------------------------
	//-----------------------------------------------------------------------
	//------------------------------------------------------------------------

	private class answerGetter extends Thread{
		public void run(){
			String answer = new String("");
			try{
				while(entrada.hasNextLine()){
					lastLine = entrada.nextLine();
					System.out.println(lastLine);
					if(lastLine.equals("disconnect")){
						disconnect();
						return;
					}
					//System.out.println(lastLine);
					commandReceived = true;
					commandProcessed = false;
					while(!commandProcessed){
					}
				}
			}catch(Exception e){
			}
		}
	}


	private Socket socket;
	private PrintStream saida;
	private Scanner entrada;
	private volatile String lastLine;
	private answerGetter read;
	private volatile boolean commandReceived;
	private volatile boolean commandProcessed;

	// Envia uma string para o servidor
	private void sendCommand(String s){
		saida.println(s);
	}

	private void waitForResponse(){
		while(!commandReceived){
		}
	}

	private void responseProcessed(){
		commandProcessed = true;
		commandReceived = false;
	}

	private void sendFile(String filepath) throws Exception{
		File file = null;
		Path pdfpath = null;
		try{
			file = new File(filepath);
			pdfpath = Paths.get(filepath);
		}catch(Exception e){
			System.out.println("Nome de arquivo invalido (" + filepath + ")");
			return;
		}
		saida.println("upload");
		String[] aux = filepath.split("/");
		saida.println(aux[aux.length - 1]);

		while(!commandReceived){
		}
		commandReceived = false;
		commandProcessed = true;

		if(lastLine.equals("upload")){
			OutputStream outStream = (OutputStream)saida;

			if(file.length() > Integer.MAX_VALUE)
				System.out.println("file is too big");
			int fileSize = (int)file.length();
//			System.out.println("File size in bytes = " + fileSize);
			byte[] size = ByteBuffer.allocate(4).putInt(fileSize).array();

			byte[] byteArray = Files.readAllBytes(pdfpath);
//			System.out.println("array size = " + byteArray.length);

			outStream.write(size);
			outStream.write(byteArray, 0, fileSize);
			outStream.flush();
			saida.println("uploaded");
		}
	}


	private BufferedImage getImage() throws Exception{
		System.out.println("Receiving image...");
		InputStream inStream = socket.getInputStream();
		byte[] sizeAr = new byte[4];
		inStream.read(sizeAr);
		int size = ByteBuffer.wrap(sizeAr).asIntBuffer().get();
		byte[] imageAr = new byte[size];
		int bytesRead = 0;
		while(bytesRead < size){
			bytesRead += inStream.read(imageAr, bytesRead, size-bytesRead);
		}
		BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageAr));
		System.out.println("Image Received");
		return image;
	}
	
	private void getAnswers(){
		read = new answerGetter();
		read.start();
	}

	private void disconnect() throws IOException{
		saida.close();
		entrada.close();
		socket.close();
	}
	
	
	/*
	public static void main(String[] args) throws Exception{
		TestClient tc = new TestClient();
		tc.getAnswers();
		while(!tc.commandReceived){
		}
		tc.commandReceived = false;
		tc.commandProcessed = true;

		String testCommand = "login";
		tc.sendCommand(testCommand);
		testCommand = "12345";
		tc.sendCommand(testCommand);
		testCommand = "senha";
		tc.sendCommand(testCommand);
		while(!tc.commandReceived){
		}
		tc.commandReceived = false;
		tc.commandProcessed = true;

		testCommand = "open";
		tc.sendCommand(testCommand);
		testCommand = "teste";
		tc.sendCommand(testCommand);
		while(!tc.commandReceived){
		}
		if(tc.lastLine.equals("reading")){
			// receber imagem
			BufferedImage image = tc.getImage();
			File outFile = new File("../testImage/test.png");
			ImageIO.write(image, "png", outFile);
			// escrever num arquivo pra testar
		}
		tc.commandReceived = false;
		tc.commandProcessed = true;

		tc.sendFile("../testPDF/Interface.pdf");
		while(!tc.commandReceived){
		}
		tc.commandReceived = false;
		tc.commandProcessed = true;

		testCommand = "disconnect";
		tc.sendCommand(testCommand);
//		String input = EntradaTeclado.leString();
//		while(!input.equals("sair")){
//			tc.sendCommand(input);
//		}
//		tc.disconnect();
	}
	*/
	
}
