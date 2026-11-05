//3.
#include <stdio.h>

int main()
{
    int x;
    double cost = 0;
    
    scanf("%d", &x);		//输入
    
    if (x <= 750)
        cost = x * 0.1;
    else if (x <= 2250)
        cost = 75.0 + (x - 750) * 0.2;
    else if (x <= 3750)
        cost = 375.0 + (x - 2250) * 0.3;
    else if (x <= 5250)
        cost = 825.0 + (x - 3750) * 0.4;
    else if (x <= 7000)
        cost = 1425.0 + (x - 5250) * 0.5;
    else
        cost = 2300.0 + (x - 7000) * 0.6;
    
    printf("%.2f\n", cost);		//输出
    
    return 0;
}
//时间复杂度o(1),空间复杂度o(1).
