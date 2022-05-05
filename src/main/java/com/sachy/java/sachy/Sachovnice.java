/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sachy.java.sachy;

import com.sachy.java.sachy.Figura.Tah;
import com.sachy.java.sachy.Figura.Tah.Efekt;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
//import javasachy.Figura.Tah;
//import javasachy.Figura.Tah.Efekt;

/**
 *Třída reprezentující šachovnici a konkrétní pozici na ní
 * @author User
 */
public class Sachovnice {

    
    /**
     * Třída obsahující seznam šachovnic a pravivostní hodnoty, která říká, jestli nějaké z těchto pozice vede k vynucenému vítězství bílého hráče
     * používána jako návratový typ funkce vyres
     */
    public class RadaPozic{
        /**
         * pravdivostní hodnota, zda si bílý může vynutit vítězství do určitého počtu tahů
         */
        private final boolean validni;
        int a=1000;
        /**
         * seznam všech šachovnic, které mohou být za určitý počet tahů dosaženy z nějaké počáteční pozice
         */
        private final ArrayList<Sachovnice> seznamPozic;
        
        /**
         * @param seznamPozic všechny pozice v této věti stromu
         * @param validni pravdivostní hodnota,zda si v této větvi můž bílý vynutit vítězství
        */
        public RadaPozic(ArrayList<Sachovnice> seznamPozic, boolean validni){
            this.validni = validni;
            this.seznamPozic = seznamPozic;
        }
        
        public boolean jeValidni(){
            return this.validni;
        }
        
        public ArrayList<Sachovnice> getPozice(){
            return this.seznamPozic;
        }
    }
    /**
     * Tabulka spojující znaky figur s k nim příslušícím objektům Figura
     */
    static HashMap<String,Figura> figuryStrings = new HashMap<>();
    
    /**
     * Pole reprezentující šachovnici 8x8 a kameny na ní
     */
    private final TokenFigury[][] pozice;
    
    /**
     * Řetězec reprezentující zápis partie od počáteční pozice k  této pozici
     */
    private String notace;
    
    public Sachovnice() {
        this.notace = "";
        figuryStrings.put(".", new Figura());
        this.pozice = new TokenFigury[8][8];
        for (int i=0;i<8;i++){
            for(int j =0; j< 8; j++){
                this.pozice[i][j] = new TokenFigury(figuryStrings.get("."), false, false);
            }
        }
        
    }

    
    /**
     * Metoda, která upraví jeden řádek šachovnice podle vstupu
     * @param radekString vstup, podle kterého má být řádek upraven, ve formátu [znak 1] [znak 2] ... [znak 8]
     * @param radek řádek, který má být upraven
     * @throws Exception Výjimka, pokud uživatel zadal neplatný vstup
     */
    public void upravRadek(String radekString, int radek) throws Exception{
        String[] znaky = radekString.split(" ");
        for (int i =0; i<8; i++){
            String znak = znaky[i].split("_")[0];
            if (figuryStrings.keySet().contains(znak.toLowerCase())){
                
                this.pozice[radek][i] = new TokenFigury(figuryStrings.get(znak.toLowerCase()), Character.isLowerCase(znak.charAt(0)), Character.isUpperCase(znak.charAt(0)));
            }
            else{
                System.out.println("neznámá figura: " + znak);
                throw new Exception();
            }
            if(znaky[i].contains("_")){
                String[] flags = znaky[i].substring(znaky[i].indexOf("_")+1).split(",");
                for(String s:flags){
                   this.setFlagOfToken(radek, i, s, false);
                }
            }
        }
    }
    /**
     * Třída reprezentující hrac kámen na šachovnici
     * Kámen se znakem "." je prázdné pole
     */
    private class TokenFigury{
        /**
         * objekt Figura, který přísluší tomuto kameni
         */
        private Figura figura;
        /**
         * pravdivostní hodnota, zda je kámen bílý
         */
        private boolean bily;
        /**
         * flagy a jejich hodnoty pro tento kámen
         */
        private HashMap<String, Boolean> flags;
        /**
         * pravdivostní hodnota, zda je kámen černý (kámen můž být i bezbarvý, je to případ zejména prázdného pole, které je také reprezentováno jako kámen, také by tak mohla být reprezentována překážka)
         */
        private boolean cerny;
        public TokenFigury(Figura figura, boolean bily, boolean cerny){
            if(figura!=null){
                this.figura = figura;
                this.bily = bily;
                this.cerny = cerny;
                this.flags = new HashMap<>();
                for (String s:figura.getAllFlags())
                    flags.put(s, true);
            }
        }
        public Set<String> getAllFlagNames(){
            return this.flags.keySet();
        }
        
