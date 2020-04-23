import java.util.Arrays;

class Register{
    short[] reg;
    public Register(){
        reg = new short[8];
    }
    public Register(short[] reg){
        this.reg = Arrays.copyOf(reg, 8);
    }
    short get(int index){
        return reg[index];
    }
    void set(int index, short elm){
        reg[index] = elm;
    }
    public String toString(){
        return Arrays.toString(reg);
    }
}