package com.example.connection;

/**
 * @author Yi-27
 * @create 2020-11-05 22:07
 */
public class Test {

    static int i = 10;

    static {
        i = 20;
    }

    public static void main(String[] args) {
        System.out.println(i);
    }

}
class A{
    public int a = 10;
    public void method(){
        System.out.println("a = " + a);
    }
}
class B extends A{
    public int a = 20;
    public void method(){
        System.out.println("a = " + a);
    }
}
