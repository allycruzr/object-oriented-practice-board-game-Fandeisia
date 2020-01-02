package pt.ulusofona.lp2.fandeisiaGame;

import java.io.File;
import java.sql.SQLOutput;
import java.util.*;
import java.io.*;
import java.util.concurrent.ThreadLocalRandom;
public class FandeisiaGameManager{

    public FandeisiaGameManager(){
    }
     List<Treasure> treasures = new ArrayList<>(); // Quando for 0 gameIsOver = true.
     List<Hole> holes = new ArrayList<>();
     List<Creature> creatures = new ArrayList<>();
     Team teamLdr = new Team (10, "LDR");
     Team teamRes = new Team (20, "RESISTENCIA");
     Team currentTeam = new Team(0,"0"); // uma espécie de cópia...? mas funciona.
     int rows;
     int columns;
     int turnsWithoutTreasure; // Será usado no gameIsOver. Quando for for >= 15 gameIsOver = true;
     long logCounter = 0; // usado como contador do meu log de execução do jogo
     void setRows(int rows){
        this.rows = rows;
     }
     void setColumns(int columns){
        this.columns = columns;
     }
     int turnCounter = 0;
     boolean iaActive;

    // Dado binário (0 ou 1)
    int rollDiceBinary(){ return ThreadLocalRandom.current().nextInt(1 );
    } // Usado para sortear time que começa o jogo

    // Contador para as linhas impressas no console vindas das funções. Apenas para utilizar no desenvolvimento.
    long iterate(long i){
        if (i < 2147483647){
        logCounter++;
        return logCounter;
        }else {
            logCounter = -2000000000;
        return logCounter;
        }
    }

    public void toggleAI(boolean active){
        if (active){
            active =false;
        } else {
            active = true;
        }
    }

    public String[][] getCreatureTypes(){
        //System.out.println( iterate(logCounter) + " - "+ "IN getCreatureTypes\n -----------------------------------\n");
        return new String[][]{
                {"Anao", "Anao.png", "add description", String.valueOf(1)},
                {"Dragao", "Dragao.png", "add description", String.valueOf(9)},
                {"Elfo", "Elfo.png", "add description", String.valueOf(5)},
                {"Gigante", "Gigante.png", "add description", String.valueOf(5)},
                {"Humano", "Humano.png", "add description", String.valueOf(3)},
        };
    }

    public Map<String, Integer> createComputerArmy(){
        //System.out.println( iterate(logCounter) + " - "+"IN createComputerArmy\n -----------------------------------\n");

        Map<String, Integer> computerArmy = new HashMap<>();

        // Criando 1 apenas para teste.
        computerArmy.put("Anao", new Random().nextInt(4)); // criar um random entre 0 e 3.
        computerArmy.put("Dragao", new Random().nextInt(4));
        computerArmy.put("Elfo", new Random().nextInt(4));
        computerArmy.put("Gigante", new Random().nextInt(4));
        computerArmy.put("Humano", new Random().nextInt(4));
        //computerArmy.put("Dragao", 1);
        return computerArmy;
    } //OK 29-12

