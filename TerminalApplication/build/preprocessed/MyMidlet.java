
import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;
import javax.microedition.lcdui.TextBox;
import javax.microedition.lcdui.Ticker;
import javax.microedition.midlet.*;
import javax.microedition.lcdui.Form;
import java.util.*;
import javax.microedition.lcdui.TextField;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreNotFoundException;

class TerminalBSCommunication extends Thread { //thread pou diaxeirizetai thn antallagh data metaksu kinhtou kai bs

    private BaseStationShort bs;

    public TerminalBSCommunication(BaseStationShort bs) {
        this.bs = bs;
    }

    public synchronized void setBaseStation(BaseStationShort bs) {
        this.bs = bs;
    }

    public synchronized void sendData(byte[] data) {//function that sends data
    }

    public synchronized byte[] recieveData() {//function that receives data
        return "".getBytes();
    }

    public void run() {
        while (true) {
            try {
                //send and recieve voice and data using sendData and recieveData for safety
                sleep(2000);
            } catch (InterruptedException ex) {
            }

        }
    }
}

class TerminalCPCCommunication extends Thread {//thread pou diaxeirizetai thn antallagh data metaksu kinhtou kai CPC

    private int millisecondBetweenCommunications;
    private String cpcip;
    private String cpcport;
    private MyMidlet m;

    public TerminalCPCCommunication(int millisecondBetweenCommunications, String cpcip, String cpcport, MyMidlet m) {
        this.cpcip = cpcip;
        this.cpcport = cpcport;
        this.m = m;
        this.millisecondBetweenCommunications = millisecondBetweenCommunications;
    }

    public void run() {

        while (true) {
            try {
                boolean connected_to_choice = false;
                Vector v = m.Discover();
                if (v.size() < 0) {
                    if (!m.automatic) {
                        for (int i = 0; i < v.size(); i++) {//elegxos an to bs pou dialeksame uparxei sth lista.
                            if (((BaseStationShort) v.elementAt(i)).baseStation_id.equals(m.myChoice.baseStation_id)) {
                                if (m.ConnectTo(m.myChoice)) {
                                    connected_to_choice = true;
                                    sleep(millisecondBetweenCommunications);
                                    break;
                                }
                            }
                        }
                    }

                    if (!connected_to_choice) {
                        for (int i = 0; i < v.size(); i++) {
                            if (m.ConnectTo((BaseStationShort) v.elementAt(i))) {   //an den mporei na sundethei me auto pou theloume
                                sleep(millisecondBetweenCommunications);            //pianei to prwto pou mporei na sundethei
                                break;
                            }
                        }
                    }
                }
                sleep(millisecondBetweenCommunications);
            } catch (InterruptedException e) {
            }
        }
    }
}

public class MyMidlet extends MIDlet implements CommandListener {

    private Command backButton;
    private Command enterButton;
    private Command okButton;
    private Command exitButton;
    private Command cancelButton;
    //othones
    private List startUpList;
    private List discoverList;
    private List optionsList;  //othoni epilogwn
    private List chargingList; //othoni epiloghs xrewshs
    private List connectionTypeInfo;
    private List favouritesList;
    private TextBox askForConnection;
    private Form askForInfo;
    private TextBox connectionFailure;
    private TextBox connectionSuccess;
    private TextBox deviceInfo;
    private TextBox userInfo;
    private TextField name;
    private String u_name;
    private TextField surname;
    private String u_surname;
    private TextField address;
    private String u_address;
    private String u_charging;
    private String u_favourites;
    private String currentScreen; //othoni pou eimaste
    public boolean automatic; //automath epilogh diktuou
    private String prevScreen; //prohgoumenh othonh (gia to backbutton)
    private Vector baseStations;
    private String cpcip;
    private String cpcport;
    public BaseStationShort myChoice;
    private BaseStationShort currentBaseStation;
    private TerminalBSCommunication bsCom;
    private TerminalCPCCommunication cpcCom;
    private TerminalActions trm;

