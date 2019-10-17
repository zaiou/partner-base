package innerclass;

/**
 * @author zaiou 2019-10-16
 * @Description:测试匿名内部类
 * @modify zaiou 2019-10-16
 */
public class InnerTest {


    public static void main(String[] args) throws Exception{
        new ProcessImpl().doProcess(new Smoking() {
            @Override
            public void smoke() {
                System.out.println("测试局部匿名内部类");
            }
        });
    }
}