    public int startGame(String[] content, int rows, int columns){
        //System.out.println( iterate(logCounter) + " - "+"IN startGame\n -----------------------------------\n");
        teamLdr = new Team (10,"LDR");
        teamRes = new Team (20, "RESISTENCIA");

        setRows(rows);
        setColumns(columns);

        for(String line: content){

            String[] individual_line = line.split(", ");

            if (individual_line.length >= 6 ) { // É criatura

                String[] detach_colon = individual_line[0].split(": ");
                int id = Integer.parseInt(detach_colon[1]);

                String[] aux_type = individual_line[1].split(": ");
                String type = aux_type[1];

                String[] aux_teamId = individual_line[2].split(": ");
                String aux_teamID = aux_teamId[1];
                int teamId = Integer.parseInt(aux_teamID);

                String[] aux_X = individual_line[3].split(": ");
                String x_string = aux_X[1];
                int x = Integer.parseInt(x_string);

                String[] aux_Y = individual_line[4].split(": ");
                String y_string = aux_Y[1];
                int y = Integer.parseInt(y_string);

                String[] aux_orientation = individual_line[5].split(": ");
                String orientation = aux_orientation[1];

                switch (type) {
                    case ("Anao") : creatures.add(new Dwarf(id, x, y, teamId, 1, orientation));
                        break;
                    case("Dragao") : creatures.add(new Dragon(id, x, y, teamId, 9, orientation));
                        break;
                    case("Elfo") : creatures.add(new Elf(id, x, y, teamId, 5, orientation));
                        break;
                    case("Gigante") : creatures.add(new Giant(id, x, y, teamId, 5, orientation));
                        break;
                    case("Humano") : creatures.add(new Human(id, x, y, teamId, 3, orientation));
                        break;
                }
            }

            if (individual_line[1].contains("hole") || individual_line[1].contains("gold") || individual_line[1].contains("silver") || individual_line[1].contains("bronze")){
                String[] detach_colon = individual_line[0].split(": ");
                int id = Integer.parseInt(detach_colon[1]);

                String[] aux_type = individual_line[1].split(": ");
                String type = aux_type[1];

                String[] aux_X = individual_line[2].split(": ");
                String x_string = aux_X[1];
                int x = Integer.parseInt(x_string);

                String[] aux_Y = individual_line[3].split(": ");
                String y_string = aux_Y[1];
                int y = Integer.parseInt(y_string);

                if (type.equals("hole")){
                    holes.add(new Hole (id, x, y));
                } else {
                    if(type.equals("gold")){
                        treasures.add(new Treasure (id, x, y, 3));
                    }
                    if(type.equals("silver")){
                        treasures.add(new Treasure (id, x, y, 2));
                    }
                    if(type.equals("bronze")){
                        treasures.add(new Treasure (id, x, y, 1));
                    }
                }
            }
        }

        /*IBAGENS*/
        // Set initial orientation and team image
        // Gera imagens diferentes para criaturas da Resistencia.  -- 1 coelho ou 2? - Change filter to a better later. mmaybe sobel filter
        for (Creature creature: creatures){
            if (creature.getTeamId() == 20){
                creature.setImage(creature.getTypeName()+"Negate-"+creature.getOrientation()+".png");
            } else {
                creature.setImage(creature.getTypeName()+"-"+creature.getOrientation()+".png");
            }
        }


       /* Prints dos elementos fatiados e separados. */
        //System.out.println(iterate(logCounter) + " - "+"  LISTA DE CRIATURAS IN START GAME: "+ creatures + "\n");
        //System.out.println(iterate(logCounter) + " - "+"  LISTA DE BURACOS IN START GAME: " + holes + "\n");
        //System.out.println(iterate(logCounter) + " - "+"  LISTA DE TESOUROS IN START GAME: " + treasures + "\n");
        //System.out.println(iterate(logCounter) + " - "+"  ID DE RESISTENCIA IN START GAME: " + teamRes.getId() + "\n");
        //System.out.println(iterate(logCounter) + " - "+"  ID DE LDR IN START GAME:" + teamLdr.getId() + "\n");
        //System.out.println(iterate(logCounter) + " - "+"  HASH DE COMPUTER ARMY IN START GAME: " + createComputerArmy() + "\n\n");

        /* Subtrai moedas para cada criatura de cada time*/
        for (Creature c: creatures){
            switch (c.getTeamId()) {
                case 10: teamLdr.removeCoins(c.getCost());
                    break;
                case 20: teamRes.removeCoins(c.getCost());
                    break;
            }
        }

        if (teamLdr.getCoins() < 0 && teamRes.getCoins() < 0){
            return 1;
        } else if(teamLdr.getCoins() < 0){
            return 2;
        } else if(teamRes.getCoins() < 0){
            return 3;
        }
        return 0;

    }

