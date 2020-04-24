public interface Operation {
    void execute(Computer com);
    short toBinary();
    static short cut(short binary,int right, int length){
        short mask = (short) ((1<<length)-1);
        return (short) ((binary>>right)&mask);
    }
    static short extend(short binary, int oldLength){
        if(((binary>>(oldLength-1))&1)==0){
            return (short) (binary & ((1<<oldLength)-1));
        }else{
            return (short) (binary | (((-1)<<oldLength)));
        }
    }
    static Operation toOperation(short binary){
        switch (cut(binary, 14, 2)){
            case 0:{
                short ra = cut(binary, 11, 3);
                short rb = cut(binary, 8,3);
                short d = cut(binary, 0, 8);
                return new Load(ra,rb,extend(d, 8));
            }
            case 1:{
                short ra = cut(binary, 11, 3);
                short rb = cut(binary, 8,3);
                short d = cut(binary, 0, 8);
                return new Store(ra,rb,d);
            }
            case 2:{
                int rb = cut(binary, 8,3);
                short d = cut(binary, 0,8);
                switch (cut(binary, 11, 3)){
                    case 0: return new LoadImmediate(rb, d);
                    case 4: return new Branch(d);
                    case 7:{
                        int cond = rb;
                        switch (cond){
                            case 0: return new BranchEqual(d);
                            case 1: return new BranchLess(d);
                            case 2: return new BranchLeq(d);
                            case 3: return new BranchNeq(d);
                            default: return new Nop();
                        }
                    }
                    default: return new Nop();
                }
            }
            default:{
                int rs = cut(binary, 11, 3);
                int rd = cut(binary, 8, 3);
                short d = cut(binary, 0, 4);
                switch(cut(binary, 4,4)){
                    case 0: return new Add(rd, rs);
                    case 1: return new Sub(rd, rs);
                    case 2: return new And(rd, rs);
                    case 3: return new Or(rd, rs);
                    case 4: return new Xor(rd, rs);
                    case 5: return new Cmp(rd, rs);
                    case 6: return new Mov(rd, rs);
                    case 8: return new ShiftLeftLogical(rd,d);
                    case 9: return new ShiftLeftRotate(rd, d);
                    case 10: return new ShiftRightLogical(rd, d);
                    case 11: return new ShiftRightArith(rd, d);
                    case 12: return new Input(rd);
                    case 13: return new Output(rs);
                    case 15: return new Halt();
                    default: return new Nop();
                }
            }
        }
    }

    int dataRadix = 10;
    static short toBinary(String s){
        String[] validTokens = s.trim().split("//")[0].split("[^-\\w]+");
        Operation op;
        if(validTokens.length==0) throw new IllegalArgumentException("empty line");
        else if(validTokens[0].equalsIgnoreCase("ADD")){
            int rd = Integer.valueOf(validTokens[1]);
            int rs = Integer.valueOf(validTokens[2]);
            if(!(0 <= rd && rd <= 7)) throw new IllegalArgumentException("invalid register number in ADD: "+rd);
            if(!(0 <= rs && rs <= 7)) throw new IllegalArgumentException("invalid register number in ADD: "+rs);
            op = new Add(rd, rs);
        }
        else if(validTokens[0].equalsIgnoreCase("SUB")){
            int rd = Integer.valueOf(validTokens[1]);
            int rs = Integer.valueOf(validTokens[2]);
            if(!(0 <= rd && rd <= 7)) throw new IllegalArgumentException("invalid register number in SUB: "+rd);
            if(!(0 <= rs && rs <= 7)) throw new IllegalArgumentException("invalid register number in SUB: "+rs);
            op = new Sub(rd, rs);
        }
        else if(validTokens[0].equalsIgnoreCase("AND")){
            int rd = Integer.valueOf(validTokens[1]);
            int rs = Integer.valueOf(validTokens[2]);
            if(!(0 <= rd && rd <= 7)) throw new IllegalArgumentException("invalid register number in AND: "+rd);
            if(!(0 <= rs && rs <= 7)) throw new IllegalArgumentException("invalid register number in AND: "+rs);
            op = new And(rd, rs);
        }
        else if(validTokens[0].equalsIgnoreCase("OR")){
            int rd = Integer.valueOf(validTokens[1]);
            int rs = Integer.valueOf(validTokens[2]);
            if(!(0 <= rd && rd <= 7)) throw new IllegalArgumentException("invalid register number in OR: "+rd);
            if(!(0 <= rs && rs <= 7)) throw new IllegalArgumentException("invalid register number in OR: "+rs);
            op = new Or(rd, rs);
        }
        else if(validTokens[0].equalsIgnoreCase("XOR")){
            int rd = Integer.valueOf(validTokens[1]);
            int rs = Integer.valueOf(validTokens[2]);
            if(!(0 <= rd && rd <= 7)) throw new IllegalArgumentException("invalid register number in XOR: "+rd);
            if(!(0 <= rs && rs <= 7)) throw new IllegalArgumentException("invalid register number in XOR: "+rs);
            op = new Xor(rd, rs);
        }
        else if(validTokens[0].equalsIgnoreCase("CMP")){
            int rd = Integer.valueOf(validTokens[1]);
            int rs = Integer.valueOf(validTokens[2]);
            if(!(0 <= rd && rd <= 7)) throw new IllegalArgumentException("invalid register number in CMP: "+rd);
            if(!(0 <= rs && rs <= 7)) throw new IllegalArgumentException("invalid register number in CMP: "+rs);
            op = new Cmp(rd, rs);
        }
        else if(validTokens[0].equalsIgnoreCase("MOV")){
            int rd = Integer.valueOf(validTokens[1]);
            int rs = Integer.valueOf(validTokens[2]);
            if(!(0 <= rd && rd <= 7)) throw new IllegalArgumentException("invalid register number in MOV: "+rd);
            if(!(0 <= rs && rs <= 7)) throw new IllegalArgumentException("invalid register number in MOV: "+rs);
            op = new Mov(rd, rs);
        }
        else if(validTokens[0].equalsIgnoreCase("SLL")){
            int rd = Integer.valueOf(validTokens[1]);
            short d = Short.valueOf(validTokens[2]);
            if(!(0 <= rd && rd <= 7)) throw new IllegalArgumentException("invalid register number in SLL: "+rd);
            if(!(0 <= d && d <= 15)) throw new IllegalArgumentException("invalid shift length in SLL: "+d);
            op = new ShiftLeftLogical(rd, d);
        }
        else if(validTokens[0].equalsIgnoreCase("SLR")){
            int rd = Integer.valueOf(validTokens[1]);
            short d = Short.valueOf(validTokens[2]);
            if(!(0 <= rd && rd <= 7)) throw new IllegalArgumentException("invalid register number in SLR: "+rd);
            if(!(0 <= d && d <= 15)) throw new IllegalArgumentException("invalid shift length in SLR: "+d);
            op = new ShiftLeftRotate(rd, d);
        }
        else if(validTokens[0].equalsIgnoreCase("SRL")){
            int rd = Integer.valueOf(validTokens[1]);
            short d = Short.valueOf(validTokens[2]);
            if(!(0 <= rd && rd <= 7)) throw new IllegalArgumentException("invalid register number in SRL: "+rd);
            if(!(0 <= d && d <= 15)) throw new IllegalArgumentException("invalid shift length in SRL: "+d);
            op = new ShiftRightLogical(rd, d);
        }
        else if(validTokens[0].equalsIgnoreCase("SRA")){
            int rd = Integer.valueOf(validTokens[1]);
            short d = Short.valueOf(validTokens[2]);
            if(!(0 <= rd && rd <= 7)) throw new IllegalArgumentException("invalid register number in SRA: "+rd);
            if(!(0 <= d && d <= 15)) throw new IllegalArgumentException("invalid shift length in SRA: "+d);
            op = new ShiftRightArith(rd, d);
        }
        else if(validTokens[0].equalsIgnoreCase("IN")){
            int rd = Integer.valueOf(validTokens[1]);
            if(!(0 <= rd && rd <= 7)) throw new IllegalArgumentException("invalid register number in IN: "+rd);
            op = new Input(rd);
        }
        else if(validTokens[0].equalsIgnoreCase("OUT")){
            int rs = Integer.valueOf(validTokens[1]);
            if(!(0 <= rs && rs <= 7)) throw new IllegalArgumentException("invalid register number in OUT: "+rs);
            op = new Output(rs);
        }
        else if(validTokens[0].equalsIgnoreCase("HLT")){
            op = new Halt();
        }
        else if(validTokens[0].equalsIgnoreCase("LD")){
            int ra = Integer.valueOf(validTokens[1]);
            short d = Short.valueOf(validTokens[2]);
            int rb = Integer.valueOf(validTokens[3]);
            if(!(0 <= ra && ra <= 7)) throw new IllegalArgumentException("invalid register number in LD: "+ra);
            if(!(0 <= rb && rb <= 7)) throw new IllegalArgumentException("invalid register number in LD: "+rb);
            if(!(-128 <= d && d <= 127)) throw new IllegalArgumentException("invalid immediate value in LD: "+d);
            op = new Load(ra,rb, d);
        }
        else if(validTokens[0].equalsIgnoreCase("ST")){
            int ra = Integer.valueOf(validTokens[1]);
            short d = Short.valueOf(validTokens[2]);
            int rb = Integer.valueOf(validTokens[3]);
            if(!(0 <= ra && ra <= 7)) throw new IllegalArgumentException("invalid register number in ST: "+ra);
            if(!(0 <= rb && rb <= 7)) throw new IllegalArgumentException("invalid register number in ST: "+rb);
            if(!(-128 <= d && d <= 127)) throw new IllegalArgumentException("invalid immediate value in ST: "+d);
            op = new Store(ra,rb, d);
        }
        else if(validTokens[0].equalsIgnoreCase("LI")){
            int rb = Integer.valueOf(validTokens[1]);
            short d = Short.valueOf(validTokens[2]);
            if(!(0 <= rb && rb <= 7)) throw new IllegalArgumentException("invalid register number in LI: "+rb);
            if(!(-128 <= d && d <= 127)) throw new IllegalArgumentException("invalid immediate value in LI: "+d);
            op = new LoadImmediate(rb, d);
        }
        else if(validTokens[0].equalsIgnoreCase("B")){
            short d = Short.valueOf(validTokens[1]);
            if(!(-128 <= d && d <= 127)) throw new IllegalArgumentException("invalid immediate value in B: "+d);
            op = new Branch(d);
        }
        else if(validTokens[0].equalsIgnoreCase("BE")){
            short d = Short.valueOf(validTokens[1]);
            if(!(-128 <= d && d <= 127)) throw new IllegalArgumentException("invalid immediate value in BE: "+d);
            op = new BranchEqual(d);
        }
        else if(validTokens[0].equalsIgnoreCase("BLT")){
            short d = Short.valueOf(validTokens[1]);
            if(!(-128 <= d && d <= 127)) throw new IllegalArgumentException("invalid immediate value in BLT: "+d);
            op = new BranchLess(d);
        }
        else if(validTokens[0].equalsIgnoreCase("BLE")){
            short d = Short.valueOf(validTokens[1]);
            if(!(-128 <= d && d <= 127)) throw new IllegalArgumentException("invalid immediate value in BLE: "+d);
            op = new BranchLeq(d);
        }
        else if(validTokens[0].equalsIgnoreCase("BNE")){
            short d = Short.valueOf(validTokens[1]);
            if(!(-128 <= d && d <= 127)) throw new IllegalArgumentException("invalid immediate value in BNE: "+d);
            op = new BranchNeq(d);
        }
        else if(validTokens.length==1){
            try{
                return Short.valueOf(validTokens[0], dataRadix);
            }catch(Exception e){
                throw new IllegalArgumentException("only one token, not number");
            }
        }
        else throw new IllegalArgumentException("no operation matched");
        return op.toBinary();
    }
}

