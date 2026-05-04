import ocsms.controller.AuthController;
import ocsms.controller.AuthController.RegisterResult;
import ocsms.model.User.UserRole;

public class TestSupabase {
    public static void main(String[] args) {
        System.out.println("Registering a new test user...");
        AuthController auth = new AuthController();
        RegisterResult res = auth.register("24P-0000", "Test User", "test@fast.edu.pk", "Password1", "Password1", UserRole.UNIVERSITY_ADMIN);
        System.out.println("Success: " + res.success);
        System.out.println("Message: " + res.message);
    }
}
