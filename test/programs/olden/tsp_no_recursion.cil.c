/* Generated by CIL v. 1.3.7 */
/* print_CIL_Input is true */

#line 1 "tsp.h"
struct tree {
   int sz ;
   double x ;
   double y ;
   struct tree *left ;
   struct tree *right ;
   struct tree *next ;
   struct tree *prev ;
};
#line 1 "tsp.h"
typedef struct tree *Tree;
#line 11
extern Tree build_tree(int n , int dir , int lo , int num_proc , double min_x , double max_x ,
                       double min_y , double max_y ) ;
#line 14
extern Tree tsp(Tree t , int sz , int nproc ) ;
#line 531 "/usr/include/stdlib.h"
extern  __attribute__((__nothrow__, __noreturn__)) void exit(int __status ) ;
#line 13 "main.c"
int flag  ;
#line 14 "main.c"
int __NumNodes  ;
#line 14 "main.c"
int __NDim  ;
#line 16 "main.c"
int mylog(int num ) 
{ int j ;
  int k ;

  {
#line 18
  j = 0;
#line 18
  k = 1;
  {
#line 20
  while (1) {
    while_0_continue: /* CIL Label */ ;
#line 20
    if (k < num) {

    } else {
      goto while_0_break;
    }
#line 20
    k = k * 2;
#line 20
    j = j + 1;
  }
  while_0_break: /* CIL Label */ ;
  }
#line 21
  return (j);
}
}
#line 24 "main.c"
int dealwithargs(void) 
{ int num ;

  {
  {
#line 28
  flag = 0;
#line 29
  __NumNodes = 4;
#line 31
  __NDim = mylog(__NumNodes);
#line 33
  num = 150000;
  }
#line 35
  return (num);
}
}
#line 45
extern int ( /* missing proto */  chatting)() ;
#line 38 "main.c"
void print_tree(Tree t ) 
{ Tree left ;
  Tree right ;
  double x ;
  double y ;
  unsigned int __cil_tmp6 ;
  unsigned int __cil_tmp7 ;
  unsigned int __cil_tmp8 ;
  unsigned int __cil_tmp9 ;
  unsigned int __cil_tmp10 ;
  unsigned int __cil_tmp11 ;
  unsigned int __cil_tmp12 ;
  unsigned int __cil_tmp13 ;
  double *mem_14 ;
  double *mem_15 ;
  struct tree **mem_16 ;
  struct tree **mem_17 ;

  {
#line 43
  if (! t) {
#line 43
    return;
  } else {

  }
  {
#line 44
  __cil_tmp6 = (unsigned int )t;
#line 44
  __cil_tmp7 = __cil_tmp6 + 4;
#line 44
  mem_14 = (double *)__cil_tmp7;
#line 44
  x = *mem_14;
#line 44
  __cil_tmp8 = (unsigned int )t;
#line 44
  __cil_tmp9 = __cil_tmp8 + 12;
#line 44
  mem_15 = (double *)__cil_tmp9;
#line 44
  y = *mem_15;
#line 45
  chatting("x=%f,y=%f\n", x, y);
#line 46
  __cil_tmp10 = (unsigned int )t;
#line 46
  __cil_tmp11 = __cil_tmp10 + 20;
#line 46
  mem_16 = (struct tree **)__cil_tmp11;
#line 46
  left = *mem_16;
#line 46
  __cil_tmp12 = (unsigned int )t;
#line 46
  __cil_tmp13 = __cil_tmp12 + 24;
#line 46
  mem_17 = (struct tree **)__cil_tmp13;
#line 46
  right = *mem_17;
#line 47
  print_tree(left);
#line 48
  print_tree(right);
  }
#line 49
  return;
}
}
#line 51 "main.c"
void print_list(Tree t ) 
{ Tree tmp ;
  double x ;
  double y ;
  unsigned int __cil_tmp5 ;
  unsigned int __cil_tmp6 ;
  unsigned int __cil_tmp7 ;
  unsigned int __cil_tmp8 ;
  unsigned int __cil_tmp9 ;
  unsigned int __cil_tmp10 ;
  unsigned int __cil_tmp11 ;
  unsigned int __cil_tmp12 ;
  unsigned int __cil_tmp13 ;
  unsigned int __cil_tmp14 ;
  unsigned int __cil_tmp15 ;
  unsigned int __cil_tmp16 ;
  unsigned int __cil_tmp17 ;
  unsigned int __cil_tmp18 ;
  double *mem_19 ;
  double *mem_20 ;
  struct tree **mem_21 ;
  double *mem_22 ;
  double *mem_23 ;
  struct tree **mem_24 ;

  {
#line 56
  if (! t) {
#line 56
    return;
  } else {

  }
  {
#line 57
  __cil_tmp5 = (unsigned int )t;
#line 57
  __cil_tmp6 = __cil_tmp5 + 4;
#line 57
  mem_19 = (double *)__cil_tmp6;
#line 57
  x = *mem_19;
#line 57
  __cil_tmp7 = (unsigned int )t;
#line 57
  __cil_tmp8 = __cil_tmp7 + 12;
#line 57
  mem_20 = (double *)__cil_tmp8;
#line 57
  y = *mem_20;
#line 58
  chatting("%f %f\n", x, y);
#line 59
  __cil_tmp9 = (unsigned int )t;
#line 59
  __cil_tmp10 = __cil_tmp9 + 28;
#line 59
  mem_21 = (struct tree **)__cil_tmp10;
#line 59
  tmp = *mem_21;
  }
  {
#line 59
  while (1) {
    while_1_continue: /* CIL Label */ ;
    {
#line 59
    __cil_tmp11 = (unsigned int )t;
#line 59
    __cil_tmp12 = (unsigned int )tmp;
#line 59
    if (__cil_tmp12 != __cil_tmp11) {

    } else {
      goto while_1_break;
    }
    }
    {
#line 61
    __cil_tmp13 = (unsigned int )tmp;
#line 61
    __cil_tmp14 = __cil_tmp13 + 4;
#line 61
    mem_22 = (double *)__cil_tmp14;
#line 61
    x = *mem_22;
#line 61
    __cil_tmp15 = (unsigned int )tmp;
#line 61
    __cil_tmp16 = __cil_tmp15 + 12;
#line 61
    mem_23 = (double *)__cil_tmp16;
#line 61
    y = *mem_23;
#line 62
    chatting("%f %f\n", x, y);
#line 59
    __cil_tmp17 = (unsigned int )tmp;
#line 59
    __cil_tmp18 = __cil_tmp17 + 28;
#line 59
    mem_24 = (struct tree **)__cil_tmp18;
#line 59
    tmp = *mem_24;
    }
  }
  while_1_break: /* CIL Label */ ;
  }
#line 64
  return;
}
}
#line 66 "main.c"
double wallclock  ;
#line 85
extern int ( /* missing proto */  timer_start)() ;
#line 87
extern int ( /* missing proto */  timer_stop)() ;
#line 90
extern int ( /* missing proto */  timer_elapsed)() ;
#line 68 "main.c"
int main(void) 
{ Tree t ;
  int num ;
  int tmp ;

  {
  {
#line 73
  num = dealwithargs();
#line 75
  chatting("Building tree of size %d\n", num);
#line 76
  t = build_tree(num, 0, 0, __NumNodes, 0.0, 1.0, 0.0, 1.0);
  }
#line 80
  if (! flag) {
    {
#line 80
    chatting("Past build\n");
    }
  } else {

  }
#line 81
  if (flag) {
    {
#line 81
    chatting("newgraph\n");
    }
  } else {

  }
#line 82
  if (flag) {
    {
#line 82
    chatting("newcurve pts\n");
    }
  } else {

  }
  {
#line 85
  timer_start(0);
#line 86
  tsp(t, 150, __NumNodes);
#line 87
  timer_stop(0);
  }
#line 88
  if (flag) {
    {
#line 88
    print_list(t);
    }
  } else {

  }
#line 89
  if (flag) {
    {
#line 89
    chatting("linetype solid\n");
    }
  } else {

  }
  {
#line 90
  tmp = timer_elapsed(0);
#line 90
  chatting("Time for TSP = %f\n", tmp);
#line 98
  exit(0);
  }
}
}