        public String getZnak(){
            return this.figura.getZnak();
        }
        
        public void setFlag(String flag, boolean hodnota){
            this.flags.put(flag, hodnota);
        }
        
        public boolean getFlag(String flag){
            return this.flags.getOrDefault(flag, false);
        }
        
        public boolean jeBily(){
            return this.bily;
        }
        
        public boolean jeCerny(){
            return this.cerny;
        }
    }
    /**
     * Metoda, která vrátí textovou reprezentaci šachovnice
     * @return zápis dosavadních tahů a nákres šachovnice
     */
    public String vykresliSe(){
        String vykresleni = "";
        for (int radek = 7; radek>=0; radek--){
            for (int sloupec =0; sloupec<8; sloupec++){
                String znak = this.pozice[radek][sloupec].getZnak();
                int delka = figuryStrings.keySet().stream().max((s1,s2)->(s1.length()-s2.length())).get().length();
                if(this.pozice[radek][sloupec].jeCerny())
                    vykresleni += znak.toUpperCase();
                    
                else
                    vykresleni += znak;
                for(int i = znak.length(); i<=delka;i++)
                        vykresleni += " ";
            }
            vykresleni+="\n";
        }
        return notace+"\n" + vykresleni;
    }
    
    /**
     * Metoda, která zjistí flag určitého hracího kamene
     * @param radek absolutní pozice hracího kamene
     * @param sloupec absolutní pozice hracího kamene
     * @param flag flag, jehož hodnota má být čtena
     * @return  hodnota flagu u tohoto kamene, false, pokud kámen tento flag vůbec nemá
     */
    public boolean getFlagOfToken(int radek, int sloupec, String flag){
        return this.pozice[radek][sloupec].getFlag(flag);
    }
    
    /**
     * Metoda, která naství flag určitého hracího kamene
     * @param radek absolutní pozice hracího kamene
     * @param sloupec absolutní pozice hracího kamene
     * @param flag flag, jehož hodnota má být nastaven
     * @param target hodnota, na kterou má být flag nastaven
     */
    public void setFlagOfToken(int radek, int sloupec, String flag, boolean target){
        this.pozice[radek][sloupec].setFlag(flag, target);
    }
    
    private Sachovnice(TokenFigury[][] pozice, String notace){
        this.pozice = new TokenFigury[8][8];
        this.notace = notace;
        for (int i =0; i<8; i++){
            System.arraycopy(pozice[i], 0, this.pozice[i], 0, 8);
        }
        
        
    }
    
    
    
   /**
    * Metoda, která zjistí, jaký kámen je na určitém poli
    * @param radek absolutní pozice hracího kamene
     * @param sloupec absolutní pozice hracího kamene
    * @return kámen na této pozici
    */
    private TokenFigury coJeNaPoli(int radek, int sloupec){
        return pozice[radek][sloupec];
    }
    
    /**
    * Metoda, která zjistí, jaký kámen je na určitém poli
    * @param radekSloupec absolutní pozice hracího kamene
    * @return kámen na této pozici
    */
    private TokenFigury coJeNaPoli(int[] radekSloupec){
        return coJeNaPoli(radekSloupec[0], radekSloupec[1]);
    }
    /**
     * Metoda, která zjistí, zda je dané pole v ohrožení
     * @param radek absolutní pozice tohoto pole
     * @param sloupec absolutní pozice tohoto pole
     * @param bily barva hráče, který se dotazuje, hodnota true zkoumá ohrožení černými figurami a naopak
     * @param cisloVolani číslo po kolikáté je tato funkce volána, zabraňuje přetečení zásobníku po rekurzivním volání dotázů na ohrožená pole
     * @return pravidvostní hodnotu, zda je dané pole napadené jedním z hráčů
     */
    public boolean jeOhrozene(int radek, int sloupec, boolean bily, int cisloVolani){
        int[] radekSloupec = {radek, sloupec};
        return jeOhrozene(radekSloupec, bily, cisloVolani);
    }
    
