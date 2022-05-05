/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sachy.java.sachy;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

/**
 *
 * @author User
 */
public class JavaSachy {
    
    /**
     * @param args the command line arguments

     */
    public static void main(String[] args) {
        //Figura f = new Figura("A {-promenne a in [1,2,3] b in <0,a> -pole (a b) -podminky -ee -fc}" );
        //System.out.println(f.toString());
        
        //System.out.println(Prekladac.rozsirTahString("-pole (1 0) -podminky (1 0) prazdny (0 0) =($6 0) -ee j|r|q|n -> (1 0) -fc"));
        Sachovnice s = new Sachovnice();
        if(args.length==0){
            System.out.println("Zadávejte figury řádek po řádku, pro ukončení zadávání figur a zadání pozice vložte na vstup string END");
            
        
            Scanner sc = new Scanner(System.in);
            String radek = sc.nextLine();
            while (!radek.equals("END")){
                try{
                    Figura f = new Figura(radek);
                    Sachovnice.figuryStrings.put(f.getZnak(), f);
                    //System.out.println(f.toString());
                    }catch (ZpracovaniFiguryException e){
                        System.out.println("Zápis figury se nepodařilo zpracovat");
                    }
            
                    radek = sc.nextLine();
                }
                System.out.println("Pokud chcete hledat vítězné řešení pozice, zadejte VYRES , pokud chcete najít všechny tahy ze zadané pozice, zadajte TAHY");
                String vstup = sc.nextLine();
                while(!(vstup.equals("VYRES")||vstup.equals("TAHY"))){
                    System.out.println("Chybný vstup, zkuste to prosím znovu");
                    vstup = sc.nextLine();
                }
        
            while(!vstup.equals("END")){

                System.out.println("Zadejte šachovnici řádek po řádku, od řádku 8 po řádek 1, figury zadávejte jejich znakem, prázdná pole zadávejte znakem .");
        
                for(int i =7; i>=0;i--){
                    try {
                        s.upravRadek(sc.nextLine(), i);
                    } catch (Exception ex) {
                        System.out.println("Nesprávný zápis řádku");
                        i++;
                    }
                }
        
                System.out.println(" ");
            


            //System.out.println(s.vykresliSe());
        
        
                if(vstup.equals("VYRES")){
                    if(!s.obsahujeObaKrale()){
                        System.out.println("Šachovnice neobsahuje krále, nelze jí řešit jako úlohu");
                        continue;
                    }
                    System.out.println("Zadejte do kolika tahů má program pozici řešit");
                    int tahy =-1;
                    while(tahy==-1){
                        try{
                            tahy = Integer.parseInt(sc.nextLine());
                        }catch(NumberFormatException e){
                            System.out.println("Nesprávný vstup, zkuste to prosím znovu");
                        }
                    }
                    ArrayList<Sachovnice> reseni = s.vyres(tahy).getPozice();
                    if(reseni.isEmpty())
                        System.out.println("Zadaná úloha nemá řešení");
                    else{
                        for (Sachovnice naslednik:reseni){
                            System.out.println(naslednik.vykresliSe());
                        }
                    }
                }else{
                    if(vstup.equals("TAHY")){
                        while(true){
                            System.out.println("Zadejte BILY, pokud hledáte všechny tahy bílého hráče, zadejte CERNY, pokud hledáte všechny tahy černého hráče");
                            String barva = sc.nextLine();
                            if(barva.equals("BILY")){
                                ArrayList<Sachovnice> vsechnyPristi = s.najdiVsechnyBudouciPozice(true);
                                if(vsechnyPristi.isEmpty())
                                    System.out.println("V této pozici daný hráč nemá žádný platný tah");
                                else{
                                    for(Sachovnice pristi:vsechnyPristi)
                                        System.out.println(pristi.vykresliSe());
                                    break;
                                }
                            }
                            if(barva.equals("CERNY")){
                                ArrayList<Sachovnice> vsechnyPristi = s.najdiVsechnyBudouciPozice(true);
                                if(vsechnyPristi.isEmpty())
                                    System.out.println("V této pozici daný hráč nemá žádný platný tah");
                                else{
                                    for(Sachovnice pristi:vsechnyPristi)
                                        System.out.println(pristi.vykresliSe());
                                    break;
                                }
                            }
                            System.out.println("Nesprávný vstup, prosím zkuste to znovu");
                    }
                }
            }
        }
        }else{
            
            int argi = 0;
            //Pattern figuraStringPtrn = Pattern.compile("\\w+\\{.*\\}[ \\w+]?");
            while (args[argi].matches("\\w+\\{.*\\}[ \\w+]*")){
                try{
                    Figura f = new Figura(args[argi]);
                    Sachovnice.figuryStrings.put(f.getZnak(), f);
                    //System.out.println(f.toString());
                    }catch (ZpracovaniFiguryException e){
                        System.out.println("Zápis figury se nepodařilo zpracovat");
                    }
            
                    argi++;
                }
                for(int radek =7; radek>=0;radek--){
                try {
                        s.upravRadek(args[argi].replaceAll(";", " "),radek);
                        argi++;
                    } catch (Exception ex) {
                        
                        System.out.println("Nesprávný zápis řádku");
                        System.exit(1);
                    }
            }
            System.out.println(s.vykresliSe());
            if(args[args.length-1].matches("r\\d+")){
            
                ArrayList<Sachovnice> reseni = s.vyres(Integer.parseInt(args[args.length-1].substring(1))).getPozice();
                    if(reseni.isEmpty())
                        System.out.println("Zadaná úloha nemá řešení");
                    else{
                        for (Sachovnice naslednik:reseni){
                            System.out.println(naslednik.vykresliSe());
                        }
                    }
                }
            else{
                switch(args[args.length-1]){
                    case "tb":
                         ArrayList<Sachovnice> vsechnyPristiB = s.najdiVsechnyBudouciPozice(true);
                                if(vsechnyPristiB.isEmpty())
                                    System.out.println("V této pozici daný hráč nemá žádný platný tah");
                                else{
                                    Iterator<Sachovnice> it = vsechnyPristiB.iterator();
                                    while(it.hasNext())
                                        System.out.println(it.next().vykresliSe());
                                    
                                }
                        break;
                    case "tc":
                           ArrayList<Sachovnice> vsechnyPristiC = s.najdiVsechnyBudouciPozice(false);
                                if(vsechnyPristiC.isEmpty())
                                    System.out.println("V této pozici daný hráč nemá žádný platný tah");
                                else{
                                    for(Sachovnice pristi:vsechnyPristiC)
                                        System.out.println(pristi.vykresliSe());
                                    
                                }
                        break;
                   
                    default:
                          System.out.println("chybné zadání");
                    }
                
            }
        }
        
    }
}
