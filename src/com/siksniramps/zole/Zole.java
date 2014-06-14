package com.siksniramps.zole;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;
import java.util.ArrayList;
import java.util.Scanner;

public class Zole {
	//variables
	
	
	private ArrayList<Card> deck=new ArrayList<Card>();
	private Suit activeSuit=null;
	private int gameState=-1, activePlayer,lielais,
	cardsOnTable=0, playerCount=0, firstHand=0, buriedPoints;
	private iPlayer[] players=new iPlayer[3];
	private Card[] table=new Card[3];
	private int[] score=new int[3], tricks=new int[3], finalPoints=new int[3];;
	public boolean simulation=false,galds=false;
	//constructors
    public Zole(int firstPlayer) {
    	activePlayer=firstPlayer;
    	firstHand=activePlayer;
    	setUpDeck(deck);
    	shuffleDeck();
    }
    public Zole(Zole game){
    	//copy variables
    	this.deck=new ArrayList<Card>(game.deck);
    	this.activeSuit=game.activeSuit;
    	this.gameState=game.gameState;
    	this.activePlayer=game.activePlayer;
    	this.lielais=game.lielais;
    	this.cardsOnTable=game.cardsOnTable;
    	this.score[0]=game.score[0];
    	this.score[1]=game.score[1];
    	this.score[2]=game.score[2];
    	this.tricks[0]=game.tricks[0];
    	this.tricks[1]=game.tricks[1];
    	this.tricks[2]=game.tricks[2];
    	this.firstHand=game.firstHand;
    	this.table[0]=game.table[0];
    	this.table[1]=game.table[1];
    	this.table[2]=game.table[2];
    	this.galds=game.galds;
    	simulation=true;
    	
    	//copy players
    	for(int i=0;i<3;i++){
    		setRandomPlayer(game.players[i],i);
    		
    	}
    	//distribute the unknown cards randomly
    	for(int i=1;i<3;i++){
    		deck.addAll(players[(activePlayer+i)%3].hand());
    		players[(activePlayer+i)%3].hand().clear();
    		deck.addAll(players[(activePlayer+i)%3].buried());
    		players[(activePlayer+i)%3].buried().clear();
    	}
    	shuffleDeck();

    	int subtrCard;
    	for(int i=1;i<3;i++){
    		subtrCard=gameState;
    		if(i==1&&cardsOnTable>1)subtrCard++;
    		else if(i==2&&cardsOnTable>0)subtrCard++;
    		if(gameState==-1)subtrCard+=2;
    		else if(gameState==0)subtrCard++;
    		for(int j=0; j<9-subtrCard;j++){
    			players[(activePlayer+i)%3].hand().add(dealCard());
    		}
    		if(players[(activePlayer+i)%3].isLielais()){
    			for(int k=0;k<2;k++){
    				buriedPoints+=deck.get(0).points();
    				if(gameState==0)players[(activePlayer+i)%3].hand().add(deck.remove(0));
    				else players[(activePlayer+i)%3].buried().add(deck.remove(0));
    			}
    		}
    	}
    	    	
    }
    //methods
    public void start(){
    
    	if(gameState==-1){
    		for(int i=0;!players[activePlayer].selectType()&&i<3;i++,activePlayer=(activePlayer+1)%3,i++){}
    		if(!players[activePlayer].isLielais()){
    			galds=true;
    			activePlayer=firstHand;
    			gameState+=2;
    			
    		}
    		else{
    			lielais=activePlayer;
    			activePlayer=firstHand;
    			gameState++;	
    		}
    	}
    	
    	if(gameState==0){
    		activePlayer=lielais;
    		buriedPoints=players[lielais].bury();
    		activePlayer=firstHand;
    		gameState++;
    	}
    	if(gameState>0){
    		int cardsLeftInTick;
    		while(gameState<9){
    			cardsLeftInTick=3-cardsOnTable;
    			for(int j=0;j<cardsLeftInTick;j++){
    				table[activePlayer]=players[activePlayer].play();
    				if(cardsOnTable==0){
    					activeSuit=table[activePlayer].suit();
    					firstHand=activePlayer;
    				}
    				cardsOnTable=(cardsOnTable+1)%3;
    				activePlayer=(activePlayer+1)%3;
    			}
    			evaluateTrick();
    			gameState++;
    		}
    	}
      	if(gameState==9)winer();
    }
    public void setUpDeck(ArrayList<Card> deck){
    	try{
    		BufferedReader breader = new BufferedReader(new FileReader("cards.txt"));
    		String line=breader.readLine();
    		String[] split;
    		Suit suit;
    		for(int i=0;line!=null &&i<26;i++){
    			split=line.split(" ");
    			switch(split[1].charAt(0)){
    				case 'H':{suit=Suit.HAERT;break;}
    				case 'C':{suit=Suit.CLUBS;break;}
    				case 'S':{suit=Suit.SPADE;break;}
    				case 'T':{suit=Suit.TRUMP;break;}
    				default: suit=null;
    			}
    			deck.add(new Card(split[0],suit,Integer.parseInt(split[2]),Integer.parseInt(split[3])));
    			line=breader.readLine();
    		}
    		breader.close();
    		shuffleDeck();	
    	}
    	catch(IOException e){
    		System.out.println(e);
    	}
    } 
    public void setHumanPlayer(String name){
    	if(playerCount<3){
    		players[playerCount]=new HumanPlayer(this,name,playerCount);
    		playerCount++;
    	}	
    }
    public void setRandomPlayer(String name){
    	if(playerCount<3){
    		players[playerCount]=new RandomPlayer(this,name,playerCount);
    		playerCount++;
    	}
    }
    public void setRandomPlayer(iPlayer player, int index){
    	if(index<3&&index>=0){
    		players[index]=new RandomPlayer(this,player,index);
    		playerCount++;
    	}
    }
    public void setMonteCarloPlayer(String name, int nSimulations){
    	if(playerCount<3){
    		players[playerCount]=new MonteCarloPlayer(this,name,playerCount, nSimulations);
    		playerCount++;
    	}	
    }
	private void shuffleDeck(){
    	int index;
    	Card temp;
    	Random rnd = new Random();
    	for(int i=deck.size()-1;i>0;i--){
    		index=rnd.nextInt(i+1);
    		temp = deck.get(index);
        	deck.set(index,deck.get(i));
        	deck.set(i,temp);
    	}
    }
    public Card dealCard(){
    	if(!deck.isEmpty())return deck.remove(0);
    	return null;
    } 
    private void evaluateTrick(){
    	int winer=firstHand;
    	for(int i=(winer+1)%3;i!=firstHand;i=(i+1)%3){
    		if(table[winer].compareTo(table[i])==-1 &&(table[i].suit()==activeSuit||table[i].suit()==Suit.TRUMP)){winer=i;}
    	}
    	if(!galds){
    		for(int i=0;i<3;i++){
    			if(winer==lielais)score[lielais]+=table[i].points();
    			else{
    				score[(lielais+1)%3]+=table[i].points();
    				score[(lielais+2)%3]+=table[i].points();
    			}	
    		}
    	}
    	else{
    		for(int i=0;i<3;i++){
    			score[winer]+=table[i].points();
    		}
    	}
    	if(!simulation)System.out.println("Trick goes to "+players[winer].name());
    	activeSuit=null;
    	activePlayer=winer;
    	cardsOnTable=0;
    	tricks[winer]++;
    	
    	
    	//System.out.println("Trick "+table[0].toString()+" "+table[1].toString()+" "+table[2].toString()+" taken by:"+players[winer].name());
    	//System.out.println("Points from trick just added "+score[0]+" "+score[1]+ " "+score[2]);
    }
    public Suit activeSuit(){
    	return activeSuit;
    }
    private void winer(){
    	
    	if(!galds){
    		score[lielais]+=buriedPoints;
    		if(score[lielais]==120){
    			 finalPoints[lielais]=6;
    			 finalPoints[(lielais+1)%3]=-3;
    			 finalPoints[(lielais+2)%3]=-3;
    			 if(!simulation)System.out.println(players[lielais].name()+" wins(no tricks) with "+score[lielais]+" over "+score[(lielais+1)%3]);
    		}
    		else if(score[lielais]>90){
    			 finalPoints[lielais]=4;
    			 finalPoints[(lielais+1)%3]=-2;
    			 finalPoints[(lielais+2)%3]=-2;
    			 if(!simulation)System.out.println(players[lielais].name()+" wins(Janji) with "+score[lielais]+" over "+score[(lielais+1)%3]);
    		}
    		else if(score[lielais]>60){
    			 finalPoints[lielais]=2;
    			 finalPoints[(lielais+1)%3]=-1;
    			 finalPoints[(lielais+2)%3]=-1;
    			 if(!simulation)System.out.println(players[lielais].name()+" wins with "+score[lielais]+" over "+score[(lielais+1)%3]);
    		}
    		else if(score[lielais]>30){
    			finalPoints[lielais]=-4;
    			finalPoints[(lielais+1)%3]=2;
    			finalPoints[(lielais+2)%3]=2;
    			if(!simulation)System.out.println(players[(lielais+1)%3].name()+" and "+players[(lielais+2)%3].name()+" win with "+score[(lielais+1)%3]+" over "+score[lielais]);
    		}
    		else if(score[lielais]>0){
    			finalPoints[lielais]=-6;
    			finalPoints[(lielais+1)%3]=3;
    			finalPoints[(lielais+2)%3]=3;
    			if(!simulation)System.out.println(players[(lielais+1)%3].name()+" and "+players[(lielais+2)%3].name()+" win(Janji) with "+score[(lielais+1)%3]+" over "+score[lielais]);
    		}
    		else if(score[lielais]==0){
    			finalPoints[lielais]=-8;
    			finalPoints[(lielais+1)%3]=4;
    			finalPoints[(lielais+2)%3]=4;
    			if(!simulation)System.out.println(players[(lielais+1)%3].name()+" and "+players[(lielais+2)%3].name()+" win(no tricks) with "+score[(lielais+1)%3]+" over "+score[lielais]);
    		}
    	}
    	else{
    		int loser=0;
    		for(int i=1;i<3;i++){
    			if(tricks[loser]<tricks[i]||(tricks[loser]==tricks[i]&&score[loser]<score[i]))loser=i;
    		}
    		finalPoints[loser]=-4;
    		finalPoints[(loser+1)%3]=2;
    		finalPoints[(loser+2)%3]=2;
    		if(!simulation)System.out.println(players[loser].name()+" loses with "+tricks[loser]+" tricks ("+score[loser]+")");
    	}
    	//System.out.println(players[0].name()+" ieguva "+score[0]+" punktus");
    	//System.out.println(players[1].name()+" ieguva "+score[1]+" punktus");
    	//System.out.println(players[2].name()+" ieguva "+score[2]+" punktus");
    }
    
   
    
