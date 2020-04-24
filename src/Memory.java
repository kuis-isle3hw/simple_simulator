import java.util.*;

class Memory{
    short[] memory;
    public Memory(){
        memory = new short[4096];
        Arrays.fill(memory, new Nop().toBinary());
    }
    public Memory(short[] mem){
        this.memory = Arrays.copyOf(mem, 4096);
    }
    short get(int index){
        return memory[index];
    }
    void set(int index, short elm){
        memory[index] = elm;
    }
    public String toString(){
        return Arrays.toString(memory);
    }
}