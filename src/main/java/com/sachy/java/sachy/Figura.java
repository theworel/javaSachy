/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sachy.java.sachy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author User
 * 
 * Reprezentuje druh figury na šachovnici. Na šachovnici se může nacházet více kamenů stjného druhu figury
 */
public class Figura {

    
    
    
   
    /**
     * Funkce, která z jednoho řetězce popisujícího určité tahy figury vytvoří příslušné objekty třídy Tah
     * @param tahString řetězec popisující tah/tahy, ve formátu -promenne [název] in [hodnoty] [název] in [hodnoty]... -pole [řetězec cílového pole] -podminky [řetězce podmínek] -ee [řetězce speciálních efektů tahu]
     * @return seznam tahů popsaných vstupním řetězcem
     */
    private List<Tah> zpracujTahy(String tahString) throws ZpracovaniFiguryException {//tah String je String s proměnnými, je třeba jej rozdělit na více stringů pro každoui možnou kombinaci proměnných v něm
        ArrayList<String> promenne;
        ArrayList<Tah> ret = new ArrayList<>();
        
        if (tahString.contains("-promenne")){
            promenne = Prekladac.promennePoporade(tahString.substring(tahString.indexOf("-promenne")+9, tahString.indexOf("-pole")));
           
            
            List<ArrayList<Integer>>vsechnyKombinace = Prekladac.vsechnyKombinace(tahString.substring(tahString.indexOf("-promenne")+9, tahString.indexOf("-pole")));
            
            for (ArrayList<Integer> kombinace:vsechnyKombinace){
                if(kombinace.size() == promenne.size()){
                int it = 0;
                String specificTahString = tahString;
                for(String promena:promenne){
                    
                    
                    specificTahString = specificTahString.replaceAll("\\("+promena+" ", "\\("+kombinace.get(it).toString()+" ");
                    specificTahString = specificTahString.replaceAll(" "+promena+"\\)", " "+kombinace.get(it)+")");
                    specificTahString = specificTahString.replaceAll("\\(-"+promena+" ", "\\(-"+kombinace.get(it).toString()+" ");
                    specificTahString = specificTahString.replaceAll(" -"+promena+"\\)", " -"+kombinace.get(it)+")");
                    specificTahString = specificTahString.replaceAll("\\(\\$"+promena+" ", "\\(\\$"+kombinace.get(it).toString()+" ");
                    specificTahString = specificTahString.replaceAll(" \\$"+promena+"\\)", " \\$"+kombinace.get(it)+")");
                    specificTahString = specificTahString.replaceAll("\\(\\$-"+promena+" ", "\\(\\$-"+kombinace.get(it).toString()+" ");
                    specificTahString = specificTahString.replaceAll(" \\$-"+promena+"\\)", " \\$-"+kombinace.get(it)+")");
                    specificTahString = specificTahString.replaceAll("--", "");
                    
                    it += 1;
                }
                
                
                Pattern vyrazPattern = Pattern.compile("-?[0-9]+[-+*/][-+*/0+9]+");
                Matcher vyrazMatcher=vyrazPattern.matcher(specificTahString);
                    
                while (vyrazMatcher.find()){
                    try {
                        vyrazMatcher.replaceFirst(""+Prekladac.spocitej(vyrazMatcher.group()));
                    } catch (Exception ex) {
                        throw new ZpracovaniFiguryException();
                    }
                     
                }
                
                
                
                String podminkyString = specificTahString.substring(specificTahString.indexOf("-podminky")+9, specificTahString.indexOf("-ee"));

                String efektyString = specificTahString.substring(specificTahString.indexOf("-ee")+3);
                    
                
 
                String poleString = specificTahString.substring(specificTahString.indexOf("-pole")+5, specificTahString.indexOf("-podminky"));
    
                
                
                 ret.add(new Tah(poleString, podminkyString, efektyString));
                }
                
            
            }
        final ArrayList<Tah> fret = new ArrayList<>();
        fret.addAll(ret);
       
        
        Stream<Tah> retStream = ret.stream().map(t->spojStejne(t, fret)).distinct();
        ret = (ArrayList<Tah>) retStream.collect(Collectors.toList());
        
        ArrayList<Tah> jedinecne = new ArrayList<>();
        for(Tah t:ret){
            if(!jedinecne.stream().anyMatch(j->(j.getCilovePole().equals(t.getCilovePole()) && j.getEfekty().containsAll(t.getEfekty()) && t.getEfekty().containsAll(j.getEfekty())))){
                jedinecne.add(t);
            }
            
            
        }
        
        return jedinecne;
        }else{
            String poleString = tahString.substring(0, tahString.indexOf("-podminky"));
            String efektyString = tahString.substring(tahString.indexOf("-ee")+3);
            String podminkyString = tahString.substring(tahString.indexOf("-podminky")+9, tahString.indexOf("-ee"));
                    
            
            ret = new ArrayList<>();
            ret.add(new Tah(poleString, podminkyString, efektyString));
        }
        
        
        return ret;
        
        
    }
   
    
    /**
     * Funkce, která sjednotí tahy, které se liší jen podmínkami, tedy mají stejný efekt na hru.
     * Je volána na konci funkce zpracujTahy, sjednotí tedy podmínky tahu, který byl zadán jedním řetězcem
     * @param t Tah, ke kterému budou jiné tahy připojovány
     * @param ostatni Seznam tahů, které budou připojovány k tahu T
     * @return Tah, který má všechny podmínky tahů, které mají stejný dopad na pozici
     */
    private Tah spojStejne(Tah t, ArrayList<Tah> ostatni) {
        for (Tah tah:ostatni){
            if (t.getAbsCil(true,0,0)[0] == tah.getAbsCil(true,0, 0)[0] && t.getAbsCil(true,0, 0)[1] == tah.getAbsCil(true,0, 0)[1] && t.getEfekty().containsAll(tah.getEfekty()) && tah.getEfekty().containsAll(t.getEfekty())){
                
                t.pripoj(tah);
            }
        }
        return t;
    }

   
    
