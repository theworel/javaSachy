/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sachy.java.sachy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 *
 * @author User
 * 
 * Abstraktní třída, která obsahuje metody pro zpracovávání uživatelského vstupu na vstup použitelný v metodách konstrukce figur
 */

abstract class Prekladac {
    
    /**
     * Metoda, která ze zkráceného zadání oborů hodnot (např <1,3> místo [1,2,3]) vytvoří úplné zadání oboru hodnot
     * @param vstup řetězec obsahující zkrácené zadání hodnot pro proměnné
     * @return řetězec obsahující úplný zápis těchto hodnot
     */
    public static String vyresIntervaly(String vstup){
        Matcher intervalMatcher = Pattern.compile("(\\(|<)-?\\d+,-?\\d+(\\)|>)").matcher(vstup);
        while(intervalMatcher.find()){
            
            vstup = intervalMatcher.replaceFirst(prevedIntervalNaMnozinu(intervalMatcher.group()));
            intervalMatcher = intervalMatcher.pattern().matcher(vstup);
            
        }
        return vstup;
    }
    
    /**
    * Metoda, která pro nějaký konkrétní zápis intervalu vrátí jeho zápis jako posloupnost
    * @param group interval pro převedení na dlouhý zápis
    * @return dlouhý zápis intevalu
    */
    private static String prevedIntervalNaMnozinu(String group) {
        Matcher cisloMatcher = Pattern.compile("-?\\d+").matcher(group);
        cisloMatcher.find();
        int a = Integer.parseInt(cisloMatcher.group());
        cisloMatcher.find();
        int b = Integer.parseInt(cisloMatcher.group());
        
        
        if (Math.abs(a-b)==1 && group.charAt(0)=='(' && group.charAt(group.length()-1)==')')
            return "[404]";
        
        if(a==b && (group.charAt(0)=='(' || group.charAt(group.length()-1)==')'))
            return"[404]";
        StringBuilder ret = new StringBuilder("[");
        if (a >= b){
            for(int i = b; i<a; i++)
                ret.append(i).append(",");
            ret.append(a).append("]");
            
        }else{
            for(int i = a; i<b; i++)
                ret.append(i).append(",");
            ret.append(b).append("]");
        }
       
        
        Matcher cisliceMatcher = Pattern.compile("-?\\d+").matcher(ret);
        cisliceMatcher.find();
        if (group.charAt(0)=='(')//[0,1,2]
                ret.delete(cisliceMatcher.start(), cisliceMatcher.end()+1);
        
        cisliceMatcher.find(ret.length()-3);
        if (group.charAt(group.length()-1)==')')//[1,2]
            ret.delete(cisliceMatcher.start()-1, cisliceMatcher.end());//[1]
        
        
        return ret.toString();
    }
    /**
     * Metoda, která ke vstupnímu řetězci po jednom připojuje řetězce ze seznamu
     * @param zaklad řetězec, k kterému budou ostatní řetězce připojovány
     * @param pridavane seznam řetězce, které budou připojeny k původnímu řetězci
     * @return seznam všech řetězců vzniklých připojením nějakého jednoho řetězce ze seznamu pridavane k řetězci zaklad
     */
    public static Stream<String> expand(String zaklad, List<String> pridavane){
        ArrayList<String> ret = new ArrayList<>();
        for (String s:pridavane){
            ret.add(zaklad+" "+s);
        }
        
        return ret.stream();  
    }
    
