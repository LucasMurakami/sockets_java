import java.io.*;
import java.net.*;

public class ServidorM4 {
    public static void main(String[] args) {

        // Pasta contendo arquivos para transferência
        String directoryPath = "src\\main\\resources\\ServerResources";
        File directory = new File(directoryPath);
        // Array com todos arquivos
        File[] files = directory.listFiles();

        while(true) {

            try {
                ServerSocket serverSocket = new ServerSocket(6789);

                while (true) {

                    System.out.println("Servidor aguardando conexão...");
                    Socket socket = serverSocket.accept();
                    System.out.println("Conexão iniciada.");

                    // Para envio de objetos para o cliente
                    ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());

                    // Para recebimento de objetos do cliente
                    ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

                    // Envio do array com todos arquivos para o cliente
                    out.writeObject(files);
                    System.out.println("Enviando a lista de arquivos disponíveis ao cliente...");

                    while (true) {
                        // Recebimento da escolha do arquivo escolhido do cliente
                        File requestedFile = (File) in.readObject();

                        // Condicional para checar o encerramento da conexão com o cliente
                        if (requestedFile == null) break;

                        System.out.println("Arquivo escolhido pelo cliente: " + requestedFile);

                        // Envio do arquivo escolhido para o cliente
                        System.out.println("Enviando arquivo para o cliente...");
//                    byte[] fileContent = Files.readAllBytes(requestedFile.toPath());
//                    out.writeObject(fileContent);
                        sendFile(requestedFile, out);
                    }

                    // Fechamento do socket cliente
                    socket.close();
                    System.out.println("Conexão com o cliente encerrada.");
                }

            } catch (SocketException e) {
                System.out.println("Conexão com o cliente instável. ");
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

    }

    public static void sendFile(File requestedFile, ObjectOutputStream out) throws IOException {
        FileInputStream fis = new FileInputStream(requestedFile);
        byte[] buffer = new byte[1024 * 1024];
        int read;
        while ((read = fis.read(buffer)) > 0) {
            out.writeInt(read);  // Send the number of bytes to be read
            out.write(buffer, 0, read);
        }
        out.writeInt(-1);  // Indicate the end of the file
        out.flush();  // Flush the stream
        fis.close();
        System.out.println("Arquivo enviado com sucesso. Nome do arquvivo: " + requestedFile.getName());
    }

}