    /**
     * Metoda, která zjistí, zda je dané pole v ohrožení
     * @param radekSloupec absolutní pozice tohoto pole
     * @param bily barva hráče, který se dotazuje, hodnota true zkoumá ohrožení černými figurami a naopak
     * @param cisloVolani číslo po kolikáté je tato funkce volána, zabraňuje přetečení zásobníku po rekurzivním volání dotázů na ohrožená pole
     * @return pravdivostní hodnotu, zda je dané pole napadené jedním z hráčů
     */
    public boolean jeOhrozene(int[] radekSloupec, boolean bily, int cisloVolani){
        return this.ohrozenaPole(bily, cisloVolani).contains(radekSloupec);
    }
    
    /**
     * Metoda ověřující nějaký objekt PodminkaStatusuPole
     * @param radekSloupec absolutní pozice pole, ke kterému se váže ověřovaná podmínka
     * @param podminka hodnota, kterou obsahuje tato PodminkaStatusuPole
     * @param radekPuvodni sabsolutní pozice kamene, k jehož tahu se váže tato podmínka
     * @param sloupecPuvodni sabsolutní pozice kamene, k jehož tahu se váže tato podmínka
     * @param cisloVolani číslo po kolikáté je tato funkce volána, zabraňuje přetečení zásobníku po rekurzivním volání dotázů na ohrožená pole
     * @return pravdivostní hodnotu, zda je podmínka splněna
     */
    public boolean overPodminku(int[]radekSloupec, String podminka, int radekPuvodni, int sloupecPuvodni, int cisloVolani){
        if(radekSloupec[0] > 7 || radekSloupec[0]<0 || radekSloupec[1]>7 || radekSloupec[1]<0)
            return false;
        switch(podminka){
            case "prazdny":
                return this.pozice[radekSloupec[0]][radekSloupec[1]].getZnak().equals(".");
            case "obsazeny":
                return !this.pozice[radekSloupec[0]][radekSloupec[1]].getZnak().equals(".");
            case "cerny":
                return this.pozice[radekSloupec[0]][radekSloupec[1]].jeCerny();
            case "bily":
                return this.pozice[radekSloupec[0]][radekSloupec[1]].jeBily();
            case "shodny":
                return (this.pozice[radekSloupec[0]][radekSloupec[1]].jeCerny() && this.pozice[radekPuvodni][sloupecPuvodni].jeCerny()) || (this.pozice[radekSloupec[0]][radekSloupec[1]].jeBily() && this.pozice[radekPuvodni][sloupecPuvodni].jeBily());
            case "opacny":
                
                return (this.pozice[radekSloupec[0]][radekSloupec[1]].jeCerny() && this.pozice[radekPuvodni][sloupecPuvodni].jeBily()) || (this.pozice[radekSloupec[0]][radekSloupec[1]].jeBily() && this.pozice[radekPuvodni][sloupecPuvodni].jeCerny());
            case "ohrozeny":
                //System.out.println("volání ohrozeny  \n" +this.vykresliSe());
                return this.ohrozenaPole(this.pozice[radekPuvodni][sloupecPuvodni].jeBily(), cisloVolani).stream().anyMatch(p->Arrays.equals(p, radekSloupec));
            case "neohrozeny":

                //System.out.println("volání neohrozeny  \n pole ("+ radekSloupec[0] +" "+  radekSloupec[1]+ ") + vysledek: "+ !this.ohrozenaPole(this.pozice[radekPuvodni][sloupecPuvodni].jeBily(), cisloVolani).contains(radekSloupec) + "\n" +this.vykresliSe());
                return !this.ohrozenaPole(this.pozice[radekPuvodni][sloupecPuvodni].jeBily(), cisloVolani).stream().anyMatch(p->Arrays.equals(p, radekSloupec));
                
            default:
                
                break;

        }
         if (podminka.contains("*")){
                    
                    return this.coJeNaPoli(radekSloupec).getFlag(podminka.substring(1));
                }else{
                    
                    return this.coJeNaPoli(radekSloupec).getZnak().equals(podminka);
                }
    }
    /**
     * Metoda, která vrátí barvu kamene na dané pozici 
     * Metoda by měla být volána pouze na pozice, na kterých stojí kámen s nějakou barvou
     * @param radek absolutní pozice kamene
     * @param sloupec absolutní pozice kamene
     * @return barva na dané pozici
     * @throws PrazdnePoleException výjimka, pokud daný kámen nemá barvu
     */
    public boolean barvaTokenu(int radek, int sloupec) throws PrazdnePoleException{
        if(this.pozice[radek][sloupec].jeCerny())
            return false;
        if(this.pozice[radek][sloupec].jeBily())
            return true;
        throw new PrazdnePoleException();
    }