    private String[] split(String original, String separator) { //function pou xwrizei to String me vash to separator
        Vector nodes = new Vector();
        int index = original.indexOf(separator);
        while (index >= 0) {
            nodes.addElement(original.substring(0, index - 1));
            original = original.substring(index + separator.length());
            index = original.indexOf(separator);
        }

        nodes.addElement(original);

        String[] result = new String[nodes.size()];
        if (nodes.size() > 0) {
            for (int loop = 0; loop < nodes.size(); loop++) {
                result[loop] = (String) nodes.elementAt(loop);
            }

        }

        return result;
    }

    public MyMidlet() {
        currentScreen = "";
        prevScreen = "";
        currentBaseStation = null;
        automatic = true;
        bsCom = null;





        u_name = "";
        u_surname = "";
        u_address = "";
        u_charging = "FIXED";
        u_favourites = "DATA";

        exitButton = new Command("Exit", Command.EXIT, 0);
        backButton = new Command("Back", Command.BACK, 0);
        cancelButton = new Command("Cancel", Command.CANCEL, 0);
        enterButton = new Command("Enter", Command.SCREEN, 0);
        okButton = new Command("OK", Command.OK, 0);


        trm = new TerminalActions();
        cpcip = trm.cpcip;
        cpcport = trm.cpcport;


        cpcCom = new TerminalCPCCommunication(5000, cpcip, cpcport, this);
        cpcCom.start();
    }

    public boolean loadInfo() { //diavasma apo recordstore
        try {
            byte[] recData;

            RecordStore rs = RecordStore.openRecordStore("UserInfo", false);

            recData = new byte[rs.getRecordSize(1)];
            int len = rs.getRecord(1, recData, 0);
            u_name = new String(recData, 0, len);

            recData = new byte[rs.getRecordSize(2)];
            len = rs.getRecord(2, recData, 0);
            u_surname = new String(recData, 0, len);

            recData = new byte[rs.getRecordSize(3)];
            len = rs.getRecord(3, recData, 0);
            u_address = new String(recData, 0, len);

            recData = new byte[rs.getRecordSize(4)];
            len = rs.getRecord(4, recData, 0);
            u_charging = new String(recData, 0, len);

            recData = new byte[rs.getRecordSize(5)];
            len = rs.getRecord(5, recData, 0);
            u_favourites = new String(recData, 0, len);

            recData = new byte[rs.getRecordSize(6)];
            len = rs.getRecord(6, recData, 0);
            automatic = new String(recData, 0, len).equals("AUTOMATIC");


            rs.closeRecordStore();
        } catch (RecordStoreNotFoundException ex) {
            try {
                RecordStore rs = RecordStore.openRecordStore("UserInfo", true);

                rs.addRecord(u_name.getBytes(), 0, u_name.length());
                rs.addRecord(u_surname.getBytes(), 0, u_surname.length());
                rs.addRecord(u_address.getBytes(), 0, u_address.length());
                rs.addRecord(u_charging.getBytes(), 0, u_charging.length());
                rs.addRecord(u_favourites.getBytes(), 0, u_favourites.length());
                String u_conType;
                if (automatic) {
                    u_conType = "AUTOMATIC";
                } else {
                    u_conType = "MANUAL";
                }
                rs.addRecord(u_conType.getBytes(), 0, u_conType.length());

                rs.closeRecordStore();
            } catch (RecordStoreException ex2) {
                ex2.printStackTrace();
            }
            return false;
        } catch (RecordStoreException ex) {
            return false;
        }

        return true;
    }

