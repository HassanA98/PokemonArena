//Hassan Arshad
import java.util.*;
import java.io.*;

public class PokemonArena{
	
	private static ArrayList<Pokemon>allpokes; // will contain all the pokemon available and then after the user has selected their four pokemon this will become the enemy pokemon list
	private static ArrayList<Pokemon>userpokes = new ArrayList<Pokemon>(); // will containt the four user pokemons
	private static Pokemon userPokemon; // current user pokemon
	private static Pokemon enemyPokemon; // current enemy pokemon
	private static Scanner kb = new Scanner(System.in);
	public static Random torf = new Random();
	public static final int USER = 1;
	public static final int ENEMY = 2;
	public static int turn = torf.nextBoolean() ? USER : ENEMY;
	
	//File I/O to get the stats of Pokemon from the text file containing Pokemon information
	public static void load() throws IOException{
		Scanner inFile = new Scanner(new BufferedReader (new FileReader("pokemon.txt")));
				
		String line = inFile.nextLine();
		int amount = Integer.parseInt(line);
		
		for(int i = 0; i < amount; i++){
			allpokes.add(new Pokemon(inFile.nextLine()));
			
		}	
	}
	
	//This method will display all the Pokemon and their stats and make the user pick 4 different pokemon
	public static void pokePick(){
		int pokenum;
			
		for(int i = 0; i<4;i++){	
			for(int p = 0; p< allpokes.size(); p++){
				System.out.println(p+1 + ": " +allpokes.get(p) + " |Hp: " + allpokes.get(p).getHp() + " |Type: " + allpokes.get(p).getType() + "\n");
			}
			pokenum = kb.nextInt()-1;
			userpokes.add(allpokes.get(pokenum));
			System.out.println("\nYou picked " + allpokes.get(pokenum) +"\n");
			allpokes.remove(pokenum);
		}
		System.out.println("All pokemons you picked " + userpokes + "\n");
	}
	
	//Main battle class
	//Gets enemy pokemon and the user pokemon
	//Alternates turns between user and enemy pokemon
	//Will continue on working until one of the pokemons facing each other has fainted
	//at end of both turns does a turn refresh
	//at the end of the battle does a battle refresh
	public static void battle(){
		enemyPokemon = allpokes.get(0);
		allpokes.remove(0);
		userPokemon = getUserPoke();
		
		while(userpokes.size() != 0 && enemyPokemon.getHp() > 0){//will keep going until one of them faints(very brutal)
			for(int i = 1; i < 3; i++){
				if (turn == USER){
					turnOption();
				}
				else if(turn == ENEMY){
					enemyTurn();
				}
				turn = (turn == USER)?ENEMY:USER;
				
				if(checkStatus()==false){
					break;
				}
			}
			turnRefresh();
		}
		battleRefresh();
	}

	//Display the four user pokemons and makes the user pick one
	public static Pokemon getUserPoke(){
		System.out.println("You will face against " + enemyPokemon + " ||Hp: " + enemyPokemon.getHp() + " |Type: " + enemyPokemon.getType() + " |Weakness: " + enemyPokemon.getWeak() + " |Resistance: " + enemyPokemon.getResis() + "\n");
		System.out.println("Pick which Pokemon to use in the next battle: ");
		for(int i = 0; i<userpokes.size();i++){
			System.out.println("Type " + (i+1) + " for " + userpokes.get(i) + " ||Hp: " + userpokes.get(i).getHp() + " |Type: " + userpokes.get(i).getType() + " |Weakness: " + userpokes.get(i).getWeak() + " |Resistance: " + userpokes.get(i).getResis());
		}
		
		System.out.println("");
		userPokemon = userpokes.get(kb.nextInt() - 1);
		System.out.println("\nYou picked " + userPokemon);
		
		
		return userPokemon;
	
	}
	