    /**
     * Posune kámen z nějaké pozice na jinou
     * @param puvodni šachovnice před hraním tohoto tahu 
     * @param start pozice, ze které je kámen posouván
     * @param cil pozice, na kterou je kámen posouván
     */
    void posun(Sachovnice puvodni, int[] start, int[] cil) {
        TokenFigury a = puvodni.pozice[start[0]][start[1]];
        if(this.coJeNaPoli(start).getZnak().equals(puvodni.coJeNaPoli(start).getZnak())&& this.coJeNaPoli(start).flags.equals(puvodni.coJeNaPoli(start).flags))
            this.pozice[start[0]][start[1]] = new TokenFigury(figuryStrings.get("."), false, false);
        if(!a.getZnak().equals(".")){
            this.pozice[cil[0]][cil[1]] = new TokenFigury(a.figura, a.bily, a.cerny);
            for (String f:a.getAllFlagNames()){
                this.pozice[cil[0]][cil[1]].setFlag(f, a.getFlag(f));
            }
            
        }
    }
    
    /**
     * Postaví na určité pole určitý kámen (může jít i o "kámen" reprezentující prázdné pole)
     * @param pozice absolutní pozice na šachovnici
     * @param token kámen, který tam má být postaven
     */
    void setPole(int[] pozice, TokenFigury token){
        this.pozice[pozice[0]][pozice[1]] = token;
    }
    
    /**
     * Postaví na určité pole nový kámen (může jít i o "kámen" reprezentující prázdné pole)
     * @param pozice absolutní pozice na šachovnici
     * @param znak znak figury, jejíž kámen má být na pole postaven
     */
    void setPole(int[] pozice, String znak) {
        this.pozice[pozice[0]][pozice[1]] = new TokenFigury(figuryStrings.getOrDefault(znak, figuryStrings.get(".")), Character.isLowerCase(znak.charAt(0)), Character.isUpperCase(znak.charAt(0)));
    }
    
    /**
     * Postaví na určité pole nový kámen (může jít i o "kámen" reprezentující prázdné pole)
     * @param pozice absolutní pozice na šachovnici
     * @param znak znak figury, jejíž kámen má být na pole postaven
     * @param bily barva stavěného kamene
     * @param cerny barva stavěného kamene
     */
    void setPoleSBarvou(int[] pozice, String znak, boolean bily, boolean cerny) {
        this.pozice[pozice[0]][pozice[1]] = new TokenFigury(figuryStrings.getOrDefault(znak, figuryStrings.get(".")), bily, cerny);
    }
    /*
    public Figura getPlnyPopisFigury(String key){
        return figuryStrings.getOrDefault(key, null);
    }
    */
    