    /*public static List<String> rozsirPodlePodminek(String podminkyString){
        
        Matcher hodnotyMatcher = Pattern.compile("[\\|a-z]+").matcher(podminkyString);
        
        Matcher poleMatcher = Pattern.compile("\\(\\$?-?\\d+ \\$?-?\\d+\\)").matcher(podminkyString);
        //String[] hodnoty = podminkyString.split("\\|");
        ArrayList<String> vsechnaReseni = new ArrayList<>();
        if(poleMatcher.find()){
            hodnotyMatcher.find();
             
             String[] hodnoty = hodnotyMatcher.group().split("\\|");
             
             for (String s:hodnoty){
                 ArrayList<String> castecnaReseni = new ArrayList<>();
                 
                 castecnaReseni.add(poleMatcher.group() +" "+s);
                 
                 Stream<String> castecnaReseniStream = castecnaReseni.stream();
                 
                 castecnaReseniStream = castecnaReseniStream.flatMap(cr -> expand(cr, rozsirPodlePodminek(podminkyString.substring(hodnotyMatcher.end()))));
                 
                 
                 vsechnaReseni.addAll(castecnaReseniStream.collect(Collectors.toList()));
             }
            return vsechnaReseni;
        }
        return Arrays.asList(new String());
    }
    
    public static String rozsirPodminku(String vstup){
        String ret = "";
        Matcher slozenaPodminkaMatcher = Pattern.compile("\\(\\$?-?\\d+ \\$?-?\\d+\\) [a-z]+\\|[a-z\\|]+").matcher(vstup);
        
        if(slozenaPodminkaMatcher.find()){
            String predpona = vstup.substring(0,slozenaPodminkaMatcher.start()+slozenaPodminkaMatcher.group().lastIndexOf(' '));
            String pripona = vstup.substring(slozenaPodminkaMatcher.end());
            String[] moznosti = slozenaPodminkaMatcher.group().substring(slozenaPodminkaMatcher.group().lastIndexOf(' ')).split("\\|");
            for (String m:moznosti){
                ret += "{" + predpona+m+pripona +"}";
            }
        }
        return ret;
    }
    
    /*public static String rozsirEfekt(String vstup){
        String ret = "";
        Matcher slozenyEfektMatcher = Pattern.compile("\\w+|[\\|\\w]+ -> \\(\\$?-?\\d+ \\$?-?\\d+\\)").matcher(vstup);
        
        if(slozenyEfektMatcher.find()){
            String predpona = vstup.
        }
    }*/
    
