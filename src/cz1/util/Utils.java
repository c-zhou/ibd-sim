package cz1.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;


public class Utils {

    public static BufferedReader getBufferedReader(String path) throws IOException {
         BufferedReader br = null;
                                 
         if(path.endsWith(".gz")){
             InputStream inputFileStream = new FileInputStream(path);
             br = new BufferedReader(new InputStreamReader(
            		 new GZIPInputStream(inputFileStream)), 65536);
         }else{
             br = new BufferedReader(new FileReader(path), 65536);
         }
         return br;
    }

    public static BufferedWriter getBufferedWriter(String path) throws IOException {
        return new BufferedWriter(new FileWriter(new File(path)));
    }

    public static BufferedWriter getGZIPBufferedWriter(String path) throws IOException {
        return new BufferedWriter(new OutputStreamWriter(new 
                    GZIPOutputStream(new FileOutputStream(path))));
    }
    
    public static String getSystemTime(){
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").
            format(Calendar.getInstance().getTime());
    }

    public static void println() {
        System.out.println();
    }

    public static void print(char character) {
        System.out.print(character);
    }

    public static void println(char character) {
        print(character+"\n");
    }
     
    public static void print(String message) {
        System.out.print(message);
    }

    public static void println(String message) {
        print(message+"\n");
    }

    public static void print(ArrayList<Integer> array) {
        for(int i=0; i<array.size(); i++)
            print(array.get(i)+"\t");
        println();
    }

    public static void print(int[][] matrix) {
        for(int i=0; i<matrix.length; i++)
            print(matrix[i]);
    }


    public static void print(int[] array) {
        for(int i=0; i<array.length; i++)
            print(array[i]+"\t");
        println();
    }

    public static void print(double[] array) {
        for(int i=0; i<array.length; i++)
            print(array[i]+"\t");
        println();
    }

    public static void print(double[][] matrix) {
        for(int i=0; i<matrix.length; i++)
            print(matrix[i]);
    }

    public static void print(long[] array) {
        for(int i=0; i<array.length; i++)
            print(array[i]+"\t");
        println();
    }
}