    /**
     * Třída reprezentující tah figury na určité pole
     */
    public class Tah{

        
        /**
         * Třída reprezentující pole na šachovnici z pohledu táhnoucí figury. 
         * Objekt třídy Figura neví, na jakých souřadnicích na šachovnici stojí, musí mu to být předáno jako argument, objekt Pole podle toho určí své absolutní souřadnice
         */
        private class Pole{
            /**
             *souřadnice řádku, absolutní nebo relativní podle proměnné radekRelative
             */
            private int cilRadek;
            /**
             *souřadnice sloupce, absolutní nebo relativní podle proměnné sloupecRelative
             */
            private int cilSloupec;
            /**
             * proměnná říká, jestli je cilRadek tohoto Pole relativní nebo absolutní souřadnicí
             */
            private boolean radekRelative;
            /**
             * proměnná říká, jestli je cilSloupec tohoto Pole relativní nebo absolutní souřadnicí
             */
            private boolean sloupecRelative;
            /**
             * 
             * @param poleString řetězec ve formátu (a b), kde a i b jsou čísla. Můž jim předcházet znak $, který znamená, že následující souřadnice je absolutní
             */
            public Pole(String poleString){//vstup - jeden string ve formátu (a b), kde a i b jsou čísla, která mohou být předcházena znakem $, který znamená, že jde o absolutní adresu pole, jinak jde o relativní adresu vzhledem k pozici příslušné figury
               
                Matcher cisloMatcher = Pattern.compile("-?\\d+").matcher(poleString);
                cisloMatcher.find();
                radekRelative = !(poleString.charAt(cisloMatcher.start()-1)=='$');
                cilRadek = Integer.parseInt(cisloMatcher.group());
                cisloMatcher.find();
                sloupecRelative = !(poleString.charAt(cisloMatcher.start()-1)=='$');
                cilSloupec = Integer.parseInt(cisloMatcher.group());
                    
                
            }
            
            @Override
            public boolean equals(Object o){
                Pole druhy;
                if(o.getClass() == this.getClass()){
                    druhy = (Pole) o;
                    return this.toString().equals(druhy.toString());
                }
                return false;
            }

