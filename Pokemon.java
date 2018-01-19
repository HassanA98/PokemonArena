//Hassan Arshad

import java.util.*;

public class Pokemon{
	
	// <name>,<hp>,<type>,<resistance>,<weakness>,<num attacks>,[<attack name>, <energy cost>,<damage>,<special>]
	private String name,type,weak,resis,status;
	private int maxhp,hp,mnumbers,amoves,energy;
	private Attack [] attacks;
	private boolean stun,disable;
	private static Random rnd = new Random(); 
	
	//Populate all the fields
	public Pokemon(String stats){
		String [] items = stats.split(",");
		name = items[0];
		hp = Integer.parseInt(items[1]);
		maxhp = hp;
		type = items[2];
		resis = items[3];
		weak = items[4];
		amoves = Integer.parseInt(items[5]);
		attacks = new Attack[amoves];
		status = "";
		energy = 50;
		stun = false;
		disable = false;
		
		for(int i = 0; i < amoves; i++){
			//                      name         cost                           dmg                            special
			attacks[i] = new Attack(items[6+i*4],Integer.parseInt(items[7+i*4]),Integer.parseInt(items[8+i*4]),items[9+i*4]);
		}
		
	}
	
	//Display the possible attacks the pokemon can do
	public void dispAtk(){
		System.out.println(name + " can do these attacks: ");
		for(int i = 0; i<attacks.length;i++){
			System.out.println(attacks[i].getName() + " ||Damage: " + attacks[i].getDmg() + " Cost: " + attacks[i].getCost() + " Special: " + attacks[i].getSpec() + "(Type "+(i+1)+" to choose this attack)");
		}
		
	}
	
	//Gets a random boolean(true or false) which help in creating a 50% chance
	public static boolean getRandomBoolean() {
       return rnd.nextBoolean();
   	}
	
	//The enemy attack method is similar to the nor attack method but here which attack the enemy is going to make is decided based on it's remaining energy
	public void eattack(Pokemon defender){
		int atknum = 0;

		for(int i = 0; i <attacks.length; i++){
			if (attacks[i].getCost() <= this.energy){
				atknum = i;
			}
		}
		
		System.out.println(this.name + " used " + attacks[atknum].getName());
		int dmg = attackCalc(atknum, defender);
		defender.hp -= dmg;
		defender.hp = (defender.hp <= 0)?0:defender.hp;
		System.out.println("It did " + dmg +" damage to " + defender);
		if(defender.hp == 0){
			System.out.println(defender + " has fainted\n");
		}
		else{
			System.out.println(defender + " has " + defender.hp + " HP left");
		}
	}
	
	//This method gets the damage that is dealt and then applies it to the defending pokemon while also displaying
	//how much damage was dealt, what attack was it and how much hp is left
	public void attack(int atknum, Pokemon defender){
		System.out.println(this.name + " used " + attacks[atknum].getName());
		int dmg = attackCalc(atknum, defender);
		defender.hp -= dmg;
		defender.hp = (defender.hp <= 0)?0:defender.hp;
		System.out.println(this.name + " did " + dmg+" damage to " + defender);
		if(defender.hp == 0){
			System.out.println(defender + " has fainted\n");
		}
		else{
			System.out.println(defender + " has " + defender.hp + " HP left");
		}
	}
	
	/*In this method we will call the damage that the attacking pokemon will do to the defendeing Pokemon
	 *First we will check if the pokemon has enough energy to use the move or if it is stunned, it is neither
	 *then it will attack.*/
	public int attackCalc(int atknum, Pokemon defender){
		
		int finaldmg = 0;
		boolean what = getRandomBoolean();//help produce a 50% chance
		if (attacks[atknum].getCost()<=this.energy && this.stun == false){
			this.energy -= attacks[atknum].getCost();
			finaldmg = attacks[atknum].getDmg(); //get base dmg
			
			//check for resistance and weakness
			finaldmg *= (defender.weak.equals(this.type))?2:1;
			finaldmg /= (defender.resis.equals(this.type))?2:1;
			finaldmg -= (this.disable = true)?0:10;
			
			//Next apply the appropriate special associated with each attack
			if (attacks[atknum].getSpec().equals("wild card")){//50% chance of working
				if(what == false){
					finaldmg = 0;
					System.out.println("Wild Card not successful");
				}
			}
			if (attacks[atknum].getSpec().equals("disable")){
				defender.disable = true;
				System.out.println("It disabled " + defender);
			}
			if (attacks[atknum].getSpec().equals("stun")){
				if(what == true){
					defender.stun = true;
					System.out.println(defender + " was stunned");
				}
				
			}
			if (attacks[atknum].getSpec().equals("recharge")){
				int addition = (this.energy+20 < 50)?20:0;   //energy cannot go above 50
				this.energy += addition;
				System.out.println(this.name + " gained 20 energy");
			}
			if (attacks[atknum].getSpec().equals("wild storm")){   //50% chance of hitting
				if(what == false){
					System.out.println(" Wild Storm not successful");
					finaldmg = 0;
				}
				else{
					System.out.println("Wild Storm successful");
					while(what){								//the wild storm has a 50% chance of hitting again
						System.out.println("Successful Hit!");
						what = getRandomBoolean();
						finaldmg += finaldmg;
					}
				}
			}
		}
		else{
			if(this.stun == true){System.out.println(this.name + " is stunned it can not move");}
			else{System.out.println(this.name + " is too tired to do that!");}
		}
		
		return (finaldmg < 0)?0:finaldmg; //check so that dmg is not returned as negative in some cases
	}
	
	//refresh required after the enemy and user turn, restores energy and removes stun
	public void turnRefresh(){
		int tenergy = this.energy;
		tenergy += (tenergy + 10 > 50)?0:10;
		this.energy = tenergy;
		this.stun = false;
	}
	
	//refresh after end of battle. restores energy, hp and removes all debuffs
	public void battleRefresh(){
		int temphp = this.hp;
		temphp += (temphp + 20 > maxhp)? (maxhp-temphp):20;
		this.energy = 50;
		this.hp = temphp;
		this.disable = false;
		this.stun = false;

	}
	
	//the following are get/set methods for some of the fields of this class
	public int getHp(){
		return this.hp;
	}
	public void setHp(int hp){
		this.hp = hp;
	}
	
	public String getType(){
		return this.type;
	}
	
	public String getWeak(){
		return this.weak;
	}
	
	public String getResis(){
		return this.resis;
	}
	
	public boolean getStun(){
		return stun;
	}
	
	public String toString(){
		return name;
	}
}


//The Attack class is used to store the attacks of a Pokemon and contains get/set methods for easy access of information
class Attack{
	
	private String name,spec;
	private int cost,dmg;
	
	public Attack(String name, int cost, int dmg, String spec){
		this.name = name;
		this.cost = cost;
		this.dmg = dmg;
		this.spec = spec;
	}
	
	public String getName(){
		return name;
	}
	public int getCost(){
		return cost;
	}
	public int getDmg(){
		return dmg;
	}
	public String getSpec(){
		return spec;
	}
	
	
	
}