    public int finalPoints(int index){
    	return finalPoints[index];
    }
    public int lielais(){
    	return lielais;
    }
    public iPlayer players(int index){
    	return players[index];
    }
    //main method
   	public static void main(String[] args){
   		Scanner sc=new Scanner(System.in);
   		Random rnd=new Random();
   		int firstPlayer=rnd.nextInt(3);
   		String ans="";

   				do{
   					firstPlayer=(firstPlayer+1)%3;
   					Zole game=new Zole(firstPlayer);
   					game.setHumanPlayer("Human");
   					game.setMonteCarloPlayer("Monte",10000);
   					game.setMonteCarloPlayer("Carlo",10000);
   					game.start();
   					   		
   					System.out.println("To restart press y. Otherwise whatever.");
   					if(sc.hasNext("y"))ans=sc.next();
   				}while(ans.equals("y"));
   		int[] p=new int[3];
   		Zole game;
   	
   			for(int j=0;j<10;j++){
   				for(int i=0;i<30;i++){
   					game=new Zole(firstPlayer);
					game.setMonteCarloPlayer("Monte",10000);
   					game.setRandomPlayer("Randy");
   					game.setRandomPlayer("Rando");
   					game.start();
   					p[0]+=game.finalPoints(0);
   					p[1]+=game.finalPoints(1);
   					p[2]+=game.finalPoints(2);
   					firstPlayer=(firstPlayer+1)%3;
   				}
   			}
   			System.out.println(p[0]/10+" "+p[1]/10+" "+p[2]/10);
   			p[0]=0;
   			p[1]=0;
   			p[2]=0;
   		}
   		
   	
   	
    
}
//Enum of card suits
enum Suit{
	HAERT, SPADE, CLUBS, TRUMP
}
//class for cards
class Card implements Comparable<Card>{
    private Suit suit;
    private String name;
    private int points;
    private int strength;	
    public Card(String name,Suit suit,int points, int strength){
    	this.suit=suit;
    	this.name=name;
    	this.points=points;
    	this.strength=strength;
    }
    public int points(){
    	return points;
    }
    public Suit suit(){
    	return suit;
    }
    public String toString(){
    	return name;
    }	
    public int compareTo(Card otherCard){
    	if(strength>otherCard.strength)return 1;
    	else if(this.strength<otherCard.strength)return -1;
    	else return -1;
    	}    
}
//player interface
interface iPlayer{
	public Card play();
	public boolean selectType();
	public int bury();
	public String name();
	public boolean isLielais();
	public ArrayList<Card> hand();
	public ArrayList<Card> buried();
	public int firstCardIndex();
}

