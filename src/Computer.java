import java.util.*;

public class Computer {
    Memory mem;
    Register reg;
    short currentPC;
    boolean sign, zero, carry, overflow;
    boolean halt;

    boolean debug;

    private final Scanner sc;

    public Computer(){
        this.mem = new Memory();
        this.reg = new Register();
        this.currentPC = 0;
        this.sc = new Scanner(System.in);
        this.debug = false;
    }
    public Computer(boolean d){
        this.mem = new Memory();
        this.reg = new Register();
        this.currentPC = 0;
        this.sc = new Scanner(System.in);
        this.debug = d;
    }
    short getMemory(int index){
        return mem.get(index);
    }
    void setMemory(int index, short elm){
        mem.set(index,elm);
    }
    short getRegister(int index){
        return reg.get(index);
    }
    void setRegister(int index, short elm){
        reg.set(index, elm);
    }
    void goTo(short c){
        this.currentPC = c;
    }
    void move(short b){
        this.currentPC += b;
    }
    void setConditionCodes(boolean s, boolean z, boolean c, boolean v){
        this.sign = s;
        this.zero = z;
        this.carry = c;
        this.overflow = v;
    }
    boolean getConditionSign(){
        return sign;
    }
    boolean getConditionZero(){
        return zero;
    }
    boolean getConditionCarry(){
        return carry;
    }
    boolean getConditionOverflow(){
        return overflow;
    }
    void halt(){
        this.halt = true;
    }

    short input(){
        while(true){
            System.out.print("Input? : ");
            if(sc.hasNextShort()) return sc.nextShort();
            else {
                String invalid = sc.nextLine();
                System.out.printf("Your input \"%s\" is invalid. Please retry.\n", invalid);
            }
        }        
    }
    void output(short o){
        System.out.printf("output on %d : %d\n", currentPC, o);
    }

    void execute(){
        while(!halt){
            Operation op = Operation.toOperation(this.getMemory(currentPC));
            if(debug){
                System.err.printf("%5d : %s\n", currentPC, op);
            }
            op.execute(this);
            if(debug){
                System.err.println(this);
            }
            currentPC++;
        }
    }

    public String toString(){
        StringBuilder b = new StringBuilder();
        b.append(reg);
        b.append(",(");
        b.append(sign ? 1 : 0);
        b.append(zero ? 1 : 0);
        b.append(carry ? 1 : 0);
        b.append(overflow ? 1 : 0);
        b.append(")");
        return b.toString();
    }
}