    /**
     * Metodá vrátí všechny možné pozice po zahrání jednoho tahu na této šachovnici
     * @param bily hráč na tahu
     * @return seznam všech možných pozic po zahrání nějakého validního tahu
     */
    public ArrayList<Sachovnice> najdiVsechnyBudouciPozice(boolean bily){
        ArrayList<Sachovnice> vsechnyBudouciPozice = new ArrayList<>();
        for (int i = 0; i<8; i++){
            for (int j = 0; j<8; j++){
                if (bily && this.pozice[i][j].jeBily() || !bily &&this.pozice[i][j].jeCerny()){
                    try {
                        vsechnyBudouciPozice.addAll(najdiTahyFigury(i,j,pozice[i][j].figura));
                    } catch (Exception ex) {
                        Logger.getLogger(Sachovnice.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
        //vsechnyBudouciPozice.removeIf(s -> s.jeKralOhrozeny(bily));
        
        return vsechnyBudouciPozice;
    }
    
    /**
     * Metoda vrátí všechny možné pozice po nějakém tahu dané figury na dané pozici
     * @param startRadek absolutní pozice hrající figury
     * @param startSloupec ansolutní pozice hrající figury
     * @param figura hrající figura
     * @return Seznam všech pozic po tahu dané figury
     * 
     */
    public ArrayList<Sachovnice> najdiTahyFigury(int startRadek, int startSloupec, Figura figura) {
        ArrayList<Sachovnice> ret = new ArrayList<>();
        for (Tah t:figura.getTahy()){
            try{
                if (t.jeValidni(this, startRadek, startSloupec, 1)){
                    Sachovnice novaSachovnice = new Sachovnice(this.pozice, this.notace);
                    novaSachovnice.proved(this, t, startSloupec, startRadek);
                    if(!novaSachovnice.jeKralOhrozeny(this.pozice[startRadek][startSloupec].jeBily()))
                        ret.add(novaSachovnice);
                    
                }
            }catch(PrazdnePoleException e){
                
            }
            
        }
        
        return ret;
    }
    
    
    /**
     * Metoda upraví šachovnici provedením určitého tahu
     * @param puvodni původní šachovnice před prováděním tohoto tahu
     * @param t tah, který bude proveden
     * @param startSloupec absolutní pozice kamene, kterým je taženo
     * @param startRadek absolutní pozice kamene, kterým je taženo
     * @throws PrazdnePoleException Výjimka, když se program při validaci tahu zeptal na barvu prázdného pole
     */
    private void proved(Sachovnice puvodni, Tah t, int startSloupec, int startRadek) throws PrazdnePoleException {
        int[] start = {startRadek, startSloupec};
        
        
        int[] absCil = t.getAbsCil(this.barvaTokenu(startRadek, startSloupec), startRadek, startSloupec);
        String radekNotace = (puvodni.coJeNaPoli(startRadek, startSloupec).getZnak() +" "+ (char)(absCil[1]+97) + (absCil[0]+1) + " ");
        
        if(puvodni.coJeNaPoli(startSloupec, startRadek).jeCerny()){
            
            radekNotace+="\n";
        }
        this.posun(puvodni, start, absCil);
        this.notace += radekNotace;
        for (Efekt e:t.getEfekty())
            e.proved(puvodni, this, startRadek, startSloupec);
        
    }
    /**
     * Vrátí seznam polí ohrožených jedním z hráčů
     * @param bily barva hráče, který se ptá na ohrožená pole - hodnota true najde pole ohražená černým a naopak
     * @param cisloVolani číslo po kolikáté je tato funkce volána, zabraňuje přetečení zásobníku po rekurzivním volání dotázů na ohrožená pole
     * @return množina absolutních souřadnic polí, která jsou ohrožena pro daného hráče
     */
    private HashSet<int[]> ohrozenaPole(boolean bily, int cisloVolani){
        HashSet<int[]> ohrozenaPole = new HashSet<>();
        
        for (int i = 0; i<8;i++){
            for (int j = 0; j<8; j++){
                if ((this.pozice[i][j].jeBily()&&!bily) || (this.pozice[i][j].jeCerny()&&bily)){
                    for(Tah t:pozice[i][j].figura.getTahy())
                        try {
                            
                            ohrozenaPole.addAll(t.ohrozenaPole(this, i, j, cisloVolani));
                            
                        } catch (PrazdnePoleException ex) {
                            
                        }
                    
                }
            }
        }
        
        return ohrozenaPole;
    }
    
    /**
     * 
     * @param bily barva hráče, na jehož krále se ptáme
     * @return pravdivostní hodnota, zda je král tohoto hráče v šachu
     */
    private boolean jeKralOhrozeny(boolean bily){
        HashSet<int[]> ohrozenaPole = this.ohrozenaPole(bily,1);
        int[] poziceKrale = this.najdiKrale(bily);
        
        
        for (int[] i:ohrozenaPole){
            if(i.length==2){
                if(i[0]==poziceKrale[0] && i[1]==poziceKrale[1]){
                    return true;
                }
            }
        }
        return false;
    }
    /**
    *@return pravdivostní hodnota, zda je na šachovnici král od každé barvy
    */
    public boolean obsahujeObaKrale(){
        int[] bilyKral = this.najdiKrale(true);
        int[] cernyKral = this.najdiKrale(false);
        return bilyKral[0]!=-1 && cernyKral[0]!=-1;
    }
    /**
     * 
     * @param bily barva hráče, jehož krále hledáme
     * @return absolutní pozice krále hledaného hráče
     */
    private int[] najdiKrale(boolean bily) {
        int[] poziceKrale = {-1,-1};
        for (int radek = 0; radek<=7; radek++){
            for (int sloupec = 0; sloupec<=7; sloupec++){
                if (this.pozice[radek][sloupec].getZnak().equals("k") && this.pozice[radek][sloupec].jeBily() == bily){
                    poziceKrale[0] = radek;
                    poziceKrale[1] = sloupec;
                    
                    return poziceKrale;
                }
            }
        }
        return poziceKrale;
    }
    /**
     * Metoda hledající vítězné kombinace do daného počtu tahů
     * @param pocetTahu počet tahů, do jaké hloubky má metoda hledat řešení
     * @return všechny pozice, které bílý dovedl k vítězství i při bezchybné hře černého
     */
    public RadaPozic vyres(int pocetTahu){
        ArrayList<Sachovnice> ret = new ArrayList<>();
        if(pocetTahu == 1){
            for(Sachovnice budouciPozice:this.najdiVsechnyBudouciPozice(true)){
                if(budouciPozice.najdiVsechnyBudouciPozice(false).isEmpty() && budouciPozice.jeKralOhrozeny(false))
                    ret.add(budouciPozice);
            }
            return new RadaPozic(ret, !ret.isEmpty());
        }
        else{
            boolean resitelne = false;
            for(Sachovnice budouciPozice:this.najdiVsechnyBudouciPozice(true)){
                boolean spravnePokracovani = true;
                ArrayList<Sachovnice> castecnaReseni = new ArrayList<>();
                ArrayList<Sachovnice> vsechnyOdpovediCerneho = budouciPozice.najdiVsechnyBudouciPozice(false);
                
                if(vsechnyOdpovediCerneho.isEmpty()){
                    if(budouciPozice.jeKralOhrozeny(false)){
                        castecnaReseni.add(budouciPozice);
                        spravnePokracovani = true;
                    }
                }
                
                for(Sachovnice odpovedCerneho:vsechnyOdpovediCerneho){
                    RadaPozic moznaPokracovani = odpovedCerneho.vyres(pocetTahu-1);
                    if(moznaPokracovani.jeValidni()){                       
                        castecnaReseni.addAll(moznaPokracovani.getPozice());
                    }else{
                        spravnePokracovani = false;
                        break;
                    }
                }
                if(spravnePokracovani){
                    ret.addAll(castecnaReseni);
                    resitelne = true;
                }
            }
            return new RadaPozic(ret, resitelne);
        }
    /**
    *@param pozice zkoumané pole
    *@return pravdivostní hodnota, zda figurka na daném poli je král
    */    
    }
    public boolean jeToKral(int[] pozice){
        return this.coJeNaPoli(pozice).getZnak().equals("k");
    }
    /**
    *@param pozice zkoumané pole
    *@param barva barva hledaného krále
    *@return pravdivostní hodnota, zda figurka na daném poli je král určené barvy
    */
    public boolean jeToKralSBarvou(int[] pozice, boolean barva){
        return this.jeToKral(pozice)&&(this.coJeNaPoli(pozice).jeBily()==barva);
   }

    
}
