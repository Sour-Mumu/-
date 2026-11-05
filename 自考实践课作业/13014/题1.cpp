#include<stdio.h>
#include<math.h>
void loan(double* pointer,double rate,int months);
int main()
{
    double principal,rate;
    int month;
    scanf("%lf %lf %d",&principal,&rate,&month);
    loan(&principal,rate,month);//函数调用修改本金principal
    printf("%d个月后应还款：%.2f\n",month,principal);
    return 0;
}
/*请在此给出函数void loan(double* pointer,double rate,int months);的定义*/
void loan(double* pointer, double rate, int months) 		//loan函数体
{
    double money = *pointer;		// 解引用读
    int i; 
    
    for(i = 1; i <= months; i = i + 1)		// 循环
    {
        money = money * (1 + rate);		// new本金=old本金× (1 +月利)^months.
    }
    //i>months退出循环
    *pointer = money;		// 解引用写
}
//时间复杂度o(n),空间复杂度o(1).