class Player implements iPlayer{
	protected Zole game;
	protected String name;
	protected ArrayList<Card> hand=new ArrayList<Card>();
	protected ArrayList<Card> buried=new ArrayList<Card>();
	protected boolean lielais=false;
	protected int index=0;
	Player(Zole game, String name,int index){
		this.index=index;
		this.game=game;
		this.name=name;
		for(int i=0;i<8;i++){
			hand.add(game.dealCard());
		}
	}
	public Player(Zole game,iPlayer player,int index){
		this.index=index;
		this.game=game;
		this.name="s-"+player.name();
		this.lielais=player.isLielais();
		this.hand=new ArrayList<Card>(player.hand());
		this.buried=new ArrayList<Card>(player.buried());
		this.index=index;
	}
	public Card play(){
		return null;
	}
	public boolean selectType(){
		return lielais;
	}
	public int bury(){
		return 0;
	}
	public boolean isLielais(){
		return lielais;
	}
	public String name(){
		return name;
	}
	protected boolean isValidMove(int selected){
		Suit activeSuit=game.activeSuit();
		if(hand.get(selected-1).suit()==activeSuit||activeSuit==null)return true;
 		else{
 			for(int i=0; i<hand.size();i++){
 				if(activeSuit==hand.get(i).suit())return false;
 			}
 			return true;
 		}
	}
	public ArrayList<Card> hand(){
		return hand;
	}
	public ArrayList<Card> buried(){
		return buried;
	}
	public int firstCardIndex(){
		return 0;
	}
}

