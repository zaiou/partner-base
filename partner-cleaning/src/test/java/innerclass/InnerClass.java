package innerclass;

import org.apache.hadoop.yarn.webapp.hamlet.Hamlet;

/**
 * @author zaiou 2019-10-16
 * @Description:内部类
 * @modify zaiou 2019-10-16
 */
public class InnerClass {
    public static void main(String[] args) {
        System.out.println("测试内部类");

        //调用成员内部类
        A.B a= new A().new B();
        a.say();

        //调用局部内部类
        new C().test();
    }
}

//成员内部类
class A{
    private String str="我是外部类成员";
    class B{
        public void say(){
            System.out.println("我是内部类:"+str);
        }
    }
}

//局部内部类
class C{
    public void test(){
        class D{
            public void in(){
                System.out.println("我是局部内部类");
            }
        }
        D d=new D();
        d.in();
    }
}