            @Override
            public int hashCode(){
                return super.hashCode();
                }
            /**
             * 
             * @param bily barva kamene, k jehož tahu přísluší toto pole
             * @param radekFigury řádek kamene, k jehož tahu přísluší toto pole
             * @param sloupecFigury sloupec kamene, k jehož tahu přísluší toto pole
             * @return absolutní souřadnice, ke kterým se vztahuje toto pole pro konkrétní barvu a pozici kamene, k jehož tahu přísluší
             */
            public int[] absolutniAdresa(boolean bily, int radekFigury, int sloupecFigury){
                int[] ret = new int[2];
                if(bily){
                    ret[0]=cilRadek;
                    ret[1]=cilSloupec;
                }else{
                    if(radekRelative)
                        ret[0]=-cilRadek;
                    else
                        ret[0]=7-cilRadek;
                    if(sloupecRelative)
                        ret[1]=-cilSloupec;
                    else
                        ret[1]=7-cilSloupec;
                }
                
                if(radekRelative)
                    ret[0]+=radekFigury;
                if (sloupecRelative)
                    ret[1]+=sloupecFigury;
                
                
                
                    
                
                return ret;
            }
            @Override
            public String toString(){
                String ret = "(";
                
                if (!radekRelative)
                    ret = ret + "$";
                ret = ret + this.cilRadek +" ";
                
                if(!sloupecRelative)
                    ret = ret + "$";
                ret = ret + this.cilSloupec + ")";
                return ret;
            }
            
            
        }
        /**
         * podmínka ověřující, zda určité pole je v určitém stavu (obsazené, volné, obsazené figurou určité barvy...)
         */
        private class PodminkaStatusuPole extends Podminka{
            /**
             * pole, ke kterým se podmínka vztahuje
             */
            private ArrayList<Pole> cilovaPole;
            /**
             * hodnota, kterou na poli podmínka potřebuje, aby platila
             */
            private String potrebnaHodnotaNaCilovychPolich;
            //private int pocetPoliKeSplneni;
            /**
             * 
             * @param podminkaString řetězec ve formátu [řetězec pole] [podmínka]
             */
            public PodminkaStatusuPole(String podminkaString) {
                Pattern polePattern = Pattern.compile("\\(\\$?-?\\d+ \\$?-?\\d+\\)");
                Matcher polePatternMatcher = polePattern.matcher(podminkaString);
                cilovaPole = new ArrayList<>();
                while(polePatternMatcher.find()){
                    cilovaPole.add(new Pole(polePatternMatcher.group()));
                
                }
                Matcher hodnotaPatternMatcher = Pattern.compile("\\*?[a-z]+").matcher(podminkaString);
                if (hodnotaPatternMatcher.find()){
                   
                    potrebnaHodnotaNaCilovychPolich = hodnotaPatternMatcher.group();
                    
                }
               }

            @Override
            public boolean plati(int radekFigury, int sloupecFigury, Sachovnice s, int cisloVolani) throws PrazdnePoleException {
                int splnenych = 0;
                for (Pole pole:cilovaPole){
                    if(cisloVolani>3 && this.potrebnaHodnotaNaCilovychPolich.contains("ohrozeny"))
                        splnenych +=1;
                    else{
                        if(s.overPodminku(pole.absolutniAdresa(s.barvaTokenu(radekFigury, sloupecFigury),radekFigury, sloupecFigury), potrebnaHodnotaNaCilovychPolich, radekFigury, sloupecFigury, cisloVolani+1))
                            splnenych+=1;
                    }
            }
                return (splnenych==cilovaPole.size());
            }
            @Override
            public String toString(){
                String ret ="";
                for (Pole p:cilovaPole){
                    ret = ret + p.toString() + " ";
                }
                
                ret = ret + potrebnaHodnotaNaCilovychPolich;
                
                return ret;
            }
            
            @Override 
            public boolean equals(Object o){
                PodminkaStatusuPole zkouseny;
                if (o.getClass().equals(this.getClass())){
                    zkouseny = (PodminkaStatusuPole) o;
                    return zkouseny.toString().equals(this.toString());
                }
                
                return false;
            }

            @Override
            public int hashCode(){
                return super.hashCode();
            }
        }
        /**
        *podmínka ověřující flag té figury, které patří tah, kterému patří tato podmínka
        */
        private class PodminkaFlag extends Podminka{
            String flag;
            public PodminkaFlag(String flag){
                this.flag = flag;
            }
            @Override
            public boolean plati(int radekFigury, int sloupecFigury, Sachovnice s, int cisloVolani){
                return s.getFlagOfToken(radekFigury, sloupecFigury, flag);
            }
            @Override
            public String toString(){
                return (this.flag);
            }
            
            @Override 
            public boolean equals(Object o){
                PodminkaFlag zkouseny;
                if (o.getClass().equals(this.getClass())){
                    zkouseny = (PodminkaFlag) o;
                    return (zkouseny.toString().equals(this.toString()));
                }
                
                return false;
            }
            @Override
            public int hashCode(){
                return super.hashCode();
            }
        }
        /**
         * podmínka zkoušející, zda dvě různé pole pro určitý kámen ukazují (případně neukazují) na stejnou absolutní pozici na šachovnici
         */
        private class PodminkaPole extends Podminka{
            private Pole p1;
            private Pole p2;
            private boolean shodna;
            /**
             * 
             * @param podminkaString řetězec ve tvaru [řetězec pole] =[řetězec pole 2], případně [řetězec pole] !=[řetězec pole 2]
             */
            public PodminkaPole(String podminkaString){
                if(podminkaString.contains("!")){
                    p1=new Pole(podminkaString.split("!=")[0].trim());
                    p2 = new Pole(podminkaString.split("!=")[1].trim());
                    shodna = false;
                }else{
                    p1=new Pole(podminkaString.split("=")[0].trim());
                    p2 = new Pole(podminkaString.split("=")[1].trim());
                    shodna = true;
                }
            }

