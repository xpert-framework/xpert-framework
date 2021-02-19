/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xpert.core.exception;

import java.util.Arrays;

/**
 *
 * @author ayslanms
 */
public class TesteExcpetion {
    
    public static void main(String[] args) {
        
        BusinessException ex = new BusinessException();
        ex.add("bla 2");
        ex.add("bla 3");
        
        System.out.println("getMessagesList()");
        System.out.println(ex.getMessagesList());
        System.out.println("ex.getMessagesList(true)");
        System.out.println(ex.getMessagesList(true));
        System.out.println("ex.getMessages()");
        System.out.println(Arrays.toString(ex.getMessages()));
        
    }
    
}
