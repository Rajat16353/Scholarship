/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dbms.Scholarship;

import java.util.Random;

/**
 *
 * @author gupta
 */
public class Captcha {

    public Captcha() {
    }
    
    public String captchaGenerator()
    {
        int s;
        String captcha="";
        Random ran=new Random();
        String random="abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        for(int i=0;i<6;i++)
        {
            s=ran.nextInt(62);
            captcha=captcha+random.charAt(s);
        }
        return captcha;
        //System.out.println(""+captcha);
    }
}