            @Override
            public boolean plati(int radekFigury, int sloupecFigury, Sachovnice s, int cisloVolani) throws PrazdnePoleException{
                if(shodna)
                    return Arrays.equals(p1.absolutniAdresa(s.barvaTokenu(radekFigury, sloupecFigury), radekFigury, sloupecFigury), p2.absolutniAdresa(s.barvaTokenu(radekFigury, sloupecFigury),radekFigury, sloupecFigury));
                else
                    return !Arrays.equals(p1.absolutniAdresa(s.barvaTokenu(radekFigury, sloupecFigury), radekFigury, sloupecFigury), p2.absolutniAdresa(s.barvaTokenu(radekFigury, sloupecFigury),radekFigury, sloupecFigury));
                            }
            
            @Override
            public String toString(){
                if(shodna)
                    return (p1.toString()+ " = " + p2.toString());
                else
                    return (p1.toString()+ " != " + p2.toString());
            }
            
            @Override 
            public boolean equals(Object o){
                PodminkaPole zkouseny;
                if (o.getClass().equals(this.getClass())){
                    zkouseny = (PodminkaPole) o;
                    return (zkouseny.toString().equals(this.toString()));
                }
                
                return false;
            }
            @Override
            public int hashCode(){
                return super.hashCode();
            }
        }
            
        
        /**
         * Třída reprezentující podmínku, která musí platit, aby bylo tah, k němuž podmínka přísluší, možné zahrát
         */
        private abstract class Podminka{
            /**
             * 
             * @param radekFigury pozice kamene, jehož tah je ověřován na šachovnici
             * @param sloupecFigury pozice kamene, jehož tah je ověřován na šachovnici
             * @param s šachovnice, na které je kámen, jehož tah je ověřován
             * @param cisloVolani po kolikáté je ověřování podmínek voláno - proměnná zabraňuje přetečení zásobníku po cyklických dotazech na ohrožená pole
             * @return true pokud podmínka je splněná, false pokud není
             * @throws PrazdnePoleException Výjimka, když se program při validaci tahu zeptal na barvu prázdného pole
             */
            public abstract boolean plati(int radekFigury, int sloupecFigury, Sachovnice s, int cisloVolani) throws PrazdnePoleException;
                
                
        }
        /**
         * Třída, reprezentující efekty tahu mimo posunutí táhnoucí figury. Může jít o přesouvání jiných figur (např. při rošádě), braní jiných figur, stavění nových figur a úpravu flagů figur
         */
        public abstract class Efekt{
            /**
             * 
             * @param puvodni šachovnice před hraním tahu, ke kterému efekt přísluší
             * @param s šachovnice, na kterou je efekt aplikován
             * @param radekFigury absolutní pozice kamene, který zahrál tah s tímto efektem
             * @param SloupecFigury absolutní pozice kamene, který zahrál tah s tímto efektem
             * @throws PrazdnePoleException Výjimka, když se program při validaci tahu zeptal na barvu prázdného pole
             */
            public abstract void proved(Sachovnice puvodni, Sachovnice s, int radekFigury, int SloupecFigury) throws PrazdnePoleException;
            /**
             * 
             * @param s šachovnice, na které by byl hrán tah s tímto efektem
             * @param radekFigury absolutní pozice kamene, který zahrál tah s tímto efektem
             * @param sloupecFigury absolutní pozice kamene, který zahrál tah s tímto efektem
             * @return pole, která by mohla být sebrána tímto efektem
             * @throws PrazdnePoleException Výjimka, když se program při validaci tahu zeptal na barvu prázdného pole 
             */
            public abstract int[] getOhrozenePole(Sachovnice s, int radekFigury, int sloupecFigury) throws PrazdnePoleException;
            /**
             * 
             * @param s šachovnice, na které by byl hrán tah s tímto efektem
             * @param radekFigury absolutní pozice kamene, který zahrál tah s tímto efektem
             * @param sloupecFigury absolutní pozice kamene, který zahrál tah s tímto efektem
             * @return pole, ze kterých by mohly kameny být přsunuty jinam
             * @throws PrazdnePoleException Výjimka, když se program při validaci tahu zeptal na barvu prázdného pole
             */
            public abstract int[] getOdsunutePole(Sachovnice s, int radekFigury, int sloupecFigury) throws PrazdnePoleException;
        }
        /**
         * efekt, který přesune kámen z danného pole na jiné
         */
        private class PosunEfekt extends Efekt{
            /**
             * pole, ze kterého je kámen přesouván
             */
            private Pole start;
            /**
             * pole, na které je kámen přesouván
             */
            private Pole target;
            /**
             * 
             * @param posunEfektString řetězec ve tvaru [řetězec pole] -> [řetězec pole 2]
             */
            public PosunEfekt(String posunEfektString){
                start = new Pole(posunEfektString.split("->")[0].trim());
                target = new Pole(posunEfektString.split("->")[1].trim());
            }
            