class HumanPlayer extends Player{
	private static Scanner sc=new Scanner(System.in);
	public HumanPlayer(Zole game, String name,int index){
		super(game,name,index);
	}
	@Override
	public Card play(){
		int selected=0;
		System.out.println(name+":Choose a card("+1+"-"+hand.size()+"):"+hand.toString());
		while(selected<1||selected>hand.size()){
			if(!sc.hasNextInt()){
				System.out.println("Invalid input");
				sc.next();
			}
			else{
				
				selected=sc.nextInt();
				if(selected<1||selected>hand.size()) System.out.println("Invalid input");
				else if(!isValidMove(selected)){
					System.out.println("Invalid move");
					selected=0;
				}	
			}	
		}
		System.out.println(name+"("+hand.size()+")"+":plays "+hand.get(selected-1));
		return hand.remove(selected-1);
	}
	@Override
	public boolean selectType(){
		System.out.println(name+":Play as Lielais?(y/n):"+hand.toString());
		while(!sc.hasNext("y")&& !sc.hasNext("n")){
			System.out.println("Invalid input");
			sc.next();
		}
		if(sc.next().equals("y")){
			lielais=true;
			System.out.println(name+":Lielais");
			hand.add(game.dealCard());
			hand.add(game.dealCard());
		}
		else System.out.println(name+":Pass");
		return lielais;
	}
	@Override
	public int bury(){
		int selected=0,points=0;
		
		for(int i=1;i<3;i++){
			selected=0;
			System.out.println(name+":Chose "+i+". of 2 cards to bury("+1+"-"+hand.size()+"):"+hand.toString());
			while(selected<1||selected>hand.size()){
				if(!sc.hasNextInt()){
					System.out.println("Invalid input");
					sc.next();
				}
				else{
					selected=sc.nextInt();
					if(selected<1||selected>hand.size()) System.out.println("Invalid input");
				}
			}
			System.out.println(name+":Buried "+hand.get(selected-1).toString());
			points+=hand.get(selected-1).points();
			buried.add(hand.remove(selected-1));
		}
		return points;
	}
}


