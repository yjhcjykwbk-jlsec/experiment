#include <iostream>
#include <fstream>
#include <vector>
#include <stdio.h>
#include <string.h>
using namespace std;
#define MAXN 255  //arsc 代码
#define DOCNUM 1  //document 个数
//statics out the similarity of characters
//give 100 document pages and the tesseract result 
//compare the real characters with the result characters
float sim[MAXN][MAXN];
int cnt[MAXN];//how many times 'a' is present in real set
int errMap[MAXN][MAXN];//how many times that 'a' is error OCRed as 'b'
float gailvMap[MAXN][MAXN];//the gailv that OCR 'b' is errored as 'a'
void tongji(char *s1,char *s2){
	cout<<s1<<"\t\t\t"<<s2<<endl;
}
//if this character is a end character between words
//the problem is how to duiying the words
inline bool isEmptyCha(char c){
	return c==' '||c=='\n';
}
void getErrMap(){
	vector<string> words1,words2;
	char f1[20],f2[20];
	char s1[1001],s2[1001];
	char word1[100],word2[100];
	for(int i=1;i<=DOCNUM;i++){
		sprintf(f1,"%d.txt",i);
		sprintf(f2,"%d.real.txt",i);
		fstream fl1(f1),fl2(f2);
		cout<<"handling with "<<f1<<":"<<f2<<endl;
		if(fl1==NULL||fl2==NULL) {cout<<i<<".[real].txt missing"<<endl;continue;}

		fl1.getline(s1,1000),fl2.getline(s2,1000);
		fclose(fl1);fclose(fl2);

		int l=0,r=0,l1=0,r1=0;while(true){
			l=l1,r=r1;
			while(isEmptyCha(s1[l]))l++;
			while(isEmptyCha(s2[r]))r++;
			l1=l,r1=r;
			while(s1[l1]!=0&&!isEmptyCha(s1[l1])) l1++;
			while(s2[r1]!=0&&!isEmptyCha(s2[r1])) r1++;
			if(l1==l||r1==r) break;
			strncpy(word1,s1+l,l1-l);word1[l1-l]=0;
			strncpy(word2,s2+r,r1-r);word2[r1-r]=0;

			words1.push_back(string(word1)),words2.push_back(string(word2));
		}
	}
}
//correct use reverse max match
void correct(string res){
}
int main(){
	getErrMap();
	return 1;
}