    public boolean saveInfo() { //apothikeush sto recordstore
        try {

            RecordStore rs = RecordStore.openRecordStore("UserInfo", false);

            rs.setRecord(1, u_name.getBytes(), 0, u_name.length());
            rs.setRecord(2, u_surname.getBytes(), 0, u_surname.length());
            rs.setRecord(3, u_address.getBytes(), 0, u_address.length());
            rs.setRecord(4, u_charging.getBytes(), 0, u_charging.length());
            rs.setRecord(5, u_favourites.getBytes(), 0, u_favourites.length());
            if (automatic) {
                rs.setRecord(6, "AUTOMATIC".getBytes(), 0, "AUTOMATIC".length());
            } else {
                rs.setRecord(6, "MANUAL".getBytes(), 0, "MANUAL".length());
            }

            rs.closeRecordStore();
        } catch (RecordStoreException ex) {
            return false;
        }

        return true;
    }

    public void startApp() {
        if (loadInfo()) {
            createStartUpList();
        } else {
            createAskForUserInfo();
        }
    }

    public void pauseApp() {
    }

    public void destroyApp(boolean unconditional) { //otan kleisei to kinhto aposundeetai apo ton bs
        if (currentBaseStation != null) {
            trm.DisconnectFrom(currentBaseStation);
        }
    }

    public void commandAction(Command c, Displayable d) { //leitourgies koumpiwn

        if (c == backButton) {
            if (prevScreen == "startUpList") {
                createStartUpList();
            } else if (prevScreen == "optionsList") {
                createOptionsList();
            }

        }

        if (c == cancelButton) {
            if (prevScreen == "discoverList") {
                createDiscoverList();
            }

        }

        if (c == exitButton) {
            destroyApp(true);
            notifyDestroyed();
        }

        if (c == enterButton) {
            if (d == startUpList) {
                if (startUpList.isSelected(0)) {
                    createDiscoverList();
                } else if (startUpList.isSelected(1)) {
                    createOptionsList();
                }
            } else if (d == discoverList) {
                myChoice = (BaseStationShort) baseStations.elementAt(discoverList.getSelectedIndex());
                createAskForConnection();
            } else if (d == askForInfo) {
                u_name = name.getString();
                u_surname = surname.getString();
                u_address = address.getString();

                createAskForCharging();
            } else if (d == chargingList) {
                u_charging = chargingList.getString(chargingList.getSelectedIndex());

                createAskForFavourites();
            } else if (d == favouritesList) {
                u_favourites = favouritesList.getString(favouritesList.getSelectedIndex());
                saveInfo();

                createStartUpList();
            } else if (d == askForConnection) {
                if (ConnectTo(myChoice)) {
                    createconnectionSuccess();
                } else {
                    createconnectionFailure();
                }
            } else if (d == optionsList) {
                if (optionsList.isSelected(0)) {
                    createDeviceInfo();
                } else if (optionsList.isSelected(1)) {
                    createUserInfo();
                } else if (optionsList.isSelected(2)) {
                    createConnectionTypeInfo();
                }
            } else if (d == userInfo) {
                createAskForUserInfo();
            } else if (d == connectionTypeInfo) {
                automatic = connectionTypeInfo.getSelectedIndex() == 0;
                saveInfo();
                createOptionsList();
            }
        } else if (c == okButton) {
            if (d == connectionFailure) {
                createDiscoverList();
            } else if (d == connectionSuccess) {
                createStartUpList();
            } else if (d == deviceInfo) {
                createOptionsList();
            }


        }
    }

    public void createStartUpList() { //dhmiourgia arxikhs othonhs
        prevScreen = "";
        currentScreen = "startUpList";

        startUpList = new List("Select an action:", Choice.IMPLICIT);
        startUpList.setTicker(new Ticker("Our awesome app!"));
        startUpList.addCommand(enterButton);
        startUpList.addCommand(exitButton);
        startUpList.setCommandListener(this);
        startUpList.append("Connectivity", null);
        startUpList.append("Options", null);
        getDisplay().setCurrent(startUpList);
    }

