package com.example.blob;

import org.omg.CORBA.Object;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: Yi-27
 * @projectName: LearnJDBC
 * @create: 2020-11-07 11:01
 * @Description:
 */
public class Abc {
    public static void main(String[] args) throws InterruptedException {

        D b = new B();


    }
}

class A{

    public void eat(){

    }
}

class B implements C, D{

    public void sleep(){

    }

    @Override
    public void CC() {

    }

    @Override
    public void DD() {

    }
}

interface C{
    void CC();
}

interface D{
    void DD();
}