	//Give the user 3 options of Attack, Retreat and Pass
	//Also if the pokemon is stunned it is not given the options and it's turn ends
	public static void turnOption(){
		if (userPokemon.getStun()){
			System.out.println(userPokemon + " is stunned! It cannot move!");
		}
		
		else{
			System.out.println("\nWhat would you like to do?:");
			System.out.println("To Attack type 1");
			System.out.println("To Retreat type 2");
			System.out.println("To Pass turn type 3\n");
			int move = kb.nextInt();
			
			if (move == 1){
				turnAttack();
			}	
			if (move == 2){
				turnSwitch();
			}
			if (move == 3){
				turnPass();
			}
		}
	}
	
	//Displays the attacks that the Pokemon can do and asks the user to choose which one
	//after which it calls the attack method in the Pokemon class
	//has the option to go back
	public static void turnAttack(){
		System.out.println("\nYou choose to attack\n");
		userPokemon.dispAtk();
		System.out.println("Type -1 to go back\n");
		int atknum = kb.nextInt()-1;
		if (atknum < -1){
			turnOption();
		}
		else{
			userPokemon.attack(atknum, enemyPokemon);
		}
		
	}
	
	//Displays the other user pokemons and switches the current pokemon with the one the user choose
	//has the option to go back
	public static void turnSwitch(){
		System.out.println("\nPick which Pokemon to switch with:");
		for (int i = 0; i < userpokes.size(); i++){
			if(userpokes.get(i) != userPokemon){
				System.out.println("Type "+i+ " for " + userpokes.get(i));
			}
		}
		System.out.println("Type -1 to go back\n");
		int neww = kb.nextInt();
		if (neww <= -1){
			turnOption();
		}
		else{
			System.out.println("\nYou switched " + userPokemon + " with " + userpokes.get(neww));
			userPokemon = userpokes.get(neww);
		}
		
	}
	
	//Simply ends the turn without doing anything
	public static void turnPass(){
		System.out.println("\nYou choose to pass this turn\n");
	}
	
	//calls the eattack method in the Pokemon class
	public static void enemyTurn(){
		enemyPokemon.eattack(userPokemon);
	}
	
	//after each turn CheckStatus will check to see if a Pokemon is still active
	//return true and false depending on weither the enemy pokemon is fainted or active
	public static boolean checkStatus(){
		//the user pokemon if fainted is immediately switched out and doesn't require a false return
		//unless all your pokemon fainted
		if(userPokemon.getHp() == 0){
			userpokes.remove(userPokemon);
			if(userpokes.size() == 0){
				return false;
			}
			else{
				turnSwitch();
				return true;
			}	
		}
		else if(enemyPokemon.getHp() == 0){
			return false;
		}
		return true;
	}
	
	//The following two method just call the battleRefresh and turnRefresh methods from the Pokemon class
	public static void turnRefresh(){
		for(int i = 0; i < userpokes.size(); i++){
			userpokes.get(i).turnRefresh();
		}
		enemyPokemon.turnRefresh();
	}
	
	public static void battleRefresh(){
		for(int i = 0; i < userpokes.size(); i++){
			userpokes.get(i).battleRefresh();	
		}
		enemyPokemon.battleRefresh();
	}
	
	//The end screen...
	public static void finale(){
		if(allpokes.size() == 0){
			System.out.println("Congratulations! You have beaten all the challengeing Pokemon. You are hereby proclaimed as Trainer Supreme!");
		}
		else{
			System.out.println("Sorry you were defeated. Better luck next time.");
		}
	}
	
	//Main method
	//loads the pokemon
	//picks the pokemon
	//run battle until all the userpokemon faint or all the enemy pokemon faint(again brutal)
	//displays the end screen
	public static void main(String[]args) throws IOException{
		allpokes = new ArrayList<Pokemon>();
		load();
		pokePick();
		Collections.shuffle(allpokes);
		
		while(allpokes.size()>0 && userpokes.size()>0){
			battle();
		}
		
		finale();
	}
}