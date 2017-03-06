package anaptyksi;

import javax.swing.*;

public class BsFrame extends JFrame{
    public JPanel panel;
    public JButton newTester, removeTester;
    private MyListener listener;
    public BaseStation bs;
    public JTextField textField, xField, yField, rmtextField, loadind;
    public JLabel outputTitle, threadListTitle;
    public JList terminalList, outList;
    public DefaultListModel terminalListModel, outListModel;

    public BsFrame(BaseStation bas) {
        
        super("Base Station Manager id: "+bas.prop.getProperty("basestationId")+" on port: "+bas.prop.getProperty("port")+".");
        this.setSize(800,600);
        this.setLocation(250, 150);
        bs=bas;
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(null);
        listener = new MyListener(this, bs);
        createWorkerCreationPanel();
        }

    public void updateTerminalNames(){ //refreshes the terminal list
        terminalListModel.clear();
        for (int i=0; i < bs.server.v.size(); i++){
            terminalListModel.addElement(bs.server.v.get(i).IMEI);
        }
        loadind.setText(Integer.toString(bs.loadP));
    }

    public void createWorkerCreationPanel(){
        panel = new JPanel();
        panel.setLayout(null);

        newTester       = addButton("Create new tester",20,20,150,30);
        textField       = addLabelAndText("with IMEI:","47564326-386549-3-90",20,55,100,30);
        xField          = addLabelAndText("x position :","1",20,135,80,30);
        yField          = addLabelAndText("y position :","1",180,135,80,30);
        threadListTitle = addLabel("List of connected terminals:","",430,15,300,30);
        outputTitle     = addLabel("Output:","",30,360,80,30);
        loadind         = addLabelAndText("Load %:",bs.loadP.toString(),230,20,70,30);
        loadind.setEditable(false);
        removeTester    = addButton("Stop running tester",20,250,150,30);
        rmtextField     = addLabelAndText("with IMEI:","47564326-386549-3-90",20,285,100,30);

        terminalList    = addList(420, 45, 350, 300, terminalListModel = new DefaultListModel());
        outList         = addList(20, 390, 750, 160, outListModel = new DefaultListModel());

    }

    public JButton addButton(String name,int x,int y,int sizex,int sizey){
        JButton b = new JButton();
        b.setText(name);
        b.setSize(sizex,sizey);
        b.setLocation(x,y);
        this.add(b);
        b.addActionListener(listener);
        return b;
    }

    public JLabel addLabel(String mylabel, String text,int x,int y,int sizex,int sizey){
        JLabel label = new JLabel(mylabel);
        label.setSize(sizex,sizey);
        label.setLocation(x,y);
        this.add(label);

        return label;
    }
    
    public JTextField addLabelAndText(String mylabel, String text,int x,int y,int sizex,int sizey){
        JLabel label = new JLabel(mylabel);
        label.setSize(sizex,sizey);
        label.setLocation(x + (150-sizex)/2,y);

        JTextField field = new JTextField(text);
        //field.setColumns(100);
        field.setSize(150,sizey);
        field.setLocation(x,y+35);

        this.add(label);
        this.add(field);

        return field;
    }

    public JList addList(int x,int y,int sizex,int sizey, DefaultListModel listModel){ //x,y, mhkos,platos,lista
        JList list = new JList(listModel);
        list.setBounds(x,y+35,sizex-20,sizey);
        //list.setLayoutOrientation(JList.VERTICAL);
        JScrollPane scr = new JScrollPane(list);
        scr.setBounds(x,y,sizex,sizey);
        scr.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        this.add(scr);

        return list;
    }
}