            public PosunEfekt(Pole start, Pole target){
                this.start = start;
                this.target = target;
            }

            @Override
            public void proved(Sachovnice puvodni, Sachovnice s, int radekFigury, int sloupecFigury) throws PrazdnePoleException {
                int odkud[] = this.start.absolutniAdresa(puvodni.barvaTokenu(radekFigury, sloupecFigury), radekFigury, sloupecFigury);
                
                int kam[] = this.target.absolutniAdresa(puvodni.barvaTokenu(radekFigury, sloupecFigury), radekFigury, sloupecFigury);
                if(odkud[0]>=0 && odkud[0]<8 && odkud[1]>=0 && odkud[1]<8 &&kam[0]>=0 && kam[0]<8 && kam[1]>=0 && kam[1]<8){
                    if(!s.jeToKral(kam))
                        s.posun(puvodni, odkud, kam);
                    else
                        s.setPole(odkud, ".");
                    }
            }
            
            @Override
            public String toString(){
                return (this.start.toString() + " -> " + this.target.toString());
            }

            @Override
            public int[] getOhrozenePole(Sachovnice s, int radekFigury, int sloupecFigury) throws PrazdnePoleException {
                return this.target.absolutniAdresa(s.barvaTokenu(radekFigury, sloupecFigury), radekFigury, sloupecFigury);
            }
            @Override
            public int[] getOdsunutePole(Sachovnice s, int radekFigury, int sloupecFigury) throws PrazdnePoleException{
                return this.start.absolutniAdresa(s.barvaTokenu(radekFigury, sloupecFigury), radekFigury, sloupecFigury);
            }
            
            @Override 
            public boolean equals(Object o){
                PosunEfekt zkouseny;
                if (o.getClass().equals(this.getClass())){
                    zkouseny = (PosunEfekt) o;
                    return (zkouseny.toString().equals(this.toString()));
                }
                
                return false;
            }
            @Override
            public int hashCode(){
                return 1;
            }
            
            
        }
        /**
         * Efekt, který dané pole nastaví na určitou hodnotu (tj. postaví na něm figuru, případně sebere z něj figuru, pokud hodnota tohot efektu je "."
         */
        private class NastavPoleEfekt extends Efekt{
            /**
             * pole, které bude efektem změněno
             */
            private Pole target;
            /**
             * hodnota, na kterou bude pole nastaveno
             */
            private String hodnota;
            
            public NastavPoleEfekt(Pole target, String figuraString){
                this.target = target;
                this.hodnota = figuraString;
            }
            /**
             * 
             * @param nastavPoleEfektString Řetězec ve tvaru (ZNAK_FIGURY) -> [řetězec pole] 
             */
            public NastavPoleEfekt(String nastavPoleEfektString){
                this.hodnota = nastavPoleEfektString.split("->")[0].trim();
                this.target = new Pole(nastavPoleEfektString.split("->")[1].trim());
                
            }

            @Override
            public void proved(Sachovnice puvodni, Sachovnice s, int radekFigury, int SloupecFigury) throws PrazdnePoleException {
                boolean cerny = (Character.isUpperCase(this.hodnota.charAt(0)) && puvodni.barvaTokenu(radekFigury, SloupecFigury)) || (Character.isLowerCase(this.hodnota.charAt(0)) && !puvodni.barvaTokenu(radekFigury, SloupecFigury));
                boolean bily = (Character.isUpperCase(this.hodnota.charAt(0)) && !puvodni.barvaTokenu(radekFigury, SloupecFigury)) || (Character.isLowerCase(this.hodnota.charAt(0)) && puvodni.barvaTokenu(radekFigury, SloupecFigury));
                s.setPoleSBarvou(target.absolutniAdresa(puvodni.barvaTokenu(radekFigury, SloupecFigury), radekFigury, SloupecFigury), hodnota, bily, cerny );
            }
            
            @Override
            public String toString(){
                return ("(" + this.target.cilRadek + " " + this.target.cilSloupec +")" + " -> " + this.hodnota);
            }

            @Override
            public int[] getOhrozenePole(Sachovnice s, int radekFigury, int sloupecFigury) throws PrazdnePoleException {
                return this.target.absolutniAdresa(s.barvaTokenu(radekFigury, sloupecFigury), radekFigury, sloupecFigury);
            }

