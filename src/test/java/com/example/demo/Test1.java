package com.example.demo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Test1 {

    public static void main(String[] args) {
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        list.add(3);
        Iterator<Integer> iterator = list.iterator();
        while (iterator.hasNext()) {
            iterator.next();
            iterator.remove();
        }
        System.out.println(list.size());


        Integer i =-128;
        Integer j = -128;
        System.out.println(i == j);
        i =127;
        j = 127;
        System.out.println(i == j);
        i =128;
        j = 128;
        System.out.println(i == j);

        test(1);
        test(128);
    }

    public static void test(int i){
        System.out.println("================================");
        Integer i1 = new Integer(i);
        Integer i2 = new Integer(i);
        System.out.println("i1 == i2 : " + (i1 == i2));
        System.out.println("i1.equals(i2) : " + (i1.equals(i2)));

        System.out.println("new Integer(i) == new Integer(i) : " + (new Integer(i) == new Integer(i)));

        System.out.println("Integer.valueOf(i) == Integer.valueOf(i) : " + (Integer.valueOf(i) == Integer.valueOf(i)));

        Integer i3 = Integer.valueOf(i);
        Integer i4 = Integer.valueOf(i);
        System.out.println("i3 == i4 : " + (i3 == i4));
    }
}
