package adaptiveHuffman.encoder;

import adaptiveHuffman.BitByteOutputStream;
import adaptiveHuffman.tree.Tree;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;

public class Encoder_f2 {
    static final int ALPHABET_SIZE = 256;
    static final int MAX_NODES = 2 * ALPHABET_SIZE - 1;
    public FileInputStream in = null;
    public BitByteOutputStream out = null;
    private static final int ROOT_NODE = 0;


    public Encoder_f2(String inputFilePath, String outputFilePath) {
        try {
            this.in = new FileInputStream(inputFilePath);
            this.out = new BitByteOutputStream(new FileOutputStream(outputFilePath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

/*
    public void encode(Tree tree) {

        try {

            int c = 0;

            while((c = in.read()) != -1) {
                ArrayList<Boolean> buffer = new ArrayList<Boolean>();
                if (tree.contains(c)) {

                    int len = tree.getCode(c,true,buffer);
                    for(len=len-1 ;len>=0;len--){
                        out.writeBit(buffer.get(len));
                    }
                    tree.insertInto((int)c);
                }
                else {
                    int len = tree.getCode(c, false,buffer);
                    for(len=len-1 ;len>=0;len--){
                        out.writeBit(buffer.get(len));
                    }
                    out.writeByte(c);
                    tree.insertInto(c);
                }

            }
            tree.printTree(false);
            out.flush();
        }
        catch (IOException e) {
            System.err.println("Error reading from input");
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
                out.close();
            }
        }
    }


 */
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
     * false, depth first algoritgm*/

    tree.printTree(false);
    System.out.println("Average Code Length = " + bitCounter / symbolCounter);
}

    public static int[] printFrequencyTable(String str) {
        int[] frequency = new int[256];
        HashSet<Character> charcount = new HashSet<Character>();
        // Count the frequency of each character in the string
        for (int i = 0; i < str.length(); i++) {
            frequency[str.charAt(i)]++;
            charcount.add(str.charAt(i));
        }
        // Print the frequency table
        System.out.println("Char\tFrequency");
        for (int i = 0; i < 256; i++) {
            if (frequency[i] > 0) {
                System.out.println((char) i + "\t" + frequency[i] + "/" + str.length());
            }
        }
        return frequency;
    }



    public static double entropia(String sequence) {
        double entropy = 0.0;
        int [] frequency = printFrequencyTable(sequence);

       for( double p : frequency){
            if (p > 0) {
                entropy += p * Math.log(p) / Math.log(2);
            }
        }

        return entropy;
    }


    public static double calculateAverageCharLength(String str) {
        int[] frequency = printFrequencyTable(str);
        int totalChars = 0;

        // Count the frequency of each character in the string
        for (int i = 0; i < str.length(); i++) {
            frequency[str.charAt(i)]++;
            totalChars++;
        }

        double averageLength = 0.0;

        // Calculate the average length
        for (int i = 0; i < 256; i++) {
            if (frequency[i] > 0) {
                char ch = (char) i;
                double probability = (double) frequency[i] / totalChars;
                int charLength = Character.toString(ch).length(); // Length of char in characters
                averageLength += charLength * probability;
            }
        }
        System.out.println("average lenght = " + averageLength);

        return averageLength;
    }




    public static void main(String[] args) {

        String sequence = "Não sei quantas almas tenho. Cada momento mudei. Continuamente\n" +
                "me estranho. Nunca me vi nem achei. De tanto ser, só tenho alma.\n" +
                "Quem tem alma não tem calma. Quem vê é só o que vê, Quem sente não\n" +
                "é quem é, Atento ao que sou e vejo, Torno-me eles e não eu. Cada meu\n" +
                "sonho ou desejo, É do que nasce e não meu.";

        String sequence1 =  "aardvark";


            /**imprime a sequencia**/
            System.out.println(sequence1);

            System.out.println("IMPRIMIR VARIAVEIS DO SISTEMA: Sequence - R - E - M");
            System.out.println("M = 26, E = 4, R = 10");

            /**calculo de entropia**/
            System.out.println("calculo de entropia");
            System.out.println("entropia = " +  entropia(sequence1));

            /** comprimento medio**/
            System.out.println("Calculate Average Lenght of a symbol " + calculateAverageCharLength(sequence1));


        String inputFilePath = "C:\\Users\\sergi\\IdeaProjects\\Projeto_MultimédiaII_Final\\adaptiveHuffman\\encoder\\sequence1.txt";
        String outputFilePath = "C:\\Users\\sergi\\IdeaProjects\\Projeto_MultimédiaII_Final\\adaptiveHuffman\\encoder\\output1.txt";
        Encoder_f2 enc_f2 = new Encoder_f2(inputFilePath,outputFilePath);
        Tree tree_f2 = new Tree();
        File in = new File(inputFilePath);
        long t = System.nanoTime();
        enc_f2.encode(tree_f2);
        long at = System.nanoTime();
        File out = new File(outputFilePath);
        System.out.println("Finished compression of: "+in.getName()+" in "+(float)(at-t)/1000000+" ms");
        System.out.println("Original size: "+in.length()+" bytes");
        System.out.println("Compressed size: "+out.length()+" bytes");
        System.out.println("Compression ratio: "+((float)in.length()/(float)out.length()));
    }



    }



