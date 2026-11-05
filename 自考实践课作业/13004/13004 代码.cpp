//题目：给定先序序列，按后序顺序，遍历跟节点到每个节点路径
//思路：
//1.先序序列根左右建立二叉树
//2.使用堆栈path[]记录当前路径，pathLen记录当前路度，push: pathLen++;	pop: pathLen--;
//3.按后序序列左右根输出，并打印到根节点路径，出栈序列
//



//伪代码
//CreateTree(T):
//    读字符 ch
//    if ch == '#' then T = NULL
//    else
//        T = 新建节点(ch)
//        CreateTree(T的左孩子)
//        CreateTree(T的右孩子)
//
//PostOrder(T):
//    if T == NULL return
//    path[pathLen++] = T.data		//入栈
//    PostOrder(T的左孩子)
//    PostOrder(T的右孩子)
//    print path[0..pathLen-1] 		//后序位置打印
//pathLen--		//回溯出栈


//完整代码
#include <stdio.h>
#include <stdlib.h>

typedef struct BiTNode {			//节点
    char data;
    struct BiTNode *lchild;
    struct BiTNode *rchild;
} BiTNode, *BiTree;

char str[100];		//先序遍历字符串
int index = 0;		//当前字符串数组下标

//建树
void CreateBiTree(BiTree *T) {
    char ch = str[index++];
    if (ch == '#') {
        *T = NULL;  //空节点
    } else {
        *T = (BiTNode *)malloc(sizeof(BiTNode));		//动态分配
        (*T)->data = ch;		//赋值
        CreateBiTree(&((*T)->lchild));			//左孩子指针的地址
        CreateBiTree(&((*T)->rchild));		//右孩子指针的地址
    }
}

// 后序遍历和输出路径
void PostOrderPath(BiTree T, char path[], int pathLen) {
    if (T == NULL) return;		//空树返回
    
    //当前节点加入路径
    path[pathLen] = T->data;
    pathLen++;
    
    // 递归遍历左子树
    PostOrderPath(T->lchild, path, pathLen);
    // 递归遍历右子树
    PostOrderPath(T->rchild, path, pathLen);
    
    printf("%c: ", T->data);		//输出 节点名: 路径
    for (int i = 0; i < pathLen; i++) {
        if (i == 0)
            printf("%c", path[i]);
        else
            printf("->%c", path[i]);
    }
    printf("\n");
}

int main() {
    scanf("%s", str);		//先序
    
    BiTree T = NULL;		//初始化
    index = 0;
    
    CreateBiTree(&T);		//建树
    char path[26];		//创建数组

    PostOrderPath(T, path, 0);		//遍历
    
    return 0;
}
//时间复杂度o(nlogn),空间复杂度o(n).
