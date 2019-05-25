package lab1;

import java.util.Stack;

import java.util.NoSuchElementException;

public class Queue<E> extends Stack<E>{
    public final int dump=10;
    private  Stack<E> stk;
    public Queue( ){
    	super();
    	stk = new Stack<>();
    }
    
    public boolean add(E e) throws IllegalStateException, ClassCastException, 
	    NullPointerException, IllegalArgumentException{
		if(stk.size() == dump && !super.empty())
			throw new IllegalStateException();
	    if(stk.size() == dump && super.empty()) {
			while(!stk.empty()) {
				super.push(stk.pop());
			}
		}
		stk.push(e);
		return true;
	}
    public boolean offer(E e) throws ClassCastException, NullPointerException, 
    	IllegalArgumentException{
		if(stk.size() == dump && !super.empty())
			return false;
	    if(stk.size() == dump && super.empty()) {
			while(!stk.empty()) {
				super.push(stk.pop());
			}
		}
		stk.push(e);
		return true;    
    }
    
    public E remove( ) throws NoSuchElementException {
    	if(super.empty() && stk.empty())
    		throw new NoSuchElementException();
    	if(super.empty()) {
    		while(!stk.empty())
    			super.push(stk.pop());
    	}
    	return super.pop();
    }
    public E poll( ) {  
    	if(super.empty() && stk.empty())
    		return null;
    	if(super.empty()) {
    		while(!stk.empty())
    			super.push(stk.pop());
    	}
    	return super.pop();
    }
    public E peek ( ) {
    	if(super.empty() && stk.empty())
    		return null;
    	if(super.empty()) {
    		while(!stk.empty())
    			super.push(stk.pop());
    	}
    	return super.peek();    	
    }
    public E element( ) throws NoSuchElementException {
    	if(super.empty() && stk.empty())
    		throw new NoSuchElementException();
    	if(super.empty()) {
    		while(!stk.empty())
    			super.push(stk.pop());
    	}
    	return super.peek();   
    }
    public void printQ() {
    	for(int i = super.size()-1; i >= 0; i--)
    	{
    		System.out.print(super.get(i).toString() + " ");
    	}
    	for(E e : stk)
    	{
    		System.out.print(e.toString() + " ");
    	}
    	System.out.println();
    }
    
    public static void main(String[] args) {
    	Queue<Integer> Q = new Queue<>();
 
//    	for(int i = 0; i < 100 && Q.add(i); ++i)
//    		Q.printQ();
    	
//    	for(int i = 0; i < 100 && Q.offer(i); ++i)
//    		Q.printQ();
    	
//    	Q.add(1);
//    	Q.printQ(); Q.remove(); 
//    	Q.printQ(); Q.remove(); 
    	
//    	for(int i = 0; i < 12; ++i)
//    		Q.add(i);
//    	while(Q.poll() != null) Q.printQ();
    	
//    	for(int i = 0; i < 5; ++i)
//    		Q.add(i);
//    	System.out.println(Q.peek());
//    	Q.printQ();
//    	while(Q.poll() != null);
//    	System.out.println(Q.peek());

    	for(int i = 0; i < 5; ++i)
    		Q.add(i);
    	System.out.println(Q.element());
    	Q.printQ();
    	while(Q.poll() != null);
    	Q.element();

    }
}

