package org.apache.iotdb.tsfile.encoding;
public class Group {
    public int[] number;
    public int count;
    public int if_count;
    public int[] count_array;
    public int range;


    public Group(int[] number, int count) {
        this.number = number;
        this.count = count;
        this.if_count = 0;
    }

    public int[] getNumber() {
        return number;
    }


    public int getCount() {
        return count;
    }

    public void addNumber(int number) {
        this.number[this.count] = number;
        this.count++;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void incrementCount() {
        count++;
    }

    @Override
    public String toString() {
        return "Number: " + number + ", Count: " + count;
    }
}