class Add implements Operation{
    int rd, rs;
    public Add(int rd, int rs){
        this.rd = rd;
        this.rs = rs;
    }
    public void execute(Computer com){
        int result = com.getRegister(rd) + com.getRegister(rs);
        boolean s = ((short)result)<0;
        boolean z = ((short)result)==0;
        boolean c = ((result>>16)&1)>0;
        boolean v =result>=32768 || result<-32768;
        com.setConditionCodes(s,z,c,v);
        com.setRegister(rd, (short)result);
    }
    public short toBinary(){
        return (short) ((0b11 << 14) + (rs << 11) + (rd << 8) + (0b0000 << 4) + (0 << 0));
    }
    public String toString(){
        return "ADD "+ rd + ","+rs;
    }
}
class Sub implements Operation{
    int rd, rs;
    public Sub(int rd, int rs){
        this.rd = rd;
        this.rs = rs;
    }
    public void execute(Computer com){
        int result = com.getRegister(rd) - com.getRegister(rs);
        boolean s = ((short)result)<0;
        boolean z = ((short)result)==0;
        boolean c = ((result>>16)&1)>0;
        boolean v =result>=32768 || result<-32768;
        com.setConditionCodes(s,z,c,v);
        com.setRegister(rd, (short)result);
    }
    public short toBinary(){
        return (short) ((0b11 << 14) + (rs << 11) + (rd << 8) + (0b0001 << 4) + (0 << 0));
    }
    public String toString(){
        return "SUB "+ rd + ","+rs;
    }
}
class And implements Operation{
    int rd, rs;
    public And(int rd, int rs){
        this.rd = rd;
        this.rs = rs;
    }
    public void execute(Computer com){
        int result = com.getRegister(rd) & com.getRegister(rs);
        boolean s = ((short)result)<0;
        boolean z = ((short)result)==0;
        boolean c = false;
        boolean v = false;
        com.setConditionCodes(s,z,c,v);
        com.setRegister(rd, (short)result);
    }
    public short toBinary(){
        return (short) ((0b11 << 14) + (rs << 11) + (rd << 8) + (0b0010 << 4) + (0 << 0));
    }
    public String toString(){
        return "AND "+ rd + ","+rs;
    }
}
class Or implements Operation{
    int rd, rs;
    public Or(int rd, int rs){
        this.rd = rd;
        this.rs = rs;
    }
    public void execute(Computer com){
        int result = com.getRegister(rd) | com.getRegister(rs);
        boolean s = ((short)result)<0;
        boolean z = ((short)result)==0;
        boolean c = false;
        boolean v = false;
        com.setConditionCodes(s,z,c,v);
        com.setRegister(rd, (short)result);
    }
    public short toBinary(){
        return (short) ((0b11 << 14) + (rs << 11) + (rd << 8) + (0b0011 << 4) + (0 << 0));
    }
    public String toString(){
        return "OR "+ rd + ","+rs;
    }
}
class Xor implements Operation{
    int rd, rs;
    public Xor(int rd, int rs){
        this.rd = rd;
        this.rs = rs;
    }
    public void execute(Computer com){
        int result = com.getRegister(rd) ^ com.getRegister(rs);
        boolean s = ((short)result)<0;
        boolean z = ((short)result)==0;
        boolean c = false;
        boolean v = false;
        com.setConditionCodes(s,z,c,v);
        com.setRegister(rd, (short)result);
    }
    public short toBinary(){
        return (short) ((0b11 << 14) + (rs << 11) + (rd << 8) + (0b0100 << 4) + (0 << 0));
    }
    public String toString(){
        return "XOR "+ rd + ","+rs;
    }
}
class Cmp implements Operation{
    int rd, rs;
    public Cmp(int rd, int rs){
        this.rd = rd;
        this.rs = rs;
    }
    public void execute(Computer com){
        int result = com.getRegister(rd) - com.getRegister(rs);
        boolean s = ((short)result)<0;
        boolean z = ((short)result)==0;
        boolean c = ((result>>16)&1)>0;
        boolean v =result>=32768 || result<-32768;
        com.setConditionCodes(s,z,c,v);
    }
    public short toBinary(){
        return (short) ((0b11 << 14) + (rs << 11) + (rd << 8) + (0b0101 << 4) + (0 << 0));
    }
    public String toString(){
        return "CMP "+ rd + ","+rs;
    }
}
class Mov implements Operation{
    int rd, rs;
    public Mov(int rd, int rs){
        this.rd = rd;
        this.rs = rs;
    }
    public void execute(Computer com){
        boolean s = com.getRegister(rs)<0;
        boolean z = com.getRegister(rs)==0;
        boolean c = false;
        boolean v =false;
        com.setConditionCodes(s,z,c,v);
        com.setRegister(rd, com.getRegister(rs));
    }
    public short toBinary(){
        return (short) ((0b11 << 14) + (rs << 11) + (rd << 8) + (0b0110 << 4) + (0 << 0));
    }
    public String toString(){
        return "MOV "+ rd + ","+rs;
    }
}