class RandomPlayer extends Player{
	
	private boolean first=true;
	public int firstCardindex;
	public RandomPlayer(Zole game, String name,int index){
		super(game,name,index);
	}
	public RandomPlayer(Zole game, iPlayer player,int index){
		super(game,player,index);
	}
	@Override
	public Card play(){
		//System.out.println("p-"+name+"("+hand.size()+")"+":"+hand.toString());
		Random rnd=new Random();
		int selected;
		do{
			selected=rnd.nextInt(hand.size());
		}while(!isValidMove(selected+1));
		
		if(first){
			firstCardindex=selected;
			first=false;
		}
		//System.out.println(name+"("+hand.size()+")"+":plays "+hand.get(selected));
		return hand.remove(selected);
	}
	@Override
	public boolean selectType(){
		Random rnd=new Random();
		//System.out.println("st-"+name+":"+hand.toString());
		if(rnd.nextInt(2)==1||game.simulation){
			lielais=true;
	
			hand.add(game.dealCard());
			hand.add(game.dealCard());
		}
		
		//System.out.println("st-"+name+":"+lielais);
		return lielais;
	}
	@Override
	public int bury(){
		Random rnd=new Random();
		//System.out.println("b-"+name+":"+hand.toString());
		int selected=0,points=0;
		
		for(int i=0;i<2;i++){	
			selected=rnd.nextInt(hand.size());
			points+=hand.get(selected).points();
			//System.out.println("s-"+name+" buried "+hand.get(selected).toString());
			buried.add(hand.remove(selected));
		}
		return points;
	}
	public int firstCardIndex(){
		return firstCardindex;
	}
}

