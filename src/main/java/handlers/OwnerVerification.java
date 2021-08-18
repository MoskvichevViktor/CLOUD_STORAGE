package handlers;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.*;

public class OwnerVerification {
    public static void main(String[] args) throws Exception {
        /*
        //get - получили
        Path path = Paths.get("C:/Users/Наталия/Documents/Витя/GeekBrains/CLOUD_STORAGE/user/2.txt");
        FileOwnerAttributeView view = Files.getFileAttributeView(path,
                FileOwnerAttributeView.class);
        UserPrincipal userPrincipal = view.getOwner();
        System.out.println(userPrincipal.getName());

        //set - установили
        FileSystem fileSystem = path.getFileSystem();
        UserPrincipalLookupService service = fileSystem.getUserPrincipalLookupService();
        UserPrincipal userPrincipalSet = service.lookupPrincipalByName(ClientInputHandler.getLogin());
        Files.setOwner(path, userPrincipalSet);
        UserPrincipal owner = Files.getOwner(path);
        System.out.println("Owner: " + owner.getName());

         */
        /*
        В данном методе хотел реалезовать доступ только к файлам клиента.
        Идея заключалась в использовании метода getOwner - изменение владельца файла.
        Владелец файла = ClientInputHandler.getLogin();
        В дальнейшем планировалось сравнение Владельца файла и логина клиента на соответствие.

        В процессе выполнения не смог запустить getOwner

         */
    }

}
