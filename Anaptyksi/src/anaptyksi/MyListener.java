package anaptyksi;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class MyListener implements ActionListener{
    BsFrame bf;
    BaseStation bs;
    
    public MyListener(BsFrame bf, BaseStation bs){
        this.bf = bf;
        this.bs = bs;
    }
    
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == bf.newTester){    //if connect action occurs
            String IMEI = bf.textField.getText();  //takes stats
            int x = Integer.parseInt( bf.xField.getText() ); //>>
            int y = Integer.parseInt( bf.yField.getText() );  //>>
            Starter.startTerminal(bf.outListModel, IMEI, bs.port, x,y);  //and sends CONNECT at basestation
            bf.updateTerminalNames(); //refreshes terminal list
        }
        else{  //disconnect action occurs
            String IMEI = bf.rmtextField.getText();
            Stopper.stopTerminal(bf.outListModel, IMEI, bs.port);
            bf.updateTerminalNames();
        }
            
    }
}
