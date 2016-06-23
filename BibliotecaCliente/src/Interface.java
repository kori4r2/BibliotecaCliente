import java.awt.*;
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
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextField;


public class Interface extends JFrame implements ActionListener{
	//------------------------------------------------------------------------
	//-----------------------------------------------------------------------
	//---------------------Comunicacao--------------------------------------
	//-----------------------------------------------------------------------
	//------------------------------------------------------------------------

	// Thread que roda em paralelo recebendo as mensagens do servidor
	private class AnswerGetter extends Thread{
		public void run(){
			String answer = new String("");
			try{
				// Enquanto estiver conectado
				while(entrada.hasNextLine()){
					// Recebe a proxima linha do servidor
					lastLine = entrada.nextLine();
					// Exibe na tela a mensagem recebida
					System.out.println("command " + lastLine + " received");
					// Se a mensagem for a de desconectar, faz isso e retorna
					if(lastLine.equals("disconnect")){
						disconnect();
						return;
					}
					// Seta as flags relativas a mensagem
					commandReceived = true;
					commandProcessed = false;
					int i = 0;
					// Espera que a mensagem seja processada na thread principal
					while(!commandProcessed){
						// Possivel utilizar a variavel para indicar grandes tempos de espera
						i++;
					}
					// Exibe na tela que o comando foi processado
					System.out.println("command " + lastLine + " processed");
				}
			}catch(Exception e){
			}
		}
	}


	// As variaveis sao declaradas como volatile para evitar conflito com a thread em paralelo
	private volatile Socket socket;
	private volatile PrintStream saida;
	private volatile Scanner entrada;
	// A string lastLine contem o ultimo comando recebido do servidor
	private volatile String lastLine;
	private volatile AnswerGetter read;
	private volatile boolean commandReceived;
	private volatile boolean commandProcessed;

	// Envia uma string para o servidor
	private void sendCommand(String s){
		saida.println(s);
	}

	// Espera pela resposta do servidor
	private void waitForResponse(){
		int i = 0;
		while(!commandReceived){
			i++;
		}
	}

	// Indica que a ultima resposta foi processada
	private void responseProcessed(){
		commandProcessed = true;
		commandReceived = false;
	}

	// Funcao que envia um arquivo para o servidor
	private void sendFile(String filepath) throws Exception{
		File file = null;
		Path pdfpath = null;
		file = new File(filepath);
		// Verifica se o arquivo existe
		if(!file.exists() || file.isDirectory()){
			// Caso nao exista, exibe mensagem correspondende e da throw em excecao
			System.out.println("Nome de arquivo invalido (" + filepath + ")");
			throw new Exception("invalid file");
		}
		pdfpath = Paths.get(filepath);
		// Avisa o servidor que vai comecar a enviar
		sendCommand("sending");
		// Espera a resposta do servidor
		waitForResponse();
		// Caso o servidor esteja pronto
		if(lastLine.equals("ready")){
			// Envia o arquivo
			OutputStream outStream = (OutputStream)saida;

			// Se o arquivo for grande a ponto de causar erro na alocacao de memoria, avisa o usuario
			if(file.length() > Integer.MAX_VALUE)
				System.out.println("file is too big");
			// Obtem o tamanho do arquivo e os seus bytes
			int fileSize = (int)file.length();
			byte[] size = ByteBuffer.allocate(4).putInt(fileSize).array();
			byte[] byteArray = Files.readAllBytes(pdfpath);

			// Envia o tamanho do arquivo e seus bytes para o servidor
			outStream.write(size);
			outStream.write(byteArray, 0, fileSize);
			// Envia mensagem avisando que o upload acabou
			sendCommand("uploaded");
			// Processa a resposta
			responseProcessed();
			// Espera resposta do servidor
			waitForResponse();
		}else{
			// Caso haja erro no servidor, processa a mensagem e sai da funcao
			responseProcessed();
			return;
		}
		// Processa a resposta do servidor
		responseProcessed();
	}