    public int getCurrentScore(int teamId){
        //System.out.println(iterate(logCounter) + " - "+"IN getCurrentScore\n -----------------------------------\n");
        //System.out.println(iterate(logCounter) + " - "+"  teamLdr.getId() in CUURRENTSCORE: " +teamLdr.getId());
        //System.out.println(iterate(logCounter) + " - "+"  teamRes.getId() in CUURRENTSCORE: " +teamRes.getId() + "\n");

        if (teamId == teamLdr.getId()){
            return teamLdr.getPoints();
        }else {
            return teamRes.getPoints();
        }
    } // Está sendo chamada duas vezes seguidas no Visualizador. Depois é chamadas mais 2 vezes.

    public int getCoinTotal(int teamId){
        //System.out.println(iterate(logCounter) + " - "+"IN getCoinTotal\n -----------------------------------\n");

        if (teamId == 10){
            //System.out.println(iterate(logCounter) + " - "+"teamLdr.getCoins(): "  + teamLdr.getCoins() + "\n");
            return teamLdr.getCoins();
        } else{
            //System.out.println(iterate(logCounter) + " - "+"  teamRes.getCoins() " + teamRes.getCoins() + "\n");
            return teamRes.getCoins();
        }
    } //OK 29-12 Também se repete depois de escolher exercito

    public void setInitialTeam(int teamId){
        //System.out.println(iterate(logCounter) + " - "+"Entrou em setInitialTeam\n -----------------------------------\n\n");

        /*System.out.println(" \n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n############################## BEM VINDO AO FANDEISIA GAME ##############################\n\n");
        System.out.println("ESCOLHA QUAL TIME IRÁ COMEÇAR JOGANDO.");
        System.out.print("DIGITE 1 PARA LDR, 2 PARA RESISTENCIA:");
        Scanner keyboard = new Scanner (System.in);
        int choice = keyboard.nextInt();
        if (choice == 1 ){
            currentTeam = teamLdr;
        } else {
            currentTeam = teamRes;
        }*/
        // Seleção aleatória com dado
         int resultDice = rollDiceBinary();
         if (resultDice == 0){
             currentTeam = teamLdr;
         }else {
             currentTeam = teamRes;
         }
    } //OK 29-12 -- TODO WTF is to do here?

    public int getCurrentTeamId(){
        //System.out.println(iterate(logCounter) + " - "+"IN currentTeamId\n -----------------------------------\n");
        //System.out.println(iterate(logCounter) + " - "+"  currentTeam.getId(): "+ currentTeam.getId() + "\n");
        return currentTeam.getId();
    } //OK 29-12

    public List<Creature> getCreatures(){
        //System.out.println(iterate(logCounter) + " - "+"IN getCreatures");
        System.out.println(iterate(logCounter) + " - "+"    Lista de criaturas do mundo: "+ creatures);
        return creatures;
    } // OK 29-12

    public int getElementId(int x, int y){
        //System.out.println(iterate(logCounter) + " - "+"IN getElementId("+x+","+y+")");

        //System.out.println("    Dimensões do mundo (rows e columns): " + ROWS + " e " + COLUMNS);

        List<Element> elements = new ArrayList<>();
        elements.addAll(creatures);
        elements.addAll(treasures);
        elements.addAll(holes);

        for(Element e: elements){
            if(e.getX() == x && e.getY() == y){
                return e.getId();
            }
        }
        // System.out.println("\n");
        return 0;
    } //OK 29-12 percorre tabuleiro e quando acha uma criatura chama o getSpell();

