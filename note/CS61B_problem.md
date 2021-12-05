# CS61B - Problem record

## 1. 对按键没有反应

project0    public boolean tilt(Side side);

问题：图形界面对按键没有反应

解决：

![image-20211205161902814](D:\Repo\CS61B-Spring21\note\CS61B_problem.assets\image-20211205161902814.png)

如上图所示，在中文系统中，由getkey() 读取的cnmd是“向上箭头”， “向下箭头” ，......

所以在下面的的case中加入这些即可