            @Override
            public int[] getOdsunutePole(Sachovnice s, int radekFigury, int sloupecFigury) {
                return new int[0];
            }
            @Override 
            public boolean equals(Object o){
                NastavPoleEfekt zkouseny;
                if (o.getClass().equals(this.getClass())){
                    zkouseny = (NastavPoleEfekt) o;
                    return (zkouseny.toString().equals(this.toString()));
                }
                
                return false;
            }
            @Override
            public int hashCode(){
                return super.hashCode();
            }
        }
        /**
         * Efekt, který nastaví daný flag daného kamene na danou hodnotu
         */
        private class SetFlagEfekt extends Efekt{
            /**
             * pole, které bude efektem změněno
             */
            private final Pole target;
            /**
             * jméno flagu, který bude změněn
             */
            private final String flagName;
            /**
             * hodnota, na kterou bude flag změněn
             */
            private final boolean targetValue;
            
            /**
             * 
             * @param flagEfektString řetězec ve tvaru [řetězec pole]_FLAGNAME=[true/false]
             */
            public SetFlagEfekt(String flagEfektString){
                this.target = new Pole(flagEfektString.split("_")[0]);
                this.flagName = flagEfektString.substring(flagEfektString.indexOf("_")+1,flagEfektString.indexOf("="));
                this.targetValue = flagEfektString.substring(flagEfektString.indexOf("=")+1).equals("true");
            }
            
            public SetFlagEfekt(Pole target, String flagName, boolean targetValue){
                this.flagName = flagName;
                this.target = target;
                this.targetValue = targetValue;
            }
            @Override
            public void proved(Sachovnice puvodni, Sachovnice s, int radekFigury, int SloupecFigury) {
                s.setFlagOfToken(radekFigury, SloupecFigury, znak, targetValue);
            }
            
            @Override
            public String toString(){
                return("(" + this.target.cilRadek +" "+this.target.cilSloupec+")" + this.flagName + " -> " + this.targetValue);
            }

            @Override
            public int[] getOhrozenePole(Sachovnice s, int radekFigury, int sloupecFigury) {
                return new int[0];
            }

            @Override
            public int[] getOdsunutePole(Sachovnice s, int radekFigury, int sloupecFigury) {
                return new int[0];
            }
            
            @Override 
            public boolean equals(Object o){
                SetFlagEfekt zkouseny;
                if (o.getClass().equals(this.getClass())){
                    zkouseny = (SetFlagEfekt) o;
                    return (zkouseny.toString().equals(this.toString()));
                }
                
                return false;
            }
            @Override
            public int hashCode(){
                return super.hashCode();
            } 
        }
        
       
        /**
         * pole, na které táhne figura, které přísluší tento tah
         */
        private Pole cil;
        /**
         * podmínky, které musí platit, aby tah bylo možné zahrát
         */
        private ArrayList<Podminka> podminky;
        /**
         * ëfekty, které tento tah způsobí, když bude zahrán
         */
        private ArrayList<Efekt> efekty;
        