class ShiftLeftLogical implements Operation{
    int rd; short d;
    public ShiftLeftLogical(int rd, short d){
        this.rd = rd;
        this.d = d;
    }
    public void execute(Computer com){
        int result = com.getRegister(rd)<<d;
        boolean s = ((short)result)<0;
        boolean z = ((short)result)==0;
        boolean c = d==0 || Operation.cut(com.getRegister(rd), 16-d, 1)>0 ;
        boolean v =false;
        com.setConditionCodes(s,z,c,v);
        com.setRegister(rd, (short)result);
    }
    public short toBinary(){
        return (short) ((0b11 << 14) + (0 << 11) + (rd << 8) + (0b1000 << 4) + (d << 0));
    }
    public String toString(){
        return "SLL "+ rd + ","+d;
    }
}
class ShiftLeftRotate implements Operation{
    int rd; short d;
    public ShiftLeftRotate(int rd, short d){
        this.rd = rd;
        this.d = d;
    }
    public void execute(Computer com){
        int result = com.getRegister(rd)<<d + Operation.cut(com.getRegister(rd), 15, d);
        boolean s = ((short)result)<0;
        boolean z = ((short)result)==0;
        boolean c = false;
        boolean v =false;
        com.setConditionCodes(s,z,c,v);
        com.setRegister(rd, (short)result);
    }
    public short toBinary(){
        return (short) ((0b11 << 14) + (0 << 11) + (rd << 8) + (0b1001 << 4) + (d << 0));
    }
    public String toString(){
        return "SLR "+ rd + ","+d;
    }
}
class ShiftRightLogical implements Operation{
    int rd; short d;
    public ShiftRightLogical(int rd, short d){
        this.rd = rd;
        this.d = d;
    }
    public void execute(Computer com){
        int result = (com.getRegister(rd)>>d) & ((1<<(16-d))-1);
        boolean s = ((short)result)<0;
        boolean z = ((short)result)==0;
        boolean c = d==0 || Operation.cut(com.getRegister(rd), d-1, 1)>0 ;
        boolean v =false;
        com.setConditionCodes(s,z,c,v);
        com.setRegister(rd, (short)result);
    }
    public short toBinary(){
        return (short) ((0b11 << 14) + (0 << 11) + (rd << 8) + (0b1010 << 4) + (d << 0));
    }
    public String toString(){
        return "SRL "+ rd + ","+d;
    }
}
class ShiftRightArith implements Operation{
    int rd; short d;
    public ShiftRightArith(int rd, short d){
        this.rd = rd;
        this.d = d;
    }
    public void execute(Computer com){
        int result = com.getRegister(rd)>>d;
        boolean s = ((short)result)<0;
        boolean z = ((short)result)==0;
        boolean c = d==0 || Operation.cut(com.getRegister(rd), d-1, 1)>0 ;
        boolean v =false;
        com.setConditionCodes(s,z,c,v);
        com.setRegister(rd, (short)result);
    }
    public short toBinary(){
        return (short) ((0b11 << 14) + (0 << 11) + (rd << 8) + (0b1011 << 4) + (d << 0));
    }
    public String toString(){
        return "SRA "+ rd + ","+d;
    }
}
class Input implements Operation{
    int rd;
    public Input(int rd){
        this.rd = rd;
    }
    public void execute(Computer com){
        short result = com.input();
        boolean s = result <0;
        boolean z = result==0;
        boolean c = false;
        boolean v =false;
        com.setConditionCodes(s,z,c,v);
        com.setRegister(rd, result);
    }
    public short toBinary(){
        return (short) ((0b11 << 14) + (0 << 11) + (rd << 8) + (0b1100 << 4) + (0 << 0));
    }
    public String toString(){
        return "IN "+ rd;
    }
}
class Output implements Operation{
    int rs;
    public Output(int rs){
        this.rs = rs;
    }
    public void execute(Computer com){
        com.output(com.getRegister(rs));
    }
    public short toBinary(){
        return (short) ((0b11 << 14) + (rs << 11) + (0 << 8) + (0b1101 << 4) + (0 << 0));
    }
    public String toString(){
        return "OUT "+ rs;
    }
}
class Halt implements Operation{
    public Halt(){}
    public void execute(Computer com){
        com.halt();
    }
    public short toBinary(){
        return (short) ((0b11 << 14) + (0 << 11) + (0 << 8) + (0b1111 << 4) + (0 << 0));
    }
    public String toString(){
        return "HLT";
    }
}