    public void createDiscoverList() {
        prevScreen = "startUpList";
        currentScreen = "discoverList";

        discoverList = new List("Select a Base Station:", Choice.IMPLICIT);
        discoverList.setTicker(new Ticker("Warning : The fact that a Base Station appears in this list does not certainly mean it's available!"));
        discoverList.addCommand(enterButton);
        discoverList.addCommand(backButton);
        discoverList.setCommandListener(this);

        baseStations = trm.Discover();
        for (int i = 0; i < baseStations.size(); i++) {
            discoverList.append(((BaseStationShort) baseStations.elementAt(i)).baseStation_id, null);
        }

        getDisplay().setCurrent(discoverList);
    }

    public void createAskForConnection() {
        prevScreen = "discoverList";
        currentScreen = "askForConnection";

        askForConnection = new TextBox("Selected BaseStation:", "BaseStationID : " + myChoice.baseStation_id + "\n"
                + "NetworkId : " + myChoice.provider + "\n"
                + "Charging : " + myChoice.charging + "\n"
                + "SignalStrength : " + myChoice.signalStrength + "\n", 256, 0);
        askForConnection.setConstraints(TextField.UNEDITABLE);

        askForConnection.addCommand(enterButton);
        askForConnection.addCommand(cancelButton);
        askForConnection.setCommandListener(this);

        getDisplay().setCurrent(askForConnection);
    }

    public void createAskForUserInfo() { //othonh pou vazei o xrhsths ta stoixeia tou
        currentScreen = "askForUserInfo";

        askForInfo = new Form("Please enter user info:");

        name = new TextField("Name:", u_name, 50, 0);
        name.setInitialInputMode("IS_LATIN");
        askForInfo.append(name);
        surname = new TextField("Surname:", u_surname, 50, 0);
        surname.setInitialInputMode("IS_LATIN");
        askForInfo.append(surname);
        address = new TextField("Address:", u_address, 50, 0);
        address.setInitialInputMode("IS_LATIN");
        askForInfo.append(address);

        askForInfo.addCommand(enterButton);
        askForInfo.setCommandListener(this);

        getDisplay().setCurrent(askForInfo);
    }

    public void createAskForCharging() { //othonh pou to kinhto zhtaei epithumhto tropo xrewshs
        currentScreen = "askForCharging";

        chargingList = new List("Charging model:", Choice.EXCLUSIVE);
        chargingList.append("FIXED", null);
        chargingList.append("METERED", null);
        chargingList.append("PACKET", null);
        chargingList.append("EXPECTED", null);
        chargingList.append("EDGE", null);
        chargingList.append("PARIS", null);
        chargingList.append("AUCTION", null);

        for (int i = 0; i < chargingList.size(); i++) {
            if (chargingList.getString(i).equals(u_charging)) {
                chargingList.setSelectedIndex(i, true);
            } else {
                chargingList.setSelectedIndex(i, false);
            }
        }

        chargingList.addCommand(enterButton);
        chargingList.setCommandListener(this);

        getDisplay().setCurrent(chargingList);
    }

    public void createAskForFavourites() { //othonh pou to kinhto zhtaei epithumhtes uphresies
        currentScreen = "askForFavourites";

        favouritesList = new List("Charging model:", Choice.EXCLUSIVE);
        favouritesList.append("DATA", null);
        favouritesList.append("VOICE", null);
        favouritesList.append("DATA & VOICE", null);

        for (int i = 0; i < favouritesList.size(); i++) {
            if (favouritesList.getString(i).equals(u_favourites)) {
                favouritesList.setSelectedIndex(i, true);
            } else {
                favouritesList.setSelectedIndex(i, false);
            }
        }

        favouritesList.addCommand(enterButton);
        favouritesList.setCommandListener(this);

        getDisplay().setCurrent(favouritesList);
    }

    public void createconnectionFailure() {
        currentScreen = "connectionFailure";

        connectionFailure = new TextBox("Connection Error", "BaseStation was not found or request timed-out.", 256, 0);
        connectionFailure.setConstraints(TextField.UNEDITABLE);

        connectionFailure.addCommand(okButton);
        connectionFailure.setCommandListener(this);

        getDisplay().setCurrent(connectionFailure);
    }

