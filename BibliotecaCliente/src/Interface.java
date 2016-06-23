import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
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
	private JButton emp;
	private JButton upl;
	
	private JButton but;
	private JButton but2;
	

	private JButton returnUser;
	private JButton returnLogin;
	private JButton newUpload;
	private JButton sendUpload;
	
	private JButton nextPDF;
	private JButton backPDF;
	
	private Container pane;
	private JPanel botao;
	
	private JTextField output;

	private JPanel userPanel;
	private JPanel senhaPanel;
	private JPanel userPass;
	private JPanel welcome;
	private JPanel usrScrn;
	
	private JPanel sulPDF;
	
	private JPanel emprestimos;
	private JPanel emprestimosScreen;
	private JPanel emprestimosSouthLayout;
	
	private JPanel uploads;
	private JPanel uploadsScreen;
	private JPanel uploadsSouthLayout;
	private JPanel panelNewUpload;
	
	private JTextField usuario2;
	private JTextField usuario;
	
	private JTextField pdfTotalPages;
	private JTextField pdfCurrentPage;
	
	private JTextField nomeULivro;
	private JTextField nomeUPdf;
	private JTextField nomeULivro2;
	private JTextField nomeUPdf2;
	
	private JPasswordField senha;
	private JTextField senha2;
	private JPasswordField senha3;
	
	private JScrollPane scroll;
	
	private ImagePanel scr;


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
	//---------------------Imagem-----------------------------
	//-----------------------------------------------------------------------
	
	protected class ImagePanel extends JPanel{
		private BufferedImage image;
		
		
		@Override
		protected void  paintComponent(Graphics g){
			super.paintComponent(g);
			g.drawImage(image, 0, 0, null);
		}
		
		@Override
		public Dimension getPreferredSize(){
			return (new Dimension(image.getWidth(), image.getHeight()));
		}
		
		public int getImageWidth(){
			// Italo: ajeita depois que integrar
			return (image == null)? 500 : image.getWidth();
		}
		public void updateImage(BufferedImage newImage){
			image = newImage;
			paintComponent(image.getGraphics());
		}
	}
	
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
//---------------------PdfLayout-----------------------------
//-----------------------------------------------------------------------	
	protected void pdfLayout(){
		pane.setVisible(false);
		pane.removeAll();
		
		pane.add(getButtonReturnUser(), BorderLayout.NORTH);
		scr = new ImagePanel();
	
		BufferedImage aux = null;
		try{
			//Italo: pegar a imagem
			aux = ImageIO.read(new File("C:/Users/vini/git/BibliotecaCliente/BibliotecaCliente/g.jpg"));
		}catch(Exception e){
			System.out.println("deu ruim");
		}
		scr.updateImage(aux);
		
		scroll = new JScrollPane(scr,
		        JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, 
		        JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		int maxWidth = 1200;
		int width = (scr.getImageWidth() > maxWidth)? maxWidth : scr.getImageWidth();
		scroll.setPreferredSize(new Dimension(width, 600));
		scroll.setViewportView(scr);
			
		add(scroll);
	
		
		pane.add(scroll, BorderLayout.WEST);
		pane.add(pdfSulLayout(), BorderLayout.SOUTH);
		pack();
		
		pane.setVisible(true);
	}
	
	
	protected JComponent pdfSulLayout(){
		
		sulPDF = new JPanel();
		sulPDF.setLayout(new GridLayout(1, 4));
		
		nextPDF = new JButton(new AbstractAction(">"){
			@Override
			public void actionPerformed(ActionEvent e){
				//Italo: manda mensagem e recarrega imagem
				//sendCommand("next");
				//waitForResponse();
				//BufferedImage img = getImage();
				//scr.updateImage(img);
				//responseProcessed();
				backToUser();
			}
		});
		
		backPDF = new JButton(new AbstractAction("<"){
			@Override
			public void actionPerformed(ActionEvent e){
				//Italo: manda mensagem e recarrega imagem
				//sendCommand("back");
				backToUser();
			}
		});
		
		pdfCurrentPage = new JTextField("01");
		pdfCurrentPage.setEditable(true);
		pdfCurrentPage.addActionListener(new ActionListener() {
		      public void actionPerformed(ActionEvent e) {
		    	  backToUser();
		    	  //Italo: Go to this page Integer.ParseInt(pdfCurrentPage)
		    	  //Italo: Atualiza Imagem
		      }
		});
		
		pdfTotalPages = new JTextField("/ " + /*Italo: receber quantidade de paginas*/"200");
		pdfTotalPages.setEditable(false);
		
		sulPDF.add(backPDF);
		sulPDF.add(pdfCurrentPage);
		sulPDF.add(pdfTotalPages);
		sulPDF.add(nextPDF);
			
		return sulPDF;
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
	protected JComponent getButtonSendUpload(){
		sendUpload = new JButton(new AbstractAction("Send Upload"){
			@Override
			public void actionPerformed(ActionEvent e){
				if(nomeULivro != null && nomeUPdf != null){
					//Italo: Envia as duas strings e espera a resposta
					//Italo: Checa se o arquivo existe
					backToUser();
				}else{
					backToUser();
				}
			}
		});
		return sendUpload;
	}
	
	protected JComponent getButtonNewUpload(){
		newUpload = new JButton(new AbstractAction("New"){
			@Override
			public void actionPerformed(ActionEvent e){
				newUploadScreen();
			}
		});
		return newUpload;
	}
	
	protected JComponent getButtonReturnUser(){
		returnUser = new JButton(new AbstractAction("Return"){
			@Override
			public void actionPerformed(ActionEvent e){
				backToUser();
			}
		});
		return returnUser;
	}
	
	protected JComponent getButtonNewEmprestimo(){
		returnUser = new JButton(new AbstractAction("New"){
			@Override
			public void actionPerformed(ActionEvent e){
				pane.setVisible(false);
				pane.removeAll();
				acervoEmprestimosScreen(getAcervoEmprestimos());
			}
		});
		return returnUser;
	}
	
	private void backToUser(){
		pane.setVisible(false);
		pane.removeAll();
		userScreen(usuario.getText());
	}
	
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
	private JComponent inicializeNomeU(){
		nomeULivro = new JTextField("Nome do Livro: ");
		nomeULivro.setEditable(false);
		nomeULivro.setPreferredSize(new Dimension(80, 40));
		
		nomeULivro2 = new JTextField();
		nomeULivro2.setEditable(true);
		nomeULivro2.setPreferredSize(new Dimension(80, 40));
		
		nomeUPdf = new JTextField("Nome do PDF: ");
		nomeUPdf.setEditable(false);
		nomeUPdf.setPreferredSize(new Dimension(80, 40));
		
		nomeUPdf2 = new JTextField();
		nomeUPdf2.setEditable(true);
		nomeUPdf2.setPreferredSize(new Dimension(80, 40));
		
		userPanel = new JPanel();
		userPanel.setLayout(new GridLayout(2,2));
		
		userPanel.add(nomeUPdf);
		userPanel.add(nomeUPdf2);
		userPanel.add(nomeULivro);
		userPanel.add(nomeULivro2);
		
		return userPanel;
	}
	
	private void newUploadScreen(){
		pane.setVisible(false);
		pane.removeAll();
		
		
		uploadsSouthLayout = new JPanel();
		uploadsSouthLayout.setLayout(new GridLayout(1,2));
		uploadsSouthLayout.add(getButtonSendUpload());
		uploadsSouthLayout.add(getButtonReturnUser());
		
		
		pane.add(uploadsSouthLayout, BorderLayout.SOUTH);
		pane.add(inicializeNomeU(), BorderLayout.CENTER);
		pack();
		pane.setVisible(true);
	}
	
	private void uploadsScreen(){
		pane.add(getWelcomeLayout("Lista de Uploads"), BorderLayout.NORTH);
		
		uploadsSouthLayout = new JPanel();
		uploadsSouthLayout.setLayout(new GridLayout(1,2));
		uploadsSouthLayout.add(getButtonReturnUser());
		uploadsSouthLayout.add(getButtonNewUpload());
			
		String[] frango = new String[100];
		for(int i=0; i<100; i++){
			frango[i] = "teste " + i;
		}	
		
		pane.add(uploadsScreenLayout(frango), BorderLayout.WEST);
		
		pane.add(uploadsSouthLayout, BorderLayout.SOUTH);
		pane.setVisible(true);
		pack();
	}
		
	private JComponent uploadsScreenLayout(String[] frango){
		pane.removeAll();
		pane.setVisible(false);
		uploadsScreen = new JPanel();
		uploadsScreen.setLayout(new FlowLayout());
			
		uploads = new JPanel();
		uploads.setLayout(new GridLayout(frango.length, 1));

		uploadBut = new JButton[frango.length];
		for(int i=0; i<frango.length; i+=1){
			uploadBut[i] = new JButton(new AbstractAction(frango[i]){
				@Override
				public void actionPerformed(ActionEvent e){
					backToUser();
					//Italo: envia comando de remocao upload
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
		
		emprestimosSouthLayout = new JPanel();
		emprestimosSouthLayout.setLayout(new GridLayout(1,2));
		
		pane.add(getWelcomeLayout("Lista de Emprestimos"), BorderLayout.NORTH);
		
		String[] frango = new String[100];
		for(int i=0; i<100; i++){
			frango[i] = "teste " + i;
		}
		pane.add(emprestimosScreenLayout(frango), BorderLayout.WEST);
		
		emprestimosSouthLayout.add(getButtonReturnUser());
		emprestimosSouthLayout.add(getButtonNewEmprestimo());
		
		pane.add(emprestimosSouthLayout, BorderLayout.SOUTH);
		
		pane.setVisible(true);
		pack();
	}
	
	private JComponent emprestimosScreenLayout(String[] livros){		
		emprestimosScreen = new JPanel();
		emprestimosScreen.setLayout(new FlowLayout());
		
		emprestimos = new JPanel();
		emprestimos.setLayout(new GridLayout(livros.length, 1));

		emprestimoBut = new JButton[livros.length];
		for(int i=0; i<livros.length; i+=1){
			emprestimoBut[i] = new JButton(new AbstractAction(livros[i]){
				@Override
				public void actionPerformed(ActionEvent e){
					//Italo: soh chama a funcao se o servidor conseguir abrir, caso
					//contrario Erro
					pdfLayout();

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
	
	
	private void acervoEmprestimosScreen(String[] teste){
		pane.setVisible(false);
		pane.removeAll();
		
		emprestimosSouthLayout = new JPanel();
		emprestimosSouthLayout.setLayout(new GridLayout(1,2));
		
		pane.add(getWelcomeLayout("Lista de Emprestimos"), BorderLayout.NORTH);
		
		pane.add(acervoEmprestimosScreenLayout(teste), BorderLayout.WEST);
		
		pane.add(getButtonReturnUser(), BorderLayout.SOUTH);
		
		pane.setVisible(true);
		pack();
	}
	
	private JComponent acervoEmprestimosScreenLayout(String[] livros){		
		emprestimosScreen = new JPanel();
		emprestimosScreen.setLayout(new FlowLayout());
		
		emprestimos = new JPanel();
		emprestimos.setLayout(new GridLayout(livros.length, 1));

		emprestimoBut = new JButton[livros.length];
		for(int i=0; i<livros.length; i+=1){
			emprestimoBut[i] = new JButton(new AbstractAction(livros[i]){
				@Override
				public void actionPerformed(ActionEvent e){
					//Italo: enviar o comando de novo emprestimo
					backToUser();
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
	
	private String[] getAcervoEmprestimos(){
		String[] teste = new String[10];
		for(int i=0; i<10; i++){
			teste[i] = "teste " + i;
		}	
		return teste;
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