class MonteCarloPlayer extends Player{
	private Zole simulation;
	private int[] evaluate=new int[hand.size()];
	private boolean[] legalMove=new boolean[hand.size()];
	private int l,m,b1,b2,nSimulations;
	private int[][]whatToBury=new int[9][10];
	private boolean[][] legalBury=new boolean[9][10];
	MonteCarloPlayer(Zole game, String name,int index,int nSimulations){
		super(game,name,index);
		this.nSimulations=nSimulations;	
	}
	@Override
	public Card play(){
		//System.out.println(name+": "+hand.toString());
		simulate(0);
		/*String panzer="";
		for(int i=0;i<evaluate.length;i++){
			panzer+=evaluate[i]+" ";
		}
		System.out.println(name+":"+panzer);*/
		//
		Random rnd=new Random();
		ArrayList<Integer> allMax=new ArrayList<Integer>();
		int max=0,selected;
		boolean first=true;
		for(int i=0;i<evaluate.length;i++){		
			if((legalMove[i]&&max<evaluate[i])||(legalMove[i]&&first)){
				max=evaluate[i];
				allMax.clear();
				allMax.add(i);
				first=false;
			}
			else if(legalMove[i]&&max==evaluate[i])allMax.add(i);
		}
		
		selected=allMax.get(rnd.nextInt(allMax.size()));
		System.out.println(name+"("+hand.size()+")"+":plays "+hand.get(selected));
		return hand.remove(selected);
	}
	@Override
	public boolean selectType(){
		//System.out.println(name+": "+hand.toString());
		simulate(1);
		//System.out.println("l="+l+" n="+nSimulations);
		if((nSimulations==1&&Math.random()>=0.5)||((double)l/(double)nSimulations>=0.33)){
			System.out.println(name+":Lienlais");
			lielais=true;
			hand.add(game.dealCard());
			hand.add(game.dealCard());
		}
		else System.out.println(name+":Pass");
		return lielais;
	}
	@Override
	public int bury(){
		//System.out.println(name+": "+hand.toString());
		simulate(2);
		int points=0;
		Random rnd=new Random();
		ArrayList<Integer> allMaxA=new ArrayList<Integer>();
		ArrayList<Integer> allMaxB=new ArrayList<Integer>();
		boolean first=true;
		int max=0, selected=0;
		for(int i=0;i<9;i++){
			for(int j=i;j<10;j++){
				if((j!=i&&legalBury[i][j]&&whatToBury[i][j]>max)||(legalBury[i][j]&&first)){
					max=whatToBury[i][j];
					allMaxA.clear();
					allMaxB.clear();
					allMaxA.add(i);
					allMaxB.add(j);
					first=false;
				
				}
				else if(j!=i&&legalBury[i][j]&&whatToBury[i][j]==max){
					allMaxA.add(i);
					allMaxB.add(j);
					
				}
			}
		}
		
		selected=rnd.nextInt(allMaxA.size());
		points+=hand.get(allMaxA.get(selected)).points();
		points+=hand.get(allMaxB.get(selected)).points();
		//System.out.println(name+" buries "+hand.get((int)allMaxA.get(selected)).toString());
		//System.out.println(name+" buries "+hand.get((int)allMaxB.get(selected)).toString());
		buried.add(hand.remove((int)allMaxB.get(selected)));
		buried.add(hand.remove((int)allMaxA.get(selected)));
		
		
		return points;
	}
	private void simulate(int mode){
		legalMove=new boolean[hand.size()];
		evaluate=new int[hand.size()];
		whatToBury=new int[9][10];
		legalBury=new boolean[9][10];
		l=0;m=0;b1=0;b2=0;
		///SIMULATION COUNT IS HERE
		for(int i=0;i<nSimulations;i++){
			simulation=new Zole(game);
			simulation.start();
			if(mode==0){
				evaluate[simulation.players(index).firstCardIndex()]+=simulation.finalPoints(index);
				legalMove[simulation.players(index).firstCardIndex()]=true;
			
			}
			if(mode==1){
				if(simulation.lielais()==index){
					if(simulation.finalPoints(index)>0)l++;
				}
				else System.out.println("Somthing ain't right here");
			}
			else if(mode==2){
				b1=hand.indexOf(simulation.players(index).buried().get(0));
				b2=hand.indexOf(simulation.players(index).buried().get(1));
				//System.out.println("b1="+b1+" b2="+b2);
				whatToBury[Math.min(b1,b2)][Math.max(b1,b2)]+=simulation.finalPoints(index);
				legalBury[Math.min(b1,b2)][Math.max(b1,b2)]=true;
			}
		}	
	}	
}



