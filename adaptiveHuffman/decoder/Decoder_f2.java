package adaptiveHuffman.decoder;

import adaptiveHuffman.BitInputStream;
import adaptiveHuffman.tree.Node;
import adaptiveHuffman.tree.Tree;

import java.io.*;
import java.util.Scanner;

public class Decoder_f2 {

    private BitInputStream in = null;
    private FileOutputStream out = null;

    public static void main(String[] args) throws FileNotFoundException {

        Scanner string = new Scanner(new File("C:\\Users\\sergi\\IdeaProjects\\Projeto_MultimédiaII_Final\\adaptiveHuffman\\decoder\\output-f2.txt"));

        String inputFilePath = "C:\\Users\\sergi\\IdeaProjects\\Projeto_MultimédiaII_Final\\adaptiveHuffman\\encoder\\output1.txt";
        String outputFilePath = "C:\\Users\\sergi\\IdeaProjects\\Projeto_MultimédiaII_Final\\adaptiveHuffman\\decoder\\output-f2.txt";
        Decoder_f2 dec_2 = new Decoder_f2(inputFilePath, outputFilePath);
        Tree tree = new Tree();
        File in = new File(inputFilePath);
        long t = System.nanoTime();
        dec_2.decode(tree);
        long at = System.nanoTime();
        File out = new File(outputFilePath);
        while (string.hasNextLine())
        {
            System.out.println("String-> " + string.nextLine());
        }
        System.out.println("Finished decompression of: " + in.getName() + " in " + (float) (at - t) / 1000000 + " ms");
        System.out.println("Original size: " + in.length() + " bytes");
        System.out.println("Uncompressed size: " + out.length() + " bytes");
        System.out.println("Compression ratio: " + ((float) out.length() / (float) in.length()));

    }

    public Decoder_f2(String inputFilePath, String outputFilePath) {
        try {
            this.in = new BitInputStream(new FileInputStream(inputFilePath));
            this.out = new FileOutputStream(outputFilePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    private int readByte(BitInputStream in) throws IOException {
        int bitBuffer = 0;
        int c;
        for (int i = 0; i < 8; i++) {
            c = in.read();
            bitBuffer |= c;
            if (i != 7) bitBuffer <<= 1;

        }
        return bitBuffer;
    }

    public void decode(Tree tree) {
        try {

            int c = 0;

            if (tree.isEmpty()) { // Just write out first byte.
                int bitBuffer = 0;
                for (int i = 0; i < 8; i++) {
                    c = in.read();
                    bitBuffer |= c;
                    System.out.println("bitbuffer = " + bitBuffer + "\n\n" + "*************");
                    if (i != 7) bitBuffer <<= 1;
                }
                out.write(bitBuffer);
                tree.insertInto(bitBuffer);

                // imprime o primeiro estágio da árvore
                tree.printTree(false);
             //   System.out.println("------------------------------------------");
            }
            Node node = tree.root;
            while ((c = in.read()) != -1) {
                if (c == 1) node = node.right;
                if (c == 0) node = node.left;

                int value = 0;
                if (node.isNYT()) {
                    value = readByte(in);
                    out.write(value);
                    tree.insertInto(value);
                    node = tree.root;
                }
                if (node.isLeaf()) {
                    value = node.getValue();
                    out.write(value);
                    tree.insertInto(value);
                    node = tree.root;
                }

                // imprime a árvore que vai sendo construida
                tree.printTree(false);
            }
            //tree.printTree(false);

        } catch (IOException e) {
            System.err.println("Error reading bytes");
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