    public String[][] getSpellTypes(){
        //System.out.println(iterate(logCounter) + " - "+"IN getSpellTypes");
        return new String[][]{
                {"EmpurraParaNorte", "Descrição do feitiço", String.valueOf(1)},
                {"EmpurraParaEste", "Descrição do feitiço", String.valueOf(1)},
                {"EmpurraParaSul", "Descrição do feitiço", String.valueOf(1)},
                {"EmpurraParaOeste", "Descrição do feitiço", String.valueOf(1)},
                {"ReduzAlcance", "Descrição do feitiço", String.valueOf(2)},
                {"DuplicaAlcance", "Descrição do feitiço", String.valueOf(3)},
                {"Congela", "Descrição do feitiço", String.valueOf(3)},
                {"Congela4Ever", "Descrição do feitiço", String.valueOf(10)},
                {"Descongela", "Descrição do feitiço", String.valueOf(8)},
        };
    }

    public String getSpell (int x, int y){
        // System.out.println(iterate(logCounter) + " - "+"IN getSpell");
        for(Creature creature: creatures){
            if(creature.getX() == x && creature.getY() == y){
                String spellName = creature.getItSpellName();
                if (spellName != null){ // <<------------------ não pode ser null
                    return spellName;
                }
            }
        }
        //System.out.println("ERRO - retornou null em GetSpell()");
        return null; // É suposto que o simulador chame apenas quando há criatura na coordenada.
    } //Scanner dos spells de todas as criaturas presentes. Ela que marca a varinha na criatura que retornou true para enchant! ok.

