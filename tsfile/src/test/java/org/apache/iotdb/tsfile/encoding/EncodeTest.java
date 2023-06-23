package org.apache.iotdb.tsfile.encoding;

import org.apache.iotdb.tsfile.compress.ICompressor;
import org.apache.iotdb.tsfile.compress.IUnCompressor;
import org.apache.iotdb.tsfile.encoding.decoder.Decoder;
import org.apache.iotdb.tsfile.encoding.encoder.Encoder;
import org.apache.iotdb.tsfile.encoding.encoder.TSEncodingBuilder;
import org.apache.iotdb.tsfile.file.metadata.enums.CompressionType;
import org.apache.iotdb.tsfile.file.metadata.enums.TSDataType;
import org.apache.iotdb.tsfile.file.metadata.enums.TSEncoding;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;

public class EncodeTest {

  public static void main(@NotNull String[] args) throws IOException {
    ArrayList<String> input_path_list = new ArrayList<>();
    ArrayList<String> output_path_list = new ArrayList<>();
    ArrayList<Integer> dataset_map_td = new ArrayList<>();
    input_path_list.add(
        "C:\\Users\\xiaoj\\Documents\\GitHub\\encoding-reorder\\reorder\\iotdb_test_small\\Vehicle-Charge");
    output_path_list.add(
        "C:\\Users\\xiaoj\\Documents\\GitHub\\encoding-reorder\\reorder\\result_evaluation"
            + "\\compression_ratio\\java_ratio\\Vehicle-Charge_ratio.csv");
    dataset_map_td.add(1);

    input_path_list.add(
        "C:\\Users\\xiaoj\\Documents\\GitHub\\encoding-reorder\\reorder\\iotdb_test_small\\EPM-Education");
    output_path_list.add(
        "C:\\Users\\xiaoj\\Documents\\GitHub\\encoding-reorder\\reorder\\result_evaluation"
            + "\\compression_ratio\\java_ratio\\EPM-Education_ratio.csv");
    dataset_map_td.add(1);
    input_path_list.add(
        "C:\\Users\\xiaoj\\Documents\\GitHub\\encoding-reorder\\reorder\\iotdb_test_small\\CS-Sensors");
    output_path_list.add(
        "C:\\Users\\xiaoj\\Documents\\GitHub\\encoding-reorder\\reorder\\result_evaluation"
            + "\\compression_ratio\\java_ratio\\CS-Sensors_ratio.csv");
    dataset_map_td.add(1);
    //    input_path_list.add("C:\\Users\\xiaoj\\Documents\\GitHub\\encoding-reorder\\vldb\\test");
    //    output_path_list.add("C:\\Users\\xiaoj\\Desktop\\ts2diff.csv");
    //    dataset_map_td.add(1);
    input_path_list.add(
        "C:\\Users\\xiaoj\\Documents\\GitHub\\encoding-reorder\\reorder\\iotdb_test_small\\Metro-Traffic");
    output_path_list.add(
        "C:\\Users\\xiaoj\\Documents\\GitHub\\encoding-reorder\\reorder\\result_evaluation"
            + "\\compression_ratio\\java_ratio\\Metro-Traffic_ratio.csv");
    dataset_map_td.add(3600);
    input_path_list.add(
        "C:\\Users\\xiaoj\\Documents\\GitHub\\encoding-reorder\\reorder\\iotdb_test_small\\Nifty-Stocks");
    output_path_list.add(
        "C:\\Users\\xiaoj\\Documents\\GitHub\\encoding-reorder\\reorder\\result_evaluation"
            + "\\compression_ratio\\java_ratio\\Nifty-Stocks_ratio.csv");
    dataset_map_td.add(86400);
    input_path_list.add(
        "C:\\Users\\xiaoj\\Documents\\GitHub\\encoding-reorder\\reorder\\iotdb_test_small\\USGS-Earthquakes");
    output_path_list.add(
        "C:\\Users\\xiaoj\\Documents\\GitHub\\encoding-reorder\\reorder\\result_evaluation"
            + "\\compression_ratio\\java_ratio\\USGS-Earthquakes_ratio.csv");
    dataset_map_td.add(50);
    input_path_list.add(
        "C:\\Users\\xiaoj\\Documents\\GitHub\\encoding-reorder\\reorder\\iotdb_test_small\\Cyber-Vehicle");
    output_path_list.add(
        "C:\\Users\\xiaoj\\Documents\\GitHub\\encoding-reorder\\reorder\\result_evaluation"
            + "\\compression_ratio\\java_ratio\\Cyber-Vehicle_ratio.csv");
    dataset_map_td.add(10);
    input_path_list.add(
        "C:\\Users\\xiaoj\\Documents\\GitHub\\encoding-reorder\\reorder\\iotdb_test_small\\TH-Climate");
    output_path_list.add(
        "C:\\Users\\xiaoj\\Documents\\GitHub\\encoding-reorder\\reorder\\result_evaluation"
            + "\\compression_ratio\\java_ratio\\TH-Climate_ratio.csv");
    dataset_map_td.add(3);
    input_path_list.add(
        "C:\\Users\\xiaoj\\Documents\\GitHub\\encoding-reorder\\reorder\\iotdb_test_small\\TY-Transport");
    output_path_list.add(
        "C:\\Users\\xiaoj\\Documents\\GitHub\\encoding-reorder\\reorder\\result_evaluation"
            + "\\compression_ratio\\java_ratio\\TY-Transport_ratio.csv");
    dataset_map_td.add(5);
    input_path_list.add(
        "C:\\Users\\xiaoj\\Documents\\GitHub\\encoding-reorder\\reorder\\iotdb_test_small\\TY-Fuel");
    output_path_list.add(
        "C:\\Users\\xiaoj\\Documents\\GitHub\\encoding-reorder\\reorder\\result_evaluation"
            + "\\compression_ratio\\java_ratio\\TY-Fuel_ratio.csv");
    dataset_map_td.add(60);
    input_path_list.add(
        "C:\\Users\\xiaoj\\Documents\\GitHub\\encoding-reorder\\reorder\\iotdb_test_small\\GW-Magnetic");
    output_path_list.add(
        "C:\\Users\\xiaoj\\Documents\\GitHub\\encoding-reorder\\reorder\\result_evaluation"
            + "\\compression_ratio\\java_ratio\\GW-Magnetic_ratio.csv");
    dataset_map_td.add(100);

    //    for(int file_i=7;file_i<8;file_i++){
    for (int file_i = 0; file_i < input_path_list.size(); file_i++) {
      String inputPath = input_path_list.get(file_i);
      String Output = output_path_list.get(file_i);
      //      String Output = "C:\\Users\\xiaoj\\Desktop\\test_ratio_ts_2diff.csv";

      // speed
      int repeatTime = 1; // set repeat time
      String dataTypeName = "int"; // set dataType
      //    if (args.length >= 2) inputPath = args[1];
      //    if (args.length >= 3) Output = args[2];

      File file = new File(inputPath);
      File[] tempList = file.listFiles();

      // select encoding algorithms
      TSEncoding[] encodingList = {
        //            TSEncoding.PLAIN ,
        TSEncoding.TS_2DIFF,
        TSEncoding.RLE,
        TSEncoding.SPRINTZ,
        TSEncoding.GORILLA,
        TSEncoding.RLBE,
        TSEncoding.RAKE
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
        //            "Points",
        "Compressed Size",
        "Compression Ratio"
      };
      writer.writeRecord(head); // write header to output file

      assert tempList != null;
      int fileRepeat = 0;
      ArrayList<Integer> columnIndexes = new ArrayList<>(); // set the column indexes of compressed
      for (int i = 0; i < 2; i++) {
        columnIndexes.add(i, i);
      }
      for (File f : tempList) {
        System.out.println(f);
        fileRepeat += 1;
        InputStream inputStream = Files.newInputStream(f.toPath());
        CsvReader loader = new CsvReader(inputStream, StandardCharsets.UTF_8);
        String fileName = f.getAbsolutePath();
        ArrayList<String> data = new ArrayList<>();

        for (int index : columnIndexes) {
          // add a column to "data"
          //        System.out.println(index);
          int max_precision = 0;
          loader.readHeaders();
          data.clear();
          while (loader.readRecord()) {
            String v = loader.getValues()[index];
            //          int ind = v.indexOf(".");
            //          if (ind > -1) {
            //            int len = v.substring(ind + 1).length();
            //            if (len > max_precision) {
            //              max_precision = len;
            //            }
            //          }
            data.add(v);
          }
          //        System.out.println(max_precision);
          inputStream.close();

          switch (dataTypeName) {
            case "int":
              {
                TSDataType dataType = TSDataType.INT32; // set TSDataType
                ArrayList<Integer> tmp = new ArrayList<>();
                for (String value : data) {
                  tmp.add(Integer.valueOf(value));
                }
                // Iterate over each encoding algorithm
                for (TSEncoding encoding : encodingList) {
                  Encoder encoder =
                      TSEncodingBuilder.getEncodingBuilder(encoding).getEncoder(dataType);
                  Decoder decoder = Decoder.getDecoderByType(encoding, dataType);
                  long encodeTime = 0;
                  long decodeTime = 0;

                  // Iterate over each compression algorithm
                  for (CompressionType comp : compressList) {
                    ICompressor compressor = ICompressor.getCompressor(comp);
                    IUnCompressor unCompressor = IUnCompressor.getUnCompressor(comp);

                    double ratio = 0;
                    double compressed_size = 0;

                    long compressTime = 0;
                    long uncompressTime = 0;

                    // repeat many times to test time
                    for (int i = 0; i < repeatTime; i++) {
                      ByteArrayOutputStream buffer = new ByteArrayOutputStream();

                      // test encode time
                      long s = System.nanoTime();
                      for (int val : tmp) {
                        encoder.encode(val, buffer);
                      }

                      //                    byte[] elems = buffer.toByteArray();
                      encoder.flush(buffer);
                      long e = System.nanoTime();
                      encodeTime += (e - s);

                      // test compress time
                      byte[] elems = buffer.toByteArray();
                      s = System.nanoTime();
                      byte[] compressed = compressor.compress(elems);
                      e = System.nanoTime();
                      compressTime += (e - s);

                      // test compression ratio and compressed size
                      compressed_size += compressed.length;
                      double ratioTmp =
                          (double) compressed.length / (double) (tmp.size() * Integer.BYTES);
                      ratio += ratioTmp;

                      // test uncompress time
                      s = System.nanoTime();
                      byte[] x = unCompressor.uncompress(compressed);
                      e = System.nanoTime();
                      uncompressTime += (e - s);

                      // test decode time
                      ByteBuffer ebuffer = ByteBuffer.wrap(buffer.toByteArray());
                      s = System.nanoTime();
                      //                    int i_tmp = 0;
                      //                    while (decoder.hasNext(ebuffer)) {
                      ////                      decoder.readInt(ebuffer);
                      //                      int tmp_tmp = decoder.readInt(ebuffer);
                      ////                      if(tmp.get(i_tmp) == tmp_tmp)
                      ////                        System.out.println("equal");
                      ////                      i_tmp += 1;
                      //                    }
                      e = System.nanoTime();
                      decodeTime += (e - s);

                      buffer.close();
                    }

                    ratio /= repeatTime;
                    compressed_size /= repeatTime;
                    encodeTime /= repeatTime;
                    decodeTime /= repeatTime;
                    compressTime /= repeatTime;
                    uncompressTime /= repeatTime;

                    String[] record = {
                      f.toString(),
                      String.valueOf(index),
                      encoding.toString(),
                      comp.toString(),
                      String.valueOf(encodeTime),
                      String.valueOf(decodeTime),
                      String.valueOf(compressTime),
                      String.valueOf(uncompressTime),
                      //                          String.valueOf(data.size()),
                      String.valueOf(compressed_size),
                      String.valueOf(ratio)
                    };
                    System.out.println(ratio);
                    writer.writeRecord(record);
                  }
                }
                tmp.clear();
                break;
              }
            case "long":
              {
                TSDataType dataType = TSDataType.INT64;
                ArrayList<Long> tmp = new ArrayList<>();
                for (String value : data) {
                  tmp.add(Long.valueOf(value));
                }
                // Iterate over each encoding algorithm
                for (TSEncoding encoding : encodingList) {
                  Encoder encoder =
                      TSEncodingBuilder.getEncodingBuilder(encoding).getEncoder(dataType);
                  Decoder decoder = Decoder.getDecoderByType(encoding, dataType);
                  long encodeTime = 0;
                  long decodeTime = 0;

                  // Iterate over each compression algorithm
                  for (CompressionType comp : compressList) {
                    ICompressor compressor = ICompressor.getCompressor(comp);
                    IUnCompressor unCompressor = IUnCompressor.getUnCompressor(comp);
                    double ratio = 0;
                    double compressed_size = 0;

                    long compressTime = 0;
                    long uncompressTime = 0;
                    for (int i = 0; i < repeatTime; i++) {
                      ByteArrayOutputStream buffer = new ByteArrayOutputStream();

                      // test encode time
                      long s = System.nanoTime();
                      for (long val : tmp) encoder.encode(val, buffer);
                      encoder.flush(buffer);
                      long e = System.nanoTime();
                      encodeTime += (e - s);

                      // test compress time
                      byte[] elems = buffer.toByteArray();
                      s = System.nanoTime();
                      byte[] compressed = compressor.compress(elems);
                      e = System.nanoTime();
                      compressTime += (e - s);

                      // test compression ratio and compressed size
                      compressed_size = compressed.length;
                      double ratioTmp =
                          (double) compressed.length / (double) (tmp.size() * Long.BYTES);
                      ratio += ratioTmp;

                      // test uncompress time
                      s = System.nanoTime();
                      byte[] x = unCompressor.uncompress(compressed);
                      e = System.nanoTime();
                      uncompressTime += (e - s);

                      // test decode time
                      ByteBuffer ebuffer = ByteBuffer.wrap(buffer.toByteArray());
                      s = System.nanoTime();
                      while (decoder.hasNext(ebuffer)) {
                        decoder.readInt(ebuffer);
                      }
                      e = System.nanoTime();
                      decodeTime += (e - s);

                      buffer.close();
                    }

                    ratio /= repeatTime;
                    compressed_size /= repeatTime;
                    encodeTime /= repeatTime;
                    decodeTime /= repeatTime;
                    compressTime /= repeatTime;
                    uncompressTime /= repeatTime;

                    // write info to file
                    String[] record = {
                      f.toString(),
                      String.valueOf(index),
                      encoding.toString(),
                      comp.toString(),
                      String.valueOf(encodeTime),
                      String.valueOf(decodeTime),
                      String.valueOf(compressTime),
                      String.valueOf(uncompressTime),
                      String.valueOf(compressed_size),
                      String.valueOf(ratio)
                    };
                    writer.writeRecord(record);
                  }
                }
                break;
              }
            case "double":
              {
                TSDataType dataType = TSDataType.DOUBLE;
                ArrayList<Double> tmp = new ArrayList<>();
                data.removeIf(String::isEmpty);
                for (String value : data) {
                  tmp.add(Double.valueOf(value));
                }
                // Iterate over each encoding algorithm
                for (TSEncoding encoding : encodingList) {
                  Encoder encoder =
                      TSEncodingBuilder.getEncodingBuilder(encoding).getEncoder(dataType);
                  Decoder decoder = Decoder.getDecoderByType(encoding, dataType);
                  long encodeTime = 0;
                  long decodeTime = 0;

                  // Iterate over each compression algorithm
                  for (CompressionType comp : compressList) {
                    ICompressor compressor = ICompressor.getCompressor(comp);
                    IUnCompressor unCompressor = IUnCompressor.getUnCompressor(comp);
                    long compressTime = 0;
                    long uncompressTime = 0;
                    double ratio = 0;
                    double compressed_size = 0;

                    // repeat many times to test time
                    for (int i = 0; i < repeatTime; i++) {
                      ByteArrayOutputStream buffer = new ByteArrayOutputStream();

                      // test encode time
                      long s = System.nanoTime();
                      for (double val : tmp) encoder.encode(val, buffer);
                      encoder.flush(buffer);
                      long e = System.nanoTime();
                      encodeTime += (e - s);

                      // test compress time
                      byte[] elems = buffer.toByteArray();
                      s = System.nanoTime();
                      byte[] compressed = compressor.compress(elems);
                      e = System.nanoTime();
                      compressTime += (e - s);

                      // test compression ratio and compressed size
                      compressed_size = compressed.length;
                      double ratioTmp =
                          (double) compressed.length / (double) (tmp.size() * Double.BYTES);
                      ratio += ratioTmp;

                      // test uncompress time
                      s = System.nanoTime();
                      byte[] x = unCompressor.uncompress(compressed);
                      e = System.nanoTime();
                      uncompressTime += (e - s);

                      // test decode time
                      ByteBuffer ebuffer = ByteBuffer.wrap(buffer.toByteArray());
                      s = System.nanoTime();
                      int i_de = 0;
                      while (decoder.hasNext(ebuffer)) {
                        double v = decoder.readDouble(ebuffer);
                        //                      if(v!=Double.parseDouble(data.get(i_de))){
                        //                        System.out.println(v);
                        //                        System.out.println(data.get(i_de));
                        //                        System.out.println("noequal");
                        //                        System.out.println(encoding);
                        //                      };
                        //                      i_de++;
                      }
                      e = System.nanoTime();
                      decodeTime += (e - s);

                      buffer.close();
                    }

                    ratio /= repeatTime;
                    compressed_size /= repeatTime;
                    encodeTime /= repeatTime;
                    decodeTime /= repeatTime;
                    compressTime /= repeatTime;
                    uncompressTime /= repeatTime;

                    // write info to file
                    String[] record = {
                      f.toString(),
                      String.valueOf(index),
                      encoding.toString(),
                      comp.toString(),
                      String.valueOf(encodeTime),
                      String.valueOf(decodeTime),
                      String.valueOf(compressTime),
                      String.valueOf(uncompressTime),
                      String.valueOf(compressed_size),
                      String.valueOf(ratio)
                    };
                    writer.writeRecord(record);
                    System.out.println(ratio);
                  }
                }
                break;
              }
            case "float":
              {
                TSDataType dataType = TSDataType.FLOAT;
                ArrayList<Float> tmp = new ArrayList<>();
                data.removeIf(String::isEmpty);
                for (int i = 0; i < data.size(); i++) {
                  tmp.add(Float.valueOf(data.get(i)));
                }

                // Iterate over each encoding algorithm
                for (TSEncoding encoding : encodingList) {
                  Encoder encoder;
                  //                if(encoding == TSEncoding.TS_2DIFF){
                  //                  Map<String, String> props = null;
                  //                  props.put(Encoder.MAX_POINT_NUMBER,
                  // String.valueOf(max_precision));
                  //                  TSEncodingBuilder.Ts2Diff.setMaxPointNumber(max_precision);
                  //                }
                  encoder = TSEncodingBuilder.getEncodingBuilder(encoding).getEncoder(dataType);

                  Decoder decoder = Decoder.getDecoderByType(encoding, dataType);

                  long encodeTime = 0;
                  long decodeTime = 0;
                  // Iterate over each compression algorithm
                  for (CompressionType comp : compressList) {
                    ICompressor compressor = ICompressor.getCompressor(comp);
                    IUnCompressor unCompressor = IUnCompressor.getUnCompressor(comp);
                    long compressTime = 0;
                    long uncompressTime = 0;
                    double ratio = 0;
                    double compressed_size = 0;

                    // repeat many times to test time
                    for (int i = 0; i < repeatTime; i++) {
                      ByteArrayOutputStream buffer = new ByteArrayOutputStream();

                      // test encode time
                      long s = System.nanoTime();
                      for (float val : tmp) {
                        encoder.encode(val, buffer);
                      }
                      encoder.flush(buffer);
                      long e = System.nanoTime();
                      encodeTime += (e - s);

                      // test compress time
                      byte[] elems = buffer.toByteArray();
                      s = System.nanoTime();
                      byte[] compressed = compressor.compress(elems);
                      e = System.nanoTime();
                      compressTime += (e - s);

                      // test compression ratio and compressed size
                      compressed_size += compressed.length;
                      double ratioTmp =
                          (double) compressed.length / (double) (tmp.size() * Float.BYTES);
                      ratio += ratioTmp;

                      // test uncompress time
                      s = System.nanoTime();
                      byte[] x = unCompressor.uncompress(compressed);
                      e = System.nanoTime();
                      uncompressTime += (e - s);

                      // test decode time
                      ByteBuffer ebuffer = ByteBuffer.wrap(buffer.toByteArray());
                      while (decoder.hasNext(ebuffer)) {
                        decoder.readFloat(ebuffer);
                      }
                      e = System.nanoTime();
                      decodeTime += (e - s);

                      buffer.close();
                    }
                    ratio /= repeatTime;
                    compressed_size /= repeatTime;
                    encodeTime /= repeatTime;
                    decodeTime /= repeatTime;
                    compressTime /= repeatTime;
                    uncompressTime /= repeatTime;

                    // write info to file
                    String[] record = {
                      f.toString(),
                      String.valueOf(index),
                      encoding.toString(),
                      comp.toString(),
                      String.valueOf(encodeTime),
                      String.valueOf(decodeTime),
                      String.valueOf(compressTime),
                      String.valueOf(uncompressTime),
                      String.valueOf(compressed_size),
                      String.valueOf(ratio)
                    };
                    writer.writeRecord(record);
                  }
                }
                break;
              }
          }
          inputStream = Files.newInputStream(f.toPath());
          loader = new CsvReader(inputStream, StandardCharsets.UTF_8);
        }
        //        if (fileRepeat > repeatTime) break;
        //      break;
      }
      writer.close();
    }
  }
}