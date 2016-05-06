package maiyatian.livedemo;

import java.io.Serializable;

/**
 * Created by 王中阳 on 2016/5/6.
 */
public class Result implements Serializable {
    sk sk;

    public maiyatian.livedemo.sk getSk() {
        return sk;
    }

    public void setSk(maiyatian.livedemo.sk sk) {
        this.sk = sk;
    }

    @Override
    public String toString() {
        return "Result{" +
                "sk=" + sk +
                '}';
    }
}
