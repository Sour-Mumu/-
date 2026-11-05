//5.
#include <stdio.h>
#include <math.h>

int main() {
    int n, m;
    scanf("%d %d", &n, &m);
    
    int a[128][128];		//原图只读
    int b[128][128];		//平滑后
    
    // 读原图
    for (int i = 0; i < n; i++) {
        for (int j = 0; j < m; j++) {
            scanf("%d", &a[i][j]);
        }
    }
    
    // 平滑
    for (int i = 0; i < n; i++) {
        for (int j = 0; j < m; j++) {
            if (i == 0 || i == n - 1 || j == 0 || j == m - 1) {
                b[i][j] = 0;
            } else {
                int sum = a[i][j] + a[i-1][j-1] + a[i-1][j+1]+ a[i+1][j-1] + a[i+1][j+1];
                b[i][j] = (int)round(sum / 5.0);		//四舍五入
            }
        }
    }
    
    // 输出
    for (int i = 0; i < n; i++) {
        for (int j = 0; j < m; j++) {
            if (j > 0) printf(" ");
            printf("%d", b[i][j]);
        }
        printf("\n");
    }
    
    return 0;
}
//时间复杂度o(nm),空间复杂度o(nm).
