package com.berkay.ssltunnel;

import org.ini4j.Ini;
import org.ini4j.Profile;
import org.ini4j.Wini;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;

import static javax.swing.JOptionPane.showInputDialog;

public class Editor {
    private JPanel panel1;
    private JTextField textField_destPort;
    private JTextField textField_ListenPort;
    private JTextField textField_destIp;
    private JButton addNewButton;
    private JComboBox comboBox_protocol;
    private JComboBox comboBox_serviceName;
    private JComboBox comboBox_clientorserver;

    private JButton deleteButton;

    File file = new File("config.ini");
    Ini ini = null;

    public Editor(){

        comboBox_serviceName.addItem("[Add New]");

        {
            try {
                ini = new Ini(file);

                for (String sectionName: ini.keySet()) {
                        comboBox_serviceName.addItem(sectionName);
                }

            } catch (IOException e) {
                System.out.println("File Read Error!");
                e.printStackTrace();
            }
        }

        comboBox_serviceName.setSelectedIndex(1);
        String selectedOne = comboBox_serviceName.getSelectedItem().toString();

        Profile.Section section = ini.get(selectedOne);
        textField_ListenPort.setText(section.get("ListenPort"));
        textField_destIp.setText(section.get("DestinationIP"));
        textField_destPort.setText(section.get("DestinationPort"));



        addNewButton.addActionListener(new ActionListener() {   //Update Button
            @Override
            public void actionPerformed(ActionEvent e) {

                if (comboBox_serviceName.getSelectedIndex() == 0) {
                    System.out.println("Select Valid Service");
                    return;
                }

                try{
                    String selectedItem = comboBox_serviceName.getSelectedItem().toString();
                    //System.out.println("Selected " + selectedItem);
                    ini.put(selectedItem, "client", comboBox_clientorserver.getSelectedItem().toString());
                    ini.put(selectedItem, "ListenPort", textField_ListenPort.getText());
                    ini.put(selectedItem, "DestinationIP", textField_destIp.getText());
                    ini.put(selectedItem, "DestinationPort", textField_destPort.getText());
                    ini.put(selectedItem, "Proto", comboBox_protocol.getSelectedItem().toString());

                    ini.store();

                }catch(Exception ex){
                    System.err.println(ex.getMessage());
                }

            }
        });

        comboBox_serviceName.addItemListener(new ItemListener() {  //Comboboxtan servis secilince
            @Override
            public void itemStateChanged(ItemEvent event) {

                if (event.getStateChange() == ItemEvent.SELECTED) {

                    Object selectedItem = event.getItem();  //Secilen service name i al

                    if (selectedItem.equals("[Add New]")){  //EGER YENI EKLEME ISE
                        String newService = showInputDialog("Service Name", "Enter Service Name");
                        comboBox_serviceName.addItem(newService);

                        try {
                            ini.put(newService, "client", "yes");
                            ini.put(newService, "ListenPort", "1234");
                            ini.put(newService, "DestinationIP", "10.10.10.1");
                            ini.put(newService, "DestinationPort", "443");
                            ini.put(newService, "Proto", "TCP");
                            ini.put(newService, "Key", "keystore.ImportKey");

                            ini.store();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        comboBox_serviceName.setSelectedIndex(comboBox_serviceName.getItemCount()-1);
                        return;
                    }   //YENI EKLEME ISE KAPAT



                        Profile.Section section = ini.get(selectedItem);
                        textField_ListenPort.setText(section.get("ListenPort"));
                        textField_destIp.setText(section.get("DestinationIP"));
                        textField_destPort.setText(section.get("DestinationPort"));



                }

            }
        });
        deleteButton.addActionListener(new ActionListener() {   //Delete Button
            @Override
            public void actionPerformed(ActionEvent e) {

                String selectedItem = comboBox_serviceName.getSelectedItem().toString();

                try{

                    ini.remove(selectedItem);
                    ini.store();
                    // To catch basically any error related to writing to the file
                    // (The system cannot find the file specified)
                }catch(Exception exc){
                    System.err.println(exc.getMessage());
                }
                comboBox_serviceName.removeItem(selectedItem);
                comboBox_serviceName.setSelectedIndex(comboBox_serviceName.getItemCount()-1);



            }
        });
    }

    public static void main(String args) {

        JFrame frame = new JFrame("Mini Editor");
        frame.setContentPane(new Editor().panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);

    }



}