    /**
     * Metoda, která z řetězce obsahujícího více možností pro nějakou hodnotu vytvoří všechny jím popsané jednoznačné řetězce
     * @param group řetězec s více hodnotami na místě, kde by měla být jen jedna hodnota
     * @return všechny možné řetězce popsané vstupním řetězcem
     */
    public static List<String> rozsirTahString(String group) {
        ArrayList<String> ret = new ArrayList<>();
        
        Matcher radaMoznostiMatcher = Pattern.compile("[a-z]+?\\|[\\|a-z]+").matcher(group);
        
        if(radaMoznostiMatcher.find()){
            
            String[] moznosti = radaMoznostiMatcher.group().split("\\|");
            for (String moznost:moznosti){
                ArrayList<String> castecnaReseni = new ArrayList<>();
                castecnaReseni.add(radaMoznostiMatcher.replaceFirst(moznost));
                
                Stream<String> castecnaReseniStream = castecnaReseni.stream();
                
                castecnaReseniStream = castecnaReseniStream.flatMap(cr -> rozsirTahString(cr).stream());
                
                ret.addAll(castecnaReseniStream.collect(Collectors.toList()));
                
            }
            return ret;
        }
        ret.add(group);
        return ret;
    }
    /**
     * Metoda, která  z řtězce proměnných získá názvy proměnných v takovém pořadí, v jakém byly uvedeny v řetězci
     * @param promenneString Řetězec popisující proměnné, ve formnátu [název] in [hodnoty] [název] in [hodnoty]...
     * @return seznam názvů proměnných
     */
    public static ArrayList<String> promennePoporade(String promenneString){
        
       
        
        Matcher promennaMatcher = Pattern.compile("\\w+ in (\\[.*?\\]|(\\(|<).*?(>|\\)))").matcher(promenneString);
        
        ArrayList<String> ret = new ArrayList<>();
        while(promennaMatcher.find()){
            ret.add(promennaMatcher.group().substring(0,promennaMatcher.group().indexOf(" ")));
        }
        
        
        return ret;
    }
    /**
     * Metoda ke spočítání výsledku aritmetického výrazu
     * @param vyraz řetězec obsahující aritmetický výraz
     * @return hodnota tohoto výrazu
     * @throws Exception 
     */
    public static int spocitej(String vyraz) throws Exception{
        boolean poOtvirajiciZavorce = true;
        
        
        int i = 0;
        int priorita = 0;
        int nejnizsiPriorita = Integer.MAX_VALUE;
        int indexOperatoruSNejnizsiPrioritou = -1;
        while (i<vyraz.length()){
            switch (vyraz.charAt(i)){
                case ')':
                    
                    priorita -=1;
                    i++;
                    break;
                case '(':
                    poOtvirajiciZavorce = true;
                    priorita +=1;
                    i++;
                    break;
                case ' ':
                    i++;
                    break;
                case '+':
                case '-':
                    
                    if ((priorita <=nejnizsiPriorita) && (!poOtvirajiciZavorce)){
                        indexOperatoruSNejnizsiPrioritou = i;
                        nejnizsiPriorita = priorita;
                                
                    }
                    poOtvirajiciZavorce = false;
                    i++;
                    break;
                case '*':
                case '/':
                    if (priorita < nejnizsiPriorita){
                        indexOperatoruSNejnizsiPrioritou = i;
                        nejnizsiPriorita = priorita;
                    }
                    i++;
                    break;
                case 'e':
                    poOtvirajiciZavorce = true;
                    i++;
                    break;
                default:
                    poOtvirajiciZavorce = false;
                    i++;
                    break;
            }
        }
        if (indexOperatoruSNejnizsiPrioritou == -1)
            return Integer.parseInt(vyraz);
        else{
            switch (vyraz.charAt(indexOperatoruSNejnizsiPrioritou)){
                case '+':
                    return spocitej(vyraz.substring(0, indexOperatoruSNejnizsiPrioritou)) + spocitej(vyraz.substring(indexOperatoruSNejnizsiPrioritou+1));
                case '-':
                    return spocitej(vyraz.substring(0, indexOperatoruSNejnizsiPrioritou)) - spocitej(vyraz.substring(indexOperatoruSNejnizsiPrioritou+1));
                case '*':
                    return spocitej(vyraz.substring(0, indexOperatoruSNejnizsiPrioritou)) * spocitej(vyraz.substring(indexOperatoruSNejnizsiPrioritou+1));
                case '/':
                    return spocitej(vyraz.substring(0, indexOperatoruSNejnizsiPrioritou)) / spocitej(vyraz.substring(indexOperatoruSNejnizsiPrioritou+1));
                default:
                    throw new Exception();
            }
        }
        
    }
    /**
     * Metoda, která pro daný řetězec proměnných vráti všechny možné kombinace těchto proměnných jako seznam seznamů
     * @param promenneString řetězec popisující proměnné, ve formátu [název] in [hodnoty] [název] in [hodnoty]
     * @return 
     */
    public static List<ArrayList<Integer>> vsechnyKombinace(String promenneString){
        
        
        
        promenneString = vyresIntervaly(promenneString);
        
       
        Matcher promennaMatcher = Pattern.compile("\\w+ in \\[.*?\\]").matcher(promenneString);
        //Pattern hodnotyPattern = Pattern.compile("\\[*.\\]");
        if (promennaMatcher.find()){
            String[] hodnotyS=promennaMatcher.group().substring(promennaMatcher.group().indexOf('[')+1,promennaMatcher.group().indexOf(']')).split(",");
            IntStream hodnoty = Arrays.stream(hodnotyS).mapToInt((String s)->{
                return Integer.parseInt(s.trim());
            }).distinct();
            List<ArrayList<Integer>> vsechnaReseni = new ArrayList<>();
            
            
            int[] h = hodnoty.toArray();
            
            for (int hodnota:h){
                List<ArrayList<Integer>> castecnaReseni = new ArrayList<>();
                
                ArrayList<Integer> a = new ArrayList<>();
                a.add(hodnota);
                castecnaReseni.add(a);
                String moznost = promenneString.substring(promennaMatcher.end()).replaceAll(promennaMatcher.group().substring(0,promennaMatcher.group().indexOf(" ")), hodnota+"");
                 
               Stream<ArrayList<Integer>> castecnaReseniStream = castecnaReseni.stream();
               castecnaReseniStream = castecnaReseniStream.flatMap(s->expand(s, vsechnyKombinace(moznost)).stream());
               vsechnaReseni.addAll(castecnaReseniStream.collect(Collectors.toList()));
            }
            
            return vsechnaReseni;
            
            
            
        }
        
        return Arrays.asList(new ArrayList<>());
    }
    /**
     * Funkce, která k zadané posloupnosti čísel připojí po jedné všechny posloupnosti ze zadaného seznamu posloupností a vrátí všechny takto vzniklé posloupnosti
     * @param a posloupnost, kterou budou všechny výsledné posloupnosti začínat
     * @param hodnoty seznam posloupností, které mohou být připojeny k posloupnosti a
     * @return všechny posloupnosti, které mohou vzniknout připojením nějaké posloupnosti ze seznamu hodnoty k posloupnosti a  
     */
    private static ArrayList<ArrayList<Integer>> expand(ArrayList<Integer> a, List<ArrayList<Integer>> hodnoty){
        ArrayList<ArrayList<Integer>> ret = new ArrayList<>();
        
        hodnoty = hodnoty.stream().distinct().collect(Collectors.toList());
       
        for(ArrayList<Integer> h:hodnoty){
            ArrayList<Integer> b = new ArrayList<>();
            b.addAll(a);
            b.addAll(h);
            
            ret.add(b);
        }
        
        return ret;
    }

    
}