    public void createconnectionSuccess() {
        currentScreen = "connectionSuccess";

        connectionSuccess = new TextBox("Connection Success", "You have been successfuly connected to the selected Base Station.", 256, 0);
        connectionSuccess.setConstraints(TextField.UNEDITABLE);

        connectionSuccess.addCommand(okButton);
        connectionSuccess.setCommandListener(this);

        getDisplay().setCurrent(connectionSuccess);
    }

    public void createOptionsList() { //othonh epilogwn
        prevScreen = "startUpList";
        currentScreen = "optionsList";

        optionsList = new List("Select an option:", Choice.IMPLICIT);
        optionsList.addCommand(enterButton);
        optionsList.addCommand(backButton);
        optionsList.setCommandListener(this);

        optionsList.append("Device Info", null);
        optionsList.append("User Info", null);
        optionsList.append("Connection Type", null);

        getDisplay().setCurrent(optionsList);
    }

    public void createDeviceInfo() {
        prevScreen = "optionsList";
        currentScreen = "deviceInfo";

        deviceInfo = new TextBox("Info about the mobile:",
                "IMEI : " + trm.IMEI + "\n"
                + "IMSI : " + trm.IMSI + "\n"
                + "CPU : " + trm.cpu + " MHz \n"
                + "RAM : " + trm.ram + " MB \n"
                + "OS   : " + trm.os + "\n", 256, 0);
        deviceInfo.setConstraints(TextField.UNEDITABLE);

        deviceInfo.addCommand(okButton);
        deviceInfo.setCommandListener(this);

        getDisplay().setCurrent(deviceInfo);
    }

    public void createConnectionTypeInfo() {
        currentScreen = "connectionTypeInfo";

        connectionTypeInfo = new List("Choose BaseStation Selection method:", Choice.EXCLUSIVE);
        connectionTypeInfo.append("AUTOMATIC", null);
        connectionTypeInfo.append("MANUAL", null);

        connectionTypeInfo.addCommand(enterButton);
        connectionTypeInfo.setCommandListener(this);

        connectionTypeInfo.setSelectedIndex(0, automatic);
        connectionTypeInfo.setSelectedIndex(1, !automatic);

        getDisplay().setCurrent(connectionTypeInfo);
    }

    public void createUserInfo() { //othonh me ta stoixeia xrhsth
        prevScreen = "optionsList";
        currentScreen = "userInfo";

        userInfo = new TextBox("Info about the user:",
                "Name : " + u_name + "\n"
                + "Surname : " + u_surname + "\n"
                + "Address : " + u_address + "\n"
                + "Charging model : " + u_charging + "\n"
                + "Favourites : " + u_favourites + "\n", 256, 0);
        userInfo.setConstraints(TextField.UNEDITABLE);

        userInfo.addCommand(enterButton);
        userInfo.addCommand(backButton);
        userInfo.setCommandListener(this);

        getDisplay().setCurrent(userInfo);
    }

    public synchronized Vector Discover() { //leitourgia eureshs bs
        return trm.Discover();
    }

    public synchronized boolean ConnectTo(BaseStationShort bs) { //leitourgia sundeshs me bs
        System.out.println(bs.baseStation_id);

        if (currentBaseStation != null) {
            if (bs.baseStation_id.equals(currentBaseStation.baseStation_id)) {
                return true;
            }
            trm.DisconnectFrom(currentBaseStation);
        }

        boolean result = trm.ConnectTo(bs);

        if (result) {
            currentBaseStation = bs;
            if (bsCom != null) {
                bsCom.setBaseStation(bs);
            } else {
                bsCom = new TerminalBSCommunication(bs);
                bsCom.start();
            }
        }

        return result;
    }

    public Display getDisplay() {
        return Display.getDisplay(this);
    }
}