	// Metodo que regebe uma imagem do servidor e retorna o BufferedImage correspondente
	private BufferedImage getImage() throws Exception{
		System.out.println("Receiving image...");
		InputStream inStream = socket.getInputStream();

		// Avisa que o usuario esta pronto para receber a imagem
		sendCommand("ready");
		// Recebe o tamanho da imagem
		byte[] sizeAr = new byte[4];
		inStream.read(sizeAr);
		int size = ByteBuffer.wrap(sizeAr).asIntBuffer().get();
		// Recebe os bytes da imagem
		byte[] imageAr = new byte[size];
		int bytesRead = 0;
		// O loop garante que a imagem chega com o tamanho total
		while(bytesRead < size){
			bytesRead += inStream.read(imageAr, bytesRead, size-bytesRead);
		}
		// Converte os bytes para uma BufferedImage
		BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageAr));
		// Processa o ultimo comando
		responseProcessed();
		// Espera pela resposta do servidor
		waitForResponse();
		// Caso haja erro, avisa e da throw na excecao
		if(lastLine.equals("error")){
			System.out.println("Error receiving image");
			throw new Exception("erro no envio da imagem (server side)");
		}
		// Exibe que a imagem foi recebida
		System.out.println("Image Received");
		// Processa o comando e retorna a imagem
		responseProcessed();
		return image;
	}

	// Recebe a lista de emprestimos do usuario atual
	private String[] getUsuarioEmprestimos(){
		// Envia o pedido para o servidor
		sendCommand("loanList");
		// list armazena todas as linhas de resposta
		Vector<String> list = new Vector<String>(0, 1);
		waitForResponse();
		// A string finished indica o fim da lista
		while(!lastLine.equals("finished")){
			list.add(lastLine);
			responseProcessed();
			waitForResponse();
		}
		responseProcessed();

		return list.toArray(new String[list.size()]);
	}
	
	// Similar ao metodo acima, mas com a lista completa de livros do acervo
	private String[] getAcervoEmprestimos(){
		sendCommand("fullList");
		Vector<String> list = new Vector<String>(0, 1);
		waitForResponse();
		while(!lastLine.equals("finished")){
			list.add(lastLine);
			responseProcessed();
			waitForResponse();
		}
		responseProcessed();

		return list.toArray(new String[list.size()]);
	}

	// Similar aos metodos acima mas com a lista de uploads do usuario atual
	private String[] getUsuarioUploads(){
		sendCommand("uploadList");
		Vector<String> list = new Vector<String>(0, 1);
		waitForResponse();
		while(!lastLine.equals("finished")){
			list.add(lastLine);
			responseProcessed();
			waitForResponse();
		}
		responseProcessed();

		return list.toArray(new String[list.size()]);
	}
	
	// Comeca a rodar a thread para recepcao de mensagens do servidor
	private void getAnswers(){
		read = new AnswerGetter();
		read.start();
	}

	// Disconecta tudo
	private void disconnect() throws IOException{
		saida.close();
		entrada.close();
		socket.close();
	}

	//------------------------------------------------------------------------
	//-----------------------------------------------------------------------
	//---------------------Interface---------------------------------------
	//-----------------------------------------------------------------------
	//------------------------------------------------------------------------


	// Classe auxiliar para permitir a criacao de botoes dentro de um loop for
	private abstract class SpecialAction extends AbstractAction{
		public final int pos;

		public SpecialAction(String s, int i){
			super(s);
			pos = i;
		}
	}
	
	private JButton[] emprestimoBut;
	private JButton[] uploadBut;
	private JButton emp;
	private JButton upl;
	
	private JButton but;
	private JButton but2;
	

	private JButton returnUser;
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
		// Conecta com o servidor
		socket = new Socket("127.0.0.1", 9669);
		// Obtem entrada e saida
		saida = new PrintStream(socket.getOutputStream());
		entrada = new Scanner(socket.getInputStream());
		// Inicializa o receptor de mensagens
		commandReceived = false;
		getAnswers();

		// Processa a mensagem de conexao bem sucedida
		waitForResponse();
		responseProcessed();

		// Interface
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
			g.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);
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
			repaint();
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
	
		BufferedImage img = null;
		// Pega a imagem do servidor
		try{
			img = getImage();
		}catch(Exception e){
			// Avisa que houve erro
			sendCommand("error");
			responseProcessed();
			// Processa a mensagem de erro
			waitForResponse();
			responseProcessed();
			// Volta para a tela inicial
			backToUser();
			return;
		}
		scr.updateImage(img);
		
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
				BufferedImage img = null;
				sendCommand("next");
				waitForResponse();
				if(lastLine.equals("sending")){
					try{
						img = getImage();
					}catch(Exception e1){
						// Avisa que houve erro
						sendCommand("error");
						responseProcessed();
						// Processa a mensagem de erro
						waitForResponse();
						responseProcessed();
						// Volta para a tela inicial
						backToUser();
						return;
					}
					scr.updateImage(img);
				}else{
					// Se der erro antes que o usuario fique pronto para enviara imagem
					responseProcessed();
					// volta para a tela inicial
					backToUser();
					return;
				}
				waitForResponse();
				if(lastLine.equals("error")){
					// Se houver erro, volta para a tela inicial
					backToUser();
					responseProcessed();
				}else{
					pdfCurrentPage.setText(lastLine);
					responseProcessed();
				}
			}
		});
		
		backPDF = new JButton(new AbstractAction("<"){
			@Override
			public void actionPerformed(ActionEvent e){
				BufferedImage img = null;
				sendCommand("previous");
				waitForResponse();
				if(lastLine.equals("sending")){
					try{
						img = getImage();
					}catch(Exception e2){
						// Avisa que houve erro
						sendCommand("error");
						responseProcessed();
						// Processa a mensagem de erro
						waitForResponse();
						responseProcessed();
						// Volta para a tela inicial
						backToUser();
						return;
					}
					scr.updateImage(img);
				}else{
					// Se der erro antes que o usuario fique pronto para enviara imagem
					responseProcessed();
					// volta para a tela inicial
					backToUser();
					return;
				}
				waitForResponse();
				if(lastLine.equals("error")){
					// Se houver erro, volta para a tela inicial
					backToUser();
					responseProcessed();
				}else{
					pdfCurrentPage.setText(lastLine);
					responseProcessed();
				}
			}
		});
		
		waitForResponse();
		pdfCurrentPage = new JTextField(lastLine);
		responseProcessed();
		pdfCurrentPage.setEditable(true);
		pdfCurrentPage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				BufferedImage img = null;
				sendCommand("choice");
				sendCommand(pdfCurrentPage.getText());
				waitForResponse();
				if(lastLine.equals("sending")){
					try{
						img = getImage();
					}catch(Exception e3){
						// Avisa que houve erro
						sendCommand("error");
						responseProcessed();
						// Processa a mensagem de erro
						waitForResponse();
						responseProcessed();
						// Volta para a tela inicial
						backToUser();
						return;
					}
					scr.updateImage(img);
				}else{
					// Se der erro antes que o usuario fique pronto para enviar a imagem
					responseProcessed();
					// volta para a tela inicial
					backToUser();
					return;
				}
				waitForResponse();
				if(lastLine.equals("error")){
					// Se houver erro, volta para a tela inicial
					responseProcessed();
					backToUser();
				}else{
					pdfCurrentPage.setText(lastLine);
					responseProcessed();
				}
			}
		});
		
		waitForResponse();
		pdfTotalPages = new JTextField("/ " + lastLine);
		responseProcessed();
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
				if(nomeULivro2.getText() != null && nomeUPdf2.getText() != null){
					//Italo: Envia as duas strings e espera a resposta
					sendCommand("upload");
					sendCommand(nomeULivro2.getText());
					sendCommand(nomeUPdf2.getText());
					waitForResponse();
					//Italo: Checa se o arquivo existe
					if(lastLine.equals("upload")){
						responseProcessed();
						try{
							sendFile("../clientPDFs/" + nomeUPdf2.getText());
						}catch(Exception e4){
							sendCommand("error");
						}
					}else{
						waitForResponse();
						responseProcessed();
					}
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
				if(scr != null){
					sendCommand("close");
					waitForResponse();
					responseProcessed();
					scr = null;
				}
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
				if(usuario.getText().isEmpty() == false && senha.getPassword() != null
						/* && usuario e senha sao compativeis*/){
					sendCommand("login");
					sendCommand(usuario.getText());
					sendCommand(new String(senha.getPassword()));
					waitForResponse();
					if(lastLine.equals("success")){
						responseProcessed();
						senha.resetKeyboardActions();
						pane.setVisible(false);
						pane.remove(welcome);
						pane.remove(userPass);
						pane.remove(botao);
						userScreen(usuario.getText());
					}else{
						responseProcessed();
						pane.remove(welcome);
						pane.setVisible(false);
						pane.setVisible(true);
						pane.add(getWelcomeLayout("Usuario ou Senha Incorretos"));
					}
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

						sendCommand("newUser");
						sendCommand(usuario.getText());
						sendCommand(new String(senha.getPassword()));
						System.out.println(lastLine + "1");
						waitForResponse();
						System.out.println(lastLine + "2");
						if(lastLine.equals("success")){
							responseProcessed();
							senha3.resetKeyboardActions();
							pane.setVisible(false);
							userScreen(usuario.getText());
						}else{
							responseProcessed();
							pane.setVisible(false);
							pane.remove(welcome);
							pane.add(getWelcomeLayout("Usuario ou Senha Incompativel"), BorderLayout.NORTH);
							pack();
							pane.setVisible(true);
						}
				}else{
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
	
	private void uploadsScreen(String[] frango){
		pane.add(getWelcomeLayout("Lista de Uploads"), BorderLayout.NORTH);
		
		uploadsSouthLayout = new JPanel();
		uploadsSouthLayout.setLayout(new GridLayout(1,2));
		uploadsSouthLayout.add(getButtonReturnUser());
		uploadsSouthLayout.add(getButtonNewUpload());
		
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
			uploadBut[i] = new JButton(new SpecialAction(frango[i], i){
				@Override
				public void actionPerformed(ActionEvent e){
					sendCommand("rmvUpload");
					sendCommand(uploadBut[pos].getText());
					waitForResponse();
					// Poderia checar o tipo de erro e exibir
					responseProcessed();
					backToUser();
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
	private void emprestimosScreen(String[] frango){
		pane.setVisible(false);
		pane.removeAll();
		
		emprestimosSouthLayout = new JPanel();
		emprestimosSouthLayout.setLayout(new GridLayout(1,2));
		
		pane.add(getWelcomeLayout("Lista de Emprestimos"), BorderLayout.NORTH);
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
			emprestimoBut[i] = new JButton(new SpecialAction(livros[i], i){
				@Override
				public void actionPerformed(ActionEvent e){
					//Italo: soh chama a funcao se o servidor conseguir abrir, caso
					//contrario Erro
					sendCommand("open");
					sendCommand(emprestimoBut[pos].getText());
					waitForResponse();
					if(lastLine.equals("reading")){
						pdfLayout();
					}else{
						responseProcessed();
						backToUser();
					}

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
			emprestimoBut[i] = new JButton(new SpecialAction(livros[i], i){
				@Override
				public void actionPerformed(ActionEvent e){
					//Italo: enviar o comando de novo emprestimo
					String[] bookStatus = emprestimoBut[pos].getText().split(" ");
					String title = bookStatus[0];
					int stock = -1;
					try{
						stock = Integer.parseInt(bookStatus[1]);
					}catch(NumberFormatException e5){
						backToUser();
					}
					// Se nao houver um livro o botao nao faz nada
					// Pode fazer com que seja necessario recarregar a lista
					if(stock > 0){
						sendCommand("newLoan");
						sendCommand(title);
						waitForResponse();
						// Poderia avaliar se houve erro e exibir mensagem de erro
						responseProcessed();
						backToUser();
					}
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
				
				emprestimosScreen(getUsuarioEmprestimos());
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
				
				uploadsScreen(getUsuarioUploads());
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
	
}
