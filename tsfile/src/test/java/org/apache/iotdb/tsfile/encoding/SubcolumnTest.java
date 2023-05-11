package org.apache.iotdb.tsfile.encoding;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;
import org.apache.commons.compress.archivers.ar.ArArchiveEntry;
import org.apache.iotdb.tsfile.file.metadata.enums.CompressionType;
import org.apache.iotdb.tsfile.file.metadata.enums.TSEncoding;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

public class SubcolumnTest {
    static double log2_10 = Math.log(10) / Math.log(2);
    public static int getBitWith(int num) {
        if (num == 0) return 1;
        else
            return 32 - Integer.numberOfLeadingZeros(num);
    }

    public static byte[] int2Bytes(int integer) {
        byte[] bytes = new byte[4];
        bytes[0] = (byte) (integer >> 24);
        bytes[1] = (byte) (integer >> 16);
        bytes[2] = (byte) (integer >> 8);
        bytes[3] = (byte) integer;
        return bytes;
    }
    public static byte[] bitPacking(ArrayList<Integer> numbers, int bit_width) {
        int block_num = numbers.size() / 8;
        byte[] result = new byte[bit_width * block_num];
        for (int i = 0; i < block_num; i++) {
            for (int j = 0; j < bit_width; j++) {
                int tmp_int = 0;
                for (int k = 0; k < 8; k++) {
                    tmp_int += (((numbers.get(i * 8 + k) >> j) % 2) << k);
                }
                result[i * bit_width + j] = (byte) tmp_int;
            }
        }
        return result;
    }
    public static byte[] double2Bytes(double dou){
        long value = Double.doubleToRawLongBits(dou);
        byte[] bytes= new byte[8];
        for(int i=0;i<8;i++){
            bytes[i] = (byte) ((value >>8*i)& 0xff);
        }
        return bytes;
    }
    public static void main(@org.jetbrains.annotations.NotNull String[] args) throws IOException {
        ArrayList<String> input_path_list = new ArrayList<>();
        ArrayList<String> output_path_list = new ArrayList<>();
        input_path_list.add("C:\\Users\\xiaoj\\Desktop\\elfdata\\1");
        output_path_list.add("C:\\Users\\xiaoj\\Desktop\\test_ratio_elf.csv");

        double value = 8.85;
        long longBits = Double.doubleToLongBits(value);
        String binaryString = Long.toBinaryString(longBits);
        System.out.println(binaryString);

        for (int file_i = 0; file_i < input_path_list.size(); file_i++) {
            String inputPath = input_path_list.get(file_i);
            String Output = output_path_list.get(file_i);

            // speed
            int repeatTime = 1; // set repeat time
            String dataTypeName = "double"; // set dataType

            File file = new File(inputPath);
            File[] tempList = file.listFiles();

            // select encoding algorithms
            TSEncoding[] encodingList = {
//            TSEncoding.PLAIN ,
                    TSEncoding.TS_2DIFF,
//                    TSEncoding.CHIMP,
                    TSEncoding.GORILLA,
            };
            // select compression algorithms
            CompressionType[] compressList = {
                    CompressionType.UNCOMPRESSED,
                    //            CompressionType.LZ4,
                    //            CompressionType.GZIP,
                    //            CompressionType.SNAPPY
            };
            CsvWriter writer = new CsvWriter(Output, ',', StandardCharsets.UTF_8);

            String[] head = {
                    "Input Direction",
                    "Column Index",
                    "Encoding Algorithm",
                    "Compress Algorithm",
                    "Encoding Time",
                    "Decoding Time",
                    "Compress Time",
                    "Uncompress Time",
                    "Points",
                    "Compressed Size",
                    "Compression Ratio"
            };
            writer.writeRecord(head); // write header to output file

            assert tempList != null;
            int fileRepeat = 0;
//            ArrayList<Integer> columnIndexes = new ArrayList<>(); // set the column indexes of compressed
//            for (int i = 0; i < 2; i++) {
//                columnIndexes.add(i, i);
//            }
            for (File f : tempList) {

                InputStream inputStream = Files.newInputStream(f.toPath());
                CsvReader loader = new CsvReader(inputStream, StandardCharsets.UTF_8);
                ArrayList<Double> data = new ArrayList<>();
                ArrayList<String> data_str = new ArrayList<>();
                ArrayList<ArrayList<Float>> data_decoded = new ArrayList<>();
                int max_precision = 0;

                // add a column to "data"
                loader.readHeaders();
                data_str.clear();
                while (loader.readRecord()) {
//                    System.out.println(loader.getValues()[1]);
                    data_str.add(loader.getValues()[0]);

                }
                data_str.removeIf(String::isEmpty);
                for (String f_str:data_str){
                    int cur_pre = 0;
                    if (f_str.split("\\.").length != 1) {
                        cur_pre = f_str.split("\\.")[1].length();
                    }
                    if (cur_pre > max_precision) {
                        max_precision = cur_pre;
                    }
//                    System.out.println(Double.valueOf(f_str).floatValue());
                    data.add(Double.valueOf(f_str));
                }


                inputStream.close();
                long encodeTime = 0;
                long decodeTime = 0;
                double ratio = 0;
                double compressed_size = 0;
                int repeatTime2 = 1;
                System.out.println(max_precision);
                for (int i = 0; i < repeatTime; i++) {
                    long s = System.nanoTime();
                    ArrayList<Byte> buffer = new ArrayList<>();
//                    System.out.println(data.get(0));
                    for (int repeat = 0; repeat < repeatTime2; repeat++)
                        buffer = SubcolumnEncoder(data, 1025, max_precision);
                    long e = System.nanoTime();
                    encodeTime += ((e - s) / repeatTime2);
                    compressed_size += buffer.size();
                    double ratioTmp =
                            (double) buffer.size() / (double) (data.size() * Integer.BYTES * 2);
                    ratio += ratioTmp;
                    s = System.nanoTime();
//          for(int repeat=0;repeat<repeatTime2;repeat++)
//            data_decoded = ReorderingRegressionDecoder(buffer);
                    e = System.nanoTime();
                    decodeTime += ((e - s) / repeatTime2);
                }

                ratio /= repeatTime;
                compressed_size /= repeatTime;
                encodeTime /= repeatTime;
                decodeTime /= repeatTime;

                String[] record = {
                        f.toString(),
                        "SUB-COLUMN",
                        String.valueOf(encodeTime),
                        String.valueOf(decodeTime),
                        String.valueOf(data.size()),
                        String.valueOf(compressed_size),
                        String.valueOf(ratio)
                };
//                System.out.println(ratio);
                writer.writeRecord(record);
                break;
            }
            writer.close();
        }
    }
    private static ArrayList<Byte> SubcolumnEncoder(ArrayList<Double> data, int block_size, int max_precision) {
        ArrayList<Byte> encoded_result = new ArrayList<Byte>();
        int length_all = data.size();
        int encoded_length_all = 0;
        byte[] length_all_bytes = int2Bytes(length_all);
        for (byte b : length_all_bytes) encoded_result.add(b);
        encoded_length_all += 4;

        int block_num = length_all / block_size;
//        System.out.println("block_num");
//        System.out.println(block_num);

        // encode block size (Integer)
        byte[] block_size_byte = int2Bytes(block_size);
        for (byte b : block_size_byte) encoded_result.add(b);
        encoded_length_all += 4;
//        int offset = 0;
//        double numberOffset = Math.pow(10,offset);
//        max_precision -= offset;
//        for (int i = 0; i < 1; i++) {
        for (int i = 0; i < block_num; i++) {
            double start_value = data.get(block_size*i);
            byte[] start_value_byte = double2Bytes(start_value);
            for (byte b : start_value_byte) encoded_result.add(b);
            long longBits = Double.doubleToLongBits(start_value);
            String binaryString = Long.toBinaryString(longBits);
            ArrayList<Boolean> sign = new ArrayList<Boolean>();
            ArrayList<Integer> exp = new ArrayList<Integer>();
            ArrayList<Long> mantissa = new ArrayList<Long>();
            ArrayList<Long> mantissa_int = new ArrayList<Long>();

            int max_precision_v = 0;
            int before_exp = Integer.parseInt(binaryString.substring(0,11),2);
            encoded_length_all += 8;

            int max_exp = Integer.MIN_VALUE;
            int min_exp = Integer.MAX_VALUE;
            for (int j=1;j<block_size;j++){
                double d = data.get(block_size*i+j);
                longBits = Double.doubleToLongBits(d);
                binaryString = Long.toBinaryString(longBits);

                if(binaryString.length()==64){
                    sign.add(true);
                    binaryString = binaryString.substring(1);
                }else {
                    sign.add(false);
                }

                int cur_exp = Integer.parseInt(binaryString.substring(0,11),2);

                if(cur_exp>max_exp){
                    max_exp = cur_exp;
                }
                if(cur_exp<min_exp){
                    min_exp = cur_exp;
                }
                exp.add(cur_exp);
//                exp.add(cur_exp ^ before_exp);
                before_exp = cur_exp;

                cur_exp -= 1023;

                String v = Double.toString(d);
                int ind = v.indexOf(".");
                if (ind > -1) {
                    int len = (int) ((double)v.substring(ind + 1).length() * log2_10);
                    if (len > max_precision_v) {
                        max_precision_v = len;
                    }
                }
            }
//           sign
            encoded_length_all += (block_size-1)/8;

//            System.out.println(max_precision_v);
            long max_int = Long.MIN_VALUE;
            long min_int = Long.MAX_VALUE;
            int encoded_length_all_bit = 0;
            for (int j=1;j<block_size;j++){
                double d = data.get(block_size*i+j);
                longBits = Double.doubleToLongBits(d);
                binaryString = Long.toBinaryString(longBits);
                int cur_exp = exp.get(j-1)-1023;

//                long intager = (0xffffffffffffffffL << (64 - (long)cur_exp))>>12) ^ longBits;
                String intstring;

                if(binaryString.length()==64){
                    intstring = binaryString.substring(12,12+cur_exp);
                }else {
                    intstring = binaryString.substring(11,11+cur_exp);
                }
//                System.out.println(intstring);
                long cur_int = Long.parseLong(intstring,2);
//                System.out.println(cur_int);

                mantissa_int.add(cur_int);
                if(cur_int >max_int){
                    max_int = cur_int;
                }
                if(cur_int < min_int){
                    min_int = cur_int;
                }
//                System.out.println(cur_exp);
                if(binaryString.length()==64){
                    binaryString = binaryString.substring(12,12+max_precision_v+cur_exp);
                }else {
                    binaryString = binaryString.substring(11,11+max_precision_v+cur_exp);
                }
                mantissa.add(Long.parseLong(binaryString,2));
                encoded_length_all_bit += (max_precision_v);
            }

            encoded_length_all +=(encoded_length_all_bit/8);
//            System.out.println("encoded_length_all");
//            System.out.println(encoded_length_all);


            if (max_exp - min_exp == 0){
//                System.out.println((double)Math.log(max_exp-1023) /(double) Math.log(2));
                encoded_length_all += ((double)Math.log(max_exp-1023) /(double) Math.log(2));
            }

            else{
//                System.out.println((double)Math.log(max_exp-min_exp) /(double) Math.log(2)+1);
                encoded_length_all +=( ( (double)Math.log(max_exp-min_exp) /(double) Math.log(2)+1)*block_size);
            }

//            System.out.println("max_int,min_int:");
//            System.out.println(max_int);
//            System.out.println(min_int);

            if (max_int - min_int == 0)
                encoded_length_all += ((double)Math.log(max_int) /(double) Math.log(2));
            else
                encoded_length_all +=( ( (double)Math.log(max_int-min_int) /(double) Math.log(2)+1/8)*((double)block_size));

            for (int j=0;j<block_size-1;j++) {
                exp.set(j,exp.get(j)-min_exp);
            }

//            System.out.println(binaryString);
        }
//        System.out.println("encoded_length_all");
//        System.out.println(encoded_length_all);
//        double ratio = (double) encoded_length_all / (double) (1025*Double.BYTES);
        double ratio = (double) encoded_length_all / (double) (data.size()*Double.BYTES);
        System.out.println("ratio");
        System.out.println(ratio);
        return encoded_result;
    }

//    private static ArrayList<Byte> SubcolumnEncoder(ArrayList<Double> data, int block_size, int max_precision) {
//        ArrayList<Byte> encoded_result = new ArrayList<Byte>();
//        int length_all = data.size();
//        int encoded_length_all = 0;
//        byte[] length_all_bytes = int2Bytes(length_all);
//        for (byte b : length_all_bytes) encoded_result.add(b);
//        int block_num = length_all / block_size;
//        System.out.println(block_num);
//
//        // encode block size (Integer)
//        byte[] block_size_byte = int2Bytes(block_size);
//        for (byte b : block_size_byte) encoded_result.add(b);
//        int offset = 0;
//        double numberOffset = Math.pow(10,offset);
//        max_precision -= offset;
////        for(int i=0;i<1;i++){
//        for (int i = 0; i < block_num; i++) {
//
//            ArrayList<Double> ts_block = new ArrayList<>();
//            ArrayList<Double> ts_block_raw = new ArrayList<>();
//            double initial = data.get(i * block_size);
//            double min_delta = Double.MAX_VALUE;
//
//            for (int j = 1; j < block_size; j++) {
//                ts_block_raw.add(data.get(j + i * block_size));
//                double cur = numberOffset * data.get(j + i * block_size) - numberOffset * data.get(j+i*block_size-1);
//                if (min_delta > cur) {
//                    min_delta = cur;
//                }
//                ts_block.add(cur);
//            }
////            System.out.println(ts_block_raw);
////            System.out.println(ts_block);
//            for (int j = 0; j < ts_block.size(); j++) {
//                ts_block.set(j, ts_block.get(j) - min_delta);
//            }
////            System.out.println(ts_block_raw);
//            ArrayList<Integer> raw_length = new ArrayList<>(); // length,max_bit_width_interval,max_bit_width_value,max_bit_width_deviation
//            ArrayList<Byte> ts_block_encoded = getEncodeBitsSubColumn(ts_block, block_size, max_precision, offset, raw_length);
//            encoded_length_all += raw_length.get(0);
//        }
////        System.out.println(encoded_length_all);
////        System.out.println(length_all*32);
////        System.out.println((float)encoded_length_all/(block_size*8));
//        System.out.println((float) encoded_length_all / ((float) length_all * 8));
//        return encoded_result;
//    }

