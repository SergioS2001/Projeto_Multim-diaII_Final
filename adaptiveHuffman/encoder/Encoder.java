package adaptiveHuffman.encoder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Time;
import java.util.ArrayList;

import adaptiveHuffman.BitByteOutputStream;
import adaptiveHuffman.tree.*;

public class Encoder {
	
	public FileInputStream in = null;
	public BitByteOutputStream out = null;
    
    public static void main(String[] args) {

		/**definir os ficheiros para comprimir e onde guardar os ficheiros comprimidos*/

			String inputFilePath = "C:\\Users\\sergi\\IdeaProjects\\Projeto_MultimédiaII_Final\\adaptiveHuffman\\encoder\\in.txt";
			String outputFilePath = "C:\\Users\\sergi\\IdeaProjects\\Projeto_MultimédiaII_Final\\adaptiveHuffman\\encoder\\dickens_TESTE.txt";
			File in = new File(inputFilePath);
			File out = new File(outputFilePath);

		/**Passar para a classe construtor os ficheiros*/

			Encoder enc = new Encoder(inputFilePath,outputFilePath);

		/**Inicar uma nova arvore para apresentar */

			Tree tree = new Tree();

		/**nano time faz o registo do decorrer da função*/

    		long t = System.nanoTime();
    		enc.encode(tree);
    		long at = System.nanoTime();

		/**Apresentar o tempo decorrido em MS e S e as restantes métricas para avaliar a performance do codec*/

    		System.out.println("Finished compression of: "+in.getName()+" in "+(float)(at-t)/1000000+" ms" + "()" + (float)(at-t)/1000 + "s");
    		System.out.println("Original size: "+in.length()+" bytes");
    		System.out.println("Compressed size: "+out.length()+" bytes");
    		System.out.println("Compression ratio: "+((float)in.length()/(float)out.length()));
	}

	/**Função Construtora que inicializa os ficheiros originiais e onde guardar os ficheiros comprimidos*/
    public Encoder(String inputFilePath, String outputFilePath) {
    	try {
			this.in = new FileInputStream(inputFilePath);
			this.out = new BitByteOutputStream(new FileOutputStream(outputFilePath));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
    	
    }

	public void encode(Tree tree) {

		/**Varivaveis auxiliares para a contagem dos bits
		 * bitCounter serve guardar o comprimento original da mensagem em bits e para identificar os diferentes simbolos
		 * symbolCounter guarda o numero total de simbolos passados na input stream de bits que são lidas no ciclo while
		 * isto após o processo de compressão
		 * */
		double bitCounter = 0;
		double symbolCounter = 0;
		try {

			int c = 0;
			
			while((c = in.read()) != -1) {
				ArrayList<Boolean> buffer = new ArrayList<Boolean>();

				/**Verificar se o simbolo já existe na arvore.
				 * Por default não existe, apenas simbolo NYT*/

				if (tree.contains(c)) {

					/**Enquanto houver conteudo de bytes para leitura, eles são guardados no buffer e incrementado no bitCounter*/

					int len = tree.getCode(c,true,buffer);
					bitCounter += len;
					// c

					/**Apos guardar a informação todas de bits, os valores comprimidos são enviados para o ficheiro de output */
					for(len=len-1 ;len>=0;len--){
						out.writeBit(buffer.get(len));
					}
					/**Simbolo introduzido na arvore
					 * apos isto é passado por diferentes funções da classe tree onde ira inserir o simbolo no local correto
					 * seja incrementar num node/leaf existente e verificar se a arvore necessita de adaptar a usa estrutura*/
					tree.insertInto((int)c);
				}
				else {
					/**Caso não exista, e definido o id do simbolo e o seu peso e introduzido na arvore*/
					int len = tree.getCode(c, false,buffer);
					bitCounter += len;
					for(len=len-1 ;len>=0;len--){
						out.writeBit(buffer.get(len));
					}
					out.writeByte(c);
					tree.insertInto(c);
				}
				symbolCounter ++;
			}
			out.flush();
		}
		catch (IOException e) {
			System.err.println("Error reading from input");
			e.printStackTrace();
		}
		finally {
			/**verificar que os ficheirios são corridos ate EOF*/
			if(in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(out != null) {
				out.close();
			}
		}
		/**Por fim e impresso para o terminal a arvore final
		 * e passado um boolean para definir o metodo pelo qual a arovre de ver impressa
		 * true, usa o metodo breadth first algoritgm
		 * false, depth first algorithm*/
		tree.printTree(false);
		System.out.println("Average Code Length = " + bitCounter / symbolCounter);
	}
}