        /**
         * 
         * @param poleString řetězec cílového pole
         * @param podminky řetězce všech podmínek tohoto tahu
         * @param extra řetězce všech efektů tohoto tahu 
         */
        private Tah(String poleString, String podminky, String extra){//konstruktor má jako vstup konkrétní cílové pole a string podmínek pro jednu konkrétní kombinaci proměnných v podminkyString
            cil = new Pole(poleString);
            this.podminky = new ArrayList<>();
            this.efekty = new ArrayList<>();
            
            Matcher podminkaStatusuPoleMatcher = Pattern.compile("\\(\\$?-?\\d+ \\$?-?\\d+\\) \\*?[a-z|&]+").matcher(podminky);
            Matcher podminkaPoleMatcher = Pattern.compile("\\(-?\\d+ -?\\d+\\) !?=\\(\\$?-?\\d+ \\$?-?\\d+\\)").matcher(podminky);
            Matcher podminkaFlagMatcher = Pattern.compile("[A-Z]+").matcher(podminky);
            
            while(podminkaStatusuPoleMatcher.find()){
                
                if(!podminkaStatusuPoleMatcher.group().contains("404"))
                    this.podminky.add(new PodminkaStatusuPole(podminkaStatusuPoleMatcher.group()));
            }
            while(podminkaPoleMatcher.find()){
                if(!podminkaPoleMatcher.group().contains("404"))
                    this.podminky.add(new PodminkaPole(podminkaPoleMatcher.group()));
            }
            while(podminkaFlagMatcher.find()){
                if(!podminkaFlagMatcher.group().contains("404"))
                    this.podminky.add(new PodminkaFlag(podminkaFlagMatcher.group()));
            }
            
            Matcher eePosunMatcher = Pattern.compile("\\(\\$?-?\\d+ \\$?-?\\d+\\) -> \\(\\$?-?\\d+ \\$?-?\\d+\\)").matcher(extra);
            Matcher eeNastavPoleMatcher = Pattern.compile("\\w+ -> \\(\\$?-?\\d+ \\$?-?\\d+\\)").matcher(extra);
            Matcher eeFlagEfektMatcher = Pattern.compile("\\(\\$?-?\\d+ \\$?-?\\d+\\)_\\w+ = (true|false)").matcher(extra);
            
            while(eePosunMatcher.find())
                this.efekty.add(new PosunEfekt(eePosunMatcher.group()));
            
            while(eeNastavPoleMatcher.find())
                this.efekty.add(new NastavPoleEfekt(eeNastavPoleMatcher.group()));
            while(eeFlagEfektMatcher.find())
                this.efekty.add(new SetFlagEfekt(eeFlagEfektMatcher.group()));
            
                
            
           
            
        }
       /**
        * 
        * @param s šachovnice, na které by tah byl proveden
        * @param radek absolutní souřadnice kamene, který by mohl zahrát tento tah
        * @param sloupec absolutní souřadnice kamene, který by mohl zahrát tento tah
        * @param cisloVolani po kolikáté je tato funkce volána - proměnná zabraňuje přetečení zásobníku kvůli cyklickým dotazům na ohrožená pole
        * @return pole, ktará by byla sebrána zahráním tohoto tahu
        * @throws PrazdnePoleException Výjimka, když se program při validaci tahu zeptal na barvu prázdného pole
        */ 

       
       public HashSet<int[]> ohrozenaPole(Sachovnice s, int radek, int sloupec, int cisloVolani) throws PrazdnePoleException{
           HashSet<int[]> ret = new HashSet<>();
           if(!this.jeValidni(s, radek, sloupec, cisloVolani))
               return ret;
           
           ret.add(this.getAbsCil(s.barvaTokenu(radek, sloupec),radek, sloupec));
           
           for (Efekt e:this.efekty){
               if(e.getOhrozenePole(s, radek, sloupec)!=null)
                   ret.add(e.getOhrozenePole(s, radek, sloupec));
               
           }
           
           for (Efekt e:this.efekty){
               ret.remove(e.getOdsunutePole(s, radek, sloupec));
           }
           
           return ret;
       }
       /**
        * 
        * @param s šachovnice, na které by tah byl proveden
        * @param radek absolutní souřadnice kamene, který by mohl zahrát tento tah
        * @param sloupec absolutní souřadnice kamene, který by mohl zahrát tento tah
        * @param cisloVolani po kolikáté je tato funkce volána - proměnná zabraňuje přetečení zásobníku kvůli cyklickým dotazům na ohrožená pole
        * @return boolean hodnota, zda je tah možné na dané šachovnici daným kamenem zahrát
        * @throws PrazdnePoleException Výjimka, když se program při validaci tahu zeptal na barvu prázdného pole 
        */
        public boolean jeValidni(Sachovnice s, int radek, int sloupec, int cisloVolani) throws PrazdnePoleException {
            if (this.getAbsCil(s.barvaTokenu(radek, sloupec), radek, sloupec)[0]>7 || this.getAbsCil(s.barvaTokenu(radek, sloupec), radek, sloupec)[0]<0 || this.getAbsCil(s.barvaTokenu(radek, sloupec),radek, sloupec)[1]>7 || this.getAbsCil(s.barvaTokenu(radek, sloupec),radek, sloupec)[1]<0)
            {
                
                return false;
            
            }
            
            for (Podminka p: this.podminky){
                
                if(!p.plati(radek, sloupec, s, cisloVolani)){
                    
                    return false;
                }
            }

            for(Efekt e:this.getEfekty()){
                int[] ohrozenePole = e.getOhrozenePole(s, radek, sloupec);
                if(ohrozenePole.length!=0){
                    if(ohrozenePole[0]>=0 && ohrozenePole[0]<8&&ohrozenePole[1]>=0&&ohrozenePole[1]<8){
                        if(s.jeToKralSBarvou(e.getOhrozenePole(s, radek, sloupec), s.barvaTokenu(radek, sloupec)))
                            return false;
                    }
                }
                int[] odsunutePole = e.getOdsunutePole(s, radek, sloupec);
                if(odsunutePole.length!=0){
                    if(odsunutePole[0]==radek && odsunutePole[1]==sloupec){
                        
                        return false;
                    }
                }
                    
            }
            
            return true;
        }
        /**
         * 
         * @param bily barva kamene, který by hrál tento tah
         * @param radek absolutní souřadnice kamene, který by mohl zahrát tento tah
        * @param sloupec absolutní souřadnice kamene, který by mohl zahrát tento tah
         * @return absolutní souřadnice pole, na které by byl kámen posunut tímto tahem
         */
        public int[] getAbsCil(boolean bily, int radek, int sloupec){
            return this.cil.absolutniAdresa(bily, radek, sloupec);
        }
        /**
        *@return cílové pole tohoto tahu
        */
        private Pole getCilovePole(){ 
            return cil;            
        }
        /**
        *@return všechny podmínky, které musí být splněny aby tento tah mohl být zahrán
        */
        private ArrayList<Podminka> getPodminky(){
            return this.podminky;
        }
        