    public boolean enchant (int x, int y, String spellName){
        //System.out.println(iterate(logCounter) + " - "+"Entrou em enchant");
        for (Creature c: creatures){
            if (c.getX() == x && c.getY() == y) {
                assert spellName != null;
                if (c.isFrozen4Ever() && spellName.equals("unfreezes")){
                    if (checkBalanceToSpell(getCurrentTeamId(),8)){
                        c.setEnchant(true);
                        c.setItSpellName(spellName);
                        taxSpell(getCurrentTeamId(), 8);
                        return true;
                    }
                } else {
                    switch (spellName){
                        case ("freezes"): {
                            if (checkBalanceToSpell(getCurrentTeamId(),3)){
                                c.setEnchant(true);
                                taxSpell(getCurrentTeamId(), 3);
                                c.setItSpellName(spellName);
                                //c.freezes();
                                //c.setItSpellName("freezes");
                                return true;
                            } else {
                                return false;
                            }
                        }

                        case ("freezes4Ever"):{
                            if (checkBalanceToSpell(getCurrentTeamId(),10)){
                                c.setEnchant(true);
                                //c.freezes4Ever();
                                //c.setItSpellName("freezes4Ever");
                                taxSpell(getCurrentTeamId(), 10);
                                c.setItSpellName(spellName);
                                return true;
                            } else {
                                return false;
                            }
                        }

                        case ("unfreezes"):{
                            if (checkBalanceToSpell(getCurrentTeamId(),8)){
                                c.setEnchant(true);
                                //c.freezes();
                                //c.setItSpellName("unfreezes");
                                taxSpell(getCurrentTeamId(), 8);
                                c.setItSpellName(spellName);
                                return true;
                            } else {
                                return false; // Neste caso não se faz unfreeze porque se tivesse mesmo frozen4Ever teria unfrozed lá em cima!
                            }
                        }

                        case ("pushNorth"): {
                            if (!c.isFrozen() && !c.isFrozen4Ever()){
                                c.setNextX(x);
                                c.setNextY(y-1);
                                if (validateMovement(x, y, c.getNextX(), c.getNextY())){ // movimento é valido
                                    if (checkBalanceToSpell(getCurrentTeamId(),1)){
                                        c.setEnchant(true);
                                        //c.freezes();
                                        //c.setItSpellName("pushNorth");
                                        taxSpell(getCurrentTeamId(), 1);
                                        c.setItSpellName(spellName);
                                        return true;
                                    } else {
                                        return false;
                                    }
                                } else {
                                    return false; // Se não entrar nesse if tem erro!
                                }
                            } return false;
                        }

                        case ("pushEast"): {
                            if (!c.isFrozen() && !c.isFrozen4Ever()){
                                c.setNextX(x+1);
                                c.setNextY(y);
                                if (validateMovement(x, y, c.getNextX(), c.getNextY())){ // movimento é valido
                                    if (checkBalanceToSpell(getCurrentTeamId(),1)){
                                        c.setEnchant(true);
                                        //c.setItSpellName("pushEast");
                                        //c.pushEast();
                                        taxSpell(getCurrentTeamId(),1);
                                        c.setItSpellName(spellName);
                                        return true;
                                    } else {
                                        return false;
                                    }
                                } else {
                                    return false; // Se não entrar nesse if tem erro!
                                }
                            } return false;
                        }

                        case ("pushSouth"): {
                            if (!c.isFrozen() && !c.isFrozen4Ever()){
                                c.setNextX(x);
                                c.setNextY(y+1);
                                if (validateMovement(x, y, c.getNextX(), c.getNextY())){ // movimento é valido
                                    if (checkBalanceToSpell(getCurrentTeamId(),1)){
                                        c.setEnchant(true);
                                        //c.freezes();
                                        //c.setItSpellName("pushSouth");
                                        taxSpell(getCurrentTeamId(), 1);
                                        c.setItSpellName(spellName);
                                        return true;
                                    } else {
                                        return false;
                                    }
                                } else {
                                    return false;
                                }
                            } return false;
                        }

                        case ("pushWest"): {
                            if (!c.isFrozen() && !c.isFrozen4Ever()){
                                c.setNextX(x-1);
                                c.setNextY(y);
                                if (validateMovement(x, y, c.getNextX(), c.getNextY())){ // movimento é valido
                                    if (checkBalanceToSpell(getCurrentTeamId(),1)){
                                        c.setEnchant(true);
                                        //c.freezes();
                                        //c.setItSpellName("pushWest");
                                        taxSpell(getCurrentTeamId(), 1);
                                        c.setItSpellName(spellName);
                                        return true;
                                    } else {
                                        return false;
                                    }
                                } else {
                                    return false;
                                }
                            } return false;
                        }

                        case ("reducesRange"): {
                            if (checkBalanceToSpell(getCurrentTeamId(),1)){
                                c.setEnchant(true);
                                taxSpell(getCurrentTeamId(), 2);
                                c.setItSpellName(spellName);
                                //c.reducesRange();
                                //c.setItSpellName("reducesRange");
                                return true;
                            } else {
                                return false;
                            }
                        }

                        case ("doubleRange"): {
                            if (checkBalanceToSpell(getCurrentTeamId(),1)){
                                c.setEnchant(true);
                                taxSpell(getCurrentTeamId(), 3);
                                c.setItSpellName(spellName);
                                //c.doubleRange(c.getRange());
                                //c.setItSpellName("doubleRange");
                                return true;
                            } else {
                                return false;
                            }
                        }
                    }
                }
                // } else {
                // O feitiço leva para um lugar ocupado ou buraco ou para fora do tabuleiro? --> Retorna falso
                // A criatura está congelada ou congelada4ever? --> Retorna falso
                // Se tá congelada4ever e spellName == descongela return true e c.isFrozen(c.getId()) = false;
                // Outras casos retorna true.
                // c.

                // return true;
            }

        } return false; // Porque nesse x e y não há criatura.

    } // ok 01/01

