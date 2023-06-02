package adaptiveHuffman.decoder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import adaptiveHuffman.BitInputStream;
import adaptiveHuffman.tree.*;

public class Decoder {
	
	private BitInputStream in = null;
	private FileOutputStream out = null;
	
    public static void main(String[] args) {

			String inputFilePath = "C:\\Users\\sergi\\IdeaProjects\\Projeto_MultimédiaII_Final\\adaptiveHuffman\\outputEncodeFiles\\dickens_encode";
			String outputFilePath = "C:\\Users\\sergi\\IdeaProjects\\Projeto_MultimédiaII_Final\\adaptiveHuffman\\decoder\\dickens_OUT.txt";

    		Decoder dec = new Decoder(inputFilePath,outputFilePath);
    		Tree tree = new Tree();
    		File in = new File(inputFilePath);
			long t = System.nanoTime();
    		dec.decode(tree);
			long at = System.nanoTime();
    		File out = new File(outputFilePath);
    		System.out.println("Finished decompression of: "+in.getName() +" in "+(float)(at-t)/1000000+" ms");
    		System.out.println("Original size: "+in.length()+" bytes");
    		System.out.println("Uncompressed size: "+out.length()+" bytes");
    		System.out.println("Compression ratio: "+((float)out.length()/(float)in.length()));

    }
    
    public Decoder(String inputFilePath, String outputFilePath) {
    	try {
			this.in = new BitInputStream(new FileInputStream(inputFilePath));
			this.out = new FileOutputStream(outputFilePath);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
    	
    }
    
    
	public void decode(Tree tree) {
		try {
			int c = 0;
			/** Para iniciar a codificação, a arvore é iniciada vazia apenas com o Node NYT*/
			if(tree.isEmpty()) { // Just write out first byte.
				int bitBuffer = 0;
				/**Loop para ler o primeiro simbolo, e ajustar o buffer para ler o proximo byte**/
				for(int i = 0; i<8;i++) {
					c = in.read();
					bitBuffer |= c;
					if(i!=7) bitBuffer <<= 1;
				}
				out.write(bitBuffer);
				tree.insertInto(bitBuffer);

				// imprime o primeiro estágio da árvore
				tree.printTree(false);
				System.out.println("--------------------");
			}
			/**quando ja existe Nodes e Leafs dentro da arvore, é passado para o processo de definir
			 *  a sua posição dentro da arvore e do peso que irá ter */
			Node node = tree.root;
			while((c = in.read()) != -1) {
				if(c == 1) node = node.right;
				if(c == 0) node = node.left;
				
				int value = 0;
				if(node.isNYT()) {
					value = readByte(in); 
					out.write(value);
					tree.insertInto(value);
					node = tree.root;
				}
				if(node.isLeaf()) {
					value = node.getValue();
					out.write(value);
					tree.insertInto(value);
					node = tree.root;
				}

				// imprime a árvore que vai sendo construida
				tree.printTree(false);
				System.out.println("--------------------");
			}
		}
		catch (IOException e) {
			System.err.println("Error reading bytes");
			e.printStackTrace();
		}
		finally {
			if(in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private int readByte(BitInputStream in) throws IOException {
		int bitBuffer = 0;
		int c;
		for(int i = 0; i<8;i++) {
			c = in.read();
			bitBuffer |= c;
			if(i!=7) bitBuffer <<= 1;
			
		}
		return bitBuffer;
	}
}