        /**
        *@return všechny zvláštní efekty tohoto tahu
        */
        public ArrayList<Efekt> getEfekty(){
            return this.efekty;
        }
        
        /*private boolean patologickyEfekt(){
            
        } */
        
        /**
         * Funkce, která připojí k podmínkám tohoto tahu podmínky jiného tahu
         * @param t tah k připojení
         */
        public void pripoj(Tah t){
            
            /*for(Efekt e:t.getEfekty()){
                if(!this.efekty.contains(e))
                    this.efekty.add(e);
            }*/
            
            
            for(Podminka p:t.getPodminky()){
                if(!this.podminky.contains(p))
                    this.podminky.add(p);
            }
            //this.flagChanges.addAll(t.getFlagChanges());
        }
        @Override
        public String toString(){
            String ret ="";
            ret=ret.concat("("+this.cil.cilRadek+" "+this.cil.cilSloupec+") ");
            
            for (Podminka p:this.podminky){
                
                ret = ret.concat(p.toString());
            }
            for (Efekt e :this.efekty){
                ret =ret.concat(e.toString());
            }
            
            
            return ret;
        }
        
        
        
    }
    
    /**
     * znak, reprezentující tuto figuru při vykreslení šachovnice
     */
    private String znak;
    
    /**
     * tahy, které tento druh figury může zahrát
     */
    private ArrayList<Tah> tahy;
    /**
     * flagy, které tento druh figury může mít
     */
    private Set<String> flags;
    
    /**
     * @throws ZpracovaniFiguryException Výjimka, pokud vstupniString nebyl zadán ve správném tvaru
     * @param vstupniString string ve tvaru [ZNAK] {řetězec tahu 1}{řetězec tahu 2}... [FLAGY]   
     */
    public Figura(String vstupniString) throws ZpracovaniFiguryException{
        this.tahy = new ArrayList<>();
        this.flags = new HashSet<>();
        znak = vstupniString.substring(0, vstupniString.indexOf("{")).trim().toLowerCase();
        Pattern vsechnyTahyPattern = Pattern.compile("\\{.+\\}");
        Matcher vsechnyTahyMatcher = vsechnyTahyPattern.matcher(vstupniString);
        vsechnyTahyMatcher.find();
        String tahyString = vsechnyTahyMatcher.group();
        
        
        Matcher tahMatcher = Pattern.compile("\\{.*?\\}").matcher(tahyString);
        
        while(tahMatcher.find()){
            
            List<String> tahyStringsMoznostiPodlePodminek = Prekladac.rozsirTahString(tahMatcher.group());
            for(String tahString:tahyStringsMoznostiPodlePodminek)
                tahy.addAll(zpracujTahy(tahString));
            
        }
        
        String flagsString = vstupniString.substring(vsechnyTahyMatcher.end());
        
        String[] flagStrings = flagsString.split(" ");
        
        Collections.addAll(this.flags, flagStrings);
    }
    /**
    * konstruktor "figury" reprezentující prázdné pole
    */
    public Figura(){
        this.tahy = new ArrayList<>();
        this.znak = ".";
        this.flags = new HashSet<>();
    }
    /**
    *@return znak reprezentující tuto figuru na šachovnici
    */
    public String getZnak(){
        return this.znak;
    }
    
    /**
    *@return všechny flagy, které má tento druh figury
    */
    public Set<String> getAllFlags(){
        return this.flags;
    }
    
    /**
    *@return všechny tahy, které má tento druh figury
    */
    public ArrayList<Tah> getTahy(){
    return this.tahy;
    }
    
    @Override
    public String toString(){
        String ret = this.znak;
        ret =ret.concat(" Tahy: ");
        for (Tah t:this.tahy){
            ret=ret.concat(t.toString() +"\n");
        }
        ret = ret.concat("\n Flags: ");
        for(String f:this.getAllFlags()){
            ret = ret.concat(f + " ");
        }
        
     return ret;   
    }
    
    
    
}