    private static ArrayList<Byte> getEncodeBitsSubColumn(ArrayList<Double> ts_block, int block_size, int max_precision, int offset, ArrayList<Integer> raw_length) {
        ArrayList<Byte> ts_block_encoded = new ArrayList<>();
        ArrayList<Integer> ts_block_integer = new ArrayList<>();
        ArrayList<Double> ts_block_decimal = new ArrayList<>();
        int length_bytes = 4;
        int max_int = 0;
//        System.out.println(max_precision);
        for (double data : ts_block) {
            int data_integer = (int) data;
            if (data_integer > max_int) {
                max_int = data_integer;
            }
            double data_float = (data - data_integer);
            ts_block_integer.add(data_integer);
            ts_block_decimal.add(data_float);
        }
        ArrayList<Integer> max_int_bit_width_list = new ArrayList<>();

        int max_int_bit_width = getBitWith(max_int);
        max_int_bit_width_list.add(max_int_bit_width);
        length_bytes += 2;
        byte[] integer_bytes = bitPacking(ts_block_integer, max_int_bit_width);
        for (byte b : integer_bytes) ts_block_encoded.add(b);
        length_bytes += integer_bytes.length;

//        System.out.println(max_precision);
//        System.out.println(ts_block_integer);
//        System.out.println(ts_block_decimal);
//        max_precision = 1;
        for (int i = 0; i < max_precision; i++) {
            max_int = 0;
            for (int j = 0; j < ts_block_decimal.size(); j++) {
                int data_integer = (int) (ts_block_decimal.get(j) * 10);
                if (data_integer > max_int) {
                    max_int = data_integer;
                }
                double data_float = (ts_block_decimal.get(j) * 10 - data_integer);
                ts_block_integer.set(j, data_integer);
                ts_block_decimal.set(j, data_float);
            }
            ArrayList<Integer> outliers = new ArrayList<>();

            GetOutlier(ts_block_integer,outliers);

            max_int_bit_width = getBitWith(max_int);
            max_int_bit_width_list.add(max_int_bit_width);

//            System.out.println(max_int_bit_width);
            length_bytes += 1;
            integer_bytes = bitPacking(ts_block_integer, max_int_bit_width);
            length_bytes += integer_bytes.length;
//            System.out.println(integer_bytes.length);

//            length_bytes += ((outlier.size() * 10) / 8);
//            length_bytes += 5;
            for (byte b : integer_bytes) ts_block_encoded.add(b);
//            System.out.println("outlier_bytes");
//            System.out.println(((outlier.size() * 10) / 8)+5);
        }
//        length_bytes-=((1024*3-80+1024*2-120+1024-60)/8);
//        System.out.println(max_int_bit_width_list);
        raw_length.add(length_bytes);
//        System.out.println(ts_block_integer);
//
//        System.out.println(length_bytes);
//        System.out.println((float)length_bytes/(256*4));
        return ts_block_encoded;
    }