    public void processTurn(){ // TODO
        System.out.println("Entrou em  processTurn\n");
        //switchCurrentTeam();
        turnCounter = turnCounter +1;
        System.out.println(turnCounter);
        turnsWithoutTreasure =+turnsWithoutTreasure; // zera toda vez que encontra um tesouro
        for (Creature creature: creatures){

            // Timer para descongelar
            if (creature.isFrozen()){
                creature.setFrozenTime(1);
            }

            // Se atingir timer descongela e troca figura
            if (creature.isFrozen() && creature.getFrozenTime() == 1){
                creature.setFrozen(false);
                creature.setImage(creature.getTypeName()+"-"+ creature.getOrientation()+".png");
            }

            // Se tem feitiço pra aplicar, aplique.
            if (creature.isEnchant()){
                executeSpell(creature.getId(), creature.getItSpellName());
                if(matchTreasure(creature.getX(), creature.getY(), creature.getId(), creature.getTeamId())){
                    turnsWithoutTreasure =0;
                }
                creature.setEnchant(false);
            }

            /* Movimento normal das criaturas */
            // Se não tá congelada ou congelada4Ever não movimenta. Se não, bota pra movimentar.
            if (!creature.isFrozen4Ever() && !creature.isFrozen()){
                if (executeStandardMovement(creature.getX(), creature.getY(), creature.getOrientation(), creature.getTypeName())){
                    creature.move();
                    if(matchTreasure(creature.getX(), creature.getY(), creature.getId(), creature.getTeamId())){
                        turnsWithoutTreasure =0;
                    }
                } else {
                    creature.spin();
                }
            }
        }
    }

