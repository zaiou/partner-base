package innerclass;

/**
 * @author zaiou 2019-10-17
 * @Description:
 * @modify zaiou 2019-10-17
 */
public class ProcessImpl{
    public Object doProcess(Smoking smoking, Object... params) throws Exception {
        smoking.smoke();
        return null;
    }
}