    private static void GetOutlier(ArrayList<Integer> ts_block_integer, ArrayList<Integer> outliers) {
        ArrayList<Integer> elementsCount_list = new ArrayList<>();
        ArrayList<Integer> raw_elementsCount_list = new ArrayList<>();
        for (int key_num=0;key_num<10;key_num++) {
            elementsCount_list.add(0);
        }
        for (int s : ts_block_integer) {
            int count_s = elementsCount_list.get(s);
            count_s ++;
            elementsCount_list.set(s,count_s);
        }
        for (int key_num=0;key_num<10;key_num++) {
            raw_elementsCount_list.add(elementsCount_list.get(key_num));
        }
//            [1910, 1, 5, 21, 2, 4, 29, 3, 8, 65]
//            [1, 2, 3, 4, 5, 8, 21, 29, 65, 1910]
//            [9, 0, 4, 6, 1, 3, 7, 2, 5, 8]
        Collections.sort(elementsCount_list);
//            System.out.println(raw_elementsCount_list);
//            System.out.println(elementsCount_list);
        ArrayList<Integer> hashmap = new ArrayList<>();

        for (int key_num=0;key_num<10;key_num++) {
            int count_s = raw_elementsCount_list.get(key_num);
            for (int key_num2=0;key_num2<10;key_num2++) {
                if(elementsCount_list.get(key_num2) == count_s){
                    hashmap.add(key_num2);
                    break;
                }
            }
        }
//            System.out.println(hashmap);
        for(int m=0;m<ts_block_integer.size();m++){
            int ts_block_bits = hashmap.get(ts_block_integer.get(m));
            if(ts_block_bits < 2){
                outliers.add(m);
            }
            ts_block_integer.set(m,(9-ts_block_bits)%8);
        }
    }
}
