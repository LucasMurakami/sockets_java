import java.io.*;
import java.net.*;

public class ClienteM4 {
    public static void main(String[] args) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        try {
            Socket socket = new Socket("localhost", 6789);

            // Para envio de objetos para o servidor
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            // Para recebimento de objetos do servidor
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            // Recebimento da lista de arquivos do servidor
            File[] files = (File[]) in.readObject();

            while (true) {

                boolean checkedNumber = false;
                int fileNumber = 0;

                // Print da lista de arquivos
                for (int i = 0; i < files.length; i++) {
                    System.out.println((i + 1) + ". " + files[i].getName());
                }

                // Seleção dos arquivos para transferência ou sair da seleção
                System.out.println("Insira o número do arquivo que você deseja baixar. Ou Insira 'Q' para sair. ");
                String input = reader.readLine();

                // Saída do loop, envio de um objeto para o servidor que indica o término do processo
                if (input.equalsIgnoreCase("q")) {
                    out.writeObject(null);
                    break;
                }

                try {
                    fileNumber = Integer.parseInt(input);
                } catch (NumberFormatException e) {
                    System.out.println("Não é um número válido. Tente novamente.");
                    checkedNumber = true;
                }

                if (fileNumber > 0 && fileNumber <= files.length) {
                    // Envio da seleção do arquivo escolhido por meio do index para o servidor
                    out.writeObject(files[fileNumber - 1]);

//                byte[] fileContent = (byte[]) in.readObject();
//                Path path = Paths.get("src\\main\\resources\\ClientResources\\" + files[fileNumber - 1].getName());
//                Files.write(path, fileContent);

                    receiveFile(files, fileNumber, in);
                } else {
                    if(!checkedNumber)
                        System.out.println("Não é um número válido. Tente novamente. ");
                }

            }

            // Encerramento da conexão
            socket.close();

        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }

    public static void receiveFile(File[] files, int fileNumber, ObjectInputStream in) throws IOException {
        String path = "src\\main\\resources\\ClientResources\\" + files[fileNumber - 1].getName();
        FileOutputStream fos = new FileOutputStream(path);

        byte[] buffer = new byte[1024 * 1024];
        int read;
        while ((read = in.readInt()) != -1) {  // Read until the end of the file is indicated
            in.readFully(buffer, 0, read);
            fos.write(buffer, 0, read);
        }
        fos.close();
        System.out.println("Arquivo '" + files[fileNumber - 1].getName() + "' recebido com sucesso e salvo em '" + path + "'.");
    }

}
