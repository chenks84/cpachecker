#include <stdio.h> 
#include <assert.h>

	// inputs
	int a= 1;
	int e= 5;
	int d= 4;
	int f= 6;
	int c= 3;

	// outputs
	int u = 21;
	int v = 22;
	int w = 23;
	int x = 24;
	int y = 25;
	int z = 26;


	int a25 = 0;
	int a11 = 0;
	int a28 = 7;
	int a19 = 1;
	int a21 = 1;
	int a17 = 8;

	int calculate_output(int input) {
	    if((((!(a11==1)&&((a19==1)&&((input==4)&&((!(a25==1)&&(a28==8))||((a25==1)&&(a28==9))))))&&(a21==1))&&(a17==8))){
	    	a28 = 9;
	    	a11 = 1;
	    	a25 = 1;
	    	return -1;
	    } else if(((a17==8)&&((((!(a11==1)&&((a21==1)&&(input==6)))&&(a28==7))&&(a19==1))&&!(a25==1)))){
	    	a28 = 10;
	    	return 22;
	    } else if(((a21==1)&&((a19==1)&&((((((a25==1)||!(a25==1))&&(input==3))&&(a17==9))&&(a11==1))&&(a28==9))))){
	    	a28 = 7;
	    	a25 = 1;
	    	return 22;
	    } else if(((a28==9)&&(!(a19==1)&&((a21==1)&&((((input==4)&&!(a25==1))&&!(a11==1))&&(a17==8)))))){
	    	a25 = 1;
	    	a19 = 1;
	    	a11 = 1;
	    	return -1;
	    } else if(((((a17==8)&&((((input==1)&&((a25==1)||!(a25==1)))&&!(a11==1))&&(a19==1)))&&(a21==1))&&(a28==10))){
	    	a25 = 0;
	    	return -1;
	    } else if(((a19==1)&&(!(a25==1)&&((a21==1)&&((((input==1)&&(a17==8))&&!(a11==1))&&(a28==7)))))){
	    	a28 = 11;
	    	a25 = 1;
	    	return 26;
	    } else if(((((!(a19==1)&&((input==1)&&((((a25==1)&&(a28==7))||((a28==7)&&!(a25==1)))||((a25==1)&&(a28==8)))))&&(a21==1))&&(a17==8))&&!(a11==1))){
	    	a28 = 7;
	    	a11 = 1;
	    	a25 = 0;
	    	return -1;
	    } else if(((a19==1)&&(((((input==1)&&(((a25==1)&&(a28==8))||(((a28==7)&&(a25==1))||((a28==7)&&!(a25==1)))))&&(a11==1))&&(a21==1))&&(a17==9)))){
	    	a28 = 11;
	    	a17 = 7;
	    	a11 = 0;
	    	a25 = 1;
	    	return 22;
	    } else if(((a19==1)&&(((a17==8)&&(((input==6)&&((!(a25==1)&&(a28==8))||((a25==1)&&(a28==9))))&&!(a11==1)))&&(a21==1)))){
	    	a28 = 10;
	    	a25 = 0;
	    	return 22;
	    } else if((!(a11==1)&&((((a21==1)&&((input==1)&&(((a28==8)&&!(a25==1))||((a25==1)&&(a28==9)))))&&!(a19==1))&&(a17==8)))){
	    	a28 = 7;
	    	a25 = 0;
	    	a11 = 1;
	    	return -1;
	    } else if((!(a19==1)&&(((((a17==8)&&((a25==1)&&(input==3)))&&(a28==10))&&!(a11==1))&&(a21==1)))){
	    	if((a19==1)){

	    	}else{
	    		a19 = 1;
	    		a28 = 8;
	    	}  
	    	return 26;
	    } else if((((!(a19==1)&&((a21==1)&&((((a25==1)&&(a28==8))||(((a28==7)&&(a25==1))||((a28==7)&&!(a25==1))))&&(input==4))))&&!(a11==1))&&(a17==8))){
	    	if((a28==10)){
	    		a28 = 7;
	    		a25 = 1;
	    	}else{
	    		a28 = 8;
	    		a25 = 0;
	    	}  
	    	return 22;
	    } else if(((a17==8)&&((a21==1)&&((a25==1)&&((a19==1)&&(!(a11==1)&&((a28==11)&&(input==3)))))))){
	    	a28 = 10;
	    	a25 = 0;
	    	return -1;
	    } else if(((a28==9)&&(((!(a19==1)&&(((input==3)&&!(a11==1))&&(a17==8)))&&!(a25==1))&&(a21==1)))){
	    	a28 = 10;
	    	a19 = 1;
	    	return 22;
	    } else if(((!(a11==1)&&(((a28==11)&&((a25==1)&&((input==1)&&(a19==1))))&&(a17==8)))&&(a21==1))){
	    	a17 = 7;
	    	a25 = 0;
	    	a11 = 1;
	    	a28 = 10;
	    	a19 = 0;
	    	return -1;
	    } else if((((a11==1)&&((((((a25==1)&&(a28==8))||(((a25==1)&&(a28==7))||(!(a25==1)&&(a28==7))))&&(input==6))&&(a19==1))&&(a17==9)))&&(a21==1))){
	    	a17 = 8;
	    	a25 = 0;
	    	a19 = 0;
	    	a28 = 11;
	    	a11 = 0;
	    	return 23;
	    } else if(((a17==8)&&((a21==1)&&(!(a11==1)&&(!(a19==1)&&(((!(a25==1)&&(a28==8))||((a25==1)&&(a28==9)))&&(input==3))))))){
	    	a28 = 10;
	    	a19 = 1;
	    	a11 = 1;
	    	a25 = 1;
	    	return -1;
	    } else if((((((a21==1)&&((a28==10)&&((input==1)&&(a25==1))))&&!(a11==1))&&(a17==8))&&!(a19==1))){
	    	a25 = 0;
	    	a19 = 1;
	    	return 26;
	    } else if((!(a11==1)&&(!(a19==1)&&(((a17==8)&&((input==6)&&((((a25==1)&&(a28==7))||((a28==7)&&!(a25==1)))||((a28==8)&&(a25==1)))))&&(a21==1))))){
	    	a17 = 7;
	    	a25 = 1;
	    	a11 = 1;
	    	a28 = 7;
	    	a19 = 1;
	    	return -1;
	    } else if(((((a17==8)&&(!(a19==1)&&(((input==6)&&(a21==1))&&!(a25==1))))&&!(a11==1))&&(a28==9))){
	    	a28 = 10;
	    	a19 = 1;
	    	return 22;
	    } else if((!(a11==1)&&((!(a25==1)&&(((a21==1)&&((input==1)&&(a19==1)))&&(a17==8)))&&(a28==11)))){
	    	a28 = 7;
	    	a19 = 0;
	    	a11 = 1;
	    	return -1;
	    } else if((((a11==1)&&(((a19==1)&&((a21==1)&&(((a25==1)||!(a25==1))&&(input==4))))&&(a17==9)))&&(a28==9))){
	    	a25 = 0;
	    	a17 = 8;
	    	return -1;
	    } else if(((!(a11==1)&&((a28==9)&&(((!(a19==1)&&(input==5))&&!(a25==1))&&(a21==1))))&&(a17==8))){
	    	a17 = 7;
	    	a11 = 1;
	    	a25 = 1;
	    	return -1;
	    } else if(((!(a11==1)&&(((a17==8)&&(((((a28==7)&&(a25==1))||(!(a25==1)&&(a28==7)))||((a28==8)&&(a25==1)))&&(input==3)))&&!(a19==1)))&&(a21==1))){
	    	a11 = 1;
	    	a19 = 1;
	    	a28 = 10;
	    	a25 = 1;
	    	return -1;
	    } else if(((a21==1)&&(!(a19==1)&&((((input==5)&&(((a28==8)&&(a25==1))||(((a25==1)&&(a28==7))||(!(a25==1)&&(a28==7)))))&&!(a11==1))&&(a17==8))))){
	    	a25 = 1;
	    	a19 = 1;
	    	a28 = 7;
	    	return -1;
	    } else if((((a17==9)&&(((a28==8)&&(((input==1)&&(a19==1))&&!(a25==1)))&&(a11==1)))&&(a21==1))){
	    	a17 = 8;
	    	a19 = 0;
	    	a28 = 9;
	    	a25 = 1;
	    	return 22;
	    } else if((((a21==1)&&((a19==1)&&((a28==7)&&((a17==8)&&(!(a25==1)&&(input==3))))))&&!(a11==1))){
	    	a25 = 1;
	    	a28 = 9;
	    	return 26;
	    } else if(((a28==11)&&(((a17==8)&&((((input==6)&&(a21==1))&&(a19==1))&&!(a25==1)))&&!(a11==1)))){
	    	a17 = 7;
	    	a11 = 1;
	    	a28 = 7;
	    	a25 = 1;
	    	return -1;
	    } else if(((!(a11==1)&&(((a17==8)&&((a21==1)&&((input==3)&&(a28==8))))&&(a19==1)))&&(a25==1))){
	    	a28 = 10;
	    	a19 = 0;
	    	return 26;
	    } else if((((a21==1)&&(((a19==1)&&(((((a25==1)&&(a28==7))||((a28==7)&&!(a25==1)))||((a25==1)&&(a28==8)))&&(input==4)))&&(a11==1)))&&(a17==9))){
	    	a17 = 8;
	    	a25 = 1;
	    	a11 = 0;
	    	a28 = 8;
	    	return -1;
	    } else if((((!(a11==1)&&((a17==8)&&(((input==4)&&(a19==1))&&(a28==8))))&&(a21==1))&&(a25==1))){
	    	return 22;
	    } else if(((a19==1)&&((((((input==4)&&(a21==1))&&(a28==9))&&!(a25==1))&&!(a11==1))&&(a17==8)))){
	    	return 22;
	    } else if(((a17==8)&&((((((input==1)&&(a21==1))&&!(a11==1))&&(a19==1))&&!(a25==1))&&(a28==9)))){
	    	return 23;
	    } else if((((((((input==4)&&((a25==1)||!(a25==1)))&&!(a11==1))&&(a21==1))&&(a17==8))&&(a28==10))&&(a19==1))){
	    	a25 = 1;
	    	return 22;
	    } else if(((!(a25==1)&&((a11==1)&&((a28==8)&&(((a17==9)&&(input==4))&&(a19==1)))))&&(a21==1))){
	    	a17 = 7;
	    	a11 = 0;
	    	return 22;
	    } else if((!(a19==1)&&((a17==8)&&((a28==10)&&(((a25==1)&&(!(a11==1)&&(input==5)))&&(a21==1)))))){
	    	a19 = 1;
	    	a28 = 11;
	    	return 23;
	    } else if((((((a21==1)&&((input==1)&&((!(a25==1)&&(a28==8))||((a25==1)&&(a28==9)))))&&(a17==8))&&!(a11==1))&&(a19==1))){
	    	if((a11==1)){
	    		a19 = 0;
	    		a25 = 0;
	    		a28 = 9;
	    	}else{
	    		a25 = 1;
	    		a28 = 9;
	    	}  
	    	return 23;
	    } else if(((a21==1)&&((((a19==1)&&((a25==1)&&((a28==11)&&(input==6))))&&!(a11==1))&&(a17==8)))){
	    	a28 = 10;
	    	return -1;
	    } else if((!(a11==1)&&((a19==1)&&((a28==10)&&(((((a25==1)||!(a25==1))&&(input==5))&&(a17==8))&&(a21==1)))))){
	    	a25 = 1;
	    	return -1;
	    } else if((((((a17==9)&&((((a25==1)||!(a25==1))&&(input==6))&&(a11==1)))&&(a19==1))&&(a21==1))&&(a28==9))){
	    	a17 = 8;
	    	a28 = 8;
	    	a25 = 1;
	    	return -1;
	    } else if((((a17==8)&&(((((input==6)&&(a25==1))&&!(a19==1))&&!(a11==1))&&(a28==10)))&&(a21==1))){
	    	return 26;
	    } else if(((!(a19==1)&&((a17==8)&&(!(a11==1)&&((input==1)&&((((a28==10)&&!(a25==1))||((a25==1)&&(a28==11)))||(!(a25==1)&&(a28==11)))))))&&(a21==1))){
	    	a28 = 7;
	    	a25 = 1;
	    	a19 = 1;
	    	a17 = 7;
	    	a11 = 1;
	    	return 22;
	    } else if((((((((input==1)&&(a21==1))&&(a28==9))&&!(a19==1))&&!(a25==1))&&(a17==8))&&!(a11==1))){
	    	return 23;
	    } else if(((a17==8)&&(((!(a11==1)&&((!(a25==1)&&(input==4))&&(a19==1)))&&(a28==7))&&(a21==1)))){
	    	a28 = 9;
	    	return 23;
	    } else if((!(a11==1)&&(((a17==8)&&((((!(a25==1)&&(a28==11))||((!(a25==1)&&(a28==10))||((a28==11)&&(a25==1))))&&(input==3))&&(a21==1)))&&!(a19==1)))){
	    	if((a25==1)){
	    		a19 = 1;
	    		a28 = 7;
	    		a25 = 1;
	    		a11 = 1;
	    	}else{
	    		a28 = 7;
	    		a25 = 1;
	    	}  
	    	return -1;
	    } else if(((((!(a11==1)&&(((!(a25==1)&&(a28==8))||((a25==1)&&(a28==9)))&&(input==5)))&&(a21==1))&&!(a19==1))&&(a17==8))){
	    	a25 = 1;
	    	a19 = 1;
	    	a28 = 7;
	    	return -1;
	    } else if((((a17==9)&&(((((input==1)&&((a25==1)||!(a25==1)))&&(a11==1))&&(a21==1))&&(a19==1)))&&(a28==9))){
	    	a28 = 7;
	    	a17 = 8;
	    	a25 = 0;
	    	a11 = 0;
	    	return -1;
	    } else if((!(a11==1)&&((a19==1)&&(((((a28==9)&&(input==6))&&(a21==1))&&(a17==8))&&!(a25==1))))){
	    	return 23;
	    } else if(((a17==9)&&((a21==1)&&((a11==1)&&(((input==3)&&(((a25==1)&&(a28==8))||(((a28==7)&&(a25==1))||(!(a25==1)&&(a28==7)))))&&(a19==1)))))){
	    	if((a19==1)){
	    		a25 = 0;
	    		a19 = 0;
	    		a17 = 7;
	    		a28 = 9;
	    		a11 = 0;
	    	}else{
	    		a19 = 0;
	    		a11 = 0;
	    		a28 = 10;
	    		a25 = 1;
	    		a17 = 7;
	    	}  
	    	return 23;
	    } else if(((a28==7)&&((a17==8)&&(!(a25==1)&&((a21==1)&&((a19==1)&&((input==5)&&!(a11==1)))))))){
	    	a25 = 1;
	    	a28 = 8;
	    	return 21;
	    } else if(((a28==8)&&((a21==1)&&((!(a11==1)&&(((input==1)&&(a17==8))&&(a19==1)))&&(a25==1))))){
	    	a28 = 10;
	    	return 26;
	    } else if((((((a17==8)&&(((input==6)&&((a25==1)||!(a25==1)))&&(a21==1)))&&(a28==10))&&!(a11==1))&&(a19==1))){
	    	a25 = 0;
	    	return 22;
	    } else if(((!(a11==1)&&((!(a19==1)&&(((!(a25==1)&&(a28==11))||(((a28==10)&&!(a25==1))||((a25==1)&&(a28==11))))&&(input==5)))&&(a17==8)))&&(a21==1))){
	    	a25 = 0;
	    	a19 = 1;
	    	a28 = 9;
	    	return -1;
	    } else if(((a17==8)&&(((a21==1)&&(!(a11==1)&&((a25==1)&&((a28==8)&&(input==6)))))&&(a19==1)))){
	    	return 26;
	    } else if((((a19==1)&&((a17==8)&&(((!(a25==1)&&(input==5))&&(a21==1))&&!(a11==1))))&&(a28==11))){
	    	a28 = 7;
	    	a25 = 1;
	    	return -1;
	    } else if(((a11==1)&&(((a17==9)&&((a28==9)&&((a19==1)&&(((a25==1)||!(a25==1))&&(input==5)))))&&(a21==1)))){
	    	if((a11==1)){
	    		a25 = 0;
	    		a17 = 8;
	    	}else{
	    		a28 = 7;
	    		a25 = 0;
	    		a11 = 0;
	    		a19 = 0;
	    		a17 = 8;
	    	}  
	    	return 25;
	    } else if((((((((input==3)&&(a19==1))&&(a21==1))&&(a17==8))&&!(a25==1))&&(a28==11))&&!(a11==1))){
	    	a25 = 1;
	    	a11 = 1;
	    	a28 = 10;
	    	return -1;
	    } else if(((a19==1)&&((a21==1)&&((((input==5)&&(((a25==1)&&(a28==8))||(((a28==7)&&(a25==1))||(!(a25==1)&&(a28==7)))))&&(a11==1))&&(a17==9))))){
	    	if((a25==1)){
	    		a11 = 0;
	    		a19 = 0;
	    		a25 = 1;
	    		a28 = 10;
	    		a17 = 8;
	    	}else{
	    		a17 = 8;
	    		a25 = 1;
	    		a11 = 0;
	    		a28 = 8;
	    	}  
	    	return 22;
	    } else if((((a19==1)&&(((!(a11==1)&&(((a25==1)||!(a25==1))&&(input==3)))&&(a28==10))&&(a21==1)))&&(a17==8))){
	    	a25 = 0;
	    	return -1;
	    } else if(((!(a11==1)&&(((((input==4)&&(a17==8))&&!(a25==1))&&(a21==1))&&(a28==11)))&&(a19==1))){
	    	if((a11==1)){
	    		a28 = 8;
	    		a19 = 0;
	    	} 
	    	return 22;
	    } else if((((!(a19==1)&&(((input==4)&&(((!(a25==1)&&(a28==10))||((a28==11)&&(a25==1)))||(!(a25==1)&&(a28==11))))&&(a21==1)))&&(a17==8))&&!(a11==1))){
	    	if((a17==7)){
	    		a25 = 0;
	    		a28 = 9;
	    	}else{
	    		a11 = 1;
	    		a17 = 7;
	    		a28 = 10;
	    		a25 = 0;
	    		a19 = 1;
	    	}  
	    	return -1;
	    } else if((!(a11==1)&&(((a17==8)&&((((a25==1)&&(input==4))&&(a19==1))&&(a28==11)))&&(a21==1)))){
	    	a28 = 9;
	    	a11 = 1;
	    	a25 = 0;
	    	return -1;
	    } else if((((((a21==1)&&((((a28==8)&&!(a25==1))||((a28==9)&&(a25==1)))&&(input==6)))&&!(a11==1))&&!(a19==1))&&(a17==8))){
	    	a19 = 1;
	    	a11 = 1;
	    	a17 = 7;
	    	a28 = 7;
	    	a25 = 1;
	    	return -1;
	    } else if(((a19==1)&&((((a21==1)&&((!(a25==1)&&(input==3))&&(a17==9)))&&(a28==8))&&(a11==1)))){
	    	a25 = 1;
	    	a11 = 0;
	    	a17 = 8;
	    	a28 = 7;
	    	a19 = 0;
	    	return -1;
	    } else if(((a17==8)&&(((a21==1)&&(((input==5)&&((!(a25==1)&&(a28==8))||((a28==9)&&(a25==1))))&&(a19==1)))&&!(a11==1)))){
	    	a11 = 1;
	    	a19 = 0;
	    	a25 = 1;
	    	a17 = 7;
	    	a28 = 9;
	    	return -1;
	    } else if(((a19==1)&&(((((a17==8)&&((input==5)&&!(a11==1)))&&(a21==1))&&(a25==1))&&(a28==8)))){
	    	a28 = 11;
	    	return 23;
	    } else if((((!(a11==1)&&((((a21==1)&&(input==4))&&(a28==10))&&(a17==8)))&&!(a19==1))&&(a25==1))){
	    	return 22;
	    } else if(((a28==8)&&(((a19==1)&&((a11==1)&&(!(a25==1)&&((a17==9)&&(input==6)))))&&(a21==1)))){
	    	if((a25==1)){
	    		a17 = 8;
	    		a11 = 0;
	    	}else{
	    		a28 = 11;
	    		a17 = 7;
	    	}  
	    	return 21;
	    } else if((!(a11==1)&&((((a21==1)&&((input==6)&&((((a28==10)&&!(a25==1))||((a25==1)&&(a28==11)))||(!(a25==1)&&(a28==11)))))&&(a17==8))&&!(a19==1)))){
	    	a28 = 11;
	    	a19 = 1;
	    	a25 = 1;
	    	return 22;
	    } else if((((((!(a25==1)&&((a19==1)&&(input==3)))&&(a28==9))&&(a21==1))&&(a17==8))&&!(a11==1))){
	    	a19 = 0;
	    	a28 = 7;
	    	return 25;
	    } else if((((a17==8)&&((((input==4)&&(((a28==8)&&!(a25==1))||((a25==1)&&(a28==9))))&&(a21==1))&&!(a19==1)))&&!(a11==1))){
	    	a28 = 8;
	    	a25 = 0;
	    	return 22;
	    } else if((((!(a11==1)&&((((a19==1)&&(input==5))&&(a17==8))&&(a21==1)))&&(a28==9))&&!(a25==1))){
	    	return 21;
	    } else if((((a11==1)&&((a19==1)&&((a21==1)&&(((a17==9)&&(input==5))&&!(a25==1)))))&&(a28==8))){
	    	a17 = 7;
	    	return -1;
	    } else if((!(a11==1)&&((a21==1)&&(((a19==1)&&((a17==8)&&((a25==1)&&(input==5))))&&(a28==11))))){
	    	return -1;
	    } else if(((a19==1)&&((a17==8)&&(((((!(a25==1)&&(a28==8))||((a25==1)&&(a28==9)))&&(input==3))&&!(a11==1))&&(a21==1))))){
	    	a25 = 0;
	    	a28 = 10;
	    	return 22;
	    } 
	    if((((((!(a25==1)&&(a11==1))&&(a28==7))&&!(a19==1))&&(a21==1))&&(a17==8))){
	    	error_50: assert(0);
	    } 
	    if(((((((a25==1)&&(a11==1))&&(a28==10))&&(a19==1))&&(a21==1))&&(a17==8))){
	    	ERROR: goto ERROR;
	    } 
	    if(((((((a25==1)&&!(a11==1))&&(a28==7))&&(a19==1))&&(a21==1))&&(a17==8))){
	    	error_59: assert(0);
	    } 
	    if(((((((a25==1)&&(a11==1))&&(a28==7))&&(a19==1))&&(a21==1))&&(a17==7))){
	    	globalError: assert(0);
	    } 
	    if(((((((a25==1)&&(a11==1))&&(a28==9))&&(a19==1))&&(a21==1))&&(a17==8))){
	    	error_43: assert(0);
	    } 
	    if(((((((a25==1)&&(a11==1))&&(a28==9))&&!(a19==1))&&(a21==1))&&(a17==7))){
	    	error_13: assert(0);
	    } 
	    if((((((!(a25==1)&&(a11==1))&&(a28==10))&&!(a19==1))&&(a21==1))&&(a17==7))){
	    	error_16: assert(0);
	    } 
	    if((((((!(a25==1)&&(a11==1))&&(a28==9))&&(a19==1))&&(a21==1))&&(a17==8))){
	    	error_44: assert(0);
	    } 
	    if((((((!(a25==1)&&(a11==1))&&(a28==7))&&(a19==1))&&(a21==1))&&(a17==8))){
	    	error_40: assert(0);
	    } 
	    if(((((((a25==1)&&(a11==1))&&(a28==8))&&(a19==1))&&(a21==1))&&(a17==8))){
	    	error_41: assert(0);
	    } 
	    if(((((((a25==1)&&(a11==1))&&(a28==11))&&!(a19==1))&&(a21==1))&&(a17==8))){
	    	error_57: assert(0);
	    } 
	    if(((((((a25==1)&&!(a11==1))&&(a28==7))&&(a19==1))&&(a21==1))&&(a17==7))){
	    	error_19: assert(0);
	    } 
	    if((((((!(a25==1)&&(a11==1))&&(a28==8))&&(a19==1))&&(a21==1))&&(a17==7))){
	    	error_2: assert(0);
	    } 
	    if(((((((a25==1)&&!(a11==1))&&(a28==10))&&!(a19==1))&&(a21==1))&&(a17==7))){
	    	error_35: assert(0);
	    } 
	    if(((((((a25==1)&&!(a11==1))&&(a28==11))&&(a19==1))&&(a21==1))&&(a17==7))){
	    	error_27: assert(0);
	    } 
	    if(((((((a25==1)&&!(a11==1))&&(a28==8))&&(a19==1))&&(a21==1))&&(a17==7))){
	    	error_21: assert(0);
	    } 
	    if((((((!(a25==1)&&!(a11==1))&&(a28==8))&&(a19==1))&&(a21==1))&&(a17==7))){
	    	error_22: assert(0);
	    } 
	    if((((((!(a25==1)&&!(a11==1))&&(a28==9))&&!(a19==1))&&(a21==1))&&(a17==7))){
	    	error_34: assert(0);
	    } 
	    if((((((!(a25==1)&&!(a11==1))&&(a28==7))&&(a19==1))&&(a21==1))&&(a17==7))){
	    	error_20: assert(0);
	    } 
	    if((((((!(a25==1)&&(a11==1))&&(a28==8))&&(a19==1))&&(a21==1))&&(a17==8))){
	    	error_42: assert(0);
	    } 
	    if((((((!(a25==1)&&(a11==1))&&(a28==9))&&!(a19==1))&&(a21==1))&&(a17==8))){
	    	error_54: assert(0);
	    } 
	    if(((((((a25==1)&&!(a11==1))&&(a28==7))&&!(a19==1))&&(a21==1))&&(a17==7))){
	    	error_29: assert(0);
	    } 
	    if((((((!(a25==1)&&(a11==1))&&(a28==11))&&!(a19==1))&&(a21==1))&&(a17==8))){
	    	error_58: assert(0);
	    } 
	    if((((((!(a25==1)&&(a11==1))&&(a28==8))&&!(a19==1))&&(a21==1))&&(a17==8))){
	    	error_52: assert(0);
	    } 
	    if(((((((a25==1)&&(a11==1))&&(a28==10))&&!(a19==1))&&(a21==1))&&(a17==8))){
	    	error_55: assert(0);
	    } 
	    if((((((!(a25==1)&&(a11==1))&&(a28==9))&&(a19==1))&&(a21==1))&&(a17==7))){
	    	error_4: assert(0);
	    } 
	    if(((((((a25==1)&&(a11==1))&&(a28==11))&&(a19==1))&&(a21==1))&&(a17==8))){
	    	error_47: assert(0);
	    } 
	    if(((((((a25==1)&&(a11==1))&&(a28==8))&&!(a19==1))&&(a21==1))&&(a17==7))){
	    	error_11: assert(0);
	    } 
	    if((((((!(a25==1)&&!(a11==1))&&(a28==8))&&!(a19==1))&&(a21==1))&&(a17==7))){
	    	error_32: assert(0);
	    } 
	    if(((((((a25==1)&&(a11==1))&&(a28==8))&&!(a19==1))&&(a21==1))&&(a17==8))){
	    	error_51: assert(0);
	    } 
	    if(((((((a25==1)&&(a11==1))&&(a28==7))&&(a19==1))&&(a21==1))&&(a17==8))){
	    	error_39: assert(0);
	    } 
	    if((((((!(a25==1)&&(a11==1))&&(a28==10))&&(a19==1))&&(a21==1))&&(a17==8))){
	    	error_46: assert(0);
	    } 
	    if(((((((a25==1)&&(a11==1))&&(a28==8))&&(a19==1))&&(a21==1))&&(a17==7))){
	    	error_1: assert(0);
	    } 
	    if((((((!(a25==1)&&!(a11==1))&&(a28==9))&&(a19==1))&&(a21==1))&&(a17==7))){
	    	error_24: assert(0);
	    } 
	    if((((((!(a25==1)&&(a11==1))&&(a28==7))&&(a19==1))&&(a21==1))&&(a17==7))){
	    	error_0: assert(0);
	    } 
	    if(((((((a25==1)&&!(a11==1))&&(a28==8))&&!(a19==1))&&(a21==1))&&(a17==7))){
	    	error_31: assert(0);
	    } 
	    if((((((!(a25==1)&&(a11==1))&&(a28==9))&&!(a19==1))&&(a21==1))&&(a17==7))){
	    	error_14: assert(0);
	    } 
	    if((((((!(a25==1)&&(a11==1))&&(a28==8))&&!(a19==1))&&(a21==1))&&(a17==7))){
	    	error_12: assert(0);
	    } 
	    if(((((((a25==1)&&(a11==1))&&(a28==11))&&!(a19==1))&&(a21==1))&&(a17==7))){
	    	error_17: assert(0);
	    } 
	    if(((((((a25==1)&&!(a11==1))&&(a28==11))&&!(a19==1))&&(a21==1))&&(a17==7))){
	    	error_37: assert(0);
	    } 
	    if(((((((a25==1)&&(a11==1))&&(a28==7))&&!(a19==1))&&(a21==1))&&(a17==7))){
	    	error_9: assert(0);
	    } 
	    if((((((!(a25==1)&&(a11==1))&&(a28==11))&&(a19==1))&&(a21==1))&&(a17==7))){
	    	error_8: assert(0);
	    } 
	    if((((((!(a25==1)&&(a11==1))&&(a28==10))&&!(a19==1))&&(a21==1))&&(a17==8))){
	    	error_56: assert(0);
	    } 
	    if((((((!(a25==1)&&!(a11==1))&&(a28==11))&&(a19==1))&&(a21==1))&&(a17==7))){
	    	error_28: assert(0);
	    } 
	    if(((((((a25==1)&&(a11==1))&&(a28==7))&&!(a19==1))&&(a21==1))&&(a17==8))){
	    	error_49: assert(0);
	    } 
	    if(((((((a25==1)&&(a11==1))&&(a28==10))&&(a19==1))&&(a21==1))&&(a17==7))){
	    	error_5: assert(0);
	    } 
	    if((((((!(a25==1)&&(a11==1))&&(a28==11))&&!(a19==1))&&(a21==1))&&(a17==7))){
	    	error_18: assert(0);
	    } 
	    if((((((!(a25==1)&&!(a11==1))&&(a28==7))&&!(a19==1))&&(a21==1))&&(a17==7))){
	    	error_30: assert(0);
	    } 
	    if(((((((a25==1)&&(a11==1))&&(a28==9))&&(a19==1))&&(a21==1))&&(a17==7))){
	    	error_3: assert(0);
	    } 
	    if(((((((a25==1)&&(a11==1))&&(a28==10))&&!(a19==1))&&(a21==1))&&(a17==7))){
	    	error_15: assert(0);
	    } 
	    if((((((!(a25==1)&&!(a11==1))&&(a28==11))&&!(a19==1))&&(a21==1))&&(a17==7))){
	    	error_38: assert(0);
	    } 
	    if((((((!(a25==1)&&!(a11==1))&&(a28==10))&&!(a19==1))&&(a21==1))&&(a17==7))){
	    	error_36: assert(0);
	    } 
	    if(((((((a25==1)&&!(a11==1))&&(a28==9))&&(a19==1))&&(a21==1))&&(a17==7))){
	    	error_23: assert(0);
	    } 
	    if(((((((a25==1)&&!(a11==1))&&(a28==10))&&(a19==1))&&(a21==1))&&(a17==7))){
	    	error_25: assert(0);
	    } 
	    if((((((!(a25==1)&&(a11==1))&&(a28==7))&&!(a19==1))&&(a21==1))&&(a17==7))){
	    	error_10: assert(0);
	    } 
	    if((((((!(a25==1)&&!(a11==1))&&(a28==10))&&(a19==1))&&(a21==1))&&(a17==7))){
	    	error_26: assert(0);
	    } 
	    if((((((!(a25==1)&&(a11==1))&&(a28==11))&&(a19==1))&&(a21==1))&&(a17==8))){
	    	error_48: assert(0);
	    } 
	    if(((((((a25==1)&&(a11==1))&&(a28==9))&&!(a19==1))&&(a21==1))&&(a17==8))){
	    	error_53: assert(0);
	    } 
	    if(((((((a25==1)&&(a11==1))&&(a28==11))&&(a19==1))&&(a21==1))&&(a17==7))){
	    	error_7: assert(0);
	    } 
	    if((((((!(a25==1)&&(a11==1))&&(a28==10))&&(a19==1))&&(a21==1))&&(a17==7))){
	    	error_6: assert(0);
	    } 
	    if(((((((a25==1)&&!(a11==1))&&(a28==9))&&!(a19==1))&&(a21==1))&&(a17==7))){
	    	error_33: assert(0);
	    } 
	    return -2; 
	}

int main()
{
    // default output
    int output = -1;

    // main i/o-loop
    while(1)
    {
        // read input
        int input;
        scanf("%d", &input);        

        // operate eca engine
        output = calculate_output(input);

        if(output == -2)
        	fprintf(stderr, "Invalid input: %d\n", input);
        else if(output != -1)
			printf("%d\n", output);
    }
}
