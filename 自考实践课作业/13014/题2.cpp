#include<stdio.h>
#define MAXN 100
struct student
{
    long long id;
    char name[MAXN];
    int score[3];
};
void SucceedStudent(FILE* in, FILE* out);
int main()
{
    FILE *in=NULL, *out=NULL;
    in = fopen("in.txt", "r");
    out = fopen("out.txt", "w");
    if (in && out)
    {
        SucceedStudent(in, out);
    }
    else
        puts("文件无法打开!");
    if (in)
    fclose(in);//请填空
    if (out)
    fclose(out);//请填空
    puts("文件保存成功!");
    return 0;
}
/* 请在这里给出void SucceedStudent(FILE* in, FILE* out); 定义*/
void SucceedStudent(FILE* in, FILE* out)		//SucceedStudent函数体
{
    char buf[256];			//缓冲 读文件一行数据
    char course[3][10] = {"Math", "English", "C"};		//缓存 成绩数组

    while (fgets(buf, sizeof(buf), in) != NULL)		//库函数从in文件读一行存buf
    {
        char* p = buf;

        //学号
        long long id = 0;
		while (*p >= '0' && *p <= '9')		//字符转数字
		{
    		int digit;
    		if (*p == '0') digit = 0;
    		else if (*p == '1') digit = 1;
    		else if (*p == '2') digit = 2;
    		else if (*p == '3') digit = 3;
    		else if (*p == '4') digit = 4;
    		else if (*p == '5') digit = 5;
    		else if (*p == '6') digit = 6;
    		else if (*p == '7') digit = 7;
    		else if (*p == '8') digit = 8;
    		else if (*p == '9') digit = 9;

    		id = id * 10 + digit;
    		p++;
		}

        //姓名
        while (*p == ' ' || *p == '\t' || *p == '\r') p++;
        char name[20];
        int i = 0;
		while (*p != ' ' && *p != '\t' && *p != '\n' && *p != '\r' && *p != '\0')		//取有效字符
            name[i++] = *p++;
        name[i] = '\0';	

        //成绩
        int score[3];
        for (int k = 0; k < 3; k++)
        {
            while (*p == ' ' || *p == '\t' || *p == '\r') p++;  //取有效字符
            score[k] = 0;		
            while (*p >= '0' && *p <= '9')
            {
                int digit;
        		if (*p == '0') digit = 0;
        		else if (*p == '1') digit = 1;
        		else if (*p == '2') digit = 2;
        		else if (*p == '3') digit = 3;
        		else if (*p == '4') digit = 4;
        		else if (*p == '5') digit = 5;
        		else if (*p == '6') digit = 6;
        		else if (*p == '7') digit = 7;
        		else if (*p == '8') digit = 8;
       		 	else if (*p == '9') digit = 9;
        		else digit = 0;

        		score[k] = score[k] * 10 + digit;
        		p++;
            }
        }

        //判断 1写入
        for (int k = 0; k < 3; k++)
        {
            if (score[k] >= 60)
                fprintf(out, "%lld %s %s\n", id, name, course[k]);		//写入out文件
        }
    }
}
//时间复杂度o(n),空间复杂度o(1).
