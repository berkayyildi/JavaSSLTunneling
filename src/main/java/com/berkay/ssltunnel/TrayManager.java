package com.berkay.ssltunnel;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

import javax.swing.JOptionPane;

public class TrayManager {

    private TrayIcon ti;
    private String TrayName;
    private SystemTray tray;

    public TrayManager(String TrayName) {

    if (!SystemTray.isSupported()) {
      return;
    }
    tray = SystemTray.getSystemTray();

    Dimension size = tray.getTrayIconSize();
    BufferedImage bi = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_RGB);
    Graphics g = bi.getGraphics();

    g.setColor(Color.blue);
    g.fillRect(0, 0, size.width, size.height);
    g.setColor(Color.yellow);
    int ovalSize = (size.width < size.height) ? size.width : size.height;
    ovalSize /= 2;
    g.fillOval(size.width / 4, size.height / 4, ovalSize, ovalSize);

    try {
      PopupMenu popup = new PopupMenu();
      MenuItem miEdit = new MenuItem("Editor");
      MenuItem miExit = new MenuItem("Exit");
      MenuItem miAbout = new MenuItem("About");

      ActionListener al,bl,cl;

      al = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          System.out.println("Goodbye");
          System.exit(0);
        }
      };

      bl = new ActionListener() {
          public void actionPerformed(ActionEvent e) {
        	  JOptionPane.showMessageDialog (null, "From Berkay YILDIZ 20150702058", TrayName, JOptionPane.INFORMATION_MESSAGE);
          }
      };

      cl = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Editor.main();  //SHOW EDITOR
            }
       };
      
      miExit.addActionListener(al);
      miAbout.addActionListener(bl);
      miEdit.addActionListener(cl);

      popup.add(miExit);
      popup.add(miAbout);
      popup.add(miEdit);

      ti = new TrayIcon(bi, TrayName, popup);
      tray.add(ti);

    } catch (AWTException e) {
      System.out.println(e.getMessage());
      return;
    }
  }

    protected void setConnected() {

        if (!SystemTray.isSupported()) {
            return;
        }

        Dimension size = tray.getTrayIconSize();
        BufferedImage bi = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_RGB);
        Graphics g = bi.getGraphics();

        g.setColor(Color.green);
        g.fillRect(0, 0, size.width, size.height);
        g.setColor(Color.yellow);
        int ovalSize = (size.width < size.height) ? size.width : size.height;
        ovalSize /= 2;
        g.fillOval(size.width / 4, size.height / 4, ovalSize, ovalSize);

        ti.setImage(bi);


    }


}

          