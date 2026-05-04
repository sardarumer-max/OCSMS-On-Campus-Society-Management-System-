import ocsms.controller.AuthController;
import ocsms.service.AuthService.LoginResult;
import ocsms.view.MainFrame;

public class TestLogin {
    public static void main(String[] args) {
        try {
            AuthController ctrl = new AuthController();
            LoginResult res = ctrl.login("00A-0000", "Password1");
            System.out.println("Login status: " + res.status);
            MainFrame mf = new MainFrame();
            System.out.println("MainFrame created successfully.");
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