class Load implements Operation{
    int ra, d, rb;
    public Load(int ra, int rb, short d){
        this.ra = ra;
        this.rb = rb;
        this.d = d;
    }
    public void execute(Computer com){
        com.setRegister(ra, com.getMemory(com.getRegister(rb) + d));
    }
    public short toBinary(){
        return (short) ((0b00 << 14) + (ra << 11) + (rb << 8) + (d << 0));
    }
    public String toString(){
        return "LD "+ ra + ","+d+"("+rb+")";
    }
}
class Store implements Operation{
    int ra, rb;
    short d;
    public Store(int ra, int rb, short d){
        this.ra = ra;
        this.rb = rb;
        this.d = d;
    }
    public void execute(Computer com){
        com.setMemory(com.getRegister(rb) + d, com.getRegister(ra));
    }
    public short toBinary(){
        return (short) ((0b01 << 14) + (ra << 11) + (rb << 8) + (d << 0));
    }
    public String toString(){
        return "ST "+ ra + ","+d+"("+rb+")";
    }
}

class LoadImmediate implements Operation{
    int rb;
    short d;
    public LoadImmediate(int rb, short d){
        this.rb = rb;
        this.d = Operation.extend(d,8);
    }
    public void execute(Computer com){
        com.setRegister(rb, d);
    }
    public short toBinary(){
        return (short) ((0b10 << 14) + (0b000 << 11) + (rb << 8) + (Operation.cut(d,0,8) << 0));
    }
    public String toString(){
        return "LI "+rb+","+d;
    }
}
class Branch implements Operation{
    short d;
    public Branch(short d){
        this.d = Operation.extend(d,8);
    }
    public void execute(Computer com){
        com.move(d);
    }
    public short toBinary(){
        return (short) ((0b10 << 14) + (0b100 << 11) + (0 << 8) + (Operation.cut(d,0,8) << 0));
    }
    public String toString(){
        return "B "+d;
    }
}
class BranchEqual implements Operation{
    short d;
    public BranchEqual(short d){
        this.d = Operation.extend(d,8);;
    }
    public void execute(Computer com){
        if(com.getConditionZero()) com.move(d);
    }
    public short toBinary(){
        return (short) ((0b10 << 14) + (0b111 << 11) + (0b000 << 8) + (Operation.cut(d,0,8) << 0));
    }
    public String toString(){
        return "BE "+d;
    }
}
class BranchLess implements Operation{
    short d;
    public BranchLess(short d){
        this.d = Operation.extend(d,8);;
    }
    public void execute(Computer com){
        if(com.getConditionSign()^com.getConditionOverflow()) com.move(d);
    }
    public short toBinary(){
        return (short) ((0b10 << 14) + (0b111 << 11) + (0b001 << 8) + (Operation.cut(d,0,8) << 0));
    }
    public String toString(){
        return "BLT "+d;
    }
}
class BranchLeq implements Operation{
    short d;
    public BranchLeq(short d){
        this.d = Operation.extend(d,8);;
    }
    public void execute(Computer com){
        if(com.getConditionZero() | (com.getConditionSign()^com.getConditionOverflow())) com.move(d);
    }
    public short toBinary(){
        return (short) ((0b10 << 14) + (0b111 << 11) + (0b010 << 8) + (Operation.cut(d,0,8) << 0));
    }
    public String toString(){
        return "BLE "+d;
    }
}
class BranchNeq implements Operation{
    short d;
    public BranchNeq(short d){
        this.d = Operation.extend(d,8);;
    }
    public void execute(Computer com){
        if(!com.getConditionZero()) com.move(d);
    }
    public short toBinary(){
        return (short) ((0b10 << 14) + (0b111 << 11) + (0b011 << 8) + (Operation.cut(d,0,8) << 0));
    }
    public String toString(){
        return "BNE "+d;
    }
}

class Nop implements Operation{
    public Nop(){}
    public void execute(Computer com){}
    // nop = branch 0
    public short toBinary(){
        return (short) (0b1010000000000000);
    }
    public String toString(){
        return "NOP";
    }
}