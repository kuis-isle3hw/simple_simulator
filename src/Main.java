import java.io.*;
import java.util.*;


public class Main {
    
    static void makeMIF(Computer com,String filename) throws Exception{
        File mif = new File(filename);
        if(!mif.exists()) mif.createNewFile();
        FileWriter writer = new FileWriter(mif);
        writer.write("WIDTH = 16;\n");
        writer.write("DEPTH = 4096;\n");
        writer.write("ADDRESS_RADIX = DEC;\n");
        writer.write("DATA_RADIX = DEC;\n");
        writer.write("CONTENT BEGIN\n");
        for(int n=0; n<4096; n++){
            writer.write(n + " : " + com.getMemory(n) + ";\n"); 
        }
        writer.write("END\n");
        writer.close();
    }
    public static void main(String[] args) throws Exception {
        boolean debug = false;
        boolean mifOutput = false;
        String inputFileName = "sample.txt";
        

        for(String s: args){
            if(s.equals("-d")) debug = true;
            else if(s.equals("-m")) mifOutput = true;
            else if(new File(s).isFile()) inputFileName = s;
        }
        if(!new File(inputFileName).isFile()) {
            System.out.printf("File \"%s\" does not exit.", inputFileName);
            return;
        }
        Scanner sc = new Scanner(new BufferedReader(new FileReader(inputFileName)));

        Computer com = new Computer(debug);

        //set memory
        int line=1;
        int pc = 0;
        while(sc.hasNext()){
            String s = sc.nextLine();
            try{
                com.setMemory(pc, Operation.toBinary(s));
                pc++;
            }catch(Exception e){
                if(!s.trim().split("//")[0].equals("")){
                    System.err.printf("caution: \"%s\" at line %d is not operation\nmessage : %s\n",
                                       s, line, e.getMessage());
                }
            }finally {
                line++;
            }
        }

        if(mifOutput) makeMIF(com,"output.mif");

        com.execute();

        if(mifOutput && debug) makeMIF(com,"debug_output.mif");



    }
}
