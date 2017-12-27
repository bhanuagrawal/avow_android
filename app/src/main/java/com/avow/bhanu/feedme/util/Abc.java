package com.avow.bhanu.feedme.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by bhanu on 3/9/17.
 */

public class Abc {




    public static void main(String args[]) {



        String[] sArray = {"a", "b", "c", "c", "c", "d", "e", "e"};
        int burst=3;

        ArrayList<String> reArr = new ArrayList<>();

        boolean shoudContinue = true;
        do{
            shoudContinue = false;
            for(int i=0; i<sArray.length; i++){


                boolean repeating = true;
                for(int j=0; j<burst-1; j++){
                    if(sArray[j] != sArray[j+1]){
                        repeating = false;
                        break;
                    }
                }

                if(repeating){
                    i+= burst-1;

                    shoudContinue = true;


                }
                else {
                    reArr.add(sArray[i]);
                }


            }
        }while (shoudContinue);

        System.out.println(reArr.toString());



    }



}
