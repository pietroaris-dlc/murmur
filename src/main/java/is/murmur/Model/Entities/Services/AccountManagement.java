package is.murmur.Model.Entities.Services;

import is.murmur.Model.DAO.Command.CommandFactory;
import is.murmur.Model.DAO.Command.DataAccessFacade;
import is.murmur.Model.DAO.DAOFactory;
import is.murmur.Model.Entities.DBEntities.Contract;
import is.murmur.Model.Entities.DBEntities.RegisteredUser;
import org.mindrot.jbcrypt.BCrypt;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AccountManagement {

    private static final int EMAIL = 0;
    private static final int PASSWORD = 1;
    private static final int FIRST_NAME = 2;
    private static final int LAST_NAME = 3;
    private static final int BIRTH_DATE = 4;
    private static final int BIRTH_CITY = 5;
    private static final int BIRTH_DISTRICT = 6;
    private static final int BIRTH_COUNTRY = 7;
    private static final int TAXCODE = 8;

    public static boolean signIn(String[] accountInfo, DataAccessFacade dataAccessFacade){
        return dataAccessFacade.executeCommand(
                CommandFactory.insert(DAOFactory.registeredUser(),
                        new RegisteredUser(
                                accountInfo[EMAIL], BCrypt.hashpw(accountInfo[PASSWORD], BCrypt.gensalt()),
                                accountInfo[FIRST_NAME], accountInfo[LAST_NAME],
                                LocalDate.parse(accountInfo[BIRTH_DATE]), accountInfo[BIRTH_CITY],
                                accountInfo[BIRTH_DISTRICT], accountInfo[BIRTH_COUNTRY],
                                accountInfo[TAXCODE]
                        )
                )
        );
    }

    public static boolean logIn(String[] loginInfo, DataAccessFacade dataAccessFacade, List<Contract> drafts){
        Map<String, Object> filters = new HashMap<>();
        filters.put("email", loginInfo[EMAIL]);
        RegisteredUser registeredUser = dataAccessFacade.executeCommand(
                CommandFactory.singleSelect(DAOFactory.registeredUser(),filters)
        );
        boolean checkPsw = false;
        if(registeredUser != null){
            checkPsw = BCrypt.checkpw(loginInfo[PASSWORD], registeredUser.getPassword());
        }
        if(drafts != null && !drafts.isEmpty() && checkPsw){
            for(Contract draft : drafts){
                    dataAccessFacade.executeCommand(
                            CommandFactory.insert(DAOFactory.contract(), draft)
                    );
            }
        }
        return checkPsw;
    }

    public static boolean logout(List<Contract> drafts, DataAccessFacade dataAccessFacade){
        Map<String, Object> filters = new HashMap<>();
        for(Contract draft : drafts){
            filters.put("id", draft.getId());
            if(dataAccessFacade.executeCommand(
                    CommandFactory.select(DAOFactory.contract(), filters)
            ) == null){
                dataAccessFacade.executeCommand(
                        CommandFactory.insert(DAOFactory.contract(), draft)
                );
            }
            filters.clear();
        }
        return false;
    }

    public static boolean accountDeletion(RegisteredUser registeredUser, DataAccessFacade dataAccessFacade){
        long userId = registeredUser.getId();

        List<Contract> contracts = dataAccessFacade.executeCommand(
                CommandFactory.select(DAOFactory.contract(), )
        );
        return false;
    }
    public static boolean upgradeApplication(){
        return false;
    }
    public static boolean collabApplication(){
        return false;
    }
    public static boolean upgradeAccount(){
        return false;
    }
    public static boolean startCollab(){
        return false;
    }
    public static boolean personalInfosView(){
        return false;
    }
}