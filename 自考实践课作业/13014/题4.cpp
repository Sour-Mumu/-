//4.
#include <stdio.h>

int main()
{
    int N, M;
    scanf("%d %d", &N, &M);
    
    int score[128];      // 每题的分值
    int answer[128];     // 每题的正确答案
    
    // 读入每题分值
    for (int i = 0; i < M; i++)
        scanf("%d", &score[i]);
    
    // 读入每题正确答案
    for (int i = 0; i < M; i++)
        scanf("%d", &answer[i]);
    
    // 判决
    for (int i = 0; i < N; i++)
    {
        int total = 0;          // 该学生总分
        int stu[128];           // 该学生的作答
        
        // 读入该学生的M个作答
        for (int j = 0; j < M; j++)
            scanf("%d", &stu[j]);
        
        // 逐题判分
        for (int j = 0; j < M; j++)
        {
            if (stu[j] != 0 && stu[j] == answer[j])		//判断
                total += score[j];
        }
        
        // 输出该学生总分
        printf("%d\n", total);
    }
    
    return 0;
}
//时间复杂度o(nm),空间复杂度o(m).