    private boolean executeStandardMovement(int x, int y, String orientation, String typeName) {
        System.out.println("Entrou em executeStandardMovement");
        for (Creature creature: creatures){
            if (creature.getX() == x && creature.getY()==y){
                switch (creature.getTypeName()){

                    case ("Anao"):case("AnaoNegate"):case("Humano"):case("HumanoNegate") : {
                        switch (creature.getOrientation()){
                            case ("Norte"):{
                                creature.setNextX(creature.getX());
                                creature.setNextY(creature.getY() - creature.getRange());
                                if (validateMovement(x,y,creature.getNextX(),creature.getNextY())){
                                    return true;
                                } else {
                                    return false;
                                }
                            }
                            case ("Sul"):{
                                creature.setNextX(creature.getX());
                                creature.setNextY(creature.getY() + creature.getRange());
                                if (validateMovement(x,y,creature.getNextX(),creature.getNextY())){
                                    return true;
                                } else {
                                    return false;
                                }
                            }
                            case ("Este"):{
                                creature.setNextX(creature.getX() + creature.getRange());
                                creature.setNextY(creature.getY());
                                if (validateMovement(x,y,creature.getNextX(),creature.getNextY())){
                                    return true;
                                } else {
                                    return false;
                                }
                            }
                            case ("Oeste"):{
                                creature.setNextX(creature.getX() - creature.getRange());
                                creature.setNextY(creature.getY());
                                if (validateMovement(x,y,creature.getNextX(),creature.getNextY())){
                                    return true;
                                } else {
                                    return false;
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean matchTreasure(int x, int y, int id, int teamId) {
        System.out.println("Entrou em matchTreasure");
        for (Creature creature: creatures){
            if (creature.getId() == id){
                //for (Treasure treasure: treasures){
                for (Iterator<Treasure> i = treasures.iterator(); i.hasNext();){ // Artifício muito louco para passar do ERRO ConcurrentModificationException
                    Treasure treasure = i.next();
                    if (x == treasure.getX() && y == treasure.getY()){ // MATCH
                        creature.addPoints(treasure.getPoints()); // Add pontos criatura
                        if (treasure.getPoints() ==3){
                            creature.addGold();
                        }
                        if (treasure.getPoints() ==2){
                            creature.addSilver();
                        }
                        if (treasure.getPoints() ==1){
                            creature.addBronze();
                        }
                        if (teamId == 10){
                            teamLdr.addPoints(treasure.getPoints()); // Add pontos time
                        } else {
                            teamRes.addPoints(treasure.getPoints()); // Add pontos time
                        }
                        i.remove();
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void executeSpell(int id,String spell) {
        for (Creature creature : creatures){
            if (creature.getId() == id){
                switch(spell){
                    case ("unfreezes"): {
                        creature.unfreezes();
                        creature.setImage(creature.getTypeName()+"-"+ creature.getOrientation()+".png");
                        creature.setItSpellName(null);
                        break;
                    }
                    case ("freezes"): {
                        creature.freezes();
                        creature.setImage(creature.getTypeName() + "-Frozen.png");
                        creature.setItSpellName(null);
                        break;
                    }
                    case ("freezes4Ever") : {
                        creature.freezes4Ever();
                        creature.setImage(creature.getTypeName() + "-Frozen4Ever.png");
                        creature.setItSpellName(null);
                        break;
                    }
                    case ("pushNorth"): {
                        if (!creature.isFrozen() && !creature.isFrozen4Ever()){
                            if (validateMovement(creature.getX(), creature.getY(), creature.getX(),creature.getY()-1)) {
                                creature.pushNorth();
                                creature.setItSpellName(null);
                                creature.setOrientation("Norte");
                                creature.setImage(creature.getTypeName()+"-Norte.png");
                            }
                        }
                        break;
                    }
                    case ("pushEast"): {
                        if (!creature.isFrozen() && !creature.isFrozen4Ever()){
                            if (validateMovement(creature.getX(), creature.getY(), creature.getX()+1,creature.getY())) {
                                creature.pushEast();
                                creature.setItSpellName(null); // Já foi executado o feitiço, então passa a ficar em estado desencantado.
                                creature.setOrientation("East");
                                creature.setImage(creature.getTypeName()+"-Este.png");
                            }
                        }
                        break;
                    }
                    case ("pushSouth"): {
                        if (!creature.isFrozen() && !creature.isFrozen4Ever()){
                            if (validateMovement(creature.getX(), creature.getY(), creature.getX(),creature.getY()+1)) {
                                creature.pushSouth();
                                creature.setItSpellName(null); // Já foi executado o feitiço, então passa a ficar em estado desencantado.
                                creature.setOrientation("Sul");
                                creature.setImage(creature.getTypeName()+"-Sul.png");
                            }
                        }
                        break;
                    }
                    case ("pushWest"): {
                        if (!creature.isFrozen() && !creature.isFrozen4Ever()){
                            if (validateMovement(creature.getX(), creature.getY(), creature.getX()-1,creature.getY())) {
                                creature.pushWest();
                                creature.setItSpellName(null); // Já foi executado o feitiço, então passa a ficar em estado desencantado.
                                creature.setOrientation("Oeste");
                                creature.setImage(creature.getTypeName()+"-Oeste.png");
                            }
                        }
                        break;
                    }
                    case ("reducesRange"): {
                        creature.reducesRange();
                        creature.setItSpellName(null);
                        break;
                    }
                    case ("doubleRange"): {
                        creature.doubleRange();
                        creature.setItSpellName(null);
                        break;
                    }
                }
            }
        }
    }

    private boolean validateMovement(int x, int y, int nextX, int nextY) {

        if (nextX < 0 || nextY < 0){ // fora da tela
            return false;
        }
        if (nextX > columns-1 || nextY > rows-1){ // fora da tela
            return false;
        }

        if (getElementId(nextX, nextY) <=-500){ // buraco
            return false;
        }

        /*outra criatura */
        return getElementId(nextX, nextY) <= 0;
    }

    // Checar saldo;
    private boolean checkBalanceToSpell(int teamId, int spellCost) {

        if (teamId == 10){
            return teamLdr.checkBalanceToSpell(spellCost);
        }
        if (teamId == 20){
            return teamRes.checkBalanceToSpell(spellCost);
        }
        System.out.println(iterate(logCounter) + " - "+"O teamId passado não é válido. Impossível consultar saldo ");
        return false;

    }

    // Debita moedas dos feitiços
    private void taxSpell(int teamId, int spellCost) {
        if (teamId == 10){
            teamLdr.removeCoins(spellCost);
            System.out.println(iterate(logCounter) + " - "+"Spell taxed from LORD ELDER");

        }
        if (teamId == 20){
            teamRes.removeCoins(spellCost);
            System.out.println(iterate(logCounter) + " - "+"Spell taxed from RESISTENCIA:");
        }
    }

    private void switchCurrentTeam() {
        if (currentTeam.equals(teamLdr)){
            currentTeam = teamRes;
        } else {
            currentTeam = teamLdr;
        }
    }

    public boolean gameIsOver(){
        System.out.println("Entrou em gameIsOver");

        if (treasures.size() == 0 || turnsWithoutTreasure >=15){
            return true;
        }

        if (impossibleToWin(teamLdr.getPoints(),teamRes.getPoints(), sumPointsTreasuresNotFounds())){
            return true;
        }
        return false;

    }

    private boolean impossibleToWin(int pointsLdr, int pointsRes, int sum) {
        if (pointsLdr + sum < pointsRes){
            return true;
        }
        if (pointsRes + sum < pointsLdr){
            return true;
        }
        return false;
    }

    private int sumPointsTreasuresNotFounds() {
        int sum = 0;
        for (Treasure treasure: treasures){
            sum = sum + treasure.getPoints();
        }
        return sum;
    }

    public List<String> getResults(){
        List<String> results = new ArrayList<>();

        if (teamLdr.getPoints() > teamRes.getPoints()){
            results.add("Welcome to FANDEISIA");
            results.add("Resultado: Vitória da equipa " + teamLdr.getName());
            results.add("LDR: "+ teamLdr.getPoints());
            results.add("RESISTENCIA: "+ teamRes.getPoints());
            results.add("Nr. de Turnos jogados: "+ turnCounter);
            results.add("-----");
            for (Creature c: creatures){
                results.add(c.getId() +" : " + c.getTypeName() + " : " + c.getGold() + " : " + c.getSilver() + " : " + c.getBronze() + " : " + c.getPoints());
            }
        } else {
            if (teamRes.getPoints() == teamLdr.getPoints()){
                results.add("Welcome to FANDEISIA");
                results.add("Resultado: EMPATE");
                results.add("LDR: "+ teamLdr.getPoints());
                results.add("RESISTENCIA: "+ teamRes.getPoints());
                results.add("Nr. de Turnos jogados: "+ turnCounter);
                results.add("-----");
                for (Creature c: creatures){
                    results.add(c.getId() +" : " + c.getTypeName() + " : " + c.getGold() + " : " + c.getSilver() + " : " + c.getBronze() + " : " + c.getPoints());
                }
            }else {
                results.add("Welcome to FANDEISIA");
                results.add("Resultado: Vitória da equipa " + teamRes.getName());
                results.add("RESISTENCIA: "+ teamRes.getPoints());
                results.add("LDR: "+ teamLdr.getPoints());
                results.add("Nr. de Turnos jogados: "+ turnCounter);
                results.add("-----");
                for (Creature c: creatures){
                    results.add(c.getId() +" : " + c.getTypeName() + " : " + c.getGold() + " : " + c.getSilver() + " : " + c.getBronze() + " : " + c.getPoints());
                }
            }
        }

        return results;

    }

    // Save e Load Game:
    public boolean saveGame (File fich){
        System.out.println("Estou em saveGame");
        return true;
    }
    public boolean loadGame (File fich){
        System.out.println("Estou em loadGame");

        return true;
    }

    //Não tem que mexer mais:
    public String whoIsLordEder(){
        System.out.println("Estou em whoIsLordEder");

        return "Éderzito António Macedo Lopes";
    } //OK 29-12
    public List<String> getAuthors(){
        return Collections.singletonList("Allyson Rodrigues");
    }
}
