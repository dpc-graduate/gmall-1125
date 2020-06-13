import com.atguigu.gmall.common.utils.JwtUtils;
import com.atguigu.gmall.common.utils.RsaUtils;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: dpc
 * @data: 2020/6/9,19:09
 */
public class JwtTest {

    // 别忘了创建D:\\project\rsa目录
    private static final String pubKeyPath = "D:\\dpc\\rsa\\rsa.pub";
    private static final String priKeyPath = "D:\\dpc\\rsa\\rsa.pri";
    static PublicKey publicKey = null;
    static PrivateKey privateKey = null;

    static {
        try {
            publicKey = RsaUtils.getPublicKey(pubKeyPath);
        } catch (Exception e) {
            e.printStackTrace();
        }


        try {
             privateKey = RsaUtils.getPrivateKey(priKeyPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public JwtTest() throws Exception {
    }

    @Test
    public void testRsa() throws Exception {
        RsaUtils.generateKey(pubKeyPath, priKeyPath, "234");
    }

//    @BeforeEach
//    public void testGetRsa() throws Exception {
//        this.publicKey = RsaUtils.getPublicKey(pubKeyPath);
//        this.privateKey = RsaUtils.getPrivateKey(priKeyPath);
//    }

    @Test
    public void testGenerateToken() throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("id", "11");
        map.put("username", "liuyan");
        // 生成token
        String token = JwtUtils.generateToken(map, privateKey, 5);
        System.out.println("token = " + token);
    }

    @Test
    public void testParseToken() throws Exception {
        String token = "eyJhbGciOiJSUzI1NiJ9.eyJpZCI6IjExIiwidXNlcm5hbWUiOiJsaXV5YW4iLCJleHAiOjE1OTE3MDE2Mzd9.QLCio-LhaX7mCN8p2tiGy_SVWpDz_UROhMgBxqQLUG0Ack7Qy3p8Gl3zoGiV8HBYglwPvd4B-Quhfb4TjAu2JxLKIg-DDf_NCO9p96CPlqyMSdwtrgHKmrfSV2fJIgmN3Sk70QjMFc-y2si8wUv45-e1qoCddZGobhFxsk-BKYS1GUDw-sV3qmXUk2r1VJAfjfrsZ3yKqrCeMfxmBOvJ8qV0UCHkkB3H54S4NSWxa-dMtvAE5bFICscyUpozVUmVDmmRKbj0LBrPCSugbJXQEnE1-ANZCmxoNs9xR8QXk9glee7ooIjiRgyO4aPUBgLMtsqhx1SLJAffDix-4p5SFQ";

        // 解析token
        Map<String, Object> map = JwtUtils.getInfoFromToken(token, publicKey);
        System.out.println("id: " + map.get("id"));
        System.out.println("userName: " + map.get("username"));
    }
}