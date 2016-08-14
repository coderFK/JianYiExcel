package com.app.jianyiexcel.model;

import java.io.File;

public class FileStack<Item>{
	Node first;
	Node current;
	Node root;
	int N;
	public class Node {
		Item item;
		Node previous;
		Node next;
		
	}
	public Item getCurrentItem(){
		if(current==null){
			return null;
		}
		return current.item;
	}
	public void add(Item item){
		if(N==0){
			Node node=new Node();
			node.item=item;
			node.previous=null;
			node.next=null;
			first=node;
			root=node;
			current=node;
		}
		else{
			Node node=new Node();
			node.item=item;
			current.next=node;
			node.previous=current;
			current=node;
		}
		N++;
	}
	
	public void remove(){
		if(N==1){
			current=null;
		}
		else{
			current=current.previous;
			current.next=null;
		}
		N--;
	}
}

